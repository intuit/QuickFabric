package com.intuit.quickfabric.emr.model;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.intuit.quickfabric.commons.vo.AwsAccountProfile;
import com.intuit.quickfabric.commons.vo.SegmentVO;

@Component
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class UIPopulationModel {
    private List<SegmentVO> segments;
    private List<AwsAccountProfile> accounts;
    private List<String> actions;
    
    
    public List<SegmentVO> getSegments() {
        return segments;
    }
    
    public void setSegments(List<SegmentVO> segments) {
        this.segments = segments;
    }
    
    public List<AwsAccountProfile> getAccounts() {
        return accounts;
    }
    
    public void setAccounts(List<AwsAccountProfile> accounts) {
        this.accounts = accounts;
    }
    
    public List<String> getActions() {
        return actions;
    }
    
    public void setActions(List<String> actions) {
        this.actions = actions;
    }
    

}
