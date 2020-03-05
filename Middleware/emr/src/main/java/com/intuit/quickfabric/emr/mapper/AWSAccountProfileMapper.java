package com.intuit.quickfabric.emr.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.AwsAccountProfile;


public class AWSAccountProfileMapper implements ResultSetExtractor<List<AwsAccountProfile>> {
    
    public List<AwsAccountProfile> extractData(ResultSet rs) throws SQLException, DataAccessException {
        
        List<AwsAccountProfile> lst = new ArrayList<>();
        
        while(rs.next()) {
            AwsAccountProfile account = new AwsAccountProfile();
            account.setAccountId(rs.getString("account_id"));
            account.setAccountOwner(rs.getString("account_owner"));
            account.setAccountEnv(rs.getString("account_env"));
            lst.add(account);
        }
        
        return lst;
        
        
    }
}
