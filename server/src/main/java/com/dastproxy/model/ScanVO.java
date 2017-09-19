package com.dastproxy.model;

import java.util.List;

public class ScanVO {

	public Long scanId;
	public String scanName;
	public String scanBatchLink;
	public String harUrl;
	public String projectUrl;
	public List<String> httpUrls;
	public Long getScanId() {
		return scanId;
	}
	public void setScanId(Long scanId) {
		this.scanId = scanId;
	}
	public String getScanName() {
		return scanName;
	}
	public void setScanName(String scanName) {
		this.scanName = scanName;
	}
	public String getHarUrl() {
		return harUrl;
	}
	public void setHarUrl(String harUrl) {
		this.harUrl = harUrl;
	}
	public String getScanBatchLink() {
		return scanBatchLink;
	}
	public void setScanBatchLink(String scanBatchLink) {
		this.scanBatchLink = scanBatchLink;
	}
	public String getProjectUrl() {
		return projectUrl;
	}
	public void setProjectUrl(String projectUrl) {
		this.projectUrl = projectUrl;
	}
	public List<String> getHttpUrls() {
		return httpUrls;
	}
	public void setHttpUrls(List<String> httpUrls) {
		this.httpUrls = httpUrls;
	}

}