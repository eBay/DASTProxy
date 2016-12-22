package com.dastproxy.model.jira;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("")
public class JiraIssueRequest {

	private Fields fields;

	/**
	 * @return the fields
	 */
	public Fields getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(Fields fields) {
		this.fields = fields;
	}
}
