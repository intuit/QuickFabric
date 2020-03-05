package com.intuit.quickfabric.emr.helper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.intuit.quickfabric.commons.exceptions.QuickFabricClientException;
import com.intuit.quickfabric.commons.vo.EMRGroupCostVO;
import com.intuit.quickfabric.commons.vo.EMRTimeSeriesReportVO;
import com.intuit.quickfabric.commons.vo.MonthlyCostVO;
import com.intuit.quickfabric.emr.dao.EMRClusterCostDao;
import com.intuit.quickfabric.emr.model.EMRClusterCostModel;

@Component
public class EMRClusterCostHelper {
    
    private static final Logger logger = LogManager.getLogger(EMRClusterCostHelper.class);
    
    @Autowired
    EMRClusterCostDao emrClusterCostDao;
  
    
    /**
     * Get the cost of the cluster up until each day for the last week (cost monotonically increases
     * until it resets to 0 at start of each month)
     * @param cluster the cluster to get the cost history of
     * @param to the last week up until this timestamp
     * @return the cost history
     */
    public EMRClusterCostModel getClusterCostWeek(String clusterId, Timestamp to) {
        logger.info("EMRClusterCostHelper -> getClusterCostWeek args: clusterId=={}, to=={}", clusterId, to);
        EMRClusterCostModel model = new EMRClusterCostModel();
        List<EMRTimeSeriesReportVO> dailyCost = 
                emrClusterCostDao.getClusterCostWeek(clusterId, to);
        
        model.setClusterCost(dailyCost);
       
        return model;
    }

    
    /**
     * Get the cost of the cluster up until each day for the last month (cost monotonically increases
     * until it resets to 0 at start of each month)
     * @param clusterId the cluster to get the cost history of
     * @param to the last month up until this timestamp
     * @return the cost history
     */
    public EMRClusterCostModel getClusterCostMonth(String clusterId, Timestamp to) {
        logger.info("EMRClusterCostHelper -> getClusterCostMonth args: clusterId=={}, to=={}", clusterId, to);

        EMRClusterCostModel model = new EMRClusterCostModel();
        model.setClusterId(clusterId);

        List<EMRTimeSeriesReportVO> dailyCost = 
                emrClusterCostDao.getClusterCostMonth(clusterId, to);

        model.setClusterCost(dailyCost);
        return model;
    }
    
    /**
     * Get the cost of the cluster up until each day for the given period (cost monotonically increases
     * until it resets to 0 at start of each month)
     * @param clusterId the cluster to get the cost history of
     * @param from get cost starting from this timestamp
     * @param to get cost until this timestamp
     * @return the cost history
     */
    public EMRClusterCostModel getClusterCostCustom(String clusterId, Timestamp from, Timestamp to) {
        logger.info("EMRClusterCostHelper -> getClusterCostCustom args: clusterId=={}, from=={}, to=={}",
                clusterId, from, to);

        EMRClusterCostModel model = new EMRClusterCostModel();
        model.setClusterId(clusterId);

        List<EMRTimeSeriesReportVO> dailyCost = 
                emrClusterCostDao.getClusterCostForPeriod(clusterId, from, to);

        model.setClusterCost(dailyCost);
        return model;
    }

    
    /**
     * Get the cost of the cluster up until each day for the given period (cost monotonically increases
     * until it resets to 0 at start of each month)
     * @param clusterId the cluster to get the cost history of
     * @param from get cost starting from this timestamp
     * @param to get cost until this timestamp
     * @return the cost history
     */
    public EMRClusterCostModel getClusterCost(String forLast , String clusterId, String from, String to) {
        logger.info("EMRClusterCostHelper -> getClusterCostCustom args: clusterId=={}, from=={}, to=={}",
                clusterId, from, to);
        long now = System.currentTimeMillis();
        Timestamp nowToNearestDay = new Timestamp(now - now % (24 * 60 * 60 * 1000));
        EMRClusterCostModel model = new EMRClusterCostModel();
        switch (forLast.toLowerCase()) {
            case "week":
                model = getClusterCostWeek(clusterId, nowToNearestDay);
                break;
            case "month":
                model = getClusterCostMonth(clusterId, nowToNearestDay);
                break;
            case "custom_range":
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                ZonedDateTime fromDateTime =
                        LocalDateTime.parse(from, formatter).atZone(ZoneId.of("America/Los_Angeles"));

                ZonedDateTime toDateTime =
                        LocalDateTime.parse(to, formatter).atZone(ZoneId.of("America/Los_Angeles"));

                Timestamp fromTs = Timestamp.valueOf(fromDateTime.toLocalDateTime());
                Timestamp toTs = Timestamp.valueOf(toDateTime.toLocalDateTime());
                model = getClusterCostCustom(clusterId, fromTs, toTs);

                break;
            default:
                logger.error("Invalid time range. Must be one of: week, month, custom");
                throw new QuickFabricClientException("Invalid time range. Must be one of: week, month, custom");

        }
        return model;
    }


    public EMRClusterCostModel getEMRGroupCost(String account, String segment, Integer months) {
        logger.info("EMRClusterCostHelper->getEMRGroupCost , args: account=={}, segment=={}, months=={}",
                account, segment, months);
        // default values
        account = account == null ? "all" : account;
        segment = segment == null ? "all" : segment;
        months = months == null ? 6 : months;
        
        EMRClusterCostModel model = new EMRClusterCostModel();
        Calendar c = Calendar.getInstance(); 
        c.setTime(new Timestamp(System.currentTimeMillis())); 
        c.add(Calendar.MONTH, -months);
        Timestamp from = new Timestamp(c.getTime().getTime());
        
        List<EMRGroupCostVO> costPerGroup = this.emrClusterCostDao.getEMRGroupCost(account, segment, from);
        
        model.setEmrGroupCost(costPerGroup);
        
        for(EMRGroupCostVO groupCost : costPerGroup) {
            groupCost.setCostPerMonth(this.fillMissingMonths(groupCost, months));
        }
        
        return model;
    }
    
    private List<MonthlyCostVO> fillMissingMonths(EMRGroupCostVO emrGroup, int months) {
        logger.info("EMRClusterCostHelper->fillMissingMonths , args: emrGroup=={}, months == {}",
                emrGroup.getEmrGroup(), months);
        
        List<MonthlyCostVO> costsForGroup = new ArrayList<>(emrGroup.getCostPerMonth());
        
        // add 1 since doing one month difference will show 2 - this month and last month
        for(int i = 0; i < months+1; i++) {
            // get the next month so we can check whether or not it's missing.
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            cal.add(Calendar.MONTH, -i);
            String nextMonth = sdf.format(cal.getTime());
            
            MonthlyCostVO newMonth = new MonthlyCostVO();
            newMonth.setCost(0);
            newMonth.setBillMonth(nextMonth);
            // this is a more recent month than any we have data for. add it to the end.
            if(i == costsForGroup.size()) {
                costsForGroup.add(newMonth);
            } 
            // A mismatch here means a month was skipped. add it into this position.
            else if(!costsForGroup.get(i).getBillMonth().equals(nextMonth)){
                costsForGroup.add(i, newMonth);;
            } 
            // if neither case is hit, then nothing to fill, so continue.
        }
        
        return costsForGroup;
    }
}