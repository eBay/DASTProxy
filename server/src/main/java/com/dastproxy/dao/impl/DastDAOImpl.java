/**
 * This is the file that contains the DAO Implementation for DASTProxy.
 * Currently it uses Hibernate to connect to a MySQL database
 * and perform CRUD operations.
 *
 * @author Kiran Shirali (kshirali@ebay.com)
 */

package com.dastproxy.dao.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dastproxy.common.constants.AppScanConstants;
import com.dastproxy.dao.DastDAO;
import com.dastproxy.model.FpReason;
import com.dastproxy.model.Issue;
import com.dastproxy.model.ProxyEntity;
import com.dastproxy.model.Recording;
import com.dastproxy.model.RecordingBatch;
import com.dastproxy.model.Scan;
import com.dastproxy.model.ScanBatch;
import com.dastproxy.model.User;
import com.ibm.icu.util.Calendar;

@Repository("dastDAOImpl")
@Transactional
public class DastDAOImpl implements DastDAO {

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 *
	 * Save the details of a submitted proxy entity
	 *
	 * @param Proxy
	 * Entity Details to be saved
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
		final List<ProxyEntity> entities = session.createCriteria(ProxyEntity.class).setMaxResults(5).list();
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
		try {
		final Session session = this.sessionFactory.openSession();
		final Transaction transaction = session.beginTransaction();
		session.saveOrUpdate(scan);
		transaction.commit();
		session.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	/**
	 *
	 * @param
	 */
	public void mergeScan(final Scan scan) {
		try {
		final Session session = this.sessionFactory.openSession();
		final Transaction transaction = session.beginTransaction();
		session.merge(scan);
		transaction.commit();
		session.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}


	public List<Scan> getScansToBeTracked() {
		final Session session = this.sessionFactory.openSession();
		List<Scan> scans = null;
		try {
		scans = session
				.createQuery("from Scan scan where toBeTracked = :toBeTracked order by firstSetUp asc").setMaxResults(10)
				.setParameter("toBeTracked", true).list();
		session.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		return scans;
	}

	public List<Scan> getScansForZap() {
		final Session session = this.sessionFactory.openSession();
		final List<Scan> scans = session
				.createQuery("from Scan scan where zap_state = :zapState")
				.setParameter("zapState", "New").list();
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
	public Issue getIssueByNativeId(final Long issueId) {

		final Session session = this.sessionFactory.openSession();

		final Issue issue = (Issue) session.createQuery("from Issue issue where issue.nativeIssueId = :issueId").setParameter("issueId", ""+issueId).uniqueResult();
		System.out.println("--------Inside DAO Impl...getIssueByNativeId...issue.toString()="+issue.toString());
		session.close();
		return issue;
	}

	@Override
	public Issue getIssueById(final Long id) {

		Issue issue = null;
		try{
			final Session session = this.sessionFactory.openSession();
			issue = (Issue) session.createQuery("from Issue issue where issue.id = :issueId").setParameter("issueId", id).uniqueResult();
			System.out.println("--------Inside DAO Impl...getIssueById...issue.toString()="+issue.toString());
			session.close();
		}catch(Exception e){
			e.printStackTrace();
		}

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
		try {
			final Session session = this.sessionFactory.openSession();
			final Transaction transaction = session.beginTransaction();
			session.saveOrUpdate(entity);
			transaction.commit();
			session.close();
		}catch(Exception e){
			e.printStackTrace();
		}

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

	public List<ScanBatch> getScanBatches(String owner, boolean isAdmin){
		List<ScanBatch> scanBatches = null;
		try {
			System.out.println("----------------------------------------Inside DAO.getScanBatches...1");
			final Session session = this.sessionFactory.openSession();
			System.out.println("----------------------------------------Inside DAO.getScanBatches...2");
			//final List<ScanBatch> scanBatches = session.createQuery("from ScanBatch scanBatch where scanBatch.owner = :user_id order by scanBatch.dateCreated desc")
			Criteria criteria = session.createCriteria(ScanBatch.class).setMaxResults(30).setFetchSize(5).addOrder( Order.desc("dateCreated") ).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			System.out.println("----------------------------------------Inside DAO.getScanBatches...3");
			if (!isAdmin) {
				criteria.add(Restrictions.eq("owner", owner));
			}
			criteria.setMaxResults(40);
			System.out.println("----------------------------------------Inside DAO.getScanBatches...4.."+isAdmin);
			scanBatches = criteria.list();
			System.out.println("----------------------------------------Inside DAO.getScanBatches...5");
			session.close();
		} catch (Exception e){
			System.out.println("----------------------------------------Inside DAO.getScanBatches...6");
			e.printStackTrace();
		}
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
	public RecordingBatch getNightlyRecordingBatch(String userId){
		final Session session = this.sessionFactory.openSession();
		RecordingBatch batch = null;
		try {
			batch = (RecordingBatch)session.createCriteria(RecordingBatch.class).add(Restrictions.eq("testsuiteName", "Nightly Batch")).add(Restrictions.eq("isNightlyBatch", true)).add(Restrictions.eq("owner", userId)).uniqueResult();
		} catch(Exception e){
			e.printStackTrace();
		}
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

	public ScanBatch getScanBatch(String userId, Long id, boolean isAdmin){
		final Session session = this.sessionFactory.openSession();
		//final List<ScanBatch> scanBatches = session.createQuery("from ScanBatch scanBatch where scanBatch.owner = :user_id order by scanBatch.dateCreated desc")
		Criteria criteria = session.createCriteria(ScanBatch.class).add(Restrictions.eq("id", id));
		if (!isAdmin) criteria.add(Restrictions.eq("owner", userId));
		final ScanBatch scanBatch = (ScanBatch)criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
		session.close();
		return scanBatch;
	}

	public Object getEntity(Class clas, Long id){
		final Session session = this.sessionFactory.openSession();
		Object obj = session.createCriteria(clas).add(Restrictions.eq("id", id)).uniqueResult();
		System.out.println("------------------------obj.getClass()="+obj.getClass());
		session.close();
		return obj;

	}

	public User getUser(String userId){
		final Session session = this.sessionFactory.openSession();
		User user = (User)session.createCriteria(User.class).add(Restrictions.eq("userId", userId)).uniqueResult();
		session.close();
		return user;

	}

	public List<FpReason> getFpReasonWithPattern(){
		final Session session = this.sessionFactory.openSession();
		final List<FpReason> recordings = (List<FpReason>)session.createQuery("from FpReason fpReason where fpReason.fpPattern is not null").list();
		session.close();
		return recordings;
	}

	public List<Scan> getAllYesterdaysScans(){
		Calendar yestCal = Calendar.getInstance();
		yestCal.add(Calendar.DATE, -7);
		Date yestDate = yestCal.getTime();
		System.out.println("-----------------yestDate="+yestDate);

		final Session session = this.sessionFactory.openSession();
		List<Scan> scans = null;
		try {
			scans = session
					.createQuery("from Scan scan where scanState = :scanState and firstSetUp > :firstSetUp")
					.setParameter("scanState", "Ready").setParameter("firstSetUp", yestDate).list();
			session.close();

		} catch(Exception e){
			e.printStackTrace();
		}
		return scans;
	}

	public Integer getScanBatchIdOfScan(Long scanId) {
		//TODO: Integer is coming from the query - need to look why it is not long

		final Session session = this.sessionFactory.openSession();
		Integer ret = null;
		try {
			String sql = "select scan_batch_id from dast_db.scan where id= :scanId";
			Query query = session.createSQLQuery(sql).setParameter("scanId", scanId);
			List results = query.list();
			if (results !=null && results.size()>0 ) ret = (Integer)results.get(0);
		} catch(Exception ex){
			ex.printStackTrace();
		}
		session.close();
		return ret;
	}

	public List<RecordingBatch> getNightlyBatches(){
		final Session session = this.sessionFactory.openSession();
		final List<RecordingBatch> recordingBatches = session.createCriteria(RecordingBatch.class)
					.add(Restrictions.eq("isNightlyBatch", true))
					.addOrder(Order.desc("dateCreated"))
					.list();
		session.close();
		return recordingBatches;
	}
	
	public List<ScanBatch> getNightlyCompletedScanBatches(){
		final Session session = this.sessionFactory.openSession();
		final List<ScanBatch> recordingBatches = session.createCriteria(ScanBatch.class)
					.add(Restrictions.eq("isNightlyBatch", true))
					.add(Restrictions.eq("nightlyBatchState", ScanBatch.CREATED))
					.addOrder(Order.desc("dateCreated"))
					.list();
		session.close();
		return recordingBatches;
		
	}
	
	public Scan getRecentNightlyScanByRecordingId(Long recordingId, Long scanId){
		final Session session = this.sessionFactory.openSession();
		List<Scan> scans = null;
		Scan scan = null;
		try {
		scans = session
				.createQuery("from Scan scan where recordingId = :recordingId and id < :scanId order by id desc").setMaxResults(1)
				.setParameter("recordingId", recordingId)
				.setParameter("scanId", scanId)
				.list();
		if (scans!=null && scans.size()>0) scan = scans.get(0);
		session.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		return scan;
	}
	//public List<Issue> getMasterListOfIssuesForNightlyBatch(Long scanBatchId){
		
	//}

}
