package com.intuit.quickfabric.commons.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)

public class ClusterVO {

    private long metadataId;
    private String account;
    private String clusterId;
    private String clusterName;
    private String message;
    private String segment;
    @JsonDeserialize(as = ClusterStatus.class)
    private ClusterStatus status;
    private String statusCode;
    private String terminationStatus;
    @JsonDeserialize(as = ClusterType.class)
    private ClusterType type;
    private String subType;
    private String creationTimestamp;
    private String apiRequestId;
    private String lambdaRequestId;
    private List<ClusterStep> steps = new ArrayList<ClusterStep>();
    private String headlessUsers;
    private String clusterDetails;
    private boolean doTerminate;
    private String coreEbsVolSize;
    private String coreInstanceCount;
    private String coreInstanceType;
    private String createdBy;
    private String customAmiId;
    private String lastUpdatedBy;
    private String masterEbsVolSize;
    private String masterInstanceType;
    private String taskEbsVolSize;
    private String taskInstanceCount;
    private String taskInstanceType;
    private String dnsName;
    private boolean dnsFlip;
    private boolean isProd;
    private String originalClusterId;
    private boolean dnsFlipCompleted;
    @JsonIgnore
    private String jiraTicket;
    @JsonIgnore
    private String snowTicket;
    private String newClusterId;
    @JsonIgnore
    private int amiRotationDaysToGo;
    private String rotationDaysToGo;
    private Boolean autoAmiRotation;

    //each is an hour of the day. AMI will rotate if it is between the start and end past SLA.
    private Integer autopilotWindowStart;
    private Integer autopilotWindowEnd;

    private int amiRotationSlaDays;

    private String masterIp;
    private String rmUrl;
    private List<BootstrapActionVO> bootstrapActions = new ArrayList<BootstrapActionVO>();

    //Objects for Autoscaling
    private String instanceGroup;
    private int min;
    private int max;

    @JsonProperty("status")
    public ClusterStatus getClusterStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setClusterStatus(ClusterStatus clusterStatus) {
        this.status = clusterStatus;
    }

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

    public ClusterType getType() {
        return type;
    }

    public void setType(ClusterType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getTerminationStatus() {
        return terminationStatus;
    }

    public void setTerminationStatus(String terminationStatus) {
        this.terminationStatus = terminationStatus;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String cluster_id) {
        this.clusterId = cluster_id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public List<ClusterStep> getSteps() {
        return steps;
    }

    public void setSteps(List<ClusterStep> steps) {
        this.steps = steps;
    }

    public String getClusterDetails() {
        return clusterDetails;
    }

    public void setClusterDetails(String clusterDetails) {
        this.clusterDetails = clusterDetails;
    }

    public ClusterStatus getStatus() {
        return status;
    }

    public void setStatus(ClusterStatus status) {
        this.status = status;
    }

    public String getCoreInstanceCount() {
        return coreInstanceCount;
    }

    public void setCoreInstanceCount(String coreInstanceCount) {
        this.coreInstanceCount = coreInstanceCount;
    }

    public String getTaskInstanceCount() {
        return taskInstanceCount;
    }

    public void setTaskInstanceCount(String taskInstanceCount) {
        this.taskInstanceCount = taskInstanceCount;
    }

    public String getCoreInstanceType() {
        return coreInstanceType;
    }

    public void setCoreInstanceType(String coreInstanceType) {
        this.coreInstanceType = coreInstanceType;
    }

    public String getTaskInstanceType() {
        return taskInstanceType;
    }

    public void setTaskInstanceType(String taskInstanceType) {
        this.taskInstanceType = taskInstanceType;
    }

    public String getMasterInstanceType() {
        return masterInstanceType;
    }

    public void setMasterInstanceType(String masterInstanceType) {
        this.masterInstanceType = masterInstanceType;
    }

    public String getCoreEbsVolSize() {
        return coreEbsVolSize;
    }

    public void setCoreEbsVolSize(String coreEbsVolSize) {
        this.coreEbsVolSize = coreEbsVolSize;
    }

    public String getTaskEbsVolSize() {
        return taskEbsVolSize;
    }

    public void setTaskEbsVolSize(String taskEbsVolSize) {
        this.taskEbsVolSize = taskEbsVolSize;
    }

    public String getMasterEbsVolSize() {
        return masterEbsVolSize;
    }

    public void setMasterEbsVolSize(String masterEbsVolSize) {
        this.masterEbsVolSize = masterEbsVolSize;
    }

    public String getCustomAmiId() {
        return customAmiId;
    }

    public void setCustomAmiId(String customAmiId) {
        this.customAmiId = customAmiId;
    }

    public boolean getDoTerminate() {
        return doTerminate;
    }

    public void setDoTerminate(boolean doTerminate) {
        this.doTerminate = doTerminate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getHeadlessUsers() {
        return headlessUsers;
    }

    public void setHeadlessUsers(String headLessUsers) {
        this.headlessUsers = headLessUsers;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public String getDnsName() {
        return dnsName;
    }

    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }

    public boolean getDnsFlip() {
        return dnsFlip;
    }

    public void setDnsFlip(boolean dnsFlip) {
        this.dnsFlip = dnsFlip;
    }

    public boolean getIsProd() {
        return isProd;
    }

    public void setIsProd(boolean isProd) {
        this.isProd = isProd;
    }

    public String getOriginalClusterId() {
        return originalClusterId;
    }

    public void setOriginalClusterId(String originalClusterId) {
        this.originalClusterId = originalClusterId;
    }

    public boolean getDnsFlipCompleted() {
        return dnsFlipCompleted;
    }

    public void setDnsFlipCompleted(boolean dnsFlipCompleted) {
        this.dnsFlipCompleted = dnsFlipCompleted;
    }

    public String getJiraTicket() {
        return jiraTicket;
    }

    public void setJiraTicket(String jiraTicket) {
        this.jiraTicket = jiraTicket;
    }

    public String getSnowTicket() {
        return snowTicket;
    }

    public void setSnowTicket(String snowTicket) {
        this.snowTicket = snowTicket;
    }

    public String getRequestTicket() {
        return snowTicket != null ? snowTicket : jiraTicket;
    }

    public String getNewClusterId() {
        return newClusterId;
    }

    public void setNewClusterId(String newClusterId) {
        this.newClusterId = newClusterId;
    }

    @JsonIgnore
    public int getAMIRotationDaysToGo() {
        return amiRotationDaysToGo;
    }

    @JsonIgnore
    public void setAMIRotationDaysTogo(int amiRotationDaysToGo) {
        this.amiRotationDaysToGo = amiRotationDaysToGo;
    }

    public String getRotationDaysToGo() {
        return rotationDaysToGo;
    }

    public void setRotationDaysToGo(String rotationDaysToGo) {
        this.rotationDaysToGo = rotationDaysToGo;
    }

    public Boolean getAutoAmiRotation() {
        return autoAmiRotation;
    }

    public void setAutoAmiRotation(Boolean autoAMIRotation) {
        this.autoAmiRotation = autoAMIRotation;
    }

    public Integer getAutopilotWindowStart() {
        return autopilotWindowStart;
    }

    public void setAutopilotWindowStart(Integer autopilotWindowStart) {
        this.autopilotWindowStart = autopilotWindowStart;
    }

    public Integer getAutopilotWindowEnd() {
        return autopilotWindowEnd;
    }

    public void setAutopilotWindowEnd(Integer autopilotWindowEnd) {
        this.autopilotWindowEnd = autopilotWindowEnd;
    }

    public int getAmiRotationSlaDays() {
        return amiRotationSlaDays;
    }

    public void setAmiRotationSlaDays(int amiRotationSlaDays) {
        this.amiRotationSlaDays = amiRotationSlaDays;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getMasterIp() {
        return masterIp;
    }

    public void setMasterIp(String masterIp) {
        this.masterIp = masterIp;
    }

    public String getRmUrl() {
        return rmUrl;
    }

    public void setRmUrl(String rmUrl) {
        this.rmUrl = rmUrl;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public long getMetadataId() {
        return metadataId;
    }

    public void setMetadataId(long metadataId) {
        this.metadataId = metadataId;
    }

    public List<BootstrapActionVO> getBootstrapActions() {
        return bootstrapActions;
    }

    public void setBootstrapActions(List<BootstrapActionVO> bootstrapActions) {
        this.bootstrapActions = bootstrapActions;
    }

    public String getInstanceGroup() {
        return instanceGroup;
    }

    public void setInstanceGroup(String instanceGroup) {
        this.instanceGroup = instanceGroup;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
