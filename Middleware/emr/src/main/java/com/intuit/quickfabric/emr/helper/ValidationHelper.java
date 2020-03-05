package com.intuit.quickfabric.emr.helper;

import com.intuit.quickfabric.commons.exceptions.QuickFabricClientException;
import com.intuit.quickfabric.commons.vo.ClusterRequest;
import com.intuit.quickfabric.commons.vo.ClusterStepRequest;
import com.intuit.quickfabric.commons.vo.ClusterType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ValidationHelper {

	public void validateCreateClusterRequest(ClusterRequest clusterDetails) {
		// Need name, type, and segment to construct a cluster_name
		if (StringUtils.isBlank(clusterDetails.getType().getValue())
				|| StringUtils.isBlank(clusterDetails.getSegment()) || StringUtils.isBlank(clusterDetails.getAccount())
				|| StringUtils.isBlank(clusterDetails.getCreatedBy())) {
			throw new QuickFabricClientException("Invalid create cluster request. Required Parameters are: segment,type,account,created_by.");
		}
		if ((ClusterType.TRANSIENT.equals(clusterDetails.getType()) && clusterDetails.getSteps().size() == 0)
				|| (ClusterType.TRANSIENT.equals(clusterDetails.getType()) && StringUtils.isBlank(clusterDetails.getHeadlessUsers())
						|| StringUtils.isBlank(clusterDetails.getCreatedBy()))) {
			throw new QuickFabricClientException("Additional ones for transient are: headLessUsers and atleast one step");

		}
	     validateSteps(clusterDetails.getSteps());
	}

	private  void validateSteps(List<ClusterStepRequest> steps) {

		for (ClusterStepRequest step : steps) {
			if ( (StringUtils.isBlank(step.getArgs()))  ||  (StringUtils.isBlank(step.getName())) || (StringUtils.isBlank(step.getActionOnFailure()))
					|| (StringUtils.isBlank(step.getJar()))) {
				throw new QuickFabricClientException("Step should have Name,Jar and Argument");

			}
		}
	
	}

	public void validateTerminateClusterRequest(ClusterRequest clusterDetails) {
		if (StringUtils.isBlank(clusterDetails.getClusterName()) || StringUtils.isBlank(clusterDetails.getClusterId())
				|| StringUtils.isBlank(clusterDetails.getAccount())) {
			throw new QuickFabricClientException("Invalid terminate cluster request. Required Parameters are: clusterName,clusterID,account.");

		}
		
	}

	public void validateAddStepsReq(ClusterRequest stepReq) {
		if (StringUtils.isBlank(stepReq.getClusterName()) || StringUtils.isBlank(stepReq.getAccount())
				|| StringUtils.isBlank(stepReq.getClusterId())) {
            throw new QuickFabricClientException("Invalid add custom step request. "
                    + "Required Parameters for cluster metadata are: clusterName,account,clusterId.");
		}
		if (stepReq.getSteps().size() == 0) {

			throw new QuickFabricClientException("atleast one step is required");

		}
	    validateSteps(stepReq.getSteps());
	}
	
	public void validateDNSFlipReq(ClusterRequest clusterDetails) {
		if(StringUtils.isBlank(clusterDetails.getClusterId()) || StringUtils.isBlank(clusterDetails.getDnsName())) {
			throw new QuickFabricClientException("Invalid DNS Flip request. Required Parameters are: cluster ID, dns name");

		}
		
	}
}
