package com.intuit.quickfabric.emr.model;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.intuit.quickfabric.commons.vo.EMRGroupCostVO;
import com.intuit.quickfabric.commons.vo.EMRTimeSeriesReportVO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EMRClusterCostModel {
    private String clusterId;
    private List<EMRTimeSeriesReportVO> clusterCost;
    private List<EMRGroupCostVO> emrGroupCost;
    
    public String getClusterId() {
        return clusterId;
    }
   
    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }
    
    public List<EMRTimeSeriesReportVO> getClusterCost() {
        return clusterCost;
    }
    
    public void setClusterCost(List<EMRTimeSeriesReportVO> clusterCost) {
        this.clusterCost = clusterCost;
    }
    
    public List<EMRGroupCostVO> getEmrGroupCost() {
        return emrGroupCost;
    }
    
    public void setEmrGroupCost(List<EMRGroupCostVO> emrGroupCost) {
        this.emrGroupCost = emrGroupCost;
    }
    
    
}