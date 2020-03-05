DROP DATABASE IF EXISTS `quickfabric`;
CREATE DATABASE IF NOT EXISTS `quickfabric` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `quickfabric`;
-- MySQL dump 10.13  Distrib 8.0.17, for macos10.14 (x86_64)
--
-- Host: 127.0.0.1    Database: quickfabric
-- ------------------------------------------------------
-- Server version	5.7.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `db_patch`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `db_patch` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `patch_name` varchar(128) NOT NULL,
  `patch_status` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `patch_name_UNIQUE` (`patch_name`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `aws_account_profile`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `aws_account_profile` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` varchar(256) NOT NULL,
  `account_env` varchar(256) NOT NULL,
  `account_owner` varchar(256) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `account_id_UNIQUE` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200003 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aws_account_profile`
--

LOCK TABLES `aws_account_profile` WRITE;
/*!40000 ALTER TABLE `aws_account_profile` DISABLE KEYS */;
INSERT INTO `aws_account_profile` VALUES (200000,'100000000000','Prod','Socrates'),(200001,'200000000000','Prod','Plato'),(200002,'300000000000','Dev','Aristotle');
/*!40000 ALTER TABLE `aws_account_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cluster_metrics`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `cluster_metrics` (
  `emr_id` varchar(512) NOT NULL DEFAULT '',
  `rm_url` varchar(1024) DEFAULT NULL,
  `refresh_timestamp` datetime DEFAULT NULL,
  `metrics_json` text,
  `emr_status` varchar(100) DEFAULT NULL,
  `memory_usage_pct` float DEFAULT NULL,
  `cores_usage_pct` float DEFAULT NULL,
  `emr_name` varchar(255) DEFAULT NULL,
  `total_nodes` int(11) DEFAULT NULL,
  `containers_pending` int(11) DEFAULT NULL,
  `apps_pending` int(11) DEFAULT NULL,
  `apps_running` int(11) DEFAULT NULL,
  `apps_succeeded` int(11) DEFAULT NULL,
  `apps_failed` int(11) DEFAULT NULL,
  `created_by` varchar(45) DEFAULT NULL,
  `account` varchar(45) DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  `cluster_create_timestamp` datetime DEFAULT NULL,
  `segment` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cluster_metrics`
--

LOCK TABLES `cluster_metrics` WRITE;
/*!40000 ALTER TABLE `cluster_metrics` DISABLE KEYS */;
INSERT INTO `cluster_metrics` (
  `emr_id`,
  `rm_url`,
  `refresh_timestamp`,
  `emr_status`,
  `memory_usage_pct`,
  `cores_usage_pct`,
  `emr_name`,
  `total_nodes`,
  `containers_pending`,
  `apps_pending`,
  `apps_running`,
  `apps_succeeded`,
  `apps_failed`,
  `created_by`,
  `account`,
  `type`,
  `cluster_create_timestamp`,
  `segment`) VALUES
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',CURRENT_TIMESTAMP(),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',CURRENT_TIMESTAMP(),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',CURRENT_TIMESTAMP(),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales');
/*!40000 ALTER TABLE `cluster_metrics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cluster_metrics_history`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `cluster_metrics_history` (
  `emr_id` varchar(512) NOT NULL DEFAULT '',
  `rm_url` varchar(1024) DEFAULT NULL,
  `refresh_timestamp` datetime DEFAULT NULL,
  `metrics_json` text,
  `emr_status` varchar(100) DEFAULT NULL,
  `memory_usage_pct` float DEFAULT NULL,
  `cores_usage_pct` float DEFAULT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cluster_metrics_history`
--

LOCK TABLES `cluster_metrics_history` WRITE;
/*!40000 ALTER TABLE `cluster_metrics_history` DISABLE KEYS */;
INSERT INTO `cluster_metrics_history` (
  `emr_id`,
  `rm_url`,
  `refresh_timestamp`,
  `emr_status`,
  `memory_usage_pct`,
  `cores_usage_pct`,
  `emr_name`,
  `total_nodes`,
  `containers_pending`,
  `apps_pending`,
  `apps_running`,
  `apps_succeeded`,
  `apps_failed`,
  `created_by`,
  `account`,
  `type`,
  `cluster_create_timestamp`,
  `segment`) VALUES
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',CURRENT_TIMESTAMP(),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',CURRENT_TIMESTAMP(),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',CURRENT_TIMESTAMP(),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -10 MINUTE),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -10 MINUTE),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -10 MINUTE),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -20 MINUTE),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -20 MINUTE),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -20 MINUTE),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -30 MINUTE),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -30 MINUTE),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -30 MINUTE),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -40 MINUTE),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -40 MINUTE),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -40 MINUTE),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -50 MINUTE),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -50 MINUTE),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -50 MINUTE),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -1 HOUR),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -1 HOUR),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -1 HOUR),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -2 HOUR),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -2 HOUR),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -2 HOUR),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -3 HOUR),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -3 HOUR),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -3 HOUR),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -4 HOUR),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -4 HOUR),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -4 HOUR),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -5 HOUR),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -5 HOUR),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -5 HOUR),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -6 HOUR),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -6 HOUR),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -6 HOUR),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -7 HOUR),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -7 HOUR),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -7 HOUR),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -8 HOUR),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -8 HOUR),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -8 HOUR),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -9 HOUR),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -9 HOUR),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -9 HOUR),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -10 HOUR),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -10 HOUR),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -10 HOUR),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -11 HOUR),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -11 HOUR),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -11 HOUR),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -12 HOUR),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -12 HOUR),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -12 HOUR),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -1 DAY),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -1 DAY),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -1 DAY),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -2 DAY),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -2 DAY),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -2 DAY),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -3 DAY),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -3 DAY),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -3 DAY),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -4 DAY),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -4 DAY),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -4 DAY),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -5 DAY),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -5 DAY),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -5 DAY),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -6 DAY),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -6 DAY),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -6 DAY),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales'),
  ('j-123ABC1IABCX1','http://ip-00-00-000-00.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -7 DAY),'WAITING',0,0,'exploratory-sales',20,0,0,0,0,0,'QuickFabric User','100000000000','exploratory','2020-01-01 15:13:32','sales'),
  ('j-234XYZ2HXYZY2','http://ip-11-11-111-11.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -7 DAY),'WAITING',83.3,68.8,'scheduled-sales',37,1000,1,5,2,1,'QuickFabric User','100000000000','scheduled','2020-01-02 20:14:20','sales'),
  ('j-456QWE3JQWEZ3','http://ip-22-22-222-22.us-west-2.compute.internal:8088',DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL -7 DAY),'WAITING',77.1,60.2,'exploratory-sales-test1',17,0,0,0,0,0,'QuickFabric User','200000000000','exploratory','2020-01-03 08:00:17','sales');

/*!40000 ALTER TABLE `cluster_metrics_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cluster_step_request`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `cluster_step_request` (
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cluster_step_request`
--

LOCK TABLES `cluster_step_request` WRITE;
/*!40000 ALTER TABLE `cluster_step_request` DISABLE KEYS */;
/*!40000 ALTER TABLE `cluster_step_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reports`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `reports` (
  `report_id` int(11) NOT NULL AUTO_INCREMENT,
  `report_name` varchar(256) NOT NULL,
  PRIMARY KEY (`report_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reports`
--

LOCK TABLES `reports` WRITE;
/*!40000 ALTER TABLE `reports` DISABLE KEYS */;
INSERT INTO `reports` VALUES (1,'AMI Rotation'),(2,'Cluster Metrics');
/*!40000 ALTER TABLE `reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `roles` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(128) NOT NULL,
  `service_id` int(11) NOT NULL,
  PRIMARY KEY (`role_id`),
  KEY `role_service_foreign_key_idx` (`service_id`),
  CONSTRAINT `role_to_services_foreign_key` FOREIGN KEY (`service_id`) REFERENCES `services` (`service_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=400009 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES
(400000,'CreateCluster',500000),
(400001,'TerminateCluster',500000),
(400002,'AddStep',500000),
(400003,'RotateAMI',500000),
(400004,'FlipDNS',500000),
(400005,'CloneCluster',500000),
(400006,'Admin',500000),
(400007,'Read',500000),
(400008,'runclusterhealthchecks',500000);
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `segments`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `segments` (
  `segment_id` int(11) NOT NULL AUTO_INCREMENT,
  `segment_name` varchar(128) NOT NULL,
  `business_owner` varchar(128) DEFAULT NULL,
  `business_owner_email` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`segment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=300001 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `segments`
--

LOCK TABLES `segments` WRITE;
/*!40000 ALTER TABLE `segments` DISABLE KEYS */;
INSERT INTO `segments` VALUES (300000,'sales','The Boss','theboss@company.com');
/*!40000 ALTER TABLE `segments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `services` (
  `service_id` int(11) NOT NULL AUTO_INCREMENT,
  `service_type` varchar(45) NOT NULL,
  PRIMARY KEY (`service_id`)
) ENGINE=InnoDB AUTO_INCREMENT=500001 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services`
--

LOCK TABLES `services` WRITE;
/*!40000 ALTER TABLE `services` DISABLE KEYS */;
INSERT INTO `services` VALUES (500000,'EMR');
/*!40000 ALTER TABLE `services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emr_billing_component_cost`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `emr_billing_component_cost` (
  `emr_name` varchar(45) NOT NULL,
  `emr_cost` int(11) DEFAULT NULL,
  PRIMARY KEY (`emr_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emr_billing_component_cost`
--

LOCK TABLES `emr_billing_component_cost` WRITE;
/*!40000 ALTER TABLE `emr_billing_component_cost` DISABLE KEYS */;
INSERT INTO `emr_billing_component_cost` VALUES ('exploratory-sales',700),('exploratory-sales-test1',500),('scheduled-sales',1000);
/*!40000 ALTER TABLE `emr_billing_component_cost` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emr_billing_component_cost_hist`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `emr_billing_component_cost_hist` (
  `emr_name` varchar(45) NOT NULL,
  `emr_cost` int(11) DEFAULT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emr_billing_component_cost_hist`
--

LOCK TABLES `emr_billing_component_cost_hist` WRITE;
/*!40000 ALTER TABLE `emr_billing_component_cost_hist` DISABLE KEYS */;
INSERT INTO `emr_billing_component_cost_hist` (`emr_name`, `emr_cost`, `created_date`) VALUES
('exploratory-sales',700, CURRENT_TIMESTAMP()),
('scheduled-sales',1000, CURRENT_TIMESTAMP()),
('exploratory-sales-test1',500, CURRENT_TIMESTAMP()),
('exploratory-sales',700, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -1 DAY)),
('scheduled-sales',1000, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -1 DAY)),
('exploratory-sales-test1',500, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -1 DAY)),
('exploratory-sales',700, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -2 DAY)),
('scheduled-sales',1000, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -2 DAY)),
('exploratory-sales-test1',500, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -2 DAY)),
('exploratory-sales',700, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -3 DAY)),
('scheduled-sales',1000, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -3 DAY)),
('exploratory-sales-test1',500, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -3 DAY)),
('exploratory-sales',700, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -4 DAY)),
('scheduled-sales',1000, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -4 DAY)),
('exploratory-sales-test1',500,DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -4 DAY)),
('exploratory-sales',700, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -5 DAY)),
('scheduled-sales',1000, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -5 DAY)),
('exploratory-sales-test1',500, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -5 DAY)),
('exploratory-sales',700, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -6 DAY)),
('scheduled-sales',1000, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -6 DAY)),
('exploratory-sales-test1',500, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -6 DAY));
/*!40000 ALTER TABLE `emr_billing_component_cost_hist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emr_cluster_metadata`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `emr_cluster_metadata` (
  `metadata_id` int(11) NOT NULL AUTO_INCREMENT,
  `cluster_id` varchar(45) DEFAULT NULL,
  `cluster_name` varchar(128) DEFAULT NULL,
  `type` varchar(45) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `master_ip` varchar(45) DEFAULT NULL,
  `rm_url` varchar(128) DEFAULT NULL,
  `message` text,
  `api_request_id` varchar(45) DEFAULT NULL,
  `lambda_request_id` varchar(45) DEFAULT NULL,
  `status_code` varchar(20) DEFAULT NULL,
  `last_updated_timestamp` datetime DEFAULT NULL,
  `creation_request_timestamp` datetime DEFAULT NULL,
  `do_terminate` tinyint(1) DEFAULT '0',
  `cluster_details` text,
  `created_by` varchar(128) DEFAULT NULL,
  `last_updated_by` varchar(55) DEFAULT NULL,
  `account` varchar(45) DEFAULT NULL,
  `headless_users` varchar(500) DEFAULT NULL,
  `segment` varchar(45) DEFAULT NULL,
  `dns_name` varchar(500) DEFAULT NULL,
  `dns_flip` tinyint(1) DEFAULT '0',
  `is_prod` tinyint(1) DEFAULT '0',
  `dns_flip_completed` tinyint(1) DEFAULT '0',
  `original_cluster_id` varchar(50) DEFAULT NULL,
  `request_ticket` varchar(45) DEFAULT NULL,
  `new_cluster_id` varchar(200) DEFAULT NULL,
  `ami_rotation_days_togo` int(11) DEFAULT '0',
  `auto_ami_rotation` tinyint(1) DEFAULT '0',
  `autopilot_window_start` int(11) DEFAULT '0',
  `autopilot_window_end` int(11) DEFAULT '24',
  `ami_rotation_sla_days` int(11) DEFAULT '30',
  `autoscaling_instance_group` varchar(45) DEFAULT NULL,
  `autoscaling_min` int(11) DEFAULT '0',
  `autoscaling_max` int(11) DEFAULT '0',
  PRIMARY KEY (`metadata_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emr_cluster_metadata`
--

LOCK TABLES `emr_cluster_metadata` WRITE;
/*!40000 ALTER TABLE `emr_cluster_metadata` DISABLE KEYS */;
INSERT INTO `emr_cluster_metadata` (
  `cluster_id`,
  `cluster_name`,
  `type`,
  `status`,
  `master_ip`,
  `rm_url`,
  `message`,
  `api_request_id`,
  `lambda_request_id`,
  `status_code`,
  `last_updated_timestamp`,
  `creation_request_timestamp`,
  `do_terminate`,
  `created_by`,
  `last_updated_by`,
  `account`,
  `headless_users`,
  `segment`,
  `dns_name`,
  `dns_flip`,
  `is_prod`,
  `dns_flip_completed`,
  `original_cluster_id`,
  `request_ticket`,
  `new_cluster_id`,
  `ami_rotation_days_togo`,
  `auto_ami_rotation`,
  `autopilot_window_start`,
  `autopilot_window_end`,
  `ami_rotation_sla_days`,
  `autoscaling_instance_group`,
  `autoscaling_min`,
  `autoscaling_max`) VALUES
('j-123ABC1IABCX1','exploratory-sales','exploratory','WAITING','0.0.0.0','http://ip-0-0-0-0.us-west-2.compute.internal:8088','Cluster ready after last step completed.','xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx','yyyyyyyyy-yyyy-yyyy-yyyy-yyyyyyyyyyyy','201','2019-12-13 12:00:00','2020-01-01 15:13:32','True','QuickFabric User',NULL,'100000000000','','sales','exploratory-sales.somedomain.company.com',0,0,0,NULL,'DATA-23094',NULL,0,0,0,24,30,NULL,0,0),
('j-234XYZ2HXYZY2','scheduled-sales','scheduled','WAITING','1.1.1.1','http://ip-1-1-1-1.us-west-2.compute.internal:8088','Cluster ready after last step completed.','xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx','yyyyyyyyy-yyyy-yyyy-yyyy-yyyyyyyyyyyy','201','2019-12-13 12:00:00','2020-01-02 20:14:20','True','QuickFabric User',NULL,'100000000000','','sales','scheduled-sales.processing-sbg-prd.company.com',0,0,0,NULL,'DATA-23095',NULL,0,0,0,24,30,'Core',1,5),
('j-456QWE3JQWEZ3','exploratory-sales-test1','exploratory','WAITING','2.2.2.2','http://ip-2-2-2-2.us-west-2.compute.internal:8088','Cluster ready after last step completed.','xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx','yyyyyyyyy-yyyy-yyyy-yyyy-yyyyyyyyyyyy','201','2019-12-13 12:00:00','2020-01-03 08:00:17','True','QuickFabric User',NULL,'200000000000','','sales','exploratory-sales-test1.company.com',0,0,0,NULL,'DATA-23096',NULL,0,0,0,24,30,'Task',1,5),
('j-567ASD4KASDA0','exploratory-sales','exploratory','TERMINATED','3.3.3.3','http://ip-3-3-3-3.us-west-2.compute.internal:8088','EMR cluster already in TERMINATED state','xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx','yyyyyyyyy-yyyy-yyyy-yyyy-yyyyyyyyyyyy','201','2019-12-13 12:00:00','2019-12-31 07:00:00','True','QuickFabric User',NULL,'100000000000','','sales','exploratory-sales.company.com',0,0,0,NULL,'DATA-23093','j-123ABC1IABCX1',0,0,0,24,30,'Core',1,10);
/*!40000 ALTER TABLE `emr_cluster_metadata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emr_functional_testsuites`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `emr_functional_testsuites` (
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emr_functional_testsuites`
--

LOCK TABLES `emr_functional_testsuites` WRITE;
/*!40000 ALTER TABLE `emr_functional_testsuites` DISABLE KEYS */;
/*!40000 ALTER TABLE `emr_functional_testsuites` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emr_functional_testsuites_status`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `emr_functional_testsuites_status` (
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emr_functional_testsuites_status`
--

LOCK TABLES `emr_functional_testsuites_status` WRITE;
/*!40000 ALTER TABLE `emr_functional_testsuites_status` DISABLE KEYS */;
/*!40000 ALTER TABLE `emr_functional_testsuites_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emr_functional_testsuites_status_history`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `emr_functional_testsuites_status_history` (
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emr_functional_testsuites_status_history`
--

LOCK TABLES `emr_functional_testsuites_status_history` WRITE;
/*!40000 ALTER TABLE `emr_functional_testsuites_status_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `emr_functional_testsuites_status_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_subscriptions`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `report_subscriptions` (
  `report_subscription_id` int(11) NOT NULL AUTO_INCREMENT,
  `report_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`report_subscription_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_subscriptions`
--

LOCK TABLES `report_subscriptions` WRITE;
/*!40000 ALTER TABLE `report_subscriptions` DISABLE KEYS */;
/*!40000 ALTER TABLE `report_subscriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `email_id` varchar(50) NOT NULL,
  `first_name` varchar(20) NOT NULL,
  `last_name` varchar(20) NOT NULL,
  `creation_date` varchar(50) NOT NULL,
  `passcode` varchar(60) NOT NULL,
  `super_admin` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'user@company.com','QuickFabric','User','2019-06-11 17:41:06','$2a$10$nGJMv4QsvfqBxW269wvgSepuzpJNX0IbZatlGjvEVXfsjkdDCCRsK',1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_account_segment_mapping`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `user_account_segment_mapping` (
  `user_account_segment_mapping_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `aws_account_id` int(11) NOT NULL,
  `segment_id` int(11) NOT NULL,
  PRIMARY KEY (`user_account_segment_mapping_id`),
  UNIQUE KEY `unique_user_account_segment` (`user_id`,`aws_account_id`,`segment_id`),
  KEY `user_foreign_key_idx` (`user_id`),
  KEY `account_foreign_key_idx` (`aws_account_id`),
  KEY `segment_foregin_key_idx` (`segment_id`),
  CONSTRAINT `aws_account_foreign_key` FOREIGN KEY (`aws_account_id`) REFERENCES `aws_account_profile` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `segment_foregin_key` FOREIGN KEY (`segment_id`) REFERENCES `segments` (`segment_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_foreign_key` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=600001 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_account_segment_mapping`
--

LOCK TABLES `user_account_segment_mapping` WRITE;
/*!40000 ALTER TABLE `user_account_segment_mapping` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_account_segment_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_account_segment_role_mapping`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `user_account_segment_role_mapping` (
  `user_account_segment_role_mapping_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_account_segment_mapping_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_account_segment_role_mapping_id`),
  KEY `action_mapping_key_idx` (`role_id`),
  KEY `account_segment_mapping_reference_key_idx` (`user_account_segment_mapping_id`),
  CONSTRAINT `roles_reference_key` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_account_segment_mapping_reference_key` FOREIGN KEY (`user_account_segment_mapping_id`) REFERENCES `user_account_segment_mapping` (`user_account_segment_mapping_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1000000 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_account_segment_role_mapping`
--

LOCK TABLES `user_account_segment_role_mapping` WRITE;
/*!40000 ALTER TABLE `user_account_segment_role_mapping` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_account_segment_role_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workflow`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS  `workflow` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `workflow_name` varchar(45) NOT NULL,
  `workflow_step` varchar(45) NOT NULL,
  `lookup_table` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workflow`
--

LOCK TABLES `workflow` WRITE;
/*!40000 ALTER TABLE `workflow` DISABLE KEYS */;
INSERT INTO `workflow` (`id`, `workflow_name`, `workflow_step`, `lookup_table`)VALUES
(1,'CreateCluster','create_new_cluster','emr_cluster_metadata'),
(2,'CreateCluster','cluster_bootstraps','cluster_step_request'),
(3,'CreateCluster','cluster_custom_steps','cluster_step_request'),
(4,'CreateCluster','health_check','emr_functional_testsuites_status'),
(5,'RotateAMI-NonHA','terminate_current_cluster','emr_cluster_metadata'),
(6,'RotateAMI-NonHA','create_new_cluster','emr_cluster_metadata'),
(7,'RotateAMI-NonHA','cluster_bootstraps','cluster_step_request'),
(8,'RotateAMI-NonHA','cluster_custom_steps','cluster_step_request'),
(9,'RotateAMI-NonHA','health_check','emr_functional_testsuites_status'),
(10,'RotateAMI-HA','create_new_cluster','emr_cluster_metadata'),
(11,'RotateAMI-HA','cluster_bootstraps','cluster_step_request'),
(12,'RotateAMI-HA','cluster_custom_steps','cluster_step_request'),
(13,'RotateAMI-HA','health_check','emr_functional_testsuites_status'),
(14,'RotateAMi-HA','mark_current_cluster_for_termination','emr_cluster_metadata');
/*!40000 ALTER TABLE `workflow` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

--
-- Table structure for table `configuration_data_types`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `configuration_data_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `data_type_name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `data_type_name_UNIQUE` (`data_type_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuration_data_types`
--

LOCK TABLES `configuration_data_types` WRITE;
/*!40000 ALTER TABLE `configuration_data_types` DISABLE KEYS */;
INSERT INTO `configuration_data_types` (`id`, `data_type_name`) VALUES
(3,'boolean'),
(4,'date'),
(5,'datetime'),
(6,'decimal'),
(2,'int'),
(7, 'long'),
(1,'string');
/*!40000 ALTER TABLE `configuration_data_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuration_types`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `configuration_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `config_type` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `config_type_UNIQUE` (`config_type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuration_types`
--

LOCK TABLES `configuration_types` WRITE;
/*!40000 ALTER TABLE `configuration_types` DISABLE KEYS */;
INSERT INTO `configuration_types` (`id`, `config_type`) VALUES (2,'Account'),(1,'Application');
/*!40000 ALTER TABLE `configuration_types` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Table structure for table `configuration_definitions`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `configuration_definitions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `config_name` varchar(128) NOT NULL,
  `config_description` varchar(256) NOT NULL,
  `config_type` int(11) NOT NULL,
  `data_type` int(11) NOT NULL DEFAULT '1',
  `encryption_required` tinyint(4) NOT NULL DEFAULT '0',
  `is_mandatory` tinyint(4) NOT NULL DEFAULT '0',
  `is_user_accessible` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `config_name_UNIQUE` (`config_name`),
  KEY `configuration_type_idx` (`config_type`),
  KEY `fk_data_type_idx` (`data_type`),
  CONSTRAINT `fk_configuration_type` FOREIGN KEY (`config_type`) REFERENCES `configuration_types` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_configurations_definition_configuration_data_type` FOREIGN KEY (`data_type`) REFERENCES `configuration_data_types` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
--
-- Dumping data for table `configuration_definitions`
--

LOCK TABLES `configuration_definitions` WRITE;
/*!40000 ALTER TABLE `configuration_definitions` DISABLE KEYS */;
INSERT INTO `configuration_definitions` (`id`, `config_name`, `config_description`, `config_type`, `data_type`, `encryption_required`, `is_mandatory`, `is_user_accessible`) VALUES
(1,'add_steps_to_clusters_scheduler','Add steps to new clusters scheduler',1,3,0,1,0),
(2,'add_step_notifications','Sends email to user who created new step in a cluster',1,3,0,0,0),
(3,'autoscaling_config_test_scheduler','Auto scaling config is verified between servless and test criteria',1,3,0,0,0),
(4,'auto_ami_rotate_scheduler','This will allow rotation of AMI in the background',1,3,0,1,0),
(5,'check_cluster_status_with_termination_initiated_scheduler','Change the status of clusters to terminated if they were pending termination.',1,3,0,1,0),
(6,'collect_cluster_metrics_scheduler','Scheduler will collect cluster metrics from resource manager',1,3,0,1,0),
(7,'connectivity_test_scheduler','This scheduler will perform connectivity on EMR',1,3,0,0,0),
(8,'create_cluster_notifications','Sends out create cluster email notifications',1,3,0,0,0),
(9,'daily_report_scheduler','Generate daily AMI and cluster usage report',1,3,0,0,0),
(10,'dns_flip_notifications','Sends out email notification on DNS flip event',1,3,0,0,0),
(11,'gateway_api_key','Gateway API key to connect with backend',2,1,0,1,0),
(12,'monthly_report_scheduler','Generate monthly cluster usage report',1,3,0,0,0),
(13,'rds_cleanup_scheduler','Cleans up terminated cluster metadata after six months',1,3,0,0,0),
(14,'rotate_ami_notifications','Sends out email notification on rotate AMI event',1,3,0,0,0),
(15,'segment_reports_scheduler','Sends out segment reports to segment leaders',1,3,0,0,0),
(16,'sso_email_key','SSO email key',1,1,0,0,0),
(17,'sso_qbn_authid','SSO authentication id',1,1,0,0,0),
(18,'sso_qbn_ptc_authid','SSO ptc authentication id',1,1,0,0,0),
(19,'sso_qbn_ptc_tkt','SSO ptc ticket',1,1,0,0,0),
(20,'sso_qbn_tkt','SSO ticket',1,1,0,0,0),
(21,'sso_shared_key','SSO key for decrypting SSO data',1,1,0,0,0),
(22,'subscription_reports_scheduler','Scheduler to send out subscribed cluster reports',1,3,0,0,0),
(23,'terminate_cluster_notifications','Sends out email notification on cluster termination event',1,3,0,0,0),
(24,'terminate_completed_clusters_scheduler','Enables auto termination of cluster',1,3,0,1,0),
(25,'testsuites_enabled','Enables or disables test suites at account level',2,3,0,0,0),
(26,'test_cluster_ttl','Value to specify duration after which test clusters will be terminated',1,2,0,0,0),
(27,'validate_cluster_steps_scheduler','Update the status of steps once they are submitted',1,3,0,1,0),
(28,'validate_existing_clusters_scheduler','Update the status of exiting running cluster',1,3,0,1,0),
(29,'validate_new_clusters_scheduler','Update the status of cluster once the cluster is initiated',1,3,0,1,0),
(30,'verify_number_of_bootstraps_scheduler','Verify the number of bootstraps between backend and application',1,3,0,0,0),
(32,'weekly_report_scheduler','Generate weekly cluster usage report',1,3,0,0,0),
(33,'from_email_address','From email address used by application to send out emails',1,1,0,0,0),
(34,'jira_enabled_global','Global switch to turn on/off JIRA validation',1,3,0,0,1),
(35,'jira_enabled_account','Allows JIRA validation for the account. Requires JIRA username, password and url',2,3,0,1,1),
(36,'test_cluster_auto_termination','Allows auto termination of test clusters',2,3,0,0,0),
(37,'collect_cluster_costs','Collects cost of the cluster from AWS cost explorer',1,3,0,1,0),
(38,'gateway_api_url','URL for connecting to backend ',2,1,0,1,0),
(39,'report_recipients','Email receipents for all reports',1,1,0,0,0),
(40,'servicenow_enabled_global','Global flag to turn on/off service now validation',1,3,0,0,1),
(41,'servicenow_enabled_account','Turns on/off service now validation',2,3,0,1,1),
(42,'sso_url','SSO URL',1,1,0,0,0),
(43,'sso_redirect_url','SSO redirect URL',1,1,0,0,0),
(44,'jira_projects','List of(comma separated) JIRA project names',2,1,0,0,0),
(45,'jira_user','Username to connect with JIRA services',2,1,0,0,0),
(46,'jira_password','Password to connect with JIRA services',2,1,1,0,0),
(47,'jira_url','URL to connect with JIRA services',2,1,0,0,0),
(48,'servicenow_user','Username to connect with Servicenow services',2,1,0,0,0),
(49,'servicenow_password','Password to connect with Servicenow services',2,1,1,0,0),
(50,'servicenow_url','URL to connect with Servicenow services',2,1,0,0,0),
(51,'notification_recipients','Email of recepients for cluster notifications',1,1,0,0,0),
(52,'sso_enabled','Global switch to turn on/off SSO functionality',1,3,0,0,1);
/*!40000 ALTER TABLE `configuration_definitions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `application_configurations`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `application_configurations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `config_id` int(11) NOT NULL,
  `config_value` varchar(128) NOT NULL,
  `is_encrypted` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `config_id_UNIQUE` (`config_id`),
  KEY `fk_configuration_id_idx` (`config_id`),
  CONSTRAINT `fk_global_configuration_id` FOREIGN KEY (`config_id`) REFERENCES `configuration_definitions` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application_configurations`
--

LOCK TABLES `application_configurations` WRITE;
/*!40000 ALTER TABLE `application_configurations` DISABLE KEYS */;
--
-- Dumping data for table `application_configurations`
--

LOCK TABLES `application_configurations` WRITE;
/*!40000 ALTER TABLE `application_configurations` DISABLE KEYS */;
INSERT INTO `application_configurations` (`id`, `config_id`, `config_value`, `is_encrypted`) VALUES
(1,39,'noreply@intuit.com',0),
(2,34,'true',0),
(3,1,'true',0),
(4,4,'true',0),
(5,5,'true',0),
(6,6,'true',0),
(7,24,'true',0),
(8,27,'true',0),
(9,28,'true',0),
(10,29,'true',0),
(11,37,'true',0),
(12,26,'2',0),
(14,3,'true',0),
(15,13,'true',0),
(16,2,'true',0),
(17,7,'false',0),
(18,8,'true',0),
(19,9,'true',0),
(20,10,'true',0),
(21,12,'true',0),
(22,14,'true',0),
(23,15,'true',0),
(24,22,'true',0),
(25,30,'true',0),
(27,32,'true',0),
(28,40,'true',0),
(29,52,'false',0),
(30,23,'true',0),
(31,33,'noreply@intuit.com',0),
(32,51,'noreply@intuit.com',0);
/*!40000 ALTER TABLE `application_configurations` ENABLE KEYS */;
UNLOCK TABLES;

/*!40000 ALTER TABLE `application_configurations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_configurations`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `account_configurations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `config_id` int(11) NOT NULL,
  `account_id` varchar(128) NOT NULL,
  `config_value` varchar(128) NOT NULL,
  `is_encrypted` varchar(45) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `composite_unique_config_id_and_account_id` (`config_id`,`account_id`),
  KEY `fk_configuration_id_idx` (`config_id`),
  KEY `fk_account_id_idx` (`account_id`),
  CONSTRAINT `fk_account_id` FOREIGN KEY (`account_id`) REFERENCES `aws_account_profile` (`account_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_configuration_id` FOREIGN KEY (`config_id`) REFERENCES `configuration_definitions` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_configurations`
--

LOCK TABLES `account_configurations` WRITE;
/*!40000 ALTER TABLE `account_configurations` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_configurations` ENABLE KEYS */;
UNLOCK TABLES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


CREATE USER 'qf_admin'@'%' IDENTIFIED BY 'supersecret';
GRANT ALL PRIVILEGES ON * . * TO 'qf_admin'@'%';
FLUSH PRIVILEGES;

-- Dump completed on 2020-01-10 11:18:44
