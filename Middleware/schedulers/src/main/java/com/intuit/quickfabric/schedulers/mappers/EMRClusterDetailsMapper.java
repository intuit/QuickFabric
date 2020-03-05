package com.intuit.quickfabric.schedulers.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.ClusterRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EMRClusterDetailsMapper implements ResultSetExtractor<List<ClusterRequest>> {
    
    public List<ClusterRequest> extractData(ResultSet rs) throws SQLException, DataAccessException {

        List<ClusterRequest> reqList= new ArrayList<ClusterRequest>();

        while (rs.next()) { 
            ClusterRequest req= new ClusterRequest();
            req.setAccount(rs.getString("account"));
            req.setClusterId(rs.getString("cluster_id"));
            req.setClusterName(rs.getString("cluster_name"));
            req.setCreationTimestamp(rs.getTimestamp("creation_request_timestamp").toString());

            if(!reqList.contains(req)){
                reqList.add(req);
            }
        }

        return reqList;
    }
}
