package com.intuit.quickfabric.emr.dao;

import com.intuit.quickfabric.commons.domain.Role;
import com.intuit.quickfabric.commons.exceptions.QuickFabricSQLException;
import com.intuit.quickfabric.commons.vo.AwsAccountProfile;
import com.intuit.quickfabric.commons.vo.LoginRolesVO;
import com.intuit.quickfabric.commons.vo.SegmentVO;
import com.intuit.quickfabric.commons.vo.UserAccountSegmentMapping;
import com.intuit.quickfabric.emr.mapper.UserAccountSegmentMappingMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
public class AdminDaoImpl implements AdminDao {

    private static final Logger logger = LogManager.getLogger(AdminDaoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplateObject;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<UserAccountSegmentMapping> getUserAccountSegmentMappings(int userId) {
        logger.info("getUserAccountSegmentMappings for userId:" + userId);

        String sql = "SELECT * FROM user_account_segment_mapping where user_id = :user_id";

        MapSqlParameterSource namedParams = new MapSqlParameterSource();
        namedParams.addValue("user_id", userId);

        List<UserAccountSegmentMapping> userAccountSegmentMappings = namedParameterJdbcTemplate.query(sql, namedParams, new UserAccountSegmentMappingMapper());
        return userAccountSegmentMappings;
    }

    @Override
    public UserAccountSegmentMapping addAccountAndSegmentToUser(int userId, Integer awsAccountId, Integer segmentId) {
        logger.info("addAccountAndSegmentToUser for userId:" + userId + " awsAccountId:" + awsAccountId + " segmentId:" + segmentId);

        String sql = "insert  into user_account_segment_mapping (user_id, aws_account_id, segment_id)" +
                " values (:user_id, :aws_account_id, :segment_id)";

        MapSqlParameterSource namedParams = new MapSqlParameterSource();
        namedParams.addValue("user_id", userId);
        namedParams.addValue("aws_account_id", awsAccountId);
        namedParams.addValue("segment_id", segmentId);

        KeyHolder holder = new GeneratedKeyHolder();
        try {
            namedParameterJdbcTemplate.update(sql, namedParams, holder);
        } catch (Exception ex) {
            throw new QuickFabricSQLException("Unable to create Account Segment User mapping in DB for userId :" + userId, ex);
        }


        int mappingId = holder.getKey().intValue();
        logger.info("received mappingId:" + mappingId);

        UserAccountSegmentMapping mapping = new UserAccountSegmentMapping();
        mapping.setUserId(userId);
        mapping.setAwsAccountId(awsAccountId);
        mapping.setSegmentId(segmentId);
        mapping.setMappingId(mappingId);
        return mapping;
    }

    @Override
    public int AddRole(int userAccountSegmentMappingId, int roleId) {
        logger.info("AddRole for userAccountSegmentMappingId:" + userAccountSegmentMappingId + " roleId:" + roleId);

        String sql = "insert  into user_account_segment_role_mapping (user_account_segment_mapping_id, role_id)" +
                " values (:user_account_segment_mapping_id, :role_id)";

        MapSqlParameterSource namedParams = new MapSqlParameterSource();
        namedParams.addValue("user_account_segment_mapping_id", userAccountSegmentMappingId);
        namedParams.addValue("role_id", roleId);

        KeyHolder holder = new GeneratedKeyHolder();
        try {
            namedParameterJdbcTemplate.update(sql, namedParams, holder);
        } catch (Exception ex) {
            throw new QuickFabricSQLException("Unable to add Role in DB for user :", ex);
        }

        int roleMappingId = holder.getKey().intValue();
        logger.info("received roleMappingId:" + userAccountSegmentMappingId);
        return roleMappingId;
    }

    @Override
    public Map<String, Integer> getSegmentsByName() {
        String sql = "SELECT * FROM segments";
        Map<String, Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        ResultSetExtractor<Map<String, Integer>> rse = resultSet -> {
            resultSet.beforeFirst();
            while (resultSet.next()) {
                map.put(resultSet.getString("segment_name"), resultSet.getInt("segment_id"));
            }
            return map;
        };
        namedParameterJdbcTemplate.query(sql, rse);

        return map;
    }

    @Override
    public Map<String, Integer> getServicesByName() {
        String sql = "SELECT * FROM services";
        Map<String, Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        ResultSetExtractor<Map<String, Integer>> rse = resultSet -> {
            resultSet.beforeFirst();
            while (resultSet.next()) {
                map.put(resultSet.getString("service_type"), resultSet.getInt("service_id"));
            }
            return map;
        };
        namedParameterJdbcTemplate.query(sql, rse);
        return map;
    }

    @Override
    public List<Role> getRoles() {
        String sql = "SELECT * FROM roles";
        List<Role> roles = new ArrayList<>();
        ResultSetExtractor<List<Role>> rse = resultSet -> {
            resultSet.beforeFirst();
            while (resultSet.next()) {
                Role role = new Role(resultSet.getInt("role_id"), resultSet.getString("role_name"), resultSet.getInt("service_id"));
                roles.add(role);
            }
            return roles;
        };
        namedParameterJdbcTemplate.query(sql, rse);

        return roles;
    }

    @Override
    public Map<String, Integer> getAwsAccountsByName() {
        String sql = "SELECT * FROM aws_account_profile";
        Map<String, Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        ResultSetExtractor<Map<String, Integer>> rse = resultSet -> {
            resultSet.beforeFirst();
            while (resultSet.next()) {
                map.put(resultSet.getString("account_id"), resultSet.getInt("id"));
            }
            return map;
        };
        namedParameterJdbcTemplate.query(sql, rse);
        return map;
    }

    @Override
    public void removeRole(int userAccountSegmentRoleMappingId) {
        logger.info("removeRole for userAccountSegmentRoleMappingId:" + userAccountSegmentRoleMappingId);

        String sql = "delete from user_account_segment_role_mapping where user_account_segment_role_mapping_id=:user_account_segment_role_mapping_id";

        MapSqlParameterSource namedParams = new MapSqlParameterSource();
        namedParams.addValue("user_account_segment_role_mapping_id", userAccountSegmentRoleMappingId);

        try {
            int rowsRemoved = namedParameterJdbcTemplate.update(sql, namedParams);
            logger.info("removeRole - rows removed:" + rowsRemoved);
        } catch (Exception ex) {
            throw new QuickFabricSQLException("Unable to remove Role in DB for mappingId :" + userAccountSegmentRoleMappingId, ex);
        }


    }

    @Override
    public LoginRolesVO createUser(String firstName, String lastName, String email, String password) {
        logger.info("createUser firstName:" + firstName + " lastName:" + lastName + " email:" + email);

        String sql = "INSERT INTO user (first_name, last_name, email_id, passcode, creation_date) " +
                " VALUES(:firstName, :lastName, :email, :passcode, :creationDate)";

        MapSqlParameterSource namedParams = new MapSqlParameterSource();
        namedParams.addValue("firstName", firstName);
        namedParams.addValue("lastName", lastName);
        namedParams.addValue("email", email);
        namedParams.addValue("passcode", password);
        namedParams.addValue("creationDate", new Date());

        KeyHolder holder = new GeneratedKeyHolder();
        try {
            namedParameterJdbcTemplate.update(sql, namedParams, holder);
        } catch (Exception ex) {
            throw new QuickFabricSQLException("Unable to create user in DB for User : " + email, ex);
        }
        int userId = holder.getKey().intValue();
        logger.info("received new userId:" + userId);

        LoginRolesVO user = new LoginRolesVO();
        user.setUserId(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmailId(email);

        return user;
    }

    @Transactional
    @Override
    public void updateAccountDefinition(AwsAccountProfile accountDetails) {
        logger.info("updating account " + accountDetails.getAccountId() + " in DB");
        String sql = "update aws_account_profile set " +
                " account_env = COALESCE(:account_env, account_env)," +
                " account_owner = COALESCE(:account_owner, account_owner) " +
                " where account_id = :account_id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("account_id", accountDetails.getAccountId());
        params.addValue("account_env", accountDetails.getAccountEnv());
        params.addValue("account_owner", accountDetails.getAccountOwner());

        try {
            logger.info("updating account details into DB");
            this.namedParameterJdbcTemplate.update(sql, params);
        } catch (Exception e) {
            logger.error("Error occurred updating account details into DB for account: {}. Error: {}",
                    accountDetails.getAccountId(), e.getMessage());
            throw new QuickFabricSQLException("Error occurred updating account details into DB ", e);
        }
    }

    @Transactional
    public void addNewAccountDefinition(List<AwsAccountProfile> accountDetails) {
        logger.info("Adding account " + accountDetails.get(0).getAccountId() + " in DB");
        String insertSQL = "INSERT INTO aws_account_profile (account_id, account_env,account_owner) " +
                " values " +
                "(:account_id, :account_env, :account_owner)";
        MapSqlParameterSource insertParams = new MapSqlParameterSource();
        insertParams.addValue("account_id", accountDetails.get(0).getAccountId());
        insertParams.addValue("account_env", accountDetails.get(0).getAccountEnv());
        insertParams.addValue("account_owner", accountDetails.get(0).getAccountOwner());
        try {
            logger.info("adding new account details into DB");
            this.namedParameterJdbcTemplate.update(insertSQL, insertParams);
        } catch (Exception e) {
            logger.error("Error occurred adding new account details into DB for account: {}. Error: {}",
                    accountDetails.get(0).getAccountId(), e.getMessage());
            throw new QuickFabricSQLException("Error occurred updating account details into DB ", e);

        }
    }

    @Override
    @Transactional
    public void updateSegment(SegmentVO segment) {
        logger.info("Updating Segment in DB");
        String sql = "update segments set business_owner =:business_owner, business_owner_email =:business_owner_email " +
                " where segment_name=:segment_name";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("segment_name", segment.getSegmentName());
        params.addValue("business_owner", segment.getBusinessOwner());
        params.addValue("business_owner_email", segment.getBusinessOwnerEmail());

        try {
            logger.info("updating segment details into DB");
            this.namedParameterJdbcTemplate.update(sql, params);
        } catch (Exception e) {
            logger.error("Error occurred updating segment in DB. segment name: {}. Error: {}",
                    segment.getSegmentName(), e.getMessage());
            throw new QuickFabricSQLException("Error occurred updating segments into DB ", e);
        }
    }

    @Transactional
    public void addNewSegment(SegmentVO segment) {
        logger.info("Adding new Segment in DB");
        String insertSQL = "INSERT INTO segments (segment_name, business_owner,business_owner_email) " +
                " values " +
                "(:segment_name, :business_owner, :business_owner_email)";
        MapSqlParameterSource insertParams = new MapSqlParameterSource();
        insertParams.addValue("segment_name", segment.getSegmentName());
        insertParams.addValue("business_owner", segment.getBusinessOwner());
        insertParams.addValue("business_owner_email", segment.getBusinessOwnerEmail());
        try {
            logger.info("adding new account details into DB");
            this.namedParameterJdbcTemplate.update(insertSQL, insertParams);
        } catch (Exception e) {
            logger.error("Error occurred while inserting new segment: {}. Error: {}",
                    segment.getSegmentName(), e.getMessage());
            throw new QuickFabricSQLException("Error occurred inserting segments into DB ", e);
        }
    }

    @Override
    @Transactional
    public void clearDefaultAccounts() {
        String deleteMetrics = "DELETE FROM cluster_metrics WHERE account "
                + "IN ('100000000000','200000000000','300000000000')";

        String deleteMetricsHistory = "DELETE FROM cluster_metrics_history WHERE account "
                + "IN ('100000000000','200000000000','300000000000')";

        String deleteAccountProfile = "DELETE FROM aws_account_profile WHERE account_id "
                + "IN ('100000000000','200000000000','300000000000')";

        String deleteCost = "DELETE cost FROM emr_billing_component_cost cost "
                + "JOIN emr_cluster_metadata metadata "
                + "ON cost.emr_name = metadata.cluster_name AND "
                + "metadata.account IN ('100000000000','200000000000','300000000000')";

        String deleteCostHistory = "DELETE cost FROM emr_billing_component_cost_hist cost "
                + "JOIN emr_cluster_metadata metadata "
                + "ON cost.emr_name = metadata.cluster_name AND "
                + "metadata.account IN ('100000000000','200000000000','300000000000')";

        String deleteConfigs = "DELETE FROM account_configurations WHERE account_id IN "
                + "('100000000000','200000000000','300000000000')";

        String deleteMetadata = "DELETE FROM emr_cluster_metadata WHERE account "
                + "IN ('100000000000','200000000000','300000000000')";

        int rowsAffected = -1;
        try {
            logger.info("AdminDaoImpl->clearDefaultAccounts Starting delete from metrics query. SQL::" + deleteMetrics);
            rowsAffected = jdbcTemplateObject.update(deleteMetrics);
            logger.info("AdminDaoImpl->clearDefaultAccounts Delete from metrics successful, " + rowsAffected + " rows deleted.");

            logger.info("AdminDaoImpl->clearDefaultAccounts Starting delete from metrics history query. SQL::" + deleteMetricsHistory);
            rowsAffected = jdbcTemplateObject.update(deleteMetricsHistory);
            logger.info("AdminDaoImpl->clearDefaultAccounts Delete from metrics history successful, " + rowsAffected + " rows deleted.");

            logger.info("AdminDaoImpl->clearDefaultAccounts Starting delete from cost table query. SQL::" + deleteCost);
            rowsAffected = jdbcTemplateObject.update(deleteCost);
            logger.info("AdminDaoImpl->clearDefaultAccounts Delete from cost table successful, " + rowsAffected + " rows deleted.");

            logger.info("AdminDaoImpl->clearDefaultAccounts Starting delete from cost history table query. SQL::" + deleteCostHistory);
            rowsAffected = jdbcTemplateObject.update(deleteCostHistory);
            logger.info("AdminDaoImpl->clearDefaultAccounts Delete from cost history table successful, " + rowsAffected + " rows deleted.");

            logger.info("AdminDaoImpl->clearDefaultAccounts Starting delete from account configurations query. SQL::" + deleteConfigs);
            rowsAffected = jdbcTemplateObject.update(deleteConfigs);
            logger.info("AdminDaoImpl->clearDefaultAccounts Delete from account configurations successful, " + rowsAffected + " rows deleted.");

            logger.info("AdminDaoImpl->clearDefaultAccounts Starting delete from AWS account profile query. SQL::" + deleteAccountProfile);
            rowsAffected = jdbcTemplateObject.update(deleteAccountProfile);
            logger.info("AdminDaoImpl->clearDefaultAccounts Delete from AWS account profile successful, " + rowsAffected + " rows deleted.");

            logger.info("AdminDaoImpl->clearDefaultAccounts Starting delete from metadata query. SQL::" + deleteMetadata);
            rowsAffected = jdbcTemplateObject.update(deleteMetadata);
            logger.info("AdminDaoImpl->clearDefaultAccounts Delete from metadata successful, " + rowsAffected + " rows deleted.");

            logger.info("AdminDaoImpl->clearDefaultAccounts All updates succeeded.");
        } catch (Exception e) {
            logger.error("AdminDaoImpl->clearDefaultAccounts Transaction failed, rolling back all previous updates. Reason: " + e.getMessage());
        }
    }
}
