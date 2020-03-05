package com.intuit.quickfabric.commons.security;

import com.intuit.quickfabric.commons.constants.Roles;
import com.intuit.quickfabric.commons.domain.QuickFabricAuthenticationToken;
import com.intuit.quickfabric.commons.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.stream.Collectors;

public class AccessControl {

    private static final Logger logger = LogManager.getLogger(AccessControl.class);

    public static boolean hasConfigAccess(ConfigDefinitionVO configDefinition) {
        logger.info("Checking access control for configAccess:" + configDefinition);
        QuickFabricAuthenticationToken authentication = (QuickFabricAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        boolean hasAccess = true;
        if (!authentication.isSuperAdmin() && !configDefinition.isUserAccessible()) {
            hasAccess = false;
        }
        return hasAccess;
    }

    public static boolean isNotAccessible(String segmentName, String accountId, String roleName) {
        return hasAccess(segmentName, accountId, roleName) == false;
    }

    public static boolean hasAccess(String segmentName, String accountId, String roleName) {

        logger.info("Checking access control for segment:" + segmentName + ", accountId:" + accountId + " and role:" + roleName);
        ServiceVO service = (ServiceVO) SecurityContextHolder.getContext().getAuthentication().getDetails();
        QuickFabricAuthenticationToken authentication = (QuickFabricAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        boolean hasAccess = false;
        if (service == null) {
            logger.warn("QuickFabricService is empty for user:" + SecurityContextHolder.getContext().getAuthentication().getDetails());
            hasAccess = false;
        }

        if (StringUtils.isBlank(accountId) || StringUtils.isBlank(segmentName)) {
            logger.warn("hasAccess - Cluster request does not have account or segment.");
            hasAccess = false;
        }

        if (authentication.isSuperAdmin()) {
            hasAccess = true;
        } else if (service != null) {
            for (UserRole role : service.getRoles().stream().filter(x -> x.getName().equalsIgnoreCase(roleName)
                    || x.getName().equalsIgnoreCase(Roles.ADMIN)).collect(Collectors.toList())) {
                SegmentVO segment = role.getSegments().stream()
                        .filter(x -> x.getSegmentName().equalsIgnoreCase(segmentName))
                        .findAny()
                        .orElse(null);

                if (segment != null) {
                    AwsAccountProfile awsAccountProfile = segment.getAccounts().stream()
                            .filter(x -> x.getAccountId().equalsIgnoreCase(accountId))
                            .findAny()
                            .orElse(null);
                    if (awsAccountProfile != null) {
                        hasAccess = true;
                        break;
                    }
                }
            }
        }

        return hasAccess;
    }
}
