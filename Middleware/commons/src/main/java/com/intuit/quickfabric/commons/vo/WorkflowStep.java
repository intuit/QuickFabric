package com.intuit.quickfabric.commons.vo;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class WorkflowStep {

    public WorkflowStep() {
        workflowStatus = WorkflowStatus.NEW;
    }

    private WorkflowType workflowType;

    private WorkflowStatus workflowStatus;

    @JsonIgnore
    private String tableName;
    private String message;

    public WorkflowType getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(WorkflowType workflowType) {
        this.workflowType = workflowType;
    }

    public WorkflowStatus getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(WorkflowStatus workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
