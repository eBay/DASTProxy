package com.dastproxy.model;

import java.io.Serializable;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="issue_variant")
public class IssueVariant implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -583450823290884241L;
	
	@Id
	private String id;
	
	@OneToOne(mappedBy="issueVariant",fetch=FetchType.EAGER,orphanRemoval=true)
	@Cascade(CascadeType.ALL)
	@Nullable
	private Traffic traffic;
	
	@Id
	@ManyToOne(fetch=FetchType.LAZY)
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumns({
		@JoinColumn(name = "issue_id", referencedColumnName="issue_id", nullable = true),
		@JoinColumn(name = "report_id", referencedColumnName="report_id",nullable = true)
	})
	@Nullable
	@JsonIgnore
	private Issue issue;
	
	/*@OneToOne
	@Cascade(CascadeType.ALL)
	@JoinColumn(name="difference_id", nullable=true)
	private Difference difference;*/
	
	/**
	 * @return the traffic
	 */
	public Traffic getTraffic() {
		return traffic;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @param traffic the traffic to set
	 */
	public void setTraffic(Traffic traffic) {
		this.traffic = traffic;
	}
	/**
	 * @return the difference
	 *//*
	public Difference getDifference() {
		return difference;
	}
	*//**
	 * @param difference the difference to set
	 *//*
	public void setDifference(Difference difference) {
		this.difference = difference;
	}*/
	
	/**
	 * @return the issue
	 */
	public Issue getIssue() {
		return issue;
	}
	/**
	 * @param issue the issue to set
	 */
	public void setIssue(Issue issue) {
		this.issue = issue;
	}
	
	
}
