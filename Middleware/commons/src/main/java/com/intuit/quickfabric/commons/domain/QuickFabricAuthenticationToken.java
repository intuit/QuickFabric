package com.intuit.quickfabric.commons.domain;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.List;

public class QuickFabricAuthenticationToken extends UsernamePasswordAuthenticationToken implements Authentication {

    private String email;
    private String firstName;
    private String lastName;
    private boolean superAdmin;

    public QuickFabricAuthenticationToken(String email, String firstName, String lastName, boolean superAdmin, List<SimpleGrantedAuthority> grantedAuthorityList) {
        super(email, null, grantedAuthorityList);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.superAdmin = superAdmin;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isSuperAdmin() {
        return superAdmin;
    }

    public String getEmail() {
        return email;
    }
}
