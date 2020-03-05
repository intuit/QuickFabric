package com.intuit.quickfabric.schedulers.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.ClusterVO;


public class AddUserRequestMapper implements ResultSetExtractor<ClusterVO> {

    @Override
    public ClusterVO extractData(ResultSet rs) throws SQLException, DataAccessException {
        if(rs.next()) {
            ClusterVO clusterMetadata = new ClusterVO();
            clusterMetadata.setClusterId(rs.getString("cluster_id"));
            clusterMetadata.setClusterName(rs.getString("cluster_name"));
            clusterMetadata.setAccount(rs.getString("account"));
            
            return clusterMetadata;
        } else {
            return null;
        }
        
    }

}
