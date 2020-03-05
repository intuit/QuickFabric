CREATE TABLE IF NOT EXISTS `configuration_data_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `data_type_name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `data_type_name_UNIQUE` (`data_type_name`)
);