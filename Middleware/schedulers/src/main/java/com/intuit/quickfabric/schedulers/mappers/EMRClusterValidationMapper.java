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

public class EMRClusterValidationMapper implements ResultSetExtractor<List<ClusterVO>> {

	 
	public List<ClusterVO> extractData(ResultSet rs) throws SQLException,
		DataAccessException {

		List<ClusterVO> eMRClusterMetricsList = new ArrayList<ClusterVO>();
		ClusterVO vo = null;
		while (rs.next()) { 
			vo = new ClusterVO();
			vo.setAccount(rs.getString("account"));
			vo.setClusterName(rs.getString("cluster_name"));
			vo.setClusterId(rs.getString("cluster_id"));
			vo.setClusterStatus(ClusterStatus.valueOf(rs.getString("status")));
			vo.setType(ClusterType.valueOf(rs.getString("type").toUpperCase()));
			vo.setMetadataId(rs.getLong("metadata_id"));
			vo.setClusterDetails(rs.getString("cluster_details") );
			vo.setSegment(rs.getString("segment"));
			vo.setOriginalClusterId(rs.getString("original_cluster_id"));
			vo.setCreationTimestamp(rs.getTimestamp("creation_request_timestamp").toString());
			vo.setInstanceGroup(rs.getString("autoscaling_instance_group"));
			vo.setMin(rs.getInt("autoscaling_min"));
			vo.setMax(rs.getInt("autoscaling_max"));
			vo.setDnsName(rs.getString("dns_name"));
			
			eMRClusterMetricsList.add(vo);
		}

		return eMRClusterMetricsList;
	}
	
	

}
