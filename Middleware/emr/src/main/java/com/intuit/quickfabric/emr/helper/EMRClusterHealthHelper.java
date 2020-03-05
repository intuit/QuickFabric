package com.intuit.quickfabric.emr.helper;

import com.intuit.quickfabric.commons.exceptions.QuickFabricClientException;
import com.intuit.quickfabric.commons.exceptions.QuickFabricServerException;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.emr.dao.EMRClusterHealthCheckDao;
import com.intuit.quickfabric.emr.dao.EMRClusterMetadataDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EMRClusterHealthHelper {

    private static final Logger logger = LogManager.getLogger(EMRClusterHealthHelper.class);

    @Autowired
    EMRClusterHealthCheckDao emrClusterHealthCheckDao;

    @Autowired
    EMRClusterMetadataDao emrClusterMetadataDao;

    public List<ClusterHealthStatus> getEMRClusterHealthStatus(String clusterId, String requestFrom) {
        List<ClusterHealthStatus> emrClusterHealthStatus;

        if (StringUtils.isNotBlank(requestFrom) && requestFrom.equalsIgnoreCase("workflow")) {
            ClusterVO requestVO = emrClusterMetadataDao.getClusterMetadataByClusterId(clusterId);
            if (requestVO != null && StringUtils.isNotBlank(requestVO.getNewClusterId())) {
                logger.info("getEMRClusterHealthStatus for New ClusterId:{}", requestVO.getNewClusterId());
                emrClusterHealthStatus = emrClusterHealthCheckDao.getEMRClusterHealthStatus(requestVO.getNewClusterId());
            } else {
                logger.error("No New Cluster associated with this currentClusterId:{} NewClusterId:{}", clusterId, requestVO.getNewClusterId());
                throw new QuickFabricClientException("No New Cluster associated with this currentClusterId: " + clusterId);
            }
        } else {
            logger.info("getEMRClusterHealthStatus for Current ClusterId:{}", clusterId);
            emrClusterHealthStatus = emrClusterHealthCheckDao.getEMRClusterHealthStatus(clusterId);
        }

        return emrClusterHealthStatus;
    }

    public List<ClusterHealthStatus> getEMRClusterHealthHistory(String clusterId) {
        List<ClusterHealthStatus> clusterHealthStatus = emrClusterHealthCheckDao.getEMRClusterHealthHistory(clusterId);
        return clusterHealthStatus;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String runEMRClusterHealthCheck(ClusterHealthCheckRequest healthCheckRequest, String updatedBy) {

        ClusterVO clusterMetadata = emrClusterMetadataDao.getClusterMetadataByClusterId(healthCheckRequest.getClusterId());
        if (clusterMetadata == null) {
            throw new QuickFabricClientException("cluster not found. clusterId:" + healthCheckRequest.getClusterId());
        }

        if (clusterMetadata.getStatus() != ClusterStatus.RUNNING && clusterMetadata.getStatus() != ClusterStatus.WAITING) {
            throw new QuickFabricServerException("cluster is not in  valid state. cluster status" + clusterMetadata.getStatus());
        }

        List<ClusterStep> pendingBootstraps = emrClusterHealthCheckDao.getClusterBootStraps(healthCheckRequest.getClusterId())
                .stream().filter(x -> x.getStatus() != StepStatus.COMPLETED && x.getStatus() != StepStatus.FAILED)
                .collect(Collectors.toList());

        if (pendingBootstraps.size() > 0) {
            String message = "Bootstraps are not ready for the clusterId:" + healthCheckRequest.getClusterId()
                    + " Bootstraps not completed/failed: "
                    + pendingBootstraps.stream().map(ClusterStep::getName)
                    .collect(Collectors.joining(","));
            logger.error(message);
            throw new QuickFabricClientException(message);
        }

        List<ClusterHealthStatus> currentTestCases = emrClusterHealthCheckDao.getEMRClusterHealthStatus(healthCheckRequest.getClusterId());
        if (!currentTestCases.isEmpty() && !healthCheckRequest.isOverrideTimeout()) {
            for (ClusterHealthStatus testCase : currentTestCases) {
                try {
                    Date executionStart = getExiprationDate(testCase, healthCheckRequest.getClusterType());
                    if (executionStart.compareTo(new Date()) > 0 && (testCase.getStatus() != ClusterHealthCheckStatusType.SUCCESS
                            && testCase.getStatus() != ClusterHealthCheckStatusType.FAILED)) {
                        logger.error("Health checks on the cluster are currently pending for " + healthCheckRequest.toString());
                        throw new QuickFabricServerException("Health checks on the cluster are currently pending.");
                    }
                } catch (ParseException e) {
                    logger.error("runEMRClusterHealthCheck error parsing date. executionStartTime:" + testCase.getExecutionStartTime(), e);
                    throw new QuickFabricClientException("runEMRClusterHealthCheck error parsing date. executionStartTime:" + testCase.getExecutionStartTime(), e);
                }
            }
        }

        List<EMRClusterHealthTestCase> emrClusterHealthTestCases = emrClusterHealthCheckDao.getEMRClusterTestSuites(
                healthCheckRequest.getClusterSegment(),
                healthCheckRequest.getClusterType());

        if (emrClusterHealthTestCases.size() == 0) {
            String message = "No Tests found for cluster type: " + healthCheckRequest.getClusterType() +
                    " and cluster segment: " + healthCheckRequest.getClusterSegment();
            logger.error(message);
            throw new QuickFabricServerException(message);
        }

        if (currentTestCases.size() > 0) {
            emrClusterHealthCheckDao.deleteCurrentClusterHealthCheckStatus(healthCheckRequest.getClusterId());
        }

        emrClusterHealthCheckDao.createHealthCheckTestCases(emrClusterHealthTestCases,
                healthCheckRequest.getClusterId(),
                healthCheckRequest.getClusterName(),
                updatedBy);
        return "Successfully created " + emrClusterHealthTestCases.size() + " tests.";
    }

    public String updateEMRClusterHealthTest(String clusterId, ClusterHealthCheckStatusUpdate healthCheckStatusUpdate) {
        ClusterHealthCheckStatusType status;
        try {
            status = ClusterHealthCheckStatusType.valueOf(healthCheckStatusUpdate.getStatus().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new QuickFabricClientException("Unable to find status: " + healthCheckStatusUpdate.getStatus());
        }

        List<ClusterHealthStatus> currentTestCases = emrClusterHealthCheckDao.getEMRClusterHealthHistory(clusterId);
        if (currentTestCases.isEmpty()) {
            logger.error("updateEMRClusterHealthTest called with invalid clusterId:{}", clusterId);
            throw new QuickFabricClientException("Invalid Request, cluster id is invalid");
        }

        ClusterHealthStatus clusterHealthStatus = currentTestCases.stream()
                .filter(test -> test.getExecutionId() == healthCheckStatusUpdate.getExecutionId())
                .findFirst()
                .orElse(null);

        if (clusterHealthStatus == null) {
            logger.error("updateEMRClusterHealthTest called with invalid excecutionId:{}", healthCheckStatusUpdate.getExecutionId());
            throw new QuickFabricClientException("Invalid Request, execution id is invalid");
        }

        emrClusterHealthCheckDao.updateEMRClusterHealthTest(clusterId, healthCheckStatusUpdate.getExecutionId(), status, healthCheckStatusUpdate.getMessage());
        return "Successfully updated clusterId:" + clusterId + " executionId:" + healthCheckStatusUpdate.getExecutionId();
    }

    public List<EMRClusterHealthTestCase> getEMRTestSuites(String clusterType, String clusterSegment) {
        return emrClusterHealthCheckDao.getEMRClusterTestSuites(clusterSegment, clusterType);
    }

    private Date getExiprationDate(ClusterHealthStatus testCase, String clusterType) throws ParseException {
        int timeout = emrClusterHealthCheckDao.getClusterTestTimeout(clusterType);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date executionStart = formatter.parse(testCase.getExecutionStartTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(executionStart);
        calendar.add(Calendar.MINUTE, timeout);
        return calendar.getTime();
    }
}