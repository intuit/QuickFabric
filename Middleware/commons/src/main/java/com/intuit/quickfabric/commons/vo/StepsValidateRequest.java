package com.intuit.quickfabric.commons.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StepsValidateRequest {
	String clusterId;
	String clusterName;
	private List<String> stepIds= new ArrayList<String>();
	
	public StepsValidateRequest() {
		
	}
	
	public StepsValidateRequest(String clusterName, String clusterId, List<String> asList) {
		this.clusterId=clusterId;
		this.clusterName=clusterName;
		this.stepIds=asList;
	}
	public String getClusterId() {
		return clusterId;
	}
	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	public List<String> getStepIds() {
		return stepIds;
	}
	public void setStepIds(List<String> stepids) {
		this.stepIds = stepids;
	}
	

}
