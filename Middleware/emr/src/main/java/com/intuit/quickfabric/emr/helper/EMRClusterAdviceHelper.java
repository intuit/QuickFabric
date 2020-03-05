package com.intuit.quickfabric.emr.helper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.quickfabric.commons.constants.ApplicationConstant;
import com.intuit.quickfabric.commons.exceptions.QuickFabricClientException;
import com.intuit.quickfabric.commons.exceptions.QuickFabricJsonException;
import com.intuit.quickfabric.commons.vo.AppSeverity;
import com.intuit.quickfabric.commons.vo.EMRAppVO;
import com.intuit.quickfabric.commons.vo.EMRClusterMetricsVO;
import com.intuit.quickfabric.commons.vo.EMRTimeSeriesReportVO;
import com.intuit.quickfabric.commons.vo.JobPerformanceAdviceVO;
import com.intuit.quickfabric.commons.vo.JobSchedulingAdviceVO;
import com.intuit.quickfabric.commons.vo.YarnAppHeuristicVO;
import com.intuit.quickfabric.emr.dao.EMRClusterMetricsDao;
import com.intuit.quickfabric.emr.model.EMRClusterAdviceModel;

@Component
public class EMRClusterAdviceHelper {
    
    Logger logger = LogManager.getLogger(EMRClusterAdviceHelper.class);
    
    @Autowired
    private EMRAWSServiceCallerHelper caller;

    @Autowired
    private EMRClusterMetricsDao emrClusterMetricsDao; 

    
    public JobPerformanceAdviceVO getClusterMetricsJobPerformanceAdviceHelper(String clusterId, String forLast, String from, String to) {
        //Conversion of String time parameters to time-stamps based on use-cases
        long fromInMs;
        long toInMs;

        long now = System.currentTimeMillis();
        long nowToNearestHour = now - now % (60 * 60 * 1000L);
        long nowToNearestDay = now - now % (24 * 60 * 60 * 1000L);

        switch (forLast.toLowerCase()) {
            case "hour":
                fromInMs = now - 60 * 60 * 1000L;
                toInMs = now;
                break;
            case "day":
                fromInMs = nowToNearestHour - 24 * 60 * 60 * 1000L;
                toInMs = nowToNearestHour;
                break;
            case "week":
                fromInMs = nowToNearestDay - 7 * 24 * 60 * 60 * 1000L;
                toInMs = nowToNearestDay;
                break;
            case "month":
                Calendar c = Calendar.getInstance();
                c.setTime(new Timestamp(nowToNearestDay));
                c.add(Calendar.MONTH, -1);
                fromInMs = c.getTime().getTime();
                toInMs = nowToNearestDay;
                break;
            case "custom_range":
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                ZonedDateTime fromDateTime =
                        LocalDateTime.parse(from, formatter).atZone(ZoneId.of("America/Los_Angeles"));

                ZonedDateTime toDateTime =
                        LocalDateTime.parse(to, formatter).atZone(ZoneId.of("America/Los_Angeles"));

                fromInMs = fromDateTime.toEpochSecond() * 1000L;
                toInMs = toDateTime.toEpochSecond() * 1000L;
                break;
            default:
                String msg = "Invalid time parameter " + forLast +
                        ". must be one of: hour, day, week, month, custom_range";
                logger.error(msg);
                throw new QuickFabricClientException(msg);
        }
        
        //Fetching Performance Advice from Dr Elephant        
        return getJobPerformanceAdvice(clusterId, fromInMs, toInMs);
    }
    
    public JobSchedulingAdviceVO getClusterMetricsSchedulingAdviceHelper(String clusterId, String forLast) {
        //Fetching Job Scheduling advice based on the use-case: daily, weekly, monthly
        long now = System.currentTimeMillis();
        Timestamp nowToNearestHour = new Timestamp(now - now % (60 * 60 * 1000));
        Timestamp nowToNearestDay = new Timestamp(now - now % (24 * 60 * 60 * 1000));

        JobSchedulingAdviceVO schedulingAdvice;
        switch (forLast.toLowerCase()) {
            case "day":
                schedulingAdvice = getSchedulingAdvice24Hours(clusterId, nowToNearestHour);
                break;
            case "week":
                schedulingAdvice = getSchedulingAdviceWeek(clusterId, nowToNearestDay);
                break;
            case "month":
                schedulingAdvice = getSchedulingAdviceMonth(clusterId, nowToNearestDay);
                break;
            default:
                String msg = "Invalid time parameter " + forLast +
                        ". must be one of: day, week, month";
                logger.error(msg);
                throw new QuickFabricClientException(msg);
        }
        return schedulingAdvice;
    }
    
    public EMRClusterAdviceModel getClusterMetricsExpertAdviceHelper(String clusterId, String forLast, String from, String to) {
        EMRClusterAdviceModel model = new EMRClusterAdviceModel();

        model.setEmrId(clusterId);

        //Jobs Scheduling Advice is applicable only for Daily, weekly or monthly
        if (!(forLast.equalsIgnoreCase("custom_range") || forLast.equalsIgnoreCase("hour"))) {
            model.setJobSchedulingAdvice(this.getClusterMetricsSchedulingAdviceHelper(clusterId, forLast));
        }

        model.setJobPerformanceAdvice(getClusterMetricsJobPerformanceAdviceHelper(clusterId, forLast, from, to));

        return model;
    }
    
    private JobPerformanceAdviceVO getJobPerformanceAdvice(String clusterId, long from, long to) {
    	logger.info("Getting Dr Elephant URL from RDS...");
    	EMRClusterMetricsVO vo = emrClusterMetricsDao.getEMRResourceManagerUrlById(clusterId).get(0);

    	//Dr Elephant URL is same as resource manager but with different port
    	String drElephantUrl = vo.getRmUrl().replace("8088", "8087");

    	List<EMRAppVO> apps = getDrElephantReport(drElephantUrl, from, to, vo.getAccount());

    	List<EMRAppVO> criticalApps = apps.stream().filter(app -> 
    	app.getSeverity() == AppSeverity.CRITICAL).collect(Collectors.toList());

    	List<EMRAppVO> severeApps = apps.stream().filter(app -> 
    	app.getSeverity() == AppSeverity.SEVERE).collect(Collectors.toList());

    	List<EMRAppVO> moderateApps = apps.stream().filter(app -> 
    	app.getSeverity() == AppSeverity.MODERATE).collect(Collectors.toList());

    	JobPerformanceAdviceVO adviceVO = new JobPerformanceAdviceVO();

    	adviceVO.setCriticalApps(criticalApps);
    	adviceVO.setSevereApps(severeApps);
    	adviceVO.setModerateApps(moderateApps);

    	String advice;
    	if(criticalApps.size() == 0 && severeApps.size() == 0 && moderateApps.size() == 0) {
    		advice = "All of your jobs have great job performance. Way to go!";
    	} else {
    		advice = "You have jobs that could use some tuning. You have %d jobs whose performance are"
    				+ " in critical state, %d jobs in severe state, and %d jobs in moderate state.";

    		advice = String.format(advice, criticalApps.size(), severeApps.size(), moderateApps.size());
    	}

    	adviceVO.setAdvice(advice);


    	return adviceVO;      
    }

    /**
     * Give scheduling advice for applications on this cluster on an hourly basis based on last 24 hours
     * @param clusterId give advice for this cluster
     * @param nowToNearestHour 24 hours going backward from this timestamp
     * @return the advice, with the metrics used to give it
     */
    private JobSchedulingAdviceVO getSchedulingAdvice24Hours(String clusterId, Timestamp nowToNearestHour) {
        List<EMRTimeSeriesReportVO> avgRunningApps = 
                emrClusterMetricsDao.getClusterMetricsHourlyLast24Hours(nowToNearestHour, clusterId);

        // Checking for sufficient data points to generate scheduling advice
        if(avgRunningApps.size() < 3) {
            return getSchedulingAdviceInsufficientData();
        }

        String advice = buildAdviceString24Hours(avgRunningApps);

        return buildAdviceModel(clusterId, avgRunningApps, advice);

    }

    /**
     * Give scheduling advice for applications on this cluster on a daily basis based on last week
     * @param clusterId give advice for this cluster
     * @param nowToNearestHour one week going backward from this timestamp
     * @return the advice, with the metrics used to give it
     */

    private JobSchedulingAdviceVO getSchedulingAdviceWeek(String clusterId, Timestamp nowToNearestDay) {
        List<EMRTimeSeriesReportVO> avgRunningApps = 
                emrClusterMetricsDao.getClusterMetricsDailyLastWeek(nowToNearestDay, clusterId);

        // Checking for sufficient data points to generate scheduling advice
        if(avgRunningApps.size() < 3) {
            return getSchedulingAdviceInsufficientData();
        }

        String advice = buildAdviceStringWeek(avgRunningApps);

        return buildAdviceModel(clusterId, avgRunningApps, advice);
    }
    
    /**
     * Give scheduling advice for applications on this cluster on a daily basis based on last month
     * @param clusterId give advice for this cluster
     * @param nowToNearestHour one month going backward from this timestamp
     * @return the advice, with the metrics used to give it
     */

    private JobSchedulingAdviceVO getSchedulingAdviceMonth(String clusterId, Timestamp nowToNearestDay) {
        List<EMRTimeSeriesReportVO> avgRunningApps = 
                emrClusterMetricsDao.getClusterMetricsDailyLastMonth(nowToNearestDay, clusterId);

        // Checking for sufficient data points to generate scheduling advice
        if(avgRunningApps.size() < 3) {
            return getSchedulingAdviceInsufficientData();
        }

        String advice = buildAdviceStringMonth(avgRunningApps);

        return buildAdviceModel(clusterId, avgRunningApps, advice);
    }

    private JobSchedulingAdviceVO getSchedulingAdviceInsufficientData() {
        JobSchedulingAdviceVO noAdviceModel = new JobSchedulingAdviceVO();
        noAdviceModel.setAdvice("Insufficient metrics history to produce advice");
        noAdviceModel.setLeastUsed(new ArrayList<>());
        noAdviceModel.setMostUsed(new ArrayList<>());
        return noAdviceModel;
    }
    
    private JobSchedulingAdviceVO buildAdviceModel(String clusterId, List<EMRTimeSeriesReportVO> avgRunningApps,
            String advice) {
        JobSchedulingAdviceVO vo = new JobSchedulingAdviceVO();
    
        avgRunningApps.sort(new Comparator<EMRTimeSeriesReportVO>( ) {
            @Override
            public int compare(EMRTimeSeriesReportVO vo1, EMRTimeSeriesReportVO vo2) {
                return vo1.getAppsRunning() - vo2.getAppsRunning();
            }
        });
    
        vo.setAdvice(advice);
        vo.setLeastUsed(avgRunningApps.subList(0, 3));
        vo.setMostUsed(avgRunningApps.subList(avgRunningApps.size() - 3, avgRunningApps.size()));
    
        return vo;
    }

    private String buildAdviceString24Hours(List<EMRTimeSeriesReportVO> hourly) {
    
        String advice = "The hours that had the most jobs running in the last 24 hours were "
                + "%s-%s, %s-%s, and %s-%s. "
                + "If possible, consider moving some jobs that run during these hours to "
                + "%s-%s, %s-%s, or %s-%s, "
                + "which were the hours that had the least jobs running.";
    
    
        DateTimeFormatter outFormat = DateTimeFormatter.ofPattern("H:mm");
    
        return String.format(advice, 
                // the 3 most used hours
                hourly.get(hourly.size() - 1).getTime().toLocalDateTime().format(outFormat),
                hourly.get(hourly.size() - 1).getTime().toLocalDateTime().plusHours(1).format(outFormat),
                hourly.get(hourly.size() - 2).getTime().toLocalDateTime().format(outFormat),
                hourly.get(hourly.size() - 2).getTime().toLocalDateTime().plusHours(1).format(outFormat),
                hourly.get(hourly.size() - 3).getTime().toLocalDateTime().format(outFormat),
                hourly.get(hourly.size() - 3).getTime().toLocalDateTime().plusHours(1).format(outFormat),
                // the 3 least used hours
                hourly.get(0).getTime().toLocalDateTime().format(outFormat),
                hourly.get(0).getTime().toLocalDateTime().plusHours(1).format(outFormat),
                hourly.get(1).getTime().toLocalDateTime().format(outFormat),
                hourly.get(1).getTime().toLocalDateTime().plusHours(1).format(outFormat),
                hourly.get(2).getTime().toLocalDateTime().format(outFormat),
                hourly.get(2).getTime().toLocalDateTime().plusHours(1).format(outFormat));
    }

    private String buildAdviceStringMonth(List<EMRTimeSeriesReportVO> daily) {
    
        String advice = "The days that had the most jobs running in the past month were "
                + "%s, %s, and %s. "
                + "If possible, consider moving some jobs that run during these days to "
                + "%s, %s, or %s, "
                + "which were the days that had the least jobs running.";
    
        DateTimeFormatter outFormat = DateTimeFormatter.ofPattern("MM/dd");
    
        return String.format(advice,
                // the 3 most used days
                daily.get(daily.size() - 1).getTime().toLocalDateTime().format(outFormat),
                daily.get(daily.size() - 2).getTime().toLocalDateTime().format(outFormat),
                daily.get(daily.size() - 3).getTime().toLocalDateTime().format(outFormat),
                // the 3 least used days
                daily.get(0).getTime().toLocalDateTime().format(outFormat),
                daily.get(1).getTime().toLocalDateTime().format(outFormat),
                daily.get(2).getTime().toLocalDateTime().format(outFormat));
    }

    private String buildAdviceStringWeek(List<EMRTimeSeriesReportVO> daily) {
    
        String advice = "The days that had the most jobs running in the past week were "
                + "%s, %s, and %s. "
                + "If possible, consider moving some jobs that run during these days to "
                + "%s, %s, or %s, "
                + "which were the days that had the least jobs running.";
    
        //Tuesday
        DateTimeFormatter outFormat = DateTimeFormatter.ofPattern("cccc");
    
        return String.format(advice,
                // the 3 most used days
                daily.get(daily.size() - 1).getTime().toLocalDateTime().format(outFormat),
                daily.get(daily.size() - 2).getTime().toLocalDateTime().format(outFormat),
                daily.get(daily.size() - 3).getTime().toLocalDateTime().format(outFormat),
                // the 3 least used days
                daily.get(0).getTime().toLocalDateTime().format(outFormat),
                daily.get(1).getTime().toLocalDateTime().format(outFormat),
                daily.get(2).getTime().toLocalDateTime().format(outFormat));
    }

    private List<EMRAppVO> getDrElephantReport(String drElephantUrl, long from, long to, String accountId) {

        logger.info("Search Dr Elephant for jobs finished between " + 
                new Timestamp(from).toString() + " and " + new Timestamp(to).toString()); 

        ResponseEntity<String> response;
        JSONObject responseJSON;
        JSONArray apps = new JSONArray();
        
        try {
            response = caller.invokeDrElephantJobSearch(drElephantUrl, from, to, accountId);
            responseJSON = new JSONObject(response.getBody());
            apps = responseJSON.getJSONArray("metricStats");
        } catch(JSONException e) {
            String drElephantErrorMessage = "Failed to parse Dr Elephant report from API. Reason: " + e.getMessage();
            logger.error(drElephantErrorMessage);
            throw new QuickFabricJsonException(drElephantErrorMessage, e);
        }

        List<EMRAppVO> appList = new ArrayList<>();

        for(int i = 0; i < apps.length(); i++) {
            try {

                JSONObject app = apps.getJSONObject(i);

                ObjectMapper mapper = new ObjectMapper();
                EMRAppVO vo = mapper.readValue(app.toString(), EMRAppVO.class);

                vo.setYarnAppHeuristicResults(vo.getYarnAppHeuristicResults().stream().filter(h -> 
                h.getSeverity().equalsIgnoreCase("CRITICAL") || 
                h.getSeverity().equalsIgnoreCase("SEVERE") || 
                h.getSeverity().equalsIgnoreCase("MODERATE")).collect(Collectors.toList()));

                for(YarnAppHeuristicVO heuristic : vo.getYarnAppHeuristicResults()) {
                    String advice = 
                            ApplicationConstant.YARN_APP_HEURISTICS.getOrDefault(heuristic.getHeuristicName(),
                                    "No advice for this heuristic yet. Sorry!");
                    heuristic.setAdvice(advice);
                }

                appList.add(vo);
            } catch(Exception e) {
                logger.error("Failed to deserialize Dr Elephant EMR app into VO. Reason: " + e.getMessage());
            }
        }
        return appList;
    }

}
