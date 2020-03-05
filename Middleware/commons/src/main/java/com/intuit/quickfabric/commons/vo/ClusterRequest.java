package com.intuit.quickfabric.commons.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ClusterRequest {

    private String clusterName;
    private String clusterId;
    private List<ClusterStepRequest> steps = new ArrayList<ClusterStepRequest>();
    private boolean doTerminate;
    private String coreInstanceCount;
    private String taskInstanceCount;
    private String coreInstanceType;
    private String taskInstanceType;
    private String coreEbsVolSize;
    private String taskEbsVolSize;
    private String masterEbsVolSize;
    private String customAmiId;
    private String masterInstanceType;
    private String createdBy;
    private String lastUpdatedBy;
    private String account;
    private ClusterType type;
    private String segment;
    private String headlessUsers;
    private String subType;
    private boolean dnsFlip;
    private boolean isProd;
    private String originalClusterId;
    private String jiraTicket;
    private String snowTicket;
    private Boolean autoAmiRotation;
    @JsonIgnore
    private String creationTimestamp;
    private int amiRotationSlaDays;
    private List<BootstrapActionVO> bootstrapActions = new ArrayList<BootstrapActionVO>();

    /* Each of these is an hour of the day. AMI will rotate if it is between the start and end
     * and past SLA.
     */
    private Integer autopilotWindowStart;
    private Integer autopilotWindowEnd;
    
    private String dnsName;
    
    //Objects for Autoscaling
    private String instanceGroup;
    private int min;
    private int max;
    
    public String getClusterName() {
        return clusterName;
    }
    
    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }


    @Override
    public boolean equals(Object req){
        if(this==req){
            return true;
        }
        if(!(req instanceof ClusterRequest)){
            return false;
        }
        ClusterRequest obj = (ClusterRequest) req;
        return this.getClusterName().equalsIgnoreCase(obj.getClusterName());
    }


    @Override
    public int hashCode(){
        return Objects.hash(this.getClusterName());
    }

    public List<ClusterStepRequest> getSteps() {
        return steps;
    }
    
    public void setSteps(List<ClusterStepRequest> steps) {
        this.steps = steps;
    }
    
    public String getClusterId() {
        return clusterId;
    }
    
    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }
    
    public boolean getDoTerminate() {
        return doTerminate;
    }
    
    public void setDoTerminate(boolean doTerminate) {
        this.doTerminate = doTerminate;
    }
    
    public String getCoreInstanceCount() {
        return coreInstanceCount;
    }

    public void setCoreInstanceCount(String coreInstanceount) {
        this.coreInstanceCount = coreInstanceount;
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

    public String getMasterInstanceType() {
        return masterInstanceType;
    }

    public void setMasterInstanceType(String masterInstanceType) {
        this.masterInstanceType = masterInstanceType;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }
    
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
    
    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    
    public ClusterType getType() {
        return type;
    }
    
    public void setType(ClusterType type) {
        this.type = type;
    }
    
    public String getHeadlessUsers() {
        return headlessUsers;
    }
    
    public void setHeadlessUsers(String headlessUsers) {
        this.headlessUsers = headlessUsers;
    }
    
    public String getSegment() {
        return segment;
    }
    
    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getSubType() {
        return subType;
    }
    
    public void setSubType(String subType) {
        this.subType = subType;
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

    public String getJiraTicket() {
        return jiraTicket;
    }
    
    public void setJiraTicket(String snowTicket) {
        this.jiraTicket = snowTicket;
    }
    
    public String getSnowTicket() {
        return snowTicket;
    }
    
    public void setSnowTicket(String snowTicket) {
        this.snowTicket = snowTicket;
    }

    public Boolean getAutoAmiRotation() {
        return autoAmiRotation;
    }

    public void setAutoAmiRotation(Boolean autoAMIRotation) {
        this.autoAmiRotation = autoAMIRotation;
    }

    @JsonIgnore
    public String getCreationTimestamp() {
        return creationTimestamp;
    }

    @JsonIgnore
    public void setCreationTimestamp(String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
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

	public List<BootstrapActionVO> getBootstrapActions() {
		return bootstrapActions;
	}

	public void setBootstrapActions(List<BootstrapActionVO> bootstrapActions) {
		this.bootstrapActions = bootstrapActions;
	}

    public String getDnsName() {
        return dnsName;
    }

    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
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

    @Override
    public String toString() {
        return "ClusterRequest{" +
                "clusterName='" + clusterName + '\'' +
                ", clusterId='" + clusterId + '\'' +
                ", steps=" + steps +
                ", doTerminate=" + doTerminate +
                ", coreInstanceCount='" + coreInstanceCount + '\'' +
                ", taskInstanceCount='" + taskInstanceCount + '\'' +
                ", coreInstanceType='" + coreInstanceType + '\'' +
                ", taskInstanceType='" + taskInstanceType + '\'' +
                ", coreEbsVolSize='" + coreEbsVolSize + '\'' +
                ", taskEbsVolSize='" + taskEbsVolSize + '\'' +
                ", masterEbsVolSize='" + masterEbsVolSize + '\'' +
                ", customAmiId='" + customAmiId + '\'' +
                ", masterInstanceType='" + masterInstanceType + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", lastUpdatedBy='" + lastUpdatedBy + '\'' +
                ", account='" + account + '\'' +
                ", type=" + type +
                ", segment='" + segment + '\'' +
                ", headlessUsers='" + headlessUsers + '\'' +
                ", subType='" + subType + '\'' +
                ", dnsFlip=" + dnsFlip +
                ", isProd=" + isProd +
                ", originalClusterId='" + originalClusterId + '\'' +
                ", jiraTicket='" + jiraTicket + '\'' +
                ", snowTicket='" + snowTicket + '\'' +
                ", autoAmiRotation=" + autoAmiRotation +
                ", creationTimestamp='" + creationTimestamp + '\'' +
                ", amiRotationSlaDays=" + amiRotationSlaDays +
                ", bootstrapActions=" + bootstrapActions +
                ", autopilotWindowStart=" + autopilotWindowStart +
                ", autopilotWindowEnd=" + autopilotWindowEnd +
                ", dnsName='" + dnsName + '\'' +
                ", instanceGroup='" + instanceGroup + '\'' +
                ", min=" + min +
                ", max=" + max +
                '}';
    }
}
