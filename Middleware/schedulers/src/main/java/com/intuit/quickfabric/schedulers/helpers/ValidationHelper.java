package com.intuit.quickfabric.schedulers.helpers;

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
            String invalidReq = "Invalid create cluster request. Required Parameters are: segment,type,account,created_by. Additional ones for transient are: headLessUsers and atleast one step";
            throw new QuickFabricClientException(invalidReq);
        }
        if ((ClusterType.TRANSIENT.equals(clusterDetails.getType()) && clusterDetails.getSteps().size() == 0)
                || (ClusterType.TRANSIENT.equals(clusterDetails.getType()) && StringUtils.isBlank(clusterDetails.getHeadlessUsers())
                || StringUtils.isBlank(clusterDetails.getCreatedBy()))) {
            throw new QuickFabricClientException("Invalid create cluster request. Required Parameters are cluster type, created by, headless users, steps.");
        }

        validateSteps(clusterDetails.getSteps());
    }

    private void validateSteps(List<ClusterStepRequest> steps) {
        for (ClusterStepRequest step : steps) {
            if ((StringUtils.isBlank(step.getName())) || (StringUtils.isBlank(step.getActionOnFailure()))
                    || (StringUtils.isBlank(step.getJar()))) {
                throw new QuickFabricClientException("invalid steps requests. required parameters: step name, actions and jar");
            }
        }
    }

    public void validateTerminateClusterRequest(ClusterRequest clusterDetails) {
        if (StringUtils.isBlank(clusterDetails.getClusterName()) || StringUtils.isBlank(clusterDetails.getClusterId())
                || StringUtils.isBlank(clusterDetails.getAccount())) {
            throw new QuickFabricClientException("Invalid terminate Cluster request. Required params are: cluster_name,cluster_id,account.");
        }
    }
}
