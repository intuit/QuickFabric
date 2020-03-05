package com.intuit.quickfabric.emr.dao;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.intuit.quickfabric.commons.vo.EMRGroupCostVO;
import com.intuit.quickfabric.commons.vo.EMRTimeSeriesReportVO;
import com.intuit.quickfabric.commons.vo.MonthlyCostVO;
import com.intuit.quickfabric.emr.mapper.EMRCostTimeSeriesMapper;

@Component
public class EMRClusterCostDaoImpl implements EMRClusterCostDao {
    private static final Logger logger = LogManager.getLogger(EMRClusterCostDaoImpl.class);

    @Autowired
    NamedParameterJdbcTemplate namedJdbcTemplateObject;
    
    
    @Override
    public List<EMRTimeSeriesReportVO> getClusterCostWeek(String clusterId, Timestamp to) {
        logger.info("EMRClusterCostDao -> getClusterCostWeek args: clusterId=={}, to=={}", clusterId, to);
        
        Timestamp from = new Timestamp(to.getTime() - 7*24*60*60*1000);
        return getClusterCostForPeriod(clusterId, from, to);
    }

    

    @Override
    public List<EMRTimeSeriesReportVO> getClusterCostMonth(String clusterId, Timestamp to) {
        logger.info("EMRClusterCostDao -> getClusterCostMonth args: clusterId=={}, to=={}", clusterId, to);
        
        Calendar c = Calendar.getInstance(); 
        c.setTime(to); 
        c.add(Calendar.MONTH, -1);
        Timestamp from = new Timestamp(c.getTime().getTime());
        
        return getClusterCostForPeriod(clusterId, from, to);
    }
    
    @Override
    public List<EMRTimeSeriesReportVO> getClusterCostForPeriod(String clusterId, Timestamp from, Timestamp to) {
        logger.info("EMRClusterCostDao -> getClusterCostForPeriod args: clusterId=={}, from=={}, to=={}",
                clusterId, from, to);
        
        String query = "SELECT  " + 
                "    m.cluster_id, " + 
                "    h.emr_name, " + 
                "    MAX(h.emr_cost) cost, " + 
                "    DATE_FORMAT(h.created_date, '%Y-%m-%d 00:00:00') ts " + 
                "FROM " + 
                "    emr_billing_component_cost_hist h " + 
                "    LEFT JOIN " + 
                "    emr_cluster_metadata m " + 
                "    ON h.emr_name = m.cluster_name " + 
                "WHERE " + 
                "    m.cluster_id = :cluster_id " + 
                "        AND h.created_date >= :from " + 
                "        AND h.created_date < :to " + 
                "GROUP BY emr_name , ts;";
        
        MapSqlParameterSource namedParams = new MapSqlParameterSource();
        
        namedParams.addValue("from", from);
        namedParams.addValue("to", to);
        namedParams.addValue("cluster_id", clusterId);

        logger.info("SQL:: " + query);

        List<EMRTimeSeriesReportVO> metrics = 
                namedJdbcTemplateObject.query(query, namedParams, new EMRCostTimeSeriesMapper());

        return metrics;   
    }



    @Override
    public List<EMRGroupCostVO> getEMRGroupCost(String account, String segment, Timestamp from) {
        logger.info("EMRClusterCostDao -> get EMRGroupCost, args: account={}, segment=={}, from=={}",
                account, segment, from);
        
        // note: subtracting one day first because the first day of the each month is actually showing
        // total cost for previous month (ie for each day d, cost shown is as of d - 1)
        String sqlStart = "SELECT  " + 
                "    SUBSTRING_INDEX(cluster, '-', 2) AS emr_group, " + 
                "    account, " +
                "    segment, " +
                "    business_owner, " +
                "    bill_month, " + 
                "    SUM(cost) AS total_cost " + 
                "FROM " + 
                "    (SELECT  " + 
                "        c.emr_name AS cluster, " + 
                "            m.account, " + 
                "            m.segment, " +
                "            s.business_owner," +          
                "            DATE_FORMAT(DATE_ADD(c.created_date, INTERVAL -1 DAY),'%Y-%m') AS bill_month, " + 
                "            MAX(c.emr_cost) AS cost " + 
                "    FROM " + 
                "        emr_billing_component_cost_hist c " + 
                "    JOIN emr_cluster_metadata m ON c.emr_name = m.cluster_name AND c.created_date >= :from " +
                "    JOIN segments s ON s.segment_name = m.segment ";
        
        String sql = sqlStart;
        MapSqlParameterSource params = new MapSqlParameterSource();
        
        params.addValue("from", from);
        
        if(!account.equalsIgnoreCase("all")) {
            sql = sql + " AND m.account = :account ";
            params.addValue("account", account);
        }
        
        if(!segment.equalsIgnoreCase("all")) {
            sql = sql + " AND m.segment = :segment ";
            params.addValue("segment", segment);
        }
        
        String sqlEnd = " GROUP BY cluster, m.account, m.segment, s.business_owner, bill_month) AS tbl1 " + 
                "GROUP BY emr_group, account, segment, business_owner, bill_month " + 
                "ORDER BY segment ASC, account ASC, emr_group ASC, bill_month DESC";
        
        sql = sql + sqlEnd;
        
        logger.info("SQL::{}, params::{}", sql, params.getValues());
        
        return this.namedJdbcTemplateObject.query(sql, params, (ResultSet rs) -> {
            Map<String, EMRGroupCostVO> map = new HashMap<>();
            
            while(rs.next()) {
                String uniqueId = rs.getString("emr_group") + "-" + rs.getString("account");
                
                EMRGroupCostVO groupVo = 
                        map.getOrDefault(uniqueId, new EMRGroupCostVO());
                
                groupVo.setEmrGroup(rs.getString("emr_group"));
                groupVo.setAccount(rs.getString("account"));
                groupVo.setSegment(rs.getString("segment"));
                groupVo.setBusinessOwner(rs.getString("business_owner"));
                
                MonthlyCostVO costVo = new MonthlyCostVO();
                
                costVo.setBillMonth(rs.getString("bill_month"));
                costVo.setCost(rs.getLong("total_cost"));
                
                groupVo.addMonthlyCost(costVo);
                
                map.put(uniqueId, groupVo);
            }
            List<EMRGroupCostVO> emrGroupCost = new ArrayList<>(map.values());
            emrGroupCost.sort((x, y) -> x.getSegment().compareTo(y.getSegment()));

            return emrGroupCost;
        });
        
        
    }
}