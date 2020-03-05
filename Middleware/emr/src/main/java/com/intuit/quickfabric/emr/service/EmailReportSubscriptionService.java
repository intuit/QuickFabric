package com.intuit.quickfabric.emr.service;

import com.intuit.quickfabric.commons.domain.QuickFabricAuthenticationToken;
import com.intuit.quickfabric.commons.utils.CommonUtils;
import com.intuit.quickfabric.commons.vo.SubscriptionVO;
import com.intuit.quickfabric.emr.helper.EmailReportSubscriptionsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report-subscriptions")
public class EmailReportSubscriptionService {

    @Autowired
    private EmailReportSubscriptionsHelper emailReportSubscriptionsHelper;

    /**
     * Full refresh of current user's subscriptions. Old subscription information will be replaced with
     * what is given.
     *
     * @param subscriptions the user's new subscriptions.
     * @return the user's new subscriptions
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = "/update")
    @PreAuthorize("hasAnyAuthority('superadmin, admin, read')")
    public ResponseEntity<List<SubscriptionVO>> updateSubscriptions(@RequestBody List<SubscriptionVO> subscriptions) {
        try {
            QuickFabricAuthenticationToken auth =
                    (QuickFabricAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getEmail();
            List<SubscriptionVO> result = emailReportSubscriptionsHelper.updateSubscriptions(subscriptions, email);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return CommonUtils.createErrorResponse(ex);
        }
    }

    /**
     * Retrieve a list of current user's email report subscriptions
     *
     * @return the subscriptions
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping()
    @PreAuthorize("hasAnyAuthority('superadmin, admin, read')")
    public ResponseEntity<List<SubscriptionVO>> getSubscriptions() {
        try {
            QuickFabricAuthenticationToken auth =
                    (QuickFabricAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getEmail();
            List<SubscriptionVO> subs = emailReportSubscriptionsHelper.getSubscriptionsForUser(email);
            return ResponseEntity.ok(subs);
        } catch (Exception ex) {
            return CommonUtils.createErrorResponse(ex);
        }
    }
}
