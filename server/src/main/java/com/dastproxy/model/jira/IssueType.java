package com.dastproxy.model.jira;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("issuetype")
public class IssueType {

	private String name;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
