package com.dastproxy.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.context.annotation.Lazy;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="scan")
public class Scan {
	
	public static final int NIGHTLY_SCAN_STATE_CREATED=0;
	public static final int NIGHTLY_SCAN_STATE_COMPLETED=1;
	public static final int NIGHTLY_SCAN_STATE_POST_PROCESSING_DONE=2;

	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="scan_id")
	private String scanId;
	
	@Column(name="scan_name")
	private String scanName;
	
	@Column(name="scan_state")
	private String scanState;
	
	@Column(name="scan_last_run", nullable=true)
	private String scanLastRun;
	
	@Column(name="email_sent")
	private boolean emailSent;
	
	@OneToOne
	@Cascade(CascadeType.ALL)
	@JoinColumn(name="report_id", nullable=true)
	private Report report;
	
	@Column(name="first_set_up")
	private Date firstSetUp;
	
	@OneToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name="user_id")
	@Lazy(value=false)
	private User user;
	
	@Column(name="set_up_via_bluefin")
	private boolean setUpViaBluefin;
	
	@Column(name="test_case_name")
	private String testCaseName;
	
	@Column(name="test_suite_name", nullable=true)
	private String testSuiteName;
	
	@Column(name="testsuite_package", nullable=true)
	private String testSuitePackage;

	@Column(name="to_be_tracked")
	private Boolean toBeTracked;

	@Column(name="testsuite_dynamic_identifier")
	private String tsDynamicIdentifier;

	@Column(name="scan_recording_id")
	private Long recordingId;

	@Column(name="breeze_unique_timestamp")
	private Long breezeUniqueTS;
	
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="scan_batch_id")
	private ScanBatch batch;
    
	@Column(name="zap_status")
	private String zapStatus;
	
	@Column(name="suspended_reason")
	private String suspendedReason;
	

	@Transient
	@JsonIgnore
	private String userForlderId;
	
	@Column(name="is_nightly_scan")
	private boolean isNightlyScan;

	@Column(name="nightly_state")
	private int nightlyState;

	/**
	 * @return scanId
	 */
	public String getScanId() {
		return scanId;
	}
	
	/**
	 * 
	 * @param scanId
	 */
	public void setScanId(final String scanId) {
		this.scanId = scanId;
	}
	
	/**
	 * @return scanName
	 */
	public String getScanName() {
		return scanName;
	}
	
	/**
	 * 
	 * @param scanName
	 */
	public void setScanName(final String scanName) {
		this.scanName = scanName;
	}
	/**
	 * 
	 * @return scanState
	 */
	public String getScanState() {
		return scanState;
	}
	
	/**
	 * 
	 * @param scanState
	 */
	public void setScanState(final String scanState) {
		this.scanState = scanState;
	}
	
	/**
	 * 
	 * @return scanLastRun
	 */
	public String getScanLastRun() {
		return scanLastRun;
	}
	
	/**
	 * 
	 * @param scanLastRun
	 */
	public void setScanLastRun(final String scanLastRun) {
		this.scanLastRun = scanLastRun;
	}
	
	/**
	 * 
	 * @return userFolderId
	 */
	public String getUserForlderId() {
		return userForlderId;
	}
	
	/**
	 * 
	 * @param userForlderId
	 */
	public void setUserForlderId(final String userForlderId) {
		this.userForlderId = userForlderId;
	}
	
	/**
	 * 
	 * @return emailSent
	 */
	public boolean isEmailSent() {
		return emailSent;
	}
	
	/**
	 * 
	 * @param emailSent
	 */
	public void setEmailSent(final boolean emailSent) {
		this.emailSent = emailSent;
	}
	
	

	/**
	 * @return the report
	 */
	public Report getReport() {
		return report;
	}

	/**
	 * @param report the report to set
	 */
	public void setReport(Report report) {
		this.report = report;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the firstSetUp
	 */
	public Date getFirstSetUp() {
		return firstSetUp;
	}

	/**
	 * @param firstSetUp the firstSetUp to set
	 */
	public void setFirstSetUp(Date firstSetUp) {
		this.firstSetUp = firstSetUp;
	}

	/**
	 * @return the setUpViaBluefin
	 */
	public boolean isSetUpViaBluefin() {
		return setUpViaBluefin;
	}

	/**
	 * @param setUpViaBluefin the setUpViaBluefin to set
	 */
	public void setSetUpViaBluefin(boolean setUpViaBluefin) {
		this.setUpViaBluefin = setUpViaBluefin;
	}	
	
	/**
	 * @return the testCaseName
	 */
	public String getTestCaseName() {
		return testCaseName;
	}

	/**
	 * @param testCaseName the testCaseName to set
	 */
	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}
	
	/**
	 * @return the testSuiteName
	 */
	public String getTestSuiteName() {
		return testSuiteName;
	}

	/**
	 * @param testSuiteName the testSuiteName to set
	 */
	public void setTestSuiteName(String testSuiteName) {
		this.testSuiteName = testSuiteName;
	}

	/**
	 * @return the toBeTracked
	 */
	public Boolean getToBeTracked() {
		return toBeTracked;
	}

	/**
	 * @param toBeTracked the toBeTracked to set
	 */
	public void setToBeTracked(Boolean toBeTracked) {
		this.toBeTracked = toBeTracked;
	}

	public Long getRecordingId() {
		return recordingId;
	}

	public void setRecordingId(Long recordingId) {
		this.recordingId = recordingId;
	}

	public String getTsDynamicIdentifier() {
		return tsDynamicIdentifier;
	}

	public void setTsDynamicIdentifier(String tsDynamicIdentifier) {
		this.tsDynamicIdentifier = tsDynamicIdentifier;
	}

	public Long getBreezeUniqueTS() {
		return breezeUniqueTS;
	}

	public void setBreezeUniqueTS(Long breezeUniqueTS) {
		this.breezeUniqueTS = breezeUniqueTS;
	}

	public ScanBatch getBatch() {
		return batch;
	}

	public void setBatch(ScanBatch batch) {
		this.batch = batch;
	}

	public String getTestSuitePackage() {
		return testSuitePackage;
	}

	public void setTestSuitePackage(String testSuitePackage) {
		this.testSuitePackage = testSuitePackage;
	}

	public String getZapStatus() {
		return zapStatus;
	}

	public void setZapStatus(String zapStatus) {
		this.zapStatus = zapStatus;
	}

	public Long getId() {
		return id;
	}

	public String getSuspendedReason() {
		return suspendedReason;
	}

	public void setSuspendedReason(String suspendedReason) {
		this.suspendedReason = suspendedReason;
	}

	public boolean isNightlyScan() {
		return isNightlyScan;
	}

	public void setNightlyScan(boolean isNightlyScan) {
		this.isNightlyScan = isNightlyScan;
	}

	public int getNightlyState() {
		return nightlyState;
	}

	public void setNightlyState(int nightlyState) {
		this.nightlyState = nightlyState;
	}

}
