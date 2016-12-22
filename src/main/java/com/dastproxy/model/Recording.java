package com.dastproxy.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="recording")
public class Recording {

	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="owner")
	private String owner;

	@Column(name="har_filename")
	private String harFilename;

	@Column(name="htd_filename")
	private String htdFilename;

	@Column(name="date_created")
	private Date dateCreated;

	@Column(name="last_modified")
	private Date lastModified;

	@Column(name="is_enabled")
	private boolean enabled;
	
	@Column(name="testcase_name")
	private String testcaseName;
	
	@Column(name="testsuite_name")
	private String testsuiteName;
	
	@Column(name="testsuite_package", nullable=true)
	private String testSuitePackage;
	
	@Column(name="testsuite_dynamic_identifier")
	private String tsDynamicIdentifier;
	
	@Column(name="is_breeze")
	private boolean breeze;	
	
	@Column(name="recording_batch_id")
	private Long recordingBatchId;

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getHarFilename() {
		return harFilename;
	}

	public void setHarFilename(String harFilename) {
		this.harFilename = harFilename;
	}

	public String getHtdFilename() {
		return htdFilename;
	}

	public void setHtdFilename(String htdFilename) {
		this.htdFilename = htdFilename;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Long getId() {
		return id;
	}

	public String getTestcaseName() {
		return testcaseName;
	}

	public void setTestcaseName(String testcaseName) {
		this.testcaseName = testcaseName;
	}

	public String getTestsuiteName() {
		return testsuiteName;
	}

	public void setTestsuiteName(String testsuiteName) {
		this.testsuiteName = testsuiteName;
	}

	public String getTsDynamicIdentifier() {
		return tsDynamicIdentifier;
	}

	public void setTsDynamicIdentifier(String tsDynamicIdentifier) {
		this.tsDynamicIdentifier = tsDynamicIdentifier;
	}

	public boolean isBreeze() {
		return breeze;
	}

	public void setBreeze(boolean breeze) {
		this.breeze = breeze;
	}

	public Long getRecordingBatchId() {
		return recordingBatchId;
	}

	public void setRecordingBatchId(Long recordingBatchId) {
		this.recordingBatchId = recordingBatchId;
	}

	public String getTestSuitePackage() {
		return testSuitePackage;
	}

	public void setTestSuitePackage(String testSuitePackage) {
		this.testSuitePackage = testSuitePackage;
	}
	
	
}
