package com.dastproxy.model;

public class IssueVO {

	public String id;
	public String issueId;
	public String reportId;
	public String issueUrl;
	public String issueDastUrl;
	public String severity;
	public String issueType;
	public String testUrl;
	public String testHTTPtraffic;
	public String origHTTPtraffic;
	public String jiraURL;
	public String jiraKey;
	public String testcaseName;
	public String fpComments;
	public boolean fp; 
	public String scanEngine;
	public long fpReasonId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIssueId() {
		return issueId;
	}
	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}
	public String getIssueUrl() {
		return issueUrl;
	}
	public void setIssueUrl(String issueUrl) {
		this.issueUrl = issueUrl;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getIssueType() {
		return issueType;
	}
	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}
	public String getTestUrl() {
		return testUrl;
	}
	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
	}
	public String getTestHTTPtraffic() {
		return testHTTPtraffic;
	}
	public void setTestHTTPtraffic(String testHTTPtraffic) {
		this.testHTTPtraffic = testHTTPtraffic;
	}
	public String getOrigHTTPtraffic() {
		return origHTTPtraffic;
	}
	public void setOrigHTTPtraffic(String origHTTPtraffic) {
		this.origHTTPtraffic = origHTTPtraffic;
	}
	public String getJiraURL() {
		return jiraURL;
	}
	public void setJiraURL(String jiraURL) {
		this.jiraURL = jiraURL;
	}
	public String getReportId() {
		return reportId;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	public String getTestcaseName() {
		return testcaseName;
	}
	public void setTestcaseName(String testcaseName) {
		this.testcaseName = testcaseName;
	}
	public String getFpComments() {
		return fpComments;
	}
	public void setFpComments(String fpComments) {
		this.fpComments = fpComments;
	}
	public boolean isFp() {
		return fp;
	}
	public void setFp(boolean fp) {
		this.fp = fp;
	}
	public String getScanEngine() {
		return scanEngine;
	}
	public void setScanEngine(String scanEngine) {
		this.scanEngine = scanEngine;
	}
	public long getFpReasonId() {
		return fpReasonId;
	}
	public void setFpReasonId(long fpReasonId) {
		this.fpReasonId = fpReasonId;
	}
	public String getJiraKey() {
		return jiraKey;
	}
	public void setJiraKey(String jiraKey) {
		this.jiraKey = jiraKey;
	}
	public String getIssueDastUrl() {
		return issueDastUrl;
	}
	public void setIssueDastUrl(String issueDastUrl) {
		this.issueDastUrl = issueDastUrl;
	}

	
}
