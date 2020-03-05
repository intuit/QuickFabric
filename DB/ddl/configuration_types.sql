CREATE TABLE IF NOT EXISTS `configuration_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `config_type` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `config_type_UNIQUE` (`config_type`)
);
