package com.intuit.quickfabric.emr.service;

import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.intuit.quickfabric.commons.utils.CommonUtils;
import com.intuit.quickfabric.emr.helper.EMRClusterCostHelper;
import com.intuit.quickfabric.emr.model.EMRClusterCostModel;


@RestController
@RequestMapping("/emr/cost")
public class EMRClusterCostService {
    
    private static final Logger logger = LogManager.getLogger(EMRClusterCostService.class);
    
    @Autowired
    private EMRClusterCostHelper emrClusterCostHelper;
    
    /** Get the per day cost history for given cluster
     * @param forLast one of: week, month, custom_range
     * @param clusterId the cluster to get cost history for
     * @param from start time in yyyy-MM-dd HH:mm:ss format. Only used with forLast==custom_range.
     * @param to end time in yyyy-MM-dd HH:mm:ss format. Only used with forLast==custom_range.
     * @return the daily cost history
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(
            value = "/{cluster_id}/{for_last}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<EMRClusterCostModel> getClusterCost(
            @PathVariable("for_last") String forLast,
            @PathVariable("cluster_id") String clusterId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        logger.info("EMRClusterMetricsService API->getClusterMetricsCost. "
                + "Path Variables: for_last=={}, cluster_id=={} "
                + "Request Params: from=={}, to=={}", forLast, clusterId, from, to);
        try {
        EMRClusterCostModel model = emrClusterCostHelper.getClusterCost(forLast,clusterId, from, to);
        return ResponseEntity.ok(model);
        }catch(Exception e){
        	logger.error("Exception during fatching emrGroupCost:" + e.getMessage(), e);
            return CommonUtils.createErrorResponse(e);
        }
    }
    
    /**
     * Get per month cost for "EMR group", defined by the type, segment, and account. For example,
     * exploratory-sales-cluster1 and exploratory-sales-cluster2 for account 0987654321 would be under
     * same group "exploratory-sales"
     * 
     * @param account the AWS account to retrieve for. defaults to "all"
     * @param segment the business segment to retrieve for. defaults to "all"
     * @param months how many months back from current date to retrieve for. default to 6.
     * @return monthly costs, grouped by "EMR group"
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(
            value = "/group",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<EMRClusterCostModel> getEMRGroupCost(@RequestParam(required = false) String account,
            @RequestParam(required = false) String segment,
            @RequestParam(required = false) Integer months) {
        
        logger.info("EMRClusterCostSerice -> getEMRGroupCost starting. "
                + "Params: account=={}, segment=={}, months=={}", account, segment, months);
        try {
        	EMRClusterCostModel model = this.emrClusterCostHelper.getEMRGroupCost(account, segment, months);
            return ResponseEntity.ok(model);
            }catch(Exception e){
            	logger.error("Exception during fatching emrGroupCost:" + e.getMessage(), e);
                return CommonUtils.createErrorResponse(e);
            }
        
        
    }
}
