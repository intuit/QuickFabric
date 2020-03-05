package com.intuit.quickfabric.commons.vo;

import java.util.List;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonInclude;

@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobSchedulingAdviceVO {
    private String advice;
    private List<EMRTimeSeriesReportVO> leastUsed;
    private List<EMRTimeSeriesReportVO> mostUsed;
    
    
    public String getAdvice() {
        return advice;
    }
    
    public void setAdvice(String advice) {
        this.advice = advice;
    }
    
    public List<EMRTimeSeriesReportVO> getLeastUsed() {
        return leastUsed;
    }
    
    public void setLeastUsed(List<EMRTimeSeriesReportVO> leastUsed) {
        this.leastUsed = leastUsed;
    }
    
    public List<EMRTimeSeriesReportVO> getMostUsed() {
        return mostUsed;
    }
    
    public void setMostUsed(List<EMRTimeSeriesReportVO> mostUsed) {
        this.mostUsed = mostUsed;
    }

}
