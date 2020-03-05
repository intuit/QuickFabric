--
-- Table structure for table `report_subscriptions`
--
CREATE TABLE IF NOT EXISTS `report_subscriptions` (
  `report_subscription_id` int(11) NOT NULL AUTO_INCREMENT,
  `report_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`report_subscription_id`)
);
