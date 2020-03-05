package com.intuit.quickfabric.schedulers.helpers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.quickfabric.commons.constants.ApiUrls;
import com.intuit.quickfabric.commons.exceptions.QuickFabricJsonException;
import com.intuit.quickfabric.commons.exceptions.QuickFabricRestHandlerException;
import com.intuit.quickfabric.commons.helper.ConfigHelper;
import com.intuit.quickfabric.commons.vo.ClusterVO;
import com.intuit.quickfabric.commons.vo.StepResponseVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class EMRAWSServiceCallerHelper {


    private static final Logger logger = LogManager.getLogger(EMRAWSServiceCallerHelper.class);

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ConfigHelper configHelper;

    // method to generate restTemplate
    public RestTemplate getRestTemplate(String accountId) {
        RestTemplate restTemplate =
                beanFactory.getBean(RestTemplate.class, accountId);
        return restTemplate;
    }

    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ClusterVO invokeAWSCreateClusterService(String acccountSpecificUrl, String accountId, String json) {

        logger.info("EMRAWSServiceCaller -> invokeAWSCreateClusterService starting cluster with input json" + json);
        try {
            return getRestTemplate(accountId).postForEntity(acccountSpecificUrl, json, ClusterVO.class).getBody();
        } catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeAWSCreateClusterService -> Exception ->" + e.getMessage());
            throw new QuickFabricRestHandlerException("Create Cluster Failed on Serverless Side: {}", e);
        }
    }


    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ClusterVO invokeAWSClusterPreCheckService(String clusterPreCheckUrl, String clusterName, String accountId) {
        logger.info("EMRAWSServiceCaller -> invokeAWSClusterPreCheckService starting for cluster " + clusterName);
        try {
            return getRestTemplate(accountId).getForEntity(clusterPreCheckUrl, ClusterVO.class, clusterName.trim()).getBody();
        } catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeAWSClusterPreCheckService -> Exception ->" + e.getMessage());
            throw new QuickFabricRestHandlerException("Pre-Check for a Cluster Failed on Serverless Side: {}", e);
        }
    }


    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ClusterVO invokeAWSClusterStatusCheckService(String accountSpecificUrl, String clusterId, String accountId) {

        logger.info("EMRAWSServiceCaller -> invokeAWSClusterStatusCheckService starting for cluster" + clusterId);
        try {
            return getRestTemplate(accountId).getForEntity(accountSpecificUrl, ClusterVO.class, clusterId.trim()).getBody();
        } catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeAWSClusterStatusCheckService -> Exception ->" + e.getMessage());
            throw new QuickFabricRestHandlerException("Fetching Cluster Status Failed on Serverless Side: {}", e);
        }

    }

    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ClusterVO invokeAWSValidateEMRClusterService(String accountSpecificUrl, String clusterId, String accountId) {

        logger.info("EMRAWSServiceCaller -> invokeAWSValidateEMRClusterService starting");

        try {
            return getRestTemplate(accountId).getForEntity(accountSpecificUrl, ClusterVO.class, clusterId.trim()).getBody();
        } catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeAWSValidateEMRClusterService -> Exception ->" + e.getMessage());
            throw new QuickFabricRestHandlerException("Validating Cluster Failed on Serverless Side: {}", e);
        }
    }


    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ClusterVO invokeAWSTerminateClusterService(String accountSpecificUrl, String json, String accountId) {

        System.out.println(accountSpecificUrl);
        logger.info("EMRAWSServiceCaller -> invokeAWSTerminateClusterService starting -> " + json);
        try {
            return getRestTemplate(accountId).postForEntity(accountSpecificUrl, json, ClusterVO.class).getBody();
        } catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeAWSTerminateClusterService -> Exception ->" + e.getMessage());
            throw new QuickFabricRestHandlerException("Terminate Cluster Failed on Serverless Side: {}", e);
        }


    }

    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public StepResponseVO invokeAWSClusterAddStepService(String accountSpecificUrl, String json, String accountId) {
        try {
            logger.info("EMRAWSServiceCaller -> invokeAWSClusterAddStepService starting -> " + json);
            return getRestTemplate(accountId).postForEntity(accountSpecificUrl, json, StepResponseVO.class).getBody();
        } catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeAWSClusterAddStepService -> Exception ->" + e.getMessage());
            throw new QuickFabricRestHandlerException("Adding steps to a Cluster Failed on Serverless Side: {}", e);
        }

    }


    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public StepResponseVO invokeAWSClusterValidateStepsService(String accountSpecificUrl, String stepValidateJson, String accountId) {
        try {
            logger.info("EMRAWSServiceCaller -> invokeAWSClusterValidateStepsService starting -> " + stepValidateJson);
            return getRestTemplate(accountId).postForEntity(accountSpecificUrl, stepValidateJson, StepResponseVO.class).getBody();
        } catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeAWSClusterValidateStepsService -> Exception ->" + e.getMessage());
            throw new QuickFabricRestHandlerException("Validating Cluster Steps Failed on Serverless Side: {}", e);
        }


    }

    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 3, backoff = @Backoff(delay = 10000, multiplier = 2))
    public ResponseEntity<String> invokeAWSAutoScalingService(ClusterVO clusterReq) {

        logger.info("EMRAWSServiceCaller -> invokeAWSAutoScalingService starting ");

        ResponseEntity<String> response = null;
        String accountSpecificUrl = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("clusterId", clusterReq.getClusterId());
            jsonObject.put("instanceGroup", clusterReq.getInstanceGroup());
            jsonObject.put("min", clusterReq.getMin());
            jsonObject.put("max", clusterReq.getMax());
            String json = jsonObject.toString();

            accountSpecificUrl = configHelper.getAccountSpecificUrl(clusterReq.getAccount(), ApiUrls.EMR_AUTOSCALING_ADD_URL);
            response = getRestTemplate(clusterReq.getAccount()).postForEntity(accountSpecificUrl, json, String.class);
        } catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeAWSAutoScalingService -> Exception ->" + e.getMessage());
            throw new QuickFabricRestHandlerException("Adding AutoScaling Policy for a Cluster Failed on Serverless Side: {}", e);
        }
        logger.info("EMRAWSServiceCaller -> invokeAWSAutoScalingService completed ");

        return response;
    }


    /**
     * GET to resource manager metrics API
     * https://hadoop.apache.org/docs/current/hadoop-yarn/hadoop-yarn-site/ResourceManagerRest.html
     *
     * @param rmUrl     url for this resource manager
     * @param accountId just to instantiate restTemplate
     * @return
     */

    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ResponseEntity<String> invokeGetClusterMetricsService(String accountSpecificUrl, String rmUrl, String accountId) {

        logger.info("Calling Serverless RM API with rmUrl: " + rmUrl + " for cluster metrics");
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(accountSpecificUrl);
        builder = builder.queryParam("rmUrl", rmUrl);
        builder = builder.queryParam("metricType", "metrics");

        ResponseEntity<String> response = null;
        try {
            response = getRestTemplate(accountId).getForEntity(builder.toUriString(), String.class);
        } catch (Exception e) {
            throw new QuickFabricRestHandlerException("something went wrong in GetClusterMetricsService", e);

        }
        logger.info("EMRAWSServiceCaller -> invokeGetClusterMetricsService completed ");

        return response;
    }


    /**
     * GET to resource manager apps API
     * https://hadoop.apache.org/docs/current/hadoop-yarn/hadoop-yarn-site/ResourceManagerRest.html
     *
     * @param rmUrl url for this resource manager
     * @return
     * @throws Exception
     */

    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ResponseEntity<String> invokeGetRmAppsService(String rmUrl, String accountId) {

        logger.info("Calling Serverless RM API with rmUrl: " + rmUrl + " for cluster applications statistics");

        long tenMinutesAgo = System.currentTimeMillis() - 10 * 60 * 1000L;
        String accountSpecificUrl = configHelper.getAccountSpecificUrl(accountId, ApiUrls.RM_PROXY_URL_SUFFIX);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(accountSpecificUrl);

        builder = builder.queryParam("rmUrl", rmUrl);
        builder = builder.queryParam("metricType", "apps");
        builder = builder.queryParam("startedTimeBegin", tenMinutesAgo);

        ResponseEntity<String> response = null;
        try {
            response = getRestTemplate(accountId).getForEntity(builder.toUriString(), String.class);
        } catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeGetRmAppsService -> Exception ->" + e.getMessage());
            throw new QuickFabricRestHandlerException("something went wrong in GetRmAppsService", e);

        }

        logger.info("EMRAWSServiceCaller -> invokeGetRmAppsService completed ");
        return response;
    }

    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ResponseEntity<String> invokeAWSDNSFlip(String clusterName, String account, String dnsName, String masterIp, String actionType) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("clusterName", clusterName);
            jsonObject.put("account", account);
            jsonObject.put("dnsName", dnsName);
            jsonObject.put("masterIp", masterIp);
            jsonObject.put("action", actionType);
        } catch (JSONException e) {
            throw new QuickFabricJsonException("error while creating json object", e);
        }
        String json = jsonObject.toString();

        String accountSpecificUrl = configHelper.getAccountSpecificUrl(account, ApiUrls.DNS_FLIP_URL);
        logger.info("EMRAWSServiceCaller -> invokeAWSDNSFlip starting -> " + json);
        ResponseEntity<String> response = null;
        try {
            response = getRestTemplate(account).postForEntity(accountSpecificUrl, json, String.class);
        } catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeAWSDNSFlip -> Exception ->" + e.getMessage());
            throw new QuickFabricRestHandlerException("DNS Flip for Cluster Failed on Serverless Side: {}", e);
        }
        logger.info("EMRAWSServiceCaller -> invokeAWSDNSFlip completed ");
        return response;
    }

    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ResponseEntity<String> invokeGetEMRCostService(String accountSpecificUrl, String clusterName, String accountId) {

        logger.info("EMRAWSServiceCaller -> invokeGetEMRCostService-> URL: " + accountSpecificUrl);
        logger.info("EMRAWSServiceCaller -> invokeGetEMRCostService starting -> ");
        ResponseEntity<String> response = null;
        try {
            response = getRestTemplate(accountId).getForEntity(accountSpecificUrl, String.class, clusterName);
        } catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeGetEMRCostService -> Exception ->" + e.getMessage());
            throw new QuickFabricRestHandlerException("Fetching Cluster Cost Failed on Serverless Side: {}", e);
        }
        logger.info("EMRAWSServiceCaller -> invokeGetEMRCostService completed ");
        return response;

    }
}
