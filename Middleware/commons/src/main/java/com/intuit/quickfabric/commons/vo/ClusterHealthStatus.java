package com.intuit.quickfabric.commons.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ClusterHealthStatus {

    private int executionId;
    private String clusterId;
    private String testName;
    private ClusterHealthCheckStatusType status;
    private String clusterName;
    private String clusterType;
    private String clusterSegment;
    private String executionStartTime;
    private String executionEndTime;
    private String executedBy;
    private String remark;
    private boolean isMandatory;
    private int expiresInMinutes;
    private boolean isDisabled;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getExecutionId() {
        return executionId;
    }

    public void setExecutionId(int executionId) {
        this.executionId = executionId;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public ClusterHealthCheckStatusType getStatus() {
        return status;
    }

    public void setStatus(ClusterHealthCheckStatusType status) {
        this.status = status;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterType() {
        return clusterType;
    }

    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
    }

    public String getClusterSegment() {
        return clusterSegment;
    }

    public void setClusterSegment(String clusterSegment) {
        this.clusterSegment = clusterSegment;
    }

    public String getExecutionStartTime() {
        return executionStartTime;
    }

    public void setExecutionStartTime(String executionStartTime) {
        this.executionStartTime = executionStartTime;
    }

    public String getExecutionEndTime() {
        return executionEndTime;
    }

    public void setExecutionEndTime(String executionEndTime) {
        this.executionEndTime = executionEndTime;
    }

    public String getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(String executedBy) {
        this.executedBy = executedBy;
    }

    public void setIsMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public boolean getIsMandatory() {
        return isMandatory;
    }

    @Override
    public String toString() {
        return "ClusterHealthStatus -> " +
                " ExecutionId: " + executionId +
                " ClusterId:" + clusterId +
                " ClusterType:" + clusterType +
                " ClusterSegment:" + clusterSegment +
                " ClusterName:" + clusterName +
                " TestName:" + testName +
                " TestStatus:" + status +
                " IsMandatory:" + isMandatory;
    }

    public void setExpiresInMinutes(int expiresInMinutes) {
        this.expiresInMinutes = expiresInMinutes;
    }

    public int getExpiresInMinutes() {
        return expiresInMinutes;
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public boolean getIsDisabled() {
        return isDisabled;
    }
}