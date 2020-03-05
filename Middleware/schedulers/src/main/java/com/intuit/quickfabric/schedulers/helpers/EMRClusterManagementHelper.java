package com.intuit.quickfabric.schedulers.helpers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.intuit.quickfabric.commons.constants.ApiUrls;
import com.intuit.quickfabric.commons.exceptions.QuickFabricClientException;
import com.intuit.quickfabric.commons.exceptions.QuickFabricJsonException;
import com.intuit.quickfabric.commons.exceptions.QuickFabricRestHandlerException;
import com.intuit.quickfabric.commons.exceptions.QuickFabricServerException;
import com.intuit.quickfabric.commons.helper.ConfigHelper;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.schedulers.dao.EMRClusterMetadataDao;
import com.intuit.quickfabric.schedulers.dao.EMRClusterStepsDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class EMRClusterManagementHelper {
    private static final String CLUSTER_NOT_PRESENT = "Cluster not present";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EMRClusterMetricsHelper clusterMetricsHelper;

    @Autowired
    EMRAWSServiceCallerHelper emrAwsServiceCaller;

    @Autowired
    ValidationHelper validationHelper;

    @Autowired
    EMRClusterMetadataHelper clusterMetadataHelper;

    @Autowired
    EMRClusterMetadataDao metadataDao;

    @Autowired
    EMRClusterStepsDao clusterStepsDao;

    @Autowired
    ConfigHelper configHelper;

    @Autowired
    HealthChecksHelper healthChecksHelper;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private final Logger logger = LogManager.getLogger(EMRClusterManagementHelper.class);

    private boolean isClusterNotPresent(String clusterName, String accountId) {
        logger.info("EMRClusterManagementHelper API->GETClusterIfPresentBeforeCreatingNewCluster starting for cluster"
                + clusterName);

        // Calling status check api called before create cluster
        String clusterPreCheckUrl = configHelper.getAccountSpecificUrl(accountId, ApiUrls.PRECHECK_CLUSTER_URL_SUFFIX);
        ClusterVO clusterDetails = emrAwsServiceCaller.invokeAWSClusterPreCheckService(clusterPreCheckUrl, clusterName, accountId);

        if (StringUtils.isNotBlank(clusterDetails.getClusterName())
                && clusterDetails.getClusterStatus().equals(ClusterStatus.ClusterNotPresent)) {
            // returns true if the cluster not present ,to allow creation of new cluster.
            return true;
        }

        logger.info("EMRClusterManagementHelper API->GETClusterIfPresentBeforeCreatingNewCluster completed for cluster"
                + clusterName);

        return false;
    }

    public ClusterVO createEMRCluster(ClusterRequest originalClusterDetails) {
        logger.info("EMRClusterManagementHelper Scheduler -> createEMRCluster starting for cluster" + originalClusterDetails.getClusterName());

        validationHelper.validateCreateClusterRequest(originalClusterDetails);
        boolean clusterNotPresent = isClusterNotPresent(originalClusterDetails.getClusterName(), originalClusterDetails.getAccount());
        if (!clusterNotPresent) {
            // if cluster is already present, we throw an exception
            String clusterAlreadyExists = "Cluster with name " + originalClusterDetails.getClusterName()
                    + " already exists.";
            throw new QuickFabricServerException(clusterAlreadyExists);
        }

        ClusterVO clusterDetailVO = new ClusterVO();
        mapCreateClusterReqToClusterVO(originalClusterDetails, clusterDetailVO);

        ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        String json;
        try {
            json = ow.writeValueAsString(clusterDetailVO);
            logger.info("JSON request to AWS: " + json);
        } catch (JsonProcessingException e) {
            throw new QuickFabricJsonException("exception happened while creating json request for create cluster", e);
        }

        String accountSpecificUrl = configHelper.getAccountSpecificUrl(originalClusterDetails.getAccount(), ApiUrls.CREATE_CLUSTER_URL_SUFFIX);
        ClusterVO newCluster = emrAwsServiceCaller.invokeAWSCreateClusterService(accountSpecificUrl, originalClusterDetails.getAccount(), json);

        if (StringUtils.isBlank(newCluster.getClusterName())) {
            throw new QuickFabricServerException("New Cluster name is empty from create cluster API response");
        }

        //mapping ClusterRequest to newClusterResponseVO
        mapOtherClusterDetailsFromResponse(newCluster, originalClusterDetails);

        //calling saveEMRClusterMetadata inside @transational to save cluster data into DB
        saveEMRClusterMetadata(clusterDetailVO, newCluster);

        logger.info("EMRClusterManagementHelper Scheduler -> createEMRCluster -> saveNewClusterDetailsToRDS starting");

        return newCluster;
    }

    private void saveEMRClusterMetadata(ClusterVO newClusterSteps, ClusterVO newClusterResponse) {
        newClusterSteps.setClusterId(newClusterResponse.getClusterId());

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);
        try {
            //Saving bootstrap actions into RDS when they exist
            if (newClusterSteps.getBootstrapActions() != null && newClusterSteps.getBootstrapActions().size() > 0) {
                logger.info("getAndSaveClusterBootstrapActionsRequest starting");
                clusterMetricsHelper.getAndSaveClusterBootstrapActionsRequest(newClusterSteps);
            }

            logger.info("EMRClusterManagementHelper API->saveNewClusterDetailsToRDS starting");
            clusterMetricsHelper.saveNewClusterDetailsToRDS(newClusterResponse);
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            throw e;
        }
    }

    private void mapOtherClusterDetailsFromResponse(ClusterVO newCluster, ClusterRequest clusterDetails) {
        try {
            //Converting Cluster hardware details to be stored as a JSON in Database
            JSONObject clusterHardwareDetailJson = new JSONObject();
            clusterHardwareDetailJson.put("Core_ebs_vol_size", clusterDetails.getCoreEbsVolSize());
            clusterHardwareDetailJson.put("Core_instance_count", clusterDetails.getCoreInstanceCount());
            clusterHardwareDetailJson.put("Core_instance_type", clusterDetails.getCoreInstanceType());
            clusterHardwareDetailJson.put("Custom_ami_id", clusterDetails.getCustomAmiId());
            clusterHardwareDetailJson.put("Master_ebs_vol_size", clusterDetails.getMasterEbsVolSize());
            clusterHardwareDetailJson.put("Task_ebs_vol_size", clusterDetails.getTaskEbsVolSize());
            clusterHardwareDetailJson.put("Task_instance_count", clusterDetails.getTaskInstanceCount());
            clusterHardwareDetailJson.put("Task_instance_type", clusterDetails.getTaskInstanceType());
            clusterHardwareDetailJson.put("Master_instance_type", clusterDetails.getMasterInstanceType());

            newCluster.setClusterDetails(clusterHardwareDetailJson.toString());
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

        } catch (Exception e) {
            throw new QuickFabricJsonException("something went wrong during Cluster mapping", e);
        }
    }

    private void mapCreateClusterReqToClusterVO(ClusterRequest clusterDetails, ClusterVO newCluster) {
        // Mapping for Custom steps from ClusterRequest to ClusterVO
        List<ClusterStep> steps = new ArrayList<>();
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


    private void updateClusterAndBootstrapStatus(ClusterVO clusterReqNew, ClusterVO originalCluster) {

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);
        try {
            //updating new Cluster Status into metadata DB
            clusterMetricsHelper.updateClusterStatusInDB(clusterReqNew);

            //Adding new cluster id to original cluster metadata row, to be used in case of AMI rotation
            if (StringUtils.isBlank(originalCluster.getOriginalClusterId())) {
                logger.info("There is no original cluster id associated with this cluster ID: {}" + clusterReqNew.getClusterId() +
                        " .Hence, not updating the new cluster ID into metadata Table");
            } else {
                logger.info("Updating New Cluster ID into Original Cluster Metadata Row");
                metadataDao.updateNewClusterByOriginalClusterId(originalCluster.getOriginalClusterId(), clusterReqNew.getClusterId());
            }


            //Updating Bootstrap status based on Cluster Status
            String bootstrapStatus = "NEW";
            if (ClusterStatus.BOOTSTRAPPING.equals(clusterReqNew.getClusterStatus())) {
                bootstrapStatus = StepStatus.PENDING.toString();
            } else if (ClusterStatus.RUNNING.equals(clusterReqNew.getClusterStatus()) ||
                    ClusterStatus.WAITING.equals(clusterReqNew.getClusterStatus())) {
                bootstrapStatus = StepStatus.COMPLETED.toString();
            } else if (ClusterStatus.FAILED.equals(clusterReqNew.getClusterStatus()) ||
                    ClusterStatus.TERMINATED_WITH_ERRORS.equals(clusterReqNew.getClusterStatus())) {
                bootstrapStatus = StepStatus.FAILED.toString();
            }

            logger.info("EMRClusterManagementHelper API->updateBootstrapActionStatus to: " + bootstrapStatus +
                    " for clusterId: " + clusterReqNew.getClusterId());
            clusterMetricsHelper.updateBootstrapActionStatus(clusterReqNew, bootstrapStatus);
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            logger.error("something went wrong during cluster metaData update for clusterName {} and ClusterId {}", clusterReqNew.getClusterName(), clusterReqNew.getClusterId(), e);

        }
    }

    public void validateEMRCluster(ClusterVO clusterReq) {
        logger.info("EMRClusterManagementHelper API->validateEMRCluster starting for cluster"
                + clusterReq.getClusterName());

        try {
            String accountId = clusterReq.getAccount();
            String clusterId = clusterReq.getClusterId();
            String accountSpecificUrl = configHelper.getAccountSpecificUrl(accountId, ApiUrls.CLUSTER_VALIDATE_URL_SUFFIX);
            ClusterVO clusterReqNew = emrAwsServiceCaller.invokeAWSValidateEMRClusterService(accountSpecificUrl, clusterId, accountId);
            objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
            objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

            if (StringUtils.isNotBlank(clusterReqNew.getClusterName())) {

                //DB call to update cluster Status and bootStrap Status
                updateClusterAndBootstrapStatus(clusterReqNew, clusterReq);

                // if cluster status is Running or waiting, attaching auto-scaling and 
                // creating default dns for it as cluster has been created successfully
                if (ClusterStatus.RUNNING.equals(clusterReqNew.getClusterStatus()) ||
                        ClusterStatus.WAITING.equals(clusterReqNew.getClusterStatus())) {

                    //Attaching autoscaling policy if it was attached with create cluster
                    if (!StringUtils.isBlank(clusterReq.getInstanceGroup())) {
                        logger.info("EMRClusterManagementHelper API->invokeAWSAutoScalingService starting");
                        emrAwsServiceCaller.invokeAWSAutoScalingService(clusterReq);
                    }

                    //Creating Default DNS for new non-kerb Cluster
                    if (!StringUtils.isBlank(clusterReq.getDnsName())) {
                        String dnsName = clusterReq.getDnsName();

                        //fetch masterIp from metadata needed for dns creation
                        ClusterVO clusterMetadata = metadataDao.getClusterMetadata(clusterReqNew.getClusterId());
                        String masterIp = clusterMetadata.getMasterIp();

                        logger.info("EMRClusterManagementHelper Scheduler->invokeAWSDNSCreate starting for creating Default DNS for cluster: "
                                + clusterReqNew.getClusterName());

                        //Catching QuickFabricRestHandlerException from dns flip creation
                        //Setting dns name as empty in metadata if creation fails
                        try {
                            emrAwsServiceCaller.invokeAWSDNSFlip(clusterReqNew.getClusterName(), accountId, dnsName, masterIp, "create");
                        } catch (QuickFabricRestHandlerException e) {
                            logger.error("EMRClusterManagementHelper Scheduler->invokeAWSDNSCreate -> Exception: ", e.getMessage(), e);
                            //Setting Dns Name as Empty in Metadata as creation of Default DNS Failed
                            clusterMetricsHelper.updateClusterDNSinDB(clusterReqNew.getClusterName(), "");
                        }
                    } else {
                        logger.warn("No Default DNS found in metadata for creation. Skipping DNS Name creation.");
                    }

                    //submit health checks
                    boolean testSuitesEnabled = false;
                    try {
                        testSuitesEnabled = configHelper.getConfigValue("testsuites_enabled", accountId);
                    } catch (Exception e) {
                        logger.error("error occurred while getting config testsuites_enabled", e);
                    }

                    if (testSuitesEnabled) {
                        healthChecksHelper.submitHealthChecks(Arrays.asList(clusterId));
                    }
                }
            }

        } catch (Exception e) {
            logger.error("something went wrong in ValidateCluster for ClusterName {} and ClusterId {}"
                    , clusterReq.getClusterName(), clusterReq.getClusterId());
        }
    }

    public void terminateOOSLACluster(ClusterRequest c, int hours) {

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date parsedDate = sdf.parse(c.getCreationTimestamp());
            Timestamp clusterCreateTimestamp = new Timestamp(parsedDate.getTime());
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

            long millisecondsDiff = currentTimestamp.getTime() - clusterCreateTimestamp.getTime();
            int hoursDiff = (int) (millisecondsDiff / (1000 * 60 * 60));

            if (hoursDiff >= hours) {
                //terminating the cluster
                terminateEMRCluster(c);
            } else {
                logger.info("EMRClusterLifeCycleSchedulers->terminateCompletedClusters Cluster with name: " + c.getClusterName()
                        + " ID: " + c.getClusterId() + " will not be terminated. "
                        + "As it did not meet requirements for termination. ElapsedTime: " + hoursDiff);
            }
        } catch (ParseException e) {
            logger.error("EMRClusterLifeCycleSchedulers->terminateCompletedClusters parsing exception occured " + e.getMessage());
        }
    }

    public ClusterVO terminateEMRCluster(ClusterRequest clusterDetails) {
        logger.info("EMRClusterManagementHelper API->terminateEMRCluster starting for cluster"
                + clusterDetails.getClusterName());

        validationHelper.validateTerminateClusterRequest(clusterDetails);
        ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(clusterDetails);
        } catch (JsonProcessingException e) {
            throw new QuickFabricJsonException("json exception during termninate cluster", e);
        }
        String accountSpecificUrl = configHelper.getAccountSpecificUrl(clusterDetails.getAccount(), ApiUrls.TERMINATE_CLUSTER_URL_SUFFIX);
        ClusterVO clusterVO = emrAwsServiceCaller.invokeAWSTerminateClusterService(accountSpecificUrl, json, clusterDetails.getAccount());

        // if the message is returned Cluster not present ,then set the status as
        // TERMINATED in DB.
        if (CLUSTER_NOT_PRESENT.equalsIgnoreCase(clusterVO.getMessage())) {
            clusterVO.setClusterStatus(ClusterStatus.TERMINATED);
        }

        mapOtherClusterDetailsFromResponse(clusterVO, clusterDetails);

        logger.info("EMRClusterManagementHelper API->updateClusterStatusByClusterId starting");
        clusterMetricsHelper.updateClusterStatusByClusterId(clusterVO);

        // fetch masterIp from metadata needed for dns deletion
        ClusterVO clusterMetadata = metadataDao.getClusterMetadata(clusterDetails.getClusterId());
        String defaultDnsName = clusterMetadata.getDnsName();

        //Deleting the Default DNS Entry if dns flip flag is marked to False and it exists in metadata means
        //this cluster was still pointing to default dns
        if (!StringUtils.isBlank(defaultDnsName) && !clusterMetadata.getDnsFlipCompleted()) {

            String masterIp = clusterMetadata.getMasterIp();

            logger.info("Deleting Default DNS: " + clusterMetadata.getDnsName() + " after terminating cluster: "
                    + clusterDetails.getClusterName());

            //Catching QuickFabricRestHandlerException in order not to break the code
            try {
                emrAwsServiceCaller.invokeAWSDNSFlip(clusterDetails.getClusterName(), clusterDetails.getAccount(), defaultDnsName, masterIp, "delete");
            } catch (QuickFabricRestHandlerException e) {
                logger.error("EMRClusterManagementHelper API->invokeAWSDNSCreate -> Exception: ", e.getMessage(), e);
            }
        }

        logger.info("EMRClusterManagementHelper API->terminateEMRCluster completed for cluster"
                + clusterDetails.getClusterName());
        return clusterVO;
    }


    public void getLatestClusterStatus(String clusterId, String accountId) {

        try {
            logger.info("EMRClusterManagementHelper API->getLatestClusterStatus starting for cluster" + clusterId);

            String accountSpecificUrl = configHelper.getAccountSpecificUrl(accountId, ApiUrls.CLUSTER_STATUS_CHECK_URL_SUFFIX);
            ClusterVO clusterResponseVO = emrAwsServiceCaller.invokeAWSClusterStatusCheckService(accountSpecificUrl, clusterId, accountId);
            // if the message is returned Cluster not present ,then set the status as
            // TERMINATED in DB.
            if (CLUSTER_NOT_PRESENT.equalsIgnoreCase(clusterResponseVO.getMessage())) {
                clusterResponseVO.setClusterStatus(ClusterStatus.TERMINATED);
            }
            clusterMetricsHelper.updateClusterStatusByClusterId(clusterResponseVO);
            logger.info("EMRClusterManagementHelper API->getLatestClusterStatus completed for cluster" + clusterId);
        } catch (Exception e) {
            logger.error("something went wrong in Cluster latest status fetch for ClusterId {}", clusterId, e);
        }

    }


    public void addStepToSucceededClustersScheduler(ClusterRequest stepReq) {
        logger.info("EMRClusterManagementHelper API->addStepToSucceededClustersScheduler starting for cluster"
                + stepReq.getClusterName());
        ClusterVO newCluster = new ClusterVO();
        mapCreateClusterReqToClusterVO(stepReq, newCluster);
        processAddSteps(newCluster);
        logger.info("EMRClusterManagementHelper API->addStepToSucceededClustersScheduler completed for cluster"
                + stepReq.getClusterName());
    }

    private void processAddSteps(ClusterVO newCluster) {
        StepResponseVO stepResponse;
        ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(newCluster);
        } catch (JsonProcessingException e) {
            throw new QuickFabricJsonException(e);
        }
        String accountSpecificUrl = configHelper.getAccountSpecificUrl(newCluster.getAccount(), ApiUrls.ADD_CUSTOM_STEP_URL_SUFFIX);
        stepResponse = emrAwsServiceCaller.invokeAWSClusterAddStepService(accountSpecificUrl, json, newCluster.getAccount());
        // if the message is returned Cluster not present ,then set the status as
        // TERMINATED in DB.
        clusterMetricsHelper.updateStepIdsInDB(stepResponse, stepResponse.getSteps());


    }


    public void validateSteps(String clusterId, String clusterName, String[] stepIds, String accountId) {
        logger.info("EMRClusterManagementHelper API->validateSteps starting for cluster -" + clusterName + "step id -"
                + stepIds);
        String stepValidateJson = null;
        stepValidateJson = createJsonToValidateSteps(clusterId, clusterName, stepIds);
        String accountSpecificUrl = configHelper.getAccountSpecificUrl(accountId, ApiUrls.VALIDATE_STEP_URL_SUFFIX);
        StepResponseVO stepResponseVO = emrAwsServiceCaller.invokeAWSClusterValidateStepsService(accountSpecificUrl, stepValidateJson, accountId);
        clusterMetricsHelper.updateStepIdsInDB(stepResponseVO, stepResponseVO.getSteps());
        logger.info("EMRClusterManagementHelper API->validateSteps completed for cluster" + clusterName);
    }


    String createJsonToValidateSteps(String clusterId, String clusterName, String[] stepIds) {
        String stepValidateJson;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("clusterId", clusterId);
            jsonObject.put("clusterName", clusterName);
            JSONArray jarray = new JSONArray(stepIds);
            jsonObject.put("stepIds", jarray);
        } catch (JSONException e) {
            throw new QuickFabricJsonException(e);
        }
        stepValidateJson = jsonObject.toString();
        return stepValidateJson;
    }

    public void rotateAMI() {
        Set<ClusterStatus> statusList = new HashSet<ClusterStatus>();
        statusList.add(ClusterStatus.RUNNING);
        statusList.add(ClusterStatus.WAITING);

        List<ClusterRequest> clustersToRotate = clusterMetricsHelper.getClustersforAMIRotation(statusList);
        for (ClusterRequest clusterDetails : clustersToRotate) {
            try {
                int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                if (currentHour >= clusterDetails.getAutopilotWindowStart() && currentHour < clusterDetails.getAutopilotWindowEnd()) {
                    //To Do: Put a validator to check if a cluster has been used for rotation already
                    // and new cluster is not terminated or failed
                    ClusterVO validateAMIReq = metadataDao.getClusterMetadataByOriginalClusterId(clusterDetails.getClusterId());
                    if (validateAMIReq != null) {
                        String invalidReq = "There is already a cluster in running state: " + validateAMIReq.getClusterName() +
                                " rotated using this cluster ID:" + clusterDetails.getClusterId() +
                                " .You cannot rotate the cluster again using the same one";
                        logger.error(invalidReq);
                        throw new QuickFabricClientException(invalidReq);
                    }

                    String clusterName = clusterDetails.getClusterName();
                    String clusterId = clusterDetails.getClusterId();
                    String accountId = clusterDetails.getAccount();
                    boolean isProd = clusterDetails.getIsProd();
                    logger.info("EMRClusterManagementHelper scheduler ->rotateAMI starting for cluster: {}", clusterName);

                    // Retrieve cluster meta data
                    ClusterVO originalClusterVo;
                    originalClusterVo = clusterMetadataHelper.getAllEMRClusterDetailsByClusterID(clusterId);
                    originalClusterVo.setBootstrapActions(clusterStepsDao.getBootstrapActionsByClusterId(clusterId));
                    originalClusterVo.setSubType("nonkerb");

                    // Validate EMR cluster
                    String statusCheckUrl = configHelper.getAccountSpecificUrl(accountId, ApiUrls.CLUSTER_STATUS_CHECK_URL_SUFFIX);
                    ClusterVO clusterVO = emrAwsServiceCaller.invokeAWSClusterStatusCheckService(statusCheckUrl, clusterId, accountId);
                    if (!clusterVO.getStatus().equals(ClusterStatus.WAITING)) {
                        String message = "EMRClusterManagementHelper scheduler ->rotateAMI Can only rotate AMIs for WAITING/IDLE clusters.";
                        throw new QuickFabricServerException(message);
                    }

                    logger.info("EMRClusterManagementHelper scheduler ->rotateAMI Cluster has been validated.");
                    if (isProd) {
                        //HA Use-Case
                        rotateHACluster(clusterId, accountId, originalClusterVo);
                    } else {
                        //Non-HA Use-Case
                        rotateNonHACluster(clusterName, clusterId, accountId, originalClusterVo);
                    }
                }
            } catch (Exception e) {
                logger.error("error happened while rotating AMI", e);
            }
        }
    }

    private void rotateHACluster(String clusterId, String accountId, ClusterVO originalClusterVo) throws Exception {
        ClusterRequest newCluster = new ClusterRequest();
        BeanUtils.copyProperties(originalClusterVo, newCluster);
        mapClusterDetailsToClusterRequestFields(originalClusterVo, newCluster);

        // validation and iterate until a suitable number is found and setting sub type of new cluster
        String segment = originalClusterVo.getSegment();
        String type = originalClusterVo.getType().toString();
        StringBuilder appendedName = new StringBuilder();

        logger.info("EMRClusterManagementHelper scheduler ->rotateAMI HA -> Iterating to find name for new cluster");
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
            boolean clusterNotPresent = isClusterNotPresent(newClusterName, accountId);
            if (clusterNotPresent) {
                logger.info("EMRClusterManagementHelper scheduler ->rotateAMI HA -> Name found, setting it to:" + newName);
                newCluster.setClusterName(newClusterName);
                break;
            } else {
                logger.info("EMRClusterManagementHelper scheduler ->rotateAMI HA -> Name not found, going back into the loop");
                //Clearing up the stringbuilder to set for another run
                appendedName.setLength(0);
            }
        }

        if (StringUtils.isBlank(appendedName.toString())) {
            throw new QuickFabricServerException("new cluster name was not found in 10 tries");
        }

        //Saving flags for the new cluster
        newCluster.setIsProd(true);
        newCluster.setOriginalClusterId(clusterId);
        newCluster.setCreatedBy("Autopilot");

        logger.info("EMRClusterManagementHelper scheduler ->rotateAMI HA -> Proceeding to Create Cluster");
        ClusterVO responseClusterVO = createEMRCluster(newCluster);

        if (StringUtils.isNotBlank(responseClusterVO.getClusterId())) {
            //UPDATING New Cluster ID in Old One and marking old cluster for auto-termination
            logger.info("Updating New Cluster ID in Old One and marking old cluster for auto-termination");
            metadataDao.updateNewClusterByOriginalClusterId(clusterId, responseClusterVO.getClusterId());
            metadataDao.markClusterforTermination(clusterId);
        }

        logger.info("EMRClusterManagementHelper scheduler ->rotateAMI HA -> Created cluster: " + newCluster.getClusterName());
    }

    private void rotateNonHACluster(String clusterName, String clusterId, String accountId, ClusterVO originalClusterVo) throws JSONException, InterruptedException {
        ClusterRequest newCluster = new ClusterRequest();
        BeanUtils.copyProperties(originalClusterVo, newCluster);
        mapClusterDetailsToClusterRequestFields(originalClusterVo, newCluster);

        newCluster.setOriginalClusterId(clusterId);
        newCluster.setCreatedBy("Autopilot");
        metadataDao.updateNewClusterByOriginalClusterId(clusterId, clusterName);

        ClusterVO terminationResponse = terminateEMRCluster(newCluster);
        logger.info("EMRClusterManagementHelper scheduler ->rotateAMI Non-HA -> Termination initiated.");

        int attempt = 1;
        int maxAttempts = 36; // 3 minutes
        String statusCheckUrl = configHelper.getAccountSpecificUrl(accountId, ApiUrls.CLUSTER_STATUS_CHECK_URL_SUFFIX);
        while (!terminationResponse.getStatus().equals(ClusterStatus.TERMINATED) && attempt <= maxAttempts) {
            logger.info("checking terminate status. attempt number:" + attempt);
            TimeUnit.SECONDS.sleep(5); // Reduces the number of times the check service is called
            terminationResponse = emrAwsServiceCaller.invokeAWSClusterStatusCheckService(statusCheckUrl, clusterId, accountId);
            attempt++;
        }

        if (!terminationResponse.getStatus().equals(ClusterStatus.TERMINATED)) {
            throw new QuickFabricServerException("cluster never got terminated.");
        }

        logger.info("EMRClusterManagementHelper scheduler ->rotateAMI Non-HA -> Termination completed");
        logger.info("EMRClusterManagementHelper scheduler ->rotateAMI Non-HA -> Proceeding to Create Cluster");
        ClusterVO responseClusterVO = createEMRCluster(newCluster);

        if (StringUtils.isNotBlank(responseClusterVO.getClusterId())) {
            //UPDATING New Cluster ID in Old One
            logger.info("Updating New Cluster ID in Old One");
            metadataDao.updateNewClusterByOriginalClusterId(clusterId, responseClusterVO.getClusterId());
        }

        logger.info("EMRClusterManagementHelper scheduler ->rotateAMI Non-HA -> Created cluster: " + newCluster.getClusterName());
    }

    private void mapClusterDetailsToClusterRequestFields(ClusterVO clusterVo, ClusterRequest clusterRequest) {
        try {
            JSONObject clusterHardwareDetailsJson = new JSONObject(clusterVo.getClusterDetails());
            clusterRequest.setCoreEbsVolSize(clusterHardwareDetailsJson.getString("Core_ebs_vol_size"));
            clusterRequest.setCoreInstanceCount(clusterHardwareDetailsJson.getString("Core_instance_count"));
            clusterRequest.setCoreInstanceType(clusterHardwareDetailsJson.getString("Core_instance_type"));
            clusterRequest.setMasterEbsVolSize(clusterHardwareDetailsJson.getString("Master_ebs_vol_size"));
            clusterRequest.setTaskEbsVolSize(clusterHardwareDetailsJson.getString("Task_ebs_vol_size"));
            clusterRequest.setTaskInstanceCount(clusterHardwareDetailsJson.getString("Task_instance_count"));
            clusterRequest.setTaskInstanceType(clusterHardwareDetailsJson.getString("Task_instance_type"));
            clusterRequest.setMasterInstanceType(clusterHardwareDetailsJson.getString("Master_instance_type"));
        } catch (JSONException e) {
            throw new QuickFabricJsonException("error happened while mapClusterDetailsToClusterRequestFields", e);
        }
    }

    /**
     * Delete records pertaining to clusters that were terminated over 6 months ago.
     *
     * @param daysAgo TODO
     */
    public void cleanUpTerminatedClusters(int daysAgo) {
        boolean succeeded = metadataDao.cleanUpTerminatedClusters(daysAgo);
        if (succeeded) {
            logger.info("Cleanup of terminated clusters completed successfully.");
        } else {
            logger.info("Cleanup of terminated clusters failed.");
        }
    }

}