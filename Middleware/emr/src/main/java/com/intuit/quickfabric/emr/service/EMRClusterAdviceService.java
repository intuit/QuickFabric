package com.intuit.quickfabric.emr.service;

import com.intuit.quickfabric.commons.utils.CommonUtils;
import com.intuit.quickfabric.commons.vo.JobPerformanceAdviceVO;
import com.intuit.quickfabric.commons.vo.JobSchedulingAdviceVO;
import com.intuit.quickfabric.emr.helper.EMRClusterAdviceHelper;
import com.intuit.quickfabric.emr.model.EMRClusterAdviceModel;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/emr/advice")
    public class EMRClusterAdviceService {

    Logger logger = LogManager.getLogger(EMRClusterAdviceService.class);

    @Autowired
    private EMRClusterAdviceHelper adviceHelper;

    /** Gives job tuning advice based on metrics from Dr Elephant for given cluster over specified
     * time period
     * @param clusterId the cluster to get advice for
     * @param forLast one of: day, week, month, custom_range
     * @param from start time in yyyy-MM-dd HH:mm:ss format. Only used with forLast==custom_range.
     * @param to end time in yyyy-MM-dd HH:mm:ss format. Only used with forLast==custom_range.
     * @return the advice with heuristics
     */
    
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/job-performance/{emr_id}/{for_last}")
    public ResponseEntity<JobPerformanceAdviceVO> getClusterMetricsJobPerformanceAdvice(
            @PathVariable("emr_id") String clusterId,
            @PathVariable("for_last") String forLast,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        logger.info("EMRClusterMetricsService API->getClusterMetricsJobPerformanceAdvice");

        try {
            JobPerformanceAdviceVO jobPerformanceAdvice = adviceHelper.getClusterMetricsJobPerformanceAdviceHelper(clusterId, forLast, from, to);
            return ResponseEntity.ok(jobPerformanceAdvice);
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }

    }

    /** Combines job tuning advice and scheduling advice into one response.
     * @param clusterId the cluster to get advice for
     * @param forLast one of: day, week, month, custom_range
     * @param from start time in yyyy-MM-dd HH:mm:ss format. Only used with forLast==custom_range.
     * @param to end time in yyyy-MM-dd HH:mm:ss format. Only used with forLast==custom_range.
     * @return the advice with heuristics/metrics
     */
    
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PreAuthorize("hasAnyAuthority('admin', 'superadmin','read')")
    @GetMapping(value = "/all/{emr_id}/{for_last}")
    public ResponseEntity<EMRClusterAdviceModel> getClusterMetricsExpertAdvice(
            @PathVariable("emr_id") String clusterId,
            @PathVariable("for_last") String forLast,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        logger.info("EMRClusterMetricsService API->getClusterMetricsExpertAdvice");

        try {
            EMRClusterAdviceModel expertAdviceModel = adviceHelper.getClusterMetricsExpertAdviceHelper(clusterId, forLast, from, to);
            return ResponseEntity.ok(expertAdviceModel);
        } catch(Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
        
    }

    /** Give job scheduling advice based on how many applications are running at a time on given cluster
     * @param clusterId the cluster to get advice for
     * @param forLast one of: day, week, month
     * @return the advice with metrics
     */
    
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PreAuthorize("hasAnyAuthority('admin', 'superadmin','read')")
    @GetMapping(value = "/scheduling/{emr_id}/{for_last}")
    public ResponseEntity<JobSchedulingAdviceVO> getClusterMetricsSchedulingAdvice(
            @PathVariable("emr_id") String clusterId,
            @PathVariable("for_last") String forLast) {

        logger.info("EMRClusterMetricsService API->getClusterMetricsSchedulingAdvice");

        try {
            JobSchedulingAdviceVO schedulingAdvice = adviceHelper.getClusterMetricsSchedulingAdviceHelper(clusterId, forLast);
            return ResponseEntity.ok(schedulingAdvice); 
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }

    }

}
