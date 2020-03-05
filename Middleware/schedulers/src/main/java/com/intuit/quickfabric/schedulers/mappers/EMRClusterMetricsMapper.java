package com.intuit.quickfabric.schedulers.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.ClusterStatus;
import com.intuit.quickfabric.commons.vo.EMRClusterMetricsVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EMRClusterMetricsMapper implements ResultSetExtractor<List<EMRClusterMetricsVO>> {

	 
	public List<EMRClusterMetricsVO> extractData(ResultSet rs) throws SQLException,
		DataAccessException {

		List<EMRClusterMetricsVO> eMRClusterMetricsList = new ArrayList<EMRClusterMetricsVO>();
		EMRClusterMetricsVO vo = null;
		while (rs.next()) { 
			vo = new EMRClusterMetricsVO();
			vo.setEmrId(rs.getString("emr_id"));
			vo.setRmUrl(rs.getString("rm_url"));
			vo.setRefreshTimestamp(rs.getTimestamp("refresh_timestamp").toString());
			vo.setClusterCreateTimestamp(rs.getTimestamp("cluster_create_timestamp").toString());
			vo.setMetricsJson(rs.getString("metrics_json"));
			vo.setEmrStatus(ClusterStatus.valueOf((rs.getString("emr_status"))));
			vo.setCost(rs.getFloat("emr_cost"));
			vo.setEmrName(rs.getString("emr_name"));
			vo.setCoresUsagePct(rs.getFloat("cores_usage_pct"));
			vo.setMemoryUsagePct(rs.getFloat("memory_usage_pct"));
			vo.setActiveNodes(rs.getInt("total_nodes"));
			vo.setAppsPending(rs.getInt("apps_pending"));
			vo.setAppsRunning(rs.getInt("apps_running"));
			vo.setAppsSucceeded(rs.getInt("apps_succeeded"));
			vo.setAppsFailed(rs.getInt("apps_failed"));
			vo.setContainersPending(rs.getInt("containers_pending"));
			vo.setAccount(rs.getString("account"));
			
            
			eMRClusterMetricsList.add(vo);
		}

		return eMRClusterMetricsList;
	}
	
	

}
