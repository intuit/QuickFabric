--
-- Table structure for table `aws_account_profile`
--

CREATE TABLE IF NOT EXISTS `aws_account_profile` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` varchar(256) NOT NULL,
  `account_env` varchar(256) NOT NULL,
  `account_owner` varchar(256) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `account_id_UNIQUE` (`account_id`)
) AUTO_INCREMENT=200000;
