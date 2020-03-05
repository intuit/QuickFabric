package com.intuit.quickfabric.emr.model;

import java.util.List;

import org.springframework.stereotype.Component;

import com.intuit.quickfabric.commons.vo.EMRClusterMetricsVO;

@Component
public class EMRClusterMetricsModel {

	private List<EMRClusterMetricsVO> emrClusterMetricsReport;

	public List<EMRClusterMetricsVO> getEmrClusterMetricsReport() {
		return emrClusterMetricsReport;
	}

	public void setEmrClusterMetricsReport(List<EMRClusterMetricsVO> emrClusterMetricsReport) {
		this.emrClusterMetricsReport = emrClusterMetricsReport;
	}
}
