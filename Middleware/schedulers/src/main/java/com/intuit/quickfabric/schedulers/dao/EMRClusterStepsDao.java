package com.intuit.quickfabric.schedulers.dao;

import com.intuit.quickfabric.commons.vo.*;

import java.util.List;
import java.util.Set;

public interface EMRClusterStepsDao {

	void updateStepIdsInDB(StepResponseVO stepResponse, List<ClusterStep> steps);

	List<ClusterRequest> getStepsForNewSucceededClusters(Set<ClusterStatus> clusterstatuses,
			List<StepStatus> stepStatuses);

	public void updateBootstrapActionStatus(ClusterVO clusterDetails, String bootstrapStatus);

	public void saveBootstrapActionRequestForCluster(ClusterVO clusterDetails);

	List<BootstrapActionVO> getBootstrapActionsByClusterId(String clusterId);

}
