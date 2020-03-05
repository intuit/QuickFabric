package com.intuit.quickfabric.schedulers.dao;

import com.intuit.quickfabric.commons.vo.ClusterHealthCheckStatusType;
import com.intuit.quickfabric.commons.vo.ClusterHealthStatus;
import com.intuit.quickfabric.commons.vo.EMRClusterHealthTestCase;

import java.util.List;

public interface ClusterHealthCheckDao {
    List<ClusterHealthStatus> getEMRClusterHealthStatus(String clusterId);

    List<ClusterHealthStatus> getNewHealthTestCases();

    List<EMRClusterHealthTestCase> getFunctionalTestSuites(String testName);

    void updateTestCaseStatus(int executionId, ClusterHealthCheckStatusType status, String remark);

    List<EMRClusterHealthTestCase> getEMRClusterTestSuites(String clusterRole, String clusterType);

    void createHealthCheckTestCases(List<EMRClusterHealthTestCase> testCases, String clusterId, String clusterName, String executedBy);

}
