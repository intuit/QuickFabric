--
-- Table structure for table `user_account_segment_role_mapping`
--
CREATE TABLE IF NOT EXISTS `user_account_segment_role_mapping` (
  `user_account_segment_role_mapping_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_account_segment_mapping_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_account_segment_role_mapping_id`),
  KEY `action_mapping_key_idx` (`role_id`),
  KEY `account_segment_mapping_reference_key_idx` (`user_account_segment_mapping_id`),
  CONSTRAINT `roles_reference_key` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_account_segment_mapping_reference_key` FOREIGN KEY (`user_account_segment_mapping_id`) REFERENCES `user_account_segment_mapping` (`user_account_segment_mapping_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) AUTO_INCREMENT 1000000;
