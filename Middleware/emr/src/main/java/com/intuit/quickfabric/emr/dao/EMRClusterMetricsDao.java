package com.intuit.quickfabric.emr.dao;

import java.sql.Timestamp;
import java.util.List;

import com.intuit.quickfabric.commons.vo.EMRClusterMetricsVO;
import com.intuit.quickfabric.commons.vo.EMRTimeSeriesReportVO;

public interface EMRClusterMetricsDao {
	List<EMRClusterMetricsVO> getEMRClusterMetricsList(String clusterType, String account);
	
	/**
	 * Get per hour metrics for this cluster over  24 hour period
	 * @param to get metrics for last 24 hours going back in time from this timestamp
	 * @param clusterId get metrics for this cluster
	 * @return 24 records where each represents metrics for one hour in the day
	 */
    List<EMRTimeSeriesReportVO> getClusterMetricsHourlyLast24Hours(Timestamp to, String clusterId);
    
    
    /**
     * Get aggregated metrics for this cluster over 24 hour period
     * @param to get metrics for last 24 hours going back in time from this timestamp
     * @param clusterId get metrics for this cluster
     * @return ONE object containing the aggregated metrics over the 24 hour period
     */   
    List<EMRClusterMetricsVO> getClusterMetricsAggregatedLast24Hours(Timestamp to, String clusterId);
    
    /**
     * Get time series metrics for this cluster over last hour
     * @param to get metrics for last  hour going back in time from this timestamp
     * @param clusterId get metrics for this cluster
     * @return approximately 5-6 records where each represents metrics at a given point in time during this hour
     */
    List<EMRTimeSeriesReportVO> getClusterMetricsTimeSeriesLastHour(Timestamp to, String clusterId);
    
    
    /**
     * Get aggregated metrics for this cluster over last hour 
     * @param to get metrics for last hour going back in time from this timestamp
     * @param clusterId get metrics for this cluster
     * @return ONE object containing the aggregated metrics over the hour
     */
    List<EMRClusterMetricsVO> getClusterMetricsAggregatedLastHour(Timestamp to, String clusterId);
    
    
    /**
     * Get per hour metrics for this cluster over  week. Each hour
     * contains metrics averaged across that hour for every day of the week
     * @param to get metrics for last week going back in time from this timestamp
     * @param clusterId get metrics for this cluster
     * @return 24 records where each represents metrics for one hour in the day
     */
    List<EMRTimeSeriesReportVO> getClusterMetricsHourlyLastWeek(Timestamp to, String clusterId);

    
    /**
     * Get aggregated metrics for this cluster over week
     * @param to get metrics for last week going back in time from this timestamp
     * @param clusterId get metrics for this cluster
     * @return ONE object containing the aggregated metrics over the week
     */
    List<EMRClusterMetricsVO> getClusterMetricsAggregatedLastWeek(Timestamp to, String clusterId);
    
    /**
     * Get per hour metrics for this cluster over  week. Each hour
     * contains metrics averaged across that hour for every day of the week
     * @param to get metrics for last month going back in time from this timestamp
     * @param clusterId get metrics for this cluster
     * @return 24 records where each represents metrics for one hour in the day
     */
    List<EMRTimeSeriesReportVO> getClusterMetricsHourlyLastMonth(Timestamp to, String clusterId);
    
    
    /**
     * Get aggregated metrics for this cluster over month
     * @param to get metrics for last month going back in time from this timestamp
     * @param clusterId get metrics for this cluster
     * @return ONE object containing the aggregated metrics over the last month
     */
    List<EMRClusterMetricsVO> getClusterMetricsAggregatedLastMonth(Timestamp to, String clusterId);
    
    
    /**
     * Get aggregated metrics for this cluster over given period
     * @param clusterId get metrics for this cluster
     * @param from get metrics for period starting at this timestamp
     * @param to get metrics for period ending at this timestamp
     * @return ONE object containing the aggregated metrics over the 24 hour period
     */
    List<EMRClusterMetricsVO> getClusterMetricsAggregatedForPeriod(String clusterId, Timestamp from, Timestamp to);

    
    /**
     * Get per hour metrics for this cluster over given period. Each hour contains metrics averaged
     * across that hour for every day of the period
     * @param clusterId get metrics for this cluster
     * @param from get metrics for period starting at this timestamp
     * @param to get metrics for period ending at this timestamp
     * @return 24 records where each represents metrics for one hour in the day
     */
    List<EMRTimeSeriesReportVO> getClusterMetricsHourlyMultiDay(String clusterId, Timestamp from, Timestamp to);

    /**
     * Get per hour metrics for this cluster over given period
     * @param clusterId get metrics for this cluster
     * @param from get metrics for period starting at this timestamp
     * @param to get metrics for period ending at this timestamp
     * @return a list of records where each represents metrics for one hour in the day
     */
    List<EMRTimeSeriesReportVO> getClusterMetricsHourlyOneDayOrLess(String clusterId, Timestamp from, Timestamp to);
    
    
    /** Returns EMR ID, EMR name, and resource manager URL for given cluster
     * 
     * @param id cluster ID to get information from
     * @return EMRClusterMetricsVO with the id, name, and resource manager url
     */
    List<EMRClusterMetricsVO> getEMRResourceManagerUrlById(String id);

    
    /**
     * Get per day metrics for this cluster over past month
     * @param clusterId get metrics for this cluster
     * @param to get metrics for last week going back in time from this timestamp
     * @return 7 objects where each object represents a day
     */
    List<EMRTimeSeriesReportVO> getClusterMetricsDailyLastWeek(Timestamp to, String clusterId);
    
    
    /**
     * Get per day metrics for this cluster over past month
     * @param clusterId get metrics for this cluster
     * @param to get metrics for last month going back in time from this timestamp
     * @return a list where each object represents a day
     */
    List<EMRTimeSeriesReportVO> getClusterMetricsDailyLastMonth(Timestamp to, String clusterId);

}
