package com.dastproxy.model.jira;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("project")
public class Project {

	private String key;

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
}
