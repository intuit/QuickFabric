package com.intuit.quickfabric.commons.vo;

public class BootstrapTestResponse {

    private String statusCode;
    private String result;
    private Integer bootstrapCount;

    public Integer getBootstrapCount() {
        return bootstrapCount;
    }

    public void setBootstrapCount(Integer bootstrapCount) {
        this.bootstrapCount = bootstrapCount;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
