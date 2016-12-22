package com.dastproxy.model.jira;

import com.dastproxy.common.constants.AppScanConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonTypeName("fields")
public class Fields {

	
	private Project project;
	private String summary;
	private String description;
	@JsonProperty("issuetype")
	private IssueType issueType;
	private String[] labels;
	private Priority priority;
	//private Reporter reporter;
	
	@JsonProperty(AppScanConstants.JIRA_CUSTOM_FIELD_1_LABEL)
	@JsonInclude(value=Include.NON_NULL)
	private CustomField1 customField1;
	@JsonProperty(AppScanConstants.JIRA_CUSTOM_FIELD_2_LABEL)
	@JsonInclude(value=Include.NON_NULL)
	private CustomField2 customField2;
	@JsonProperty(AppScanConstants.JIRA_CUSTOM_FIELD_3_LABEL)
	@JsonInclude(value=Include.NON_NULL)
	private CustomField3 customField3;
	@JsonProperty(AppScanConstants.JIRA_CUSTOM_FIELD_4_LABEL)
	@JsonInclude(value=Include.NON_NULL)
	private CustomField4 customField4;
	@JsonProperty(AppScanConstants.JIRA_CUSTOM_FIELD_5_LABEL)
	@JsonInclude(value=Include.NON_NULL)
	private CustomField5 customField5;
	@JsonProperty(AppScanConstants.JIRA_CUSTOM_FIELD_6_LABEL)
	@JsonInclude(value=Include.NON_NULL)
	private CustomField6 customField6;
	@JsonProperty(AppScanConstants.JIRA_CUSTOM_FIELD_7_LABEL)
	@JsonInclude(value=Include.NON_NULL)
	private CustomField7 customField7;
	@JsonProperty(AppScanConstants.JIRA_CUSTOM_FIELD_8_LABEL)
	@JsonInclude(value=Include.NON_NULL)
	private CustomField8 customField8;
	@JsonProperty(AppScanConstants.JIRA_CUSTOM_FIELD_9_LABEL)
	@JsonInclude(value=Include.NON_NULL)
	private CustomField9 customField9;
	@JsonProperty(AppScanConstants.JIRA_CUSTOM_FIELD_10_LABEL)
	@JsonInclude(value=Include.NON_NULL)
	private CustomField10 customField10;
	@JsonProperty(AppScanConstants.JIRA_CUSTOM_FIELD_11_LABEL)
	@JsonInclude(value=Include.NON_NULL)
	private CustomField11 customField11;
	
	/**
	 * @return the reporter
	 */
	/*public Reporter getReporter() {
		return reporter;
	}*/
	/**
	 * @param reporter the reporter to set
	 */
	/*public void setReporter(final Reporter reporter) {
		this.reporter = reporter;
	}*/
	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}
	/**
	 * @param project the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}
	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}
	/**
	 * @param summary the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the issueType
	 */
	public IssueType getIssueType() {
		return issueType;
	}
	/**
	 * @param issueType the issueType to set
	 */
	public void setIssueType(IssueType issueType) {
		this.issueType = issueType;
	}
	/**
	 * @return the labels
	 */
	public String[] getLabels() {
		return labels;
	}
	/**
	 * @param labels the labels to set
	 */
	public void setLabels(String[] labels) {
		this.labels = labels;
	}
	/**
	 * @return the priority
	 */
	public Priority getPriority() {
		return priority;
	}
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	/**
	 * @return the customField1
	 */
	public CustomField1 getCustomField1() {
		return customField1;
	}
	/**
	 * @param customField1 the customField1 to set
	 */
	public void setCustomField1(CustomField1 customField1) {
		this.customField1 = customField1;
	}
	/**
	 * @return the customField2
	 */
	public CustomField2 getCustomField2() {
		return customField2;
	}
	/**
	 * @param customField2 the customField2 to set
	 */
	public void setCustomField2(CustomField2 customField2) {
		this.customField2 = customField2;
	}
	/**
	 * @return the customField3
	 */
	public CustomField3 getCustomField3() {
		return customField3;
	}
	/**
	 * @param customField3 the customField3 to set
	 */
	public void setCustomField3(CustomField3 customField3) {
		this.customField3 = customField3;
	}
	/**
	 * @return the customField4
	 */
	public CustomField4 getCustomField4() {
		return customField4;
	}
	/**
	 * @param customField4 the customField4 to set
	 */
	public void setCustomField4(CustomField4 customField4) {
		this.customField4 = customField4;
	}
	/**
	 * @return the customField5
	 */
	public CustomField5 getCustomField5() {
		return customField5;
	}
	/**
	 * @param customField5 the customField5 to set
	 */
	public void setCustomField5(CustomField5 customField5) {
		this.customField5 = customField5;
	}
	/**
	 * @return the customField6
	 */
	public CustomField6 getCustomField6() {
		return customField6;
	}
	/**
	 * @param customField6 the customField6 to set
	 */
	public void setCustomField6(CustomField6 customField6) {
		this.customField6 = customField6;
	}
	/**
	 * @return the customField7
	 */
	public CustomField7 getCustomField7() {
		return customField7;
	}
	/**
	 * @param customField7 the customField7 to set
	 */
	public void setCustomField7(CustomField7 customField7) {
		this.customField7 = customField7;
	}
	/**
	 * @return the customField8
	 */
	public CustomField8 getCustomField8() {
		return customField8;
	}
	/**
	 * @param customField8 the customField8 to set
	 */
	public void setCustomField8(CustomField8 customField8) {
		this.customField8 = customField8;
	}
	/**
	 * @return the customField9
	 */
	public CustomField9 getCustomField9() {
		return customField9;
	}
	/**
	 * @param customField9 the customField9 to set
	 */
	public void setCustomField9(CustomField9 customField9) {
		this.customField9 = customField9;
	}
	/**
	 * @return the customField10
	 */
	public CustomField10 getCustomField10() {
		return customField10;
	}
	/**
	 * @param customField10 the customField10 to set
	 */
	public void setCustomField10(CustomField10 customField10) {
		this.customField10 = customField10;
	}
	/**
	 * @return the customField11
	 */
	public CustomField11 getCustomField11() {
		return customField11;
	}
	/**
	 * @param customField11 the customField11 to set
	 */
	public void setCustomField11(CustomField11 customField11) {
		this.customField11 = customField11;
	}
	
}
