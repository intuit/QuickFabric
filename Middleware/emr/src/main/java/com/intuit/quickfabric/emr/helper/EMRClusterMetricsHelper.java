package com.intuit.quickfabric.emr.helper;

import com.intuit.quickfabric.commons.exceptions.QuickFabricClientException;
import com.intuit.quickfabric.commons.exceptions.QuickFabricJsonException;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.emr.dao.EMRClusterMetadataDao;
import com.intuit.quickfabric.emr.dao.EMRClusterMetricsDao;
import com.intuit.quickfabric.emr.dao.EMRClusterStepsDao;
import com.intuit.quickfabric.emr.model.EMRAppsModel;
import com.intuit.quickfabric.emr.model.EMRClusterMetricsModel;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
public class EMRClusterMetricsHelper {


    @Autowired
    EMRClusterMetricsDao emrClusterMetricsDao;

    @Autowired
    EMRClusterMetadataDao emrClusterMetadataDao;

    @Autowired
    EMRClusterStepsDao emrClusterStepsDao;

    @Autowired
    EMRClusterMetricsModel model;

    @Autowired
    EMRAWSServiceCallerHelper caller;


    Logger logger = LogManager.getLogger(EMRClusterMetricsHelper.class);


    public EMRClusterMetricsModel getEMRClusterMetricsList(String clusterType, String account) {
        //Taking all for account and cluster
        account = account == null ? "all" : account;
        clusterType = clusterType == null ? "all" : clusterType;

        List<EMRClusterMetricsVO> metricsReport = emrClusterMetricsDao.getEMRClusterMetricsList(clusterType, account);
        model.setEmrClusterMetricsReport(metricsReport);
        return model;
    }

    public void saveClusterStepsRequest(ClusterVO clusterDetails) {

        emrClusterStepsDao.saveStepRequestForCluster(clusterDetails);
    }

    public void saveClusterBootstrapActionsRequest(ClusterVO clusterDetails) {
        emrClusterStepsDao.saveBootstrapActionRequestForCluster(clusterDetails);
    }


    public void updateStepIdsInDB(StepResponseVO stepResponse, List<ClusterStep> steps) {
        emrClusterStepsDao.updateStepIdsInDB(stepResponse, steps);
    }


    public StepResponseVO getLatestClusterStepStatusForCluster(String clusterId, String requestFrom) {
        ClusterVO clusterVO = emrClusterMetadataDao.getClusterMetadataByClusterId(clusterId);
        StepResponseVO stepResponseVO;

        if (clusterVO == null || StringUtils.isBlank(clusterVO.getClusterName())) {
            throw new QuickFabricClientException("No Cluster with cluster id :" + clusterId + " exists in QuickFabric.");
        }

        //Checking if the API request is from workflow or not
        if (StringUtils.isNotBlank(requestFrom) && requestFrom.equalsIgnoreCase("workflow")) {
            logger.info("Fetching Steps for new cluster as it has been called from workflow. new clusterId:" + clusterVO.getNewClusterId());
            if (StringUtils.isBlank(clusterVO.getNewClusterId())) {
                throw new QuickFabricClientException("No New Cluster associated with this currentClusterId: " + clusterId);
            }
            stepResponseVO = emrClusterStepsDao.getStepsOfACluster(clusterVO.getNewClusterId());
        } else {
            logger.info("Fetching Steps for current cluster");
            stepResponseVO = emrClusterStepsDao.getStepsOfACluster(clusterVO.getClusterId());
        }

        stepResponseVO.setClusterStatus(clusterVO.getStatus());
        stepResponseVO.setClusterId(clusterVO.getClusterId());
        stepResponseVO.setIsActive(clusterVO.getStatus().equals(ClusterStatus.RUNNING) || clusterVO.getStatus().equals(ClusterStatus.WAITING));

        return stepResponseVO;
    }


    public void updateClusterStatusByClusterId(ClusterVO clusterVO) {
        emrClusterMetadataDao.updateClusterStatusByClusterId(clusterVO);
    }

    public void markClusterforTermination(String clusterId) {
        emrClusterMetadataDao.markClusterforTermination(clusterId);

    }

    public void updateClusterDNSinDB(String clusterName, String dnsName) {
        emrClusterMetadataDao.updateClusterDNSinDB(clusterName, dnsName);
    }

    public EMRClusterMetricsModel getEMRClusterMetricsForTimeHelper(String clusterId, String forLast) {

        long now = System.currentTimeMillis();
        Timestamp nowToNearestHour = new Timestamp(now - now % (60 * 60 * 1000));

        switch (forLast) {
            case "hour":
                logger.info("Getting cluster metrics for last hour");
                Timestamp timestamp = new Timestamp(now);
                return getClusterMetricsLastHour(timestamp, clusterId);
            case "day":
                logger.info("Getting cluster metrics for last day");
                return getClusterMetricsLast24Hours(nowToNearestHour, clusterId);
            case "week":
                logger.info("Getting cluster metrics for last week");
                return getClusterMetricsLastWeek(nowToNearestHour, clusterId);
            case "month":
                logger.info("Getting cluster metrics for last month");
                return getClusterMetricsLastMonth(nowToNearestHour, clusterId);
            default:
                String message = "Invalid time parameter " + forLast + " must be one of: hour, day, week, month";
                logger.error(message);
                throw new QuickFabricClientException(message);
        }
    }

    /**
     * Get both aggregated and per-hour metrics for the given cluster over 24 hour period
     *
     * @param to        get metrics for 24 hours going back in time from this timestamp
     * @param clusterId get metrics for this cluster
     * @return the aggregated and per-hour metrics plus cluster metadata
     */
    public EMRClusterMetricsModel getClusterMetricsLast24Hours(Timestamp to, String clusterId) {
        logger.info("Aggregating metrics over last 24 hours");
        List<EMRClusterMetricsVO> aggregatedMetrics =
                emrClusterMetricsDao.getClusterMetricsAggregatedLast24Hours(to, clusterId);

        logger.info("Computing metrics per hour over last 24 hours");
        List<EMRTimeSeriesReportVO> dailyMetrics =
                emrClusterMetricsDao.getClusterMetricsHourlyLast24Hours(to, clusterId);

        //Set the time-series if the list is not empty, else return an empty one
        if (aggregatedMetrics.size() > 0) {
            aggregatedMetrics.get(0).setTimeSeriesMetrics(dailyMetrics);
        }

        model.setEmrClusterMetricsReport(aggregatedMetrics);
        return model;
    }


    /**
     * Get both aggregated and time series metrics for this cluster over last hour
     *
     * @param to        get metrics for last hour going back in time from this timestamp
     * @param clusterId get metrics for this cluster
     * @return the aggregated and time series metrics plus cluster metadata
     */
    public EMRClusterMetricsModel getClusterMetricsLastHour(Timestamp to, String clusterId) {
        logger.info("Aggregating metrics over last hour");
        List<EMRClusterMetricsVO> aggregatedMetrics =
                emrClusterMetricsDao.getClusterMetricsAggregatedLastHour(to, clusterId);

        logger.info("Computing metrics per hour over last hour");
        List<EMRTimeSeriesReportVO> hourlyMetrics =
                emrClusterMetricsDao.getClusterMetricsTimeSeriesLastHour(to, clusterId);

        //Set the time-series if the list is not empty, else return an empty one
        if (aggregatedMetrics.size() > 0) {
            aggregatedMetrics.get(0).setTimeSeriesMetrics(hourlyMetrics);
        }

        model.setEmrClusterMetricsReport(aggregatedMetrics);
        return model;
    }


    /**
     * Get both aggregated and per-hour metrics for the given cluster over past week. Each hour
     * contains metrics averaged across that hour for every day of the week
     *
     * @param to        get metrics for last week going back in time from this timestamp
     * @param clusterId get metrics for this cluster
     * @return the aggregated and per-hour metrics plus cluster metadata
     */
    public EMRClusterMetricsModel getClusterMetricsLastWeek(Timestamp to, String clusterId) {
        logger.info("Aggregating metrics over last week");
        List<EMRClusterMetricsVO> aggregatedMetrics =
                emrClusterMetricsDao.getClusterMetricsAggregatedLastWeek(to, clusterId);

        logger.info("Computing metrics per hour over last week");
        List<EMRTimeSeriesReportVO> weeklyMetrics =
                emrClusterMetricsDao.getClusterMetricsHourlyLastWeek(to, clusterId);

        //Set the time-series if the list is not empty, else return an empty one
        if (aggregatedMetrics.size() > 0) {
            aggregatedMetrics.get(0).setTimeSeriesMetrics(weeklyMetrics);
        }

        model.setEmrClusterMetricsReport(aggregatedMetrics);
        return model;
    }


    /**
     * Get both aggregated and per-hour metrics for the given cluster over past month. Each hour
     * contains metrics averaged across that hour for every day of the month
     *
     * @param to        get metrics for last month going back in time from this timestamp
     * @param clusterId get metrics for this cluster
     * @return the aggregated and per-hour metrics plus cluster metadata
     */
    public EMRClusterMetricsModel getClusterMetricsLastMonth(Timestamp to, String clusterId) {
        logger.info("Aggregating metrics over last month");
        List<EMRClusterMetricsVO> aggregatedMetrics =
                emrClusterMetricsDao.getClusterMetricsAggregatedLastMonth(to, clusterId);

        logger.info("Computing metrics per hour over last month");
        List<EMRTimeSeriesReportVO> monthlyMetrics =
                emrClusterMetricsDao.getClusterMetricsHourlyLastMonth(to, clusterId);

        //Set the time-series if the list is not empty, else return an empty one
        if (aggregatedMetrics.size() > 0) {
            aggregatedMetrics.get(0).setTimeSeriesMetrics(monthlyMetrics);
        }

        model.setEmrClusterMetricsReport(aggregatedMetrics);
        return model;
    }


    /**
     * Get aggregated and per hour metrics for given time period. For time periods spanning longer
     * than 24 hours, each hour contains metrics averaged across that hour for every day of the period
     *
     * @param clusterId get metrics for this cluster
     * @param from      get metrics for period starting from this timestamp
     * @param to        get metrics for period up until this timestamp
     * @return the aggregated and per-hour metrics plus cluster metadata
     */
    public EMRClusterMetricsModel getClusterMetricsCustomPeriod(String clusterId, String from, String to) {
        //Converting String Time to actual Timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ZonedDateTime fromDateTime =
                LocalDateTime.parse(from, formatter).atZone(ZoneId.of("America/Los_Angeles"));

        ZonedDateTime toDateTime =
                LocalDateTime.parse(to, formatter).atZone(ZoneId.of("America/Los_Angeles"));
        Timestamp start = Timestamp.valueOf(fromDateTime.toLocalDateTime());
        Timestamp end = Timestamp.valueOf(toDateTime.toLocalDateTime());

        logger.info("Aggregating metrics from " + start + " to " + end);
        List<EMRClusterMetricsVO> aggregatedMetrics =
                emrClusterMetricsDao.getClusterMetricsAggregatedForPeriod(clusterId, start, end);

        List<EMRTimeSeriesReportVO> customRangeMetrics;
        if (end.getTime() - start.getTime() > 24 * 60 * 60 * 1000L) {
            customRangeMetrics = emrClusterMetricsDao.getClusterMetricsHourlyMultiDay(clusterId, start, end);
        } else {
            customRangeMetrics = emrClusterMetricsDao.getClusterMetricsHourlyOneDayOrLess(clusterId, start, end);
        }

        //Set the time-series if the list is not empty, else return an empty one
        if (aggregatedMetrics.size() > 0) {
            aggregatedMetrics.get(0).setTimeSeriesMetrics(customRangeMetrics);
        }

        model.setEmrClusterMetricsReport(aggregatedMetrics);
        return model;
    }

    public EMRAppsModel getCompletedAppsHelper(String clusterId, String status, String forLast, String from, String to) {
        //Doing time-stamp conversion based on the forLast use-case like latest,day,hour etc.
        long now = System.currentTimeMillis();
        long nowToNearestHour = now - now % (60 * 60 * 1000);

        logger.info("Getting all " + status + " apps for last " + forLast + " for cluster " + clusterId);

        long finishedTimeBegin;
        long finishedTimeEnd = -1;
        switch (forLast.toLowerCase()) {
            case "latest":
                finishedTimeBegin = now - 10 * 60 * 1000;
            case "hour":
                finishedTimeBegin = now - 60 * 60 * 1000;
                break;
            case "day":
                finishedTimeBegin = nowToNearestHour - 24 * 60 * 60 * 1000L;
                break;
            case "week":
                finishedTimeBegin = nowToNearestHour - 7 * 24 * 60 * 60 * 1000L;
                break;
            case "month":
                Calendar c = Calendar.getInstance();
                c.setTime(new Timestamp(nowToNearestHour));
                c.add(Calendar.MONTH, -1);
                finishedTimeBegin = c.getTime().getTime();
                break;
            case "custom_range":
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                ZonedDateTime fromDateTime =
                        LocalDateTime.parse(from, formatter).atZone(ZoneId.of("America/Los_Angeles"));

                ZonedDateTime toDateTime =
                        LocalDateTime.parse(to, formatter).atZone(ZoneId.of("America/Los_Angeles"));

                finishedTimeBegin = fromDateTime.toEpochSecond() * 1000L;
                finishedTimeEnd = toDateTime.toEpochSecond() * 1000L;
                break;
            default:
                String message = "Invalid time parameter " + forLast +
                        " must be one of: hour, day, week, month, latest, custom_range";
                logger.error(message);
                throw new QuickFabricClientException(message);
        }

        //Fetching the applications for above calculated time period based on status: SUCCEEDED/FAILED
        switch (status.toUpperCase()) {
            case "SUCCEEDED":
                return getSucceededApps(clusterId, finishedTimeBegin, finishedTimeEnd);
            case "FAILED":
                return getFailedApps(clusterId, finishedTimeBegin, finishedTimeEnd);
            default:
                String message = "Invalid status " + status + " must be one of: FAILED, SUCCEEDED";
                logger.error(message);
                throw new QuickFabricClientException(message);
        }
    }

    /**
     * Returns all apps from Hadoop resource manager apps API that succeeded in the given period for
     * this cluster
     *
     * @param clusterId         the cluster to get apps information from
     * @param finishedTimeBegin get apps that finished after this time, specified in ms since epoch
     * @param finishedTimeEnd   get apps that finished before this time, specified in ms since epoch
     * @return model containing apps details and cluster ID
     */
    private EMRAppsModel getSucceededApps(String clusterId, long finishedTimeBegin, long finishedTimeEnd) {
        return getApps(clusterId, null, "SUCCEEDED", finishedTimeBegin, finishedTimeEnd);
    }


    /**
     * Returns all apps from Hadoop resource manager apps API that failed in the given period for
     * this cluster
     *
     * @param clusterId         the cluster to get apps information from
     * @param finishedTimeBegin get apps that failed after this time, specified in ms since epoch
     * @param finishedTimeEnd   get apps that failed before this time, specified in ms since epoch
     * @return model containing apps details and cluster ID
     */
    private EMRAppsModel getFailedApps(String clusterId, long finishedTimeBegin, long finishedTimeEnd) {
        return getApps(clusterId, null, "FAILED", finishedTimeBegin, finishedTimeEnd);
    }


    /**
     * Get all apps currently running for this cluster
     *
     * @param clusterId the cluster to get apps information from
     * @return model containing apps details and cluster ID
     */
    public EMRAppsModel getRunningApps(String clusterId) {
        return getApps(clusterId, "RUNNING", null, -1, -1);
    }


    /**
     * get Apps using Hadoop resource manager apps API. Query parameters combined conjunctively with AND
     *
     * @param clusterId         the cluster to get apps information from
     * @param state             one of: NEW, NEW_SAVING, SUBMITTED, ACCEPTED, RUNNING, FINISHED, FAILED, KILLED
     * @param finalStatus       one of: UNDEFINED, SUCCEEDED, FAILED, KILLED
     * @param finishedTimeBegin get apps that finished after this time, specified in ms since epoch. defaults to 0
     * @param finishedTimeEnd   get apps that finished before this time, specified in ms since epoch
     * @return model containing apps details and cluster ID
     */
    public EMRAppsModel getApps(String clusterId, String state, String finalStatus,
                                long finishedTimeBegin, long finishedTimeEnd) {

        logger.info("Getting RM Url for cluster " + clusterId);
        EMRClusterMetricsVO vo = emrClusterMetricsDao.getEMRResourceManagerUrlById(clusterId).get(0);

        ResponseEntity<String> response;
        response = caller.invokeGetRmAppsStatusService(vo.getRmUrl(), state, finalStatus, vo.getAccount(),
                finishedTimeBegin, finishedTimeEnd);


        EMRAppsModel model = new EMRAppsModel();
        model.setEmrId(vo.getEmrId());
        model.setEmrName(vo.getEmrName());
        model.setApps(processAppsResponse(response));

        return model;
    }


    /**
     * Convert payload from Hadoop resource manager apps API to list of POJO
     *
     * @param response json payload
     * @return list of objects containing apps information
     */
    private List<EMRAppVO> processAppsResponse(ResponseEntity<String> response) {

        JSONObject responseJSON;
        JSONArray appList;
        try {
            logger.info("Parsing resource managers apps API response into Java JSON object");
            responseJSON = new JSONObject(response.getBody()).getJSONObject("metricStats");
            
            // if empty, will be {"apps": null}, otherwise {"apps": {"app": [...]}}
            if(responseJSON.has("apps") && !responseJSON.isNull("apps")) {
                appList = responseJSON.getJSONObject("apps").getJSONArray("app");
            } else {
                logger.warn("EMRClusterManagementHelper->processAppsResponse no apps found.");
                appList = new JSONArray();
            }
            
        } catch (JSONException e) {
            logger.error("Failed to parse response. Reason: " + e.getMessage());
            throw new QuickFabricJsonException("Failed to parse apps API response Json. Reason: ", e);
        }

        logger.info("processAppsResponse -> Deserializing JSON response to List<VO>");

        List<EMRAppVO> apps = new ArrayList<>();
        for (int i = 0; i < appList.length(); i++) {
            EMRAppVO vo = new EMRAppVO();

            try {
                JSONObject app = appList.getJSONObject(i);
                vo.setApplicationId(app.getString("id"));
                vo.setApplicationName(app.getString("name"));
                vo.setApplicationType(app.getString("applicationType"));

                if (app.getString("finalStatus").equalsIgnoreCase("UNDEFINED")) {
                    vo.setStatus(AppStatus.valueOf("RUNNING"));
                } else {
                    vo.setStatus(AppStatus.valueOf(app.getString("finalStatus")));
                }


                vo.setUser(app.getString("user"));
                vo.setStartTimestamp(new Timestamp(app.getLong("startedTime")));

                if (app.getLong("finishedTime") > 0) {
                    LocalDateTime finishedTime = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(app.getLong("finishedTime")),
                            ZoneId.of("America/Los_Angeles"));

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");

                    vo.setFinishedTimestamp(finishedTime.format(formatter));
                } else {
                    vo.setFinishedTimestamp("N/A");
                }

                vo.setElapsedTime(app.getLong("elapsedTime") / 60 / 1000L);
                vo.setClusterUsagePercentage(Math.round(app.getDouble("clusterUsagePercentage") * 100.0) / 100.0F);
                vo.setProgress(Math.round(app.getDouble("progress") * 100.0) / 100.0F);
                vo.setAllocatedMB(app.getLong("allocatedMB"));
                vo.setAllocatedVCores(app.getLong("allocatedVCores"));
                vo.setRunningContainers(app.getLong("runningContainers"));

            } catch (JSONException e) {
                logger.error("Error retrieving data from app with id {} and name {}. Reason: {}",
                        vo.getApplicationId(), vo.getApplicationName(), e.getMessage());
            }

            apps.add(vo);
        }

        return apps;

    }

}