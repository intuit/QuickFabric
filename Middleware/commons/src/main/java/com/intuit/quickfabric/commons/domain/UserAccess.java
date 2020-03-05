package com.intuit.quickfabric.commons.domain;

import com.intuit.quickfabric.commons.vo.ServiceType;

public class UserAccess {

    private String firstName;
    private String lastName;
    private String awsAccountName;
    private ServiceType serviceType;
    private String accountEnv;
    private String segmentName;
    private String roleName;
    private int roleId;
    private int segmentId;
    private int serviceTypeId;
    private int awsAccountProfileId;
    private int userAccountSegmentRoleMappingId;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setAwsAccountName(String awsAccountName) {
        this.awsAccountName = awsAccountName;
    }

    public String getAwsAccountName() {
        return awsAccountName;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setAccountEnv(String accountEnv) {
        this.accountEnv = accountEnv;
    }

    public String getAccountEnv() {
        return accountEnv;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getRoleId() {
        return roleId;
    }

    public int getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public int getAwsAccountProfileId() {
        return awsAccountProfileId;
    }

    public void setAwsAccountProfileId(int awsAccountProfileId) {
        this.awsAccountProfileId = awsAccountProfileId;
    }

    public void setUserAccountSegmentRoleMappingId(int userAccountSegmentRoleMappingId) {
        this.userAccountSegmentRoleMappingId = userAccountSegmentRoleMappingId;
    }

    public int getUserAccountSegmentRoleMappingId() {
        return userAccountSegmentRoleMappingId;
    }

    @Override
    public String toString() {
        return "UserAccess{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", awsAccountName='" + awsAccountName + '\'' +
                ", serviceType=" + serviceType +
                ", awsAccountType='" + accountEnv + '\'' +
                ", segmentName='" + segmentName + '\'' +
                ", roleName='" + roleName + '\'' +
                ", roleId=" + roleId +
                ", segmentId=" + segmentId +
                ", serviceTypeId=" + serviceTypeId +
                ", awsAccountProfileId=" + awsAccountProfileId +
                ", userAccountSegmentRoleMappingId=" + userAccountSegmentRoleMappingId +
                '}';
    }
}
