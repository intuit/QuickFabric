package com.intuit.quickfabric.emr.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.EMRTimeSeriesReportVO;

public class EMRClusterMetricsMultipleDaysHourlyMapper implements ResultSetExtractor<List<EMRTimeSeriesReportVO>> {
    public List<EMRTimeSeriesReportVO> extractData(ResultSet rs) throws SQLException,
    DataAccessException {

    List<EMRTimeSeriesReportVO> hourlyMetrics = new ArrayList<>();
    while (rs.next()) { 
        
        EMRTimeSeriesReportVO hour = new EMRTimeSeriesReportVO();
        hour.setHourOfDay(rs.getInt("hr_of_day"));
        hour.setAvgCoresUsagePct(rs.getFloat("avg_cores_usage_pct"));
        hour.setAvgMemoryUsagePct(rs.getFloat("avg_memory_usage_pct"));
        hour.setAppsSucceeded(rs.getInt("avg_apps_succeeded"));
        hour.setAppsFailed(rs.getInt("avg_apps_failed"));
        hour.setAppsRunning(rs.getInt("avg_apps_running"));
        hour.setAppsPending(rs.getInt("avg_apps_pending"));
         
        addHour(hourlyMetrics, hour);
    }
    
    return hourlyMetrics;
}

/**
 * Sorted insert. Does not assume data for all 24 hours in a day.
 * @param hourlyMetrics a sorted list of metrics per hour for this EMR
 * @param hour a new hour's worth of metrics
 */
private void addHour(List<EMRTimeSeriesReportVO> hourlyMetrics, EMRTimeSeriesReportVO hour) {
    for(int i = 0; i < hourlyMetrics.size(); i++) {
        if (hour.getHourOfDay() < (hourlyMetrics.get(i).getHourOfDay())) {
            hourlyMetrics.add(i, hour);
            return;
        }
    }  
    
    hourlyMetrics.add(hour);
    
}

}
