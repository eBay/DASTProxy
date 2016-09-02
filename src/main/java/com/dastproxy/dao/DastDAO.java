package com.dastproxy.dao;

import java.util.List;
import com.dastproxy.model.Issue;
import com.dastproxy.model.ProxyEntity;
import com.dastproxy.model.Scan;

public interface DastDAO {

	public void saveEntity(ProxyEntity proxyEntity);
	public List<ProxyEntity> getEntities();
	public boolean removeEntity(ProxyEntity proxyEntity);
	
	public void saveScan(Scan scan);
	public List<Scan> getScansToBeTracked();
	public List<Scan> getAllScans();
	
	//Below are DAO functions for Getting Metrics
	public List getActiveUserList();
	public Long getNoOfScansSuccessfullyRun();
	public Long getNoOfScansSetUpButNotRun();
	public Long getNoOfScansSetUpButInError();
	
	public Long getNoOfScansSetupViaBluefin();
	public Long getNoOfScansSetupViaDASTUI();
	
	public List getScanOverMonthsData();
	
	public Issue getIssue(String issueId, String reportId);
	public void saveIssue(Issue issue);
	
}
