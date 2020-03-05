package com.intuit.quickfabric.commons.vo;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ClusterHealthCheckStatusUpdate {

    @NotNull(message = "execution Id is not valid")
    @Min(1)
    private int executionId;

    @NotNull(message = "status cannot be empty")
    @Size(min = 1)
    private String status;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getExecutionId() {
        return executionId;
    }

    public void setExecutionId(int executionId) {
        this.executionId = executionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ClusterHealthCheckStatusUpdate executionId:" + executionId + " status:" + status;
    }
}
