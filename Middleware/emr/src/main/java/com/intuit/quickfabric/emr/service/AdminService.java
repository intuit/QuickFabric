package com.intuit.quickfabric.emr.service;

import com.intuit.quickfabric.commons.model.LoginRolesModel;
import com.intuit.quickfabric.commons.utils.CommonUtils;
import com.intuit.quickfabric.commons.vo.AccountSetupModel;
import com.intuit.quickfabric.commons.vo.ChangeRoleModel;
import com.intuit.quickfabric.commons.vo.ResetPasswordRequest;
import com.intuit.quickfabric.emr.helper.AdminHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminService {
    private final Logger logger = LogManager.getLogger(AdminService.class);

    @Autowired
    private AdminHelper adminHelper;

    /**
     * Adds roles (permissions) to the given user
     * 
     * @param changeRoleModel user and role details
     * @return user details with new roles
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = "/roles/add")
    @PreAuthorize("hasAnyAuthority('superadmin, addroles')")
    public ResponseEntity<LoginRolesModel> addRoles(@Valid @RequestBody ChangeRoleModel changeRoleModel) {
        ResponseEntity<LoginRolesModel> result;
        try {
            logger.info("AdminService API -> addRoles");
            LoginRolesModel model = adminHelper.addRoles(changeRoleModel);
            result = ResponseEntity.ok(model);
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage(), e);
            result = CommonUtils.createErrorResponse(e);
        }
        return result;
    }

    /**
     * Removes roles (permissions) from the given user
     * 
     * @param changeRoleModel user and role details
     * @return
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = "/roles/remove")
    @PreAuthorize("hasAnyAuthority('superadmin, removeroles')")
    public ResponseEntity<LoginRolesModel> removeRoles(@Valid @RequestBody ChangeRoleModel changeRoleModel) {
        ResponseEntity<LoginRolesModel> result;
        try {
            logger.info("AdminService API -> removeRoles");
            LoginRolesModel model = adminHelper.removeRoles(changeRoleModel);
            result = ResponseEntity.ok(model);
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage(), e);
            result = CommonUtils.createErrorResponse(e);
        }
        return result;
    }

    /**
     * Changes the password for the given user to the one given
     * 
     * @param resetPassword user and new password details
     * @return OK or Error message
     */
    @PutMapping(value = "/reset-password")
    @PreAuthorize("hasAnyAuthority('superadmin')")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPassword) {
        ResponseEntity response;
        try {
            logger.info("AdminService API -> resetPassword");
             adminHelper.resetPassword(resetPassword.getEmail(), resetPassword.getNewPassword());
            response = ResponseEntity.ok("Successfully reset password");
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage(), e);
            response = CommonUtils.createErrorResponse(e);
        }

        return response;
    }
    
    /**
     * Onboards a new AWS account to QuickFabric. Includes associated segments, account-specific 
     * configuations, test suites, and users.
     * 
     * @param accountSetupModel the account information
     * @return
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = "/account-setup")
    @PreAuthorize("hasAnyAuthority('superadmin')")
    public ResponseEntity<String> accountSetup(@Valid @RequestBody AccountSetupModel accountSetupModel) {
        ResponseEntity response;
        try {
            logger.info("AdminService API -> accountSetup");
            adminHelper.accountSetup(accountSetupModel);
            response = ResponseEntity.ok("Account setup Successfully completed for account : "+ accountSetupModel.getAccountDetails().get(0).getAccountId());
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage(), e);
            response = CommonUtils.createErrorResponse(e);
        }
        return response;
    }
}
