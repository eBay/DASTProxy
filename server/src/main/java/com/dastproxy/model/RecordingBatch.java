package com.dastproxy.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="recording_batch")
public class RecordingBatch {

	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="testsuite_name")
	private String testsuiteName;
	
	@Column(name="testsuite_dynamic_identifier")
	private String testsuiteDynamicIdentifier;
	
	@Column(name="is_manual_test_batch")
	private boolean manualTestBatch = false;

	@Column(name="owner")
	private String owner;

	//Making it enabled by default.
	@Column(name="is_enabled")
	private boolean enabled = true;

	@Column(name="date_created")
	private Date dateCreated;

	@Column(name="last_modified")
	private Date lastModified;
	
	@Column(name="is_nightly_batch")
	private boolean isNightlyBatch;


	public String getTestsuiteName() {
		return testsuiteName;
	}

	public void setTestsuiteName(String testsuiteName) {
		this.testsuiteName = testsuiteName;
	}

	public String getTestsuiteDynamicIdentifier() {
		return testsuiteDynamicIdentifier;
	}

	public void setTestsuiteDynamicIdentifier(String testsuiteDynamicIdentifier) {
		this.testsuiteDynamicIdentifier = testsuiteDynamicIdentifier;
	}

	public boolean isManualTestBatch() {
		return manualTestBatch;
	}

	public void setManualTestBatch(boolean manualTestBatch) {
		this.manualTestBatch = manualTestBatch;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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

	public Long getId() {
		return id;
	}

	public boolean isNightlyBatch() {
		return isNightlyBatch;
	}

	public void setNightlyBatch(boolean isNightlyBatch) {
		this.isNightlyBatch = isNightlyBatch;
	}
}
