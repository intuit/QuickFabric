package com.intuit.quickfabric.schedulers.helpers;

import com.intuit.quickfabric.commons.constants.ApiUrls;
import com.intuit.quickfabric.commons.constants.HealthCheckConstants;
import com.intuit.quickfabric.commons.exceptions.QuickFabricRestHandlerException;
import com.intuit.quickfabric.commons.exceptions.QuickFabricServerException;
import com.intuit.quickfabric.commons.helper.ConfigHelper;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.schedulers.dao.ClusterHealthCheckDao;
import com.intuit.quickfabric.schedulers.dao.EMRClusterMetadataDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConnectivityHealthCheckHelper {
    private final Logger logger = LogManager.getLogger(ConnectivityHealthCheckHelper.class);

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    ClusterHealthCheckDao clusterHealthCheckDao;

    @Autowired
    EMRClusterMetadataDao clusterMetadataDao;

    @Autowired
    ConfigHelper configHelper;

    public void initiateConnectivityTests() {
        List<ClusterHealthStatus> pendingHealthTestCases = clusterHealthCheckDao.getNewHealthTestCases();
        List<ClusterHealthStatus> connectivityTests = pendingHealthTestCases.stream()
                .filter(x -> x.getTestName().equalsIgnoreCase(HealthCheckConstants.Connectivity))
                .collect(Collectors.toList());
        logger.info("Number of Connectivity test cases found to execute: " + connectivityTests.size());

        for (ClusterHealthStatus test : connectivityTests) {
            try {
                // Construct request
                ConnectivityTestRequest request = createConnectivityTestRequest(test);

                // Call API
                getConnectivityResponse(request, test);
                logger.info("marking test case as initiated, test case: " + test);
                clusterHealthCheckDao.updateTestCaseStatus(test.getExecutionId(), ClusterHealthCheckStatusType.INITIATED, "initiated connectivity test");
            } catch (Exception ex) {
                logger.error("Exception executing test case: " + test, ex);
                clusterHealthCheckDao.updateTestCaseStatus(test.getExecutionId(), ClusterHealthCheckStatusType.FAILED, "marking failed, exception executing test case. Ex:" + ex.getMessage());
            }
        }
    }

    private ConnectivityTestRequest createConnectivityTestRequest(ClusterHealthStatus test) {
        ConnectivityTestRequest request = new ConnectivityTestRequest();
        ClusterVO clusterMetadata = clusterMetadataDao.getClusterMetadata(test.getClusterId());
        if (clusterMetadata == null) {
            throw new QuickFabricServerException("cluster metadata not found for clusterID:" + test.getClusterId());
        }

        request.setAccount(clusterMetadata.getAccount());
        request.setClusterType(clusterMetadata.getType().getValue());
        request.getParametersRequest().setDnsName(clusterMetadata.getDnsName());

        request.setExecutionId(test.getExecutionId());
        request.setClusterId(test.getClusterId());
        request.setClusterName(test.getClusterName()); //HARDCODING
        request.getParametersRequest().setHeadlessUser("sys_sbseg_" + test.getClusterSegment()); // sys_sbseg_ + segment = sys_sbseg_marketing

        return request;
    }

    private ConnectivityTestResponse getConnectivityResponse(ConnectivityTestRequest request, ClusterHealthStatus test) {
        RestTemplate restTemplate = beanFactory.getBean(RestTemplate.class, request.getAccount());
        String url = configHelper.getAccountSpecificUrl(request.getAccount(), ApiUrls.CLUSTER_HEALTH_CHECK_SUFFIX);

        logger.info("Calling connectivity test for clusterId:" + test.getClusterId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ConnectivityTestRequest> requestHttpEntity = new HttpEntity<>(request, headers);
        ConnectivityTestResponse response = null;
        try {
            response = restTemplate.postForEntity(url, requestHttpEntity, ConnectivityTestResponse.class).getBody();
        } catch (Exception e) {
            throw new QuickFabricRestHandlerException("Rest error happened while getting connectivity test response.", e);
        }

        logger.info("Connectivity test response for clusterId:" + test.getClusterId() + " response:" + response.toString());
        return response;
    }
}
