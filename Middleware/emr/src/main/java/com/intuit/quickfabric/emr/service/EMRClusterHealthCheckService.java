package com.intuit.quickfabric.emr.service;

import com.intuit.quickfabric.commons.domain.QuickFabricAuthenticationToken;
import com.intuit.quickfabric.commons.utils.CommonUtils;
import com.intuit.quickfabric.commons.vo.ClusterHealthCheckRequest;
import com.intuit.quickfabric.commons.vo.ClusterHealthCheckStatusUpdate;
import com.intuit.quickfabric.commons.vo.ClusterHealthStatus;
import com.intuit.quickfabric.commons.vo.EMRClusterHealthTestCase;
import com.intuit.quickfabric.emr.helper.EMRClusterHealthHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/emr/health")
public class EMRClusterHealthCheckService {

    @Autowired
    EMRClusterHealthHelper emrClusterHealthHelper;

    private static final Logger logger = LogManager.getLogger(EMRClusterHealthCheckService.class);

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/test-suites")
    @PreAuthorize("hasAnyAuthority('admin, superadmin, read')")
    public ResponseEntity<List<EMRClusterHealthTestCase>> getEMRTestSuites(
            @RequestParam @NotNull @Size(min = 1) String clusterType,
            @RequestParam @NotNull @Size(min = 1) String clusterSegment) {
        try {
            logger.info("EMRClusterHealthService API -> getEMRTestSuites clusterType:"
                    + clusterType + " clusterSegment:" + clusterSegment);
            List<EMRClusterHealthTestCase> emrTestSuites = emrClusterHealthHelper.getEMRTestSuites(clusterType, clusterSegment);
            return ResponseEntity.ok(emrTestSuites);
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/status/{clusterId}")
    @PreAuthorize("hasAnyAuthority('admin, superadmin, read')")
    public ResponseEntity<List<ClusterHealthStatus>> getEMRClusterHealthStatus(
            @PathVariable("clusterId") String clusterId,
            @RequestParam(value = "request_from", required = false) String requestFrom) {
        try {
            logger.info("EMRClusterHealthService API -> getEMRClusterHealthStatus clusterId:{} request_from:{}", clusterId, requestFrom);
            List<ClusterHealthStatus> healthStatuses = emrClusterHealthHelper.getEMRClusterHealthStatus(clusterId, requestFrom);
            return ResponseEntity.ok(healthStatuses);
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/history/{clusterId}")
    @PreAuthorize("hasAnyAuthority('admin, superadmin, read')")
    public ResponseEntity<List<ClusterHealthStatus>> getEMRClusterHealthHistory(@PathVariable("clusterId") String clusterId) {
        try {
            logger.info("EMRClusterHealthService API -> getEMRClusterHealthHistory clusterId:{}", clusterId);
            List<ClusterHealthStatus> emrClusterHealthHistory = emrClusterHealthHelper.getEMRClusterHealthHistory(clusterId);
            return ResponseEntity.ok(emrClusterHealthHistory);
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = "/run-check")
    @PreAuthorize("hasAnyAuthority('admin, superadmin, runclusterhealthchecks')")
    public ResponseEntity<String> runEMRClusterHealthCheck(@Valid @RequestBody ClusterHealthCheckRequest healthCheckRequest) {
        try {
            logger.info("EMRClusterHealthService API -> runEMRClusterHealthCheck {}", healthCheckRequest.toString());
            QuickFabricAuthenticationToken auth = (QuickFabricAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            String message = emrClusterHealthHelper.runEMRClusterHealthCheck(healthCheckRequest, auth.getEmail());
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping(value = "/test/update/{clusterId}")
    @PreAuthorize("hasAnyAuthority('admin, superadmin, updateclusterhealthtest')")
    public ResponseEntity<String> updateEMRClusterHealthTest(@PathVariable("clusterId") String clusterId,
                                                             @Valid @RequestBody ClusterHealthCheckStatusUpdate healthCheckStatusUpdate) {
        try {
            logger.info("EMRClusterHealthService API -> updateClusterHealthTest clusterId:{} {}", clusterId, healthCheckStatusUpdate.toString());
            String message = emrClusterHealthHelper.updateEMRClusterHealthTest(clusterId, healthCheckStatusUpdate);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return CommonUtils.createErrorResponse(e);
        }
    }
}