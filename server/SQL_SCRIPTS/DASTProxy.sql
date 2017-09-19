-- MySQL dump 10.13  Distrib 5.6.21, for Win64 (x86_64)
--
-- Host: localhost    Database: dast_db
-- ------------------------------------------------------
-- Server version	5.6.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `dast_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `dast_db` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `dast_db`;

--
-- Table structure for table `difference`
--

DROP TABLE IF EXISTS `difference`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `difference` (
  `issue_id` varchar(45) NOT NULL DEFAULT '',
  `report_id` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`issue_id`,`report_id`),
  KEY `report_id_difference_fx_idx` (`report_id`),
  CONSTRAINT `issue_id_difference_fx` FOREIGN KEY (`issue_id`) REFERENCES `issue_variant` (`issue_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fp_reason`
--

DROP TABLE IF EXISTS `fp_reason`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fp_reason` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `abbr` varchar(45) NOT NULL,
  `date_created` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `fp_text_pattern` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1004 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `issue`
--

DROP TABLE IF EXISTS `issue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `issue` (
  `issue_id` varchar(45) NOT NULL,
  `issue_url` varchar(800) DEFAULT NULL,
  `severity` varchar(100) DEFAULT NULL,
  `issue_type` varchar(2000) DEFAULT NULL,
  `test_url` varchar(2000) DEFAULT NULL,
  `ase_report_id` varchar(45) NOT NULL DEFAULT '',
  `jira_key` varchar(45) DEFAULT NULL,
  `fp_comments` varchar(255) DEFAULT NULL,
  `date_created` datetime DEFAULT CURRENT_TIMESTAMP,
  `fp_marked_by` varchar(255) DEFAULT NULL,
  `fp_marked_date` datetime DEFAULT NULL,
  `is_fp` binary(1) DEFAULT '0',
  `scan_engine` varchar(100) DEFAULT 'ASE',
  `fp_reason_id` int(11) DEFAULT NULL,
  `report_id` int(11) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `test_http_traffic` longblob,
  `original_http_traffic` longblob,
  PRIMARY KEY (`id`),
  KEY `report_id_idx` (`ase_report_id`),
  KEY `jira_key_fx_idx` (`jira_key`),
  KEY `report_id_issue_fx` (`report_id`),
  CONSTRAINT `jira_key_fx` FOREIGN KEY (`jira_key`) REFERENCES `jira` (`jira_key`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `report_id_issue_fx` FOREIGN KEY (`report_id`) REFERENCES `report` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17098 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `issue_variant`
--

DROP TABLE IF EXISTS `issue_variant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `issue_variant` (
  `id` varchar(45) NOT NULL,
  `issue_id` varchar(45) NOT NULL DEFAULT '',
  `report_id` varchar(45) NOT NULL,
  PRIMARY KEY (`id`,`issue_id`,`report_id`),
  KEY `issue_id_idx` (`issue_id`),
  KEY `report_id__issue_variant_foregin_key_idx` (`report_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jira`
--

DROP TABLE IF EXISTS `jira`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jira` (
  `jira_key` varchar(45) NOT NULL,
  `self` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`jira_key`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `proxy`
--

DROP TABLE IF EXISTS `proxy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `proxy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proxy_address` varchar(45) DEFAULT NULL,
  `proxy_port` int(11) DEFAULT NULL,
  `newly_created` binary(1) DEFAULT NULL,
  `htd_file_name` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `proxy_entity`
--

DROP TABLE IF EXISTS `proxy_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `proxy_entity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proxy_server` blob,
  `proxyIdentifier` varchar(45) NOT NULL,
  `testCaseName` varchar(500) DEFAULT NULL,
  `testCaseSuiteName` varchar(500) DEFAULT NULL,
  `errorMessage` varchar(500) DEFAULT NULL,
  `proxy_id` int(11) DEFAULT NULL,
  `scan_configuration_id` int(11) DEFAULT NULL,
  `user_id` varchar(45) NOT NULL,
  `testCasePackageName` varchar(500) DEFAULT NULL,
  `testCaseTagName` varchar(500) DEFAULT NULL,
  `testCaseClassName` varchar(500) DEFAULT NULL,
  `testsuiteDynamicIdentifier` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `proxy_id_idx` (`proxy_id`),
  KEY `scan_configuration_id_idx` (`scan_configuration_id`),
  KEY `user_id_idx` (`user_id`),
  CONSTRAINT `proxy_id` FOREIGN KEY (`proxy_id`) REFERENCES `proxy` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `scan_configuration_id` FOREIGN KEY (`scan_configuration_id`) REFERENCES `scan_configuration` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recording`
--

DROP TABLE IF EXISTS `recording`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recording` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `owner` varchar(45) NOT NULL,
  `har_filename` varchar(2400) DEFAULT NULL,
  `htd_filename` varchar(2400) DEFAULT NULL,
  `is_enabled` binary(1) DEFAULT '1',
  `date_created` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `testcase_name` varchar(255) DEFAULT NULL,
  `testsuite_name` varchar(255) DEFAULT NULL,
  `testsuite_dynamic_identifier` varchar(255) DEFAULT NULL,
  `is_breeze` binary(1) DEFAULT NULL,
  `recording_batch_id` int(11) DEFAULT NULL,
  `testsuite_package` varchar(250) DEFAULT NULL,
  `scan_policy_id` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=386 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recording_batch`
--

DROP TABLE IF EXISTS `recording_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recording_batch` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `testsuite_name` varchar(45) NOT NULL,
  `testsuite_dynamic_identifier` varchar(255) DEFAULT NULL,
  `is_manual_test_batch` binary(1) DEFAULT '0',
  `owner` varchar(45) NOT NULL,
  `is_enabled` binary(1) DEFAULT '1',
  `date_created` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_nightly_batch` binary(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report` (
  `report_id` varchar(45) NOT NULL,
  `report_last_run` varchar(45) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=942 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `request_modification`
--

DROP TABLE IF EXISTS `request_modification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `request_modification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `modified_value` blob,
  `original_value` blob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scan`
--

DROP TABLE IF EXISTS `scan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scan` (
  `scan_id` varchar(45) DEFAULT NULL,
  `scan_name` varchar(1000) DEFAULT NULL,
  `scan_state` varchar(45) NOT NULL,
  `scan_last_run` varchar(45) DEFAULT NULL,
  `email_sent` binary(1) DEFAULT '0',
  `ase_report_id` varchar(45) DEFAULT NULL,
  `first_set_up` datetime DEFAULT NULL,
  `user_id` varchar(45) NOT NULL,
  `set_up_via_bluefin` binary(1) DEFAULT '0',
  `test_case_name` varchar(500) NOT NULL,
  `test_suite_name` varchar(500) DEFAULT NULL,
  `to_be_tracked` binary(1) DEFAULT '1',
  `scan_recording_id` int(11) DEFAULT NULL,
  `testsuite_dynamic_identifier` varchar(255) DEFAULT NULL,
  `breeze_unique_timestamp` bigint(15) DEFAULT NULL,
  `scan_batch_id` int(11) DEFAULT NULL,
  `testsuite_package` varchar(250) DEFAULT NULL,
  `zap_status` varchar(20) DEFAULT 'New',
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `report_id` int(11) DEFAULT NULL,
  `suspended_reason` varchar(1000) DEFAULT NULL,
  `is_nightly_scan` binary(1) DEFAULT '0',
  `nightly_state` int(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_id_idx` (`user_id`),
  KEY `report_id_idx` (`ase_report_id`),
  KEY `report_id_scan_fx` (`report_id`),
  CONSTRAINT `report_id_scan_fx` FOREIGN KEY (`report_id`) REFERENCES `report` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_id_key` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=859 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scan_batch`
--

DROP TABLE IF EXISTS `scan_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scan_batch` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `testsuite_name` varchar(45) NOT NULL,
  `recording_batch_id` int(11) NOT NULL,
  `is_subset_of_batch` binary(1) DEFAULT '0',
  `owner` varchar(45) NOT NULL,
  `date_created` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_nightly_batch` binary(1) DEFAULT '0',
  `nightly_batch_state` int(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `recording_batch_id_fk` (`recording_batch_id`),
  CONSTRAINT `scan_batch_ibfk_1` FOREIGN KEY (`recording_batch_id`) REFERENCES `recording_batch` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=546 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scan_configuration`
--

DROP TABLE IF EXISTS `scan_configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scan_configuration` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `start_scan` binary(1) DEFAULT NULL,
  `name_of_scan` varchar(500) DEFAULT NULL,
  `scan_policy` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scan_policy`
--

DROP TABLE IF EXISTS `scan_policy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scan_policy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `scan_policy_type` int(11) NOT NULL,
  `date_created` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `last_modified_by` varchar(25) DEFAULT 'SYSTEM',
  `scan_templates_csv` varchar(250) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_scan_policy_type` (`scan_policy_type`),
  CONSTRAINT `fk_scan_policy_type` FOREIGN KEY (`scan_policy_type`) REFERENCES `scan_policy_types` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scan_policy_item`
--

DROP TABLE IF EXISTS `scan_policy_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scan_policy_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `scan_policy_item_name` varchar(100) NOT NULL,
  `scan_policy_item_type` enum('DEVELOPMENT','SERVER','DB','ATTACK_VECTOR') DEFAULT NULL,
  `scan_policy_item_abbr` varchar(100) NOT NULL,
  `scan_templates_abbr` varchar(200) NOT NULL,
  `date_created` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `last_modified_by` varchar(25) DEFAULT 'SYSTEM',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scan_policy_item_mapping`
--

DROP TABLE IF EXISTS `scan_policy_item_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scan_policy_item_mapping` (
  `scan_policy_id` int(11) NOT NULL,
  `scan_policy_item_id` int(11) NOT NULL,
  `date_created` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified_by` varchar(25) DEFAULT 'SYSTEM',
  PRIMARY KEY (`scan_policy_id`,`scan_policy_item_id`),
  KEY `fk_scan_policy_item_id` (`scan_policy_item_id`),
  CONSTRAINT `fk_scan_policy_id` FOREIGN KEY (`scan_policy_id`) REFERENCES `scan_policy` (`id`),
  CONSTRAINT `fk_scan_policy_item_id` FOREIGN KEY (`scan_policy_item_id`) REFERENCES `scan_policy_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scan_policy_types`
--

DROP TABLE IF EXISTS `scan_policy_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scan_policy_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `scan_policy_name` varchar(100) NOT NULL,
  `scan_policy_abbr` varchar(20) NOT NULL,
  `date_created` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `last_modified_by` varchar(25) DEFAULT 'SYSTEM',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scan_template`
--

DROP TABLE IF EXISTS `scan_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scan_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `attack_vector_name` varchar(100) NOT NULL,
  `attack_vector_abbr` varchar(20) NOT NULL,
  `native_template_id` varchar(100) NOT NULL,
  `native_template_name` varchar(50) NOT NULL,
  `date_created` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `last_modified_by` varchar(25) DEFAULT 'SYSTEM',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `traffic`
--

DROP TABLE IF EXISTS `traffic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `traffic` (
  `test_http_traffic` longblob,
  `original_http_traffic` longblob,
  `issue_id` varchar(45) NOT NULL DEFAULT '',
  `report_id` varchar(45) NOT NULL DEFAULT '',
  `issue_variant_id` varchar(45) NOT NULL,
  PRIMARY KEY (`issue_id`,`report_id`,`issue_variant_id`),
  KEY `issue_id_traffic_fx_idx` (`issue_id`),
  KEY `report_id_traffic_fx_idx` (`report_id`),
  CONSTRAINT `issue_id_traffic_fx` FOREIGN KEY (`issue_id`) REFERENCES `issue_variant` (`issue_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `user_id` varchar(45) NOT NULL,
  `enable_email_for_automated_scan` binary(1) DEFAULT '0',
  `appscan_userid` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-09-19 14:36:40
