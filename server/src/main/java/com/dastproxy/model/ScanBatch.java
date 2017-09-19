package com.dastproxy.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name="scan_batch")
public class ScanBatch {
	
	public static final int CREATED = 0;
	public static final int COMPLETED = 1;
	

	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="testsuite_name")
	private String testsuiteName;

	@Column(name="recording_batch_id")
	private Long recordingBatchId;
	
	@Column(name="is_subset_of_batch")
	private boolean subsetOfBatch;

	@Column(name="owner")
	private String owner;

	@Column(name="date_created")
	private Date dateCreated;

	@Column(name="last_modified")
	private Date lastModified;
		
	@OneToMany(mappedBy="batch", fetch=FetchType.EAGER)
	private List<Scan> scans;
	
	@Transient
	private String displayStatus;
	
	@Column(name="is_nightly_batch")
	private boolean isNightlyBatch;
	
	@Column(name="nightly_batch_state")
	private int nightlyBatchState;

	public String getTestsuiteName() {
		return testsuiteName;
	}

	public void setTestsuiteName(String testsuiteName) {
		this.testsuiteName = testsuiteName;
	}

	public Long getRecordingBatchId() {
		return recordingBatchId;
	}

	public void setRecordingBatchId(Long recordingBatchId) {
		this.recordingBatchId = recordingBatchId;
	}

	public boolean isSubsetOfBatch() {
		return subsetOfBatch;
	}

	public void setSubsetOfBatch(boolean subsetOfBatch) {
		this.subsetOfBatch = subsetOfBatch;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
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
	
	public List<Scan> getScans() {
		return scans;
	}

	public void setScans(List<Scan> scans) {
		this.scans = scans;
	}

	public String getDisplayStatus() {
		return displayStatus;
	}

	public void setDisplayStatus(String displayStatus) {
		this.displayStatus = displayStatus;
	}

	public boolean isNightlyBatch() {
		return isNightlyBatch;
	}

	public void setNightlyBatch(boolean isNightlyBatch) {
		this.isNightlyBatch = isNightlyBatch;
	}

	public Long getId() {
		return id;
	}

	public int getNightlyBatchState() {
		return nightlyBatchState;
	}

	public void setNightlyBatchState(int nightlyBatchState) {
		this.nightlyBatchState = nightlyBatchState;
	}

}