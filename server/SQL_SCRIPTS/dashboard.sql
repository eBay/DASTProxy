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

/* New changes */

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



/*For now making this null, in future this has to be made not null, after the data migration to the new schema is done.*/

ALTER TABLE dast_db.scan ADD scan_batch_id int(11) NULL;
ALTER TABLE dast_db.recording ADD recording_batch_id int(11) NULL;


ALTER TABLE dast_db.scan ADD testsuite_package varchar(250) NULL;
ALTER TABLE dast_db.recording ADD testsuite_package varchar(250) NULL;
ALTER TABLE dast_db.proxy_entity ADD testsuiteDynamicIdentifier varchar(250) NULL;

ALTER TABLE dast_db.issue ADD fp_comments varchar(255) NULL;
ALTER TABLE dast_db.issue ADD date_created datetime NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE dast_db.issue ADD fp_marked_by varchar(255) NULL;
ALTER TABLE dast_db.issue ADD fp_marked_date datetime NULL;

ALTER TABLE dast_db.issue ADD is_fp binary(1) DEFAULT '0';

ALTER TABLE dast_db.recording MODIFY har_filename VARCHAR(2400);
ALTER TABLE dast_db.recording MODIFY htd_filename VARCHAR(2400);

ALTER TABLE dast_db.issue ADD scan_engine varchar(100) NULL DEFAULT 'ASE';


CREATE TABLE dast_db.fp_reason (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(45) NOT NULL,
  abbr varchar(45) NOT NULL,
  `date_created` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified` datetime ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

insert into dast_db.fp_reason (name, abbr) values ('Error Page - ABC', 'ERROR1_ABC');
insert into dast_db.fp_reason (name, abbr) values ('Error Page - Invalid char', 'ERROR2_INV_CH');
insert into dast_db.fp_reason (id, name, abbr) values (1000, 'Error Page - Invalid char', 'OTHER');

commit;

ALTER TABLE dast_db.issue ADD fp_reason_id int(11) NULL;
ALTER TABLE dast_db.scan add zap_status varchar(10) NULL DEFAULT 'New';
/*
CREATE TRIGGER task_creation_timestamp_issue AFTER INSERT ON dast_db.issue 
FOR EACH ROW
UPDATE dast_db.issue SET date_created = CURRENT_TIMESTAMP WHERE issue_id = NEW.issue_id;



DROP TRIGGER dast_db.task_creation_timestamp_issue;
*/
---------------1/9/2017
ALTER TABLE dast_db.scan DROP PRIMARY KEY;
ALTER TABLE dast_db.scan ADD `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY; 

ALTER TABLE dast_db.report ADD `id` int(11) NOT NULL; 
ALTER TABLE dast_db.issue drop foreign key report_id_fx;
ALTER TABLE dast_db.scan drop foreign key report_id;
ALTER TABLE dast_db.traffic drop foreign key report_id_traffic_fx;
ALTER TABLE dast_db.difference drop foreign key report_id_difference_fx;

DROP INDEX `PRIMARY` ON dast_db.report;

ALTER TABLE  dast_db.report drop column id;
ALTER TABLE dast_db.report ADD `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY; 

ALTER TABLE dast_db.issue CHANGE report_id ase_report_id varchar(45); 
ALTER TABLE dast_db.issue ADD `report_id` int(11);

UPDATE dast_db.issue iss,dast_db.report rep
SET iss.report_id=rep.id
WHERE iss.ase_report_id=rep.report_id;

commit;

ALTER TABLE dast_db.scan CHANGE report_id ase_report_id varchar(45); 
ALTER TABLE dast_db.scan ADD `report_id` int(11);

UPDATE dast_db.scan sca,dast_db.report rep
SET sca.report_id=rep.id
WHERE sca.ase_report_id=rep.report_id;

commit;

CREATE INDEX PRIMARY1 ON dast_db.report (id) USING BTREE;

ALTER TABLE `dast_db`.`traffic` 
ADD CONSTRAINT `report_id_traffic_fx`
  FOREIGN KEY (`report_id`)
  REFERENCES `dast_db`.`issue_variant` (`report_id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
  
ALTER TABLE `dast_db`.`difference` 
ADD CONSTRAINT `report_id_difference_fx`
  FOREIGN KEY (`report_id`)
  REFERENCES `dast_db`.`issue_variant` (`report_id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
  
ALTER TABLE `dast_db`.`issue` 
ADD CONSTRAINT `report_id_issue_fx`
  FOREIGN KEY (`report_id`)
  REFERENCES `dast_db`.`report` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
  
ALTER TABLE `dast_db`.`scan` 
ADD CONSTRAINT `report_id_scan_fx`
  FOREIGN KEY (`report_id`)
  REFERENCES `dast_db`.`report` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

ALTER TABLE `dast_db`.`issue` DROP PRIMARY KEY;
ALTER TABLE dast_db.issue ADD `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY; 

ALTER TABLE dast_db.scan MODIFY scan_id VARCHAR(45);
ALTER TABLE dast_db.user ADD `enable_email_for_automated_scan` binary(1) DEFAULT '0';

ALTER TABLE dast_db.issue ADD `test_http_traffic` longblob;
ALTER TABLE dast_db.issue ADD `original_http_traffic` longblob;

UPDATE dast_db.issue JOIN dast_db.traffic USING (issue_id) SET issue.test_http_traffic = traffic.test_http_traffic;
UPDATE dast_db.issue JOIN dast_db.traffic USING (issue_id) SET issue.original_http_traffic = traffic.original_http_traffic;

commit;

ALTER TABLE dast_db.issue MODIFY issue_url VARCHAR(800);
ALTER TABLE dast_db.fp_reason add fp_text_pattern varchar(250);
ALTER TABLE dast_db.scan_configuration MODIFY name_of_scan VARCHAR(500);

ALTER TABLE dast_db.scan MODIFY ase_report_id VARCHAR(45);
ALTER TABLE dast_db.issue MODIFY ase_report_id VARCHAR(45);

update dast_db.fp_reason set fp_text_pattern='Illegal character in path at index' where abbr='ERROR2_INV_CH'
update dast_db.fp_reason set fp_text_pattern='QA PNR (Page Not Responding) Page' where abbr='ERROR1_PNR'
ALTER TABLE dast_db.issue MODIFY issue_type VARCHAR(200);

insert into dast_db.fp_reason (id, name, abbr,fp_text_pattern) values (3, 'Error Page - Invalid query', 'ERROR1_INV_QUERY', 'Illegal character in query at index');

-----------------After 4.0-------------------------
ALTER TABLE dast_db.issue MODIFY issue_type VARCHAR(2000);
ALTER TABLE dast_db.issue MODIFY test_url VARCHAR(2000);

ALTER TABLE dast_db.recording_batch ADD is_nightly_batch binary(1) NULL DEFAULT 0;

ALTER TABLE dast_db.scan ADD suspended_reason varchar(1000) NULL;

insert into dast_db.recording_batch (testsuite_name, is_manual_test_batch, owner, is_enabled, date_created, last_modified,is_nightly_batch)
SELECT 'Nightly Batch', 0, user_id, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1
FROM dast_db.user;

commit;

ALTER TABLE dast_db.scan_batch ADD is_nightly_batch binary(1) NULL DEFAULT 0;
ALTER TABLE dast_db.scan ADD is_nightly_scan binary(1) NULL DEFAULT 0;

ALTER TABLE dast_db.scan_batch ADD nightly_batch_state int(4) NULL DEFAULT 0;
ALTER TABLE dast_db.scan ADD nightly_state int(4) NULL DEFAULT 0;
UPDATE dast_db.scan_batch SET nightly_batch_state=1;
commit;
insert into dast_db.fp_reason (id, name, abbr) values (1003, 'Duplicate - Nightly Scan', 'DUPLICATE-NIGHTLY');
commit;
ALTER TABLE dast_db.user ADD appscan_userid VARCHAR(2000);