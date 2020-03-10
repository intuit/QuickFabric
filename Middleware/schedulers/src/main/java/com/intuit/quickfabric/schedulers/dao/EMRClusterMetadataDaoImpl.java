package com.intuit.quickfabric.schedulers.dao;

import com.intuit.quickfabric.commons.domain.UserAccess;
import com.intuit.quickfabric.commons.exceptions.QuickFabricSQLException;
import com.intuit.quickfabric.commons.vo.*;
import com.intuit.quickfabric.schedulers.mappers.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class EMRClusterMetadataDaoImpl implements EMRClusterMetadataDao {
    private static final Logger logger = LogManager.getLogger(EMRClusterMetadataDaoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplateObject;

    @Autowired
    NamedParameterJdbcTemplate namedJdbcTemplateObject;

    @Autowired
    private PlatformTransactionManager transactionManager;


    public ClusterVO getAllEMRClusterDataForAMI(String clusterId)  {
        String sql = "SELECT account, cluster_details, cluster_id, cluster_name, created_by, "
                + "creation_request_timestamp AS createdTS, do_terminate, headless_users, "
                + "last_updated_by, message, metadata_id, segment, status, "
                + "status_code, type, auto_ami_rotation, is_prod, autopilot_window_start, "
                + "autopilot_window_end, autoscaling_instance_group, autoscaling_min, autoscaling_max "
                + "FROM emr_cluster_metadata "
                + "WHERE cluster_id = :cluster_id OR cluster_name = :cluster_id";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("cluster_id", clusterId);

        List<ClusterVO> list = namedJdbcTemplateObject.query(sql, parameters, new AMIRotationClusterMetadataMapper());
        return list.get(0);
    }

    @Override
    public ClusterVO getClusterMetadata(String clusterId) {
        logger.info("Getting Cluster Metadata for clusterId:" + clusterId);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("cluster_id", clusterId);
        String sql = "select * from emr_cluster_metadata where cluster_id=:cluster_id ";

        List<ClusterVO> clusters = namedJdbcTemplateObject.query(sql, parameters, new EMRClusterMetadataRowMapper());
        return clusters.size() > 0 ? clusters.get(0) : null;
    }

    @Override
    public ClusterVO getClusterMetadataByOriginalClusterId(String originalClusterId) {
        logger.info("Getting Cluster Metadata for originalClusterId:" + originalClusterId);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("originalClusterId", originalClusterId);
        String sql = "select * from emr_cluster_metadata "
                + "where original_cluster_id=:originalClusterId "
                + "AND status IN ('CREATED','IN_PROGRESS','SUCCEEDED','RUNNING','WAITING') ";
        List<ClusterVO> clusters = namedJdbcTemplateObject.query(sql, parameters, new EMRClusterMetadataRowMapper());
        return clusters.size() > 0 ? clusters.get(0) : null;
    }

    @Override
    @Transactional
    public void updateNewClusterByOriginalClusterId(String originalClusterId,String newClusterId) {
        logger.info("Updating New Cluster Metadata for originalClusterId:" + originalClusterId);

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
            throw new QuickFabricSQLException("DB error during metadata update of orginal Cluster Id",e);
            
        }

    }

    @Override
    public ClusterVO getEMRMetadataForAddQBUser(String emrName) {
        logger.info("Getting metadata for Add User to EMR...");

        String sql = "SELECT " +
                "    cluster_id, cluster_name, account " +
                "FROM" +
                "    emr_cluster_metadata " +
                "WHERE" +
                "    cluster_name = :cluster_name " +
                "        AND status IN ('WAITING' , 'RUNNING')";

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("cluster_name", emrName);

        return namedJdbcTemplateObject.query(sql, params, new AddUserRequestMapper());
    }

    @Override
    public boolean cleanUpTerminatedClusters(int daysAgo) {
        String deleteFromSteps = "DELETE steps FROM cluster_step_request steps " +
                "        JOIN " +
                "    emr_cluster_metadata metadata ON steps.cluster_id = metadata.cluster_id " +
                "        AND metadata.status = 'TERMINATED' " +
                "        AND metadata.last_updated_timestamp < DATE_ADD(CURRENT_DATE(), " +
                "        INTERVAL " + (-daysAgo) + " DAY)";

        String deleteFromMetrics = "DELETE metrics FROM cluster_metrics_history metrics " +
                "        JOIN " +
                "    emr_cluster_metadata metadata ON metrics.emr_id = metadata.cluster_id " +
                "        AND metadata.status = 'TERMINATED' " +
                "        AND metadata.last_updated_timestamp < DATE_ADD(CURRENT_DATE(), " +
                "        INTERVAL " + (-daysAgo) + " DAY)";

        String deleteFromTestsHistory = "DELETE tests FROM emr_functional_testsuites_status_history tests " +
                "        JOIN " +
                "    emr_cluster_metadata metadata  " +
                "ON " +
                "    tests.cluster_id = metadata.cluster_id AND " +
                "    metadata.status = 'TERMINATED' " +
                "    AND metadata.last_updated_timestamp < DATE_ADD(CURRENT_DATE(), " +
                "    INTERVAL " + (-daysAgo) + " DAY)";

        String deleteFromTests = "DELETE tests FROM emr_functional_testsuites_status tests " +
                "        JOIN " +
                "    emr_cluster_metadata metadata  " +
                "ON " +
                "    tests.cluster_id = metadata.cluster_id AND " +
                "    metadata.status = 'TERMINATED' " +
                "    AND metadata.last_updated_timestamp < DATE_ADD(CURRENT_DATE(), " +
                "    INTERVAL " + (-daysAgo) + " DAY)";

        String deleteFromMetadata = "DELETE FROM emr_cluster_metadata  " +
                "WHERE " +
                "    status = 'TERMINATED' " +
                "    AND last_updated_timestamp < DATE_ADD(CURRENT_DATE(), " +
                "    INTERVAL " + (-daysAgo) + " DAY)";

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        int rowsAffected = -1;
        try {
            logger.info("Starting delete from steps query");
            logger.info("SQL::" + deleteFromSteps);
            rowsAffected = jdbcTemplateObject.update(deleteFromSteps);
            logger.info("Delete from steps successful, " + rowsAffected + " rows deleted.");

            logger.info("Starting delete from metrics query");
            logger.info("SQL::" + deleteFromMetrics);
            rowsAffected = jdbcTemplateObject.update(deleteFromMetrics);
            logger.info("Delete from metrics successful, " + rowsAffected + " rows deleted.");

            logger.info("Starting delete from tests history query");
            logger.info("SQL::" + deleteFromTestsHistory);
            rowsAffected = jdbcTemplateObject.update(deleteFromTestsHistory);
            logger.info("Delete from tests history successful, " + rowsAffected + " rows deleted.");

            logger.info("Starting delete from tests history query");
            logger.info("SQL::" + deleteFromTests);
            rowsAffected = jdbcTemplateObject.update(deleteFromTests);
            logger.info("Delete from tests history successful, " + rowsAffected + " rows deleted.");

            logger.info("Starting delete from metadata query");
            logger.info("SQL::" + deleteFromMetadata);
            rowsAffected = jdbcTemplateObject.update(deleteFromMetadata);
            logger.info("Delete from metadata successful, " + rowsAffected + " rows deleted.");
        } catch (Exception e) {
            logger.error("Transaction failed, rolling back all previous updates. Reason: " + e.getMessage());
            transactionManager.rollback(txStatus);
            return false;
        }

        logger.info("All updates succeeded. Committing...");
        transactionManager.commit(txStatus);
        return true;
    }

    @Override
    public List<SegmentVO> getSegment(String segmentName) {
        String baseQuery = "SELECT * FROM segments";
        MapSqlParameterSource params = new MapSqlParameterSource();

        String sql = baseQuery;
        if (!(StringUtils.isBlank(segmentName) || segmentName.equalsIgnoreCase("all"))) {
            String whereClause = " WHERE segment_name = :segment_name";
            sql = baseQuery + whereClause;
            params.addValue("segment_name", segmentName);
        }

        logger.info("SQL:: {}", sql);

        return namedJdbcTemplateObject.query(sql, params, rs -> {
            List<SegmentVO> segments = new ArrayList<>();
            while (rs.next()) {
                SegmentVO segment = new SegmentVO();
                segment.setSegmentId(rs.getInt("segment_id"));
                segment.setSegmentName(rs.getString("segment_name"));
                segment.setBusinessOwner(rs.getString("business_owner"));
                segment.setBusinessOwnerEmail(rs.getString("business_owner_email"));
                segments.add(segment);
            }
            return segments;
        });
    }

    @Override
    public List<UserVO> getSubscribers() {
        logger.info("Retrieving all users who have subscribed to a report");

        String sql = "SELECT DISTINCT u.user_id, email_id FROM user u JOIN report_subscriptions s "
                + "ON u.user_id = s.user_id ";

        logger.info("SQL:: {}", sql);

        return this.namedJdbcTemplateObject.query(sql, (ResultSet rs) -> {
            List<UserVO> users = new ArrayList<>();

            while (rs.next()) {
                UserVO vo = new UserVO();
                vo.setUserId(rs.getLong("user_id"));
                vo.setUserEmail(rs.getString("email_id"));
                users.add(vo);
            }

            return users;
        });


    }

    @Override
    public List<SubscriptionVO> getSubscriptionsForUser(long userId) {
        logger.info("Retrieving names of all reports user with ID {} has subscribed to", userId);

        String sql = "SELECT  " +
                "    report_name " +
                "FROM " +
                "    report_subscriptions s " +
                "        JOIN " +
                "    reports r ON r.report_id = s.report_id " +
                "    AND s.user_id = :user_id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);

        logger.info("SQL:: {}", sql);

        return this.namedJdbcTemplateObject.query(sql, params, (ResultSet rs) -> {
            List<SubscriptionVO> subscriptions = new ArrayList<>();
            while (rs.next()) {
                SubscriptionVO vo = new SubscriptionVO();
                vo.setReportName(rs.getString("report_name"));
                subscriptions.add(vo);
            }

            return subscriptions;
        });
    }

    @Override
    public List<UserAccess> getReadAccessForUser(long userId) {
        logger.info("Retriving all account/segment pairs user with ID {} has access to...", userId);

        String sql = "SELECT  " +
                "    role_name, " +
                "    account_id, " +
                "    segment_name " +
                "FROM " +
                "    user_account_segment_mapping tbl1 " +
                "        JOIN " +
                "    user_account_segment_role_mapping tbl2 ON tbl1.user_account_segment_mapping_id = tbl2.user_account_segment_mapping_id " +
                "        AND tbl1.user_id = :user_id " +
                "        JOIN roles r ON tbl2.role_id = r.role_id AND role_name = 'Read' " +
                "        JOIN aws_account_profile a ON a.id = tbl1.aws_account_id " +
                "        JOIN segments s ON s.segment_id = tbl1.segment_id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);

        logger.info("SQL:: {}", sql);

        return this.namedJdbcTemplateObject.query(sql, params, (ResultSet rs) -> {
            List<UserAccess> readAccesses = new ArrayList<>();
            while (rs.next()) {
                UserAccess access = new UserAccess();
                access.setAwsAccountName(rs.getString("account_id"));
                access.setRoleName(rs.getString("role_name"));
                access.setSegmentName(rs.getString("segment_name"));
                readAccesses.add(access);
            }

            return readAccesses;
        });
    }

    @Override
    public List<UserVO> getUsers() {
        String sql = "SELECT user_id, email_id, first_name, last_name FROM user";

        return this.jdbcTemplateObject.query(sql, (ResultSet rs) -> {
            List<UserVO> users = new ArrayList<>();
            while (rs.next()) {
                UserVO user = new UserVO();
                user.setUserId(rs.getInt("user_id"));
                user.setUserEmail(rs.getString("email_id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));

                users.add(user);
            }

            return users;
        });

    }

    @Override
    public void saveNewClusterDetails(ClusterVO clusterDetails)  {
        String insertQuery = "INSERT INTO emr_cluster_metadata"
                + " (cluster_name,cluster_id,type,status,message,api_request_id,lambda_request_id,status_code,do_terminate,cluster_details,"
                + "creation_request_timestamp,created_by,last_updated_by,account,headless_users,segment,dns_name,dns_flip,is_prod,original_cluster_id,auto_ami_rotation,"
                + "autopilot_window_start, autopilot_window_end, ami_rotation_sla_days,"
                + "autoscaling_instance_group,autoscaling_min,autoscaling_max) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

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
                    new Timestamp(Calendar.getInstance().getTime().getTime()),
                    clusterDetails.getCreatedBy(),
                    clusterDetails.getLastUpdatedBy(),
                    clusterDetails.getAccount(),
                    clusterDetails.getHeadlessUsers(),
                    clusterDetails.getSegment(),
                    clusterDetails.getDnsName(),
                    clusterDetails.getDnsFlip(),
                    clusterDetails.getIsProd(),
                    clusterDetails.getOriginalClusterId(),
                    clusterDetails.getAutoAmiRotation(),
                    clusterDetails.getAutopilotWindowStart(),
                    clusterDetails.getAutopilotWindowEnd(),
                    clusterDetails.getAmiRotationSlaDays(),
                    clusterDetails.getInstanceGroup(),
                    clusterDetails.getMin(),
                    clusterDetails.getMax());

            transactionManager.commit(txStatus);

        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            throw e;
        }


    }

    @Override
    @Transactional
    public void updateClusterStatusByClusterId(ClusterVO clusterDetails) {
        String updateQuery = "UPDATE  emr_cluster_metadata SET status_code=? ,message=?, status=?,"
                + "api_request_id=?,lambda_request_id=?,last_updated_timestamp=?,last_updated_by=? "
                + "WHERE cluster_id=?";
        logger.info("updating query ",updateQuery);
        try {

            jdbcTemplateObject.update(updateQuery,
                    clusterDetails.getStatusCode(),
                    clusterDetails.getMessage(),
                    (clusterDetails.getClusterStatus() == null ? null : clusterDetails.getClusterStatus().toString()),
                    clusterDetails.getApiRequestId(),
                    clusterDetails.getLambdaRequestId(),
                    new Timestamp(Calendar.getInstance().getTime().getTime()),
                    clusterDetails.getLastUpdatedBy(),
                    clusterDetails.getClusterId());


        } catch (Exception e) {
            throw new QuickFabricSQLException("Error happend while updating cluster status by cluster id", e);
        }
    }

    @Override
    public void updateClusterStatusInDB(ClusterVO clusterDetails)  {
        String updateQuery = "UPDATE  emr_cluster_metadata SET status_code=? ,message=?, status=?,"
                + "cluster_id=?,api_request_id=?,lambda_request_id=?,last_updated_timestamp=?,"
                + "last_updated_by=?,master_ip=?,rm_url=? WHERE cluster_name=? and "
                + "status IN ('STARTING','BOOTSTRAPPING')";


        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);

        try {
            jdbcTemplateObject.update(updateQuery,
                    clusterDetails.getStatusCode(),
                    clusterDetails.getMessage(),
                    (clusterDetails.getClusterStatus() == null ? null : clusterDetails.getClusterStatus().toString()),
                    clusterDetails.getClusterId(),
                    clusterDetails.getApiRequestId(),
                    clusterDetails.getLambdaRequestId(),
                    new Timestamp(Calendar.getInstance().getTime().getTime()),
                    clusterDetails.getLastUpdatedBy(),
                    clusterDetails.getMasterIp(),
                    clusterDetails.getRmUrl(),
                    clusterDetails.getClusterName().trim());

            transactionManager.commit(txStatus);


        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            throw e;
        }
    }

    @Override
    public List<ClusterRequest> getClustersWithClusterStatuses(Set<ClusterStatus> statues)  {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        ArrayList<String> statuseslist = new ArrayList<String>();
        for (ClusterStatus c : statues) {
            statuseslist.add(c.toString());
        }
        parameters.addValue("statusParamName", statuseslist);

        String sql = "SELECT account, cluster_name ,cluster_id, creation_request_timestamp from emr_cluster_metadata  where status  IN (:statusParamName)";

        logger.info("SQL::{}, params: {}", sql, parameters.getValues());
        List<ClusterRequest> list = namedJdbcTemplateObject.query(sql, parameters, new EMRClusterDetailsMapper());
        return list;
    }

    @Override
    public List<ClusterVO> getClustersToValidate(Set<ClusterStatus> statues)  {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        ArrayList<String> statuseslist = new ArrayList<String>();

        for (ClusterStatus c : statues) {
            statuseslist.add(c.toString());
        }

        parameters.addValue("statusParamName", statuseslist);

        String sql = "SELECT account, cluster_name ,cluster_id, type,status,metadata_id,"
                + "cluster_details,segment,original_cluster_id,creation_request_timestamp,dns_name,"
                + "autoscaling_instance_group,autoscaling_min,autoscaling_max"
                + " from emr_cluster_metadata  where status  IN (:statusParamName)";

        logger.info("SQL::{}, params: {}", sql, parameters.getValues());
        List<ClusterVO> list = namedJdbcTemplateObject.query(sql, parameters, new EMRClusterValidationMapper());
        return list;
    }

    @Override
    public List<ClusterRequest> getClusterWithSucceededStatusAndStepsWithCompletedStatus(Set<ClusterStatus> statuses,
                                                                                         List<StepStatus> stepStatuses) {

        List<String> clusterstatusesString = new ArrayList<String>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<String> stepStatusesString = new ArrayList<String>();


        for (ClusterStatus c : statuses) {
            clusterstatusesString.add(c.toString());
        }

        for (StepStatus c : stepStatuses) {
            stepStatusesString.add(c.toString());
        }
        parameters.addValue("clusterStatusParamName", clusterstatusesString);
        parameters.addValue("stepStatusParamName", stepStatusesString);

        String sql = "";

        sql = "SELECT  t1.*  FROM emr_cluster_metadata t1" +
                " WHERE NOT EXISTS ( SELECT * FROM cluster_step_request t2 WHERE t1.cluster_name = t2.cluster_name "
                + "AND t2.status NOT IN (:stepStatusParamName)) AND t1.status IN (:clusterStatusParamName) "
                + "AND t1.do_terminate = TRUE AND t1.type = 'transient' ";

        logger.info("SQL::" + sql);
        List<ClusterRequest> list = namedJdbcTemplateObject.query(sql, parameters, new EMRClusterDetailsMapper());
        return list;
    }

    @Override
    public void markClusterforTermination(String clusterId)  {

        String updateClusterQuery = "UPDATE emr_cluster_metadata SET do_terminate = TRUE,"
                + "last_updated_timestamp=? WHERE cluster_id=? ";

        TransactionDefinition txDef = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);
        logger.info("SQL:: " + updateClusterQuery);

        try {
            jdbcTemplateObject.update(updateClusterQuery,
                    new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()),
                    clusterId);
            transactionManager.commit(txStatus);
            logger.info("Marked Cluster with ID: " + clusterId + " for termination.");
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            throw e;
        }
    }

    @Override
    public List<ClusterRequest> getCompletedNonTransientClusters(Set<ClusterStatus> statuses,
                                                                 List<StepStatus> stepStatuses) {

        List<String> clusterstatusesString = new ArrayList<String>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<String> stepStatusesString = new ArrayList<String>();
        Timestamp current_timestamp = new Timestamp(System.currentTimeMillis());

        for (ClusterStatus c : statuses) {
            clusterstatusesString.add(c.toString());
        }

        for (StepStatus c : stepStatuses) {
            stepStatusesString.add(c.toString());
        }
        parameters.addValue("clusterStatusParamName", clusterstatusesString);
        parameters.addValue("stepStatusParamName", stepStatusesString);
        parameters.addValue("current_timestamp", current_timestamp);

        String sql = "SELECT  t1.*  FROM emr_cluster_metadata t1" +
                " WHERE NOT EXISTS ( SELECT * FROM cluster_step_request t2 WHERE t1.cluster_name = t2.cluster_name "
                + "AND t2.status NOT IN (:stepStatusParamName)) AND t1.status IN (:clusterStatusParamName) "
                + "AND t1.do_terminate = TRUE AND t1.type IN ('scheduled','scheduled_nonkerb','exploratory_nonkerb') ";

        logger.info("SQL::" + sql);
        List<ClusterRequest> list = namedJdbcTemplateObject.query(sql, parameters, new EMRClusterDetailsMapper());
        return list;
    }

    @Override
    public List<ClusterRequest> getCompletedTestingClusters(Set<ClusterStatus> statuses,
                                                            List<StepStatus> stepStatuses) {

        logger.info("Fetching Testing Clusters for auto Termination");

        List<String> clusterstatusesString = new ArrayList<String>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<String> stepStatusesString = new ArrayList<String>();

        for (ClusterStatus c : statuses) {
            clusterstatusesString.add(c.toString());
        }

        for (StepStatus c : stepStatuses) {
            stepStatusesString.add(c.toString());
        }
        parameters.addValue("clusterStatusParamName", clusterstatusesString);
        parameters.addValue("stepStatusParamName", stepStatusesString);

        String sql = "SELECT  t1.*  FROM emr_cluster_metadata t1" +
                " where not exists( select * from cluster_step_request t2 where t1.cluster_name = t2.cluster_name "
                + "and t2.status not in (:stepStatusParamName)) and t1.status IN (:clusterStatusParamName) "
                + "and (t1.segment IN ('testing') OR t1.cluster_name like '%test%' ) ";
        
        List<ClusterRequest> list = namedJdbcTemplateObject.query(sql, parameters, new EMRClusterDetailsMapper());

        logger.info("Fetched Testing Clusters for auto Termination");
        return list;
    }

    @Override
    public List<ClusterVO> getAMIRotationReport(String segmentName) {
    
        String baseQuery = "SELECT  " + 
                "    cluster_id, " + 
                "    cluster_name, " + 
                "    status, " + 
                "    type, " + 
                "    creation_request_timestamp, " + 
                "    created_by, " + 
                "    account, " + 
                "    segment, " +
                "    TIMESTAMPDIFF(DAY, NOW(), DATE_ADD(creation_request_timestamp, " +
                "    INTERVAL ami_rotation_sla_days DAY)) as ami_rotation_days_togo  " +
                "FROM " +
                "    emr_cluster_metadata " +
                "WHERE " +
                "    status IN ('RUNNING' , 'WAITING')";

        String sql = baseQuery;
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (!(StringUtils.isBlank(segmentName) || segmentName.equalsIgnoreCase("all"))) {
            String segmentFilter = " AND segment = :segment_name";
            sql = baseQuery + segmentFilter;
            params.addValue("segment_name", segmentName);
        }

        logger.info("SQL:: " + baseQuery);

        List<ClusterVO> amiReport =
                namedJdbcTemplateObject.query(sql, params, new EMRClusterMetricsAMIReportMapper());

        logger.info("Picked up EMR Clusters from Database for AMI Rotation report");
        return amiReport;
    }

    @Override
    public List<ClusterRequest> getClustersforAMIRotation(Set<ClusterStatus> statuses) {

        logger.info("Fetching Clusters for auto AMI Rotation");
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        ArrayList<String> statuseslist = new ArrayList<String>();
        for (ClusterStatus c : statuses) {
            statuseslist.add(c.toString());
        }
        parameters.addValue("statusParamName", statuseslist);

        String sql = "SELECT  " +
                "    account, " +
                "    cluster_name, " +
                "    cluster_id, " +
                "    is_prod, " +
                "    autopilot_window_start, " +
                "    autopilot_window_end, " +
                "    ami_rotation_sla_days " +
                "FROM " +
                "    emr_cluster_metadata " +
                "WHERE " +
                "    auto_ami_rotation = TRUE " +
                "        AND status IN (:statusParamName) " +
                "        AND NOW() > DATE_ADD(creation_request_timestamp, " +
                "        INTERVAL ami_rotation_sla_days DAY)";

        List<ClusterRequest> list = new ArrayList<>();
        ResultSetExtractor<List<ClusterRequest>> rse = resultSet -> {
            while (resultSet.next()) {
                ClusterRequest clusterDetail = new ClusterRequest();
                clusterDetail.setAccount(resultSet.getString("account"));
                clusterDetail.setClusterName(resultSet.getString("cluster_name"));
                clusterDetail.setClusterId(resultSet.getString("cluster_id"));
                clusterDetail.setIsProd(resultSet.getBoolean("is_prod"));
                clusterDetail.setAutopilotWindowStart(resultSet.getInt("autopilot_window_start"));
                clusterDetail.setAutopilotWindowEnd(resultSet.getInt("autopilot_window_end"));
                clusterDetail.setAmiRotationSlaDays(resultSet.getInt("ami_rotation_sla_days"));
                list.add(clusterDetail);
            }
            return list;
        };
        namedJdbcTemplateObject.query(sql, parameters, rse);
        logger.info("Fetched Clusters for auto AMI Rotation");

        return list;
    }

    @Override
    public List<EMRClusterMetricsVO> getClusterMetadataForMetrics(Set<ClusterStatus> statuses) {
    	logger.info("Fetching Clusters list for Metrics Collection");

    	List<String> clusterStatusesString = new ArrayList<String>();
    	MapSqlParameterSource parameters = new MapSqlParameterSource();
    
    	for (ClusterStatus c : statuses) {
    		clusterStatusesString.add(c.toString());
    	}
    
    	parameters.addValue("clusterStatusParamName", clusterStatusesString);
    
    	String getClusterMetadataForMetricsSql = "SELECT cluster_id,cluster_name,type,status,"
    	        + "creation_request_timestamp,created_by,account,segment,rm_url"
    			+ " FROM emr_cluster_metadata"
    			+ " WHERE status IN (:clusterStatusParamName)";
    
    
    	List<EMRClusterMetricsVO> list = namedJdbcTemplateObject.query(getClusterMetadataForMetricsSql, parameters, new EMRClusterFetchMetricsMapper());
    	logger.info("completed Fetching Clusters list for Metrics Collection");

    	return list;
    }

    @Override
    @Transactional
    public void updateClusterDNSinDB(String clusterName, String dnsName) {
        //Updating new dns name and marking dns_flip to false so that this cluster won't be picked up in next run
        String updateDNSQuery = "UPDATE emr_cluster_metadata SET dns_name = :dnsName,last_updated_timestamp = :updatedTS"
                + "WHERE cluster_name = :clusterName AND status IN ('WAITING','RUNNING') ";
        logger.info("SQL:: " + updateDNSQuery);
        
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("dnsName", dnsName);
        parameters.addValue("clusterName", clusterName);
        parameters.addValue("updatedTS", new Date());

        try {
            namedJdbcTemplateObject.update(updateDNSQuery, parameters);
            logger.info("Updated DNS to: " + dnsName + " for Cluster: " + clusterName);
        } catch (Exception e) {
            throw new QuickFabricSQLException("DB error during cluster metadata update for DNS", e);
        }

    }

}
