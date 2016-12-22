/**
 * 
 */
package com.dastproxy.model;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.lightbody.bmp.proxy.ProxyServer;
import org.hibernate.annotations.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
//import org.codehaus.jackson.annotate.JsonIgnore;
//import org.codehaus.jackson.annotate.JsonProperty;

import org.hibernate.annotations.Cascade;

/**
 * @author Kiran Shirali (kshirali@ebay.com)
 *
 */

@Entity
@Table(name="proxy_entity")
public class ProxyEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4629915997618923393L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", insertable = true, updatable = true)
	private Long id;
	
	@OneToOne(fetch=FetchType.EAGER)
	@Cascade(CascadeType.ALL)
	private Proxy proxy;
	
	@Column(name="proxyIdentifier")
	private String proxyIdentifier;
	
	@Column(name="testCaseName")
	private String testCaseName;
	
	@Column(name="testCasePackageName")
	private String testCasePackageName;
	
	@Column(name="testCaseTagName")
	private String testCaseTagName;
	
	@Column(name="testCaseClassName")
	private String testCaseClassName;
	
	@Column(name="testCaseSuiteName")
	private String testCaseSuiteName;
	
	@Column(name="testsuiteDynamicIdentifier")
	private String testsuiteDynamicIdentifier;
	
	// Here if a user is in the database, point to the same user.
	// However when deleting the this entry do to remove the user. Leave them there as is.
	@OneToOne(fetch=FetchType.EAGER)
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name="user_id")
	private User user;
	
	@Transient
	private ProxyServer proxyServer;
	
	@Transient
	private List<String> scanConfigurationParameters;
	
	@Column(name="errorMessage")
	private String errorMessage;
	
	@OneToOne(fetch=FetchType.EAGER)
	@Cascade(CascadeType.ALL)
	@JoinColumn(name="scan_configuration_id")
	private ScanConfiguration scanConfiguration;
	
	/**
	 * 
	 */
	public ProxyEntity() {
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
	 * @param id the id to set
	 */
	public void setId(final Long id) {
		this.id = id;
	}

	
	/**
	 * @return the testCaseSuiteName
	 */
	public String getTestCaseSuiteName() {
		return testCaseSuiteName;
	}

	/**
	 * @param testCaseSuiteName the testCaseSuiteName to set
	 */
	public void setTestCaseSuiteName(final String testCaseSuiteName) {
		this.testCaseSuiteName = testCaseSuiteName;
	}

	
	
	/**
	 * @param proxy
	 * @param proxyIdentifier
	 */
	public ProxyEntity(final Proxy proxy, final String proxyIdentifier) {
		super();
		this.proxy = proxy;
		this.proxyIdentifier = proxyIdentifier;
	}

	/**
	 * @return the proxyServer
	 */
	@JsonIgnore
	public ProxyServer getProxyServer() {
		return proxyServer;
	}

	/**
	 * @param proxyServer the proxyServer to set
	 */
	public void setProxyServer(final ProxyServer proxyServer) {
		this.proxyServer = proxyServer;
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
	public void setUser(final User user) {
		this.user = user;
	}

	/**
	 * @return the proxy
	 */
	@JsonProperty("proxy")
	public Proxy getProxy() {
		return proxy;
	}

	/**
	 * @param proxy the proxy to set
	 */
	public void setProxy(final Proxy proxy) {
		this.proxy = proxy;
	}

	/**
	 * @return the proxyIdentifier
	 */
	public String getProxyIdentifier() {
		return proxyIdentifier;
	}

	/**
	 * @param proxyIdentifier the proxyIdentifier to set
	 */
	public void setProxyIdentifier(final String proxyIdentifier) {
		this.proxyIdentifier = proxyIdentifier;
	}

	/**
	 * @return the testCaseName
	 */
	public String getTestCaseName() {
		return testCaseName;
	}

	/**
	 * @param testCaseName the testCaseName to set
	 */
	public void setTestCaseName(final String testCaseName) {
		this.testCaseName = testCaseName;
	}
	
	/**
	 * @return the scanConfigurationParameters
	 */
	public List<String> getScanConfigurationParameters() {
		return scanConfigurationParameters;
	}

	/**
	 * @param scanConfigurationParameters the scanConfigurationParameters to set
	 */
	public void setScanConfigurationParameters(
			final List<String> scanConfigurationParameters) {
		this.scanConfigurationParameters = scanConfigurationParameters;
	}
	
	
	

	/**
	 * @return the scanConfiguration
	 */
	@JsonIgnore
	public ScanConfiguration getScanConfiguration() {
		return scanConfiguration;
	}

	/**
	 * @param scanConfiguration the scanConfiguration to set
	 */
	public void setScanConfiguration(final ScanConfiguration scanConfiguration) {
		this.scanConfiguration = scanConfiguration;
	}

	/**
	 * @return the errorMessage
	 */
	@JsonIgnore
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	@JsonIgnore
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((proxy == null) ? 0 : proxy.hashCode());
		result = prime * result
				+ ((proxyIdentifier == null) ? 0 : proxyIdentifier.hashCode());
		result = prime * result
				+ ((proxyServer == null) ? 0 : proxyServer.hashCode());
		result = prime
				* result
				+ ((scanConfigurationParameters == null) ? 0
						: scanConfigurationParameters.hashCode());
		result = prime * result
				+ ((testCaseName == null) ? 0 : testCaseName.hashCode());
		result = prime
				* result
				+ ((testCaseSuiteName == null) ? 0 : testCaseSuiteName
						.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}
	public String getTestCasePackageName() {
		return testCasePackageName;
	}

	public void setTestCasePackageName(String testCasePackageName) {
		this.testCasePackageName = testCasePackageName;
	}

	public String getTestCaseClassName() {
		return testCaseClassName;
	}

	public void setTestCaseClassName(String testCaseClassName) {
		this.testCaseClassName = testCaseClassName;
	}
	
	
	public String getTestsuiteDynamicIdentifier() {
		return testsuiteDynamicIdentifier;
	}

	public void setTestsuiteDynamicIdentifier(String testsuiteDynamicIdentifier) {
		this.testsuiteDynamicIdentifier = testsuiteDynamicIdentifier;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	@JsonIgnore
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProxyEntity other = (ProxyEntity) obj;
		if (proxy == null) {
			if (other.proxy != null)
				return false;
		} else if (!proxy.equals(other.proxy))
			return false;
		if (proxyIdentifier == null) {
			if (other.proxyIdentifier != null)
				return false;
		} else if (!proxyIdentifier.equals(other.proxyIdentifier))
			return false;
		if (proxyServer == null) {
			if (other.proxyServer != null)
				return false;
		} else if (!proxyServer.equals(other.proxyServer))
			return false;
		if (scanConfigurationParameters == null) {
			if (other.scanConfigurationParameters != null)
				return false;
		} else if (!scanConfigurationParameters
				.equals(other.scanConfigurationParameters))
			return false;
		if (testCaseName == null) {
			if (other.testCaseName != null)
				return false;
		} else if (!testCaseName.equals(other.testCaseName))
			return false;
		if (testCaseSuiteName == null) {
			if (other.testCaseSuiteName != null)
				return false;
		} else if (!testCaseSuiteName.equals(other.testCaseSuiteName))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	/**
	 * 
	 */
	@Override
	@JsonIgnore
	public String toString(){
		
		StringBuilder tempString = new StringBuilder();
		
		if(this.proxy != null){
			if(this.proxy.getProxyAddress() != null && !this.proxy.getProxyAddress().isEmpty()){
				tempString.append(this.proxy.getProxyAddress());
			}
			if(this.proxy.getProxyPort() > 0){
				tempString.append(":"+this.proxy.getProxyPort());
			}
		}
		
		if(this.proxyIdentifier != null && !this.proxyIdentifier.isEmpty()){
			tempString.append(":"+this.proxyIdentifier);
		}
		
		if(this.testCaseSuiteName != null && !this.testCaseSuiteName.isEmpty()){
			tempString.append(":"+this.testCaseSuiteName);
		}
		
		if(this.testCaseName != null && !this.testCaseName.isEmpty()){
			tempString.append(":"+this.testCaseName);
		}
		
		return tempString.toString();
	}
}
