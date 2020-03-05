--
-- Table structure for table `cluster_metrics_history`
--

CREATE TABLE IF NOT EXISTS `cluster_metrics_history` (
  `emr_id` varchar(512) NOT NULL DEFAULT '',
  `rm_url` varchar(1024) DEFAULT NULL,
  `refresh_timestamp` datetime DEFAULT NULL,
  `metrics_json` text,
  `emr_status` varchar(100) DEFAULT NULL,
  `cost` float DEFAULT '100',
  `available_memory_perc` float DEFAULT NULL,
  `available_cores_perc` float DEFAULT NULL,
  `emr_name` varchar(255) DEFAULT NULL,
  `total_nodes` int(11) DEFAULT NULL,
  `containers_pending` int(11) DEFAULT NULL,
  `apps_pending` int(11) DEFAULT NULL,
  `apps_running` int(11) DEFAULT NULL,
  `apps_succeeded` int(11) DEFAULT NULL,
  `apps_failed` int(11) DEFAULT NULL,
  `created_by` varchar(45) DEFAULT NULL,
  `account` varchar(45) DEFAULT NULL,
  `cluster_create_timestamp` datetime DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  `segment` varchar(50) DEFAULT NULL
);
