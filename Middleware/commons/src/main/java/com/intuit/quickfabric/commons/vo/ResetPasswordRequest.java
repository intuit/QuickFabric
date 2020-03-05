package com.intuit.quickfabric.commons.vo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotNull
    @Size(min = 1)
    @Email
    private String email;

    @NotBlank
    @Size(min = 5)
    private String newPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword.trim();
    }
}
