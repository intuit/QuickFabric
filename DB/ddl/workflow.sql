--
-- Table structure for table `workflow`
--
CREATE TABLE IF NOT EXISTS `workflow` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `workflow_name` varchar(45) NOT NULL,
  `workflow_step` varchar(45) NOT NULL,
  `lookup_table` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
);
