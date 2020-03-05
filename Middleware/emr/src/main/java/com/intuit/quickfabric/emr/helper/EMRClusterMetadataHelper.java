package com.intuit.quickfabric.emr.helper;

import com.intuit.quickfabric.commons.exceptions.QuickFabricClientException;
import com.intuit.quickfabric.commons.exceptions.QuickFabricJsonException;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.emr.dao.EMRClusterMetadataDao;
import com.intuit.quickfabric.emr.dao.EMRClusterStepsDao;
import com.intuit.quickfabric.emr.model.EMRClusterMetadataModel;
import com.intuit.quickfabric.emr.model.UIPopulationModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.SQLException;
import java.util.List;

@Component
public class EMRClusterMetadataHelper {

    private final Logger logger = LogManager.getLogger(EMRClusterMetadataHelper.class);
    @Autowired
    EMRClusterMetadataDao emrClusterMetadataDao;
    @Autowired
    EMRClusterStepsDao emrClusterStepsDao;

    public EMRClusterMetadataModel getEMRClusterMetadataList(String clusterName, String clusterType, String account)
            throws SQLException {

        EMRClusterMetadataModel model = new EMRClusterMetadataModel();
        model.setEmrClusterMetadataReport(emrClusterMetadataDao.getAllEMRClusterMetadata(clusterName, clusterType, account));
        return model;
    }

    public ClusterVO getAllEMRClusterDetailsByClusterID(String clusterId) {
        return emrClusterMetadataDao.getAllEMRClusterDataForAMI(clusterId);
    }

    public List<SegmentVO> getSegment(String segmentName) {
        List<SegmentVO> segments;
        if (StringUtils.isNotBlank(segmentName) && !segmentName.equalsIgnoreCase("all")) {
            segments = emrClusterMetadataDao.getSegment(segmentName);
        } else {
            segments = getAllSegments();
        }
        return segments;
    }

    public List<SegmentVO> getAllSegments() {
        return emrClusterMetadataDao.getAllSegments();
    }

    public List<AwsAccountProfile> getAllAWSAccountProfiles() {
        return emrClusterMetadataDao.getAllAWSAccountProfiles();
    }

    public List<AwsAccountProfile> getAWSAccountProfile(String accountId) {
        List<AwsAccountProfile> awsAccountProfile;
        if (StringUtils.isNotBlank(accountId) && !accountId.equalsIgnoreCase("all")) {
            awsAccountProfile = emrClusterMetadataDao.getAWSAccountProfile(accountId);
        } else {
            awsAccountProfile = getAllAWSAccountProfiles();
        }
        return awsAccountProfile;
    }

    public ClusterVO getSingleClusterDetailsByClusterID(String clusterId) {
        return emrClusterMetadataDao.getClusterMetadataByClusterId(clusterId);
    }

    public ClusterVO getSingleClusterDetailsByClusterID(String clusterId, String requestFrom) {
        logger.info("EMRClusterMetaDataService API-> getSingleEMRClusterMetaData");
        ClusterVO clusterDetails = emrClusterMetadataDao.getClusterMetadataByClusterId(clusterId);
        ClusterVO clusterResponse;

        //Checking if the API request is from workflow or not
        if (StringUtils.isNotBlank(requestFrom) && requestFrom.equalsIgnoreCase("workflow")) {
            logger.info("Fetching metadata for new cluster as it has been called from workflow");
            if (StringUtils.isBlank(clusterDetails.getNewClusterId())) {
                throw new QuickFabricClientException("No New Cluster associated with this currentClusterId: " + clusterId);
            }
            clusterResponse = getSingleClusterDetailsByClusterID(clusterDetails.getNewClusterId());

        } else {
            logger.info("Fetching Metadata for current clusterId:{}", clusterId);
            clusterResponse = getSingleClusterDetailsByClusterID(clusterId);
        }
        return clusterResponse;
    }

    public ClusterVO getEMRClusterMetadataByMetadataId(long metadataId) {
        return emrClusterMetadataDao.getEMRClusterMetadataByMetadataId(metadataId);
    }

    public ClusterVO getClusterMetadataByOriginalClusterId(String clusterId) {
        return emrClusterMetadataDao.getClusterMetadataByOriginalClusterId(clusterId);
    }

    public ClusterMessageResponseVO updateAutopilotConfig(@RequestBody ClusterRequest clusterDetails) {
        emrClusterMetadataDao.updateAutopilotConfig(clusterDetails);
        ClusterMessageResponseVO response = new ClusterMessageResponseVO();
        String message = "Successfully updated Autopilot configs for cluster ID " + clusterDetails.getClusterId()
                + ": enabled == " + clusterDetails.getAutoAmiRotation() + ","
                + " rotation window start == " + clusterDetails.getAutopilotWindowStart() +
                ", rotation window end == " + clusterDetails.getAutopilotWindowEnd() +
                ", AMI rotation SLA == " + clusterDetails.getAmiRotationSlaDays();

        response.setMessage(message);
        response.setClusterId(clusterDetails.getClusterId());
        response.setClusterName(clusterDetails.getClusterName());
        return response;
    }

    public ClusterMessageResponseVO updateDoTerminateConfig(@RequestBody ClusterRequest clusterDetails) {
        emrClusterMetadataDao.updateDoTerminateConfig(clusterDetails);
        ClusterMessageResponseVO response = new ClusterMessageResponseVO();
        String message = "Updated Do Terminate config for cluster ID: " + clusterDetails.getClusterId() + " Name: " + clusterDetails.getClusterName()
                + " set to: " + clusterDetails.getDoTerminate();

        response.setMessage(message);
        response.setClusterId(clusterDetails.getClusterId());
        response.setClusterName(clusterDetails.getClusterName());
        return response;
    }

    public List<ClusterStep> getStepsByStepIds(List<String> stepIds) {
        return this.emrClusterStepsDao.getStepsByStepIds(stepIds);
    }

    public void mapClusterDetailsToClusterRequestFields(ClusterVO clusterVo, ClusterRequest clusterRequest) {
        JSONObject clusterHardwareDetailsJson;
        try {
            clusterHardwareDetailsJson = new JSONObject(clusterVo.getClusterDetails());
            clusterRequest.setCoreEbsVolSize(clusterHardwareDetailsJson.getString("Core_ebs_vol_size"));
            clusterRequest.setCoreInstanceCount(clusterHardwareDetailsJson.getString("Core_instance_count"));
            clusterRequest.setCoreInstanceType(clusterHardwareDetailsJson.getString("Core_instance_type"));
            clusterRequest.setMasterEbsVolSize(clusterHardwareDetailsJson.getString("Master_ebs_vol_size"));
            clusterRequest.setTaskEbsVolSize(clusterHardwareDetailsJson.getString("Task_ebs_vol_size"));
            clusterRequest.setTaskInstanceCount(clusterHardwareDetailsJson.getString("Task_instance_count"));
            clusterRequest.setTaskInstanceType(clusterHardwareDetailsJson.getString("Task_instance_type"));
            clusterRequest.setMasterInstanceType(clusterHardwareDetailsJson.getString("Master_instance_type"));
        } catch (JSONException e) {
            logger.error("json error during map cluster details to cluster request fields ", e.getMessage());
            throw new QuickFabricJsonException("json error during mapping ", e);
        }
    }

    public ClusterVO getClusterClone(String clusterId) {
        logger.info("EMRClusterMetadataHelper->getClusterClone, args: clusterId:{}", clusterId);

        ClusterVO vo = this.emrClusterMetadataDao.getClusterMetadataByClusterId(clusterId);
        vo.setBootstrapActions(emrClusterStepsDao.getBootstrapActionsByClusterId(clusterId));
        vo.setSubType("non-kerberos");

        //this is silly but also awfully convenient
        ClusterRequest temp = new ClusterRequest();
        BeanUtils.copyProperties(vo, temp);
        this.mapClusterDetailsToClusterRequestFields(vo, temp);
        BeanUtils.copyProperties(temp, vo);

        return vo;
    }

    public void saveNewClusterDetailsToRDS(ClusterVO clusterDetails) {
        emrClusterMetadataDao.saveNewClusterDetails(clusterDetails);
    }

    public UIPopulationModel getLookupData() {
        UIPopulationModel model = new UIPopulationModel();
        model.setAccounts(getAllAWSAccountProfiles());
        model.setSegments(getAllSegments());
        model.setActions(emrClusterMetadataDao.getUserRoles());
        return model;
    }
}