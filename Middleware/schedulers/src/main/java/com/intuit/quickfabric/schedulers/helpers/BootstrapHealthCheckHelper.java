package com.intuit.quickfabric.schedulers.helpers;

import com.intuit.quickfabric.commons.constants.ApiUrls;
import com.intuit.quickfabric.commons.constants.HealthCheckConstants;
import com.intuit.quickfabric.commons.exceptions.QuickFabricRestHandlerException;
import com.intuit.quickfabric.commons.helper.ConfigHelper;
import com.intuit.quickfabric.commons.utils.CommonUtils;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.schedulers.dao.ClusterHealthCheckDao;
import com.intuit.quickfabric.schedulers.dao.EMRClusterMetadataDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BootstrapHealthCheckHelper {
    private final Logger logger = LogManager.getLogger(BootstrapHealthCheckHelper.class);

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    ClusterHealthCheckDao clusterHealthCheckDao;

    @Autowired
    EMRClusterMetadataDao clusterMetadataDao;

    @Autowired
    ConfigHelper configHelper;

    public void verifyNoOfBootstraps() {
        List<EMRClusterHealthTestCase> stepsCriteria = clusterHealthCheckDao.getFunctionalTestSuites(HealthCheckConstants.NoOfBootstraps);
        List<ClusterHealthStatus> pendingHealthTestCases = clusterHealthCheckDao.getNewHealthTestCases();
        List<ClusterHealthStatus> bootstrapTests = pendingHealthTestCases.stream()
                .filter(x -> x.getTestName().equalsIgnoreCase(HealthCheckConstants.NoOfBootstraps))
                .collect(Collectors.toList());
        logger.info("Number of bootstrap test cases found to execute: " + bootstrapTests.size());

        for (ClusterHealthStatus test : bootstrapTests) {
            try {

                // mark initiated
                clusterHealthCheckDao.updateTestCaseStatus(test.getExecutionId(), ClusterHealthCheckStatusType.INITIATED, "initiated at verifyNumberOfBootstraps");
                ClusterVO clusterMetadata = clusterMetadataDao.getClusterMetadata(test.getClusterId());

                if (clusterMetadata == null) {
                    logger.error("cluster metadata not found for clusterID:" + test.getClusterId());
                    continue;
                }

                // Call
                RestTemplate restTemplate = beanFactory.getBean(RestTemplate.class, clusterMetadata.getAccount());
                String url = configHelper.getAccountSpecificUrl(clusterMetadata.getAccount(), ApiUrls.CLUSTER_BOOTSTRAP_TEST_SUFFIX);

                logger.info("Calling bootstrap test for clusterId:" + test.getClusterId());
                ResponseEntity<BootstrapTestResponse> entityResponse = null;
                try {
                    entityResponse = restTemplate.getForEntity(url, BootstrapTestResponse.class, test.getClusterId());
                } catch (Exception e) {
                    throw new QuickFabricRestHandlerException("Rest error happened while getting bootstrap test response.", e);
                }

                BootstrapTestResponse response = entityResponse.getBody();
                logger.info("Response for bootstrap test for clusterId:" + test.getClusterId() + " response:" + entityResponse.toString());

                //Verify
                EMRClusterHealthTestCase testCriteria = stepsCriteria.stream()
                        .filter(x -> x.getClusterSegment().equalsIgnoreCase(test.getClusterSegment())
                                && x.getClusterType().equalsIgnoreCase(test.getClusterType()))
                        .findFirst()
                        .orElse(null);

                if (testCriteria != null) {
                    Optional<Integer> bootstrapsRequired = CommonUtils.tryParseInt(testCriteria.getTestCriteria());
                    ClusterHealthCheckStatusType newStatus;
                    String remark = "";
                    if (!bootstrapsRequired.isPresent()) {
                        // mark failed
                        newStatus = ClusterHealthCheckStatusType.FAILED;
                        remark = "No criteria was found for bootstrap test";

                    } else {
                        if (bootstrapsRequired.get() == response.getBootstrapCount()) {
                            newStatus = ClusterHealthCheckStatusType.SUCCESS;
                            remark = "Successfully completed all Bootstraps(" + response.getBootstrapCount() + ")";
                        } else {
                            newStatus = ClusterHealthCheckStatusType.FAILED;
                            remark = "Did not complete all the Bootstraps. Required:" + bootstrapsRequired.get() + " Completed:" + response.getBootstrapCount();
                        }
                    }
                    clusterHealthCheckDao.updateTestCaseStatus(test.getExecutionId(), newStatus, remark);
                } else {
                    logger.error("verifyNumberOfBootstraps parent test case not found for test case: ", test);
                    clusterHealthCheckDao.updateTestCaseStatus(test.getExecutionId(), ClusterHealthCheckStatusType.FAILED, "marking failed, test criteria not found.");
                }
            } catch (Exception ex) {
                logger.error("Exception executing test case: " + test, ex);
                clusterHealthCheckDao.updateTestCaseStatus(test.getExecutionId(), ClusterHealthCheckStatusType.FAILED, "marking failed, exception executing test case. Ex:" + ex.getMessage());
            }
        }
    }
}
