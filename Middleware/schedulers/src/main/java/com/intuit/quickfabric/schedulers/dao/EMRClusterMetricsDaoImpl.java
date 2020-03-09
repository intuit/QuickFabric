package com.intuit.quickfabric.schedulers.dao;


import com.intuit.quickfabric.commons.exceptions.QuickFabricSQLException;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.schedulers.mappers.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Component
public class EMRClusterMetricsDaoImpl implements EMRClusterMetricsDao {

    private static final Logger logger = LogManager.getLogger(EMRClusterMetricsDaoImpl.class);

    @Autowired
    NamedParameterJdbcTemplate namedJdbcTemplateObject;

    @Autowired
    private JdbcTemplate jdbcTemplateObject;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    @Transactional
    public void updateEMRClusterMetricsCost(List<EMRClusterMetricsVO> costs) {
        //TODO add table name
        String insertCostQuery = "INSERT INTO emr_billing_component_cost (emr_name,emr_cost) VALUES "
                + "(?, ?)";

        String deleteCostQuery = "DELETE FROM emr_billing_component_cost";

        String insertCostHistQuery = "INSERT INTO emr_billing_component_cost_hist (emr_name,emr_cost) VALUES "
                + "(?, ?)";

        try {

            jdbcTemplateObject.execute(deleteCostQuery);

            jdbcTemplateObject.batchUpdate(insertCostQuery,
                    new BatchPreparedStatementSetter() {

                public void setValues(PreparedStatement preparedStmt, int i) throws SQLException {

                    EMRClusterMetricsVO vo = costs.get(i);
                    preparedStmt.setString(1, vo.getEmrName());
                    preparedStmt.setDouble(2, vo.getCost());

                }

                @Override
                public int getBatchSize() {
                    return costs.size();
                }


            });

            jdbcTemplateObject.batchUpdate(insertCostHistQuery,
                    new BatchPreparedStatementSetter() {

                public void setValues(PreparedStatement preparedStmt, int i) throws SQLException {

                    EMRClusterMetricsVO vo = costs.get(i);
                    preparedStmt.setString(1, vo.getEmrName());
                    preparedStmt.setDouble(2, vo.getCost());

                }

                @Override
                public int getBatchSize() {
                    return costs.size();
                }


            });


        } catch (Exception e) {
            //System.out.println("update failed , please check the logs or reach to administrator! \n" + e);
            logger.error("update failed , please check the logs or reach to administrator! \n" + e.getMessage());
            throw new QuickFabricSQLException("DB error during EMR Cluster cost update", e);
        }
        //System.out.println("EMR Cluster Metrics cost inserted succussfully to table");
        logger.info("EMR Cluster Metrics cost inserted succussfully to table");

    }


    public List<String> getDistinctEMRBillingComponent() {
        String queryString = "SELECT  distinct (emr_name)  FROM cluster_metrics";
        List<String> billingComponents = new ArrayList<String>();

        logger.info("SQL::" + queryString);
        billingComponents = jdbcTemplateObject.queryForList(queryString, String.class);

        return billingComponents;
    }


    @Override
    public List<EMRClusterMetricsVO> getClusterMetricsReport(Timestamp from, Timestamp to, String segmentName) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("from", from);
        parameters.addValue("to", to);

        String baseQuery = "SELECT t1.emr_id, t1.emr_name, t1.segment, t1.account, t2.emr_cost, " +
                "ROUND(AVG(t1.memory_usage_pct),2) avg_memory_usage_pct, " +
                "ROUND(AVG(t1.cores_usage_pct),2) avg_cores_usage_pct, " + 
                "SUM(t1.apps_succeeded) total_apps_succeeded, SUM(t1.apps_failed) total_apps_failed, " + 
                "ROUND(AVG(t1.apps_running),0) avg_apps_running " +
                "FROM cluster_metrics_history t1 " +
                "LEFT JOIN emr_billing_component_cost t2 ON t1.emr_name = t2.emr_name " +
                "WHERE t1.refresh_timestamp BETWEEN :from AND :to " +
                "AND t1.emr_id IS NOT NULL ";

        String sql = baseQuery;
        if(!(StringUtils.isBlank(segmentName) || segmentName.equalsIgnoreCase("all"))) {
            String segmentsFilter = "AND t1.segment = :segment_name ";
            sql = baseQuery + segmentsFilter;
            parameters.addValue("segment_name", segmentName);
        }

        String groupAndOrderByClause = "GROUP BY emr_id,emr_name ORDER BY total_apps_succeeded desc";

        sql = sql + groupAndOrderByClause;

        logger.info("SQL:: " + sql);

        List<EMRClusterMetricsVO> report = 
                namedJdbcTemplateObject.query(sql, parameters, new EMRClusterMetricsHourlyReportMapper());


        return report;
    }

    @Override
    @Transactional
    public void updateEMRClusterMetricsList( List<EMRClusterMetricsVO> metrics)  {
    
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MM-dd-yyyy hh:mm:ss");
    
        String insertQuery = "INSERT INTO cluster_metrics (emr_id, emr_name,rm_url, metrics_json, "
                + "emr_status,refresh_timestamp,total_nodes,cores_usage_pct,memory_usage_pct,"
                + "containers_pending,apps_pending,account,cluster_create_timestamp,type,"
                + "segment,apps_running,apps_succeeded,"
                + "apps_failed,created_by) "
                + "VALUES (?, ?, ?, ?, ?, ?,?, ?, ?,?,?,?,?,?,?,?,?,?,?)";
        String deleteQuery = "DELETE FROM cluster_metrics";
        String insertHistQuery = "INSERT INTO cluster_metrics_history (emr_id, emr_name,rm_url, "
                + "metrics_json, emr_status,refresh_timestamp,total_nodes,cores_usage_pct,"
                + "memory_usage_pct,containers_pending,apps_pending,account,"
                + "cluster_create_timestamp,type,"
                + "segment,apps_running,apps_succeeded,apps_failed,created_by) "
                + "VALUES (?, ?, ?, ?, ?, ?,?, ?, ?,?,?,?,?,?,?,?,?,?,?)";
    
       
        //0002-03-31 00:00:00
        try {
            jdbcTemplateObject.execute(deleteQuery);
    
    
            jdbcTemplateObject.batchUpdate(insertQuery,
                    new BatchPreparedStatementSetter() {
    
                public void setValues(PreparedStatement preparedStmt, int i) throws SQLException {
    
                    EMRClusterMetricsVO clusterVO  = metrics.get(i);
                    preparedStmt.setString(1, clusterVO.getEmrId());
                    preparedStmt.setString(2, clusterVO.getEmrName()); 
                    preparedStmt.setString(3, clusterVO.getRmUrl());
                    preparedStmt.setString(4, clusterVO.getMetricsJson());
                    preparedStmt.setString(5, clusterVO.getEmrStatus().toString());
                    
                    Date date1 = null;
                    try {
                        date1=dateFormat.parse(clusterVO.getClusterCreateTimestamp());
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                    //preparedStmt.setTimestamp(6, new java.sql.Timestamp(clusterVO.getRefresh_timestamp().getTime()));
                    //preparedStmt.setTimestamp(6,new Timestamp(System.currentTimeMillis()));
    	            preparedStmt.setTimestamp(6,new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
                    preparedStmt.setInt(7, clusterVO.getActiveNodes());
                    preparedStmt.setDouble(8, clusterVO.getCoresUsagePct());
                    preparedStmt.setDouble(9, clusterVO.getMemoryUsagePct());
                    preparedStmt.setInt(10, clusterVO.getContainersPending());
                    preparedStmt.setInt(11, clusterVO.getAppsPending());
                    preparedStmt.setString(12, clusterVO.getAccount());
                    preparedStmt.setTimestamp(13, new Timestamp(date1.getTime()));
                    preparedStmt.setString(14, clusterVO.getType().toString());
                    preparedStmt.setString(15, clusterVO.getClusterSegment());
                    preparedStmt.setInt(16, clusterVO.getAppsRunning());
                    preparedStmt.setInt(17, clusterVO.getAppsSucceeded());
                    preparedStmt.setInt(18, clusterVO.getAppsFailed());
                    preparedStmt.setString(19, clusterVO.getCreatedBy());
                }
    
                public int getBatchSize() {
                    return metrics.size();
                }
            });
    
            jdbcTemplateObject.batchUpdate(insertHistQuery,
                    new BatchPreparedStatementSetter() {
    
                public void setValues(PreparedStatement preparedStmt, int i) throws SQLException
                {
    
                    EMRClusterMetricsVO clusterVO  = metrics.get(i);
                    preparedStmt.setString(1, clusterVO.getEmrId());
                    preparedStmt.setString(2, clusterVO.getEmrName()); 
                    preparedStmt.setString(3, clusterVO.getRmUrl());
                    preparedStmt.setString(4, clusterVO.getMetricsJson());
                    preparedStmt.setString(5, clusterVO.getEmrStatus().toString());
                    
                    Date date1 = null;
                    try {
                        date1=dateFormat.parse(clusterVO.getClusterCreateTimestamp());
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                    //preparedStmt.setTimestamp(6, new java.sql.Timestamp(clusterVO.getRefresh_timestamp().getTime()));
                    //preparedStmt.setTimestamp(6,new Timestamp(System.currentTimeMillis()));
                    preparedStmt.setTimestamp(6,new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
                    preparedStmt.setInt(7, clusterVO.getActiveNodes());
                    preparedStmt.setDouble(8, clusterVO.getCoresUsagePct());
                    preparedStmt.setDouble(9, clusterVO.getMemoryUsagePct());
                    preparedStmt.setInt(10, clusterVO.getContainersPending());
                    preparedStmt.setInt(11, clusterVO.getAppsPending());
                    preparedStmt.setString(12, clusterVO.getAccount());
                    preparedStmt.setTimestamp(13, new Timestamp(date1.getTime()));
                    preparedStmt.setString(14, clusterVO.getType().toString());
                    preparedStmt.setString(15, clusterVO.getClusterSegment());
                    preparedStmt.setInt(16, clusterVO.getAppsRunning());
                    preparedStmt.setInt(17, clusterVO.getAppsSucceeded());
                    preparedStmt.setInt(18, clusterVO.getAppsFailed());
                    preparedStmt.setString(19, clusterVO.getCreatedBy());
                }
    
                public int getBatchSize() {
                    return metrics.size();
                }
            });
        } catch (Exception e) {
            logger.error("update failed , please check the logs or reach to administrator! \n" + e.getMessage());
            throw new QuickFabricSQLException("DB error during update metrics", e);
        }           
        //System.out.println("EMR Cluster Metrics List inserted succussfully to table");
        logger.info("EMR Cluster Metrics List inserted succussfully to table");
    }
    
}