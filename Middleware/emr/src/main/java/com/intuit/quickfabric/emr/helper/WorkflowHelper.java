package com.intuit.quickfabric.emr.helper;

import com.intuit.quickfabric.commons.constants.WorkflowConstants;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.emr.dao.EMRClusterHealthCheckDao;
import com.intuit.quickfabric.emr.dao.EMRClusterMetadataDao;
import com.intuit.quickfabric.emr.dao.WorkflowDao;
import com.intuit.quickfabric.commons.exceptions.QuickFabricClientException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkflowHelper {

    private final Logger logger = LogManager.getLogger(WorkflowHelper.class);

    @Autowired
    EMRClusterMetadataDao clusterMetadataDao;

    @Autowired
    EMRClusterHealthCheckDao clusterHealthCheckDao;

    @Autowired
    WorkflowDao workflowDao;

    public Workflow selectWorkflow(String workflowName,long metadataId) {
        logger.info("WorkflowService API-> getWorkflow");
        Workflow workflow = null;
        switch (workflowName.toUpperCase()) {
            case WorkflowConstants.CREATE_CLUSTER:
                workflow = getCreateClusterWorkflow(metadataId);
                break;

            case WorkflowConstants.ROTATE_AMI:
                workflow = getRotateAMIWorkflow(metadataId);
                break;

            default:
                logger.error("Workflow name not found:" + workflowName);
                throw new QuickFabricClientException("Workflow name not found:" + workflowName);
        }
        return workflow;
    }
    
    private Workflow getCreateClusterWorkflow(long metadataId) {

        logger.info("Starting create workflow for metadataId:" + metadataId);
        Workflow workflow = workflowDao.getWorkflow(WorkflowConstants.CREATE_CLUSTER);

        ClusterVO cluster = clusterMetadataDao.getEMRClusterMetadataByMetadataId(metadataId);

        // check of cluster exists or not
        WorkflowStep createClusterStep = workflow.getStep(WorkflowType.CREATE_NEW_CLUSTER);
        if (cluster == null) {
            logger.info("getCreateClusterWorkflow - cluster not found.");
            return workflow;
        }

        // cluster status
        switch (cluster.getStatus()) {
            case STARTING:
                createClusterStep.setWorkflowStatus(WorkflowStatus.IN_PROGRESS);
                break;
            case BOOTSTRAPPING:
                createClusterStep.setWorkflowStatus(WorkflowStatus.IN_PROGRESS);
                break;
            case RUNNING:
                createClusterStep.setWorkflowStatus(WorkflowStatus.SUCCESS);
                break;
            case WAITING:
                createClusterStep.setWorkflowStatus(WorkflowStatus.SUCCESS);
                break;
            case FAILED:
                createClusterStep.setWorkflowStatus(WorkflowStatus.FAILED);
                break;
            case TERMINATED:
            case ClusterNotPresent:
            case TERMINATING:
            case TERMINATION_INITIATED:
            case TERMINATED_WITH_ERRORS:
                createClusterStep.setWorkflowStatus(WorkflowStatus.FAILED);
                break;
        }

        logger.info("getCreateClusterWorkflow - setting create cluster workflow step CREATE_NEW_CLUSTER as:" + createClusterStep.getWorkflowStatus());

        // Checking workflow status for Cluster Bootstrap passed during creation 
        WorkflowStep clusterBootstraps = workflow.getStep(WorkflowType.CLUSTER_BOOTSTRAPS);
        List<ClusterStep> bootstrapSteps = clusterHealthCheckDao.getClusterBootStraps(cluster.getClusterId());

        logger.info("getCreateClusterWorkflow - creating workflow for cluster bootstraps");

        if (bootstrapSteps.size() == 0) {
            clusterBootstraps.setWorkflowStatus(WorkflowStatus.NEW);
            clusterBootstraps.setMessage("No Bootstraps found");
        } else if (bootstrapSteps.stream().allMatch(x -> x.getStatus() == StepStatus.NEW || x.getStatus() == StepStatus.INTIATED)) {
            clusterBootstraps.setWorkflowStatus(WorkflowStatus.NEW);

        } else if (bootstrapSteps.stream().anyMatch(x -> x.getStatus() == StepStatus.FAILED
                || x.getStatus() == StepStatus.TERMINATED
                || x.getStatus() == StepStatus.CANCELLED)) {

            clusterBootstraps.setWorkflowStatus(WorkflowStatus.FAILED);
            clusterBootstraps.setMessage("Steps failed/cancelled/terminated. Step names " +
                    bootstrapSteps.stream().filter(x -> x.getStatus() == StepStatus.FAILED
                            || x.getStatus() == StepStatus.TERMINATED
                            || x.getStatus() == StepStatus.CANCELLED)
                            .map(ClusterStep::getName).collect(Collectors.joining(", ")));

        } else if (bootstrapSteps.stream().anyMatch(x -> x.getStatus() == StepStatus.RUNNING
                || x.getStatus() == StepStatus.PENDING)) {

            clusterBootstraps.setWorkflowStatus(WorkflowStatus.IN_PROGRESS);

        } else if (bootstrapSteps.stream().allMatch(x -> x.getStatus() == StepStatus.COMPLETED)) {
            clusterBootstraps.setWorkflowStatus(WorkflowStatus.SUCCESS);
        }

        logger.info("getCreateClusterWorkflow - setting create cluster workflow step CLUSTER_BOOTSTRAPS as:" + clusterBootstraps.getWorkflowStatus());

        // Checking workflow status for Cluster Custom Steps passed during creation or added afterwards
        WorkflowStep clusterCustomSteps = workflow.getStep(WorkflowType.CLUSTER_CUSTOM_STEPS);
        List<ClusterStep> customSteps = clusterHealthCheckDao.getClusterCustomSteps(cluster.getClusterId());

        logger.info("getCreateClusterWorkflow - creating workflow for cluster custom steps");

        if (customSteps.size() == 0) {
            clusterCustomSteps.setWorkflowStatus(WorkflowStatus.NEW);
            clusterCustomSteps.setMessage("No Custom Steps found");
        } else if (customSteps.stream().allMatch(x -> x.getStatus() == StepStatus.NEW || x.getStatus() == StepStatus.INTIATED)) {
            clusterCustomSteps.setWorkflowStatus(WorkflowStatus.NEW);

        } else if (customSteps.stream().anyMatch(x -> x.getStatus() == StepStatus.FAILED
                || x.getStatus() == StepStatus.TERMINATED
                || x.getStatus() == StepStatus.CANCELLED)) {

            clusterCustomSteps.setWorkflowStatus(WorkflowStatus.FAILED);
            clusterCustomSteps.setMessage("Steps failed/cancelled/terminated. Step names " +
                    customSteps.stream().filter(x -> x.getStatus() == StepStatus.FAILED
                    || x.getStatus() == StepStatus.TERMINATED
                    || x.getStatus() == StepStatus.CANCELLED)
                    .map(ClusterStep::getName).collect(Collectors.joining(", ")));

        } else if (customSteps.stream().anyMatch(x -> x.getStatus() == StepStatus.RUNNING
                || x.getStatus() == StepStatus.PENDING)) {

            clusterCustomSteps.setWorkflowStatus(WorkflowStatus.IN_PROGRESS);

        } else if (customSteps.stream().allMatch(x -> x.getStatus() == StepStatus.COMPLETED)) {
            clusterCustomSteps.setWorkflowStatus(WorkflowStatus.SUCCESS);
        }

        logger.info("getCreateClusterWorkflow - setting create cluster workflow step CLUSTER_CUSTOM_STEPS as:" + clusterCustomSteps.getWorkflowStatus());
        
        if (clusterCustomSteps.getWorkflowStatus() != WorkflowStatus.IN_PROGRESS) {
            // Checking workflow status for Cluster test suites/ health check
            WorkflowStep healthCheckStep = workflow.getStep(WorkflowType.HEALTH_CHECK);
            List<ClusterHealthStatus> healthChecks = clusterHealthCheckDao.getEMRClusterHealthStatus(cluster.getClusterId());
            logger.info("getCreateClusterWorkflow - creating workflow for health check");

            if (healthChecks.size() == 0) {
                healthCheckStep.setWorkflowStatus(WorkflowStatus.NEW);
                healthCheckStep.setMessage("No health check tests found");
            } else if (healthChecks.stream().allMatch(x -> x.getStatus() == ClusterHealthCheckStatusType.NEW))
                healthCheckStep.setWorkflowStatus(WorkflowStatus.NEW);
            else if (healthChecks.stream().anyMatch(x -> x.getStatus() == ClusterHealthCheckStatusType.FAILED)) {
                healthCheckStep.setWorkflowStatus(WorkflowStatus.FAILED);
                healthCheckStep.setMessage("Health checks failed: " + healthChecks.stream()
                        .filter(x -> x.getStatus() == ClusterHealthCheckStatusType.FAILED)
                        .map(ClusterHealthStatus::getTestName).collect(Collectors.joining(", ")));

            } else if (healthChecks.stream().anyMatch(x -> x.getStatus() == ClusterHealthCheckStatusType.INPROGRESS
                    || x.getStatus() == ClusterHealthCheckStatusType.INITIATED)) {
                healthCheckStep.setWorkflowStatus(WorkflowStatus.IN_PROGRESS);
            } else if (healthChecks.stream().allMatch(x -> x.getStatus() == ClusterHealthCheckStatusType.SUCCESS)) {
                healthCheckStep.setWorkflowStatus(WorkflowStatus.SUCCESS);
            }

            logger.info("getCreateClusterWorkflow - setting health check workflow step HEALTH_CHECK as:" + healthCheckStep.getWorkflowStatus());
        }
        return workflow;
    }

    private Workflow getRotateAMIWorkflow(long metadataId) {
        logger.info("Starting RotateAMI workflow for metadataId:" + metadataId);

        Workflow workflow;
        ClusterVO clusterRequest = clusterMetadataDao.getEMRClusterMetadataByMetadataId(metadataId);
        if (clusterRequest == null) {
            throw new QuickFabricClientException("Metadata id not found:" + metadataId);
        }

        ClusterVO newCluster = clusterMetadataDao.getClusterMetadataByOriginalClusterId(clusterRequest.getClusterId());
        if(newCluster == null) {
            throw new QuickFabricClientException("There is no Running Cluster that has been rotated"
                    + " using this cluster ID: " + clusterRequest.getClusterId());
        }

        if (!newCluster.getIsProd()) {
            workflow = getNonHighAvailabilityWorkflow(clusterRequest);
        } else {
            workflow = getHighAvailabilityWorkflow(clusterRequest);
        }
        return workflow;
    }

    private Workflow getHighAvailabilityWorkflow(ClusterVO clusterRequest) {
        logger.info("Starting HighAvailabilityWorkflow for AMI Rotation. For clusterId:" + clusterRequest.getClusterId());

        Workflow rotateAMIHAWorkflow = workflowDao.getWorkflow(WorkflowConstants.ROTATE_AMI_HA);

        //Fetching record id for newly created cluster
        String currentClusterId = clusterRequest.getClusterId();
        ClusterVO newCluster = clusterMetadataDao.getClusterMetadataByOriginalClusterId(currentClusterId);

        //Calling create cluster workflow for adding create steps
        List<WorkflowStep> createClusterSteps = getCreateClusterWorkflow(newCluster == null ? 0 : newCluster.getMetadataId()).getWorkflowSteps();
        rotateAMIHAWorkflow.mergeSteps(createClusterSteps);
        
        //Checking if current cluster is marked for termination step
        WorkflowStep currentClusterMarkForTerminationStep = rotateAMIHAWorkflow.getStep(WorkflowType.MARK_CURRENT_CLUSTER_FOR_TERMINATION);
        ClusterVO currentCluster = clusterMetadataDao.getClusterMetadataByClusterId(currentClusterId);
        logger.info("getHighAvailabilityWorkflow - checking mark for termination");
        
        //Checking if New Cluster was created or not
        //based on that the workflow for mark for termination with change
        if(newCluster != null) {
            if (currentCluster.getDoTerminate()) {
                currentClusterMarkForTerminationStep.setWorkflowStatus(WorkflowStatus.SUCCESS);
                currentClusterMarkForTerminationStep.setMessage("Cluster with name " + currentCluster.getClusterName() +
                        " and ID: " + currentClusterId + " will be Terminated after 1 Day");
            } else {
                currentClusterMarkForTerminationStep.setWorkflowStatus(WorkflowStatus.IN_PROGRESS);
            }
        } else {
            currentClusterMarkForTerminationStep.setWorkflowStatus(WorkflowStatus.NEW);
        }
        
        return rotateAMIHAWorkflow;
    }

    private Workflow getNonHighAvailabilityWorkflow(ClusterVO clusterRequest) {
        logger.info("Starting getNonHighAvailabilityWorkflow for AMI Rotation. clusterId:" + clusterRequest.getClusterId());
        Workflow rotateAMINonHAWorkflow = workflowDao.getWorkflow(WorkflowConstants.ROTATE_AMI_NON_HA);

        //Terminate Current Cluster Workflow
        WorkflowStep terminateCurrentClusterStep = rotateAMINonHAWorkflow.getStep(WorkflowType.TERMINATE_CURRENT_CLUSTER);
        String currentClusterId = clusterRequest.getClusterId();
        ClusterVO currentCluster = clusterMetadataDao.getClusterMetadataByClusterId(currentClusterId);

        switch (currentCluster.getStatus()) {
            case TERMINATION_INITIATED:
                terminateCurrentClusterStep.setWorkflowStatus(WorkflowStatus.IN_PROGRESS);
                break;
            case TERMINATING:
                terminateCurrentClusterStep.setWorkflowStatus(WorkflowStatus.IN_PROGRESS);
                break;
            case TERMINATED:
                terminateCurrentClusterStep.setWorkflowStatus(WorkflowStatus.SUCCESS);
                break;
            default:
                terminateCurrentClusterStep.setWorkflowStatus(WorkflowStatus.NEW);
                break;
        }

        logger.info("getNonHighAvailabilityWorkflow setting terminate step status:" + terminateCurrentClusterStep.getWorkflowStatus());

        //Fetching record id for newly created cluster
        ClusterVO newCluster = clusterMetadataDao.getClusterMetadataByOriginalClusterId(currentClusterId);

        //Calling create cluster workflow for adding create steps
        logger.info("getNonHighAvailabilityWorkflow adding create cluster steps:" + terminateCurrentClusterStep.getWorkflowStatus());

        List<WorkflowStep> createClusterSteps = getCreateClusterWorkflow(newCluster == null ? 0 : newCluster.getMetadataId()).getWorkflowSteps();
        rotateAMINonHAWorkflow.mergeSteps(createClusterSteps);

        return rotateAMINonHAWorkflow;
    }
}
