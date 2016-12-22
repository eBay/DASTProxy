package com.dastproxy.model.jira;

import com.dastproxy.common.constants.AppScanConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(AppScanConstants.JIRA_CUSTOM_FIELD_1_LABEL)
@JsonInclude(value=Include.NON_NULL)
public class CustomField1 {

	private String value;

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
