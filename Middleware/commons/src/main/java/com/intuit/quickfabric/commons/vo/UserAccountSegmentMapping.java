package com.intuit.quickfabric.commons.vo;

public class UserAccountSegmentMapping {
    private int mappingId;
    private int segmentId;
    private int userId;
    private int awsAccountId;

    public void setMappingId(int mappingId) {
        this.mappingId = mappingId;
    }

    public int getMappingId() {
        return mappingId;
    }

    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
    }

    public int getSegmentId() {
        return segmentId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setAwsAccountId(int awsAccountId) {
        this.awsAccountId = awsAccountId;
    }

    public int getAwsAccountId() {
        return awsAccountId;
    }
}
