package com.intuit.quickfabric.commons.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.LoginRolesVO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginRolesMapper implements ResultSetExtractor<LoginRolesVO> {


    public LoginRolesVO extractData(ResultSet rs) throws SQLException,
            DataAccessException {

    	LoginRolesVO loginRolesVO = null;
        while (rs.next()) {
            loginRolesVO = new LoginRolesVO();
            loginRolesVO.setEmailId(rs.getString("email_id"));
            loginRolesVO.setFirstName(rs.getString("first_name"));
            loginRolesVO.setLastName(rs.getString("last_name"));
            loginRolesVO.setCreationDate(rs.getString("creation_date"));
            loginRolesVO.setPasscode(rs.getString("passcode"));
            loginRolesVO.setSuperAdmin(rs.getBoolean("super_admin"));
            loginRolesVO.setUserId(rs.getInt("user_id"));
        }
        return loginRolesVO;
    }
}