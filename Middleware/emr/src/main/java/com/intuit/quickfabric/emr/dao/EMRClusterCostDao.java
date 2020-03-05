package com.intuit.quickfabric.emr.dao;

import java.sql.Timestamp;
import java.util.List;

import com.intuit.quickfabric.commons.vo.EMRGroupCostVO;
import com.intuit.quickfabric.commons.vo.EMRTimeSeriesReportVO;

public interface EMRClusterCostDao {
    
    /**
     * Get the cost of the cluster up until each day for the last week (cost monotonically increases
     * until it resets to 0 at start of each month)
     * @param clusterId the cluster to get the cost history of
     * @param to the last week up until this timestamp
     * @return the cost history
     */
    List<EMRTimeSeriesReportVO> getClusterCostWeek(String clusterId, Timestamp to);
    
    /**
     * Get the cost of the cluster up until each day for the last month (cost monotonically increases
     * until it resets to 0 at start of each month)
     * @param clusterId the cluster to get the cost history of
     * @param to the last month up until this timestamp
     * @return the cost history
     */
    List<EMRTimeSeriesReportVO> getClusterCostMonth(String clusterId, Timestamp to);

    /**
     * Get the cost of the cluster up until each day for the given period (cost monotonically increases
     * until it resets to 0 at start of each month)
     * @param clusterId the cluster to get the cost history of
     * @param from get cost starting from this timestamp
     * @param to get cost until this timestamp
     * @return the cost history
     */
    List<EMRTimeSeriesReportVO> getClusterCostForPeriod(String clusterId, Timestamp from, Timestamp to);

    List<EMRGroupCostVO> getEMRGroupCost(String account, String segment, Timestamp from);

}
