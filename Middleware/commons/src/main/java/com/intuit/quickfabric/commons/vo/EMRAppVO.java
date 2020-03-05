package com.intuit.quickfabric.commons.vo;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EMRAppVO {


    private String applicationId;
    private String applicationName;
    private String applicationType;
    private String user;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy HH:mm:ss", timezone="PST")
    private Timestamp startTimestamp;
    @JsonDeserialize(as=AppStatus.class)
    private AppStatus status;
    //String to easily change to N/A for running apps
    private String finishedTimestamp;
    //in minutes
    private long elapsedTime;
    //in percent
    private float progress;
    private float clusterUsagePercentage;
    private long drElephantScore;
    @JsonDeserialize(as=AppSeverity.class)
    private AppSeverity severity;
    //The product of container size and task run time (API returns in MBSeconds, we convert to GB Hours
    private long resourceUsed;
    private long resourceWasted;
    private long waitTime;
    private List<YarnAppHeuristicVO> yarnAppHeuristicResults;
    private long allocatedMB;
    private long allocatedVCores;
    private long runningContainers;

    public String getApplicationId() {
        return applicationId;
    }

    @JsonSetter("id")
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationType() {
        return applicationType;
    }

    @JsonSetter("jobType")
    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @JsonSetter("username")
    private void setUsername(String user) {
        this.user = user;
    }

    public Timestamp getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Timestamp startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    @JsonSetter("startTime")
    private void setStartTime(long startTime) {
        this.startTimestamp = new Timestamp(startTime);
    }


    public AppStatus getStatus() {
        return status;
    }

    public void setStatus(AppStatus status) {
        this.status = status;
    }

    @JsonGetter("endTimestamp")
    public String getFinishedTimestamp() {
        return finishedTimestamp;
    }


    public void setFinishedTimestamp(String endTimestamp) {
        this.finishedTimestamp = endTimestamp;
    }

    @JsonSetter("finishTime")
    private void setFinishedTime(long finishedTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        this.finishedTimestamp = formatter.format(new Timestamp(finishedTime));
    }

    public long getElapsedTime() {
        if(elapsedTime == 0L && finishedTimestamp != null && 
                !finishedTimestamp.equalsIgnoreCase("N/A") && startTimestamp != null) {

            long begin = startTimestamp.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
            try {
                long end = formatter.parse(finishedTimestamp).getTime();
                return (end - begin)/(60 * 1000L);
            } catch(ParseException e) {
                return elapsedTime;
            }
        } else {
            return elapsedTime;
        }
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public float getProgress() {
        return progress;
    }
    public void setProgress(float progress) {
        this.progress = progress;
    }

    public float getClusterUsagePercentage() {
        return clusterUsagePercentage;
    }
    public void setClusterUsagePercentage(float clusterUsagePercentage) {
        this.clusterUsagePercentage = clusterUsagePercentage;
    }

    public String getApplicationName() {
        return applicationName;
    }

    @JsonSetter("name")
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public long getDrElephantScore() {
        return drElephantScore;
    }

    @JsonSetter("score")
    public void setDrElephantScore(long drElephantScore) {
        this.drElephantScore = drElephantScore;
    }

    public AppSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AppSeverity severity) {
        this.severity = severity;
    }

    public long getResourceUsed() {
        return resourceUsed;
    }

    @JsonSetter("resourceUsed")
    public void setResourceUsed(long resourceUsed) {
        //divide by 1000 to go from MBSeconds to GBSeconds
        //divide by 60*60 to go from GBSeconds to GBHours
        this.resourceUsed = resourceUsed / (60 * 60) / 1000L;
    }

    public long getResourceWasted() {
        return resourceWasted;
    }

    @JsonSetter("resourceWasted")
    public void setResourceWasted(long resourceWasted) {
        //divide by 1000 to go from MBSeconds to GBSeconds
        //divide by 60*60 to go from GBSeconds to GBHours
        this.resourceWasted = resourceWasted / (60 * 60) / 1000L;
    }

    public long getWaitTime() {
        return waitTime;
    }

    //referred to totalDelay in certain portions of code/API response, but wait time in docs
    @JsonSetter("totalDelay")
    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime / (60 * 1000L);
    }

    public List<YarnAppHeuristicVO> getYarnAppHeuristicResults() {
        return yarnAppHeuristicResults;
    }

    @JsonSetter("yarnAppHeuristicResults")
    public void setYarnAppHeuristicResults(List<YarnAppHeuristicVO> heuristicResults) {
        this.yarnAppHeuristicResults = heuristicResults;
    }

    public long getAllocatedMB() {
        return allocatedMB;
    }

    public void setAllocatedMB(long allocatedMB) {
        this.allocatedMB = allocatedMB;
    }

    public long getAllocatedVCores() {
        return allocatedVCores;
    }

    public void setAllocatedVCores(long allocatedVCores) {
        this.allocatedVCores = allocatedVCores;
    }

    public long getRunningContainers() {
        return runningContainers;
    }

    public void setRunningContainers(long runningContainers) {
        this.runningContainers = runningContainers;
    }



}
