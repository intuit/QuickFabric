package com.intuit.quickfabric.commons.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.intuit.quickfabric.commons.vo.ClusterStatus;
import com.intuit.quickfabric.commons.vo.ClusterStep;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StepResponseVO {
    String statusCode;
    String clusterName;
    String clusterId;
    private ClusterStatus clusterStatus;
    List<ClusterStep> steps = new ArrayList<ClusterStep>();
    String type;
    Boolean isActive;
    String apiRequestId;
    String lambdaRequestId;

    public String getStatusCode() {
        return statusCode;
    }
    
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    
    public String getClusterName() {
        return clusterName;
    }
    
    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterId() {
        return clusterId;
    }
    
    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }
    
    public String getApiRequestId() {
        return apiRequestId;
    }

    public void setApiRequestId(String apiRequestId) {
        this.apiRequestId = apiRequestId;
    }
    public String getLambdaRequestId() {
        return lambdaRequestId;
    }
    public void setLambdaRequestId(String lambdaRequestId) {
        this.lambdaRequestId = lambdaRequestId;
    }

    public List<ClusterStep> getSteps() {
        return steps;
    }
     
    public void setSteps(List<ClusterStep> steps) {
        this.steps = steps;
    }
    
    public ClusterStatus getClusterStatus() {
        return clusterStatus;
    }
    
    public void setClusterStatus(ClusterStatus clusterStatus) {
        this.clusterStatus = clusterStatus;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
}
