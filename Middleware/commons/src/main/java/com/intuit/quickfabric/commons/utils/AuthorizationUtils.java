package com.intuit.quickfabric.commons.utils;

import org.apache.commons.lang3.StringUtils;
//import com.intuit.generalutils.AESUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.intuit.quickfabric.commons.constants.ApplicationConstant;
import com.intuit.quickfabric.commons.exceptions.QuickFabricUnauthenticatedException;
import com.intuit.quickfabric.commons.helper.ConfigHelper;
import com.intuit.quickfabric.commons.vo.SSODetailsVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthorizationUtils {

    private static final Logger logger = LogManager.getLogger(AuthorizationUtils.class);
    
    @Autowired
    ConfigHelper configHelper;

    public SSODetailsVO getSSODetails(HttpServletRequest request, HttpServletResponse response)  {
        logger.debug("AuthorizationUtils API->getSSODetails()");
        SSODetailsVO ssoDetails = new SSODetailsVO();

        //AESUtility aesUtility = new AESUtility();
        String returnValues = request.getParameter(ApplicationConstant.RETURN_VALUES);
        logger.debug("return values from SSO " + returnValues);
        if(StringUtils.isBlank(returnValues) ) {
        	throw new QuickFabricUnauthenticatedException("user is not authenticated via SSO");
        }

        // example: userid=znorcross|mail=Zackery_Norcross@intuit.com|intuitcorpid=10000119491|qbn.ptc.ticket=V1-94-a3oqp0h5n33danuxfpc3em|qbn.ptc.tkt=V1-94-a3oqp0h5n33danuxfpc3em|qbn.ptc.authid=1015594897|qbn.ptc.gauthid=1015594897|qbn.ptc.agentid=1015594897|qbn.ptc.parentid=50000000|timestamp=2017-08-23 08:58:09
        String decryptedReturnValues = "";//aesUtility.decryptCBC(returnValues, configHelper.getConfigValue("sso_shared_key"), request.getParameter(ApplicationConstant.IV));

        //            if(StringUtils.isBlank(decryptedReturnValues) ) {
        //            	throw new QuickFabricUnauthenticatedException("asdfasdf);
        //            }

        logger.debug("decryptedReturnValues return values from SSO " + decryptedReturnValues);


        String userId = parseValueFromReturnValues(ApplicationConstant.USER_ID, decryptedReturnValues);
        logger.debug("Parsed Email return values from SSO " + userId);

        String email = parseValueFromReturnValues(configHelper.getConfigValue("sso_email_key"), decryptedReturnValues);
        logger.debug("Parsed Email return values from SSO " + email);

        String qbnPtcAuthId = parseValueFromReturnValues(configHelper.getConfigValue("sso_qbn_ptc_authid"), decryptedReturnValues);
        logger.debug("Parsed qbnPtcAuthId return values from SSO " + qbnPtcAuthId);

        String qbnAuthId = parseValueFromReturnValues(configHelper.getConfigValue("sso_qbn_authid"), decryptedReturnValues);
        logger.debug("Parsed qbnAuthId return values from SSO " + qbnAuthId);

        String qbnPtcTkt = parseValueFromReturnValues(configHelper.getConfigValue("sso_qbn_ptc_tkt"), decryptedReturnValues);
        logger.debug("Parsed qbnPtcTkt return values from SSO " + qbnPtcTkt);

        String qbnTkt = parseValueFromReturnValues(configHelper.getConfigValue("sso_qbn_tkt"), decryptedReturnValues);
        logger.debug("Parsed qbnTkt return values from SSO " + qbnTkt);

        String fName = parseValueFromReturnValues(ApplicationConstant.FNAME, decryptedReturnValues);
        logger.debug("Parsed fName return values from SSO " + fName);

        String lName = parseValueFromReturnValues(ApplicationConstant.LNAME, decryptedReturnValues);
        logger.debug("Parsed lName return values from SSO " + lName);
        ssoDetails.setUserId(userId);
        ssoDetails.setEmail(email);
        ssoDetails.setQbnPtcAuthid(qbnPtcAuthId);
        ssoDetails.setQbnAuthid(qbnAuthId);
        ssoDetails.setQbnPtcTkt(qbnPtcTkt);
        ssoDetails.setQbTkt(qbnTkt);
        ssoDetails.setFirstName(fName);
        ssoDetails.setLastName(lName);

        return ssoDetails;
    }

    private String parseValueFromReturnValues(String key, String decryptedReturnValues) {
        Integer startIndex = decryptedReturnValues.indexOf(key);
        if (startIndex > -1) {
            startIndex += key.length();
            return decryptedReturnValues.substring(startIndex + 1, decryptedReturnValues.indexOf("|", startIndex));
        } else {
            return null;
        }
    }
}
