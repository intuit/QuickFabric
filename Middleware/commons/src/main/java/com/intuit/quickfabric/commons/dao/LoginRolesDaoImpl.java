package com.intuit.quickfabric.commons.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.intuit.quickfabric.commons.domain.UserAccess;
import com.intuit.quickfabric.commons.exceptions.QuickFabricSQLException;
import com.intuit.quickfabric.commons.mapper.LoginRolesMapper;
import com.intuit.quickfabric.commons.mapper.UserAccessMapper;
import com.intuit.quickfabric.commons.vo.LoginRolesVO;

import java.util.List;

@Component
public class LoginRolesDaoImpl implements LoginRolesDao {

    private static final Logger logger = LogManager.getLogger(LoginRolesDaoImpl.class);

    @Autowired
    NamedParameterJdbcTemplate namedJdbcTemplateObject;

    public LoginRolesVO getUserByEmail(String emailID) {
        String SQL = "select user_id, email_id, first_name, last_name, creation_date, passcode, super_admin "
                + "FROM user where email_id = :email_id";
        MapSqlParameterSource namedParams = new MapSqlParameterSource();

        namedParams.addValue("email_id", emailID);

        logger.info("SQL::" + SQL);
        LoginRolesVO loginRoles = namedJdbcTemplateObject.query(SQL, namedParams, new LoginRolesMapper());

        return loginRoles;
    }

    public List<UserAccess> getUserAccessList(String email) {
        String query = "SELECT " +
                "    u.first_name," +
                "    u.last_name," +
                "    ds.service_type," +
                "    LOWER(dr.role_name) as role_name, " +
                "    dseg.segment_name," +
                "    ap.account_id AS aws_account_id," +
                "    ap.account_env ," +
                "    dr.role_id," +
                "    dseg.segment_id," +
                "    ap.id AS aws_account_profile_id," +
                "    uasrm.user_account_segment_role_mapping_id," +
                "    ds.service_id" +
                "  FROM" +
                "    user_account_segment_role_mapping uasrm" +
                "        JOIN" +
                "    user_account_segment_mapping uasm ON uasm.user_account_segment_mapping_id = uasrm.user_account_segment_mapping_id" +
                "        JOIN" +
                "    user u ON u.user_id = uasm.user_id" +
                "        JOIN" +
                "    aws_account_profile ap ON ap.id = uasm.aws_account_id" +
                "        JOIN" +
                "    segments dseg ON dseg.segment_id = uasm.segment_id" +
                "        JOIN" +
                "    roles dr ON dr.role_id = uasrm.role_id" +
                "        JOIN" +
                "    services ds ON ds.service_id = dr.service_id" +
                " WHERE" +
                "    u.email_id = :email" +
                " ORDER BY ds.service_type , dr.role_name , dseg.segment_name";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("email", email);

        List<UserAccess> accessRoleLists = namedJdbcTemplateObject.query(query, parameters, new UserAccessMapper());
        return accessRoleLists;
    }

    @Override
    public void resetPassword(int userId, String newEncryptedPassword) {

        String sql = "update user set passcode = :newEncryptedPassword where user_id = :user_id";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("newEncryptedPassword", newEncryptedPassword);
        parameters.addValue("user_id", userId);
        int rowsUpdated=0;
        try {
            rowsUpdated = namedJdbcTemplateObject.update(sql, parameters);
            logger.info("resetPassword - rows updated:" + rowsUpdated);
        }catch(Exception ex){
        	throw new QuickFabricSQLException("Unable to reset Password in DB for  userId :"+userId,ex);
        }

        if (rowsUpdated != 1) {
            logger.error("failed to update password for user_id:" + userId + ". Rows updated:" + rowsUpdated);
            throw new QuickFabricSQLException("unable to update new password for userId:" + userId);
        }

        logger.info("updated new password for user_id:" + userId + ". Rows updated:" + rowsUpdated);
    }
}
