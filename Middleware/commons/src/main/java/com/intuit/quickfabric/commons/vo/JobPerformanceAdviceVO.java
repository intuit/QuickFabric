package com.intuit.quickfabric.commons.vo;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;

@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobPerformanceAdviceVO {
    private String advice;
    private List<EMRAppVO> criticalApps;
    private List<EMRAppVO> severeApps;
    private List<EMRAppVO> moderateApps;
    

    public String getAdvice() {
        return advice;
    }


    public void setAdvice(String advice) {
        this.advice = advice;
    }
    
    public List<EMRAppVO> getCriticalApps() {
        return criticalApps;
    }

    
    public void setCriticalApps(List<EMRAppVO> criticalApps) {
        this.criticalApps = criticalApps;
    }

    public List<EMRAppVO> getSevereApps() {
        return severeApps;
    }

    public void setSevereApps(List<EMRAppVO> severeApps) {
        this.severeApps = severeApps;
    }

    public List<EMRAppVO> getModerateApps() {
        return moderateApps;
    }

    public void setModerateApps(List<EMRAppVO> moderateApps) {
        this.moderateApps = moderateApps;
    }

}
