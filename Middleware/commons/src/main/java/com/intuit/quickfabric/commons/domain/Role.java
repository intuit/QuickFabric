package com.intuit.quickfabric.commons.domain;

public class Role {

    private int roleId;
    private String roleName;
    private int serviceId;

    public Role(int roleId, String roleName, int serviceId) {

        this.roleId = roleId;
        this.roleName = roleName;
        this.serviceId = serviceId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }
}
