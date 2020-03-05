--
-- Table structure for table `emr_functional_testsuites_status`
--
CREATE TABLE IF NOT EXISTS `emr_functional_testsuites_status` (
  `execution_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(1024) NOT NULL,
  `status` varchar(256) NOT NULL,
  `cluster_id` varchar(256) NOT NULL,
  `cluster_name` varchar(256) NOT NULL,
  `cluster_type` varchar(256) NOT NULL,
  `cluster_segment` varchar(256) NOT NULL,
  `execution_start_time` datetime NOT NULL,
  `execution_end_time` datetime DEFAULT NULL,
  `executed_by` varchar(256) NOT NULL,
  `remark` varchar(1024) DEFAULT NULL,
  `expires_minutes` int(11) NOT NULL DEFAULT '60',
  `mandatory` tinyint(1) NOT NULL DEFAULT '0',
  `disabled` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`execution_id`)
);
