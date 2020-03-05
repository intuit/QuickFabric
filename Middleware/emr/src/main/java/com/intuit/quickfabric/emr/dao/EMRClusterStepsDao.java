package com.intuit.quickfabric.emr.dao;

import com.intuit.quickfabric.commons.vo.BootstrapActionVO;
import com.intuit.quickfabric.commons.vo.ClusterStep;
import com.intuit.quickfabric.commons.vo.ClusterVO;
import com.intuit.quickfabric.commons.vo.StepResponseVO;

import java.util.List;


public interface EMRClusterStepsDao {

	void saveStepRequestForCluster(ClusterVO clusterDetails);

	void saveStepRequestForCluster(String clusterName, ClusterStep step);

    StepResponseVO getStepsOfACluster(String clusterId);

	void updateStepIdsInDB(StepResponseVO stepResponse, List<ClusterStep> steps);

	List<ClusterStep> getStepsByStepIds(List<String> stepIds);

	void saveBootstrapActionRequestForCluster(ClusterVO clusterDetails);

	List<BootstrapActionVO> getBootstrapActionsByClusterId(String clusterId);

}
