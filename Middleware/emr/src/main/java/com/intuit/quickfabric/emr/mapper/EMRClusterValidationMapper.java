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
        
			eMRClusterMetricsList.add(vo);
		}

		return eMRClusterMetricsList;
	}
	
	

}
