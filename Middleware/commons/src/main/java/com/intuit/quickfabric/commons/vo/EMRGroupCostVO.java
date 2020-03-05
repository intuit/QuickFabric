package com.intuit.quickfabric.commons.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"emrGroup", "account", "segment", "businessOwner", "costPerMonth"})
public class EMRGroupCostVO {
    private String emrGroup;
    private String account;
    private String segment;
    private List<MonthlyCostVO> costPerMonth;
    private String businessOwner;
    
    public String getEmrGroup() {
        return emrGroup;
    }
    
    public void setEmrGroup(String emrGroup) {
        this.emrGroup = emrGroup;
    }
    
    public String getAccount() {
        return account;
    }
    
    public void setAccount(String account) {
        this.account = account;
    }
    
    public String getSegment() {
        return segment;
    }
    
    public void setSegment(String segment) {
        this.segment = segment;
    }

    public List<MonthlyCostVO> getCostPerMonth() {
        return costPerMonth;
    }

    public void setCostPerMonth(List<MonthlyCostVO> costPerMonth) {
        this.costPerMonth = costPerMonth;
    }  
    
    public List<MonthlyCostVO> addMonthlyCost(MonthlyCostVO month) {
        if(this.costPerMonth == null) {
            this.costPerMonth = new ArrayList<>();
        }
        
        this.costPerMonth.add(month);
        
        return this.costPerMonth;
    }

    public String getBusinessOwner() {
        return businessOwner;
    }

    public void setBusinessOwner(String businessOwner) {
        this.businessOwner = businessOwner;
    }
} 