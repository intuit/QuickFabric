package com.intuit.quickfabric.commons.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EMRClusterHealthTestCase {

    private int id;
    private String testName;
    private String testCriteria;
    private String clusterType;
    private String clusterSegment;
    private int expiresInMinutes;
    private boolean isMandatory;
    private boolean isDisabled;

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestCriteria() {
        return testCriteria;
    }

    public void setTestCriteria(String testCriteria) {
        this.testCriteria = testCriteria;
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

    public void setExpiresInMinutes(int expiresInMinutes) {
        this.expiresInMinutes = expiresInMinutes;
    }

    public int getExpiresInMinutes() {
        return expiresInMinutes;
    }

    public void setMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    @Override
    public String toString() {
        return "EMRClusterHealthTestCase id:" + id + " testName:" + testName + " testCriteria:" + testCriteria
                + " clusterType:" + clusterType + " clusterSegment:" + clusterSegment + " isMandatory:" + isMandatory;
    }

}