/**
 *  This is the entity to denote a proxy. It contains the port and address. I plan to track proxies based on this class
 *  in the future.
 *  
 *  Presently this class is being used to return information about a proxy to the user. It doesn't contain any Proxy Specific details.
 *  For example we are currently using Browser Mob proxy. It may change tomorrow. Keeping this identification class as decoupled as possible.
 *  
 *  @author Kiran Shirali (kshirali@ebay.com)
 */

package com.dastproxy.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("proxy")
@Entity
@Table(name = "proxy")
public class Proxy implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7863059998443195627L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", insertable = true, updatable = true)
	private Long id;
	@Column(name = "proxy_address")
	private String proxyAddress;
	@Column(name = "proxy_port")
	private int proxyPort;
	// This flag to consider whether the proxy has been newly created or whether
	// the proxy is already running.
	// This indicates to the client whether it is my UI or it is a Bluefin/Breeze/Selenium test
	// case the same.
	@Column(name = "newly_created")
	private boolean newlyCreated;
	@Column(name = "htd_file_name")
	private String htdFileName;

	public Proxy() {
		super();
	}

	/**
	 * @return the id
	 */
	@JsonIgnore
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final Long id) {
		this.id = id;
	}

	/**
	 * @param proxyAddress
	 * @param proxyPort
	 * @param newlyCreated
	 */
	public Proxy(final String proxyAddress, final int proxyPort,
			final boolean newlyCreated) {
		super();
		this.proxyAddress = proxyAddress;
		this.proxyPort = proxyPort;
		this.newlyCreated = newlyCreated;
	}

	/**
	 * @return the htdFileName
	 */
	@JsonIgnore
	public String getHtdFileName() {
		return htdFileName;
	}

	/**
	 * @param htdFileName
	 *            the htdFileName to set
	 */
	public void setHtdFileName(final String htdFileName) {
		this.htdFileName = htdFileName;
	}

	/**
	 * @return the proxyAddress
	 */
	public String getProxyAddress() {
		return proxyAddress;
	}

	/**
	 * @param proxyAddress
	 *            the proxyAddress to set
	 */
	public void setProxyAddress(final String proxyAddress) {
		this.proxyAddress = proxyAddress;
	}

	/**
	 * @return the proxyPort
	 */
	public int getProxyPort() {
		return proxyPort;
	}

	/**
	 * @param proxyPort
	 *            the proxyPort to set
	 */
	public void setProxyPort(final int proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * @return the newlyCreated
	 */
	public boolean isNewlyCreated() {
		return newlyCreated;
	}

	/**
	 * @param newlyCreated
	 *            the newlyCreated to set
	 */
	public void setNewlyCreated(final boolean newlyCreated) {
		this.newlyCreated = newlyCreated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	@JsonIgnore
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((proxyAddress == null) ? 0 : proxyAddress.hashCode());
		result = prime * result + proxyPort;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	@JsonIgnore
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Proxy other = (Proxy) obj;
		if (proxyAddress == null) {
			if (other.proxyAddress != null) {
				return false;
			}

		} else if (!proxyAddress.equals(other.proxyAddress)) {
			return false;
		}
		if (proxyPort != other.proxyPort) {
			return false;
		}
		return true;
	}

}
