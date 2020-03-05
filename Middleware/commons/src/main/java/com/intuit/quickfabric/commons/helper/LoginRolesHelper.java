
package com.intuit.quickfabric.commons.helper;

import com.intuit.quickfabric.commons.dao.LoginRolesDao;
import com.intuit.quickfabric.commons.domain.UserAccess;
import com.intuit.quickfabric.commons.exceptions.QuickFabricClientException;
import com.intuit.quickfabric.commons.exceptions.QuickFabricUnauthenticatedException;
import com.intuit.quickfabric.commons.model.LoginRolesModel;
import com.intuit.quickfabric.commons.security.JWTTokenProvider;
import com.intuit.quickfabric.commons.utils.AuthorizationUtils;
import com.intuit.quickfabric.commons.vo.*;
import io.jsonwebtoken.Claims;
import org.apache.cxf.common.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LoginRolesHelper {

    private static final Logger logger = LogManager.getLogger(LoginRolesHelper.class);

    @Autowired
    LoginRolesDao loginRolesDao;

    @Autowired
    JWTTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    AuthorizationUtils authorizationUtils;

    public LoginRolesModel getLoginRolesModel(LoginRequest loginRequest)  {
    	
    	HttpServletRequest servletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String emailID = loginRequest == null ? null : loginRequest.getEmailId();
        String passcode = loginRequest == null ? null : loginRequest.getPasscode();
        logger.info("LoginRolesHelper -> getLoginRolesModel emailID:" + emailID);
        
        String token = tokenProvider.resolveToken(servletRequest);
        LoginRolesModel loginRolesModel;
        if (!StringUtils.isEmpty(emailID) && !StringUtils.isEmpty(passcode)) {
            LoginRolesVO userDetails = loginRolesDao.getUserByEmail(emailID);
            if (userDetails == null || !passwordEncoder.matches(passcode, userDetails.getPasscode())) {
                throw new QuickFabricUnauthenticatedException("username or password is incorrect.");
            }
            loginRolesModel = getRolesWithSegments(emailID, userDetails);
        } else if (!StringUtils.isEmpty(token)) {
            Claims claims = tokenProvider.getClaims(token);
            String email = claims.getSubject();
            loginRolesModel = getLoginRolesModel(email);
        } else {
            throw new QuickFabricUnauthenticatedException("username or password is incorrect.");
        }
        
        String jwtToken = tokenProvider.createTokenAuth(loginRolesModel.getLoginRoles());
        loginRolesModel.setJwtToken(jwtToken);
        return loginRolesModel;
    }

    
    public LoginRolesModel getLoginRolesModel(String emailID)  {
        if (StringUtils.isEmpty(emailID)) {
            throw new QuickFabricClientException("email cannot be null or empty.");
        }

        LoginRolesVO userRoles = loginRolesDao.getUserByEmail(emailID);
        LoginRolesModel loginRolesModel = getRolesWithSegments(emailID, userRoles);
        return loginRolesModel;
    }

    private LoginRolesModel getRolesWithSegments(String emailID, LoginRolesVO userRoles) {
        List<UserAccess> userAccessList = loginRolesDao.getUserAccessList(emailID);

        populateRolesWithSegmentAndAccount(userRoles, userAccessList, ServiceType.EMR);
        LoginRolesModel loginRolesModel = new LoginRolesModel();
        loginRolesModel.setLoginRoles(userRoles);
        return loginRolesModel;
    }

    private void populateRolesWithSegmentAndAccount(LoginRolesVO user, List<UserAccess> userAccessList, ServiceType serviceType) {

        // Get all roles by service
        List<UserAccess> rolesByServiceType = userAccessList.stream()
                .filter(r -> r.getServiceType() == serviceType).collect(Collectors.toList());

        if (!rolesByServiceType.isEmpty()) {

            ServiceVO service = new ServiceVO();
            List<UserRole> roles = new ArrayList<>();

            // Add service to user
            service.setServiceType(serviceType);
            user.setService(service);

            // Create role -> access list grouping
            Map<Integer, List<UserAccess>> roleToAccessMap = rolesByServiceType
                    .stream()
                    .collect(Collectors.groupingBy(a -> a.getRoleId(),
                            Collectors.mapping(z -> z, Collectors.toList())));

            for (Map.Entry<Integer, List<UserAccess>> roleMap : roleToAccessMap.entrySet()) {
                int roleId = roleMap.getKey();

                // Create role and add it to user
                UserRole role = new UserRole();
                role.setId(roleId);
                role.setName(roleMap.getValue().get(0).getRoleName());

                // create segment to access list map for a given role
                Map<String, List<UserAccess>> segmentToAccessMap = roleMap.getValue().stream()
                        .collect(Collectors.groupingBy(a -> a.getSegmentName(),
                                Collectors.mapping(z -> z, Collectors.toList())));

                List<SegmentVO> segments = new ArrayList<>();
                for (Map.Entry<String, List<UserAccess>> segmentMap : segmentToAccessMap.entrySet()) {

                    SegmentVO segment = new SegmentVO();
                    segment.setSegmentName(segmentMap.getKey());

                    List<AwsAccountProfile> accounts = new ArrayList<>();

                    // Get all the access list for a segment
                    for (UserAccess accessRole : segmentMap.getValue()) {
                        AwsAccountProfile account = new AwsAccountProfile();
                        account.setAccountId(accessRole.getAwsAccountName());
                        account.setAccountEnv(accessRole.getAccountEnv());

                        accounts.add(account);
                    }

                    segment.setAccounts(accounts);
                    segments.add(segment);
                }

                role.setSegments(segments);
                roles.add(role);
            }

            service.setRoles(roles);
        }
    }


	public void createSSOResponse(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		SSODetailsVO ssoDetails = authorizationUtils.getSSODetails(request, response);
		LoginRolesModel loginRolesModel = getLoginRolesModel(ssoDetails.getEmail());
        String jwtToken = tokenProvider.createTokenAuth(loginRolesModel.getLoginRoles());
		buildSSOResponse(response,ssoDetails.getEmail(), jwtToken);
	}
	
	private void buildSSOResponse(HttpServletResponse response, String emailId, String jwtToken) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "*");
        Cookie userEmailCookie = new Cookie("user_email", emailId);
        userEmailCookie.setPath("/");
        userEmailCookie.setMaxAge(30000);

        Cookie tokenCookie = new Cookie("jwt",jwtToken);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(30000);

        response.addCookie(userEmailCookie);
        response.addCookie(tokenCookie);

       
    }
}




