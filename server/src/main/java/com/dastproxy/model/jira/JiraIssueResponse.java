package com.dastproxy.model.jira;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonTypeName;

@Entity
@Table(name = "jira")
@JsonTypeName("jira")
public class JiraIssueResponse {
	
		@Transient
		private String id;
		
		@Id
		@Column(name="jira_key", nullable=false)
		private String key;
		@Column(name="self")
		private String self;
		
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
		 * @return the key
		 */
		public String getKey() {
			return key;
		}
		/**
		 * @param key the key to set
		 */
		public void setKey(String key) {
			this.key = key;
		}
		/**
		 * @return the self
		 */
		public String getSelf() {
			return self;
		}
		/**
		 * @param self the self to set
		 */
		public void setSelf(String self) {
			this.self = self;
		}
}
