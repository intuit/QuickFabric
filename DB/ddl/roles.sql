--
-- Table structure for table `roles`
--
CREATE TABLE IF NOT EXISTS `roles` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(128) NOT NULL,
  `service_id` int(11) NOT NULL,
  PRIMARY KEY (`role_id`),
  KEY `role_service_foreign_key_idx` (`service_id`),
  CONSTRAINT `role_to_services_foreign_key` FOREIGN KEY (`service_id`) REFERENCES `services` (`service_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) AUTO_INCREMENT=400000;
