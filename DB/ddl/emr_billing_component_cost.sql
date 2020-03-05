--
-- Table structure for table `emr_billing_component_cost`
--
CREATE TABLE IF NOT EXISTS `emr_billing_component_cost` (
  `emr_name` varchar(45) NOT NULL,
  `emr_cost` int(11) DEFAULT NULL,
  PRIMARY KEY (`emr_name`)
);
