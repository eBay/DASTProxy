/**
 * 
 */
package com.dastproxy.model.jira;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author kshirali
 *
 */

@JsonTypeName("reporter")
public class Reporter {

	
	public String name;

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
