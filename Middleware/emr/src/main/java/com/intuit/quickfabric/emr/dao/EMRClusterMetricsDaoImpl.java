package com.intuit.quickfabric.emr.dao;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.intuit.quickfabric.commons.vo.EMRClusterMetricsVO;
import com.intuit.quickfabric.commons.vo.EMRTimeSeriesReportVO;
import com.intuit.quickfabric.emr.mapper.EMRClusterMetricsAggregateMapper;
import com.intuit.quickfabric.emr.mapper.EMRClusterMetricsMapper;
import com.intuit.quickfabric.emr.mapper.EMRClusterMetricsMultipleDaysHourlyMapper;
import com.intuit.quickfabric.emr.mapper.EMRClusterMetricsRmUrlMapper;
import com.intuit.quickfabric.emr.mapper.EMRClusterMetricsTimeSeriesMapper;

@Component
public class EMRClusterMetricsDaoImpl implements EMRClusterMetricsDao {

    private static final Logger logger = LogManager.getLogger(EMRClusterMetricsDaoImpl.class);

    @Autowired
    NamedParameterJdbcTemplate  namedJdbcTemplateObject;

    public List<EMRClusterMetricsVO> getEMRClusterMetricsList(String clusterType, String account) {
        String sql = "SELECT  t1.*, t2.emr_cost FROM cluster_metrics t1"+
                    " LEFT JOIN emr_billing_component_cost t2 ON t1.emr_name = t2.emr_name";
        MapSqlParameterSource namedParams = new MapSqlParameterSource();
        
        //this is awkward, but the simplest way to add extra filters on the fly by joining with AND
        if(!clusterType.equals("all") || !account.equals("all")) {
            sql = sql + " WHERE TRUE";
        }
        
        if(!clusterType.equals("all")) {
            sql = sql + " AND t1.emr_name LIKE :cluster_type ";
            namedParams.addValue("cluster_type", "%" + clusterType + "%");
        }
        
        if(!account.equals("all")) {
            sql = sql + " AND t1.account = :account ";
            namedParams.addValue("account", account);
        }
       
        logger.info("SQL::{}, params: {}", sql, namedParams.getValues());

        List<EMRClusterMetricsVO> list = 
                namedJdbcTemplateObject.query(sql, namedParams, new EMRClusterMetricsMapper());

        return list;
    }

    @Override
    public List<EMRTimeSeriesReportVO> getClusterMetricsTimeSeriesLastHour(Timestamp date, String clusterId) {

        String query = "SELECT refresh_timestamp ts, memory_usage_pct, cores_usage_pct, " +
                "apps_succeeded,apps_failed," +
                "apps_running, apps_pending " +
                "FROM cluster_metrics_history h " + 
                "WHERE h.refresh_timestamp < :timestamp AND " +
                "h.refresh_timestamp >= DATE_ADD(:timestamp, INTERVAL -1 HOUR) " + 
                "AND emr_id = :cluster_id";

        MapSqlParameterSource namedParams = new MapSqlParameterSource();

        namedParams.addValue("timestamp", date);
        namedParams.addValue("cluster_id", clusterId);

        logger.info("SQL:: " + query);

        List<EMRTimeSeriesReportVO> metrics = 
                namedJdbcTemplateObject.query(query, namedParams, new EMRClusterMetricsTimeSeriesMapper());

        return metrics;
    }

    @Override
    public List<EMRClusterMetricsVO> getClusterMetricsAggregatedLastHour(Timestamp to, String clusterId) {
        Timestamp from = new Timestamp(to.getTime() - 60*60*1000L);
        return getClusterMetricsAggregatedForPeriod(clusterId, from, to);
    }

    @Override
    public List<EMRClusterMetricsVO> getClusterMetricsAggregatedLast24Hours(Timestamp to, String clusterId) {
        Timestamp from = new Timestamp(to.getTime() - 24*60*60*1000L);
        return getClusterMetricsAggregatedForPeriod(clusterId, from, to);
    }

    @Override
    public List<EMRClusterMetricsVO> getClusterMetricsAggregatedLastWeek(Timestamp to, String clusterId) {
        Timestamp from = new Timestamp(to.getTime() - 7*24*60*60*1000L);
        return getClusterMetricsAggregatedForPeriod(clusterId, from, to);
    }

    @Override
    public List<EMRClusterMetricsVO> getClusterMetricsAggregatedLastMonth(Timestamp to, String clusterId) {
        Calendar c = Calendar.getInstance(); 
        c.setTime(to); 
        c.add(Calendar.MONTH, -1);
        Timestamp from = new Timestamp(c.getTime().getTime());
        return getClusterMetricsAggregatedForPeriod(clusterId, from, to);
    }

    @Override
    public List<EMRTimeSeriesReportVO> getClusterMetricsHourlyLast24Hours(Timestamp to, String clusterId) {
        Timestamp from = new Timestamp(to.getTime() - 24*60*60*1000L);
        return getClusterMetricsHourlyOneDayOrLess(clusterId, from, to);
    }

    @Override
    public List<EMRTimeSeriesReportVO> getClusterMetricsHourlyLastWeek(Timestamp to, String clusterId) {
        Timestamp from = new Timestamp(to.getTime() - 7*24*60*60*1000L);
        return getClusterMetricsHourlyMultiDay(clusterId, from, to);
    }

    @Override
    public List<EMRTimeSeriesReportVO> getClusterMetricsHourlyLastMonth(Timestamp to, String clusterId) {
        Calendar c = Calendar.getInstance(); 
        c.setTime(to); 
        c.add(Calendar.MONTH, -1);
        Timestamp from = new Timestamp(c.getTime().getTime());
        return getClusterMetricsHourlyMultiDay(clusterId, from, to);
    }

    @Override
    public List<EMRClusterMetricsVO> getClusterMetricsAggregatedForPeriod(String clusterId, Timestamp from, Timestamp to) {
        String query = "SELECT " + 
                "    h.emr_id, " + 
                "    h.emr_name, " + 
                "    ROUND(AVG(memory_usage_pct), 2) memory_usage_pct, " + 
                "    ROUND(AVG(cores_usage_pct), 2) cores_usage_pct, " + 
                "    SUM(apps_succeeded) apps_succeeded, " + 
                "    SUM(apps_failed) apps_failed, " + 
                "    ROUND(AVG(apps_running)) apps_running, " + 
                "    ROUND(AVG(apps_pending)) apps_pending, " + 
                "    emr_cost emr_cost, " + 
                "    ROUND(AVG(containers_pending)) containers_pending, " + 
                "    ROUND(AVG(total_nodes)) total_nodes " + 
                "FROM " + 
                "    cluster_metrics_history h " + 
                "        LEFT JOIN " + 
                "    emr_billing_component_cost c ON c.emr_name = h.emr_name " + 
                "WHERE " + 
                "    h.refresh_timestamp >= :from " + 
                "        AND h.refresh_timestamp < :to " + 
                "        AND emr_id = :cluster_id " +
                "GROUP BY h.emr_id, h.emr_name";

        MapSqlParameterSource namedParams = new MapSqlParameterSource();

        namedParams.addValue("from", from);
        namedParams.addValue("to", to);
        namedParams.addValue("cluster_id", clusterId);

        logger.info("Params: " + namedParams);

        logger.info("SQL:: " + query);

        List<EMRClusterMetricsVO> metrics = 
                namedJdbcTemplateObject.query(query, namedParams, new EMRClusterMetricsAggregateMapper());

        return metrics;
    }

    @Override
    public List<EMRTimeSeriesReportVO> getClusterMetricsHourlyMultiDay(String clusterId, Timestamp from, Timestamp to) {
        String query = "SELECT  " + 
                "    emr_id, " + 
                "    emr_name, " + 
                "    HOUR(hr) hr_of_day, " + 
                "    ROUND(AVG(avg_memory_usage_pct), 2) avg_memory_usage_pct, " + 
                "    ROUND(AVG(avg_cores_usage_pct), 2) avg_cores_usage_pct, " + 
                "    ROUND(AVG(apps_succeeded)) avg_apps_succeeded, " + 
                "    ROUND(AVG(apps_failed)) avg_apps_failed, " + 
                "    ROUND(AVG(avg_apps_running)) avg_apps_running, " + 
                "    ROUND(AVG(avg_apps_pending)) avg_apps_pending " + 
                "FROM " + 
                "    (SELECT  " + 
                "        emr_id, " + 
                "            emr_name, " + 
                "            DATE_FORMAT(refresh_timestamp, '%Y-%m-%d %H:00:00') hr, " + 
                "            AVG(memory_usage_pct) avg_memory_usage_pct, " + 
                "            AVG(cores_usage_pct) avg_cores_usage_pct, " + 
                "            SUM(apps_succeeded) apps_succeeded, " + 
                "            SUM(apps_failed) apps_failed, " + 
                "            AVG(apps_running) avg_apps_running, " + 
                "            AVG(apps_pending) avg_apps_pending " + 
                "    FROM " + 
                "        cluster_metrics_history h " + 
                "    WHERE " + 
                "        h.refresh_timestamp >= :from " + 
                "            AND h.refresh_timestamp < :to " + 
                "            AND emr_id = :cluster_id " + 
                "    GROUP BY emr_id , emr_name , hr) AS t1 " + 
                "GROUP BY emr_id , emr_name, hr_of_day";




        MapSqlParameterSource namedParams = new MapSqlParameterSource();

        namedParams.addValue("from", from);
        namedParams.addValue("to", to);
        namedParams.addValue("cluster_id", clusterId);

        logger.info("SQL:: " + query);

        List<EMRTimeSeriesReportVO> metrics = 
                namedJdbcTemplateObject.query(query, namedParams, new EMRClusterMetricsMultipleDaysHourlyMapper());

        return metrics;
    }

    @Override
    public List<EMRTimeSeriesReportVO> getClusterMetricsHourlyOneDayOrLess(String clusterId, Timestamp from, Timestamp to) {
        String query = "SELECT emr_id, emr_name, DATE_FORMAT(refresh_timestamp,'%Y-%m-%d %H:00:00') ts, " +
                "AVG(memory_usage_pct) memory_usage_pct, " +
                "AVG(cores_usage_pct) cores_usage_pct, " + 
                "SUM(apps_succeeded) apps_succeeded, SUM(apps_failed) apps_failed, " + 
                "ROUND(AVG(apps_running)) apps_running, ROUND(AVG(apps_pending)) apps_pending " +
                "FROM cluster_metrics_history h " + 
                "WHERE h.refresh_timestamp >= :from AND " +
                "h.refresh_timestamp < :to " + 
                "AND emr_id = :cluster_id " +
                "GROUP BY emr_id,emr_name, ts"; 


        MapSqlParameterSource namedParams = new MapSqlParameterSource();

        namedParams.addValue("from", from);
        namedParams.addValue("to", to);
        namedParams.addValue("cluster_id", clusterId);

        logger.info("SQL:: " + query);

        List<EMRTimeSeriesReportVO> metrics = 
                namedJdbcTemplateObject.query(query, namedParams, new EMRClusterMetricsTimeSeriesMapper());

        return metrics;
    }


    @Override
    public List<EMRClusterMetricsVO> getEMRResourceManagerUrlById(String clusterId) {

        String query = "SELECT emr_id, emr_name, rm_url, account FROM cluster_metrics WHERE emr_id=:id";

        MapSqlParameterSource namedParams = new MapSqlParameterSource();

        namedParams.addValue("id", clusterId);

        logger.info("SQL:: " + query);

        List<EMRClusterMetricsVO> metrics = 
                namedJdbcTemplateObject.query(query, namedParams, new EMRClusterMetricsRmUrlMapper());

        return metrics;
    }

    @Override
    public List<EMRTimeSeriesReportVO> getClusterMetricsDailyLastWeek(Timestamp nowToNearestDay, String clusterId) {
        Timestamp from = new Timestamp(nowToNearestDay.getTime() - 7*24*60*60*1000);
        return getDailyMetricsForPeriod(clusterId, from, nowToNearestDay);
    }



    @Override
    public List<EMRTimeSeriesReportVO> getClusterMetricsDailyLastMonth(Timestamp nowToNearestDay, String clusterId) {
        Calendar c = Calendar.getInstance(); 
        c.setTime(nowToNearestDay); 
        c.add(Calendar.MONTH, -1);
        Timestamp from = new Timestamp(c.getTime().getTime());
        return getDailyMetricsForPeriod(clusterId, from, nowToNearestDay);
    }

    private List<EMRTimeSeriesReportVO> getDailyMetricsForPeriod(String clusterId, Timestamp from,
            Timestamp to) {
        String query = "SELECT  " + 
                "    emr_id, " + 
                "    DATE_FORMAT(refresh_timestamp, '%Y-%m-%d 00:00:00') ts, " + 
                "    ROUND(AVG(apps_running)) apps_running, " + 
                "    ROUND(AVG(apps_pending)) apps_pending, " + 
                "    SUM(apps_failed) apps_failed, " + 
                "    SUM(apps_succeeded) apps_succeeded, " + 
                "    ROUND(AVG(memory_usage_pct), 2) memory_usage_pct, " + 
                "    ROUND(AVG(cores_usage_pct), 2) cores_usage_pct " + 
                "FROM " + 
                "    cluster_metrics_history " + 
                "WHERE " + 
                "    refresh_timestamp >= :from " + 
                "        AND refresh_timestamp < :to " + 
                "        AND emr_id = :cluster_id " + 
                "GROUP BY ts";

        MapSqlParameterSource namedParams = new MapSqlParameterSource();

        namedParams.addValue("from", from);
        namedParams.addValue("to", to);
        namedParams.addValue("cluster_id", clusterId);

        logger.info("SQL:: " + query);

        List<EMRTimeSeriesReportVO> metrics = 
                namedJdbcTemplateObject.query(query, namedParams, new EMRClusterMetricsTimeSeriesMapper());

        return metrics;
    }

}