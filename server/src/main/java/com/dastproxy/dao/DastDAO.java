package com.dastproxy.dao;

import java.util.List;

import com.dastproxy.model.BreezeScanVO;
import com.dastproxy.model.FpReason;
import com.dastproxy.model.Issue;
import com.dastproxy.model.ProxyEntity;
import com.dastproxy.model.Recording;
import com.dastproxy.model.RecordingBatch;
import com.dastproxy.model.Scan;
import com.dastproxy.model.ScanBatch;
import com.dastproxy.model.User;

public interface DastDAO {

	public void saveEntity(ProxyEntity proxyEntity);
	public List<ProxyEntity> getEntities();
	public boolean removeEntity(ProxyEntity proxyEntity);
	
	public void saveScan(Scan scan);
	public void mergeScan(Scan scan);
	public List<Scan> getScansToBeTracked();
	public List<Scan> getScansForZap();
	public List<Scan> getAllScans();
	public List<Scan> getRecentScansWithTestsuiteNameAndSameOwner(String testSuiteName, String owner);

	
	//Below are DAO functions for Getting Metrics
	public List getActiveUserList();
	public Long getNoOfScansSuccessfullyRun();
	public Long getNoOfScansSetUpButNotRun();
	public Long getNoOfScansSetUpButInError();
	
	public Long getNoOfScansSetupViaBluefin();
	public Long getNoOfScansSetupViaDASTUI();
	
	public List getScanOverMonthsData();
	
	public Issue getIssueByNativeId(Long issueId);
	public Issue getIssueById(Long Id);
	/*public Issue getIssues(String userId, String scanId, String reportId);*/
	public void saveIssue(Issue issue);
	
	public void saveGenericEntity(Object object);
	public List<Recording> getAllRecordings();
	public Recording getRecording(Long recordingId);
	public List<Scan> getScansByUser(String userId);
	public Scan getScan(String scanId);
	
	public List<RecordingBatch> getRecordingBatches(String userId);
	public List<ScanBatch> getScanBatches(String userId, boolean isAdmin);
	public List<Recording> getRecordingsByBatchId(Long recordingBatchId);
	public RecordingBatch getRecordingBatch(Long id);
	public RecordingBatch getNightlyRecordingBatch(String userId);
	public void saveScanBatch(ScanBatch scanBatch);
	public ScanBatch getScanBatch(String userId, Long id, boolean isAdmin);
	public RecordingBatch getManualRecordingBatch(String owner);
	public RecordingBatch getRecBatchByTsDynamicIdentifier(String owner, String tsDynIdentifier);
	public ScanBatch getScanBatchByRecordingBatchId(String owner, Long recordingBatchId);
		
	public Object getEntity(Class clas, Long id);
	public User getUser(String userId);
	
	public List<FpReason> getFpReasonWithPattern();
	
	public List<Scan> getAllYesterdaysScans();
	public Integer getScanBatchIdOfScan(Long scanId);
	public List<RecordingBatch> getNightlyBatches();
	public List<ScanBatch> getNightlyCompletedScanBatches();
	public Scan getRecentNightlyScanByRecordingId(Long recordingId, Long scanId);
	
}
