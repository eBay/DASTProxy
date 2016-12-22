/**
 * 
 */
package com.dastproxy.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Kiran Shirali (kshirali@ebay.com)
 *
 */
@Entity
@Table(name="request_modification")
public class RequestModification implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2210866295332070241L;
	
	@Id
	@GeneratedValue
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	@Column(name="modified_value")
	private String modifiedValue;
	@Column(name="original_value")
	private String originalValue;
	/**
	 * @return the id
	 */
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
	 * @return the modifiedValue
	 */
	public String getModifiedValue() {
		return modifiedValue;
	}
	/**
	 * @param modifiedValue the modifiedValue to set
	 */
	public void setModifiedValue(String modifiedValue) {
		this.modifiedValue = modifiedValue;
	}
	/**
	 * @return the originalValue
	 */
	public String getOriginalValue() {
		return originalValue;
	}
	/**
	 * @param originalValue the originalValue to set
	 */
	public void setOriginalValue(String originalValue) {
		this.originalValue = originalValue;
	}
	
	
}
