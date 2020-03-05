package com.intuit.quickfabric.commons.vo;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EMRClusterMetricsVO {


    private String emrId;
    private String emrName;
    private String rmUrl;
    private String refreshTimestamp;
    private String metricsJson;
    @JsonDeserialize(as=ClusterStatus.class)
    private ClusterStatus emrStatus;
    private int activeNodes;
    private float memoryUsagePct;
    private float coresUsagePct;
    private float cost;
    private int containersPending;
    private int appsPending;
    private int appsRunning;
    private int appsSucceeded;
    private int appsFailed;
    private String account;
    private String clusterCreateTimestamp;
    @JsonDeserialize(as=ClusterType.class)
    private ClusterType type;
    private String clusterSegment;
    private List<EMRTimeSeriesReportVO> timeSeriesMetrics;
    private String createdBy;

    public EMRClusterMetricsVO() {
    }

    public ClusterStatus getEmrStatus() {
        return emrStatus;
    }


    public void setEmrStatus(ClusterStatus emrStatus) {
        this.emrStatus = emrStatus;
    }


    public String getAccount() {
        return account;
    }


    public void setAccount(String account) {
        this.account = account;
    }


    public String getEmrId() {
        return emrId;
    }


    public void setEmrId(String emrId) {
        this.emrId = emrId;
    }

    public String getEmrName() {
        return emrName;
    }


    public void setEmrName(String emrName) {
        this.emrName = emrName;
    }
    public String getRmUrl() {
        return rmUrl;
    }


    public void setRmUrl(String rmUrl) {
        this.rmUrl = rmUrl;
    }


    public String getRefreshTimestamp() {
        return refreshTimestamp;
    }


    public void setRefreshTimestamp(String refreshTimestamp) {
        this.refreshTimestamp = refreshTimestamp;
    }


    public String getMetricsJson() {
        return metricsJson;
    }


    public void setMetricsJson(String metricsJson) {
        this.metricsJson = metricsJson;
    }


    public float getMemoryUsagePct() {
        return memoryUsagePct;
    }


    public void setMemoryUsagePct(float memoryUsagePct) {
        this.memoryUsagePct = memoryUsagePct;
    }


    public float getCoresUsagePct() {
        return coresUsagePct;
    }


    public void setCoresUsagePct(float coresUsagePct) {
        this.coresUsagePct = coresUsagePct;
    }


    public float getCost() {
        return cost;
    }


    public void setCost(float cost) {
        this.cost = cost;
    }

    public ClusterType getType() {
        return type;
    }


    public void setType(ClusterType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "EMRClusterMetricsVO [emr_id=" + emrId + ", emr_name=" + emrName + ", rm_url=" + rmUrl
                + ", refresh_timestamp=" + refreshTimestamp + ", metrics_json=" + metricsJson + ", emr_status="
                + emrStatus + ", active_nodes=" + getActiveNodes() + ", memory_usage_pct="
                + memoryUsagePct + ", cores_usage_pct=" + coresUsagePct + ", cost="
                + cost + ", containers_pending=" + getContainersPending() + ", apps_pending="
                + getAppsPending() + ", account=" + account + ", cluster_create_timestamp=" + getClusterCreateTimestamp()
                + ", type=" + type + "]";
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getAppsRunning() {
        return appsRunning;
    }

    public void setAppsRunning(int appsRunning) {
        this.appsRunning = appsRunning;
    }

    public int getAppsSucceeded() {
        return appsSucceeded;
    }

    public void setAppsSucceeded(int appsSucceeded) {
        this.appsSucceeded = appsSucceeded;
    }

    public int getAppsFailed() {
        return appsFailed;
    }

    public void setAppsFailed(int appsFailed) {
        this.appsFailed = appsFailed;
    }

    public String getClusterSegment() {
        return clusterSegment;
    }

    public void setClusterSegment(String clusterSegment) {
        this.clusterSegment = clusterSegment;
    }

    public List<EMRTimeSeriesReportVO> getTimeSeriesMetrics() {
        return timeSeriesMetrics;
    }

    public void setTimeSeriesMetrics(List<EMRTimeSeriesReportVO> timeSeriesMetrics) {
        this.timeSeriesMetrics = timeSeriesMetrics;
    }

    public int getActiveNodes() {
        return activeNodes;
    }

    public void setActiveNodes(int activeNodes) {
        this.activeNodes = activeNodes;
    }

    public int getContainersPending() {
        return containersPending;
    }

    public void setContainersPending(int containersPending) {
        this.containersPending = containersPending;
    }

    public int getAppsPending() {
        return appsPending;
    }

    public void setAppsPending(int appsPending) {
        this.appsPending = appsPending;
    }

    public String getClusterCreateTimestamp() {
        return clusterCreateTimestamp;
    }

    public void setClusterCreateTimestamp(String clusterCreateTimestamp) {
        this.clusterCreateTimestamp = clusterCreateTimestamp;
    }
  

}