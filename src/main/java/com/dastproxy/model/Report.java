package com.dastproxy.model;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="report")
public class Report implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4748763213168527210L;

	@Id
	@Column(name="report_id")
	private String reportId;
	
	@Column(name="report_last_run", nullable=true)
	private String reportLastRun;
	
	
	/*
	 * 
	 * Note from Kiran Shirali: Ignoring issues to make sure cyclic dependency condition will not crop up.
	 * 
	 */
	@JsonIgnore
	@OneToMany(fetch=FetchType.EAGER, mappedBy="issuePrimaryKey.report")
	@Cascade(CascadeType.ALL)
	@Nullable
	private List<Issue> issues;
	
	/**
	 * @return the reportId
	 */
	public String getReportId() {
		return reportId;
	}
	/**
	 * @param reportId the reportId to set
	 */
	public void setReportId(final String reportId) {
		this.reportId = reportId;
	}
	
	/**
	 * @return the reportLastRun
	 */
	public String getReportLastRun() {
		return reportLastRun;
	}
	/**
	 * @param reportLastRun the reportLastRun to set
	 */
	public void setReportLastRun(String reportLastRun) {
		this.reportLastRun = reportLastRun;
	}
	
	/**
	 * @return the issues
	 */
	
	public List<Issue> getIssues() {
		return issues;
	}
	/**
	 * @param issues the issues to set
	 */
	public void setIssues(final List<Issue> issues) {
		this.issues = issues;
	}
	
}
