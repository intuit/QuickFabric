--
-- Table structure for table `emr_billing_component_cost_hist`
--
CREATE TABLE IF NOT EXISTS `emr_billing_component_cost_hist` (
  `emr_name` varchar(45) NOT NULL,
  `emr_cost` int(11) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP
);
