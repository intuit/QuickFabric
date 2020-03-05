
package com.intuit.quickfabric.commons.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.stereotype.Component;

import com.intuit.quickfabric.commons.vo.LoginRolesVO;
import com.intuit.quickfabric.commons.vo.SSODetailsVO;

@Component
@XmlRootElement(name = "LoginRolesInfo")
public class LoginRolesModel {

    @XmlElement(required = false)
    private LoginRolesVO loginRoles;

    @XmlElement(required = false)
    private String jwtToken;

    @XmlElement(required = false)
    private
    SSODetailsVO ssoDetails;

    public LoginRolesVO getLoginRoles() {
        return loginRoles;
    }

    public void setLoginRoles(LoginRolesVO loginRoles) {
        this.loginRoles = loginRoles;
    }

    public SSODetailsVO getSsoDetails() {
        return ssoDetails;
    }

    public void setSsoDetails(SSODetailsVO ssoDetails) {
        this.ssoDetails = ssoDetails;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

}


