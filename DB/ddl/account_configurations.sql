--
-- Table structure for table `account_configurations`
--
CREATE TABLE IF NOT EXISTS `account_configurations` (
  `int` int(11) NOT NULL AUTO_INCREMENT,
  `config_id` int(11) NOT NULL,
  `account_id` varchar(128) NOT NULL,
  `config_value` varchar(128) NOT NULL,
  `is_encrypted` varchar(45) NOT NULL DEFAULT '0',
  PRIMARY KEY (`int`),
  UNIQUE KEY `composite_unique_config_id_and_account_id` (`config_id`,`account_id`),
  KEY `fk_configuration_id_idx` (`config_id`),
  KEY `fk_account_id_idx` (`account_id`),
  CONSTRAINT `fk_account_id` FOREIGN KEY (`account_id`) REFERENCES `aws_account_profile` (`account_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_configuration_id` FOREIGN KEY (`config_id`) REFERENCES `configuration_definitions` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);
