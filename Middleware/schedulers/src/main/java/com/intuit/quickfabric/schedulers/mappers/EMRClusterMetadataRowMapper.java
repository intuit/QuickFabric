package com.intuit.quickfabric.schedulers.mappers;

import com.intuit.quickfabric.commons.vo.ClusterStatus;
import com.intuit.quickfabric.commons.vo.ClusterType;
import com.intuit.quickfabric.commons.vo.ClusterVO;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EMRClusterMetadataRowMapper implements ResultSetExtractor<List<ClusterVO>> {

    @Override
    public List<ClusterVO> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<ClusterVO> clusters = new ArrayList<>();

        while (rs.next()) {
            ClusterVO cluster = new ClusterVO();
            cluster.setMetadataId(rs.getLong("metadata_id"));
            cluster.setClusterId(rs.getString("cluster_id"));
            cluster.setClusterName(rs.getString("cluster_name"));
            cluster.setType(ClusterType.valueOf(rs.getString("type").toUpperCase()));
            cluster.setClusterStatus(ClusterStatus.valueOf((rs.getString("status"))));
            cluster.setMessage(rs.getString("message"));
            cluster.setStatusCode(rs.getString("status_code"));
            cluster.setDoTerminate(rs.getBoolean("do_terminate"));
            cluster.setClusterDetails(rs.getString("cluster_details"));
            cluster.setCreatedBy(rs.getString("created_by"));
            cluster.setLastUpdatedBy(rs.getString("last_updated_by"));
            cluster.setAccount(rs.getString("account"));
            cluster.setHeadlessUsers(rs.getString("headless_users"));
            cluster.setSegment(rs.getString("segment"));
            cluster.setDnsName(rs.getString("dns_name"));
            cluster.setDnsFlip(rs.getBoolean("dns_flip"));
            cluster.setDnsFlipCompleted(rs.getBoolean("dns_flip_completed"));
            cluster.setIsProd(rs.getBoolean("is_prod"));
            cluster.setOriginalClusterId(rs.getString("original_cluster_id"));
            cluster.setCreationTimestamp(rs.getTimestamp("creation_request_timestamp").toString());
            cluster.setMasterIp(rs.getString("master_ip"));
            cluster.setInstanceGroup(rs.getString("autoscaling_instance_group"));
            cluster.setMax(rs.getInt("autoscaling_max"));
            cluster.setMin(rs.getInt("autoscaling_min"));
            cluster.setAmiRotationSlaDays(rs.getInt("ami_rotation_sla_days"));
            cluster.setAutopilotWindowStart(rs.getInt("autopilot_window_start"));
            cluster.setAutopilotWindowEnd(rs.getInt("autopilot_window_end"));
            cluster.setAutoAmiRotation(rs.getBoolean("auto_ami_rotation"));
            cluster.setAMIRotationDaysTogo(rs.getInt("ami_rotation_days_togo"));
            cluster.setNewClusterId(rs.getString("new_cluster_id"));

            clusters.add(cluster);
        }
        return clusters;
    }
}
