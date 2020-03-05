package com.intuit.quickfabric.emr.dao;

import com.intuit.quickfabric.commons.vo.AwsAccountProfile;
import com.intuit.quickfabric.commons.vo.ClusterRequest;
import com.intuit.quickfabric.commons.vo.ClusterVO;
import com.intuit.quickfabric.commons.vo.SegmentVO;

import java.util.List;


public interface EMRClusterMetadataDao {
    List<ClusterVO> getAllEMRClusterMetadata(String clusterName, String clusterType, String account);

	ClusterVO getEMRClusterMetadataByMetadataId(long metadataId);

	ClusterVO getClusterMetadataByClusterId(String clusterId);

	ClusterVO getAllEMRClusterDataForAMI(String clusterId) ;

	ClusterVO getClusterMetadataByOriginalClusterId(String originalClusterId);

	void updateNewClusterByOriginalClusterId(String originalClusterId, String newCluster);

	List<AwsAccountProfile> getAWSAccountProfile(String accountId);

    List<AwsAccountProfile> getAllAWSAccountProfiles();

    List<String> getUserRoles();

    void updateAutopilotConfig(ClusterRequest clusterDetails);

    void updateDoTerminateConfig(ClusterRequest clusterDetails);

	void saveNewClusterDetails(ClusterVO clusterDetails) ;

	void updateClusterStatusByClusterId(ClusterVO clusterDetails);

	void updateClusterDNSinDB(String clusterName, String dnsName) ;

	void markClusterforTermination(String clusterId);

    List<SegmentVO> getSegment(String segmentName);

    List<SegmentVO> getAllSegments();
}