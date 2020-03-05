package com.intuit.quickfabric.commons.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AccountSetupModel {
	
	private List<AwsAccountProfile> accountDetails;
	private List<SegmentVO> segmentDetails;
	private List<ClusterTestSuitesDefinitionVO> testSuitesDetails;
	private List<ConfigVO> configDetails;
	private List<ChangeRoleModel> userDetails;
	
	
	public List<AwsAccountProfile> getAccountDetails() {
		return accountDetails;
	}
	public void setAccountDetails(List<AwsAccountProfile> accountDetails) {
		this.accountDetails = accountDetails;
	}
	public List<SegmentVO> getSegmentDetails() {
		return segmentDetails;
	}
	public void setSegmentDetails(List<SegmentVO> segmentDetails) {
		this.segmentDetails = segmentDetails;
	}
	
	public List<ClusterTestSuitesDefinitionVO> getTestSuitesDetails() {
		return testSuitesDetails;
	}
	public void setTestSuitesDetails(List<ClusterTestSuitesDefinitionVO> testSuitesDetails) {
		this.testSuitesDetails = testSuitesDetails;
	}
	public List<ConfigVO> getConfigDetails() {
		return configDetails;
	}
	public void setConfigDetails(List<ConfigVO> configDetails) {
		this.configDetails = configDetails;
	}
	public List<ChangeRoleModel> getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(List<ChangeRoleModel> userDetails) {
		this.userDetails = userDetails;
	}
	
	
	

}
