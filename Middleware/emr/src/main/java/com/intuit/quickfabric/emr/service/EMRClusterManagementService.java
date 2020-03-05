package com.intuit.quickfabric.emr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.quickfabric.commons.utils.CommonUtils;
import com.intuit.quickfabric.commons.vo.ClusterRequest;
import com.intuit.quickfabric.commons.vo.ClusterVO;
import com.intuit.quickfabric.commons.vo.StepResponseVO;
import com.intuit.quickfabric.emr.helper.EMRAWSServiceCallerHelper;
import com.intuit.quickfabric.emr.helper.EMRClusterManagementHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/emr/management")
public class EMRClusterManagementService {
    //private static final String CLUSTER_NOT_PRESENT = "Cluster not present";

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    EMRAWSServiceCallerHelper emrawsServiceCallerHelper;
    @Autowired
    EMRClusterManagementHelper emrClusterManagementHelper;

    private static final Logger logger = LogManager.getLogger(EMRClusterManagementService.class);

    /**
     * Provision a new EMR cluster
     *
     * @param clusterRequest details of the new cluster to create
     * @return acknowledgement that cluster creation process has begun
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = "/create")
    @PreAuthorize("hasAnyAuthority('superadmin,admin, createcluster')")
    public ResponseEntity<ClusterVO> createEMRCluster(@RequestBody ClusterRequest clusterRequest) {
        logger.info("EMRClusterManagementService -> createEMRCluster -> " + clusterRequest);
        try {
            ClusterVO response = emrClusterManagementHelper.createEMRCluster(clusterRequest, false);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return CommonUtils.createErrorResponse(ex);
        }

    }

    /**
     * Test whether new cluster has been provisioned successfully and update its status in the database.
     *
     * @param clusterReq cluster to validate
     * @return the cluster's status
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = "/validate")
    @PreAuthorize("hasAnyAuthority('superadmin, admin, read')")
    public ResponseEntity<ClusterVO> validateEMRCluster(@RequestBody ClusterVO clusterReq) {
        logger.info("EMRClusterManagementService -> validateEMRCluster -> " + clusterReq);
        try {
            ClusterVO response = emrClusterManagementHelper.validateEMRCluster(clusterReq);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return CommonUtils.createErrorResponse(ex);
        }
    }

    /**
     * Tear down an EMR cluster
     *
     * @param clusterRequest the cluster to terminate
     * @return Acknowledgement that cluster termination has been initiated
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = "/terminate")
    @PreAuthorize("hasAnyAuthority('superadmin, admin, terminatecluster')")
    public ResponseEntity<ClusterVO> terminateEMRCluster(@RequestBody ClusterRequest clusterRequest) {
        logger.info("EMRClusterManagementService -> terminateEMRCluster  -> " + clusterRequest);
        try {
            ClusterVO response = emrClusterManagementHelper.terminateEMRCluster(clusterRequest, false);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return CommonUtils.createErrorResponse(ex);
        }

    }


    /**
     * Get a cluster's current status.
     *
     * @param clusterId the clusterId to check
     * @param accountId the account to which the cluster belongs
     * @return the cluster's status
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = "/status-check/{cluster_id}/{account_id}")
    @PreAuthorize("hasAnyAuthority('superadmin, admin, read')")
    public ResponseEntity<ClusterVO> getLatestClusterStatus(@PathVariable("cluster_id") String clusterId,
                                                            @PathVariable("account_id") String accountId) {
        logger.info("EMRClusterManagementService -> getLatestClusterStatus  -> " + clusterId);
        try {
            ClusterVO response = emrClusterManagementHelper.getLatestClusterStatus(clusterId, accountId);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return CommonUtils.createErrorResponse(ex);
        }
    }


    /**
     * Add custom steps to an active EMR cluster
     *
     * @param clusterStepReqest details of the step to add
     * @return acknowledgment that step has begun to execute on cluster
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = "/add-custom-steps")
    @PreAuthorize("hasAnyAuthority('superadmin, admin, addstep')")
    public ResponseEntity<StepResponseVO> addStepToSucceededClusters(@RequestBody ClusterRequest clusterStepReqest) {
        logger.info("EMRClusterManagementService -> addStepToSucceededClusters  -> " + clusterStepReqest);
        try {
            StepResponseVO response = emrClusterManagementHelper.addStepToSucceededClusters(clusterStepReqest);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return CommonUtils.createErrorResponse(ex);
        }
    }


    /**
     * Tear down existing cluster and provision a new one with the same specs. Production / scheduled
     * clusters will be considered high availability, where existing cluster will remain but can be
     * optionally marked to be terminated 24 hours later.
     *
     * @param clusterRequest the cluster to rotate
     * @return Confirmation that rotation has completed.
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = "/rotate-ami")
    @PreAuthorize("hasAnyAuthority('superadmin, admin, rotateami')")
    public ResponseEntity<ClusterVO> rotateAMI(@Valid @RequestBody ClusterRequest clusterRequest) {
        logger.info("EMRClusterManagementService -> rotateAMI  -> " + clusterRequest);

        try {
            ClusterVO response = emrClusterManagementHelper.rotateAMI(clusterRequest);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return CommonUtils.createErrorResponse(ex);
        }
    }

    /**
     * Flip this cluster to production DNS
     *
     * @param clusterRequest the cluster to put into production
     * @return confirmation that cluster has been put into production
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = "/dns-flip")
    @PreAuthorize("hasAnyAuthority('superadmin, admin, flipdns')")
    public ResponseEntity<ClusterVO> dnsFlip(@Valid @RequestBody ClusterRequest clusterRequest) {
        logger.info("EMRClusterManagementService -> dnsFlip  -> " + clusterRequest);
        try {
            ClusterVO response = emrClusterManagementHelper.dnsFlip(clusterRequest);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return CommonUtils.createErrorResponse(ex);
        }
    }
}
