package com.intuit.quickfabric.commons.vo;

import javax.validation.constraints.*;
import java.util.List;

public class ChangeRoleModel {

    @NotEmpty
    private List<ChangeRoleVO> roles;

    @NotNull
    @Size(min = 1)
    @Email
    private String email;
    private String firstName;
    private String lastName;

    public List<ChangeRoleVO> getRoles() {
        return roles;
    }

    public void setRoles(List<ChangeRoleVO> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    @Override
    public String toString() {
        return "ChangeRoleModel{" +
                "roles=" + roles +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
