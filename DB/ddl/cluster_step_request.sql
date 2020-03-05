CREATE TABLE IF NOT EXISTS `cluster_step_request` (
  `cluster_name` varchar(100) DEFAULT NULL,
  `cluster_id` varchar(45) DEFAULT NULL,
  `step_id` varchar(45) DEFAULT NULL,
  `api_request_id` varchar(45) DEFAULT NULL,
  `lambda_request_id` varchar(45) DEFAULT NULL,
  `updated_ts` datetime DEFAULT NULL,
  `step_arg` varchar(3000) DEFAULT NULL,
  `created_ts` datetime DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `action_on_failure` varchar(200) DEFAULT NULL,
  `main_class` varchar(45) DEFAULT NULL,
  `jar` varchar(100) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `created_by` varchar(55) DEFAULT NULL,
  `step_type` varchar(50) NOT NULL DEFAULT 'Custom'
);
