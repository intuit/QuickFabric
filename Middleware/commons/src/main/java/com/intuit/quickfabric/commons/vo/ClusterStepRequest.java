package com.intuit.quickfabric.commons.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClusterStepRequest {
	

	private String name;
	private String actionOnFailure;
	private String mainClass="";
	private String jar;
	private String args;
	@JsonProperty("stepCreatedBy")
	private String createdBy;
	private String stepId;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getActionOnFailure() {
		return actionOnFailure;
	}
	
	public void setActionOnFailure(String actionOnFailure) {
		this.actionOnFailure = actionOnFailure;
	}
	

	public String getMainClass() {
		return mainClass;
	}
	
	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}
	
	public String getJar() {
		return jar;
	}
    
	public void setJar(String jar) {
		this.jar = jar;
	}
	
	public String getArgs() {
		return args;
	}
    
	public void setArgs(String args) {
		this.args = args;
	}
	
	public String getStepId() {
		return stepId;
	}
	
	public void setStepId(String stepId) {
		this.stepId = stepId;
	}
	
	public String getStepCreatedBy() {
		return createdBy;
	}
	
	public void setStepCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
}
