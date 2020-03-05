package com.intuit.quickfabric.schedulers.functions;

import com.intuit.quickfabric.commons.constants.ApplicationConstant;
import com.intuit.quickfabric.commons.exceptions.QuickFabricClientException;
import com.intuit.quickfabric.commons.helper.ConfigHelper;
import com.intuit.quickfabric.commons.utils.CommonUtils;
import com.intuit.quickfabric.commons.utils.EnableMethod;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.schedulers.dao.ClusterHealthCheckDao;
import com.intuit.quickfabric.schedulers.dao.EMRClusterMetadataDao;
import com.intuit.quickfabric.schedulers.helpers.EMRClusterManagementHelper;
import com.intuit.quickfabric.schedulers.helpers.EMRClusterMetricsHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Component
@ConditionalOnProperty(value = "scheduling.enabled", havingValue = "true", matchIfMissing = false)

public class EMRClusterLifeCycleSchedulers {


    private static final Logger logger = LogManager.getLogger(EMRClusterLifeCycleSchedulers.class);

    @Autowired
    EMRClusterMetricsHelper clusterMetricsHelper;

    @Autowired
    EMRClusterManagementHelper clusterManagementHelper;

    @Autowired
    ClusterHealthCheckDao clusterHealthCheckDao;

    @Autowired
    EMRClusterMetadataDao emrMetadataDao;

    @Autowired
    ConfigHelper configHelper;

    @Scheduled(cron = "${validateNewClustersSchedule}")
    @Async
    @EnableMethod(configName = "validate_new_clusters_scheduler")
    public void validateNewClusters() {

            List<ClusterVO> clusters = null ;
            try {
            	 final LocalDateTime start = LocalDateTime.now();
                 logger.info("EMRClusterLifeCycleSchedulers->validateNewClusters starting at " + start);
                 Set<ClusterStatus> statuses = new HashSet<ClusterStatus>();
                 //Getting clusters with below cluster status to Validate cluster statues in AWS.
                 statuses.add(ClusterStatus.STARTING);
                 statuses.add(ClusterStatus.BOOTSTRAPPING);
            	 clusters = clusterMetricsHelper.getClustersToValidate(statuses);
            }catch (Exception e) {
                CommonUtils.logErrorResponse(e);
            }
            for (ClusterVO c : clusters) {
            	try {
                    logger.info("EMRClusterLifeCycleSchedulers->validateNewCluster for ClusterName :{} and ClusterId {}",c.getClusterName(),c.getClusterId());
                    clusterManagementHelper.validateEMRCluster(c);
                    logger.info("EMRClusterLifeCycleSchedulers->validateNewClusters completed");
            	}catch (Exception e) {
                    CommonUtils.logErrorResponse(e);
            	}
            }
    }

    @Scheduled(cron = "${statuscheckExistingClustersSchedule}")
    @Async
    @EnableMethod(configName = "validate_existing_clusters_scheduler")
    public void validateExistingClusters() {

        try {
        	final LocalDateTime start = LocalDateTime.now();
            logger.info("EMRClusterLifeCycleSchedulers->validateExistingClusters starting at " + start);

            Set<ClusterStatus> statuses = new HashSet<ClusterStatus>();
            //Getting clusters with below cluster status to Validate cluster statues in AWS.
            // TODO replace with new api
            statuses.add(ClusterStatus.RUNNING);
            statuses.add(ClusterStatus.WAITING);
            List<ClusterVO> clusters = clusterMetricsHelper.getClustersToValidate(statuses);
            for (ClusterVO c : clusters) {
                clusterManagementHelper.getLatestClusterStatus(c.getClusterId(), c.getAccount());
                logger.info("EMRClusterLifeCycleSchedulers->validateExistingClusters completed");
            }
        } catch (Exception e) {
            CommonUtils.logErrorResponse(e);
        }



    }

    @Scheduled(cron = "${statuscheckTermInitiatedSchedule}")
    @Async
    @EnableMethod(configName = "check_cluster_status_with_termination_initiated_scheduler")
    public void checkClusterStatusWithTerminationInitiated() {
    	List<ClusterRequest> clusters =null;
        try {
        	final LocalDateTime start = LocalDateTime.now();
            logger.info("EMRClusterLifeCycleSchedulers->checkClusterStatusWithTerminationInitiated starting at " + start);

            Set<ClusterStatus> statuses = new HashSet<ClusterStatus>();
            statuses.add(ClusterStatus.TERMINATION_INITIATED);
            statuses.add(ClusterStatus.TERMINATING);
            clusters = clusterMetricsHelper.getClustersWithClusterStatuses(statuses);
        }catch(Exception e) {
            CommonUtils.logErrorResponse(e);
        }
            for (ClusterRequest c : clusters) {
            	try {
                clusterManagementHelper.getLatestClusterStatus(c.getClusterId(), c.getAccount());
            	}catch(Exception e) {
                    CommonUtils.logErrorResponse(e);
            	}
            }
    }

    @Scheduled(cron = "${terminateCompletedClustersSchedule}")
    @Async
    @EnableMethod(configName = "terminate_completed_clusters_scheduler")
    public void terminateCompletedClusters() {
        final LocalDateTime start = LocalDateTime.now();
        logger.info("EMRClusterLifeCycleSchedulers->terminateCompletedClusters starting at " + start);

        Set<ClusterStatus> statuses = new HashSet<ClusterStatus>();
        statuses.add(ClusterStatus.RUNNING);
        statuses.add(ClusterStatus.WAITING);
        List<StepStatus> stepStatuses = new ArrayList<StepStatus>();
        stepStatuses.add(StepStatus.COMPLETED);
        stepStatuses.add(StepStatus.FAILED);
        stepStatuses.add(StepStatus.CANCELLED);
        stepStatuses.add(StepStatus.COMPLETED_TERMINATEDCLUSTER);

        List<ClusterRequest> transientClusters = clusterMetricsHelper.getCompletedTransientClusters(statuses, stepStatuses);
        List<ClusterRequest> nontransientClusters = clusterMetricsHelper.getCompletedNonTransientClusters(statuses, stepStatuses);
        List<ClusterRequest> testingCluster = clusterMetricsHelper.getCompletedTestingClusters(statuses, stepStatuses);

        //Terminating Transient Clusters
        for (ClusterRequest c : transientClusters) {
            clusterManagementHelper.terminateEMRCluster(c);
        }

        //Terminating Non-Transient Clusters
        for (ClusterRequest c : nontransientClusters) {
            //Checking if the newly created cluster through old one is one day older
            ClusterVO newCluster = emrMetadataDao.getClusterMetadataByOriginalClusterId(c.getClusterId());
            if (newCluster != null) {
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try {
                    Date parsedDate = sdf.parse(newCluster.getCreationTimestamp());
                    Timestamp newClusterCreateTimestamp = new Timestamp(parsedDate.getTime());
                    Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

                    long millisecondsDiff = currentTimestamp.getTime() - newClusterCreateTimestamp.getTime();
                    int hoursDiff = (int) (millisecondsDiff / (1000 * 60 * 60));

                    if (hoursDiff >= 24) {
                        //terminating the old cluster as new one has been there for 24 hours
                        clusterManagementHelper.terminateEMRCluster(c);
                    } else {
                        logger.info("EMRClusterLifeCycleSchedulers->terminateCompletedClusters Cluster with name: " + c.getClusterName()
                                + " ID: " + c.getClusterId() + " will not be terminated. "
                                + "As new cluster has been up for less than 1 day. ElapsedTime: " + hoursDiff);
                    }
                } catch (ParseException e) {
                    CommonUtils.logErrorResponse(e);
                }
            } else {
                logger.warn("No Cluster Found in Running State rotated from CLuster ID: " + c.getClusterId());
            }
        }

        //Terminating Testing segment clusters after x hours based on test_cluster_ttl config value
        for (ClusterRequest c : testingCluster) {
        	boolean autoTerminationEnabled=false;
        	try {
                autoTerminationEnabled =
                    configHelper.getConfigValue("test_cluster_auto_termination", c.getAccount());
        	}catch(QuickFabricClientException e) {
        		logger.error("Config: test_cluster_auto_termination not found. Exception: {}",
                        e.getMessage());
        	}
            if (autoTerminationEnabled) {
                int ttl = 0;
                try {
                    ttl = configHelper.getConfigValue("test_cluster_ttl", c.getAccount());
        		} catch (QuickFabricClientException e) {
                    logger.error("Config: test_cluster_ttl not found. Exception: {}",
                            e.getMessage());
                    ttl = 2;
                    logger.error("EMRClusterLifeCycleSchedulers->terminateCompletedClusters "
                            + "config test_cluster_ttl not found. Setting default ttl to {}", ttl);
                }

        		clusterManagementHelper.terminateOOSLACluster(c, ttl);
            }
        }//end of FOR loop
        logger.info("EMRClusterLifeCycleSchedulers->terminateCompletedClusters completed");
    }

    @Scheduled(cron = "${addstepstoClusterSchedule}")
    @Async
    @EnableMethod(configName = "add_steps_to_clusters_scheduler")
    public void addStepsToNewClusters() {
    	List<ClusterRequest> stepRequests = null;
        try {
	    	final LocalDateTime start = LocalDateTime.now();
	        logger.info("EMRClusterLifeCycleSchedulers->addStepsToNewClusters starting at {}", start);

            Set<ClusterStatus> statuses = new HashSet<ClusterStatus>();
	        statuses.add(ClusterStatus.RUNNING);
	        statuses.add(ClusterStatus.WAITING);
	        List<StepStatus> stepStatuses = new ArrayList<StepStatus>();
	        stepStatuses.add(StepStatus.NEW);

            //Custom Steps
	        stepRequests = clusterMetricsHelper.getStepsForNewSucceededClusters(statuses, stepStatuses);
        }catch(Exception e) {
        	CommonUtils.logErrorResponse(e);
        }
        for (ClusterRequest req : stepRequests) {
        	try {
        		clusterManagementHelper.addStepToSucceededClustersScheduler(req);
                logger.info("EMRClusterLifeCycleSchedulers->addStepsToNewClusters completed  at " + LocalDateTime.now());
        	}catch(Exception e) {
            	CommonUtils.logErrorResponse(e);
            }
        }

    }

    @Scheduled(cron = "${validateClusterStepsSchedule}")
    @Async
    @EnableMethod(configName = "validate_cluster_steps_scheduler")
    public void validateStepsOfClusters() {
    	List<ClusterRequest> stepRequests=null;
    	try{
	    	final LocalDateTime start = LocalDateTime.now();
	        logger.info("EMRClusterLifeCycleSchedulers->validateStepsOfClusters starting at {}", start);

            List<StepStatus> stepStatuses = new ArrayList<StepStatus>();
	        stepStatuses.add(StepStatus.PENDING);
	        stepStatuses.add(StepStatus.RUNNING);
	        stepStatuses.add(StepStatus.VALIDATED);
	        stepStatuses.add(StepStatus.INTIATED);
	        stepStatuses.add(StepStatus.CANCEL_PENDING);

            Set<ClusterStatus> clusterStatuses = new HashSet<ClusterStatus>();
	        clusterStatuses.add(ClusterStatus.RUNNING);
	        clusterStatuses.add(ClusterStatus.WAITING);

            //Custom Steps
	        stepRequests = clusterMetricsHelper.getStepsForNewSucceededClusters(clusterStatuses, stepStatuses);
    	}catch(Exception e) {
        	CommonUtils.logErrorResponse(e);
        }
        for (ClusterRequest req : stepRequests) {
        	try {
	            String[] stepids = new String[req.getSteps().size()];
	            int i = 0;
	            for (ClusterStepRequest step : req.getSteps()) {

                    stepids[i++] = step.getStepId();
	            }

                clusterManagementHelper.validateSteps(req.getClusterId(), req.getClusterName(), stepids, req.getAccount());
	            logger.info("EMRClusterLifeCycleSchedulers->validateStepsOfClusters completed  for stepids {}", stepids);
        	}catch(Exception e) {
            	CommonUtils.logErrorResponse(e);
            }
       }//end of upper FOR loop

    }

    @Scheduled(cron = "${clusterCleanUpSchedule}")
    @Async
    @EnableMethod(configName = "rds_cleanup_scheduler")
    public void cleanUpTerminatedClusters() {
    	try {
	        final LocalDateTime start = LocalDateTime.now();
	        logger.info("EMRClusterLifeCycleSchedulers->cleanUpTerminatedClusters starting at {}", start);
	        clusterManagementHelper.cleanUpTerminatedClusters(ApplicationConstant.TERMINATED_CLUSTER_CLEAN_DAYS_AGO);
	        logger.info("EMRClusterLifeCycleSchedulers->cleanUpTerminatedClusters finished at {}", LocalDateTime.now());
    	}catch(Exception e) {
        	CommonUtils.logErrorResponse(e);
        }
    }

    @Scheduled(cron = "${autoAMIRotationSchedule}")
    @Async
    @EnableMethod(configName = "auto_ami_rotate_scheduler")
    public void autoAMIRotationOfClusters() {
        try {
            logger.info("EMRClusterLifeCycleSchedulers->autoAMIRotationOfClusters starting at {}", LocalDateTime.now());
            clusterManagementHelper.rotateAMI();
            logger.info("EMRClusterLifeCycleSchedulers->autoAMIRotationOfClusters finished at {}", LocalDateTime.now());
        } catch (Exception e) {
            logger.error("error happened during autoAMIRotationOfClusters.", e);
        }
    }
}
