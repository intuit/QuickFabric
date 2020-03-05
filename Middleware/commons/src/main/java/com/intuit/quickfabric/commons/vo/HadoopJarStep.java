package com.intuit.quickfabric.commons.vo;

import java.util.ArrayList;
import java.util.List;

public class HadoopJarStep {
	
	private String mainClass="";
	
	private String jar;
	
	private List<String> stepArgs = new ArrayList<String>();
	
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
	
	public List<String> getStepArgs() {
		return stepArgs;
	}
	
	public void setStepArgs(List<String> stepArgs) {
		this.stepArgs = stepArgs;
	}
	
	
}
