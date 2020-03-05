
package com.intuit.quickfabric.emr.helper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.intuit.quickfabric.commons.constants.Roles;
import com.intuit.quickfabric.commons.domain.QuickFabricAuthenticationToken;
import com.intuit.quickfabric.commons.exceptions.*;
import com.intuit.quickfabric.commons.helper.ConfigHelper;
import com.intuit.quickfabric.commons.security.AccessControl;
import com.intuit.quickfabric.commons.utils.AWSEmailUtil;
import com.intuit.quickfabric.commons.utils.ReportBuilder;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.emr.dao.EMRClusterMetadataDao;
import com.intuit.quickfabric.emr.dao.EMRClusterStepsDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EMRClusterManagementHelper {
    private static final String CLUSTER_NOT_PRESENT = "Cluster not present";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EMRClusterMetricsHelper emrClusterMetricsHelper;

    @Autowired
    EMRAWSServiceCallerHelper awsServiceCallerHelper;

    @Autowired
    ValidationHelper validationHelper;

    @Autowired
    EMRClusterMetadataHelper emrClusterMetadataHelper;

    @Autowired
    EMRClusterMetadataDao metadataDao;

    @Autowired
    TicketValidationHelper ticketValidationHelper;

    @Autowired
    ConfigHelper configHelper;

    @Autowired
    AWSEmailUtil emailUtil;

    @Autowired
    EMRClusterStepsDao emrClusterStepsDao;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private static final Logger logger = LogManager.getLogger(EMRClusterManagementHelper.class);


    public boolean isClusterPresent(String clusterName, String accountId) {
        logger.info("EMRClusterManagementHelper API->GETClusterIfPresentBeforeCreatingNewCluster starting for cluster"
                + clusterName);

        // Calling status check api called before create cluster
        ClusterVO clusterResponse = awsServiceCallerHelper.invokeAWSClusterPreCheckService(clusterName, accountId);
        boolean isPresent = false;

        // This enables Deserialization of Enum fields such as ClusterStatus and
        // ClusterType
        if (clusterResponse.getClusterStatus().equals(ClusterStatus.ClusterAlreadyExists)) {
            // returns true if the cluster not present ,to allow creation of new cluster.
            isPresent = true;
        }

        logger.info("isClusterPresent completed for cluster: " + clusterName + " isPresent:" + isPresent);

        return isPresent;
    }


    public ClusterVO createEMRCluster(ClusterRequest clusterRequest, boolean nonHARotateFlag) {

        //Access Validation
        if (AccessControl.isNotAccessible(clusterRequest.getSegment(), clusterRequest.getAccount(), Roles.CREATE_CLUSTER)
                || AccessControl.isNotAccessible(clusterRequest.getSegment(), clusterRequest.getAccount(), Roles.AMI_ROTATION)) {
            throw new QuickFabricUnauthorizedException();
        }

        //Change Request Ticket Validation
        ticketValidationHelper.validRequestTicket(clusterRequest);
        logger.info("Ticket is approved! Proceeding with create cluster.");

        logger.info("EMRClusterManagementHelper API->createEMRCluster starting for cluster "
                + clusterRequest.getClusterName());
        String json = null;
        logger.info("EMRClusterManagementHelper API->validateCreateClusterRequest"
                + clusterRequest.getClusterName());
        validationHelper.validateCreateClusterRequest(clusterRequest);
        ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        if (!nonHARotateFlag && isClusterPresent(clusterRequest.getClusterName(), clusterRequest.getAccount())) {
            throw new QuickFabricClientException("Cluster with name" + clusterRequest.getClusterName() + " already exists.");
        }

        ClusterVO newClusterSteps = new ClusterVO();
        mapCreateClusterReqToClusterVO(clusterRequest, newClusterSteps);
        try {
            json = ow.writeValueAsString(newClusterSteps);
        } catch (JsonProcessingException e) {
            logger.error("error during json object Deserialization with error {}", e.getMessage());
            throw new QuickFabricJsonException("error during json object Deserialization", e);
        }
        logger.info("EMRClusterManagementHelper API->createEMRCluster -> JSON request to AWS: " + json);

        // call status API to check if cluster already present, if not then create new cluster
        ClusterVO newClusterResponse = awsServiceCallerHelper.invokeAWSCreateClusterService(json);

        //mapping ClusterRequest to newClusterResponseVO
        mapOtherClusterDetailsFromResponse(newClusterResponse, clusterRequest);

        //calling saveEMRClusterMetadata inside @transactional to save cluster data into DB only if createEMRCluster method was called from API
        //sending email notification to user and admin(s) about the cluster creation action if call from API
        if (!nonHARotateFlag) {
            saveEMRClusterMetadata(newClusterSteps, newClusterResponse);
            sendClusterCreatedEmail(newClusterResponse);
        }

        return newClusterResponse;

    }

    /**
     * its a method to update clusterMetadata , bootstrap steps and custom Steps
     *
     * @param newClusterSteps
     * @param newClusterResponse
     */
    private void saveEMRClusterMetadata(ClusterVO newClusterSteps, ClusterVO newClusterResponse) {
        newClusterSteps.setClusterId(newClusterResponse.getClusterId());

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);
        try {
            //Saving custom steps into RDS when they exist
            if (null != newClusterSteps.getSteps() && newClusterSteps.getSteps().size() > 0) {
                emrClusterMetricsHelper.saveClusterStepsRequest(newClusterSteps);
            }

            //Saving bootstrap actions into RDS when they exist
            if (null != newClusterSteps.getBootstrapActions() && newClusterSteps.getBootstrapActions().size() > 0) {
                emrClusterMetricsHelper.saveClusterBootstrapActionsRequest(newClusterSteps);
            }
            newClusterSteps = null;
            logger.info("EMRClusterManagementHelper API->saveNewClusterDetailsToRDS starting");
            emrClusterMetadataHelper.saveNewClusterDetailsToRDS(newClusterResponse);
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            throw e;
        }
    }

    /**
     * method to update Original cluster status , new Cluster metadata
     *
     * @param originalClusterResponse
     * @param newClusterResponse
     */
    private void saveEMRClusterMetadataNonHA(ClusterVO originalClusterResponse, ClusterVO newClusterResponse) {

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);
        try {

            //UPDATING New Cluster ID in Old One
            metadataDao.updateNewClusterByOriginalClusterId(newClusterResponse.getOriginalClusterId(), newClusterResponse.getClusterId());

            //updating status of original Terminated cluster
            emrClusterMetricsHelper.updateClusterStatusByClusterId(originalClusterResponse);

            //Saving bootstrap actions into RDS when they exist
            if (null != newClusterResponse.getBootstrapActions() && newClusterResponse.getBootstrapActions().size() > 0) {
                emrClusterMetricsHelper.saveClusterBootstrapActionsRequest(newClusterResponse);
            }
            logger.info("EMRClusterManagementHelper API->saveNewClusterDetailsToRDS starting");
            //Saving clusterMetadata into RDS
            emrClusterMetadataHelper.saveNewClusterDetailsToRDS(newClusterResponse);

            transactionManager.commit(txStatus);
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            throw e;
        }
    }


    private void mapOtherClusterDetailsFromResponse(ClusterVO newCluster, ClusterRequest clusterDetails) {
        //Converting Cluster hardware details to be stored as a JSON in Database
        try {
            JSONObject clusterHardwareDetailsJson = new JSONObject();
            clusterHardwareDetailsJson.put("Core_ebs_vol_size", clusterDetails.getCoreEbsVolSize());
            clusterHardwareDetailsJson.put("Core_instance_count", clusterDetails.getCoreInstanceCount());
            clusterHardwareDetailsJson.put("Core_instance_type", clusterDetails.getCoreInstanceType());
            clusterHardwareDetailsJson.put("Custom_ami_id", clusterDetails.getCustomAmiId());
            clusterHardwareDetailsJson.put("Master_ebs_vol_size", clusterDetails.getMasterEbsVolSize());
            clusterHardwareDetailsJson.put("Task_ebs_vol_size", clusterDetails.getTaskEbsVolSize());
            clusterHardwareDetailsJson.put("Task_instance_count", clusterDetails.getTaskInstanceCount());
            clusterHardwareDetailsJson.put("Task_instance_type", clusterDetails.getTaskInstanceType());
            clusterHardwareDetailsJson.put("Master_instance_type", clusterDetails.getMasterInstanceType());
            newCluster.setClusterDetails(clusterHardwareDetailsJson.toString());
        } catch (JSONException e) {
            logger.error("error during json parser");
            throw new QuickFabricJsonException("error during json parser", e);
        }
        newCluster.setCreatedBy(clusterDetails.getCreatedBy());
        newCluster.setLastUpdatedBy(clusterDetails.getLastUpdatedBy());
        newCluster.setAccount(clusterDetails.getAccount());
        newCluster.setType(clusterDetails.getType());
        newCluster.setDoTerminate(clusterDetails.getDoTerminate());
        newCluster.setHeadlessUsers(clusterDetails.getHeadlessUsers());
        newCluster.setSegment(clusterDetails.getSegment());
        newCluster.setDnsFlip(clusterDetails.getDnsFlip());
        newCluster.setIsProd(clusterDetails.getIsProd());
        newCluster.setOriginalClusterId(clusterDetails.getOriginalClusterId());
        newCluster.setJiraTicket(clusterDetails.getJiraTicket());
        newCluster.setSnowTicket(clusterDetails.getSnowTicket());
        newCluster.setAMIRotationDaysTogo(-30);
        newCluster.setAutoAmiRotation(clusterDetails.getAutoAmiRotation() == null ? false : clusterDetails.getAutoAmiRotation());
        //presumably both null or neither - default to 0-24, so any time.
        newCluster.setAutopilotWindowStart(clusterDetails.getAutopilotWindowStart() == null ?
                0 : clusterDetails.getAutopilotWindowStart());
        newCluster.setAutopilotWindowEnd(clusterDetails.getAutopilotWindowEnd() == null ?
                24 : clusterDetails.getAutopilotWindowEnd());
        newCluster.setAmiRotationSlaDays(clusterDetails.getAmiRotationSlaDays() == 0 ?
                30 : clusterDetails.getAmiRotationSlaDays());
        newCluster.setInstanceGroup(clusterDetails.getInstanceGroup());
        newCluster.setMax(clusterDetails.getMax());
        newCluster.setMin(clusterDetails.getMin());
        newCluster.setBootstrapActions(clusterDetails.getBootstrapActions());

    }

    private void mapCreateClusterReqToClusterVO(ClusterRequest clusterDetails, ClusterVO newCluster) {
        // Mapping for Custom steps from ClusterRequest to ClusterVO
        List<ClusterStep> steps = new ArrayList<ClusterStep>();
        BeanUtils.copyProperties(clusterDetails, newCluster, "steps");
        for (ClusterStepRequest req : clusterDetails.getSteps()) {
            ClusterStep s = new ClusterStep();
            s.setActionOnFailure(req.getActionOnFailure());
            HadoopJarStep h = new HadoopJarStep();
            h.setJar(req.getJar());
            h.setMainClass(req.getMainClass());
            String argsInput = req.getArgs();
            String[] argsFinal = argsInput.split(" ");
            h.setStepArgs(Arrays.asList(argsFinal));
            s.setHadoopJarStep(h);
            s.setName(req.getName());
            s.setStepCreatedBy(req.getStepCreatedBy());
            steps.add(s);
        }
        newCluster.getSteps().addAll(steps);
    }


    public ClusterVO validateEMRCluster(@RequestBody ClusterVO clusterRequest) {
        logger.info("EMRClusterManagementHelper API->validateEMRCluster starting for cluster"
                + clusterRequest.getClusterName());
        String accountId = clusterRequest.getAccount();
        String clusterId = clusterRequest.getClusterId();
        clusterRequest = awsServiceCallerHelper.invokeAWSValidateEMRClusterService(clusterId, accountId);

        // if status is 200 ,updating the latest cluster in DB.
        emrClusterMetricsHelper.updateClusterStatusByClusterId(clusterRequest);
        logger.info("EMRClusterManagementHelper API->validateEMRCluster completed for cluster"
                + clusterRequest.getClusterName());
        return clusterRequest;

    }


    public ClusterVO terminateEMRCluster(@RequestBody ClusterRequest clusterRequest, boolean nonHARotateFlag) {
        logger.info("EMRClusterManagementHelper API->terminateEMRCluster starting for cluster"
                + clusterRequest.getClusterName());

        ClusterVO clusterDetails = emrClusterMetadataHelper.getSingleClusterDetailsByClusterID(clusterRequest.getClusterId());
        if (clusterDetails == null ||
                AccessControl.isNotAccessible(clusterDetails.getSegment(), clusterDetails.getAccount(), Roles.TERMINATE_CLUSTER) ||
                AccessControl.isNotAccessible(clusterDetails.getSegment(), clusterDetails.getAccount(), Roles.AMI_ROTATION)) {
            throw new QuickFabricUnauthorizedException("user is not authorized to terminate cluster:" + clusterRequest.getClusterName());
        }

        ticketValidationHelper.validRequestTicket(clusterRequest);
        logger.info("Ticket is approved! Proceeding with terminate cluster.");
        ClusterVO responseVO = null;
        validationHelper.validateTerminateClusterRequest(clusterRequest);
        ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        String terminateRequestJson;

        try {
            terminateRequestJson = ow.writeValueAsString(clusterRequest);
        } catch (JsonProcessingException e) {
            logger.error("error during json object Deserialization with error {}", e.getMessage());
            throw new QuickFabricJsonException("error during json object Deserialization", e);
        }

        responseVO = awsServiceCallerHelper.invokeAWSTerminateClusterService(terminateRequestJson, clusterRequest.getAccount());
        // if the message is returned Cluster not present ,then set the status as
        // TERMINATED in DB.
        if (CLUSTER_NOT_PRESENT.equalsIgnoreCase(responseVO.getMessage())) {
            responseVO.setClusterStatus(ClusterStatus.TERMINATED);
        }
        mapOtherClusterDetailsFromResponse(responseVO, clusterRequest);
        //we are going to update the DB only if terminateEMRCluster method was called from Terminate API 
        if (!nonHARotateFlag) {
            logger.info("EMRClusterManagementHelper API->updateClusterStatusByClusterId starting");
            emrClusterMetricsHelper.updateClusterStatusByClusterId(responseVO);
            sendClusterTerminatedEmail(responseVO);
        }
        // fetch masterIp from metadata needed for dns deletion
        ClusterVO clusterMetadata = metadataDao.getClusterMetadataByClusterId(clusterRequest.getClusterId());
        String defaultDnsName = clusterMetadata.getDnsName();

        //Deleting the Default DNS Entry if dns flip flag is marked to False and it exists in metadata means
        //this cluster was still pointing to default dns
        if (!StringUtils.isBlank(defaultDnsName) && !clusterMetadata.getDnsFlipCompleted()) {

            String masterIp = clusterMetadata.getMasterIp();

            logger.info("Deleting Default DNS: " + clusterMetadata.getDnsName() + " after terminating cluster: "
                    + clusterRequest.getClusterName());

            //Catching QuickFabricRestHandlerException in order not to break the code
            try {
                awsServiceCallerHelper.invokeAWSDNSFlip(clusterRequest.getClusterName(), clusterRequest.getAccount(), defaultDnsName, masterIp, "delete");
            } catch (QuickFabricRestHandlerException e) {
                logger.error("EMRClusterManagementHelper API->invokeAWSDNSCreate -> Exception: ", e.getMessage(), e);
            }

        } else {
            logger.info("DNS Flip flag is set to: " + clusterMetadata.getDnsFlipCompleted() +
                    " .Hence, not deleting any default dns.");
        }

        logger.info("EMRClusterManagementHelper API->terminateEMRCluster completed for cluster"
                + clusterRequest.getClusterName());
        return responseVO;
    }


    public ClusterVO getLatestClusterStatus(String clusterId, String accountId) {
        logger.info("EMRClusterManagementHelper API->getLatestClusterStatus starting for cluster" + clusterId);
        ClusterVO responseVO = awsServiceCallerHelper.invokeAWSClusterStatusCheckService(clusterId, accountId);
        // if the message is returned Cluster not present ,then set the status as
        // TERMINATED in DB.
        if (CLUSTER_NOT_PRESENT.equalsIgnoreCase(responseVO.getMessage())) {
            responseVO.setClusterStatus(ClusterStatus.TERMINATED);
        }
        emrClusterMetricsHelper.updateClusterStatusByClusterId(responseVO);
        logger.info("EMRClusterManagementHelper API->getLatestClusterStatus completed for cluster" + clusterId);
        return responseVO;
    }


    public StepResponseVO addStepToSucceededClusters(ClusterRequest clusterStepRequest) {
        logger.info("EMRClusterManagementHelper API->addStepToSucceededClusters starting for cluster"
                + clusterStepRequest.getClusterName());

        ClusterVO clusterDetails = emrClusterMetadataHelper.getSingleClusterDetailsByClusterID(clusterStepRequest.getClusterId());
        if (clusterDetails == null || AccessControl.isNotAccessible(clusterDetails.getSegment(), clusterDetails.getAccount(), Roles.ADD_STEP)) {
            throw new QuickFabricUnauthorizedException("user is not authorized to add Steps in the cluster" + clusterStepRequest.getClusterName());
        }
        //change Request (jira/snow ticket ) validation
        ticketValidationHelper.validRequestTicket(clusterStepRequest);
        logger.info("Ticket is approved! Proceeding with add custom steps.");

        //client input validation
        validationHelper.validateAddStepsReq(clusterStepRequest);
        logger.info("client input is validated! Proceeding with add custom steps.");

        ClusterVO newClusterSteps = new ClusterVO();
        mapCreateClusterReqToClusterVO(clusterStepRequest, newClusterSteps);

        StepResponseVO stepResponseVO = processAddSteps(newClusterSteps);

        sendCustomStepsAddedEmail(stepResponseVO);
        logger.info("EMRClusterManagementHelper API->addStepToSucceededClusters completed for cluster"
                + clusterStepRequest.getClusterName());
        return stepResponseVO;
    }


    private StepResponseVO processAddSteps(ClusterVO newClusterSteps) {
        String addClusterStepRequestJson;
        try {
            ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
            addClusterStepRequestJson = ow.writeValueAsString(newClusterSteps);
        } catch (JsonProcessingException e) {
            logger.error("error during json object Deserialization with error {}", e.getMessage());
            throw new QuickFabricJsonException("error during json object Deserialization", e);
        }
        System.out.println(addClusterStepRequestJson);
        StepResponseVO stepResponse = awsServiceCallerHelper.invokeAWSClusterAddStepService(addClusterStepRequestJson, newClusterSteps.getAccount());

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);
        try {
            //Saving custom steps into RDS when they exist
            emrClusterMetricsHelper.saveClusterStepsRequest(newClusterSteps);
            //updating stepID and stepStatus in RDS
            emrClusterMetricsHelper.updateStepIdsInDB(stepResponse, stepResponse.getSteps());
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            throw e;
        }
        return stepResponse;
    }


    StepResponseVO readJsonResponeForStepValidateToVO(ResponseEntity<String> response)
            throws IOException, JsonParseException, JsonMappingException {
        StepResponseVO stepResponse = null;
        if (response.getStatusCode() != null && response.getStatusCodeValue() == 201) {
            stepResponse = objectMapper.readValue(response.getBody(), StepResponseVO.class);
        }
        return stepResponse;
    }


    String createJsonToValidateSteps(String clusterId, String clusterName, String[] stepIds) throws JSONException {
        String stepValidateJson;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("clusterId", clusterId);
        jsonObject.put("clusterName", clusterName);
        JSONArray jarray = new JSONArray(stepIds);
        jsonObject.put("stepIds", jarray);
        stepValidateJson = jsonObject.toString();
        return stepValidateJson;
    }

    public ClusterVO rotateAMI(@RequestBody ClusterRequest clusterRequest) {
        logger.info("EMRClusterManagementHelper API->rotateAMI" + clusterRequest.getClusterName());

        ClusterVO clusterDetails = emrClusterMetadataHelper.getSingleClusterDetailsByClusterID(clusterRequest.getClusterId());
        if (clusterDetails == null || AccessControl.isNotAccessible(clusterDetails.getSegment(), clusterDetails.getAccount(), Roles.AMI_ROTATION)) {
            throw new QuickFabricUnauthorizedException("user is not authorized for AMIRotation of cluster" + clusterRequest.getClusterName());
        }

        ticketValidationHelper.validRequestTicket(clusterRequest);
        logger.info("Ticket is approved! Proceeding with rotate AMI.");

        //validator to check if a cluster has been used for rotation already
        // and new cluster is not in terminated or failed state
        // We can only have 1 original and 1 new cluster running
        ClusterVO validateAMIReq = metadataDao.getClusterMetadataByOriginalClusterId(clusterRequest.getClusterId());
        if (validateAMIReq != null) {
            String invalidReq = "There is already a cluster in running state: " + validateAMIReq.getClusterName() +
                    " rotated using this cluster ID:" + clusterRequest.getClusterId() +
                    " .You cannot rotate the cluster again using the same one";
            throw new QuickFabricClientException(invalidReq);
        }

        String clusterId = clusterRequest.getClusterId();
        String accountId = clusterRequest.getAccount();
        boolean isProd = clusterRequest.getIsProd();
        Boolean autoAMIRotation = clusterRequest.getAutoAmiRotation() == null ? false : clusterRequest.getAutoAmiRotation();

        // Retrieve cluster meta data
        ClusterVO originalClusterVo = emrClusterMetadataHelper.getAllEMRClusterDetailsByClusterID(clusterId);

        originalClusterVo.setBootstrapActions(emrClusterStepsDao.getBootstrapActionsByClusterId(clusterId));

        originalClusterVo.setSubType("nonkerb");

        // validate EMR cluster for idle/waiting status before proceed to AMIRotation
        ClusterVO clusterResponse = awsServiceCallerHelper.invokeAWSClusterStatusCheckService(clusterId, accountId);
        if (!(clusterResponse.getClusterStatus().equals(ClusterStatus.WAITING)
                || clusterResponse.getClusterStatus().equals(ClusterStatus.TERMINATION_INITIATED)
                || clusterResponse.getClusterStatus().equals(ClusterStatus.TERMINATING)
                || clusterResponse.getClusterStatus().equals(ClusterStatus.TERMINATED))) {
            String errorMsg = "EMRClusterManagementHelper API->rotateAMI -> Can only rotate AMIs for IDLE/Terminated clusters.";
            throw new QuickFabricClientException(errorMsg);
        }

        logger.info("EMRClusterManagementHelper API->rotateAMI -> Cluster has been validated.");
        //Non-HA Use-Case (means we will terminate the cluster first then recreate the cluster again with same configurations but with new amiID)
        if (!isProd) {
            ClusterVO terminationResponseClusterVO = new ClusterVO();
            if (!(clusterResponse.getClusterStatus().equals(ClusterStatus.TERMINATION_INITIATED)
                    || clusterResponse.getClusterStatus().equals(ClusterStatus.TERMINATING)
                    || clusterResponse.getClusterStatus().equals(ClusterStatus.TERMINATED)
            )) {
                logger.info("EMRClusterManagementHelper API->rotateAMI Non-HA -> Termination initiated.");
                terminationResponseClusterVO = terminateEMRCluster(clusterRequest, true);
            }

            logger.info("EMRClusterManagementHelper API->rotateAMI Non-HA -> Proceeding to Create Cluster");
            //Mapping Original cluster VO into new ClusterRequest for creation
            ClusterRequest createNewCluster = new ClusterRequest();
            BeanUtils.copyProperties(originalClusterVo, createNewCluster);
            emrClusterMetadataHelper.mapClusterDetailsToClusterRequestFields(originalClusterVo, createNewCluster);

            //Saving flags for the new cluster to be used for auto AMI Rotation and workflow
            createNewCluster.setOriginalClusterId(clusterId);
            createNewCluster.setCreatedBy(clusterRequest.getCreatedBy());
            createNewCluster.setAutoAmiRotation(autoAMIRotation);
            if (StringUtils.isNotBlank(clusterRequest.getCustomAmiId())) {
                createNewCluster.setCustomAmiId(clusterRequest.getCustomAmiId());
            }
            //use new config if specified, otherwise use the old one.
            createNewCluster.setAutopilotWindowStart(
                    clusterRequest.getAutopilotWindowStart() == null
                            ? originalClusterVo.getAutopilotWindowStart()
                            : clusterRequest.getAutopilotWindowStart());
            createNewCluster.setAutopilotWindowEnd(clusterRequest.getAutopilotWindowEnd() == null
                    ? originalClusterVo.getAutopilotWindowEnd()
                    : clusterRequest.getAutopilotWindowEnd());

            createNewCluster.setAmiRotationSlaDays(
                    clusterRequest.getAmiRotationSlaDays() == 0
                            ? originalClusterVo.getAmiRotationSlaDays()
                            : clusterRequest.getAmiRotationSlaDays());

            createNewCluster.setIsProd(isProd);
            createNewCluster.setJiraTicket(clusterRequest.getJiraTicket());
            ClusterVO createResponseClusterVO = createEMRCluster(createNewCluster, true);
            mapOtherClusterDetailsFromResponse(createResponseClusterVO, createNewCluster); //mapping createResponseClusterVO  from createNewCluster

            //calling saveEMRClusterMetadata inside @transational to save cluster data into DB 
            if (terminationResponseClusterVO == null) {
                saveEMRClusterMetadataNonHA(clusterResponse, createResponseClusterVO);
            } else {
                saveEMRClusterMetadataNonHA(terminationResponseClusterVO, createResponseClusterVO);
            }

            logger.info("EMRClusterManagementHelper API->rotateAMI Non-HA -> Created cluster: " + createResponseClusterVO.getClusterName());
            sendAMIRotatedEmail(clusterId, createResponseClusterVO.getClusterId());
        }
        /* HA Use-Case
         * 	means we will create the cluster first with same configurations as original cluster but with new custom amiID
         *  sent via user or auto pick the latest by AWS backend service
         *  then update original cluster for termination after 24 hours
         *
         */
        else if (isProd) {
            ClusterRequest createNewCluster = new ClusterRequest();
            BeanUtils.copyProperties(originalClusterVo, createNewCluster);
            emrClusterMetadataHelper.mapClusterDetailsToClusterRequestFields(originalClusterVo, createNewCluster);
            // validation and iterate until a suitable number is found
            String segment = originalClusterVo.getSegment();
            String type = originalClusterVo.getType().toString();
            StringBuilder appendedName = new StringBuilder();
            logger.info("EMRClusterManagementHelper API->rotateAMI HA -> Iterating to find name for new cluster");
            // Iterating to find new name for new cluster
            for (int i = 1; i < 10; i++) {
                String newName = String.valueOf(i);
                appendedName.append(type);
                appendedName.append("-");
                appendedName.append(segment);
                appendedName.append("-");
                appendedName.append(newName);
                String newClusterName = appendedName.toString();

                //Checking if a cluster with this name exists or not
                if (isClusterPresent(newClusterName, accountId)) {
                    //Clearing up the stringbuilder to set for another run
                    logger.info("EMRClusterManagementHelper API->rotateAMI HA -> Name not found, going back into the loop");
                    appendedName.setLength(0);
                } else {
                    logger.info("EMRClusterManagementHelper API->rotateAMI HA -> Name found, setting it to:" + newName);
                    createNewCluster.setClusterName(newClusterName);
                    break;
                }

            }//end of for loop

            if (StringUtils.isBlank(appendedName.toString())) {
                throw new QuickFabricServerException("new cluster name was not found in 10 tries");
            }

            //Saving flags for the new cluster to be used for workflow and auto AMI Rotation
            createNewCluster.setIsProd(isProd);
            createNewCluster.setOriginalClusterId(clusterId);
            createNewCluster.setCreatedBy(clusterRequest.getCreatedBy());
            createNewCluster.setAutoAmiRotation(autoAMIRotation);
            //use new config if specified, otherwise use the old one.
            createNewCluster.setAutopilotWindowStart(
                    clusterRequest.getAutopilotWindowStart() == null
                            ? originalClusterVo.getAutopilotWindowStart()
                            : clusterRequest.getAutopilotWindowStart());
            createNewCluster.setAutopilotWindowEnd(clusterRequest.getAutopilotWindowEnd() == null
                    ? originalClusterVo.getAutopilotWindowEnd()
                    : clusterRequest.getAutopilotWindowEnd());

            createNewCluster.setAmiRotationSlaDays(
                    clusterRequest.getAmiRotationSlaDays() == 0
                            ? originalClusterVo.getAmiRotationSlaDays()
                            : clusterRequest.getAmiRotationSlaDays());

            createNewCluster.setJiraTicket(clusterRequest.getJiraTicket());

            if (StringUtils.isNotBlank(clusterRequest.getCustomAmiId())) {
                createNewCluster.setCustomAmiId(clusterRequest.getCustomAmiId());
            }
            logger.info("EMRClusterManagementHelper API->rotateAMI HA -> Proceeding to Create Cluster");
            ClusterVO createResponseClusterVO = createEMRCluster(createNewCluster, false);
            //UPDATING New Cluster ID in Old One and marking old cluster for auto-termination
            saveMetadataforAmiRotation(clusterId, createResponseClusterVO.getClusterId());

            logger.info("EMRClusterManagementHelper API->rotateAMI HA -> Created cluster: " + createNewCluster.getClusterName());

            sendAMIRotatedEmail(clusterId, createResponseClusterVO.getClusterId());

        } else {
            String invalidAMIRotationUseCase = "Production Flag is set to: " + isProd + " . AMI Rotation is only supported for Non-HA (not production) and HA (is production) use-cases";
            logger.error(invalidAMIRotationUseCase);
            throw new QuickFabricClientException(invalidAMIRotationUseCase);
        }

        return clusterResponse;

    }

    //updating metadata record by maintaining transactionality
    private void saveMetadataforAmiRotation(String OriginalclusterId, String newClusterId) {
        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);
        try {
            metadataDao.updateNewClusterByOriginalClusterId(OriginalclusterId, newClusterId);
            metadataDao.markClusterforTermination(OriginalclusterId);
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            throw e;
        }

    }

    public ClusterVO dnsFlip(ClusterRequest clusterRequest) {
        logger.info("EMRClusterManagementHelper -> dsnFlip. clusterDetails" + clusterRequest);

        ClusterVO clusterDetails = emrClusterMetadataHelper.getSingleClusterDetailsByClusterID(clusterRequest.getClusterId());
        if (clusterDetails == null || AccessControl.isNotAccessible(clusterDetails.getSegment(), clusterDetails.getAccount(), Roles.FLIP_DNS)) {
            throw new QuickFabricUnauthorizedException("user is not authorized for dnsFlip" + clusterRequest.getClusterName());
        }

        ticketValidationHelper.validRequestTicket(clusterRequest);
        logger.info("Ticket is approved! Proceeding with DNS Flip.");

        //Validating DNS Flip Request
        validationHelper.validateDNSFlipReq(clusterRequest);

        String dnsName = clusterRequest.getDnsName();
        String clusterId = clusterRequest.getClusterId();
        ClusterVO clusterMetadata = metadataDao.getClusterMetadataByClusterId(clusterId);
        String account = clusterMetadata.getAccount();
        String clusterName = clusterMetadata.getClusterName();
        String masterIp = clusterMetadata.getMasterIp();
        ClusterVO dnsResponseClusterVO = awsServiceCallerHelper.invokeAWSDNSFlip(clusterName, account, dnsName, masterIp, "update");
        // Update new emr with new dns entry and mark old cluster for termination
        String returnedDnsName = dnsResponseClusterVO.getDnsName();

        //Removing the trailing "." in the dns name
        if (returnedDnsName.endsWith(".")) {
            dnsName = returnedDnsName.substring(0, returnedDnsName.length() - 1);
        } else {
            dnsName = returnedDnsName;
        }
        emrClusterMetricsHelper.updateClusterDNSinDB(clusterName, dnsName);
        //Fetching the original cluster ID based on new cluster
        if (StringUtils.isBlank(clusterMetadata.getOriginalClusterId())) {
            logger.warn("No original Cluster to be marked for termination for cluster ID: " + clusterRequest.getClusterId());
        } else {
            //Marking the original cluster for termination as it was found
            emrClusterMetricsHelper.markClusterforTermination(clusterMetadata.getOriginalClusterId());
        }

        //Delete the old DNS name if it's not empty and is the default one
        String oldDnsName = clusterMetadata.getDnsName();

        if (!StringUtils.isBlank(oldDnsName) && !clusterMetadata.getDnsFlipCompleted()) {
            //Catching QuickFabricRestHandlerException in order to log the error message 
            //rather than throwing an exception to client
            logger.info("Deleting Old DNS: " + oldDnsName + " for cluster: " + clusterName);
            try {
                awsServiceCallerHelper.invokeAWSDNSFlip(clusterName, account, clusterMetadata.getDnsName(), masterIp, "delete");
            } catch (QuickFabricRestHandlerException e) {
                logger.error("EMRClusterManagementHelper API->invokeAWSDNSDelete -> Exception: ", e.getMessage(), e);
            }
        }


        sendDnsFlipEmail(clusterMetadata);
        return dnsResponseClusterVO;
    }

    private void sendClusterCreatedEmail(ClusterVO request) {
        boolean sendEmailEnabled = configHelper.getConfigValue("create_cluster_notifications");
        if (!sendEmailEnabled) {
            return;
        }

        QuickFabricAuthenticationToken auth =
                (QuickFabricAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        ClusterVO newCluster = emrClusterMetadataHelper
                .getAllEMRClusterDetailsByClusterID(request.getClusterId());

        ReportBuilder builder = new ReportBuilder();

        String emailBody = builder.openHtmlTag()
                .openBodyTag()
                .h3("New Cluster Details:")
                .appendClusterDetailsTable(newCluster)
                .closeBodyTag()
                .closeHtmlTag()
                .build();


        String subject = auth.getEmail() + " created cluster " + newCluster.getClusterName() + " via QuickFabric";
        String additionalRecipients = configHelper.getConfigValue("notification_recipients");
        
        try {
            emailUtil.sendEmail(emailBody, subject, auth.getEmail(), additionalRecipients);
        } catch (Exception e) {
            logger.error("Error sending email to {} for creating cluster. Error: {}", auth.getEmail(), e.getMessage());
            return;
        }
    }

    private void sendClusterTerminatedEmail(ClusterVO cluster) {
        boolean sendEmailEnabled = configHelper.getConfigValue("terminate_cluster_notifications");
        if (!sendEmailEnabled) {
            return;
        }

        QuickFabricAuthenticationToken auth =
                (QuickFabricAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        logger.info("Sending terminate cluster email notification to {}", auth.getEmail());
        ClusterVO terminatedCluster;
        terminatedCluster = emrClusterMetadataHelper
                .getSingleClusterDetailsByClusterID(cluster.getClusterId());
        ReportBuilder builder = new ReportBuilder();
        String emailBody = builder.openHtmlTag()
                .openBodyTag()
                .h3("Terminated Cluster Details:")
                .appendClusterDetailsTable(terminatedCluster)
                .closeBodyTag()
                .closeHtmlTag()
                .build();
        
        String subject = auth.getEmail() + " terminated cluster " + cluster.getClusterName() + " via QuickFabric";
        String additionalRecipients = configHelper.getConfigValue("notification_recipients");
        
        try {
            emailUtil.sendEmail(emailBody, subject, auth.getEmail(), additionalRecipients);
        } catch (Exception e) {
            logger.error("Error sending email to {} for terminating cluster. Error: {}", auth.getEmail(), e.getMessage());
            return;
        }
    }

    private void sendAMIRotatedEmail(String OriginalClusterId, String newClusterId) {
        try {
            boolean sendEmailEnabled = configHelper.getConfigValue("rotate_ami_notifications");
            if (!sendEmailEnabled) {
                return;
            }

            QuickFabricAuthenticationToken auth =
                    (QuickFabricAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

            ClusterVO newCluster = metadataDao.getClusterMetadataByClusterId(newClusterId);
            ClusterVO oldCluster = metadataDao.getClusterMetadataByClusterId(OriginalClusterId);
            ReportBuilder builder = new ReportBuilder();
            String emailBody = builder.openHtmlTag()
                    .openBodyTag()
                    .h3("Old Cluster Details:")
                    .appendClusterDetailsTable(oldCluster)
                    .h3("New Cluster Details:")
                    .appendClusterDetailsTable(newCluster)
                    .closeBodyTag()
                    .closeHtmlTag()
                    .build();

            String subject = auth.getEmail() + " rotated cluster " + newCluster.getClusterName() + " via QuickFabric";
            String additionalRecipients = configHelper.getConfigValue("notification_recipients");
            
            emailUtil.sendEmail(emailBody, subject, auth.getEmail(), additionalRecipients);
        } catch (Exception e) {
            logger.error("error during email notification", e.getMessage());
        }

    }

    private void sendDnsFlipEmail(ClusterVO clusterVO) {
        boolean sendEmailEnabled = configHelper.getConfigValue("dns_flip_notifications");
        if (!sendEmailEnabled) {
            return;
        }

        QuickFabricAuthenticationToken auth =
                (QuickFabricAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        ReportBuilder builder = new ReportBuilder();
        builder.openHtmlTag()
                .openBodyTag()
                .h3("Details of cluster flipped to production:")
                .appendClusterDetailsTable(clusterVO);
        ClusterVO toBeTerminatedCluster = null;
        toBeTerminatedCluster = this.emrClusterMetadataHelper.getAllEMRClusterDetailsByClusterID(clusterVO.getOriginalClusterId());
        if (toBeTerminatedCluster == null) {
            String msg = "No cluster marked for termination with this DNS flip.";
            logger.info(msg);
            builder.h3(msg);
        } else {
            builder.h3("Details of cluster marked for termination:")
                    .appendClusterDetailsTable(toBeTerminatedCluster);
        }

        builder.closeBodyTag().closeHtmlTag();

        String emailBody = builder.build();

        String subject = auth.getEmail() + " flipped cluster " +
                clusterVO.getClusterName() + " to production via QuickFabric";
        
        String additionalRecipients = configHelper.getConfigValue("notification_recipients");
        
        try {
            emailUtil.sendEmail(emailBody, subject, auth.getEmail(), additionalRecipients);
        } catch (Exception e) {
            logger.error("Error sending email to {} for DNS Flip. Error: {}", auth.getEmail(), e.getMessage());
            return;
        }
    }

    private void sendCustomStepsAddedEmail(StepResponseVO stepRequest) {
        boolean sendEmailEnabled = configHelper.getConfigValue("add_step_notifications");
        if (!sendEmailEnabled) {
            return;
        }

        QuickFabricAuthenticationToken auth =
                (QuickFabricAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        List<String> stepIds =
                stepRequest.getSteps().stream().map(s -> s.getStepId()).collect(Collectors.toList());
        ClusterVO cluster = emrClusterMetadataHelper.getAllEMRClusterDetailsByClusterID(stepRequest.getClusterId());
        List<ClusterStep> steps = this.emrClusterMetadataHelper.getStepsByStepIds(stepIds);
        ReportBuilder builder = new ReportBuilder();
        String emailBody = builder.openHtmlTag()
                .openBodyTag()
                .h3("Cluster Details:")
                .appendClusterDetailsTable(cluster)
                .h3("Steps Added:")
                .appendStepsDetailsTable(steps)
                .closeBodyTag()
                .closeHtmlTag()
                .build();
        String subject = auth.getEmail() + " added custom steps to cluster " +
                cluster.getClusterName() + " via QuickFabric";
        String additionalRecipients = configHelper.getConfigValue("notification_recipients");
        
        try {
            emailUtil.sendEmail(emailBody, subject, auth.getEmail(), additionalRecipients);
        } catch (Exception e) {
            logger.error("Error sending email to {} for adding custom steps. Error: {}", auth.getEmail(), e.getMessage());
            return;
        }
    }
}
