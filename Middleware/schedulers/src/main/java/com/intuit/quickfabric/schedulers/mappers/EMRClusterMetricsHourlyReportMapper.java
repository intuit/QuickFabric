package com.intuit.quickfabric.schedulers.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.EMRClusterMetricsVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EMRClusterMetricsHourlyReportMapper implements ResultSetExtractor<List<EMRClusterMetricsVO>> {

	 
	public List<EMRClusterMetricsVO> extractData(ResultSet rs) throws SQLException,
		DataAccessException {

		List<EMRClusterMetricsVO> eMRClusterMetricsList = new ArrayList<EMRClusterMetricsVO>();
		EMRClusterMetricsVO vo = null;
		while (rs.next()) { 
			vo = new EMRClusterMetricsVO();
			
			vo.setCost(rs.getInt("emr_cost"));
			vo.setAccount(rs.getString("account"));
			vo.setEmrId(rs.getString("emr_id"));
			vo.setEmrName(rs.getString("emr_name"));
			vo.setCoresUsagePct(rs.getFloat("cores_usage_pct"));
			vo.setMemoryUsagePct(rs.getFloat("memory_usage_pct"));
			vo.setAppsRunning(rs.getInt("avg_apps_running"));
			vo.setAppsSucceeded(rs.getInt("total_apps_succeeded"));
			vo.setAppsFailed(rs.getInt("total_apps_failed"));
			vo.setClusterSegment(rs.getString("segment"));
			
			eMRClusterMetricsList.add(vo);
		}

		return eMRClusterMetricsList;
	}
	
	

}
