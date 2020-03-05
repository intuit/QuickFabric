CREATE TABLE `application_configurations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `config_id` int(11) NOT NULL,
  `config_value` varchar(128) NOT NULL,
  `is_encrypted` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `config_id_UNIQUE` (`config_id`),
  KEY `fk_configuration_id_idx` (`config_id`),
  CONSTRAINT `fk_global_configuration_id` FOREIGN KEY (`config_id`) REFERENCES `configuration_definitions` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);