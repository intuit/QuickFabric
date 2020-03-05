package com.intuit.quickfabric.commons.vo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class YarnAppHeuristicVO {
    private String heuristicName;
    private String severity;
    private long score;
    private String advice; 
    
    public String getHeuristicName() {
        return heuristicName;
    }
    
    public void setHeuristicName(String heuristicName) {
        this.heuristicName = heuristicName;
    }
    public String getSeverity() {
        return severity;
    }
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    public long getScore() {
        return score;
    }
    public void setScore(long score) {
        this.score = score;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }
}
