package com.intuit.quickfabric.commons.vo;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LoginRolesVO {

    public LoginRolesVO() {
        services = new ArrayList<>();
    }

    private int userId;
    private String emailId;
    private String firstName;
    private String lastName;
    private String creationDate;
    private List<ServiceVO> services;
    private String passcode;
    private boolean isSuperAdmin;

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        isSuperAdmin = superAdmin;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public List<ServiceVO> getServices() {
        return services;
    }

    public void setService(ServiceVO service) {

        if (services.stream().anyMatch(x -> x.getServiceType() == service.getServiceType())) {
            throw new IllegalArgumentException(String.format("service of type %s already exists in the list", service.getServiceType()));
        }

        services.add(service);
    }

    @Override
    public String toString() {
        return "LoginRolesVO [EmailID=" + emailId + ", FirstName=" + firstName + ", LastName=" + lastName
                + ", CreationDate=" + creationDate + "]";
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
