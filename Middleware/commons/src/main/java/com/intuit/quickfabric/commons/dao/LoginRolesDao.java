package com.intuit.quickfabric.commons.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.intuit.quickfabric.commons.domain.UserAccess;
import com.intuit.quickfabric.commons.vo.LoginRolesVO;

@Component
public interface LoginRolesDao {

    LoginRolesVO getUserByEmail(String emailID);

	List<UserAccess> getUserAccessList(String email);

    void resetPassword(int userId, String newEncryptedPassword) ;
}
