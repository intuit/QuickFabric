package com.intuit.quickfabric.emr.model;

import java.util.List;

import com.intuit.quickfabric.commons.vo.EMRAppVO;

public class EMRAppsModel {
    
    private String emrId;
    private String emrName;
    private List<EMRAppVO> apps;

    public List<EMRAppVO> getApps() {
        return apps;
    }

    public void setApps(List<EMRAppVO> apps) {
        this.apps = apps;
    }

    public String getEmrId() {
        return emrId;
    }

    public void setEmrId(String emrId) {
        this.emrId = emrId;
    }

    public String getEmrName() {
        return emrName;
    }

    public void setEmrName(String emrName) {
        this.emrName = emrName;
    }

}
