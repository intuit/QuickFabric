package com.intuit.quickfabric.schedulers.helpers;

import org.apache.commons.lang3.StringUtils;
import com.intuit.quickfabric.commons.constants.ApiUrls;
import com.intuit.quickfabric.commons.constants.HealthCheckConstants;
import com.intuit.quickfabric.commons.exceptions.QuickFabricRestHandlerException;
import com.intuit.quickfabric.commons.helper.ConfigHelper;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.schedulers.dao.ClusterHealthCheckDao;
import com.intuit.quickfabric.schedulers.dao.EMRClusterMetadataDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AutoScalingHealthCheckHelper {

    private final Logger logger = LogManager.getLogger(AutoScalingHealthCheckHelper.class);

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    ClusterHealthCheckDao clusterHealthCheckDao;

    @Autowired
    EMRClusterMetadataDao clusterMetadataDao;

    @Autowired
    ConfigHelper configHelper;

    public void checkAutoScalingConfiguration() {
        List<EMRClusterHealthTestCase> stepsCriteria = clusterHealthCheckDao.getFunctionalTestSuites(HealthCheckConstants.AutoScaling);
        List<ClusterHealthStatus> pendingHealthTestCases = clusterHealthCheckDao.getNewHealthTestCases();
        List<ClusterHealthStatus> autoScalingTests = pendingHealthTestCases.stream()
                .filter(x -> x.getTestName().equalsIgnoreCase(HealthCheckConstants.AutoScaling))
                .collect(Collectors.toList());
        logger.info("Number of auto scaling test cases found to execute: " + autoScalingTests.size());

        for (ClusterHealthStatus test : autoScalingTests) {
            try {
                clusterHealthCheckDao.updateTestCaseStatus(test.getExecutionId(), ClusterHealthCheckStatusType.INITIATED, "initiated at checkAutoScalingConfiguration");
                ClusterVO clusterMetadata = clusterMetadataDao.getClusterMetadata(test.getClusterId());

                if (clusterMetadata == null) {
                    logger.error("cluster metadata not found for clusterID:" + test.getClusterId());
                    continue;
                }
                
                //Marking Autoscaling testsuite as Failed when there is no auto-scaling attached to the cluster
                if(StringUtils.isBlank(clusterMetadata.getInstanceGroup())) {
                    logger.error("No Autoscaling attached to this cluster during creation. "
                            + "Marking Autoscaling test suites as Failed for cluster: " + clusterMetadata.getClusterId());
                    clusterHealthCheckDao.updateTestCaseStatus(test.getExecutionId(), ClusterHealthCheckStatusType.FAILED,
                            "No Autoscaling attached to this cluster during creation.");
                    continue;
                }

                String url = configHelper.getAccountSpecificUrl(clusterMetadata.getAccount(), ApiUrls.CLUSTER_AUTOSCALING_TEST_SUFFIX);
                RestTemplate restTemplate = beanFactory.getBean(RestTemplate.class, clusterMetadata.getAccount());

                logger.info("Calling bootstrap test for clusterId:" + test.getClusterId());
                AutoScalingTestResponse response = null;
                try {
                    response = restTemplate.getForEntity(url, AutoScalingTestResponse.class, test.getClusterId(), clusterMetadata.getInstanceGroup()).getBody();
                } catch (Exception e) {
                    throw new QuickFabricRestHandlerException("Rest error happened while getting auto scaling test response.", e);
                }

                logger.info("Response for bootstrap test for clusterId:" + test.getClusterId() + " response:" + response);
                EMRClusterHealthTestCase testCriteria = stepsCriteria.stream()
                        .filter(x -> x.getClusterSegment().equalsIgnoreCase(test.getClusterSegment())
                                && x.getClusterType().equalsIgnoreCase(test.getClusterType()))
                        .findFirst()
                        .orElse(null);

                String[] pairValues = testCriteria.getTestCriteria().split(",");
                int testCriteriaMin = Integer.parseInt(pairValues[0]);
                Integer receivedMin = response.getMin();
                Integer receivedMax = response.getMax();
                Integer testCriteriaMax = null;
                if (pairValues.length > 1) {
                    testCriteriaMax = Integer.parseInt(pairValues[1]);
                }

                if (receivedMin == null || (receivedMax == null && testCriteriaMax != null)) {
                    logger.error("did not receive min or max from rest call. response: " + response);
                    clusterHealthCheckDao.updateTestCaseStatus(test.getExecutionId(), ClusterHealthCheckStatusType.FAILED,
                            "Did not receive min or max from rest call. Received testCriteriaMin:" + receivedMin + " Received testCriteriaMax:" + receivedMax);
                } else if ((receivedMin == testCriteriaMin && testCriteriaMax == null)
                        || (testCriteriaMax != null && receivedMin == testCriteriaMin && receivedMax == testCriteriaMax)) {
                    clusterHealthCheckDao.updateTestCaseStatus(test.getExecutionId(),
                            ClusterHealthCheckStatusType.SUCCESS,
                            "Success matching testCriteriaMin and testCriteriaMax. Expected testCriteriaMin:" + testCriteriaMin
                                    + " Expected testCriteriaMax:" + testCriteriaMax + " Received testCriteriaMin:"
                                    + receivedMin + " Received testCriteriaMax:" + receivedMax);
                } else {
                    clusterHealthCheckDao.updateTestCaseStatus(test.getExecutionId(),
                            ClusterHealthCheckStatusType.FAILED,
                            "Failed matching testCriteriaMin and testCriteriaMax. Expected testCriteriaMin:" + testCriteriaMin
                                    + " Expected testCriteriaMax:" + testCriteriaMax + " Received testCriteriaMin:"
                                    + receivedMin + " Received testCriteriaMax:" + receivedMax);
                }
            } catch (Exception ex) {
                clusterHealthCheckDao.updateTestCaseStatus(test.getExecutionId(), ClusterHealthCheckStatusType.FAILED, "Exception occur: " + ex.getMessage());
                logger.error("auto scaling test failed. Test: " + test, ex);
            }
        }
    }
}
