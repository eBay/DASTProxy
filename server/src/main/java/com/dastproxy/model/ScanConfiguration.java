package com.dastproxy.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="scan_configuration")
public class ScanConfiguration implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8658154276905811743L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", insertable = true, updatable = true)
	private Long id;
	@Column(name="start_scan")
	private boolean startScan;
	@Column(name="name_of_scan")
	private String nameOfScan;
	
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
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the startScan
	 */
	public boolean isStartScan() {
		return startScan;
	}
	/**
	 * @param startScan the startScan to set
	 */
	public void setStartScan(boolean startScan) {
		this.startScan = startScan;
	}
	/**
	 * @return the nameOfScan
	 */
	public String getNameOfScan() {
		return nameOfScan;
	}
	/**
	 * @param nameOfScan the nameOfScan to set
	 */
	public void setNameOfScan(String nameOfScan) {
		this.nameOfScan = nameOfScan;
	}
}
