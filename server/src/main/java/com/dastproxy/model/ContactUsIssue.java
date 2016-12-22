package com.dastproxy.model;

public class ContactUsIssue {

	private String issueType;
	private User user;
	private String desc;
	
	/**
	 * @return the issueType
	 */
	public String getIssueType() {
		return issueType;
	}
	
	/**
	 * @param issueType the issueType to set
	 */
	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}
	
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}
	
	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
