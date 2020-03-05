--
-- Table structure for table `emr_functional_testsuites`
--
CREATE TABLE IF NOT EXISTS `emr_functional_testsuites` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(767) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `criteria` varchar(256) DEFAULT NULL,
  `cluster_type` varchar(256) NOT NULL,
  `cluster_segment` varchar(256) NOT NULL,
  `timeout` int(11) NOT NULL DEFAULT '60',
  `expires_minutes` int(11) NOT NULL DEFAULT '60',
  `mandatory` tinyint(1) NOT NULL DEFAULT '0',
  `disabled` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `TestName_ClusterSegment_ClusterType_Unique_Constraint` (`name`,`cluster_type`,`cluster_segment`)
);
