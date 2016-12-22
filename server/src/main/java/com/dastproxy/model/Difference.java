/**
 * 
 */
package com.dastproxy.model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * @author Kiran Shirali (kshirali@ebay.com)
 *
 */
@Entity
@Table(name="difference")
public class Difference {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@OneToMany
	@Cascade(CascadeType.ALL)
	@JoinColumn(name="difference_id", nullable=true)
	private List<RequestModification> requestModifications;

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
	 * @return the requestModifications
	 */
	public List<RequestModification> getRequestModifications() {
		return requestModifications;
	}

	/**
	 * @param requestModifications the requestModifications to set
	 */
	public void setRequestModifications(
			List<RequestModification> requestModifications) {
		this.requestModifications = requestModifications;
	}
	
	
}
