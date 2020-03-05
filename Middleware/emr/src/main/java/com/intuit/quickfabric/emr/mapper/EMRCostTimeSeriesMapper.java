package com.intuit.quickfabric.emr.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.EMRTimeSeriesReportVO;

public class EMRCostTimeSeriesMapper implements ResultSetExtractor<List<EMRTimeSeriesReportVO>> {

     
    public List<EMRTimeSeriesReportVO> extractData(ResultSet rs) throws SQLException,
        DataAccessException {

        List<EMRTimeSeriesReportVO> timeSeries = new ArrayList<>();
        while (rs.next()) { 

            EMRTimeSeriesReportVO entry = new EMRTimeSeriesReportVO();
            entry.setTime(rs.getTimestamp("ts"));
            entry.setCost(rs.getInt("cost"));
            
                   
            addEntry(timeSeries, entry);
        }
        
        return timeSeries;
    }
    
    /**
     * @param timeSeries a sorted list of time series metrics for this EMR
     * @param entry a new entry in this time series
     */
    private void addEntry(List<EMRTimeSeriesReportVO> timeSeries, EMRTimeSeriesReportVO entry) {
        for(int i = 0; i < timeSeries.size(); i++) {
            if (entry.getTime().before(timeSeries.get(i).getTime())) {
                timeSeries.add(i, entry);
                return;
            }
        }  
        
        timeSeries.add(entry);
        
    }
}
