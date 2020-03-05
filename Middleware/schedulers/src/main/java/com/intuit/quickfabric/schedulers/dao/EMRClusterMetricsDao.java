package com.intuit.quickfabric.schedulers.dao;

import com.intuit.quickfabric.commons.vo.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface EMRClusterMetricsDao {

	void updateEMRClusterMetricsList(List<EMRClusterMetricsVO> metrics);

	void updateEMRClusterMetricsCost(List<EMRClusterMetricsVO> costs);
	
	public List<String> getDistinctEMRBillingComponent();

	List<EMRClusterMetricsVO> getClusterMetricsReport(Timestamp from, Timestamp to, String segmentName);
	
}
