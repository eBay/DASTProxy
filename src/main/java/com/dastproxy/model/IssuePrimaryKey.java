/**
 * 
 */
package com.dastproxy.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * @author kshirali
 *
 */

@Embeddable
public class IssuePrimaryKey implements Serializable{

	private static final long serialVersionUID = -1011053140433710997L;
	
	@Column(name="issue_id")
	private String issueId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="report_id")
	@Cascade(CascadeType.ALL)
	private Report report;
	
	/**
	 * @return the report
	 */
	public Report getReport() {
		return report;
	}

	/**
	 * @param report the report to set
	 */
	public void setReport(Report report) {
		this.report = report;
	}

	/**
	 * @return issueId
	 */
	public String getIssueId() {
		return issueId;
	}
	
	/**
	 * @param issueId 
	 * 				the Issue Id to set
	 */
	public void setIssueId(final String issueId) {
		this.issueId = issueId;
	}

}
