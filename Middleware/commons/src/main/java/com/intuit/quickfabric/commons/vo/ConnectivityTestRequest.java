package com.intuit.quickfabric.commons.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConnectivityTestRequest {

    public ConnectivityTestRequest() {
        scriptName = "emr_decrypt_test.sh";
        parametersRequest = new BootstrapParametersRequest();
    }

    @JsonProperty("type")
    private String clusterType;
    private String account;
    private String clusterId;
    private String clusterName;
    private String scriptName;
    private int executionId;
    @JsonProperty("parameters")
    private BootstrapParametersRequest parametersRequest;

    public BootstrapParametersRequest getParametersRequest() {
        return parametersRequest;
    }

    public void setParametersRequest(BootstrapParametersRequest parametersRequest) {
        this.parametersRequest = parametersRequest;
    }

    public String getClusterType() {
        return clusterType;
    }

    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public int getExecutionId() {
        return executionId;
    }

    public void setExecutionId(int executionId) {
        this.executionId = executionId;
    }
}
