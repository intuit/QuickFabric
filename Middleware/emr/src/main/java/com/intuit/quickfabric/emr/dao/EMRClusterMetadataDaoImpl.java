package com.intuit.quickfabric.emr.dao;

import com.intuit.quickfabric.commons.exceptions.QuickFabricSQLException;
import com.intuit.quickfabric.commons.vo.AwsAccountProfile;
import com.intuit.quickfabric.commons.vo.ClusterRequest;
import com.intuit.quickfabric.commons.vo.ClusterVO;
import com.intuit.quickfabric.commons.vo.SegmentVO;
import com.intuit.quickfabric.emr.mapper.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
public class EMRClusterMetadataDaoImpl implements EMRClusterMetadataDao {
    private static final Logger logger = LogManager.getLogger(EMRClusterMetadataDaoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplateObject;

    @Autowired
    NamedParameterJdbcTemplate namedJdbcTemplateObject;

    @Override
    public List<ClusterVO> getAllEMRClusterMetadata(String clusterName, String clusterType, String account) {
        logger.info("getting cluster metadata for cluster name: {}, cluster type: {}, and account: {}",
                clusterName, clusterType, account);

        String metadataQuery = "SELECT  " +
                "   metadata_id, " +
                "   cluster_id, " +
                "   cluster_name, " +
                "   status, " +
                "   created_by, " +
                "   account, " +
                "   creation_request_timestamp, " +
                "   dns_name, " +
                "   segment, " +
                "   type, " +
                "   request_ticket, " +
                "   TIMESTAMPDIFF(DAY, NOW(), DATE_ADD(creation_request_timestamp, " +
                "   INTERVAL ami_rotation_sla_days DAY)) as ami_rotation_days_togo,  " +
                "   ami_rotation_sla_days, " +
                "   auto_ami_rotation, " +
                "   autopilot_window_start, " +
                "   autopilot_window_end, " +
                "   do_terminate " +
                "   FROM " +
                "   emr_cluster_metadata ";

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if (!StringUtils.isBlank(clusterName)) {
            metadataQuery = metadataQuery + " WHERE cluster_name = :cluster_name";
            parameters.addValue("cluster_name", clusterName);
        } else if (!StringUtils.isBlank(clusterType) && !clusterType.equalsIgnoreCase("all")) {
            // all cluster names will have the type in it, e.g. scheduled-qboa-prd2
            metadataQuery = metadataQuery + " WHERE cluster_name LIKE :cluster_type";
            parameters.addValue("cluster_type", "%" + clusterType + "%");
        } else if (!StringUtils.isBlank(account)) {
            metadataQuery = metadataQuery + " WHERE account = :account";
            parameters.addValue("account", account);
        }

        String finalQuery = metadataQuery + " order by creation_request_timestamp desc";

        logger.info("SQL::" + finalQuery);
        List<ClusterVO> list = namedJdbcTemplateObject.query(finalQuery, parameters, new EMRClusterMetadataMapper());
        return list;
    }


    public ClusterVO getAllEMRClusterDataForAMI(String clusterId) {
        logger.info("Retrieving cluster metadata for rotate AMI for clusterId: {}", clusterId);

        String sql = "SELECT account, cluster_details, cluster_id, cluster_name, created_by, "
                + "creation_request_timestamp AS createdTS, do_terminate, headless_users, "
                + "last_updated_by, message, metadata_id, segment, status, "
                + "status_code, type, auto_ami_rotation, autopilot_window_start, autopilot_window_end, "
                + "ami_rotation_sla_days, autoscaling_instance_group, autoscaling_min, autoscaling_max "
                + "FROM emr_cluster_metadata "
                + "WHERE cluster_id = :cluster_id OR cluster_name = :cluster_id";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("cluster_id", clusterId);

        List<ClusterVO> list = namedJdbcTemplateObject.query(sql, parameters, new AMIRotationClusterMetadataMapper());
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public ClusterVO getEMRClusterMetadataByMetadataId(long metadataId) {
        logger.info("Getting Cluster Metadata for metadataId: {}", metadataId);

        String sql = "SELECT * FROM emr_cluster_metadata WHERE metadata_id = :metadata_id ";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("metadata_id", metadataId);

        List<ClusterVO> clusters = namedJdbcTemplateObject.query(sql, parameters, new EMRClusterMetadataRowMapper());

        return clusters.size() > 0 ? clusters.get(0) : null;
    }

    @Override
    public ClusterVO getClusterMetadataByClusterId(String clusterId) {
        logger.info("Getting Cluster Metadata for clusterId {}", clusterId);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("cluster_id", clusterId);
        String sql = "select * from emr_cluster_metadata where cluster_id=:cluster_id OR cluster_name=:cluster_id ";
        List<ClusterVO> clusters = namedJdbcTemplateObject.query(sql, parameters, new EMRClusterMetadataRowMapper());
        return clusters.size() > 0 ? clusters.get(0) : null;
    }

    @Override
    public ClusterVO getClusterMetadataByOriginalClusterId(String originalClusterId) {
        logger.info("Getting Cluster Metadata for originalClusterId: {}", originalClusterId);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("originalClusterId", originalClusterId);
        String sql = "select * from emr_cluster_metadata "
                + "where original_cluster_id=:originalClusterId "
                + "AND status IN ('STARTING','BOOTSTRAPPING','RUNNING','WAITING') ";

        logger.info("SQL:: {}", sql);

        List<ClusterVO> clusters = namedJdbcTemplateObject.query(sql, parameters, new EMRClusterMetadataRowMapper());
        return clusters.size() > 0 ? clusters.get(0) : null;
    }

    @Override
    @Transactional
    public void updateNewClusterByOriginalClusterId(String originalClusterId, String newClusterId) {
        logger.info("Updating New Cluster Metadata for originalClusterId: {} and new cluster {}",
                originalClusterId, newClusterId);

        String updateNewClusterintoOriginalsql = "Update emr_cluster_metadata "
                + "set new_cluster_id =? "
                + "where cluster_id=? ";
        logger.info("SQL:: " + updateNewClusterintoOriginalsql);

        try {
            jdbcTemplateObject.update(updateNewClusterintoOriginalsql,
                    newClusterId,
                    originalClusterId);
            logger.info("Updated New Cluster to: " + newClusterId + " for Cluster ID: " + originalClusterId);
        } catch (Exception e) {
            logger.error("DB error during clusterID update in metadata", e.getMessage());
            throw new QuickFabricSQLException("DB error during clusterID update in metadata", e);
        }

    }

    @Override
    public List<SegmentVO> getSegment(String segmentName) {
        logger.info("Getting segment form DB. segment name:" + segmentName);

        String sql = "SELECT * FROM segments WHERE segment_name = :segment_name";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("segment_name", segmentName);

        return namedJdbcTemplateObject.query(sql, params, new SegmentsMapper());
    }

    @Override
    public List<SegmentVO> getAllSegments() {
        logger.info("Getting all segments form DB.");

        String sql = "SELECT * FROM segments ";
        return namedJdbcTemplateObject.query(sql, new SegmentsMapper());
    }

    @Override
    public List<AwsAccountProfile> getAWSAccountProfile(String accountId) {
        logger.info("Retrieving all AWS accountId:" + accountId);

        String sql = "SELECT * FROM aws_account_profile WHERE account_id = :account_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("account_id", accountId);

        return namedJdbcTemplateObject.query(sql, params, new AWSAccountProfileMapper());
    }

    @Override
    public List<AwsAccountProfile> getAllAWSAccountProfiles() {
        logger.info("Retrieving all AWS accounts");

        String sql = "SELECT * FROM aws_account_profile";
        return namedJdbcTemplateObject.query(sql, new AWSAccountProfileMapper());
    }

    public List<String> getUserRoles() {
        logger.info("Reriving all EMR actions...");

        String sql = "SELECT " +
                "    role_name " +
                "FROM" +
                "    roles" +
                "        JOIN" +
                "    services ON roles.service_id = services.service_id" +
                "        AND services.service_type = 'EMR'" +
                "        AND roles.role_name NOT LIKE '%admin%'";

        logger.info("SQL:: {}", sql);

        return this.namedJdbcTemplateObject.query(sql, (ResultSet rs) -> {
            List<String> actions = new ArrayList<>();
            while (rs.next()) {
                actions.add(rs.getString("role_name"));
            }
            return actions;
        });
    }


    @Override
    @Transactional
    public void updateAutopilotConfig(ClusterRequest clusterDetails) {
        try {
            logger.info("Updating Auto AMI Rotation config for ClusterId: {}, cluster name: {}",
                    clusterDetails.getClusterId(),
                    clusterDetails.getClusterName());

            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("cluster_id", clusterDetails.getClusterId());

            if (clusterDetails.getAutoAmiRotation() != null) {
                String updateAutopilotEnableSQL = "UPDATE emr_cluster_metadata "
                        + "SET auto_ami_rotation = :auto_ami_rotation "
                        + "WHERE cluster_id = :cluster_id ";

                params.addValue("auto_ami_rotation", clusterDetails.getAutoAmiRotation());
                namedJdbcTemplateObject.update(updateAutopilotEnableSQL, params);

                logger.info("Updated auto AMI rotation enable for cluster {} to {}",
                        clusterDetails.getClusterId(), clusterDetails.getAutoAmiRotation());
            }

            if (clusterDetails.getAutopilotWindowStart() != null &&
                    clusterDetails.getAutopilotWindowEnd() != null) {
                String updateAutopilotWindowSQL = "UPDATE emr_cluster_metadata "
                        + "SET autopilot_window_start = :autopilot_window_start, "
                        + "autopilot_window_end = :autopilot_window_end "
                        + "WHERE cluster_id = :cluster_id";

                params.addValue("autopilot_window_start", clusterDetails.getAutopilotWindowStart());
                params.addValue("autopilot_window_end", clusterDetails.getAutopilotWindowEnd());
                namedJdbcTemplateObject.update(updateAutopilotWindowSQL, params);

                logger.info("Updated auto AMI rotation window for cluster {} to {}:00-{}:00",
                        clusterDetails.getClusterId(), clusterDetails.getAutopilotWindowStart(),
                        clusterDetails.getAutopilotWindowEnd());
            }

            if (clusterDetails.getAmiRotationSlaDays() != 0) {
                String updateAmiSlaSQL = "UPDATE emr_cluster_metadata "
                        + "SET ami_rotation_sla_days = :ami_rotation_sla_days "
                        + "WHERE cluster_id = :cluster_id ";

                params.addValue("ami_rotation_sla_days", clusterDetails.getAmiRotationSlaDays());
                namedJdbcTemplateObject.update(updateAmiSlaSQL, params);

                logger.info("Updated AMI rotation SLA for cluster {} to {}",
                        clusterDetails.getClusterId(), clusterDetails.getAmiRotationSlaDays());
            }

        } catch (Exception e) {
            logger.error("Error updating autopilot config for cluster {}. Error: {}",
                    clusterDetails.getClusterId(), e.getMessage());
            throw new QuickFabricSQLException("Error updating autopilot config", e);
        }
    }


    @Override
    @Transactional
    public void updateDoTerminateConfig(ClusterRequest clusterDetails) {
        try {
            logger.info("Updating Do terminate config for ClusterId: {}, cluster name: {}",
                    clusterDetails.getClusterId(),
                    clusterDetails.getClusterName());

            String updateAutopilotSQL = "UPDATE emr_cluster_metadata "
                    + "SET do_terminate = :do_terminate "
                    + "WHERE cluster_id = :cluster_id ";
            logger.info("SQL:: " + updateAutopilotSQL);

            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("do_terminate", clusterDetails.getDoTerminate());
            params.addValue("cluster_id", clusterDetails.getClusterId());

            namedJdbcTemplateObject.update(updateAutopilotSQL, params);
            logger.info("Updated Do terminate config for cluster ID {}: Name {} "
                            + "doTerminate == {}",
                    clusterDetails.getClusterId(), clusterDetails.getClusterName(), clusterDetails.getDoTerminate());
        } catch (Exception e) {
            logger.error("Error updating Do terminate config for cluster {}. Error: {}",
                    clusterDetails.getClusterId(), e.getMessage());
            throw new QuickFabricSQLException("Error updating Do terminate config.", e);
        }
    }

    @Override
    public void saveNewClusterDetails(ClusterVO clusterDetails) {
        String insertQuery = "INSERT INTO emr_cluster_metadata"
                + " (cluster_name,cluster_id,type,status,message,api_request_id,lambda_request_id,status_code,do_terminate,cluster_details,"
                + "creation_request_timestamp,created_by,last_updated_by,account,headless_users,segment,"
                + "dns_name,dns_flip,is_prod,original_cluster_id,request_ticket,auto_ami_rotation,"
                + "autopilot_window_start,autopilot_window_end, ami_rotation_sla_days,"
                + "autoscaling_instance_group,autoscaling_min,autoscaling_max) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        String requestTicket = !StringUtils.isBlank(clusterDetails.getJiraTicket())
                ? clusterDetails.getJiraTicket()
                : clusterDetails.getSnowTicket();

        try {

            jdbcTemplateObject.update(insertQuery,
                    clusterDetails.getClusterName(),
                    clusterDetails.getClusterId(),
                    clusterDetails.getType().toString(),
                    clusterDetails.getClusterStatus().toString(),
                    clusterDetails.getMessage(),
                    clusterDetails.getApiRequestId(),
                    clusterDetails.getLambdaRequestId(),
                    clusterDetails.getStatusCode(),
                    clusterDetails.getDoTerminate(),
                    clusterDetails.getClusterDetails(),
                    new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()),
                    clusterDetails.getCreatedBy(),
                    clusterDetails.getLastUpdatedBy(),
                    clusterDetails.getAccount(),
                    clusterDetails.getHeadlessUsers(),
                    clusterDetails.getSegment(),
                    clusterDetails.getDnsName(),
                    clusterDetails.getDnsFlip(),
                    clusterDetails.getIsProd(),
                    clusterDetails.getOriginalClusterId(),
                    requestTicket,
                    clusterDetails.getAutoAmiRotation(),
                    clusterDetails.getAutopilotWindowStart(),
                    clusterDetails.getAutopilotWindowEnd(),
                    clusterDetails.getAmiRotationSlaDays(),
                    clusterDetails.getInstanceGroup(),
                    clusterDetails.getMin(),
                    clusterDetails.getMax());


        } catch (Exception e) {
            logger.error("error happened while saving cluster info: {}", e.getMessage());
            logger.error("Insertion to metadata failed for new cluster {}",
                    clusterDetails.getClusterName());

            throw new QuickFabricSQLException("DB error during cluster metadata insert", e);
        }
    }

    @Override
    @Transactional
    public void markClusterforTermination(String clusterId) {
        String updateClusterQuery = "UPDATE emr_cluster_metadata SET do_terminate = TRUE,"
                + " last_updated_timestamp=? WHERE cluster_id=? ";
        logger.info("SQL:: " + updateClusterQuery);

        try {
            jdbcTemplateObject.update(updateClusterQuery,
                    new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()),
                    clusterId);
            logger.info("Marked Cluster with ID: " + clusterId + " for termination.");
        } catch (Exception e) {
            throw new QuickFabricSQLException("DB error during metadata update for marking termination", e);
        }
    }


    @Override
    @Transactional
    public void updateClusterDNSinDB(String clusterName, String dnsName) {
        //Updating new dns name and marking dns_flip to false so that this cluster won't be picked up in next run
        String updateDNSQuery = "UPDATE emr_cluster_metadata SET dns_name=?,last_updated_timestamp=?,"
                + "dns_flip = FALSE ,dns_flip_completed = TRUE "
                + "WHERE cluster_name=? AND status IN ('WAITING','RUNNING') ";
        logger.info("SQL:: " + updateDNSQuery);

        try {
            jdbcTemplateObject.update(updateDNSQuery,
                    dnsName,
                    new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()),
                    clusterName);
            logger.info("Updated DNS to: " + dnsName + " for Cluster: " + clusterName);
        } catch (Exception e) {
            throw new QuickFabricSQLException("DB error during cluster metadata update for DNS", e);
        }
    }


    @Override
    @Transactional
    public void updateClusterStatusByClusterId(ClusterVO clusterDetails) {
        logger.info("executing updateClusterStatusByClusterId nethod");
        String updateQuery = "UPDATE  emr_cluster_metadata SET status_code=? ,message=?, status=? ,api_request_id=?,lambda_request_id=?,last_updated_timestamp=?,last_updated_by=? where cluster_id=?";
        try {
            jdbcTemplateObject.update(updateQuery,
                    clusterDetails.getStatusCode(),
                    clusterDetails.getMessage(),
                    (clusterDetails.getClusterStatus() == null ? null : clusterDetails.getClusterStatus().toString()),
                    clusterDetails.getApiRequestId(),
                    clusterDetails.getLambdaRequestId(),
                    new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()),
                    clusterDetails.getLastUpdatedBy(),
                    clusterDetails.getClusterId());
        } catch (Exception e) {
            logger.error("DB error during cluster metadata update with error {}", e.getMessage());
            throw new QuickFabricSQLException("DB error during cluster metadata update", e);
        }

    }

}
