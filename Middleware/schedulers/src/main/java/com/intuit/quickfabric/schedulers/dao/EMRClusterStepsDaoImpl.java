package com.intuit.quickfabric.schedulers.dao;

import com.intuit.quickfabric.commons.exceptions.QuickFabricSQLException;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.schedulers.mappers.BootstrapActionMapper;
import com.intuit.quickfabric.schedulers.mappers.ClusterStepMapper;
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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

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

        String updateQuery = "UPDATE  cluster_step_request SET step_id=?,api_request_id=?,lambda_request_id=?,updated_ts=?,status=? where cluster_id=? and name=?";
        try {

            jdbcTemplateObject.batchUpdate(updateQuery,
                    new BatchPreparedStatementSetter() {

                        public void setValues(PreparedStatement preparedStmt, int i) {

                            ClusterStep step = steps.get(i);
                            try {
                                preparedStmt.setString(1, step.getStepId());
                                preparedStmt.setString(2, stepResponse.getLambdaRequestId());
                                preparedStmt.setString(3, stepResponse.getApiRequestId());
                                preparedStmt.setTimestamp(4, new Timestamp(Calendar.getInstance().getTime().getTime()));
                                preparedStmt.setString(5, (step.getStatus() == null ? StepStatus.PENDING.toString() : step.getStatus().toString()));
                                preparedStmt.setString(6, stepResponse.getClusterId());
                                //TODO update with step id mapping
                                preparedStmt.setString(7, step.getName());
                            } catch (SQLException e) {
                                throw new QuickFabricSQLException("error while creating prepared statement.", e);
                            }
                        }

                        @Override
                        public int getBatchSize() {
                            return steps.size();
                        }


                    });
        } catch (Exception e) {
            throw new QuickFabricSQLException("DB error during step update",e);
        }
    }

    @Override
    public List<ClusterRequest> getStepsForNewSucceededClusters(Set<ClusterStatus> clusterstatuses, List<StepStatus> stepStatuses) {
        List<String> stepStatusesString = new ArrayList<String>();
        List<String> clusterstatusesString = new ArrayList<String>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        for (StepStatus c : stepStatuses) {
            stepStatusesString.add(c.toString());
        }

        for (ClusterStatus c : clusterstatuses) {
            clusterstatusesString.add(c.toString());
        }
        parameters.addValue("stepStatusParamName", stepStatusesString);
        parameters.addValue("clusterStatusParamName", clusterstatusesString);

        String sql = "SELECT  " +
                "    steps.*, metadata.account, metadata.cluster_name " +
                "FROM " +
                "    cluster_step_request steps " +
                "        JOIN " +
                "    emr_cluster_metadata metadata ON steps.cluster_name = metadata.cluster_name " +
                "        AND metadata.status IN (:clusterStatusParamName) " +
                "        AND steps.status IN (:stepStatusParamName) " +
                "        AND steps.step_type = 'Custom' " +
                "        AND metadata.cluster_id IS NOT NULL";

        logger.info("SQL::{}, params: {}", sql, parameters.getValues());
        List<ClusterRequest> list = namedJdbcTemplateObject.query(sql, parameters, new ClusterStepMapper());
        return list;

    }

    @Override
    @Transactional
    public void updateBootstrapActionStatus(ClusterVO clusterDetails, String bootstrapStatus) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("bootstrapStatus", bootstrapStatus);
        parameters.addValue("clusterId", clusterDetails.getClusterId());
        parameters.addValue("timeStamp", new Timestamp(Calendar.getInstance().getTime().getTime()));

        String updateStepQuery = "UPDATE cluster_step_request SET status= :bootstrapStatus, updated_ts= :timeStamp "
                + "where cluster_id= :clusterId and step_type='Bootstrap' ";

		logger.info("Updating Bootstrap Actions Status for Cluster to:{}{}{} ", bootstrapStatus, clusterDetails.getClusterName(), clusterDetails.getClusterId());
        try {
        	namedJdbcTemplateObject.update(updateStepQuery,parameters);

        } catch (Exception e) {
            logger.error("Error updating Bootstrap Actions Status. Exception -> ", e);
            throw new QuickFabricSQLException("DB Error during Bootstrap step update",e);
        }

	}

    @Override
    public void saveBootstrapActionRequestForCluster(ClusterVO clusterDetails) {
        String insertBootstrapActionQuery = "INSERT INTO cluster_step_request"
                + " (cluster_name,step_arg,name,status,created_ts,cluster_id,created_by,step_type) "
                + "VALUES(?,?,?,?,?,?,?,?)";


        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        try {

            jdbcTemplateObject.batchUpdate(insertBootstrapActionQuery,
                    new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStmt, int i) throws SQLException{
                    BootstrapActionVO clusterStepVO  = clusterDetails.getBootstrapActions().get(i);
                    preparedStmt.setString(1, clusterDetails.getClusterName());
                    preparedStmt.setString(2, clusterStepVO.getBootstrapScript());
                    preparedStmt.setString(3,clusterStepVO.getBootstrapName());
                    preparedStmt.setString(4, "NEW");
                    preparedStmt.setTimestamp(5,  new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
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
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            logger.error("update failed.", e);
            transactionManager.rollback(txStatus);
            throw e;
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
