package com.intuit.quickfabric.commons.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.domain.UserAccess;
import com.intuit.quickfabric.commons.vo.ServiceType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserAccessMapper implements ResultSetExtractor<List<UserAccess>> {

    @Override
    public List<UserAccess> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<UserAccess> accessList = new ArrayList<>();

        while (resultSet.next()) {
            UserAccess userAccess = new UserAccess();
            userAccess.setFirstName(resultSet.getString("first_name"));
            userAccess.setLastName(resultSet.getString("last_name"));
            userAccess.setServiceType(ServiceType.valueOf(resultSet.getString("service_type").toUpperCase()));
            userAccess.setAwsAccountName(resultSet.getString("aws_account_id"));
            userAccess.setAccountEnv(resultSet.getString("account_env"));
            userAccess.setSegmentName(resultSet.getString("segment_name"));
            userAccess.setRoleName(resultSet.getString("role_name"));
            userAccess.setRoleId(resultSet.getInt("role_id"));
            userAccess.setServiceTypeId(resultSet.getInt("service_id"));
            userAccess.setSegmentId(resultSet.getInt("segment_id"));
            userAccess.setAwsAccountProfileId(resultSet.getInt("aws_account_profile_id"));
            userAccess.setUserAccountSegmentRoleMappingId(resultSet.getInt("user_account_segment_role_mapping_id"));

            accessList.add(userAccess);
        }

        return accessList;
    }
}
