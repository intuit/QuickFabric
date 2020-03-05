package com.intuit.quickfabric.emr.model;

import java.util.List;

import org.springframework.stereotype.Component;

import com.intuit.quickfabric.commons.vo.ClusterVO;

@Component
public class EMRClusterMetadataModel {

	private List<ClusterVO> emrClusterMetadataReport;

	public List<ClusterVO> getEmrClusterMetadataReport() {
		return emrClusterMetadataReport;
	}

	public void setEmrClusterMetadataReport(List<ClusterVO> emrClusterMetadataReport) {
		this.emrClusterMetadataReport = emrClusterMetadataReport;
	}
}