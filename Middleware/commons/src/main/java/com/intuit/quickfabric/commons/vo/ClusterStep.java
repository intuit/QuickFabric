package com.intuit.quickfabric.commons.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.intuit.quickfabric.commons.vo.HadoopJarStep;
import com.intuit.quickfabric.commons.vo.StepStatus;
@JsonInclude(JsonInclude.Include.NON_EMPTY)

public class ClusterStep {

	private String stepId;
	private String apiRequestId;
	private String lambdaRequestId;
	private String name;
	private String actionOnFailure;
	private HadoopJarStep hadoopJarStep;
	private String createdBy;
	@JsonDeserialize(as = StepStatus.class)
	private StepStatus status;
	private String stepType;
	private String creationTimestamp;
	private String stepArg;
	private String mainClass;
	private String jar;
	
	public String getStepId() {
		return stepId;
	}
	
	public void setStepId(String stepId) {
		this.stepId = stepId;
	}
	
	public String getApiRequestId() {
		return apiRequestId;
	}
	
	public void setApiRequestId(String apiRequestId) {
		this.apiRequestId = apiRequestId;
	}
	
	public String getLambdaRequestId() {
		return lambdaRequestId;
	}
	public void setLambdaRequestId(String lambdaRequestId) {
		this.lambdaRequestId = lambdaRequestId;
	}
	
	@JsonDeserialize(as = StepStatus.class)
	public StepStatus getStatus() {
		
		return status;
	}
	
	@JsonDeserialize(as = StepStatus.class)
	public void setStatus(StepStatus status) {
		this.status = status;
	}
	
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
	
	public HadoopJarStep getHadoopJarStep() {
		return hadoopJarStep;
	}
	
	public void setHadoopJarStep(HadoopJarStep hadoopJarStep) {
		this.hadoopJarStep = hadoopJarStep;
	}
	
	public String getStepCreatedBy() {
		return createdBy;
	}
	
	public void setStepCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getStepType() {
		return stepType;
	}
	
	public void setStepType(String stepType) {
		this.stepType = stepType;
	}
	
	public String getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(String creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
	
    public String getStepArg() {
        return stepArg;
    }
    
    public void setStepArg(String stepArg) {
        this.stepArg = stepArg;
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
}