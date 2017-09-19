package com.dastproxy.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="fp_reason")
public class FpReason {

	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="name")
	private String name;

	@Column(name="abbr")
	private String abbr;

	@Column(name="fp_text_pattern")
	private String fpPattern;


	@Column(name="date_created")
	private Date dateCreated;

	@Column(name="last_modified")
	private Date lastModified;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbbr() {
		return abbr;
	}

	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getFpPattern() {
		return fpPattern;
	}

	public void setFpPattern(String fpPattern) {
		this.fpPattern = fpPattern;
	}

	public Long getId() {
		return id;
	}
	
	
	
}