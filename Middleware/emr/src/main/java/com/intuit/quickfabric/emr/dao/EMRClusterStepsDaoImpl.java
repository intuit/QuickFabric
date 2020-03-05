package com.intuit.quickfabric.emr.dao;

import com.intuit.quickfabric.commons.exceptions.QuickFabricSQLException;
import com.intuit.quickfabric.commons.exceptions.QuickFabricServerException;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.emr.mapper.BootstrapActionMapper;
import com.intuit.quickfabric.emr.mapper.ClusterStepResponseMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
public class EMRClusterStepsDaoImpl implements EMRClusterStepsDao {

    private static final Logger logger = LogManager.getLogger(EMRClusterStepsDaoImpl.class);

    @Autowired
    NamedParameterJdbcTemplate namedJdbcTemplateObject;

    @Autowired
    private JdbcTemplate jdbcTemplateObject;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    @Transactional
    public void updateStepIdsInDB(StepResponseVO stepResponse, List<ClusterStep> steps) {
        String updateQuery = "UPDATE cluster_step_request SET step_id=?,api_request_id=?,lambda_request_id=?,updated_ts=?,status=? where cluster_id=? and name=?";
        try {

            jdbcTemplateObject.batchUpdate(updateQuery,
                    new BatchPreparedStatementSetter() {

                        public void setValues(PreparedStatement preparedStmt, int i) throws SQLException {

                            ClusterStep step = steps.get(i);
                            preparedStmt.setString(1, step.getStepId());
                            preparedStmt.setString(2, stepResponse.getLambdaRequestId());
                            preparedStmt.setString(3, stepResponse.getApiRequestId());
                            preparedStmt.setTimestamp(4, new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
                            preparedStmt.setString(5, (step.getStatus() == null ? StepStatus.PENDING.toString() : step.getStatus().toString()));
                            preparedStmt.setString(6, stepResponse.getClusterId());
                            preparedStmt.setString(7, step.getName());

                        }

                        @Override
                        public int getBatchSize() {
                            return steps.size();
                        }


                    });
        } catch (Exception e) {
            logger.error("DB error during Step update with error {}", e.getMessage());
            throw new QuickFabricSQLException("DB error during cluster step update", e);
        }
    }

    @Override
    public void saveStepRequestForCluster(String clusterName, ClusterStep step) {

        String insertQuery = "INSERT INTO cluster_step_request"
                + " (cluster_name,step_args,created_ts,status,created_by) "
                + "VALUES(?,?,?,?)";


        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        try {

            jdbcTemplateObject.update(insertQuery,
                    clusterName,
                    step.getHadoopJarStep().getStepArgs(),
                    new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()),
                    StepStatus.INTIATED);
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            throw e;
        }
    }

    @Override
    public void saveStepRequestForCluster(ClusterVO clusterDetails) {
        //Marking stale custom steps as cancelled for a cluster with same name that failed during creation
        String updateOldSteps = "UPDATE cluster_step_request set status='CANCELLED' where cluster_name=? and cluster_id is null and status ='NEW' and step_type = 'Custom'";
        String insertQuery = "INSERT INTO cluster_step_request"
                + " (cluster_name,step_arg,main_class,jar,name,status,created_ts,action_on_failure,cluster_id,created_by) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?)";

        try {

            jdbcTemplateObject.update(updateOldSteps, clusterDetails.getClusterName());
            jdbcTemplateObject.batchUpdate(insertQuery,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement preparedStmt, int i) throws SQLException {
                            ClusterStep clusterStepVO = clusterDetails.getSteps().get(i);
                            StringBuilder finalArg = new StringBuilder();
                            for (String s : clusterStepVO.getHadoopJarStep().getStepArgs()) {
                                finalArg.append(s);
                                finalArg.append(" ");
                            }
                            preparedStmt.setString(1, clusterDetails.getClusterName());
                            preparedStmt.setString(2, finalArg.toString());
                            preparedStmt.setString(3, clusterStepVO.getHadoopJarStep().getMainClass());
                            preparedStmt.setString(4, clusterStepVO.getHadoopJarStep().getJar());
                            preparedStmt.setString(5, clusterStepVO.getName());
                            preparedStmt.setString(6, "NEW");
                            preparedStmt.setTimestamp(7, new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
                            preparedStmt.setString(8, clusterStepVO.getActionOnFailure());
                            preparedStmt.setString(9, clusterDetails.getClusterId());
                            preparedStmt.setString(10, clusterStepVO.getStepCreatedBy());

                        }

                        @Override
                        public int getBatchSize() {
                            return clusterDetails.getSteps().size();
                        }
                    });
            logger.info("Insert Query" + insertQuery);

        } catch (Exception e) {
            logger.error("DB error during step insert", e.getMessage());
            throw new QuickFabricServerException("DB error during step insert", e);
        }
        logger.info("Cluster Steps inserted succussfully to table");
    }

    @Override
    public StepResponseVO getStepsOfACluster(String clusterId) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("clusterId", clusterId);

        String sql = "SELECT * FROM cluster_step_request where cluster_id = :clusterId";

        logger.info("Fetching Steps for Cluster: " + clusterId);
        StepResponseVO stepResponse = namedJdbcTemplateObject.query(sql, parameters, new ClusterStepResponseMapper());
        return stepResponse;

    }

    @Override
    public List<ClusterStep> getStepsByStepIds(List<String> stepIds) {
        logger.info("Retrieving steps with IDs: {}", stepIds);

        String sql = "SELECT step_id, name, action_on_failure, step_arg, main_class, jar, status, created_by, created_ts " +
                "FROM cluster_step_request WHERE step_id IN (:step_ids)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("step_ids", stepIds);

        logger.info("SQL:: {}", sql);

        return this.namedJdbcTemplateObject.query(sql, params, (ResultSet rs) -> {
            List<ClusterStep> steps = new ArrayList<>();

            while (rs.next()) {
                ClusterStep step = new ClusterStep();
                step.setStepId(rs.getString("step_id"));
                step.setName(rs.getString("name"));
                step.setActionOnFailure(rs.getString("action_on_failure"));
                step.setStepArg(rs.getString("step_arg"));
                step.setMainClass(rs.getString("main_class"));
                step.setStatus(StepStatus.valueOf(rs.getString("status")));
                step.setJar(rs.getString("jar"));
                step.setStepCreatedBy(rs.getString("created_by"));
                step.setCreationTimestamp(rs.getString("created_ts"));

                steps.add(step);
            }

            return steps;

        });


    }

    @Override
    public void saveBootstrapActionRequestForCluster(ClusterVO clusterDetails) {

        String insertBootstrapActionQuery = "INSERT INTO cluster_step_request"
                + " (cluster_name,step_arg,name,status,created_ts,cluster_id,created_by,step_type) "
                + "VALUES(?,?,?,?,?,?,?,?)";
        try {

            jdbcTemplateObject.batchUpdate(insertBootstrapActionQuery,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement preparedStmt, int i) throws SQLException {
                            BootstrapActionVO bootstrapActionVO = clusterDetails.getBootstrapActions().get(i);
                            preparedStmt.setString(1, clusterDetails.getClusterName());
                            preparedStmt.setString(2, bootstrapActionVO.getBootstrapScript());
                            preparedStmt.setString(3, bootstrapActionVO.getBootstrapName());
                            preparedStmt.setString(4, "NEW");
                            preparedStmt.setTimestamp(5, new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
                            preparedStmt.setString(6, clusterDetails.getClusterId());
                            preparedStmt.setString(7, clusterDetails.getCreatedBy());
                            preparedStmt.setString(8, "Bootstrap");

                        }

                        @Override
                        public int getBatchSize() {
                            return clusterDetails.getBootstrapActions().size();
                        }
                    });
            logger.info("Insert Bootstrap Action Query" + insertBootstrapActionQuery);
        } catch (Exception e) {
            logger.error("DB error during Bootstep insert", e.getMessage());
            throw new QuickFabricServerException("DB error during Bootstep insert", e);
        }
        logger.info("Cluster Bootstrap Actions inserted succussfully to table");

    }

    @Override
    public List<BootstrapActionVO> getBootstrapActionsByClusterId(String clusterId) {
        logger.info("Getting Bootstrap Actions for clusterId {}", clusterId);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("cluster_id", clusterId);
        String sql = "SELECT * from cluster_step_request "
                + "where cluster_id=:cluster_id AND step_type = 'Bootstrap'";

        List<BootstrapActionVO> bootstrapActions = namedJdbcTemplateObject.query(sql, parameters, new BootstrapActionMapper());

        return bootstrapActions;
    }

}
