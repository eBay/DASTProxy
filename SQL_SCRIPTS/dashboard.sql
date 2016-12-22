use dast_db;

CREATE TABLE `recording` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `owner` varchar(45) NOT NULL,
  `har_filename` varchar(200) NULL,  
  `htd_filename` varchar(200) NULL,
  `is_enabled` binary(1) DEFAULT '1',
  `date_created` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified` datetime ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE dast_db.scan ADD testsuite_dynamic_identifier varchar(255) NULL;
ALTER TABLE dast_db.scan ADD scan_recording_id int(11) NULL;
ALTER TABLE dast_db.scan ADD breeze_unique_timestamp BIGINT(15) NULL;

ALTER TABLE dast_db.recording ADD testcase_name varchar(255) NULL;
ALTER TABLE dast_db.recording ADD testsuite_name varchar(255) NULL;
ALTER TABLE dast_db.recording ADD testsuite_dynamic_identifier varchar(255) NULL;
ALTER TABLE dast_db.recording ADD is_breeze binary(1);

ALTER TABLE dast_db.scan MODIFY scan_name VARCHAR(1000);

CREATE TABLE `recording_batch` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `testsuite_name` varchar(45) NOT NULL,
  `testsuite_dynamic_identifier` varchar(255) NULL,
  `is_manual_test_batch` binary(1) DEFAULT '0',
  `owner` varchar(45) NOT NULL,
  `is_enabled` binary(1) DEFAULT '1',
  `date_created` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified` datetime ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `scan_batch` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `testsuite_name` varchar(45) NOT NULL,
  `recording_batch_id` int(11) NULL,
  `is_subset_of_batch` binary(1) DEFAULT '0',
  `owner` varchar(45) NOT NULL,
  `date_created` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified` datetime ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY recording_batch_id_fk (recording_batch_id) REFERENCES recording_batch(id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE dast_db.scan ADD scan_batch_id int(11) NULL;
ALTER TABLE dast_db.recording ADD recording_batch_id int(11) NULL;

ALTER TABLE dast_db.scan ADD testsuite_package varchar(250) NULL;
ALTER TABLE dast_db.recording ADD testsuite_package varchar(250) NULL;
ALTER TABLE dast_db.proxy_entity ADD testsuiteDynamicIdentifier varchar(250) NULL;
