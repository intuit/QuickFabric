package com.intuit.quickfabric.emr.dao;

import com.intuit.quickfabric.commons.vo.SubscriptionVO;

import java.util.List;

public interface EmailReportSubscriptionsDao {
    
    List<SubscriptionVO> getSubscriptionsForUser(String userEmail);

    void updateSubscriptionsForUser(List<Long> reports, String email);

}
