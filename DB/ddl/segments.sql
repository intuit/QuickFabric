--
-- Table structure for table `segments`
--
CREATE TABLE IF NOT EXISTS `segments` (
  `segment_id` int(11) NOT NULL AUTO_INCREMENT,
  `segment_name` varchar(128) NOT NULL,
  `business_owner` varchar(128) DEFAULT NULL,
  `business_owner_email` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`segment_id`)
) AUTO_INCREMENT=300000;
