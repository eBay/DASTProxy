package com.dastproxy.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.dastproxy.model.jira.JiraIssueResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "issue")
public class Issue implements Serializable {

	private static final long serialVersionUID = 2826904328096011698L;
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="issue_id")
	private String nativeIssueId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="report_id")
	@Cascade(CascadeType.ALL)
	private Report report;
	
	@Column(name = "issue_url")
	private String issueUrl;
	@Column(name = "severity")
	private String severity;
	@Column(name = "issue_type")
	private String issueType;
	@Column(name = "test_url")
	private String testUrl;

	@Transient
	@JsonIgnore
	private String dastProxyBugUIIssueUrl;
	
	@OneToOne(fetch=FetchType.EAGER)
	@Nullable
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name="jira_key")
	private JiraIssueResponse jira;

	@Column(name = "fp_comments")
	private String fpComments;
	
	@Column(name = "fp_marked_by")
	private String fpMarkedBy;
	
	@Column(name = "fp_marked_date")
	private Date fpMarkedDate;
	
	@Column(name = "date_created")
	private Date dateCreated;
	
	@Column(name="is_fp")
	private boolean isFp;
	
	@Column(name = "scan_engine")
	private String scanEngine;
	
	@Column(name = "fp_reason_id")
	private Long fpReasonId;
	
	@Column(name="test_http_traffic")
	private String testHttpTraffic;
	@Column(name="original_http_traffic")
	private String originalHttpTraffic;
	
	/**
	 * @return the jira
	 */
	public JiraIssueResponse getJira() {
		return jira;
	}

	/**
	 * @param jira the jira to set
	 */
	public void setJira(JiraIssueResponse jira) {
		this.jira = jira;
	}
	
	/**
	 * @return issueUrl
	 */
	public String getIssueUrl() {
		return issueUrl;
	}

	/**
	 * @return severity
	 */
	public String getSeverity() {
		return severity;
	}

	/**
	 * @return issueType
	 */
	public String getIssueType() {
		return issueType;
	}

	/**
	 * @return testUrl
	 */
	public String getTestUrl() {
		return testUrl;
	}

	/**
	 * @param issueUrl
	 *            the issueUrl to set
	 */
	public void setIssueUrl(final String issueUrl) {
		this.issueUrl = issueUrl;
	}

	/**
	 * @param severity
	 *            the Severity to set
	 */
	public void setSeverity(final String severity) {
		this.severity = severity;
	}

	/**
	 * @param issueType
	 *            the issueType to set
	 */
	public void setIssueType(final String issueType) {
		this.issueType = issueType;
	}

	/**
	 * @param testUrl
	 *            the testUrl to set
	 */
	public void setTestUrl(final String testUrl) {
		this.testUrl = testUrl;
	}
	/**
	 * @return the dastProxyBugUIIssueUrl
	 */
	public String getDastProxyBugUIIssueUrl() {
		return dastProxyBugUIIssueUrl;
	}

	/**
	 * @param dastProxyBugUIIssueUrl
	 *            the dastProxyBugUIIssueUrl to set
	 */
	public void setDastProxyBugUIIssueUrl(String dastProxyBugUIIssueUrl) {
		this.dastProxyBugUIIssueUrl = dastProxyBugUIIssueUrl;
	}

	public String getFpComments() {
		return fpComments;
	}

	public void setFpComments(String fpComments) {
		this.fpComments = fpComments;
	}

	public String getFpMarkedBy() {
		return fpMarkedBy;
	}

	public void setFpMarkedBy(String fpMarkedBy) {
		this.fpMarkedBy = fpMarkedBy;
	}

	public Date getFpMarkedDate() {
		return fpMarkedDate;
	}

	public void setFpMarkedDate(Date fpMarkedDate) {
		this.fpMarkedDate = fpMarkedDate;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public boolean isFp() {
		return isFp;
	}

	public void setFp(boolean isFp) {
		this.isFp = isFp;
	}
	
	public String getScanEngine() {
		return scanEngine;
	}

	public void setScanEngine(String scanEngine) {
		this.scanEngine = scanEngine;
	}

	public Long getFpReasonId() {
		return fpReasonId;
	}

	public void setFpReasonId(Long fpReasonId) {
		this.fpReasonId = fpReasonId;
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

	public String getNativeIssueId() {
		return nativeIssueId;
	}

	public void setNativeIssueId(String nativeIssueId) {
		this.nativeIssueId = nativeIssueId;
	}

	public String getTestHttpTraffic() {
		return testHttpTraffic;
	}

	public void setTestHttpTraffic(String testHttpTraffic) {
		this.testHttpTraffic = testHttpTraffic;
	}

	public String getOriginalHttpTraffic() {
		return originalHttpTraffic;
	}

	public void setOriginalHttpTraffic(String originalHttpTraffic) {
		this.originalHttpTraffic = originalHttpTraffic;
	}

	public Long getId() {
		return id;
	}
	
}
