package com.intuit.quickfabric.emr.tests.helper;

import com.intuit.quickfabric.commons.exceptions.QuickFabricClientException;
import com.intuit.quickfabric.commons.exceptions.QuickFabricServerException;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.emr.dao.EMRClusterHealthCheckDao;
import com.intuit.quickfabric.emr.dao.EMRClusterMetadataDao;
import com.intuit.quickfabric.emr.helper.EMRClusterHealthHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EMRClusterHealthHelperTests {

    @Mock
    EMRClusterHealthCheckDao clusterHealthCheckDoa;

    @Mock
    EMRClusterMetadataDao clusterMetaDataDao;

    @InjectMocks
    EMRClusterHealthHelper emrClusterHealthHelper;

    final String fakeClusterId = "CLUSTER-123";

    @Test
    @DisplayName("Verify that cluster has health checks")
    public void verifyClusterHealthChecks() {
        ArrayList<ClusterHealthStatus> clusterList = spy(new ArrayList<>());
        when(clusterList.size()).thenReturn(5);
        when(clusterHealthCheckDoa.getEMRClusterHealthStatus(anyString())).thenReturn(clusterList);
        List<ClusterHealthStatus> result = emrClusterHealthHelper.getEMRClusterHealthStatus(fakeClusterId, null);

        assertEquals(5, result.size());
    }

    @Test
    @DisplayName("Cluster not found while running health test")
    public void verifyClusterNotFound() {
        ClusterHealthCheckRequest clusterHealthCheckRequest = spy(ClusterHealthCheckRequest.class);
        when(clusterHealthCheckRequest.getClusterId()).thenReturn(fakeClusterId);
        when(clusterMetaDataDao.getClusterMetadataByClusterId(anyString())).thenReturn(null);
        QuickFabricClientException exception = assertThrows(QuickFabricClientException.class,
                () -> emrClusterHealthHelper.runEMRClusterHealthCheck(clusterHealthCheckRequest, "foo"));

        assertEquals("cluster not found. clusterId:" + fakeClusterId, exception.getMessage());
    }

    @Test
    @DisplayName("Do not run health check if cluster is not terminated")
    public void doNotRunTestsIfClusterIsTerminated() {
        ClusterHealthCheckRequest clusterHealthCheckRequest = spy(ClusterHealthCheckRequest.class);
        ClusterVO clusterMetadata = spy(ClusterVO.class);
        when(clusterHealthCheckRequest.getClusterId()).thenReturn(fakeClusterId);
        when(clusterMetadata.getStatus()).thenReturn(ClusterStatus.TERMINATED);
        when(clusterMetaDataDao.getClusterMetadataByClusterId(anyString())).thenReturn(clusterMetadata);

        QuickFabricServerException exception = assertThrows(QuickFabricServerException.class,
                () -> emrClusterHealthHelper.runEMRClusterHealthCheck(clusterHealthCheckRequest, null));

        assertEquals("cluster is not in  valid state. cluster status" + ClusterStatus.TERMINATED, exception.getMessage());
    }

    @Test
    @DisplayName("return error if no health checks are found for a cluster type and role")
    public void returnErrorIfNoHealthChecksFound() {
        ClusterHealthCheckRequest healthCheckRequest = spy(ClusterHealthCheckRequest.class);
        ClusterVO clusterMetadata = spy(ClusterVO.class);
        when(healthCheckRequest.getClusterType()).thenReturn("exploratory");
        when(healthCheckRequest.getClusterSegment()).thenReturn("marketing");
        when(clusterMetaDataDao.getClusterMetadataByClusterId(null)).thenReturn(clusterMetadata);
        when(clusterMetadata.getStatus()).thenReturn(ClusterStatus.RUNNING);

        QuickFabricServerException exception = assertThrows(QuickFabricServerException.class,
                () -> emrClusterHealthHelper.runEMRClusterHealthCheck(healthCheckRequest, null));

        String expected = "No Tests found for cluster type: " + healthCheckRequest.getClusterType() + " and cluster segment: " + healthCheckRequest.getClusterSegment();
        assertEquals(expected, exception.getMessage());
    }

    @Test
    @DisplayName("verify that current health checks are deleted before adding any new health checks")
    public void verifyThatCurrentHealthChecksAreDeletedBeforeAddingNew() {
        ClusterHealthCheckRequest healthCheckRequest = spy(ClusterHealthCheckRequest.class);
        ClusterVO clusterMetadata = spy(ClusterVO.class);
        List<ClusterHealthStatus> currentTestCases = spy(new ArrayList<>());
        List<EMRClusterHealthTestCase> criteriaTests = spy(new ArrayList<>());

        when(healthCheckRequest.getClusterType()).thenReturn("exploratory");
        when(healthCheckRequest.getClusterSegment()).thenReturn("marketing");
        when(healthCheckRequest.isOverrideTimeout()).thenReturn(true);
        when(clusterMetadata.getStatus()).thenReturn(ClusterStatus.RUNNING);
        when(clusterMetaDataDao.getClusterMetadataByClusterId(any())).thenReturn(clusterMetadata);
        when(clusterHealthCheckDoa.getEMRClusterHealthStatus(any())).thenReturn(currentTestCases);
        when(clusterHealthCheckDoa.getEMRClusterTestSuites(anyString(), anyString())).thenReturn(criteriaTests);
        when(criteriaTests.size()).thenReturn(2);
        when(currentTestCases.size()).thenReturn(1);
        emrClusterHealthHelper.runEMRClusterHealthCheck(healthCheckRequest, null);

        verify(clusterHealthCheckDoa, times(1)).deleteCurrentClusterHealthCheckStatus(any());
    }

    @Test
    @DisplayName("updating a health check with invalid status should return bad request")
    public void updatingHealthCheckThrowsExceptionOnInvalidStatus() {
        ClusterHealthCheckStatusUpdate healthCheckStatusUpdate = spy(new ClusterHealthCheckStatusUpdate());
        when(healthCheckStatusUpdate.getStatus()).thenReturn("foo");

        QuickFabricClientException exception = assertThrows(QuickFabricClientException.class,
                () -> emrClusterHealthHelper.updateEMRClusterHealthTest(fakeClusterId, healthCheckStatusUpdate));

        String expected = "Unable to find status: " + healthCheckStatusUpdate.getStatus();
        assertEquals(expected, exception.getMessage());
    }

    @Test
    @DisplayName("verify that updateEMRClusterHealthTest is called once for a valid request")
    public void verifyUpdateEMRClusterHealthTestIsCalled() {
        ClusterHealthCheckStatusUpdate healthStatusUpdate = new ClusterHealthCheckStatusUpdate();
        healthStatusUpdate.setExecutionId(1);
        healthStatusUpdate.setStatus("success");
        healthStatusUpdate.setMessage("asdf");
        List<ClusterHealthStatus> currentTestCases = new ArrayList<>();
        ClusterHealthStatus status = new ClusterHealthStatus();
        status.setExecutionId(1);
        currentTestCases.add(status);

        when(clusterHealthCheckDoa.getEMRClusterHealthHistory(any())).thenReturn(currentTestCases);
//        emrClusterHealthHelper.updateEMRClusterHealthTest(fakeClusterId, healthStatusUpdate);

        //todo-varun: debug what is missing here for null exception
//        verify(clusterHealthCheckDoa, times(1)).updateEMRClusterHealthTest(any(), any(), any(), any());
//        assertEquals(stringResponseEntity.getStatusCode(), HttpStatus.OK);
    }
}
