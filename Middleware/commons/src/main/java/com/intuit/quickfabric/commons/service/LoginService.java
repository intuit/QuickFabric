package com.intuit.quickfabric.commons.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.quickfabric.commons.constants.ApiUrls;
import com.intuit.quickfabric.commons.exceptions.QuickFabricUnauthenticatedException;
import com.intuit.quickfabric.commons.helper.ConfigHelper;
import com.intuit.quickfabric.commons.helper.LoginRolesHelper;
import com.intuit.quickfabric.commons.model.LoginRolesModel;
import com.intuit.quickfabric.commons.security.JWTTokenProvider;
import com.intuit.quickfabric.commons.utils.CommonUtils;
import com.intuit.quickfabric.commons.vo.LoginRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@RestController
public class LoginService {

    private static final Logger logger = LogManager.getLogger(LoginService.class);

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LoginRolesHelper loginRolesHelper;


    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    ConfigHelper configHelper;

    /** Get the roles (permissions) of the given user
     * @param email email address for the user to get roles for
     * @return the user's roles
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PreAuthorize("hasAnyAuthority('superadmin')")
    @GetMapping(value = "/user/roles/{email}")
    public ResponseEntity<LoginRolesModel> getUserRoles(@PathVariable("email") String email) {
        try {
            logger.info("LoginService API->getUserRoles() for user:" + email);
            LoginRolesModel loginRolesModel = loginRolesHelper.getLoginRolesModel(email);
            return ResponseEntity.ok(loginRolesModel);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return CommonUtils.createErrorResponse(e);
        }
    }

    /** Generate JWT token for the user
     * @param loginRequest username and password
     * @return JWT token and user information
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value = ApiUrls.LOGIN_SERVICE_PATH, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<LoginRolesModel> generateToken(@RequestBody(required = false) LoginRequest loginRequest) {
        try {
            logger.info("LoginService API->generateToken()");
            LoginRolesModel loginRolesModel = loginRolesHelper.getLoginRolesModel(loginRequest);
            return ResponseEntity.ok(loginRolesModel);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return CommonUtils.createErrorResponse(e);
        }
    }

    /** this Service does browser redirects to SSO url */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value = ApiUrls.SSO_REDIRECT_PATH, method = RequestMethod.GET)
    public void doLoginSSORedirect(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        try {
            logger.info("LoginService -> doLoginSSORedirect ");
            response.sendRedirect(configHelper.getConfigValue("sso_url"));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /** this Service is call back method from SSO after SSO authentication succeeds 
     * @return JWT token and user information
     * @throws IOException 
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value = ApiUrls.GET_USER_ROLES_SERVICE_PATH, method = RequestMethod.POST)
    public void getLoginRoleSSO(@Context HttpServletRequest request,
                                @Context HttpServletResponse response) throws IOException {
        try {
             loginRolesHelper.createSSOResponse(request,response);
             //redirecting to UI home page (ui will check if jwtToken is available in cookie to redirect to Home page"
             response.sendRedirect(configHelper.getConfigValue("sso_redirect_url"));
        } catch (QuickFabricUnauthenticatedException e) {
            logger.error("SSO unauthorized exception , please try again", e);
            //redirecting to UI login page 
            response.sendRedirect(configHelper.getConfigValue("sso_redirect_url"));
        } catch (Exception e) {
            logger.error("SSO unauthorized exception , please try again!", e);
        }
    }

    
}
