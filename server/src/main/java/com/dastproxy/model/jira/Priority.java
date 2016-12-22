/**
 * 
 */
package com.dastproxy.model.jira;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author kshirali
 *
 */
@JsonTypeName("priority")
public class Priority {

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
