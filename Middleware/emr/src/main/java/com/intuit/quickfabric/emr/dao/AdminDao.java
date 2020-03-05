package com.intuit.quickfabric.emr.dao;

import com.intuit.quickfabric.commons.domain.Role;
import com.intuit.quickfabric.commons.vo.AwsAccountProfile;
import com.intuit.quickfabric.commons.vo.LoginRolesVO;
import com.intuit.quickfabric.commons.vo.SegmentVO;
import com.intuit.quickfabric.commons.vo.UserAccountSegmentMapping;

import java.util.List;
import java.util.Map;

public interface AdminDao {
    List<UserAccountSegmentMapping> getUserAccountSegmentMappings(int userId);

    UserAccountSegmentMapping addAccountAndSegmentToUser(int userId, Integer awsAccountId, Integer segmentId);

    int AddRole(int userAccountSegmentMappingId, int roleId);

    Map<String, Integer> getSegmentsByName();

    Map<String, Integer> getServicesByName();

    List<Role> getRoles();

    Map<String, Integer> getAwsAccountsByName();

    void removeRole(int userAccountSegmentRoleMappingId);

    LoginRolesVO createUser(String firstName, String lastName, String email, String password);
    
    void addNewAccountDefinition(List<AwsAccountProfile> accountDetails)  ;
    
    void addNewSegment(SegmentVO segmentDetails) ;

    void updateSegment(SegmentVO segmentDetails);

    void clearDefaultAccounts();

    void updateAccountDefinition(AwsAccountProfile accountDetails);
}
