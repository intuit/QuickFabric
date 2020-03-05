package com.intuit.quickfabric.commons.vo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SegmentVO {

    @JsonIgnore
    private int segmentId;

    private String segmentName;
    
    private String businessOwner;
    
    private String businessOwnerEmail;

    private List<AwsAccountProfile> accounts;

    public List<AwsAccountProfile> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AwsAccountProfile> accounts) {
        this.accounts = accounts;
    }

    public int getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    public String getBusinessOwner() {
        return businessOwner;
    }

    public void setBusinessOwner(String businessOwner) {
        this.businessOwner = businessOwner;
    }

    public String getBusinessOwnerEmail() {
        return businessOwnerEmail;
    }

    public void setBusinessOwnerEmail(String businessOwnerEmail) {
        this.businessOwnerEmail = businessOwnerEmail;
    }

    @Override
    public String toString() {
        return "SegmentVO{" +
                "segmentId=" + segmentId +
                ", segmentName='" + segmentName + '\'' +
                ", businessOwner='" + businessOwner + '\'' +
                ", businessOwnerEmail='" + businessOwnerEmail + '\'' +
                ", accounts=" + accounts +
                '}';
    }
}