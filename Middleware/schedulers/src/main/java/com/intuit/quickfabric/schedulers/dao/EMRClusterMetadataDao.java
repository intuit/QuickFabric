package com.intuit.quickfabric.schedulers.dao;

import com.intuit.quickfabric.commons.domain.UserAccess;
import com.intuit.quickfabric.commons.vo.*;

import java.util.List;
import java.util.Set;

public interface EMRClusterMetadataDao {

	ClusterVO getAllEMRClusterDataForAMI(String clusterId) ;

	ClusterVO getClusterMetadata(String clusterId);

	ClusterVO getClusterMetadataByOriginalClusterId(String originalClusterId);

	void updateNewClusterByOriginalClusterId(String originalClusterId, String newClusterId);

    ClusterVO getEMRMetadataForAddQBUser(String emrName);

    boolean cleanUpTerminatedClusters(int daysAgo);

    List<SegmentVO> getSegment(String segmentName);

    List<UserVO> getSubscribers();
    
    List<UserVO> getUsers();

    List<SubscriptionVO> getSubscriptionsForUser(long userId);

    List<UserAccess> getReadAccessForUser(long userId);

    void saveNewClusterDetails(ClusterVO clusterDetails);

    List<EMRClusterMetricsVO> getClusterMetadataForMetrics(Set<ClusterStatus> statuses);

    List<ClusterRequest> getClustersforAMIRotation(Set<ClusterStatus> statuses);

    List<ClusterVO> getAMIRotationReport(String segmentName);

    List<ClusterRequest> getCompletedTestingClusters(Set<ClusterStatus> statuses, List<StepStatus> stepStatuses);

    List<ClusterRequest> getCompletedNonTransientClusters(Set<ClusterStatus> statuses, List<StepStatus> stepStatuses);

    void markClusterforTermination(String clusterId) ;

    List<ClusterRequest> getClusterWithSucceededStatusAndStepsWithCompletedStatus(Set<ClusterStatus> statuses,
            List<StepStatus> stepStatuses);

    void updateClusterStatusByClusterId(ClusterVO clusterDetails) ;

    void updateClusterStatusInDB(ClusterVO clusterDetails) ;

    List<ClusterRequest> getClustersWithClusterStatuses(Set<ClusterStatus> statues) ;

    List<ClusterVO> getClustersToValidate(Set<ClusterStatus> statues) ;

    void updateClusterDNSinDB(String clusterName, String dnsName);

}