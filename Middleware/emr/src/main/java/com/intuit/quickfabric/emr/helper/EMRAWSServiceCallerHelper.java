package com.intuit.quickfabric.emr.helper;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intuit.quickfabric.commons.constants.ApiUrls;
import com.intuit.quickfabric.commons.exceptions.QuickFabricJsonException;
import com.intuit.quickfabric.commons.exceptions.QuickFabricRestHandlerException;
import com.intuit.quickfabric.commons.helper.ConfigHelper;
import com.intuit.quickfabric.commons.vo.ClusterVO;
import com.intuit.quickfabric.commons.vo.StepResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;

@Component
public class EMRAWSServiceCallerHelper {

    private static final Logger logger = LogManager.getLogger(EMRAWSServiceCallerHelper.class);

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private ConfigHelper configHelper;


    // method to generate restTemplate
    public RestTemplate getRestTemplate(String accountId) {
        RestTemplate restTemplate =
                beanFactory.getBean(RestTemplate.class, accountId);
        return restTemplate;
    }

    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ClusterVO invokeAWSCreateClusterService(String json) {

        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        String accountId = jsonObject.get("account").getAsString();
        String accountSpecificUrl = configHelper.getAccountSpecificUrl(accountId, ApiUrls.CREATE_CLUSTER_URL_SUFFIX);
        
        logger.info("EMRAWSServiceCaller -> invokeAWSCreateClusterService starting cluster with input json" + json);
        ClusterVO response = null;
        try {
            response = getRestTemplate(accountId).postForEntity(accountSpecificUrl, json, ClusterVO.class).getBody();
        }catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeAWSCreateClusterService -> Exception ->" + e.getMessage(), e);
            throw new QuickFabricRestHandlerException("something went wrong with AWSCreateClusterService",e);
        }
        return response;
    }


    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ClusterVO invokeAWSClusterPreCheckService(String clusterName, String accountId)  {
        logger.info("EMRAWSServiceCaller -> invokeAWSClusterPreCheckService starting for cluster" + clusterName);

        //Calling status check api called before create cluster
        ClusterVO response = null;
        
            String accountSpecificUrl = configHelper.getAccountSpecificUrl(accountId, ApiUrls.PRECHECK_CLUSTER_URL_SUFFIX);
        try {
            response = getRestTemplate(accountId).getForEntity(accountSpecificUrl, ClusterVO.class, clusterName.trim()).getBody();
        }catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeAWSClusterPreCheckService -> Exception ->" + e.getMessage(), e);
            throw new QuickFabricRestHandlerException("something went wrong with AWSClusterPreCheckService",e);
        }
        logger.info("EMRAWSServiceCaller ->invokeAWSClusterPreCheckService completed for cluster" + clusterName);
        return response;

    }

    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ClusterVO invokeAWSClusterStatusCheckService(String clusterId, String accountId) {
        logger.info("EMRAWSServiceCaller -> invokeAWSClusterStatusCheckService starting for cluster" + clusterId);
        //Calling status check api called before create cluster
        try {
            String accountSpecificUrl = configHelper.getAccountSpecificUrl(accountId, ApiUrls.CLUSTER_STATUS_CHECK_URL_SUFFIX);
            ClusterVO response=getRestTemplate(accountId).getForEntity(accountSpecificUrl, ClusterVO.class, clusterId.trim()).getBody();
            if(response.getClusterStatus() == null){
                throw new QuickFabricRestHandlerException("ClusterStatus coming NULL from AWSClusterStatusCheckService api call");
            }
            return response;
        }catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeAWSClusterStatusCheckService -> Exception ->" + e.getMessage());
            throw new QuickFabricRestHandlerException("something went wrong in AWSClusterStatusCheckService api call",e);
        }

    }

    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ClusterVO invokeAWSValidateEMRClusterService(String clusterId, String accountId)  {

        String accountSpecificUrl = configHelper.getAccountSpecificUrl(accountId, ApiUrls.CLUSTER_VALIDATE_URL_SUFFIX);
        logger.info("EMRAWSServiceCaller -> invokeAWSValidateEMRClusterService starting -> ");
        
        ClusterVO response = null;
        try {
            response = getRestTemplate(accountId).getForEntity(accountSpecificUrl, ClusterVO.class, clusterId.trim()).getBody();
            if (StringUtils.isBlank(response.getClusterName())) {
            	throw new QuickFabricRestHandlerException("Cluster Creation Failed with ClusterName coming NULL from backend Service");
            }
        }catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeAWSValidateEMRClusterService -> Exception ->" + e.getMessage(), e);
            throw new QuickFabricRestHandlerException("something went wrong in AWSValidateEMRClusterService call",e);
        }
        logger.info("EMRAWSServiceCaller -> invokeAWSValidateEMRClusterService completed ");

        return response;
    }


    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ClusterVO invokeAWSTerminateClusterService(String json , String accountId){
        String accountSpecificUrl = configHelper.getAccountSpecificUrl(accountId, ApiUrls.TERMINATE_CLUSTER_URL_SUFFIX);
        logger.info("EMRAWSServiceCaller -> invokeAWSTerminateClusterService starting -> " + json);
        try {
            return getRestTemplate(accountId).postForEntity(accountSpecificUrl, json, ClusterVO.class).getBody();
        }catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeAWSTerminateClusterService -> Exception ->" + e.getMessage(), e);
            throw new QuickFabricRestHandlerException("something went wrong during AWSTerminateClusterService",e);
        }




    }

    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public StepResponseVO invokeAWSClusterAddStepService(String json, String accountId) {
        String accountSpecificUrl = configHelper.getAccountSpecificUrl(accountId, ApiUrls.ADD_CUSTOM_STEP_URL_SUFFIX);
        logger.info("EMRAWSServiceCaller -> invokeAWSClusterAddStepService starting -> " + json);
        try {
            return getRestTemplate(accountId).postForEntity(accountSpecificUrl, json, StepResponseVO.class).getBody();
        }catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeAWSClusterAddStepService -> Exception ->" + e.getMessage(), e);
            throw new QuickFabricRestHandlerException("something went wrong in AWSClusterAddStepService call",e);
        }
    }

    
    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ClusterVO invokeAWSDNSFlip(String clusterName, String account, String dnsName, String masterIp, String actionType) {
    	String dnsFlipRequestJson;
    	try {
	        JSONObject jsonObject = new JSONObject();
	        jsonObject.put("clusterName", clusterName);
	        jsonObject.put("account", account);
	        jsonObject.put("dnsName", dnsName);
	        jsonObject.put("masterIp", masterIp);
	        jsonObject.put("action", actionType);
	        dnsFlipRequestJson = jsonObject.toString();
    	}catch(JSONException e){
            logger.error("EMRAWSServiceCaller -> invokeAWSDNSFlip -> JSONException ->" + e.getMessage(),e);
            throw new QuickFabricJsonException("json exception in AWSDNSFlip method", e);
    	}
        String accountSpecificUrl = configHelper.getAccountSpecificUrl(account, ApiUrls.DNS_FLIP_URL);
        logger.info("EMRAWSServiceCaller -> invokeAWSDNSFlip starting -> " + dnsFlipRequestJson);
        try {
        	return getRestTemplate(account).postForEntity(accountSpecificUrl, dnsFlipRequestJson, ClusterVO.class).getBody();
        }catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeAWSDNSFlip -> HttpStatusCodeException ->" + e.getMessage(), e);
            throw new QuickFabricRestHandlerException("something went wrong in AWSDNSFlip api call", e);
        }

    }

    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ResponseEntity<String> invokeGetRmAppsStatusService(String rmUrl, String states,
            String finalStatus, String accountId, long finishedTimeBegin, long finishedTimeEnd) {

        logger.info("Calling Serverless RM API with rmUrl: " + rmUrl + " for cluster applications statistics");

        String accountSpecificUrl = configHelper.getAccountSpecificUrl(accountId, ApiUrls.RM_PROXY_URL_SUFFIX);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(accountSpecificUrl);

        builder = builder.queryParam("rmUrl", rmUrl);
        builder = builder.queryParam("metricType", "apps");

        if (states != null) {
            logger.info("Adding query param states=" + states);
            builder = builder.queryParam("states", states);
        }

        if (finalStatus != null) {
            logger.info("Adding query param finalStatus=" + finalStatus);
            builder = builder.queryParam("finalStatus", finalStatus);
        }

        //-1 means not needed (running)
        if (finishedTimeBegin != -1) {
            logger.info("Adding query param finishedTimeBegin=" + finishedTimeBegin);
            logger.info("Human readable timestamp: " + new Timestamp(finishedTimeBegin).toString());
            builder = builder.queryParam("finishedTimeBegin", finishedTimeBegin);
        }

        if (finishedTimeEnd != -1) {
            logger.info("Adding query param finishedTimeEnd=" + finishedTimeEnd);
            logger.info("Human readable timestamp: " + new Timestamp(finishedTimeEnd).toString());
            builder = builder.queryParam("finishedTimeEnd", finishedTimeEnd);
        }

        ResponseEntity<String> response;
        try {
            response = getRestTemplate(accountId).getForEntity(builder.toUriString(), String.class);
        } catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeGetAppStatsService -> Exception ->" + e.getMessage());
            String message = "Exception from Serverless RM Proxy Service for App Stats Metrics -> Exception: " + e.getMessage();
            throw new QuickFabricRestHandlerException(message, e);
        }
        logger.info("EMRAWSServiceCaller -> invokeGetAppStatsService completed ");

        return response;
    }

    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ResponseEntity<String> invokeGetServiceNowTicketService(String ticketId, String account)  {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(configHelper.getConfigValue("snow_url", account) + ApiUrls.SNOW_TABLE_PATH);

        String fields = "number,description,state,assignment_group,short_description,assigned_to,"
                + "u_pending_code,u_ud_parent,u_reported_by,work_start,work_end";

        builder.queryParam("number", ticketId)
                .queryParam("sysparm_display_value", true)
                .queryParam("sysparm_exclude_reference_link", false)
                .queryParam("sysparm_fields", fields)
                .queryParam("sysparm_limit", 1);

        logger.info("Sending GET to " + builder.toUriString());

        ResponseEntity<String> response = null;
        try {
            RestTemplate restTemplate = getRestTemplate(account);
            addBasicAuthForSnow(restTemplate, account);
            response = restTemplate.getForEntity(builder.toUriString(), String.class);

        } catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeGetServiceNowTicketService -> Exception -> " + e.getMessage(), e);
            throw new QuickFabricRestHandlerException("Something went wrong with Snow Api call",e);
        }
        return response;
    }

    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public String invokeGetJiraTicketService(String ticketId, String account)  {

        String url = configHelper.getConfigValue("jira_url", account) + ApiUrls.JIRA_ISSUE_PATH + ticketId;

        logger.info("Sending GET to {}", url);


        try {
            RestTemplate restTemplate = getRestTemplate(account);
            addBasicAuthForJira(restTemplate, account);
            return restTemplate.getForEntity(url, String.class).getBody();

        } catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeGetJiraTicketService -> Exception ->{}", e.getMessage());
            throw new QuickFabricRestHandlerException("Something went wrong with JIRA Api call",e);
        }

    }


    private void addBasicAuthForSnow(RestTemplate restTemplate, String account) {
        String snowUser = configHelper.getConfigValue("snow_user", account);
        String snowPw = configHelper.getConfigValue("snow_password", account);
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(snowUser, snowPw));
    }

    private void addBasicAuthForJira(RestTemplate restTemplate, String account) {
        String jiraUser = configHelper.getConfigValue("jira_user", account);
        String jiraPw = configHelper.getConfigValue("jira_password", account);
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(jiraUser, jiraPw));
    }


    @Retryable(value = {QuickFabricRestHandlerException.class}, maxAttempts = 2, backoff = @Backoff(delay = 10000))
    public ResponseEntity<String> invokeDrElephantJobSearch(String drElephantUrl, long from, long to, String accountId) {

        logger.info("Calling Serverless RM API with drElephantUrl: " + drElephantUrl + " for dr elephant report");

        String accountSpecificUrl = configHelper.getAccountSpecificUrl(accountId, ApiUrls.RM_PROXY_URL_SUFFIX);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(accountSpecificUrl);

        builder = builder.queryParam("rmUrl", drElephantUrl);
        builder = builder.queryParam("metricType", "drElephant");
        builder = builder.queryParam("finishedTimeBegin", from);
        builder = builder.queryParam("finishedTimeEnd", to);

        ResponseEntity<String> response = null;
        try {
            response = getRestTemplate(accountId).getForEntity(builder.toUriString(), String.class);
        } catch (Exception e) {
            logger.error("EMRAWSServiceCaller -> invokeDrElephantJobSearch -> Exception ->" + e.getMessage());
            String drElephantException = "Exception from Serverless RM Proxy Service for Dr Elephant -> Exception: " + e.getMessage();
            throw new QuickFabricRestHandlerException(drElephantException, e);
        }
        logger.info("EMRAWSServiceCaller -> invokeDrElephantJobSearch completed ");

        return response;
    }

}