package com.intuit.quickfabric.schedulers.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.ClusterStatus;
import com.intuit.quickfabric.commons.vo.ClusterType;
import com.intuit.quickfabric.commons.vo.EMRClusterMetricsVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EMRClusterFetchMetricsMapper implements ResultSetExtractor<List<EMRClusterMetricsVO>> {


	public List<EMRClusterMetricsVO> extractData(ResultSet rs) throws SQLException,
	DataAccessException {

		List<EMRClusterMetricsVO> emrClusterMetricsList = new ArrayList<EMRClusterMetricsVO>();
		EMRClusterMetricsVO vo = null;
		while (rs.next()) { 
			vo = new EMRClusterMetricsVO();
			vo.setEmrId(rs.getString("cluster_id"));
			vo.setRmUrl(rs.getString("rm_url"));
			vo.setClusterCreateTimestamp(rs.getTimestamp("creation_request_timestamp").toString());
			vo.setClusterSegment(rs.getString("segment"));;
			vo.setEmrStatus(ClusterStatus.valueOf((rs.getString("status"))));
			vo.setEmrName(rs.getString("cluster_name"));
			vo.setAccount(rs.getString("account"));
			vo.setCreatedBy(rs.getString("created_by"));
			vo.setType(ClusterType.valueOf(rs.getString("type").toUpperCase()));

			emrClusterMetricsList.add(vo);
		}

		return emrClusterMetricsList;
	}

}
