package com.intuit.quickfabric.emr.dao;

import com.intuit.quickfabric.commons.exceptions.QuickFabricSQLException;
import com.intuit.quickfabric.commons.vo.SubscriptionVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component
public class EmailReportSubscriptionsDaoImpl implements EmailReportSubscriptionsDao {

    private final Logger logger = LogManager.getLogger(EmailReportSubscriptionsDaoImpl.class);

    @Autowired
    NamedParameterJdbcTemplate namedJdbcTemplateObject;

    @Override
    public List<SubscriptionVO> getSubscriptionsForUser(String userEmail) {
        logger.info("Retrieving subscriptions for user: {}", userEmail);

        String sql = "SELECT report_name, r.report_id, u.user_id IS NOT NULL subscribed " +
                " FROM " +
                " report_subscriptions s " +
                " JOIN user u ON u.user_id = s.user_id AND u.email_id = :user_email" +
                " RIGHT JOIN reports r ON r.report_id = s.report_id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_email", userEmail);

        logger.info("SQL:: {}", sql);

        return this.namedJdbcTemplateObject.query(sql, params, (ResultSet rs) -> {
            List<SubscriptionVO> reports = new ArrayList<>();

            while (rs.next()) {
                SubscriptionVO vo = new SubscriptionVO();
                vo.setReportId(rs.getLong("report_id"));
                vo.setReportName(rs.getString("report_name"));
                vo.setSubscribed(rs.getBoolean("subscribed"));
                reports.add(vo);
            }
            return reports;
        });
    }

    @Override
    @Transactional
    public void updateSubscriptionsForUser(List<Long> reportsToSubscribe, String email) {
        try {
            logger.info("Updating subscriptions for user: {}", email);

            //full refresh. delete all old subscription mappings and replace them with new ones
            String unsubscribeSQL = "DELETE subs FROM report_subscriptions subs " +
                    " JOIN  user u ON subs.user_id = u.user_id AND u.email_id = :email";
            MapSqlParameterSource deleteParams = new MapSqlParameterSource();
            deleteParams.addValue("email", email);

            String subscribeSQL = "INSERT INTO report_subscriptions(report_id, user_id) " +
                    "SELECT report_id, (SELECT user_id FROM user u WHERE email_id = :email) " +
                    "FROM reports WHERE report_id IN (:to_subscribe)";

            MapSqlParameterSource insertParams = new MapSqlParameterSource();
            insertParams.addValue("email", email);
            insertParams.addValue("to_subscribe", reportsToSubscribe);

            logger.info("Removing old subscription records for user: {}", email);
            this.namedJdbcTemplateObject.update(unsubscribeSQL, deleteParams);

            if (reportsToSubscribe.size() > 0) {
                logger.info("Inserting new subscriptions for user: {}", email);
                this.namedJdbcTemplateObject.update(subscribeSQL, insertParams);
            } else {
                logger.info("User {} has unsubscribed from all reportsToSubscribe. Not running insert query.", email);
            }
        } catch (Exception e) {
            String message = "Error occurred updating subscriptions for user: " + email;
            logger.error(message, e);
            throw new QuickFabricSQLException(message, e);
        }
    }
}
