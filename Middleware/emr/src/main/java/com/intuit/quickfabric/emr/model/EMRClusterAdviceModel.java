package com.intuit.quickfabric.emr.model;


import org.springframework.stereotype.Component;

import com.intuit.quickfabric.commons.vo.JobPerformanceAdviceVO;
import com.intuit.quickfabric.commons.vo.JobSchedulingAdviceVO;

@Component
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EMRClusterAdviceModel {
    
    private String emrId;
    private JobPerformanceAdviceVO jobPerformanceAdvice;
    private JobSchedulingAdviceVO jobSchedulingAdvice;
    
    public String getEmrId() {
        return emrId;
    }
    
    public void setEmrId(String emrId) {
        this.emrId = emrId;
    }
       
    public JobPerformanceAdviceVO getJobPerformanceAdvice() {
        return jobPerformanceAdvice;
    }
    
    public void setJobPerformanceAdvice(JobPerformanceAdviceVO jobPerfomanceAdvice) {
        this.jobPerformanceAdvice = jobPerfomanceAdvice;
    }
    
    public JobSchedulingAdviceVO getJobSchedulingAdvice() {
        return jobSchedulingAdvice;
    }
    
    public void setJobSchedulingAdvice(JobSchedulingAdviceVO jobSchedulingAdvice) {
        this.jobSchedulingAdvice = jobSchedulingAdvice;
    }
}
