--
-- Table structure for table `db_patch`
--
CREATE TABLE `db_patch` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `patch_name` varchar(128) NOT NULL,
  `patch_status` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `patch_name_UNIQUE` (`patch_name`)
);
