package com.intuit.quickfabric.commons.vo;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ChangeRoleVO {

    @NotNull
    @Size(min = 1)
    private String serviceName;

    @NotNull
    @Size(min = 1)
    private String awsAccountName;

    @NotNull
    @Size(min = 1)
    private String segmentName;

    @NotNull
    @Size(min = 1)
    private String roleName;

    public String getAwsAccountName() {
        return awsAccountName;
    }

    public void setAwsAccountName(String awsAccountName) {
        this.awsAccountName = awsAccountName;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "ChangeRoleVO{" +
                "serviceName='" + serviceName + '\'' +
                ", awsAccountName='" + awsAccountName + '\'' +
                ", segmentName='" + segmentName + '\'' +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
