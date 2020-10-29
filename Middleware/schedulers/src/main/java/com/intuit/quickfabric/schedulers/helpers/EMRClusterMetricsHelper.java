package com.intuit.quickfabric.schedulers.helpers;


import com.intuit.quickfabric.commons.constants.ApiUrls;
import com.intuit.quickfabric.commons.constants.ApplicationConstant;
import com.intuit.quickfabric.commons.domain.UserAccess;
import com.intuit.quickfabric.commons.exceptions.QuickFabricJsonException;
import com.intuit.quickfabric.commons.helper.ConfigHelper;
import com.intuit.quickfabric.commons.utils.AWSEmailUtil;
import com.intuit.quickfabric.commons.utils.ReportBuilder;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.schedulers.dao.EMRClusterMetadataDao;
import com.intuit.quickfabric.schedulers.dao.EMRClusterMetricsDao;
import com.intuit.quickfabric.schedulers.dao.EMRClusterStepsDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EMRClusterMetricsHelper {


    @Autowired
    EMRClusterMetricsDao emrClusterMetricsDao;

    @Autowired
    EMRClusterMetadataDao emrClusterMetadataDao;

    @Autowired
    EMRClusterStepsDao emrClusterStepsDao;

    @Autowired
    EMRAWSServiceCallerHelper caller;

    @Autowired
    ConfigHelper configHelper;

    @Autowired
    AWSEmailUtil emailUtil;

    Logger logger = LogManager.getLogger(EMRClusterMetricsHelper.class);


    public void updateEMRClusterMetricsList(List<EMRClusterMetricsVO> metrices) {
        emrClusterMetricsDao.updateEMRClusterMetricsList(metrices);
    }

    public void saveNewClusterDetailsToRDS(ClusterVO clusterDetails) {
        emrClusterMetadataDao.saveNewClusterDetails(clusterDetails);

    }

    public void updateBootstrapActionStatus(ClusterVO clusterDetails, String bootstrapStatus) {
        emrClusterStepsDao.updateBootstrapActionStatus(clusterDetails, bootstrapStatus);

    }

    public void updateClusterStatusInDB(ClusterVO clusterDetails) {
        emrClusterMetadataDao.updateClusterStatusInDB(clusterDetails);

    }


    public List<ClusterRequest> getClustersWithClusterStatuses(Set<ClusterStatus> statues) {
        return emrClusterMetadataDao.getClustersWithClusterStatuses(statues);

    }


    public List<ClusterVO> getClustersToValidate(Set<ClusterStatus> statues) {
        return emrClusterMetadataDao.getClustersToValidate(statues);
    }

    public List<ClusterRequest> getStepsForNewSucceededClusters(Set<ClusterStatus> statuses, List<StepStatus> stepStatuses) {
        return emrClusterStepsDao.getStepsForNewSucceededClusters(statuses, stepStatuses);
    }


    public void getAndSaveClusterBootstrapActionsRequest(ClusterVO clusterDetails) {

        emrClusterStepsDao.saveBootstrapActionRequestForCluster(clusterDetails);
    }

    public void updateStepIdsInDB(StepResponseVO stepResponse, List<ClusterStep> steps) {
        emrClusterStepsDao.updateStepIdsInDB(stepResponse, steps);
    }


    public List<ClusterRequest> getCompletedTransientClusters(Set<ClusterStatus> statuses, List<StepStatus> stepStatuses) {
        return emrClusterMetadataDao.getClusterWithSucceededStatusAndStepsWithCompletedStatus(statuses, stepStatuses);
    }

    public List<ClusterRequest> getCompletedNonTransientClusters(Set<ClusterStatus> statuses, List<StepStatus> stepStatuses) {
        return emrClusterMetadataDao.getCompletedNonTransientClusters(statuses, stepStatuses);
    }

    public List<ClusterRequest> getCompletedTestingClusters(Set<ClusterStatus> statuses,
                                                            List<StepStatus> stepStatuses) {
        return emrClusterMetadataDao.getCompletedTestingClusters(statuses, stepStatuses);
    }

    public void updateClusterStatusByClusterId(ClusterVO clusterVO) {
        emrClusterMetadataDao.updateClusterStatusByClusterId(clusterVO);
    }

    public List<ClusterRequest> getClustersforAMIRotation(Set<ClusterStatus> statuses) {
        return emrClusterMetadataDao.getClustersforAMIRotation(statuses);
    }

    /**
     * Pulls cluster metrics from dynamoDB. Returns data for non-transient clusters
     * that are running or waiting. Also returns data from resource manager
     * metrics and applications APIs
     *
     * @return the cluster metrics
     * @throws JSONException
     */

    public List<EMRClusterMetricsVO> getClusterMetrics() {
        logger.info("Starting EMR Metrics Collection...");

        //fetching clusters directly from db for which metrics collection need to happen
        Set<ClusterStatus> statuses = new HashSet<ClusterStatus>();
        statuses.add(ClusterStatus.RUNNING);
        statuses.add(ClusterStatus.WAITING);

        List<EMRClusterMetricsVO> metrics = emrClusterMetadataDao.getClusterMetadataForMetrics(statuses);
        //Separate foreach to only make API calls about clusters we are interested in
        for (EMRClusterMetricsVO vo : metrics) {
        	try {
	        	String accountSpecificUrl = configHelper.getAccountSpecificUrl(vo.getAccount(), ApiUrls.RM_PROXY_URL_SUFFIX);
	            ResponseEntity<String> metricResponse = caller.invokeGetClusterMetricsService(accountSpecificUrl, vo.getRmUrl(), vo.getAccount());
	            //we save both the whole JSON object from the call and then extract certain values
	            flattenClusterMetricsJsonOntoVo(vo, metricResponse);
	            //APPS
	            ResponseEntity<String> appStatsRespons = caller.invokeGetRmAppsService(vo.getRmUrl(), vo.getAccount());
	            
	            setAppsSucceededFailed(appStatsRespons, vo);
        	}catch(Exception e) {
        		//not throwing new QF exceptions as we want to complete FOR loop
        		logger.error("error occured during Metric Collection for cluster {}. with error {}",vo.getEmrName(),e);
        	}
	  }//end of for loop

        return metrics;
    }


    /**
     * retrieve values from the cluster metrics JSON and set them on a top level attribute of the VO
     *
     * @param vo       the metrics object on which to set fields
     */

    private void flattenClusterMetricsJsonOntoVo(EMRClusterMetricsVO vo, ResponseEntity<String> metricResponse) {

        try {
        	JSONObject metricStatsResponse = new JSONObject(metricResponse.getBody()).getJSONObject("metricStats");
            JSONObject clusterDataJson =
                    metricStatsResponse.getJSONObject(ApplicationConstant.CLUSTER_METRICS);
            vo.setMetricsJson(metricStatsResponse.toString());
            if (!clusterDataJson.isNull(ApplicationConstant.ACTIVE_NODES)) {
                vo.setActiveNodes(clusterDataJson.getInt(ApplicationConstant.ACTIVE_NODES));
            }
            if (!clusterDataJson.isNull(ApplicationConstant.CONTAINER_PENDING)) {
                vo.setContainersPending(clusterDataJson.getInt(ApplicationConstant.CONTAINER_PENDING));
            }
            if (!clusterDataJson.isNull(ApplicationConstant.APPS_PENDING)) {
                vo.setAppsPending(clusterDataJson.getInt(ApplicationConstant.APPS_PENDING));
            }
            if (!clusterDataJson.isNull(ApplicationConstant.APPS_RUNNING)) {
                vo.setAppsRunning(clusterDataJson.getInt(ApplicationConstant.APPS_RUNNING));
            }
            if (!clusterDataJson.isNull(ApplicationConstant.AVAILABLE_MB) &&
                    !clusterDataJson.isNull(ApplicationConstant.TOTAL_MB)) {

    			float memoryUsagePct =
    					100 * (1 - computePct(clusterDataJson.getDouble(ApplicationConstant.AVAILABLE_MB),
    							clusterDataJson.getDouble(ApplicationConstant.TOTAL_MB)));

    			vo.setMemoryUsagePct(memoryUsagePct);

            }

            if (!clusterDataJson.isNull(ApplicationConstant.AVAILABLE_VIRTUAL_CORES)
                    && !clusterDataJson.isNull(ApplicationConstant.TOTAL_VIRTUAL_CORES)) {

    			float coresUsagePct =
    					100 * (1 - computePct(clusterDataJson.getDouble(ApplicationConstant.AVAILABLE_VIRTUAL_CORES),
    							clusterDataJson.getDouble(ApplicationConstant.TOTAL_VIRTUAL_CORES)));

    			vo.setCoresUsagePct(coresUsagePct);
            }
        } catch (JSONException e) {
            logger.error("Error getting metrics for emr name '" + vo.getEmrName() + "' " + e.getMessage());
            throw new QuickFabricJsonException("Error getting metrics for emr name :"+vo.getEmrName(),e);

        }
    }

    /**
     * Count the number of apps that have succeeded and failed in this response and set the counts
     * on the vo
     *
     * @param vo       the metrics object to set the stats onto
     */

    private void setAppsSucceededFailed( ResponseEntity<String> appStatsResponse, EMRClusterMetricsVO vo) {
        //try to set these fields to null if computing the result leads to an exception
        // (not completely robust)

    	Integer appsSucceeded = null;
        Integer appsFailed = null;

        try {
        	JSONObject appMetricsResponse = new JSONObject(appStatsResponse.getBody()).getJSONObject("metricStats");
            logger.info("EMRClusterMetricsHelper->setAppsSucceededFailed rm url " + vo.getRmUrl());

            appsSucceeded = 0;
            appsFailed = 0;
            if (!appMetricsResponse.get("apps").equals(JSONObject.NULL)) {
                JSONObject responseJSONApps = appMetricsResponse.getJSONObject("apps");
                JSONArray appsList = responseJSONApps.getJSONArray("app");
                for (int i = 0; i < appsList.length(); i++) {
                    JSONObject app = appsList.getJSONObject(i);

    				if(app.getString("finalStatus").equals("SUCCEEDED")) {
                        appsSucceeded++;
                    }

                    if (app.getString("finalStatus").equals(ClusterStatus.FAILED.toString())) {
                        appsFailed++;
                    }
                }
            } else {
                logger.info("EMRClusterMetricsHelper->setAppsSucceededFailed responseJSON value =  " + appMetricsResponse.get("apps"));
            }
        } catch (JSONException e) {
            throw new QuickFabricJsonException("Failed to write applications succeeded/failed onto VO", e);
        }

        vo.setAppsSucceeded(appsSucceeded);
        vo.setAppsFailed(appsFailed);
    }


    private float computePct(double available, double total) {
        return (float) (Math.round((available / total) * 1000d) / 1000d);
    }

   



    public void dailyReport() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Timestamp yesterday = this.generateFromTimestamp("Daily");
        logger.info("Fetching Metrics for EMR Clusters for daily report");
        List<EMRClusterMetricsVO> reportMetrics = emrClusterMetricsDao.getClusterMetricsReport(yesterday, currentTime, "all");
        logger.info("Fetching clusters close to rotation SLA for AMI rotation report");
        List<ClusterVO> clusters =
                this.emrClusterMetadataDao.getAMIRotationReport("all").stream()
                        .filter(c -> c.getAMIRotationDaysToGo() < 7).collect(Collectors.toList());

        logger.info("Picked up clusters close to rotation SLA for AMI rotation report");

        ReportBuilder builder = new ReportBuilder();

        String report = builder.openHtmlTag()
                .openBodyTag()
                .appendAMIRotationReport(clusters, currentTime)
                .appendMetricsReport(reportMetrics, yesterday, currentTime)
                .closeBodyTag()
                .closeHtmlTag()
                .build();

        String subject = "Daily Report for EMR Clusters";
        logger.info("Calling the Email Utility Service to send email daily report");
        String recipients = configHelper.getConfigValue("report_recipients");
        emailUtil.sendEmail(report,subject, recipients.split(","));

        
    }


    public void weeklyReport() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Timestamp lastWeek = this.generateFromTimestamp("Weekly");

        logger.info("Fetching Metrics for EMR Clusters for weekly report");
        List<EMRClusterMetricsVO> reportMetrics = emrClusterMetricsDao.getClusterMetricsReport(lastWeek, currentTime, "all");
        logger.info("Picked up metrics from Database for cluster for weekly report");

        ReportBuilder builder = new ReportBuilder();

        String report = builder.openHtmlTag()
                .openBodyTag()
                .appendMetricsReport(reportMetrics, lastWeek, currentTime)
                .closeBodyTag()
                .closeHtmlTag()
                .build();

        String subject = "Weekly Metrics Report for EMR Clusters";
        logger.info("Calling the Email Utility Service to send email weekly report");
        String recipients = configHelper.getConfigValue("report_recipients");
        emailUtil.sendEmail(report,subject, recipients.split(","));
       
    }

    public void monthlyReport() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Timestamp lastMonth = this.generateFromTimestamp("Monthly");

        logger.info("Fetching Metrics for EMR Clusters for monthly report");
        List<EMRClusterMetricsVO> reportMetrics = emrClusterMetricsDao.getClusterMetricsReport(lastMonth, currentTime, "all");

        logger.info("Fetching clusters close to rotation SLA for AMI rotation monthly report");
        List<ClusterVO> clusters =
                this.emrClusterMetadataDao.getAMIRotationReport("all").stream()
                        .filter(c -> c.getAMIRotationDaysToGo() < 7).collect(Collectors.toList());
        logger.info("Picked up clusters close to rotation SLA for AMI rotation monthly report");

        ReportBuilder builder = new ReportBuilder();

        String report = builder.openHtmlTag()
                .openBodyTag()
                .appendAMIRotationReport(clusters, currentTime)
                .appendMetricsReport(reportMetrics, lastMonth, currentTime)
                .closeBodyTag()
                .closeHtmlTag()
                .build();

        String subject = "Monthly Metrics Report for EMR Clusters";
        logger.info("Calling the Email Utility Service to send email monthly report");
        String recipients = configHelper.getConfigValue("report_recipients");
        emailUtil.sendEmail(report,subject, recipients.split(","));

       
    }


    private Timestamp generateFromTimestamp(String reportType) {
        Calendar cal = Calendar.getInstance();
        if (reportType.equals("Daily")) {
            cal.add(Calendar.HOUR, -24);
        } else if (reportType.equals("Hourly")) {
            cal.add(Calendar.HOUR, -1);
        } else if (reportType.equals("Weekly")) {
            cal.add(Calendar.DATE, -7);
        } else if (reportType.equals("Monthly")) {
            cal.add(Calendar.MONTH, -1);
        } else {
            logger.info("Improper Report Type: " + reportType + " Sent. Exiting...");
            System.exit(1);
        }

        Timestamp fromTimestamp = new Timestamp(cal.getTime().getTime());

        return fromTimestamp;

    }

   
    public void sendSegmentReports() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Timestamp yesterday = this.generateFromTimestamp("Daily");
        List<SegmentVO> segments = emrClusterMetadataDao.getSegment("all");

        for (SegmentVO segment : segments) {
            //not all segments have a business owner
            if (StringUtils.isBlank(segment.getBusinessOwnerEmail())) {
                logger.info("No business owner information for {} segment, skipping report",
                        segment.getSegmentName());
                continue;
            }

            List<EMRClusterMetricsVO> reportMetrics = emrClusterMetricsDao.getClusterMetricsReport(yesterday, currentTime,segment.getSegmentName());
            List<ClusterVO> clusters =
                    this.emrClusterMetadataDao.getAMIRotationReport(segment.getSegmentName()).stream()
                            .filter(c -> c.getAMIRotationDaysToGo() < 7).collect(Collectors.toList());

            ReportBuilder builder = new ReportBuilder();

            String report = builder.openHtmlTag()
                    .openBodyTag()
                    .appendAMIRotationReport(clusters, currentTime)
                    .appendMetricsReport(reportMetrics, yesterday, currentTime)
                    .closeBodyTag()
                    .closeHtmlTag()
                    .build();

            String subject = "Daily EMR Report for " + segment.getSegmentName() + " segment";
            logger.info("Calling the Email Utility Service to send personalized email report to {}",
            		segment.getBusinessOwnerEmail());
            emailUtil.sendEmail(report, subject, segment.getBusinessOwnerEmail());

        }
    }


    public void sendSubscriptionReports() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp yesterday = new Timestamp(now.getTime() - 24 * 60 * 60 * 1000L);

        //Just grab these once and then filter them for the user
        List<EMRClusterMetricsVO> allMetrics = emrClusterMetricsDao.getClusterMetricsReport(yesterday, now,"all");

        List<ClusterVO> allClusters = this.emrClusterMetadataDao.getAMIRotationReport("all");

        List<UserVO> users = this.getUsers();
	    for (UserVO user : users) { //Different report for each user
	    	try {
	            ReportBuilder reportBuilder = new ReportBuilder();
	
	            reportBuilder.openHtmlTag()
	                    .openBodyTag();
	
	            //all the reports this user is subscribed to
	            List<SubscriptionVO> subscriptions = this.getSubscriptionsForUser(user.getUserId());
	            //send details about all clusters this user has read access to
	            List<UserAccess> readAccessForUser = this.getReadAccessForUser(user.getUserId());
	
	            List<ClusterVO> clustersOwnedByUser = this.filterClustersOwnedByUser(user, allClusters);
	            List<EMRClusterMetricsVO> metricsOwnedByUser =
	                    this.filterMetricsOwnedByUser(clustersOwnedByUser, allMetrics);
	
	            // this user doesn't own any clusters, nor has any subscriptions. So skip.
	            if (clustersOwnedByUser.isEmpty() && subscriptions.isEmpty()) {
	                logger.info("User {} does not own any clusters or have any subscriptions, "
	                        + "not sending email", user.getUserEmail());
	                continue;
	            }
	
	            boolean subscribedToAMI = subscriptions.stream().anyMatch(
	                    s -> s.getReportName().equalsIgnoreCase("AMI Rotation"));
	
	            boolean subscribedToMetrics = subscriptions.stream().anyMatch(
	                    s -> s.getReportName().equalsIgnoreCase("Cluster Metrics"));
	
	            List<ClusterVO> clustersForAMIReport =
	                    subscribedToAMI ? this.collectClustersForAMIReport(clustersOwnedByUser, allClusters, readAccessForUser)
	                            : clustersOwnedByUser;
	
	            clustersForAMIReport = clustersForAMIReport.stream()
	                    .filter(c -> c.getAMIRotationDaysToGo() < 7).collect(Collectors.toList());
	
	            List<EMRClusterMetricsVO> metricsForReport =
	                    subscribedToMetrics ?
	                            this.collectClustersForMetricsReport(metricsOwnedByUser, allMetrics, readAccessForUser)
	                            : metricsOwnedByUser;
	
	            if (!clustersForAMIReport.isEmpty()) {
	                logger.info("Adding AMI Rotation report to daily report for {}",
	                        user.getUserEmail());
	                reportBuilder.appendAMIRotationReport(clustersForAMIReport, now);
	            }
	
	            if (!metricsForReport.isEmpty()) {
	                logger.info("Adding Cluster Metrics report to daily report for {}", user.getUserEmail());
	                reportBuilder.appendMetricsReport(metricsForReport, yesterday, now);
	            }
	
	            reportBuilder.closeBodyTag().closeHtmlTag();
	
	           
	                logger.info("Sending daily report email to {}", user.getUserEmail());
	                emailUtil.sendEmail(reportBuilder.build(), "Your Daily Report from QuickFabric",user.getUserEmail());
	    	}catch(Exception e) { // not throwing any QF exception because we want to finish whole FOR loop
	    		logger.error("skipping !! as something went wrong during sending report for user {}",user.getUserEmail());
	    	}
	    }//end of FOR loop
    }


    private List<ClusterVO> collectClustersForAMIReport(List<ClusterVO> clustersOwnedByUser,
                                                        List<ClusterVO> allClusters, List<UserAccess> roles) {
        List<ClusterVO> retVal = new ArrayList<>();

        List<ClusterVO> accessibleClusters = this.filterClustersForUser(allClusters, roles);

        retVal.addAll(clustersOwnedByUser);

        for (ClusterVO cluster : accessibleClusters) {
            if (!retVal.stream().map(c -> c.getClusterId()).anyMatch(id -> id.equals(cluster.getClusterId()))) {
                retVal.add(cluster);
            }
        }

        return retVal;
    }


    private List<EMRClusterMetricsVO> collectClustersForMetricsReport(List<EMRClusterMetricsVO> metricsOwnedByUser,
                                                                      List<EMRClusterMetricsVO> allMetrics,
                                                                      List<UserAccess> roles) {

        List<EMRClusterMetricsVO> retVal = new ArrayList<>();

        retVal.addAll(metricsOwnedByUser);

        List<EMRClusterMetricsVO> accessibleClusters = this.filterMetricsForUser(allMetrics, roles);

        // no duplicates
        for (EMRClusterMetricsVO cluster : accessibleClusters) {
            if (!retVal.stream().map(m -> m.getEmrId()).anyMatch(id -> id.equals(cluster.getEmrId()))) {
                retVal.add(cluster);
            }
        }

        return retVal;

    }


    private List<EMRClusterMetricsVO> filterMetricsOwnedByUser(List<ClusterVO> usersClusters,
                                                               List<EMRClusterMetricsVO> allMetrics) {
        return allMetrics.stream().filter(m ->
                usersClusters.stream().map(c -> c.getClusterId()).anyMatch(id -> id.equals(m.getEmrId())))
                .collect(Collectors.toList());
    }


    private List<ClusterVO> filterClustersOwnedByUser(UserVO user, List<ClusterVO> allClusters) {
        return allClusters.stream().filter(c -> (user.getFirstName() + " " + user.getLastName())
                .equalsIgnoreCase(c.getCreatedBy())).collect(Collectors.toList());
    }


    public List<UserAccess> getReadAccessForUser(long userId) {
        return this.emrClusterMetadataDao.getReadAccessForUser(userId);
    }


    public List<UserVO> getSubscribers() {
        return this.emrClusterMetadataDao.getSubscribers();
    }


    public List<SubscriptionVO> getSubscriptionsForUser(long userId) {
        return this.emrClusterMetadataDao.getSubscriptionsForUser(userId);
    }

    private List<EMRClusterMetricsVO> filterMetricsForUser(List<EMRClusterMetricsVO> metrics, List<UserAccess> roles) {
        List<EMRClusterMetricsVO> metricsForUser = new ArrayList<>();

        for(UserAccess role : roles) {
            for(EMRClusterMetricsVO cluster : metrics) {
                if(role.getAwsAccountName().equals(cluster.getAccount()) && 
                        role.getSegmentName().equalsIgnoreCase(cluster.getClusterSegment())) {
                    metricsForUser.add(cluster);
                }
            }
        }

        return metricsForUser;
    }

    private List<ClusterVO> filterClustersForUser(List<ClusterVO> clusters, List<UserAccess> roles) {
        List<ClusterVO> clustersForUser = new ArrayList<>();

        for(UserAccess role : roles) {
            for(ClusterVO cluster : clusters) {
                if(role.getAwsAccountName().equals(cluster.getAccount()) && 
                        role.getSegmentName().equalsIgnoreCase(cluster.getSegment())) {
                    clustersForUser.add(cluster);
                }
            }
        }

        return clustersForUser;
    }

    private List<UserVO> getUsers() {
        return this.emrClusterMetadataDao.getUsers();
    }

    public void collectEMRClusterCost() throws Exception {

    	//Fetch the clusters for which cost is needed
    	logger.info("EMRClusterMetricsHelper -> collectEMRClusterCost -> "
    			+ "Fetching Running Clusters");

    	Set<ClusterStatus> statuses = new HashSet<ClusterStatus>();
    	statuses.add(ClusterStatus.RUNNING);
    	statuses.add(ClusterStatus.WAITING);

    	List<EMRClusterMetricsVO> clusters = emrClusterMetadataDao.getClusterMetadataForMetrics(statuses);

    	//for loop to serverless api to fetch the cluster cost
    	for (EMRClusterMetricsVO vo : clusters) {
    		logger.info("EMRClusterMetricsHelper -> collectEMRClusterCost -> "
    				+ "Fetching cost for Cluster:" + vo.getEmrName());
    		try {
    	        String accountSpecificUrl = configHelper.getAccountSpecificUrl(vo.getAccount(), ApiUrls.EMR_COST_URL);
	    		ResponseEntity<String> costResponse = caller.invokeGetEMRCostService(accountSpecificUrl,vo.getEmrName(),vo.getAccount());
	    		Object metricStatsResponse = new JSONObject(costResponse.getBody()).get("totalAmount");
	    		float emrCost = Float.parseFloat(metricStatsResponse.toString());
	    		vo.setCost(emrCost);
    		}catch(Exception e) {
    			logger.error("something went wrong during EMRCluster Cost fatch for  :" + vo.getEmrName(),e);
    		}
    	}

    	//update the db table for the fetched cluster costs
    	logger.info("EMRClusterMetricsHelper -> collectEMRClusterCost -> "
    			+ "Updating Database with Cluster Costs");

    	emrClusterMetricsDao.updateEMRClusterMetricsCost(clusters);
    }

    public void updateClusterDNSinDB(String clusterName, String dnsName) {
        emrClusterMetadataDao.updateClusterDNSinDB(clusterName, dnsName);
    }


}


