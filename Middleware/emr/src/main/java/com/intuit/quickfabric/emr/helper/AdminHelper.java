package com.intuit.quickfabric.emr.helper;

import com.intuit.quickfabric.commons.dao.LoginRolesDao;
import com.intuit.quickfabric.commons.domain.Role;
import com.intuit.quickfabric.commons.domain.UserAccess;
import com.intuit.quickfabric.commons.exceptions.QuickFabricClientException;
import com.intuit.quickfabric.commons.exceptions.QuickFabricServerException;
import com.intuit.quickfabric.commons.helper.ConfigHelper;
import com.intuit.quickfabric.commons.helper.LoginRolesHelper;
import com.intuit.quickfabric.commons.model.LoginRolesModel;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.emr.dao.AdminDao;
import com.intuit.quickfabric.emr.dao.EMRClusterHealthCheckDao;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class AdminHelper {

    private final Logger logger = LogManager.getLogger(AdminHelper.class);

    @Autowired
    AdminDao adminDao;

    @Autowired
    LoginRolesDao loginRolesDao;

    @Autowired
    LoginRolesHelper loginRolesHelper;

    @Autowired
    EMRClusterHealthCheckDao emrClusterHealthCheckDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    ConfigHelper configHelper;

    public LoginRolesModel addRoles(ChangeRoleModel changeRoleModel) {
        logger.info("add roles request:" + changeRoleModel);
        LoginRolesVO user = loginRolesDao.getUserByEmail(changeRoleModel.getEmail());
        Map<String, Integer> segmentMap = adminDao.getSegmentsByName();
        Map<String, Integer> serviceMap = adminDao.getServicesByName();
        Map<String, Integer> accountMap = adminDao.getAwsAccountsByName();
        List<Role> roles = adminDao.getRoles();

        validateRequest(changeRoleModel, serviceMap, accountMap, segmentMap, roles);
        addUserAndRoles(changeRoleModel, user, serviceMap, accountMap, segmentMap, roles);

        return loginRolesHelper.getLoginRolesModel(changeRoleModel.getEmail());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public LoginRolesModel removeRoles(ChangeRoleModel changeRoleModel) {
        logger.info("remove roles request:" + changeRoleModel);
        LoginRolesVO user = loginRolesDao.getUserByEmail(changeRoleModel.getEmail());
        if (user == null || user.getUserId() <= 0) {
            throw new QuickFabricClientException("Cannot find user by email:" + changeRoleModel.getEmail());
        }

        List<UserAccess> existingRoles = loginRolesDao.getUserAccessList(changeRoleModel.getEmail());
        for (ChangeRoleVO role : changeRoleModel.getRoles()) {
            UserAccess existingRole = existingRoles.stream()
                    .filter(x -> x.getRoleName().equalsIgnoreCase(role.getRoleName())
                            && x.getAwsAccountName().equalsIgnoreCase(role.getAwsAccountName())
                            && x.getSegmentName().equalsIgnoreCase(role.getSegmentName())
                            && x.getServiceType() == ServiceType.valueOf(role.getServiceName().toUpperCase()))
                    .findFirst()
                    .orElse(null);

            if (existingRole != null) {
                logger.info("removeRoles - removing role:" + existingRole);
                adminDao.removeRole(existingRole.getUserAccountSegmentRoleMappingId());
            } else {
                logger.info("removeRoles - role not found associated to user. role:" + role);
            }
        }
        return loginRolesHelper.getLoginRolesModel(changeRoleModel.getEmail());
    }

    private void validateRequest(ChangeRoleModel changeRoleModel,
                                 Map<String, Integer> serviceMap,
                                 Map<String, Integer> accountMap,
                                 Map<String, Integer> segmentMap, List<Role> roles) {
        for (ChangeRoleVO role : changeRoleModel.getRoles()) {
            Integer segmentId = segmentMap.get(role.getSegmentName());
            Integer awsAccountId = accountMap.get(role.getAwsAccountName());
            Integer serviceId = serviceMap.get(role.getServiceName());

            if (serviceId == null) {
                throw new QuickFabricServerException("Service not found. Service name:" + role.getServiceName());
            }

            if (segmentId == null) {
                throw new QuickFabricServerException("Segment not found. Segment name:" + role.getSegmentName());
            }

            if (awsAccountId == null) {
                throw new QuickFabricServerException("Aws account not found. Account name:" + role.getAwsAccountName());
            }

            Integer roleId = roles.stream().filter(x -> x.getRoleName().equalsIgnoreCase(role.getRoleName())
                    && x.getServiceId() == serviceId).map(z -> z.getRoleId()).findFirst().orElse(null);

            if (roleId == null) {
                throw new QuickFabricServerException("Role not found. Role name:" + role.getRoleName() + " for Service:" + role.getServiceName());
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    void addUserAndRoles(ChangeRoleModel changeRoleModel,
                         LoginRolesVO user,
                         Map<String, Integer> serviceMap,
                         Map<String, Integer> accountMap,
                         Map<String, Integer> segmentMap,
                         List<Role> roles) {

        if (user == null || user.getUserId() <= 0) { // add option for email domain requirement?
            if (StringUtils.isEmpty(changeRoleModel.getFirstName())
                    || StringUtils.isEmpty(changeRoleModel.getLastName())
                    || StringUtils.isEmpty(changeRoleModel.getEmail())) {
                throw new QuickFabricClientException("Cannot create user. user details are missing or invalid.");
            }

            String randomString = RandomStringUtils.randomAscii(12);
            String password = passwordEncoder.encode(randomString);
            user = adminDao.createUser(changeRoleModel.getFirstName().trim(), changeRoleModel.getLastName().trim(), changeRoleModel.getEmail().trim(), password);
        }

        int userId = user.getUserId();
        List<UserAccountSegmentMapping> userAccountSegmentMappings = adminDao.getUserAccountSegmentMappings(userId);
        List<UserAccess> userAccesses = loginRolesDao.getUserAccessList(changeRoleModel.getEmail());

        for (ChangeRoleVO role : changeRoleModel.getRoles()) {
            boolean hasRole = hasRole(role, userAccesses);
            if (hasRole) {
                logger.info("addRoles - User:" + changeRoleModel.getEmail() + " already has access for:" + role);
            } else {
                logger.info("addRoles - User:" + changeRoleModel.getEmail() + " adding role:" + role);

                int segmentId = segmentMap.get(role.getSegmentName());
                int awsAccountId = accountMap.get(role.getAwsAccountName());
                int serviceId = serviceMap.get(role.getServiceName());
                int roleId = roles.stream().filter(x -> x.getRoleName().equalsIgnoreCase(role.getRoleName())
                        && x.getServiceId() == serviceId).map(z -> z.getRoleId()).findFirst().get();

                // check if user has associated account and segment
                UserAccountSegmentMapping userAccountSegmentMapping = userAccountSegmentMappings.stream()
                        .filter(x -> x.getUserId() == userId
                                && x.getAwsAccountId() == awsAccountId
                                && x.getSegmentId() == segmentId)
                        .findFirst().orElse(null);

                if (userAccountSegmentMapping == null) {
                    logger.info("added user accoun segment mapping - userId:" + userId + " awsAccountId:" + awsAccountId
                            + " segmentId" + segmentId);
                    userAccountSegmentMapping = adminDao.addAccountAndSegmentToUser(userId, awsAccountId, segmentId);
                    userAccountSegmentMappings.add(userAccountSegmentMapping);
                }

                adminDao.AddRole(userAccountSegmentMapping.getMappingId(), roleId);

                UserAccess newRole = new UserAccess();
                newRole.setAwsAccountName(role.getAwsAccountName());
                newRole.setSegmentName(role.getSegmentName());
                newRole.setRoleName(role.getRoleName());
                newRole.setServiceType(ServiceType.valueOf(role.getServiceName().toUpperCase()));

                userAccesses.add(newRole);
                logger.info("added new role: " + newRole);
            }
        }
    }

    private boolean hasRole(ChangeRoleVO role, List<UserAccess> existingRoles) {

        if (existingRoles.size() == 0) {
            return false;
        }

        return existingRoles.stream()
                .anyMatch(x -> x.getRoleName().equalsIgnoreCase(role.getRoleName())
                        && x.getAwsAccountName().equalsIgnoreCase(role.getAwsAccountName())
                        && x.getSegmentName().equalsIgnoreCase(role.getSegmentName())
                        && x.getServiceType() == ServiceType.valueOf(role.getServiceName().toUpperCase()));
    }

    public void resetPassword(String email, String newPassword) {
        LoginRolesVO user = loginRolesDao.getUserByEmail(email);
        if (user == null || user.getUserId() == 0) {
            logger.info("invalid user or email:" + email);
            throw new QuickFabricClientException("Invalid email.");
        }

        logger.info("changing password for email:" + email);
        String newEncryptedPassword = passwordEncoder.encode(newPassword);
        loginRolesDao.resetPassword(user.getUserId(), newEncryptedPassword);
    }

    @Transactional
    public void accountSetup(AccountSetupModel accountSetupModel) {
        logger.info(" in AdminHelper -> accountSetup");

        //doing work for accountDetails object 
        if (adminDao.getAwsAccountsByName().containsKey(accountSetupModel.getAccountDetails().get(0).getAccountId())) { //checking if this requested account is not already available in DB
            logger.info("updating this account : " + accountSetupModel.getAccountDetails().get(0).getAccountId() + "as its already available in DB ");
            adminDao.updateAccountDefinition(accountSetupModel.getAccountDetails().get(0));
        } else {
            logger.info("calling addNewAccountDefinition method for : " + accountSetupModel.getAccountDetails().get(0).getAccountId());
            adminDao.addNewAccountDefinition(accountSetupModel.getAccountDetails());
        }

        //doing work for segmentDetails object
        if (accountSetupModel.getSegmentDetails().size() > 0) {

            //checking if this requested segment is not already available in DB
            for (SegmentVO segment : accountSetupModel.getSegmentDetails()) {

                //checking if this requested segment is not already available in DB
                if (adminDao.getSegmentsByName().containsKey(segment.getSegmentName())) {
                    logger.info("updating this segment : {} , as its already available in DB", segment);
                    adminDao.updateSegment(segment);
                } else {
                    logger.info("accountSetup -> calling addNewSegment method");
                    adminDao.addNewSegment(segment);
                }
            }

        } else {
            logger.info("Error! as segment Details are not sent from client the account : {} ", accountSetupModel.getAccountDetails().get(0).getAccountId());
            throw new QuickFabricClientException("Error! as segment Details are not sent from the client for account :" + accountSetupModel.getAccountDetails().get(0).getAccountId());
        }

        //doing work for testSuites object	
        if (accountSetupModel.getTestSuitesDetails().size() > 0) {
            List<ClusterTestSuitesDefinitionVO> testSuitesDetails = accountSetupModel.getTestSuitesDetails();
            for (ClusterTestSuitesDefinitionVO testSuitesDefinition : testSuitesDetails)
                for (SegmentVO segment : accountSetupModel.getSegmentDetails())
                    // adding all segments values in testSuite definition tables.
                    for (ClusterType clusterType : ClusterType.values()) {
                        // updating testSuite definition table for all cluster types (i.e. exploratory,schedule,transient etc)
                        EMRClusterHealthTestCase existingTestCase = emrClusterHealthCheckDao.getEMRClusterTestSuitesForValidation(
                                segment.getSegmentName(),
                                clusterType.getValue(),
                                testSuitesDefinition.getName());

                        if (existingTestCase == null) {
                            logger.info("accountSetup -> calling updateEMRClusterTestSuitesDefinition() method");
                            testSuitesDefinition.setClusterSegment(segment.getSegmentName());
                            testSuitesDefinition.setClusterType(clusterType);
                            emrClusterHealthCheckDao.addEMRClusterTestSuitesDefinition(testSuitesDefinition);
                        } else {

                            logger.info("Updating test definition for cluster type: " + clusterType.getValue() +
                                    " and cluster role: " + segment.getSegmentName() + " test name:" + testSuitesDefinition.getName() +
                                    " in DB ");
                            testSuitesDefinition.setId(existingTestCase.getId());
                            emrClusterHealthCheckDao.updateEMRClusterTestSuitesDefinition(testSuitesDefinition);
                        }
                    }

            //updating "testsuites_enabled" config as "true" into account_config table
            ConfigVO configVO = new ConfigVO();
            configVO.setAccountId(accountSetupModel.getAccountDetails().get(0).getAccountId());
            configVO.setConfigName("testsuites_enabled");
            configVO.setConfigValue("true");
            configHelper.addConfig(configVO);
        } else {
            logger.info("skipping ! as testSuites Details are not sent from client for the account : {} ", accountSetupModel.getAccountDetails().get(0).getAccountId());
        }

        //doing work for user Object
        if (accountSetupModel.getUserDetails().size() > 0) {
            for (ChangeRoleModel userDetails : accountSetupModel.getUserDetails()) {
                logger.info("AdminHelper -> calling method to addUser and addRoles for user {} :", userDetails.getEmail());
                addRoles(userDetails);
            }
        } else {
            logger.info("skipping ! as user Details are not sent from client for the account : {} ", accountSetupModel.getAccountDetails().get(0).getAccountId());
        }

        //doing work for config Object
        if (accountSetupModel.getConfigDetails().size() > 0) {
            for (ConfigVO configVO : accountSetupModel.getConfigDetails())
                configHelper.addConfig(configVO);
        } else {
            logger.info("Error ! as Config Details are not sent from client for the account : {} ", accountSetupModel.getAccountDetails().get(0).getAccountId());
            throw new QuickFabricClientException("Error ! as Config Details are not sent from client for the account :" + accountSetupModel.getAccountDetails().get(0).getAccountId());
        }

        //  clearing default accounts
        logger.info("clearing default accounts");
        adminDao.clearDefaultAccounts();
    }
}
