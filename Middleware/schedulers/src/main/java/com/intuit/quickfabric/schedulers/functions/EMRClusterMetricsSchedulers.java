package com.intuit.quickfabric.schedulers.functions;

import com.intuit.quickfabric.commons.utils.CommonUtils;
import com.intuit.quickfabric.commons.utils.EnableMethod;
import com.intuit.quickfabric.commons.vo.EMRClusterMetricsVO;
import com.intuit.quickfabric.schedulers.helpers.EMRClusterMetricsHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@ConditionalOnProperty(value = "scheduling.enabled", havingValue = "true", matchIfMissing = false)
public class EMRClusterMetricsSchedulers {

	private static final Logger logger = LogManager.getLogger(EMRClusterMetricsSchedulers.class);
	
    @Autowired
    private EMRClusterMetricsHelper helper;

    /**
     *  This service gets data on EMR clusters from resource manager REST api setup behind serverless API's
     *  https://hadoop.apache.org/docs/r2.7.3/hadoop-yarn/hadoop-yarn-site/ResourceManagerRest.html
     * @throws SQLException
     * @throws JSONException 
     */

    @Scheduled(cron = "${collectEMRClusterMetricsSchedule}")
    @Async
    @EnableMethod(configName="collect_cluster_metrics_scheduler")
    public void collectEMRRegistryMetrics() {
    	try {
    		final LocalDateTime start = LocalDateTime.now();
        	logger.info("EMRClusterMetricsSchedulers->collectEMRRegistryMetrics starting at " + start);
        	List<EMRClusterMetricsVO> clusterMetrics = helper.getClusterMetrics();
    		helper.updateEMRClusterMetricsList(clusterMetrics);
        	logger.info("EMRClusterMetricsSchedulers->collectEMRRegistryMetrics completed");
    	} catch (Exception e) {
            CommonUtils.logErrorResponse(e);   
    	}

    }

    @Scheduled(cron = "${generateDailyReportSchedule}")
    @Async
    @EnableMethod(configName="daily_report_scheduler")
    public void generateDailyReport() {
    	try {
	        final LocalDateTime start = LocalDateTime.now();
	        logger.info("EMRClusterMetricsSchedulers->generateDailyReport starting at " + start);
	        helper.dailyReport();  
	        logger.info("EMRClusterMetricsSchedulers->generateDailyReport completed");
    	} catch (Exception e) {
            CommonUtils.logErrorResponse(e);   
    	}
    }

    @Scheduled(cron = "${generateWeeklyReportSchedule}")
    @Async
    @EnableMethod(configName="weekly_report_scheduler")
    public void generateWeeklyReport() {
    	try {
	        final LocalDateTime start = LocalDateTime.now();
	        logger.info("EMRClusterMetricsSchedulers->generateWeeklyReport starting at " + start);
	        helper.weeklyReport(); 
	        logger.info("EMRClusterMetricsSchedulers->generateWeeklyReport completed");
    	} catch (Exception e) {
            CommonUtils.logErrorResponse(e);   
    	}
    }

    @Scheduled(cron = "${generateMonthlyReportSchedule}")
    @Async
    @EnableMethod(configName="monthly_report_scheduler")
    public void generateMonthlyReport() {
    	try {
	        final LocalDateTime start = LocalDateTime.now();
	        logger.info("EMRClusterMetricsSchedulers->generateMonthlyReport starting at " + start);
	        helper.monthlyReport();
	        logger.info("EMRClusterMetricsSchedulers->generateMonthlyReport completed");
    	} catch (Exception e) {
            CommonUtils.logErrorResponse(e);   
    	}
    }
    
    @Scheduled(cron = "${segmentReportsSchedule}")
    @Async
    @EnableMethod(configName="segment_reports_scheduler")
    public void sendSegmentReports() {
    	try {
	        final LocalDateTime start = LocalDateTime.now();
	        logger.info("EMRClusterMetricsSchedulers->sendSegmentReports starting at " + start);
	        helper.sendSegmentReports();
	        logger.info("EMRClusterMetricsSchedulers->sendSegmentReports completed");
    	} catch (Exception e) {
            CommonUtils.logErrorResponse(e);   
    	}
    }
    
    
    //NOTE: this will probably deprecate "sendSegmentReports"
    @Scheduled(cron = "${subscriptionReportsSchedule}")
    @Async
    @EnableMethod(configName="subscription_reports_scheduler")
    public void sendSubscriptionReports() {
    	try {
	        final LocalDateTime start = LocalDateTime.now();
	        logger.info("EMRClusterMetricsSchedulers->sendSubscriptionReports starting at {}", start);
	        helper.sendSubscriptionReports();
	        logger.info("EMRClusterMetricsSchedulers->sendSubscriptionReports completed");
    	} catch (Exception e) {
            CommonUtils.logErrorResponse(e);   
    	}
    }
    
    @Scheduled(cron = "${collectEMRClusterCostsSchedule}")
    @Async
    @EnableMethod(configName="collect_cluster_costs")
    public void collectEMRClusterCosts() {
    	
    	try {
    		final LocalDateTime start = LocalDateTime.now();
        	logger.info("EMRClusterMetricsSchedulers->collectEMRClusterCosts starting at " + start);
    		helper.collectEMRClusterCost();
        	logger.info("EMRClusterMetricsSchedulers->collectEMRClusterCosts completed");
    	} catch(Exception e) {
    		CommonUtils.logErrorResponse(e); 
    	}

    }

}