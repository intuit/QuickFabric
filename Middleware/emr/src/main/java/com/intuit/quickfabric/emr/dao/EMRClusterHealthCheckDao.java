package com.intuit.quickfabric.emr.dao;


import com.intuit.quickfabric.commons.vo.*;

import java.util.List;

public interface EMRClusterHealthCheckDao {
    public List<ClusterHealthStatus> getEMRClusterHealthStatus(String clusterId);

    List<ClusterHealthStatus> getEMRClusterHealthHistory(String clusterId);

    List<EMRClusterHealthTestCase> getEMRClusterTestSuites(String clusterSegment, String clusterType);

    EMRClusterHealthTestCase getEMRClusterTestSuitesForValidation(String clusterSegment, String clusterType, String testSuiteName);
    

    void deleteCurrentClusterHealthCheckStatus(String clusterId);

    void createHealthCheckTestCases(List<EMRClusterHealthTestCase> testCases, String clusterId, String clusterName, String executedBy);

    void updateEMRClusterHealthTest(String clusterId, int executionId, ClusterHealthCheckStatusType status, String message);

    int getClusterTestTimeout(String clusterType);

    List<ClusterStep> getClusterBootStraps(String clusterId);
     
    void addEMRClusterTestSuitesDefinition(ClusterTestSuitesDefinitionVO testSuitesDetails)  ;

    List<ClusterStep> getClusterCustomSteps(String clusterId);

    void updateEMRClusterTestSuitesDefinition(ClusterTestSuitesDefinitionVO testSuitesDefinition);
}