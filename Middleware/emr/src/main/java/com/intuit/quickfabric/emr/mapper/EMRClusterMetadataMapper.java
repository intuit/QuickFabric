package com.intuit.quickfabric.emr.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.ClusterStatus;
import com.intuit.quickfabric.commons.vo.ClusterType;
import com.intuit.quickfabric.commons.vo.ClusterVO;

public class EMRClusterMetadataMapper implements ResultSetExtractor<List<ClusterVO>> {

    public List<ClusterVO> extractData(ResultSet rs) throws SQLException, DataAccessException {

        List<ClusterVO> emrClusterMetadataList = new ArrayList<ClusterVO>();
        ClusterVO vo = null;
        while (rs.next()) {
            vo = new ClusterVO();

            ClusterStatus status = ClusterStatus.valueOf((rs.getString("status")));
            switch(status) {
                case WAITING:
                    vo.setClusterStatus(ClusterStatus.HEALTHY);
                    break;
                default:
                    vo.setClusterStatus(ClusterStatus.valueOf((rs.getString("status"))));
            }
            
            vo.setClusterName(rs.getString("cluster_name"));
            vo.setClusterId(rs.getString("cluster_id"));
            vo.setCreatedBy(rs.getString("created_by"));
            vo.setAccount(rs.getString("account"));
            vo.setCreationTimestamp(rs.getTimestamp("creation_request_timestamp").toString());
            vo.setDnsName(rs.getString("dns_name"));
            vo.setSegment(rs.getString("segment"));
            vo.setType(ClusterType.valueOf(rs.getString("type").toUpperCase()));
            vo.setMetadataId(rs.getLong("metadata_id"));
            vo.setAMIRotationDaysTogo(rs.getInt("ami_rotation_days_togo"));
            vo.setAmiRotationSlaDays(rs.getInt("ami_rotation_sla_days"));
            vo.setAutoAmiRotation(rs.getBoolean("auto_ami_rotation"));
            vo.setAutopilotWindowStart(rs.getInt("autopilot_window_start"));
            vo.setAutopilotWindowEnd(rs.getInt("autopilot_window_end"));
            vo.setDoTerminate(rs.getBoolean("do_terminate"));


            //Changing the response for AMI rotation days to go based on the values and cluster status
            if(vo.getAMIRotationDaysToGo() < 0) {
                vo.setRotationDaysToGo("Days Overdue: " + vo.getAMIRotationDaysToGo());
            } else if(vo.getClusterStatus() == ClusterStatus.TERMINATED ||
                    vo.getClusterStatus() == ClusterStatus.FAILED || vo.getClusterStatus() == ClusterStatus.TERMINATED_WITH_ERRORS ||
                    vo.getClusterStatus() == ClusterStatus.TERMINATING || vo.getClusterStatus() == ClusterStatus.TERMINATION_INITIATED ||
                    vo.getClusterStatus() == ClusterStatus.INITIATED || vo.getClusterStatus() == ClusterStatus.BOOTSTRAPPING) {
                vo.setRotationDaysToGo("Not Applicable");
            } else {
                vo.setRotationDaysToGo("Days Left: " + Math.abs(vo.getAMIRotationDaysToGo()));
            }

            // best not to over think this - ultimately doesn't matter which gets set here because
            // it will always serialize to "request_ticket" in the JSON.
            String requestTicket = rs.getString("request_ticket");
            if(requestTicket != null && requestTicket.contains("SBGDATA")) {
                vo.setJiraTicket(requestTicket);
            } else {
                vo.setSnowTicket(requestTicket);
            }

            emrClusterMetadataList.add(vo);
        }
        return emrClusterMetadataList;
    }
}