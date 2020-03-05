package com.intuit.quickfabric.schedulers.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.ClusterStatus;
import com.intuit.quickfabric.commons.vo.ClusterType;
import com.intuit.quickfabric.commons.vo.ClusterVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EMRClusterMetricsAMIReportMapper implements ResultSetExtractor<List<ClusterVO>> {
	@Override
    public List<ClusterVO> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<ClusterVO> clusters = new ArrayList<>();

        while (rs.next())
        {
            ClusterVO cluster = new ClusterVO();
            cluster.setCreationTimestamp(rs.getTimestamp("creation_request_timestamp").toString());
            cluster.setClusterId(rs.getString("cluster_id"));
            cluster.setClusterName(rs.getString("cluster_name"));
            cluster.setType(ClusterType.valueOf(rs.getString("type").toUpperCase()));
            cluster.setClusterStatus(ClusterStatus.valueOf((rs.getString("status"))));
            cluster.setCreatedBy(rs.getString("created_by"));
            cluster.setAccount(rs.getString("account"));
            cluster.setSegment(rs.getString("segment"));
            cluster.setAMIRotationDaysTogo(rs.getInt("ami_rotation_days_togo"));

            clusters.add(cluster);
        }
        return clusters;
	}
}
