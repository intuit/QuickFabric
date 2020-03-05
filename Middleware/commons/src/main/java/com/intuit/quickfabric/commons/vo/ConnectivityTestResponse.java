package com.intuit.quickfabric.commons.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConnectivityTestResponse {

    private String statusCode;
    private String status;
    private String apiRequestId;
    private String labmdaRequestId;
    @JsonProperty("executionId")
    private String serverlessExecutionId;
    private String errorType;
    private String type;
    private String logStreamName;
    private String lambdaFunctionName;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApiRequestId() {
        return apiRequestId;
    }

    public void setApiRequestId(String apiRequestId) {
        this.apiRequestId = apiRequestId;
    }

    public String getLabmdaRequestId() {
        return labmdaRequestId;
    }

    public void setLabmdaRequestId(String labmdaRequestId) {
        this.labmdaRequestId = labmdaRequestId;
    }

    public String getServerlessExecutionId() {
        return serverlessExecutionId;
    }

    public void setServerlessExecutionId(String serverlessExecutionId) {
        this.serverlessExecutionId = serverlessExecutionId;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLogStreamName() {
        return logStreamName;
    }

    public void setLogStreamName(String logStreamName) {
        this.logStreamName = logStreamName;
    }

    public String getLambdaFunctionName() {
        return lambdaFunctionName;
    }

    public void setLambdaFunctionName(String lambdaFunctionName) {
        this.lambdaFunctionName = lambdaFunctionName;
    }
}
