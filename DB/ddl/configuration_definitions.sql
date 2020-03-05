CREATE TABLE IF NOT EXISTS `configuration_definitions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `config_name` varchar(128) NOT NULL,
  `config_description` varchar(256) NOT NULL,
  `config_type` int(11) NOT NULL,
  `data_type` int(11) NOT NULL DEFAULT '1',
  `encryption_required` tinyint(4) NOT NULL DEFAULT '0',
  `is_mandatory` tinyint(4) NOT NULL DEFAULT '0',
  `is_user_accessible` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `config_name_UNIQUE` (`config_name`),
  KEY `configuration_type_idx` (`config_type`),
  KEY `fk_data_type_idx` (`data_type`),
  CONSTRAINT `fk_configuration_type` FOREIGN KEY (`config_type`) REFERENCES `configuration_types` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_configurations_definition_configuration_data_type` FOREIGN KEY (`data_type`) REFERENCES `configuration_data_types` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);
