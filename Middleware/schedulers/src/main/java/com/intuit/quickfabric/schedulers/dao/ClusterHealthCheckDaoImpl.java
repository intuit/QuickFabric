package com.intuit.quickfabric.schedulers.dao;

import com.intuit.quickfabric.commons.exceptions.QuickFabricSQLException;
import com.intuit.quickfabric.commons.vo.ClusterHealthCheckStatusType;
import com.intuit.quickfabric.commons.vo.ClusterHealthStatus;
import com.intuit.quickfabric.commons.vo.EMRClusterHealthTestCase;
import com.intuit.quickfabric.schedulers.mappers.EMRClusterHealthStatusMapper;
import com.intuit.quickfabric.schedulers.mappers.TestSuiteDefinitionMapper;
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
public class ClusterHealthCheckDaoImpl implements ClusterHealthCheckDao {
    private static final Logger logger = LogManager.getLogger(ClusterHealthCheckDaoImpl.class);

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<ClusterHealthStatus> getEMRClusterHealthStatus(String clusterId) {
        logger.info("EMRClusterHealthDaoImpl -> getEMRClusterHealthStatus clusterId:{}", clusterId);

        String sql = "select * from emr_functional_testsuites_status where cluster_id = :clusterId";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("clusterId", clusterId);

        List<ClusterHealthStatus> clusterHealthStatus = namedParameterJdbcTemplate.query(sql, parameters, new EMRClusterHealthStatusMapper());
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

        List<EMRClusterHealthTestCase> clusterHealthStatus = namedParameterJdbcTemplate.query(sql, parameters, new TestSuiteDefinitionMapper());
        return clusterHealthStatus;
    }

    @Override
    public void createHealthCheckTestCases(List<EMRClusterHealthTestCase> testCases, String clusterId, String clusterName, String executedBy) {
        logger.info("EMRClusterHealthDaoImpl -> createHealthCheckTestCases clusterId: {} clusterName:{} executedBy:{}", clusterId, clusterName, executedBy);

        for (EMRClusterHealthTestCase testCase : testCases) {
            logger.info("createHealthCheckTestCases creating testcase: {}", testCase.toString());

            String historySql = "insert into emr_functional_testsuites_status_history(name, status, cluster_id, cluster_name, cluster_type, cluster_segment, execution_start_time, executed_by, execution_end_time)" +
                    " values " +
                    "(:name, :status, :cluster_id, :cluster_name, :cluster_type, :cluster_segment, :execution_start_time, :executed_by, :execution_end_time)";

            MapSqlParameterSource historyParams = getTestCaseParameters(testCase, clusterId, clusterName, executedBy);

            KeyHolder holder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(historySql, historyParams, holder);
            int executionId = holder.getKey().intValue();

            String sql = "insert into emr_functional_testsuites_status(execution_id, name, status, cluster_id, cluster_name, cluster_type, cluster_segment, execution_start_time, executed_by, execution_end_time)" +
                    " values " +
                    "(:execution_id, :name, :status, :cluster_id, :cluster_name, :cluster_type, :cluster_segment, :execution_start_time, :executed_by, :execution_end_time)";

            MapSqlParameterSource parameters = getTestCaseParameters(testCase, clusterId, clusterName, executedBy);
            parameters.addValue("execution_id", executionId, Types.INTEGER);

            namedParameterJdbcTemplate.update(sql, parameters);
        }
    }

    private MapSqlParameterSource getTestCaseParameters(EMRClusterHealthTestCase testCase, String clusterId, String clusterName,
                                                        String executedBy) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("name", testCase.getTestName());
        parameters.addValue("status", ClusterHealthCheckStatusType.NEW.toString());
        parameters.addValue("cluster_id", clusterId);
        parameters.addValue("cluster_name", clusterName);
        parameters.addValue("cluster_type", testCase.getClusterType());
        parameters.addValue("cluster_segment", testCase.getClusterSegment());
        parameters.addValue("execution_start_time", new java.util.Date());
        parameters.addValue("execution_end_time", new java.util.Date());
        parameters.addValue("executed_by", executedBy);

        return parameters;
    }

    @Override
    public List<ClusterHealthStatus> getNewHealthTestCases() {
        logger.info("EMRClusterTestSuitesStatusDaoImpl -> getPendingHealthTestCases");
        String sql = "select * from emr_functional_testsuites_status where status = 'new'";
        List<ClusterHealthStatus> clusterHealthStatus = namedParameterJdbcTemplate.query(sql, new EMRClusterHealthStatusMapper());
        return clusterHealthStatus;
    }

    @Override
    public List<EMRClusterHealthTestCase> getFunctionalTestSuites(String testName) {
        logger.info("EMRClusterTestSuitesStatusDaoImpl -> getFunctionalTestSuites testname:" + testName);
        String sql = "SELECT * FROM emr_functional_testsuites where name =:testName";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("testName", testName);

        List<EMRClusterHealthTestCase> testCases = namedParameterJdbcTemplate.query(sql, parameters, new TestSuiteDefinitionMapper());
        return testCases;
    }

    @Override
    @Transactional
    public void updateTestCaseStatus(int executionId, ClusterHealthCheckStatusType status, String remark) {
        try {
            logger.info("EMRClusterTestSuitesStatusDaoImpl -> updateTestCaseStatus executionId:" + executionId + " status:" + status + " remark:" + remark);
            String sql = "update emr_functional_testsuites_status_history" +
                    " set status=:status," +
                    " remark=:remark," +
                    " execution_end_time =:currentDate" +
                    " where execution_id =:executionId";

            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("executionId", executionId);
            parameters.addValue("status", status.toString());
            parameters.addValue("remark", remark);
            parameters.addValue("currentDate", new java.util.Date());

            logger.info("Updating emr_functional_testsuites_status_history ");
            namedParameterJdbcTemplate.update(sql, parameters);

            sql = "update emr_functional_testsuites_status" +
                    " set status=:status," +
                    " remark=:remark," +
                    " execution_end_time =:currentDate" +
                    " where execution_id =:executionId";
            logger.info("Updating emr_functional_testsuites_status ");
            namedParameterJdbcTemplate.update(sql, parameters);
        } catch (Exception e) {
            throw new QuickFabricSQLException("Error happened in DB while updating test case status. ", e);
        }
    }
}
