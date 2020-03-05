package com.intuit.quickfabric.emr.service;

import com.intuit.quickfabric.commons.utils.CommonUtils;
import com.intuit.quickfabric.emr.EMRMainApplication;
import com.intuit.quickfabric.emr.helper.EMRClusterMetricsHelper;
import com.intuit.quickfabric.emr.model.EMRAppsModel;
import com.intuit.quickfabric.emr.model.EMRClusterMetricsModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/emr/metrics")
public class EMRClusterMetricsService {

    private static final Logger logger = LogManager.getLogger(EMRMainApplication.class);


    @Autowired
    EMRClusterMetricsHelper helper;

    /** Retrieve most recently refreshed cluster metrics (running jobs, active nodes, etc) for each
     * cluster. Optionally filter by account and cluster type
     * @param clusterType one of: exploratory, scheduled
     * @param account ID of AWS account
     * @return cluster metrics
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PreAuthorize("hasAnyAuthority('admin', 'superadmin','read')")
    @GetMapping(value = "")
    public ResponseEntity<EMRClusterMetricsModel> getEMRClusterMetrics(
            @RequestParam(value = "cluster_type", required = false) String clusterType,
            @RequestParam(value = "account", required = false) String account) {
        logger.info("EMRClusterMetricsService API->getEMRClusterMetrics() starting");
        try {
            EMRClusterMetricsModel emrClusterMetricsModel = helper.getEMRClusterMetricsList(clusterType, account);
            return ResponseEntity.ok(emrClusterMetricsModel);
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
    }

    /** Get history of metrics for the given cluster over specified time range
     * @param forLast one of: day, week, month, custom
     * @param emrId the cluster ID to get metrics for
     * @return the cluster metrics
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PreAuthorize("hasAnyAuthority('admin', 'superadmin','read')")
    @GetMapping(value = "/{emr_id}/{for_last}")
    public ResponseEntity<EMRClusterMetricsModel> getClusterMetricsForTime(
            @PathVariable("for_last") String forLast,
            @PathVariable("emr_id") String emrId) {

        logger.info("EMRClusterMetricsService API->getClusterMetricsForTime calling");
        try {
            EMRClusterMetricsModel emrClusterMetricsModel = helper.getEMRClusterMetricsForTimeHelper(emrId, forLast);
            return ResponseEntity.ok(emrClusterMetricsModel);
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
    }

    /** Get history of metrics for the given cluster over specified time range
     * @param emrId ID of cluster to get metrics for
     * @param from start time in yyyy-MM-dd HH:mm:ss format
     * @param to end time in yyyy-MM-dd HH:mm:ss format
     * @return the cluster mtrics
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PreAuthorize("hasAnyAuthority('admin', 'superadmin','read')")
    @GetMapping(value = "/{emr_id}/custom_range", params = {"from", "to"})
    public ResponseEntity<EMRClusterMetricsModel> getClusterMetricsForCustomTime(
            @PathVariable("emr_id") String emrId,
            @RequestParam("from") String from,
            @RequestParam("to") String to) {

        try {
            EMRClusterMetricsModel emrClusterMetricsModel = helper.getClusterMetricsCustomPeriod(emrId, from, to);
            return ResponseEntity.ok(emrClusterMetricsModel);
        } catch(Exception e) {
            return CommonUtils.createErrorResponse(e);
        }

    }

    /** Get succeeded or failed applications on cluster over specified time range
     * @param clusterId cluster to get applications for
     * @param status "succeeded" or "failed"
     * @param forLast one of: latest, hour, day, week, month, custom_range (latest == last 10 minutes)
     * @param from start time in yyyy-MM-dd HH:mm:ss format. Only used with forLast=="custom_range"
     * @param to end time in yyyy-MM-dd HH:mm:ss format. forLast=="custom_range"
     * @return list of completed applications
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PreAuthorize("hasAnyAuthority('admin', 'superadmin','read')")
    @GetMapping(value = "/{emr_id}/apps/{status}/{for_last}")
    public ResponseEntity<EMRAppsModel> getCompletedApps(
            @PathVariable("emr_id") String clusterId,
            @PathVariable("status") String status,
            @PathVariable("for_last") String forLast,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        try {
            EMRAppsModel appsModel = helper.getCompletedAppsHelper(clusterId, status, forLast, from, to);
            return ResponseEntity.ok(appsModel);
        } catch(Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
    }


    /** Get details of currently running applications on given cluster
     * @param clusterId the ID of the cluster to get running applications for 
     * @return the list of applications
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PreAuthorize("hasAnyAuthority('admin', 'superadmin','read')")
    @GetMapping(value = "/{emr_id}/apps/running")
    public ResponseEntity<EMRAppsModel> getRunningApps(@PathVariable("emr_id") String clusterId) {
        logger.info("Getting all running apps for cluster " + clusterId);
        try {
            EMRAppsModel appsModel = helper.getRunningApps(clusterId);
            return ResponseEntity.ok(appsModel);
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
    }
    
}