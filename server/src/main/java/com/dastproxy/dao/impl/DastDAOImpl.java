/**
 * This is the file that contains the DAO Implementation for DASTProxy. 
 * Currently it uses Hibernate to connect to a MySQL database 
 * and perform CRUD operations.
 * 
 * @author Kiran Shirali (kshirali@ebay.com)
 */

package com.dastproxy.dao.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.dastproxy.common.constants.AppScanConstants;
import com.dastproxy.dao.DastDAO;
import com.dastproxy.model.BreezeScanVO;
import com.dastproxy.model.Issue;
import com.dastproxy.model.ProxyEntity;
import com.dastproxy.model.Recording;
import com.dastproxy.model.RecordingBatch;
import com.dastproxy.model.Report;
import com.dastproxy.model.Scan;
import com.dastproxy.model.ScanBatch;

@Service
@Qualifier("dastDAOImpl")
public class DastDAOImpl implements DastDAO {

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 
	 * Save the details of a submitted proxy entity
	 * 
	 * @param Proxy
	 *            Entity Details to be saved
	 */
	public void saveEntity(final ProxyEntity proxyEntity) {

		final Session session = this.sessionFactory.openSession();
		final Transaction transaction = session.beginTransaction();
		session.saveOrUpdate(proxyEntity);
		transaction.commit();
		session.close();

	}

	/**
	 * 
	 * Get the list of proxy entities currently residing in the database.
	 * 
	 */
	public List<ProxyEntity> getEntities() {
		final Session session = this.sessionFactory.openSession();
		final List<ProxyEntity> entities = session.createCriteria(ProxyEntity.class).list();
		session.close();
		return entities;
	}

	/**
	 * Remove the details of a submitted proxy entity
	 * 
	 * @param Proxy
	 *            Entity Details to be deleted
	 */
	public boolean removeEntity(final ProxyEntity proxyEntity) {

		final Session session = this.sessionFactory.openSession();
		final Transaction transaction = session.beginTransaction();
		session.delete(proxyEntity);
		transaction.commit();
		session.close();
		return true;
	}

	/**
	 * 
	 * @param
	 */
	public void saveScan(final Scan scan) {
		final Session session = this.sessionFactory.openSession();
		final Transaction transaction = session.beginTransaction();
		session.saveOrUpdate(scan);
		transaction.commit();
		session.close();
	}

	public List<Scan> getScansToBeTracked() {
		final Session session = this.sessionFactory.openSession();
		final List<Scan> scans = session
				.createQuery("from Scan scan where toBeTracked = :toBeTracked")
				.setParameter("toBeTracked", true).list();
		session.close();
		return scans;
	}

	public List<Scan> getRecentScansWithTestsuiteNameAndSameOwner(String testSuiteName, String owner) {
		final Session session = this.sessionFactory.openSession();
		final List<Scan> scans = session
				.createQuery("from Scan scan where toBeTracked = :toBeTracked and testSuiteName = :testSuiteName and user.userId = :userId and breezeUniqueTS is not null and breezeUniqueTS > :breezeUniqueTS")
				.setParameter("toBeTracked", true)
				.setParameter("testSuiteName", testSuiteName)
				.setParameter("userId", owner)
				.setParameter("breezeUniqueTS", System.currentTimeMillis() - (15 * 60 * 1000))
				.list();
		session.close();
		return scans;
	}

	public List<Scan> getAllScans() {
		final Session session = this.sessionFactory.openSession();
		final List<Scan> scans = session.createCriteria(Scan.class).list();
		session.close();
		return scans;
	}

	public List getActiveUserList() {
		final Session session = this.sessionFactory.openSession();

		List<String> adminUsers = new ArrayList<String>();

		Query query = session.createQuery(
				"SELECT scan.user.userId, count(*) FROM Scan scan "
						+ "WHERE scan.scanLastRun is NOT NULL "
						+ "and scan.user.userId not in (:adminusers) "
						+ "GROUP BY scan.user.userId").setParameterList(
				"adminusers", adminUsers);
		query.setCacheable(false);
		List uniqueUserList = query.list();
		session.close();
		return uniqueUserList;
	}

	public Long getNoOfScansSuccessfullyRun() {
		final Session session = this.sessionFactory.openSession();
		List<String> adminUsers = new ArrayList<String>();

		Query query = session.createQuery(
				"SELECT count(*) FROM Scan scan "
						+ "WHERE scan.scanLastRun is NOT NULL "
						+ "and scan.user.userId not in (:adminusers) ")
				.setParameterList("adminusers", adminUsers);
		Long count = (Long) query.uniqueResult();
		session.close();
		return count;
	}

	public Long getNoOfScansSetUpButNotRun() {
		final Session session = this.sessionFactory.openSession();

		List<String> scanStates = new ArrayList<String>();
		scanStates.add(AppScanConstants.APPSCAN_JOB_SCAN_STATE_READY);

		List<String> adminUsers = new ArrayList<String>();

		Query query = session
				.createQuery(
						"SELECT count(*) FROM Scan scan "
								+ "WHERE scan.scanLastRun is NULL "
								+ "and scan.user.userId not in (:adminusers) "
								+ "and scanState in (:states)")
				.setParameterList("adminusers", adminUsers)
				.setParameterList("states", scanStates);
		Long count = (Long) query.uniqueResult();
		session.close();
		return count;
	}

	public Long getNoOfScansSetUpButInError() {
		final Session session = this.sessionFactory.openSession();

		List<String> scanStates = new ArrayList<String>();
		scanStates.add(AppScanConstants.APPSCAN_JOB_SCAN_STATE_SUSPENDED);

		List<String> adminUsers = new ArrayList<String>();

		Query query = session
				.createQuery(
						"SELECT count(*) FROM Scan scan "
								+ "WHERE scanState in (:states) "
								+ "and scan.user.userId not in (:adminusers) ")
				.setParameterList("states", scanStates)
				.setParameterList("adminusers", adminUsers);
		Long count = (Long) query.uniqueResult();
		session.close();
		return count;
	}

	public Long getNoOfScansSetupViaBluefin() {

		final Session session = this.sessionFactory.openSession();

		List<String> adminUsers = new ArrayList<String>();

		Query query = session.createQuery(
				"SELECT count(*) FROM Scan scan "
						+ "WHERE scan.setUpViaBluefin is true "
						+ "and scan.scanLastRun is not NULL "
						+ "and scan.user.userId not in (:adminusers) ")
				.setParameterList("adminusers", adminUsers);
		Long count = (Long) query.uniqueResult();
		session.close();
		return count;

	}

	public Long getNoOfScansSetupViaDASTUI() {

		final Session session = this.sessionFactory.openSession();

		List<String> adminUsers = new ArrayList<String>();

		Query query = session.createQuery(
				"SELECT count(*) FROM Scan scan "
						+ "WHERE scan.setUpViaBluefin is false "
						+ "and scan.scanLastRun is not NULL "
						+ "and scan.user.userId not in (:adminusers) ")
				.setParameterList("adminusers", adminUsers);
		Long count = (Long) query.uniqueResult();
		session.close();
		return count;

	}

	@Override
	public List getScanOverMonthsData() {

		final Session session = this.sessionFactory.openSession();

		List<String> adminUsers = new ArrayList<String>();

		Query query = session
				.createQuery(
						"SELECT CONCAT(MONTHNAME(scan.firstSetUp),\' \' ,YEAR(scan.firstSetUp)) , COUNT(*) FROM Scan scan "
								+ "where (scan.scanLastRun IS NOT NULL) "
								+ "and scan.user.userId not in (:adminusers) "
								+ "GROUP BY MONTHNAME(scan.firstSetUp) "
								+ "ORDER BY Year(scan.firstSetUp), Month(scan.firstSetUp)")
				.setParameterList("adminusers", adminUsers);

		List monthAndScanData = query.list();
		session.close();
		return monthAndScanData;
	}

	@Override
	public Issue getIssue(final String issueId, final String reportId) {

		final Session session = this.sessionFactory.openSession();

		final Report report = (Report) session
				.createQuery(
						"from Report report where report.reportId = :reportId")
				.setParameter("reportId", reportId).uniqueResult();

		final Issue issue = (Issue) session
				.createQuery(
						"from Issue issue where issue.issuePrimaryKey.issueId = :issueId and issue.issuePrimaryKey.report = :report")
				.setParameter("issueId", issueId)
				.setParameter("report", report).uniqueResult();
		session.close();
		return issue;
	}

	/**
	 *  Save an issue
	 */
	@Override
	public void saveIssue(Issue issue) {
		final Session session = this.sessionFactory.openSession();
		final Transaction transaction = session.beginTransaction();
		session.saveOrUpdate(issue);
		transaction.commit();
		session.close();
		
	}

	/**
	 *  Save a recording
	 */
	@Override
	public void saveGenericEntity(Object entity){
		final Session session = this.sessionFactory.openSession();
		final Transaction transaction = session.beginTransaction();
		session.saveOrUpdate(entity);
		transaction.commit();
		session.close();

	}

	public List<Recording> getAllRecordings(){
		final Session session = this.sessionFactory.openSession();
		final List<Recording> recordings = session.createCriteria(Recording.class).addOrder( Order.desc("dateCreated") ).list();
		session.close();
		return recordings;

	}
	public Recording getRecording(Long recordingId){
		final Session session = this.sessionFactory.openSession();
		final Recording recording = session.get(Recording.class, recordingId);
		session.close();
		return recording;

	}

	public List<Scan> getScansByUser(String userId){
		final Session session = this.sessionFactory.openSession();
		final List<Scan> scans = session.createQuery("from Scan scan where scan.user.userId = :user_id and scan.setUpViaBluefin is false order by scan.firstSetUp desc")
				.setParameter("user_id", userId).list();
		Comparator<Scan> byScanIdcomparator = (Scan s1, Scan s2)->s2.getScanId().compareTo(s1.getScanId());
		scans.sort(byScanIdcomparator);
		session.close();
		return scans;

	}

	public Scan getScan(String scanId){
		final Session session = this.sessionFactory.openSession();
		final Scan scan = session.get(Scan.class, scanId);
		session.close();
		return scan;
	}

	public List<RecordingBatch> getRecordingBatches(String owner){
		final Session session = this.sessionFactory.openSession();
		final List<RecordingBatch> recordingBatches = session.createCriteria(RecordingBatch.class)
					.add(Restrictions.eq("owner", owner))
					.addOrder(Order.desc("manualTestBatch"))
					.addOrder(Order.desc("dateCreated"))
					.list();
		session.close();
		return recordingBatches;
	}
	public RecordingBatch getManualRecordingBatch(String owner){
		final Session session = this.sessionFactory.openSession();
		RecordingBatch batch = (RecordingBatch)session.createQuery("from RecordingBatch batch where batch.manualTestBatch is true and owner = :userId")
				.setParameter("userId", owner).uniqueResult();
		session.close();
		return batch;
	}
	public RecordingBatch getRecBatchByTsDynamicIdentifier(String owner, String tsDynIdentifier){

		final Session session = this.sessionFactory.openSession();
		RecordingBatch batch = (RecordingBatch)session.createQuery("from RecordingBatch batch where owner=:userId and testsuiteDynamicIdentifier=:tsDynIdentifier")
				.setParameter("userId", owner)
				.setParameter("tsDynIdentifier", tsDynIdentifier)
				.uniqueResult();
		session.close();
		return batch;
	}

	public ScanBatch getScanBatchByRecordingBatchId(String owner, Long recordingBatchId){

		final Session session = this.sessionFactory.openSession();
		ScanBatch batch = (ScanBatch)session.createQuery("from ScanBatch batch where owner=:userId and recordingBatchId=:recordingBatchId")
				.setParameter("userId", owner)
				.setParameter("recordingBatchId", recordingBatchId)
				.uniqueResult();
		session.close();
		return batch;

	}

	public List<ScanBatch> getScanBatches(String owner){
		final Session session = this.sessionFactory.openSession();
		//final List<ScanBatch> scanBatches = session.createQuery("from ScanBatch scanBatch where scanBatch.owner = :user_id order by scanBatch.dateCreated desc")
		final List<ScanBatch> scanBatches = session.createCriteria(ScanBatch.class).add(Restrictions.eq("owner", owner)).addOrder( Order.desc("dateCreated") ).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		session.close();
		return scanBatches;
	}

	public List<Recording> getRecordingsByBatchId(Long recordingBatchId){
		final Session session = this.sessionFactory.openSession();
		final List<Recording> recordings = session.createQuery("from Recording recording where recording.recordingBatchId = :recordingBatchId order by dateCreated desc")
				.setParameter("recordingBatchId", recordingBatchId).list();
		session.close();
		return recordings;

	}

	public RecordingBatch getRecordingBatch(Long id){
		final Session session = this.sessionFactory.openSession();
		RecordingBatch batch = (RecordingBatch)session.createCriteria(RecordingBatch.class).add(Restrictions.eq("id", id)).uniqueResult();
		session.close();
		return batch;

	}

	public void saveScanBatch(ScanBatch scanBatch){
		final Session session = this.sessionFactory.openSession();
		final Transaction transaction = session.beginTransaction();
		session.saveOrUpdate(scanBatch);
		transaction.commit();
		session.close();
	}

	public ScanBatch getScanBatch(String userId, Long id){
		final Session session = this.sessionFactory.openSession();
		//final List<ScanBatch> scanBatches = session.createQuery("from ScanBatch scanBatch where scanBatch.owner = :user_id order by scanBatch.dateCreated desc")
		final ScanBatch scanBatch = (ScanBatch)session.createCriteria(ScanBatch.class).add(Restrictions.eq("id", id)).add(Restrictions.eq("owner", userId)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
		session.close();
		return scanBatch;

	}
}
