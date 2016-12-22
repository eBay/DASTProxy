package com.dastproxy.model;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
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
	@EmbeddedId
	private IssuePrimaryKey issuePrimaryKey;
	@Column(name = "issue_url")
	private String issueUrl;
	@Column(name = "severity")
	private String severity;
	@Column(name = "issue_type")
	private String issueType;
	@Column(name = "test_url")
	private String testUrl;

	@OneToMany(mappedBy="issue",fetch=FetchType.EAGER, orphanRemoval=true)
	@Cascade(CascadeType.ALL)
	@Nullable
	private List<IssueVariant> issueVariants;

	@Transient
	@JsonIgnore
	private String dastProxyBugUIIssueUrl;
	
	@OneToOne(fetch=FetchType.EAGER)
	@Nullable
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name="jira_key")
	private JiraIssueResponse jira;

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
	 * @return the issuePrimaryKey
	 */
	public IssuePrimaryKey getIssuePrimaryKey() {
		return issuePrimaryKey;
	}

	/**
	 * @param issuePrimaryKey
	 *            the issuePrimaryKey to set
	 */
	public void setIssuePrimaryKey(IssuePrimaryKey issuePrimaryKey) {
		this.issuePrimaryKey = issuePrimaryKey;
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
	 * 
	 * @return issueVariant
	 */
	public List<IssueVariant> getIssueVariants() {
		return issueVariants;
	}

	/**
	 * 
	 * @param issueVariant
	 */
	public void setIssueVariants(final List<IssueVariant> issueVariants) {
		this.issueVariants = issueVariants;
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

}
