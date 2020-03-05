package com.intuit.quickfabric.commons.vo;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EMRTimeSeriesReportVO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy HH:mm:ss", timezone="PST")
    private Timestamp time;


    
    private float avgMemoryUsagePct;
    private float avgCoresUsagePct;
    private int appsSucceeded;
    private int appsFailed;
    private int appsRunning;
    private int appsPending;
    private int hourOfDay;
    private int cost;
    

    public EMRTimeSeriesReportVO() {

    }



    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @JsonGetter("memoryUsagePct")
    public float getAvgMemoryUsagePct() {
        return avgMemoryUsagePct;
    }

    public void setAvgMemoryUsagePct(float memoryUsagePct) {
        this.avgMemoryUsagePct = memoryUsagePct;
    }

    @JsonGetter("coresUsagePct")
    public float getAvgCoresUsagePct() {
        return avgCoresUsagePct;
    }

    public void setAvgCoresUsagePct(float coresUsagePct) {
        this.avgCoresUsagePct = coresUsagePct;
    }

    public int getAppsSucceeded() {
        return appsSucceeded;
    }


    public void setAppsSucceeded(int appsSucceeded) {
        this.appsSucceeded = appsSucceeded;
    }

    public int getAppsFailed() {
        return appsFailed;
    }

    public void setAppsFailed(int appsFailed) {
        this.appsFailed = appsFailed;
    }

    public int getAppsRunning() {
        return appsRunning;
    }

    public void setAppsRunning(int appsRunning) {
        this.appsRunning = appsRunning;
    }

    public int getAppsPending() {
        return appsPending;
    }

    public void setAppsPending(int appsPending) {
        this.appsPending = appsPending;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }
    
    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }


    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
