package com.intuit.quickfabric.emr.service;

import com.intuit.quickfabric.commons.utils.CommonUtils;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.emr.helper.AdminHelper;
import com.intuit.quickfabric.emr.helper.EMRClusterMetadataHelper;
import com.intuit.quickfabric.emr.helper.EMRClusterMetricsHelper;
import com.intuit.quickfabric.emr.model.EMRClusterMetadataModel;
import com.intuit.quickfabric.emr.model.UIPopulationModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class EMRClusterMetadataService {

    private final Logger logger = LogManager.getLogger(EMRClusterMetadataService.class);

    @Autowired
    EMRClusterMetadataHelper clusterMetadataHelper;

    @Autowired
    AdminHelper adminHelper;

    @Autowired
    EMRClusterMetricsHelper clusterMetricsHelper;

    /**
     * Retrieve metadata for clusters fitting optional query parameters
     *
     * @param clusterName return only clusters with this name
     * @param clusterType return only clusters with this type
     * @param account     return only clusters belong to this account
     * @return a list of clusters' metadata
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/emr/metadata")
    @PreAuthorize("hasAnyAuthority('admin', 'superadmin','read')")
    public ResponseEntity<EMRClusterMetadataModel> getEMRClusterMetaData(
            @RequestParam(value = "cluster_name", required = false) String clusterName,
            @RequestParam(value = "cluster_type", required = false) String clusterType,
            @RequestParam(value = "account", required = false) String account) {
        try {
            logger.info("EMRClusterMetaDataService API-> getEMRClusterMetaData");
            EMRClusterMetadataModel emrClusterMetadataModel = clusterMetadataHelper
                    .getEMRClusterMetadataList(clusterName, clusterType, account);

            return ResponseEntity.ok(emrClusterMetadataModel);
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
    }

    /**
     * Get setep statuses for the given cluster. If requestFrom == "worfklow", then return step statuses
     * for new cluster (from AMI rotation) rather than the actual cluster being given
     *
     * @param clusterId   the cluster to get step statuses for
     * @param requestFrom "workflow" or omitted
     * @return
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/emr/steps/{cluster_id}")
    @PreAuthorize("hasAnyAuthority('admin', 'superadmin', 'read')")
    public ResponseEntity<StepResponseVO> getEMRClusterStepStatus(
            @PathVariable("cluster_id") String clusterId,
            @RequestParam(value = "request_from", required = false) String requestFrom) {
        try {
            logger.info("EMRClusterMetaDataService API-> getEMRClusterStepStatus()");
            StepResponseVO stepResponse = clusterMetricsHelper.getLatestClusterStepStatusForCluster(clusterId, requestFrom);
            return ResponseEntity.ok(stepResponse);
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
    }

    /**
     * Get a list of AWS account profiles
     *
     * @param accountId if provided, get details for only this accouny
     * @return the AWS account profiles
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = {"/aws-account-profile", "/aws-account-profile/{account_id}"})
    @PreAuthorize("hasAnyAuthority('admin', 'superadmin', 'read')")
    public ResponseEntity<List<AwsAccountProfile>> getAwsAccountProfile(@PathVariable(value = "account_id", required = false) String accountId) {
        try {
            logger.info("EMRClusterMetaDataService API -> getAwsAccountProfile");
            List<AwsAccountProfile> accounts = clusterMetadataHelper.getAWSAccountProfile(accountId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
    }

    /**
     * Get details of business segments
     *
     * @param segmentName if provided, only give details of this segment
     * @return list of business segments
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = {"/segment/}", "/segment/{segment_name}"})
    @PreAuthorize("hasAnyAuthority('admin', 'superadmin', 'read')")
    public @ResponseBody
    ResponseEntity<List<SegmentVO>> getSegment(@PathVariable(value = "segment_name", required = false) String segmentName) {
        try {
            logger.info("EMRClusterMetaDataService API -> getAllSegments");
            List<SegmentVO> segments = clusterMetadataHelper.getSegment(segmentName);
            return ResponseEntity.ok(segments);
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
    }

    /**
     * Get lists of dynamic portions of this application (business segments, AWS accounts,
     * culster actions
     *
     * @return
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/populate-ui-list")
    @PreAuthorize("hasAnyAuthority('admin', 'superadmin', 'read')")
    public ResponseEntity<UIPopulationModel> getLookupData() {
        try {
            logger.info("EMRClusterMetaDataService API -> populate-ui-list");
            UIPopulationModel model = clusterMetadataHelper.getLookupData();
            return ResponseEntity.ok(model);
        } catch (Exception ex) {
            return CommonUtils.createErrorResponse(ex);
        }
    }

    /**
     * Get metadata for the given cluster. If request_from == "workflow" then retrieves metadata
     * for new cluster (from AMI rotation) rather than this one
     *
     * @param clusterId   the cluster to retrieve metadata for
     * @param requestFrom "workflow" or omitted
     * @return the metadata for the given cluster
     */
    @GetMapping(value = "/emr/metadata/{cluster_id}")
    @PreAuthorize("hasAnyAuthority('admin', 'superadmin','read')")
    public ResponseEntity<ClusterVO> getSingleEMRClusterMetadata(
            @PathVariable("cluster_id") String clusterId,
            @RequestParam(value = "request_from", required = false) String requestFrom) {
        try {
            ClusterVO cluster = clusterMetadataHelper.getSingleClusterDetailsByClusterID(clusterId, requestFrom);
            return ResponseEntity.ok(cluster);
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
    }

    /**
     * Update configurations for EMR autopilot (auto AMI rotation). Options are enabled, time window,
     * and SLA.
     *
     * @param clusterDetails the new configurations
     * @return success or error
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = {"/emr/autopilot/update-config"})
    @PreAuthorize("hasAnyAuthority('superadmin, admin, rotateami')")
    public ResponseEntity<ClusterMessageResponseVO> updateAutopilotConfig(@Valid @RequestBody ClusterRequest clusterDetails) {
        try {
            logger.info("EMRClusterManagementService -> updateAutopilotConfig  -> " + "starting for clusterID: " + clusterDetails.getClusterId());
            ClusterMessageResponseVO response = clusterMetadataHelper.updateAutopilotConfig(clusterDetails);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
    }

    /**
     * mark/unmark this cluster to be terminated in 24 hours
     *
     * @param clusterDetails the cluster to mark, and on/off
     * @return success or error
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = {"/emr/auto-terminate/update"})
    @PreAuthorize("hasAnyAuthority('superadmin, admin, terminatecluster')")
    public ResponseEntity<ClusterMessageResponseVO> updateDoTerminateConfig(@Valid @RequestBody ClusterRequest clusterDetails) {
        try {
            logger.info("EMRClusterManagementService -> updateDoTerminateConfig  -> " + "starting for clusterID: " + clusterDetails.getClusterId());
            ClusterMessageResponseVO response = clusterMetadataHelper.updateDoTerminateConfig(clusterDetails);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
    }

    /**
     * Get a copy of the given cluster with all information needed to create a new one.
     *
     * @param clusterId the cluster to copy
     * @return the cluster's metadata and specs
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/emr/clone/{cluster_id}")
    @PreAuthorize("hasAnyAuthority('admin', 'superadmin','read')")
    public ResponseEntity<ClusterVO> getClusterClone(@PathVariable(value = "cluster_id") String clusterId) {
        try {
            logger.info("EMRCLusterManagementService -> getClusterClone , args: clusterId:{}", clusterId);
            ClusterVO cluster = clusterMetadataHelper.getClusterClone(clusterId);
            return ResponseEntity.ok(cluster);
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
    }
}