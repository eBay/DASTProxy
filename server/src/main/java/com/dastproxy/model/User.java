package com.dastproxy.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user")
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4139057308311599023L;

	@Id
	@Column(name = "user_id")
	private String userId;
	@JsonIgnore
	@Transient
	private String password;
	
	@Column(name="enable_email_for_automated_scan")
	private boolean enableEmailForAutomatedScan;
	
	@Column(name="appscan_userid")
	private String appScanUserId;

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(final String userId) {
		this.userId = userId;
	}
	
	public boolean getEnableEmailForAutomatedScan() {
		return enableEmailForAutomatedScan;
	}

	public void setEnableEmailForAutomatedScan(boolean enableEmailForAutomatedScan) {
		this.enableEmailForAutomatedScan = enableEmailForAutomatedScan;
	}

	
	public String getAppScanUserId() {
		return appScanUserId;
	}

	public void setAppScanUserId(String appScanUserId) {
		this.appScanUserId = appScanUserId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [userId=" + userId + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass())
			return false;
		final User other = (User) obj;
		if (password == null) {
			if (other.password != null){
				return false;				
			}
		} else if (!password.equals(other.password)){
			return false;
		}
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId)){
			return false;
		}
		return true;
	}

}
