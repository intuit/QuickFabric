package com.intuit.quickfabric.emr.dao;

import com.intuit.quickfabric.commons.exceptions.QuickFabricSQLException;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.emr.mapper.ClusterStepResponseMapper;
import com.intuit.quickfabric.emr.mapper.EMRClusterHealthHistoryMapper;
import com.intuit.quickfabric.emr.mapper.EMRClusterHealthStatusMapper;
import com.intuit.quickfabric.emr.mapper.TestSuiteDefinitionMapper;
import org.apache.cxf.common.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.util.List;

@Repository
public class EMRClusterHealthCheckDaoImpl implements EMRClusterHealthCheckDao {

    private static final Logger logger = LogManager.getLogger(EMRClusterHealthCheckDaoImpl.class);

    @Autowired
    NamedParameterJdbcTemplate namedJdbcTemplateObject;

    @Override
    public List<ClusterHealthStatus> getEMRClusterHealthStatus(String clusterId) {
        logger.info("EMRClusterHealthDaoImpl -> getEMRClusterHealthStatus clusterId:{}", clusterId);

        String sql = "select * from emr_functional_testsuites_status where cluster_id = :clusterId";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("clusterId", clusterId);

        List<ClusterHealthStatus> clusterHealthStatus = namedJdbcTemplateObject.query(sql, parameters, new EMRClusterHealthStatusMapper());
        return clusterHealthStatus;
    }

    @Override
    public List<ClusterHealthStatus> getEMRClusterHealthHistory(String clusterId) {
        logger.info("EMRClusterHealthDaoImpl -> getEMRClusterHealthStatusHistory clusterId:{}", clusterId);

        String sql = "select * from emr_functional_testsuites_status_history where cluster_id = :clusterId";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("clusterId", clusterId);

        List<ClusterHealthStatus> clusterHealthStatus = namedJdbcTemplateObject.query(sql, parameters, new EMRClusterHealthHistoryMapper());
        return clusterHealthStatus;
    }

    @Override
    public List<EMRClusterHealthTestCase> getEMRClusterTestSuites(String clusterSegment, String clusterType) {
        logger.info("EMRClusterHealthDaoImpl -> getEMRClusterTestSuites clusterSegment:{} clusterType:{}", clusterSegment, clusterType);

        String sql = "select * from emr_functional_testsuites" +
                " where cluster_type =:clusterType" +
                " AND cluster_segment =:clusterSegment " +
                " AND disabled = false";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("clusterType", clusterType);
        parameters.addValue("clusterSegment", clusterSegment);

        List<EMRClusterHealthTestCase> clusterHealthStatus = namedJdbcTemplateObject.query(sql, parameters, new TestSuiteDefinitionMapper());
        return clusterHealthStatus;
    }

    @Override
    public EMRClusterHealthTestCase getEMRClusterTestSuitesForValidation(String clusterSegment, String clusterType, String testSuiteName) {
        logger.info("EMRClusterHealthDaoImpl -> getEMRClusterTestSuites clusterSegment:{} clusterType:{}", clusterSegment, clusterType);

        String sql = "select * from emr_functional_testsuites" +
                " where cluster_type =:clusterType" +
                " AND cluster_segment =:clusterSegment " +
                " AND name =:testSuiteName " +
                " AND disabled = false";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("clusterType", clusterType);
        parameters.addValue("clusterSegment", clusterSegment);
        parameters.addValue("testSuiteName", testSuiteName);

        List<EMRClusterHealthTestCase> clusterHealthStatus = namedJdbcTemplateObject.query(sql, parameters, new TestSuiteDefinitionMapper());
        return clusterHealthStatus.size() > 0 ? clusterHealthStatus.get(0) : null;
    }


    @Override
    public void deleteCurrentClusterHealthCheckStatus(String clusterId) {
        logger.info("EMRClusterHealthDaoImpl -> deleteCurrentClusterHealthCheckStatus clusterId:{}", clusterId);

        try {
            String sql = "delete from emr_functional_testsuites_status where cluster_id = :clusterId";
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("clusterId", clusterId);

            int update = namedJdbcTemplateObject.update(sql, parameters);
            logger.info("deleteCurrentClusterHealthCheckStatus deleted {} records for clusterId {}", update, clusterId);
        } catch (Exception ex) {
            throw new QuickFabricSQLException("Error happened while deleting current health checks for cluster id:" + clusterId, ex);
        }
    }

    @Override
    @Transactional
    public void createHealthCheckTestCases(List<EMRClusterHealthTestCase> testCases, String clusterId, String clusterName, String executedBy) {
        logger.info("EMRClusterHealthDaoImpl -> createHealthCheckTestCases clusterId: {} clusterName:{} executedBy:{}", clusterId, clusterName, executedBy);

        try {
            for (EMRClusterHealthTestCase testCase : testCases) {
                logger.info("createHealthCheckTestCases creating testcase: {}", testCase.toString());

                String historySql = "insert into emr_functional_testsuites_status_history(name, status, cluster_id, cluster_name," +
                        " cluster_type, cluster_segment, execution_start_time, executed_by, execution_end_time, expires_minutes, mandatory, disabled)" +
                        " values " +
                        "(:name, :status, :cluster_id, :cluster_name, :cluster_type, :cluster_segment, :execution_start_time, :executed_by," +
                        " :execution_end_time, :expires_minutes, :mandatory, :disabled)";

                MapSqlParameterSource historyParams = getTestCaseParameters(testCase, clusterId, clusterName, executedBy);
                KeyHolder holder = new GeneratedKeyHolder();
                namedJdbcTemplateObject.update(historySql, historyParams, holder);
                int executionId = holder.getKey().intValue();

                String sql = "insert into emr_functional_testsuites_status(execution_id, name, status, cluster_id, cluster_name," +
                        " cluster_type, cluster_segment, execution_start_time, executed_by, execution_end_time, expires_minutes, mandatory, disabled)" +
                        " values " +
                        "(:execution_id, :name, :status, :cluster_id, :cluster_name, :cluster_type, :cluster_segment, :execution_start_time, :executed_by," +
                        " :execution_end_time, :expires_minutes, :mandatory, :disabled)";

                MapSqlParameterSource parameters = getTestCaseParameters(testCase, clusterId, clusterName, executedBy);
                parameters.addValue("execution_id", executionId, Types.INTEGER);

                namedJdbcTemplateObject.update(sql, parameters);
            }
        } catch (Exception ex) {
            logger.error("error happened while creating health checks.", ex);
            throw new QuickFabricSQLException("error happened while creating health checks.", ex);
        }
    }

    @Override
    @Transactional
    public void updateEMRClusterHealthTest(String clusterId, int executionId, ClusterHealthCheckStatusType status, String message) {
        logger.info("EMRClusterHealthDaoImpl -> updateEMRClusterHealthTest clusterId:{} executionId:{} status:{}", clusterId, executionId, status);

        try {
            String sql = "update emr_functional_testsuites_status_history set status=:status, execution_end_time =:currentDate, " +
                    " remark = COALESCE(:message, remark)" +
                    " where cluster_id = :cluster_id AND execution_id = :execution_id";
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("cluster_id", clusterId);
            parameters.addValue("execution_id", executionId, Types.INTEGER);
            parameters.addValue("status", status.toString());
            parameters.addValue("currentDate", new java.util.Date());
            parameters.addValue("message", StringUtils.isEmpty(message) ? null : message);

            int updated = namedJdbcTemplateObject.update(sql, parameters);
            logger.info("updateEMRClusterHealthTest emr_functional_testsuites_status_history recordsUpdated:" + updated);

            sql = "update emr_functional_testsuites_status set status=:status where cluster_id = :cluster_id AND execution_id = :execution_id";
            updated = namedJdbcTemplateObject.update(sql, parameters);
            logger.info("updateEMRClusterHealthTest emr_functional_testsuites_status records updated:" + updated);
        } catch (Exception ex) {
            throw new QuickFabricSQLException("error happened while updating health test.", ex);
        }
    }

    @Override
    public void updateEMRClusterTestSuitesDefinition(ClusterTestSuitesDefinitionVO testSuiteDefinition) {
        logger.info("updateEMRClusterTestSuitesDefinition :" + testSuiteDefinition);

        try {
            String sql = "update emr_functional_testsuites set " +
                    " criteria = :criteria," +
                    " description = :description" +
                    " where id = :id";

            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("id", testSuiteDefinition.getId());
            parameters.addValue("criteria", testSuiteDefinition.getCriteria());
            parameters.addValue("description", testSuiteDefinition.getDescription());

            namedJdbcTemplateObject.update(sql, parameters);
        } catch (Exception e) {
            logger.error("Error occurred updating testSuites definition into DB with Error: {}", e.getMessage());
            throw new QuickFabricSQLException("Error occurred updating testSuites definition into DB", e);
        }
    }

    public void addEMRClusterTestSuitesDefinition(ClusterTestSuitesDefinitionVO testSuitesDetails) {
        logger.info("addEMRClusterTestSuitesDefinition: " + testSuitesDetails);

        try {
            String insertQuery = "INSERT INTO emr_functional_testsuites (name, description, "
                    + "criteria, cluster_type, cluster_segment)"
                    + " values "
                    + " ( :name,:description,:criteria,:cluster_type,:cluster_segment)";
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("name", testSuitesDetails.getName());
            parameters.addValue("description", testSuitesDetails.getDescription());
            parameters.addValue("criteria", testSuitesDetails.getCriteria());
            parameters.addValue("cluster_type", testSuitesDetails.getClusterType().toString());
            parameters.addValue("cluster_segment", testSuitesDetails.getClusterSegment());

            namedJdbcTemplateObject.update(insertQuery, parameters);
        } catch (Exception e) {
            logger.error("Error occurred adding new testSuites definition into DB with Error: {}", e.getMessage());
            throw new QuickFabricSQLException("Error occurred adding new testSuites definition into DB", e);
        }
    }

    @Override
    public int getClusterTestTimeout(String clusterType) {
        logger.info("EMRClusterHealthDaoImpl -> getClusterTestTimeout clusterType:{}", clusterType);

        String sql = "select timeout from emr_functional_testsuites where cluster_type = :cluster_type LIMIT 1";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("cluster_type", clusterType);

        int timeout = namedJdbcTemplateObject.queryForObject(sql, parameters, Integer.class);
        return timeout;
    }

    @Override
    public List<ClusterStep> getClusterBootStraps(String clusterId) {
        logger.info("EMRClusterTestSuitesStatusDaoImpl -> getClusterBootStraps clusterId:" + clusterId);
        String sql = "SELECT * FROM cluster_step_request where step_type ='Bootstrap' and cluster_id =:clusterId";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("clusterId", clusterId);

        StepResponseVO response = namedJdbcTemplateObject.query(sql, parameters, new ClusterStepResponseMapper());
        return response.getSteps();
    }

    @Override
    public List<ClusterStep> getClusterCustomSteps(String clusterId) {
        logger.info("EMRClusterTestSuitesStatusDaoImpl -> getClusterBootStraps clusterId:" + clusterId);
        String sql = "SELECT * FROM cluster_step_request where step_type ='Custom' and cluster_id =:clusterId";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("clusterId", clusterId);

        StepResponseVO response = namedJdbcTemplateObject.query(sql, parameters, new ClusterStepResponseMapper());
        return response.getSteps();
    }

    private MapSqlParameterSource getTestCaseParameters(EMRClusterHealthTestCase testCase, String clusterId, String clusterName, String executedBy) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("name", testCase.getTestName());
        parameters.addValue("status", ClusterHealthCheckStatusType.NEW.toString());
        parameters.addValue("cluster_id", clusterId);
        parameters.addValue("cluster_name", clusterName);
        parameters.addValue("cluster_type", testCase.getClusterType());
        parameters.addValue("cluster_segment", testCase.getClusterSegment());
        parameters.addValue("execution_start_time", new java.util.Date());
        parameters.addValue("executed_by", executedBy);
        parameters.addValue("execution_end_time", new java.util.Date());
        parameters.addValue("expires_minutes", testCase.getExpiresInMinutes());
        parameters.addValue("mandatory", testCase.isMandatory());
        parameters.addValue("disabled", testCase.isDisabled());

        return parameters;
    }
}