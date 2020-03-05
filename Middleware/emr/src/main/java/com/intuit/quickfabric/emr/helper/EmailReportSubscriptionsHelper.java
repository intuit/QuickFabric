package com.intuit.quickfabric.emr.helper;

import com.intuit.quickfabric.commons.vo.SubscriptionVO;
import com.intuit.quickfabric.emr.dao.EmailReportSubscriptionsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmailReportSubscriptionsHelper {

    @Autowired
    private EmailReportSubscriptionsDao emailReportSubscriptionsDao;

    public List<SubscriptionVO> getSubscriptionsForUser(String email) {
        return this.emailReportSubscriptionsDao.getSubscriptionsForUser(email);
    }

    public List<SubscriptionVO> updateSubscriptions(List<SubscriptionVO> subscriptions, String email) {

        //report ids for reports the user wants to subscribe to
        List<Long> reportsToSubscribe = subscriptions.stream()
                .filter(s -> s.isSubscribed())
                .map(s -> s.getReportId()).collect(Collectors.toList());

        this.emailReportSubscriptionsDao.updateSubscriptionsForUser(reportsToSubscribe, email);
        return getSubscriptionsForUser(email);
    }
}
