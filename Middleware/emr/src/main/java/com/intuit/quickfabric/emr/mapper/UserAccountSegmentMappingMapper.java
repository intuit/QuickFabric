package com.intuit.quickfabric.emr.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.UserAccountSegmentMapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserAccountSegmentMappingMapper implements ResultSetExtractor<List<UserAccountSegmentMapping>> {

    @Override
    public List<UserAccountSegmentMapping> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<UserAccountSegmentMapping> userAccountSegmentMappings = new ArrayList<>();
        while (resultSet.next()) {
            UserAccountSegmentMapping map = new UserAccountSegmentMapping();
            map.setMappingId(resultSet.getInt("user_account_segment_mapping_id"));
            map.setUserId(resultSet.getInt("user_id"));
            map.setAwsAccountId(resultSet.getInt("aws_account_id"));
            map.setSegmentId(resultSet.getInt("segment_id"));

            userAccountSegmentMappings.add(map);
        }

        return userAccountSegmentMappings;
    }
}
