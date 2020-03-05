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

public class AMIRotationClusterMetadataMapper implements ResultSetExtractor<List<ClusterVO>> {

    public List<ClusterVO> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<ClusterVO> clusters = new ArrayList<ClusterVO>();
        ClusterVO vo = null;
        while (rs.next()) {
            vo = new ClusterVO();
            vo.setAccount(rs.getString("account"));
            vo.setClusterDetails(rs.getString("cluster_details"));
            vo.setClusterId(rs.getString("cluster_id"));
            vo.setClusterName(rs.getString("cluster_name"));

            vo.setCreatedBy(rs.getString("created_by"));
            vo.setDoTerminate(rs.getBoolean("do_terminate"));
            vo.setHeadlessUsers(rs.getString("headless_users"));
            vo.setLastUpdatedBy(rs.getString("last_updated_by"));
            vo.setMessage(rs.getString("message"));
            vo.setMetadataId(rs.getLong("metadata_id"));
            vo.setSegment(rs.getString("segment"));
            vo.setClusterStatus(ClusterStatus.valueOf((rs.getString("status"))));
            vo.setStatusCode(rs.getString("status_code"));

            // to match enum format of ClusterType
            String type_uppercase = rs.getString("type").toUpperCase();
            vo.setType(ClusterType.valueOf(type_uppercase));
            vo.setAutoAmiRotation(rs.getBoolean("auto_ami_rotation"));
            vo.setIsProd(rs.getBoolean("is_prod"));

            vo.setAutopilotWindowStart(rs.getInt("autopilot_window_start"));
            vo.setAutopilotWindowEnd(rs.getInt("autopilot_window_end"));
            vo.setInstanceGroup(rs.getString("autoscaling_instance_group"));
            vo.setMin(rs.getInt("autoscaling_min"));
            vo.setMax(rs.getInt("autoscaling_max"));

            clusters.add(vo);
        }
        return clusters;
    }
}