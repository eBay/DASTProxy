CREATE DATABASE  IF NOT EXISTS `dast_db` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `dast_db`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win32 (x86)
--
-- Host: localhost    Database: dast_db
-- ------------------------------------------------------
-- Server version	5.6.20

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
-- Table structure for table `issue`
--

DROP TABLE IF EXISTS `issue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `issue` (
  `issue_id` varchar(45) NOT NULL,
  `issue_url` varchar(200) DEFAULT NULL,
  `severity` varchar(45) DEFAULT NULL,
  `issue_type` varchar(45) DEFAULT NULL,
  `test_url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`issue_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `issue_variant`
--

DROP TABLE IF EXISTS `issue_variant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `issue_variant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `issue_variant_value` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
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
  `htd_file_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `proxy_entity`
--

DROP TABLE IF EXISTS `proxy_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `proxy_entity` (
  `proxy_server` blob,
  `proxyIdentifier` varchar(45) NOT NULL,
  `testCaseName` varchar(45) DEFAULT NULL,
  `testCaseSuiteName` varchar(45) DEFAULT NULL,
  `errorMessage` varchar(500) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proxy_id` int(11) DEFAULT NULL,
  `scan_configuration_id` int(11) DEFAULT NULL,
  `user_id` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `proxy_id_idx` (`proxy_id`),
  KEY `scan_configuration_id_idx` (`scan_configuration_id`),
  KEY `user_id_idx` (`user_id`),
  CONSTRAINT `proxy_id` FOREIGN KEY (`proxy_id`) REFERENCES `proxy` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `scan_configuration_id` FOREIGN KEY (`scan_configuration_id`) REFERENCES `scan_configuration` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report` (
  `report_id` varchar(45) NOT NULL,
  PRIMARY KEY (`report_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scan`
--

DROP TABLE IF EXISTS `scan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scan` (
  `scan_id` varchar(45) NOT NULL,
  `scan_last_run` varchar(45) DEFAULT NULL,
  `report_id` varchar(45) DEFAULT NULL,
  `first_set_up` datetime DEFAULT NULL,
  `user_id` varchar(45) NOT NULL,
  `set_up_via_bluefin` binary(1) DEFAULT '0',
  PRIMARY KEY (`scan_id`),
  KEY `user_id_idx` (`user_id`),
  KEY `report_id_idx` (`report_id`),
  CONSTRAINT `report_id` FOREIGN KEY (`report_id`) REFERENCES `report` (`report_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_id_key` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
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
  `name_of_scan` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `user_id` varchar(45) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'dast_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


ALTER TABLE `dast_db`.`issue` 
ADD COLUMN `report_id` VARCHAR(45) NULL AFTER `test_url`,
ADD INDEX `report_id_idx` (`report_id` ASC);
ALTER TABLE `dast_db`.`issue` 
ADD CONSTRAINT `report_id_fx`
  FOREIGN KEY (`report_id`)
  REFERENCES `dast_db`.`report` (`report_id`)
  ON DELETE NO ACTION
  ON UPDATE CASCADE;
  
 
ALTER TABLE `dast_db`.`issue_variant` 
ADD COLUMN `issue_id` VARCHAR(45) NULL AFTER `issue_variant_value`,
ADD INDEX `issue_id_idx` (`issue_id` ASC);
ALTER TABLE `dast_db`.`issue_variant` 
ADD CONSTRAINT `issue_id`
  FOREIGN KEY (`issue_id`)
  REFERENCES `dast_db`.`issue` (`issue_id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
  

ALTER TABLE `dast_db`.`scan`
ADD COLUMN `scan_name` VARCHAR(150) NOT NULL AFTER `scan_id`;
ALTER TABLE `dast_db`.`scan`
ADD COLUMN `scan_state` VARCHAR(45) NOT NULL AFTER `scan_name`;
ALTER TABLE `dast_db`.`scan`
ADD COLUMN `email_sent` bool DEFAULT FALSE AFTER `scan_last_run`;
ALTER TABLE `dast_db`.`scan`
ADD COLUMN `test_case_name` VARCHAR(45) NOT NULL AFTER `set_up_via_bluefin`;
ALTER TABLE `dast_db`.`scan`
ADD COLUMN `test_suite_name` VARCHAR(45) AFTER `test_case_name`;
ALTER TABLE `dast_db`.`scan`
ADD COLUMN `to_be_tracked` bool DEFAULT TRUE AFTER `test_suite_name`;

ALTER TABLE `dast_db`.`report`
ADD COLUMN `report_last_run` VARCHAR(45) AFTER `report_id`;

ALTER TABLE `dast_db`.`scan` 
CHANGE COLUMN `email_sent` `email_sent` BINARY NULL DEFAULT '0' ,
CHANGE COLUMN `to_be_tracked` `to_be_tracked` BINARY NULL DEFAULT '1' ;

ALTER TABLE `dast_db`.`proxy_entity` 
CHANGE COLUMN `testCaseName` `testCaseName` VARCHAR(500) NULL DEFAULT NULL ,
CHANGE COLUMN `testCaseSuiteName` `testCaseSuiteName` VARCHAR(500) NULL DEFAULT NULL ;

ALTER TABLE `dast_db`.`scan` 
CHANGE COLUMN `test_case_name` `test_case_name` VARCHAR(500) NOT NULL ,
CHANGE COLUMN `test_suite_name` `test_suite_name` VARCHAR(500) NULL DEFAULT NULL ;

ALTER TABLE `dast_db`.`proxy` 
CHANGE COLUMN `htd_file_name` `htd_file_name` VARCHAR(500) NULL DEFAULT NULL ;

ALTER TABLE `dast_db`.`proxy_entity` 
ADD COLUMN `testCasePackageName` VARCHAR(500) NULL DEFAULT NULL AFTER `user_id`,
ADD COLUMN `testCaseTagName` VARCHAR(500) NULL DEFAULT NULL AFTER `testCasePackageName`,
ADD COLUMN `testCaseClassName` VARCHAR(500) NULL DEFAULT NULL AFTER `testCaseTagName`;

CREATE TABLE `dast_db`.`traffic` (
  `id` INT NOT NULL,
  `testHttpTraffic` BLOB NULL,
  `originalHttpTraffic` BLOB NULL,
  PRIMARY KEY (`id`));
  
ALTER TABLE `dast_db`.`issue_variant` 
CHANGE COLUMN `traffic_id` `traffic_id` INT NULL DEFAULT NULL ;
ALTER TABLE `dast_db`.`issue_variant` 
ADD CONSTRAINT `traffic_id`
  FOREIGN KEY (`traffic_id`)
  REFERENCES `dast_db`.`traffic` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

 ALTER TABLE `dast_db`.`issue_variant` 
CHANGE COLUMN `issue_variant_value` `issue_variant_value` BLOB NULL DEFAULT NULL COMMENT '' ;

ALTER TABLE `dast_db`.`issue` 
CHANGE COLUMN `test_url` `test_url` VARCHAR(500) NULL DEFAULT NULL COMMENT '' ;

ALTER TABLE `dast_db`.`issue` 
CHANGE COLUMN `severity` `severity` VARCHAR(100) NULL DEFAULT NULL COMMENT '' ,
CHANGE COLUMN `issue_type` `issue_type` VARCHAR(100) NULL DEFAULT NULL COMMENT '' ;


----------------

CREATE TABLE `dast_db`.`request_modification` (
  `id` INT NOT NULL COMMENT '',
  `modified_value` BLOB NULL COMMENT '',
  `original_value` BLOB NULL COMMENT '',
  `issue_variant_id` INT NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  INDEX `issue_variant_id_idx` (`issue_variant_id` ASC)  COMMENT '',
  CONSTRAINT `issue_variant_id`
    FOREIGN KEY (`issue_variant_id`)
    REFERENCES `dast_db`.`issue_variant` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

ALTER TABLE `dast_db`.`request_modification` 
DROP COLUMN `issue_variant_id`,
DROP INDEX `issue_variant_id_idx` ;

CREATE TABLE `dast_db`.`difference` (
  `id` INT NOT NULL COMMENT '',
  `issue_variant_id` INT NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  INDEX `issue_variant_id_idx` (`issue_variant_id` ASC)  COMMENT '',
  CONSTRAINT `issue_variant_id`
    FOREIGN KEY (`issue_variant_id`)
    REFERENCES `dast_db`.`issue_variant` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
    
ALTER TABLE `dast_db`.`issue_variant` 
ADD COLUMN `difference_id` INT NULL COMMENT '' AFTER `traffic_id`,
ADD INDEX `difference_id_idx` (`difference_id` ASC)  COMMENT '';
ALTER TABLE `dast_db`.`issue_variant` 
ADD CONSTRAINT `difference_id`
  FOREIGN KEY (`difference_id`)
  REFERENCES `dast_db`.`difference` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

ALTER TABLE `dast_db`.`request_modification` 
CHANGE COLUMN `id` `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '' ;

ALTER TABLE `dast_db`.`request_modification` 
ADD COLUMN `difference_id` INT NULL COMMENT '' AFTER `original_value`,
ADD INDEX `differece_id_idx` (`difference_id` ASC)  COMMENT '';
ALTER TABLE `dast_db`.`request_modification` 
ADD CONSTRAINT `differece_id`
  FOREIGN KEY (`difference_id`)
  REFERENCES `dast_db`.`difference` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
  
ALTER TABLE `dast_db`.`request_modification` 
DROP COLUMN `difference_id`,
DROP INDEX `differece_id_idx` ;

ALTER TABLE `dast_db`.`issue_variant` 
DROP FOREIGN KEY `traffic_id`;
ALTER TABLE `dast_db`.`issue_variant` 
DROP COLUMN `traffic_id`,
DROP INDEX `traffic_id_idx` ;

ALTER TABLE `dast_db`.`traffic` 
CHANGE COLUMN `id` `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '' ;

ALTER TABLE `dast_db`.`issue_variant` 
ADD COLUMN `traffic_id` INT NULL COMMENT '' AFTER `difference_id`,
ADD INDEX `traffic_id_idx` (`traffic_id` ASC)  COMMENT '';
ALTER TABLE `dast_db`.`issue_variant` 
ADD CONSTRAINT `traffic_id`
  FOREIGN KEY (`traffic_id`)
  REFERENCES `dast_db`.`traffic` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
  
ALTER TABLE `dast_db`.`traffic` 
CHANGE COLUMN `id` `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '' ,
CHANGE COLUMN `testHttpTraffic` `test_http_traffic` LONGBLOB NULL DEFAULT NULL COMMENT '' ,
CHANGE COLUMN `originalHttpTraffic` `original_http_traffic` LONGBLOB NULL COMMENT '' ;

ALTER TABLE `dast_db`.`issue_variant` 
DROP FOREIGN KEY `issue_id`;

ALTER TABLE `dast_db`.`issue` 
CHANGE COLUMN `issue_id` `issue_id` VARCHAR(45) NULL COMMENT '' ,
ADD COLUMN `id` INT NOT NULL AUTO_INCREMENT COMMENT '' AFTER `report_id`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`id`)  COMMENT '';

ALTER TABLE `dast_db`.`issue_variant` 
CHANGE COLUMN `issue_id` `issue_id` INT NULL DEFAULT NULL COMMENT '' ;



-----

  
  ALTER TABLE `dast_db`.`traffic` 
ADD COLUMN `issue_id` VARCHAR(45) NULL COMMENT '' AFTER `original_http_traffic`,
ADD COLUMN `report_id` VARCHAR(45) NULL COMMENT '' AFTER `issue_id`,
ADD INDEX `issue_id_traffic_fx_idx` (`issue_id` ASC)  COMMENT '',
ADD INDEX `report_id_traffic_fx_idx` (`report_id` ASC)  COMMENT '';
ALTER TABLE `dast_db`.`traffic` 
ADD CONSTRAINT `issue_id_traffic_fx`
  FOREIGN KEY (`issue_id`)
  REFERENCES `dast_db`.`issue_variant` (`issue_id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
ADD CONSTRAINT `report_id_traffic_fx`
  FOREIGN KEY (`report_id`)
  REFERENCES `dast_db`.`issue_variant` (`report_id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
 
  
ALTER TABLE `dast_db`.`issue_variant` 
DROP FOREIGN KEY `traffic_id`;

ALTER TABLE `dast_db`.`traffic` 
CHANGE COLUMN `id` `id` INT(11) NULL COMMENT '' ,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`issue_id`, `report_id`)  COMMENT '';

ALTER TABLE `dast_db`.`traffic` 
DROP COLUMN `id`;

ALTER TABLE `dast_db`.`issue_variant` 
DROP COLUMN `traffic_id`,
DROP INDEX `traffic_id_idx` ;

ALTER TABLE `dast_db`.`issue_variant` 
DROP COLUMN `issue_variant_value`;

ALTER TABLE `dast_db`.`difference` 
DROP FOREIGN KEY `issue_variant_id`;
ALTER TABLE `dast_db`.`difference` 
DROP COLUMN `issue_variant_id`,
DROP INDEX `issue_variant_id_idx` ;

ALTER TABLE `dast_db`.`issue_variant` 
DROP COLUMN `id`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`report_id`, `issue_id`)  COMMENT '';

ALTER TABLE `dast_db`.`issue_variant` 
DROP FOREIGN KEY `difference_id`;
ALTER TABLE `dast_db`.`issue_variant` 
DROP COLUMN `difference_id`,
DROP INDEX `difference_id_idx` ;

ALTER TABLE `dast_db`.`difference` 
DROP COLUMN `id`,
ADD COLUMN `issue_id` VARCHAR(45) NULL COMMENT '' FIRST,
ADD COLUMN `report_id` VARCHAR(45) NULL COMMENT '' AFTER `issue_id`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`issue_id`, `report_id`)  COMMENT '';

ALTER TABLE `dast_db`.`difference` 
ADD INDEX `report_id_difference_fx_idx` (`report_id` ASC)  COMMENT '';
ALTER TABLE `dast_db`.`difference` 
ADD CONSTRAINT `issue_id_difference_fx`
  FOREIGN KEY (`issue_id`)
  REFERENCES `dast_db`.`issue_variant` (`issue_id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
ADD CONSTRAINT `report_id_difference_fx`
  FOREIGN KEY (`report_id`)
  REFERENCES `dast_db`.`issue_variant` (`report_id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

ALTER TABLE `dast_db`.`issue_variant` 
ADD COLUMN `id` VARCHAR(45) NOT NULL COMMENT '' AFTER `report_id`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`report_id`, `issue_id`, `id`)  COMMENT '';

ALTER TABLE `dast_db`.`traffic` 
ADD COLUMN `issue_variant_id` VARCHAR(45) NOT NULL COMMENT '' AFTER `report_id`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`issue_id`, `report_id`, `issue_variant_id`)  COMMENT '';

CREATE TABLE `dast_db`.`jira` (
  `jira_key` VARCHAR(45) NOT NULL COMMENT '',
  `self` VARCHAR(45) NULL COMMENT '',
  PRIMARY KEY (`key`)  COMMENT '');
  
  ALTER TABLE `dast_db`.`issue` 
ADD COLUMN `jira_key` VARCHAR(45) NULL COMMENT '' AFTER `report_id`,
ADD INDEX `jira_key_fx_idx` (`jira_key` ASC)  COMMENT '';
ALTER TABLE `dast_db`.`issue` 
ADD CONSTRAINT `jira_key_fx`
  FOREIGN KEY (`jira_key`)
  REFERENCES `dast_db`.`jira` (`jira_key`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
  
ALTER TABLE `dast_db`.`jira` 
CHANGE COLUMN `self` `self` VARCHAR(150) NULL DEFAULT NULL COMMENT '' ;

GRANT SELECT, INSERT, UPDATE, DELETE ON `dast_db`.`jira` TO 'dast_db'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON `dast_db`.`user` TO 'dast_db'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON `dast_db`.`proxy` TO 'dast_db'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON `dast_db`.`proxy_entity` TO 'dast_db'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON `dast_db`.`scan_configuration` TO 'dast_db'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON `dast_db`.`issue` TO 'dast_db'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON `dast_db`.`issue_variant` TO 'dast_db'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON `dast_db`.`report` TO 'dast_db'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON `dast_db`.`scan` TO 'dast_db'@'localhost';