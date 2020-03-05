--
-- Table structure for table `user_account_segment_mapping`
--
CREATE TABLE IF NOT EXISTS `user_account_segment_mapping` (
  `user_account_segment_mapping_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `aws_account_id` int(11) NOT NULL,
  `segment_id` int(11) NOT NULL,
  PRIMARY KEY (`user_account_segment_mapping_id`),
  UNIQUE KEY `unique_user_account_segment` (`user_id`,`aws_account_id`,`segment_id`),
  KEY `user_foreign_key_idx` (`user_id`),
  KEY `account_foreign_key_idx` (`aws_account_id`),
  KEY `segment_foregin_key_idx` (`segment_id`),
  CONSTRAINT `aws_account_foreign_key` FOREIGN KEY (`aws_account_id`) REFERENCES `aws_account_profile` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `segment_foregin_key` FOREIGN KEY (`segment_id`) REFERENCES `segments` (`segment_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_foreign_key` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) AUTO_INCREMENT=600000;
