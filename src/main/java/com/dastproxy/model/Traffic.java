package com.dastproxy.model;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Kiran Shirali (kshirali@ebay.com)
 *
 */

@Entity
@Table(name="traffic")
public class Traffic implements Serializable{	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7073584390911537904L;
	
	@Column(name="test_http_traffic")
	private String testHttpTraffic;
	@Column(name="original_http_traffic")
	private String originalHttpTraffic;
	
	@Id
	@OneToOne(fetch=FetchType.LAZY)
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumns({
		@JoinColumn(name = "issue_id", referencedColumnName="issue_id", nullable = true),
		@JoinColumn(name = "report_id", referencedColumnName="report_id",nullable = true),
		@JoinColumn(name = "issue_variant_id", referencedColumnName="id",nullable = true)
	})
	@Nullable
	@JsonIgnore
	private IssueVariant issueVariant;
	
	/**
	 * @return the testHttpTraffic
	 */
	public String getTestHttpTraffic() {
		return testHttpTraffic;
	}
	/**
	 * @param testHttpTraffic the testHttpTraffic to set
	 */
	public void setTestHttpTraffic(String testHttpTraffic) {
		this.testHttpTraffic = testHttpTraffic;
	}
	/**
	 * @return the originalHttpTraffic
	 */
	public String getOriginalHttpTraffic() {
		return originalHttpTraffic;
	}
	/**
	 * @param originalHttpTraffic the originalHttpTraffic to set
	 */
	public void setOriginalHttpTraffic(String originalHttpTraffic) {
		this.originalHttpTraffic = originalHttpTraffic;
	}
	/**
	 * @return the issueVariant
	 */
	public IssueVariant getIssueVariant() {
		return issueVariant;
	}
	/**
	 * @param issueVariant the issueVariant to set
	 */
	public void setIssueVariant(IssueVariant issueVariant) {
		this.issueVariant = issueVariant;
	}
	
	
	
}
