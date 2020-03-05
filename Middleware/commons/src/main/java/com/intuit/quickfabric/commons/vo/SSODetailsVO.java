package com.intuit.quickfabric.commons.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SSODetails", propOrder = { "userId","qbnPtcAuthid","qbnAuthid","qbnPtcTkt","qbTkt","email","firstName","lastName"})

@XmlRootElement(name = "SSODetailsInfo")
public class SSODetailsVO {
	@XmlElement(required = false)
	private String userId;
	@XmlElement(required = false)
	private String qbnPtcAuthid;
	@XmlElement(required = false)
	private String qbnAuthid;
	@XmlElement(required = false)
	private String qbnPtcTkt;
	@XmlElement(required = false)
	private String qbTkt;
	@XmlElement(required = false)
	private String email;
	@XmlElement(required = false)
	private String firstName;
	@XmlElement(required = false)
	private String lastName;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getQbnPtcAuthid() {
		return qbnPtcAuthid;
	}
	public void setQbnPtcAuthid(String qbnPtcAuthid) {
		this.qbnPtcAuthid = qbnPtcAuthid;
	}
	public String getQbnAuthid() {
		return qbnAuthid;
	}
	public void setQbnAuthid(String qbnAuthid) {
		this.qbnAuthid = qbnAuthid;
	}
	public String getQbnPtcTkt() {
		return qbnPtcTkt;
	}
	public void setQbnPtcTkt(String qbnPtcTkt) {
		this.qbnPtcTkt = qbnPtcTkt;
	}
	public String getQbTkt() {
		return qbTkt;
	}
	public void setQbTkt(String qbTkt) {
		this.qbTkt = qbTkt;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "SSODetailsVO [userId=" + userId + ", qbnPtcAuthid=" + qbnPtcAuthid + ", qbnAuthid="
				+ qbnAuthid + ", qbnPtcTkt=" + qbnPtcTkt + ", qbTkt=" + qbTkt + ", email=" + email
				+ ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}
}
