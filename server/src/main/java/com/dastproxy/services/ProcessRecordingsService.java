package com.dastproxy.services;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dastproxy.common.constants.AppScanConstants;
import com.dastproxy.common.utils.AppScanUtils;
import com.dastproxy.common.utils.DastUtils;
import com.dastproxy.configuration.RootConfiguration;
import com.dastproxy.dao.DastDAO;
import com.dastproxy.model.DASTProxyException;
import com.dastproxy.model.ProxyEntity;
import com.dastproxy.model.Recording;
import com.dastproxy.model.RecordingBatch;
import com.dastproxy.model.Report;
import com.dastproxy.model.Scan;
import com.dastproxy.model.ScanBatch;
import com.dastproxy.model.User;
import com.dastproxy.services.impl.ZapService;

@Service
public class ProcessRecordingsService {

	// Logger for this class.
	private static final Logger LOGGER = LogManager
			.getLogger(ProcessRecordingsService.class.getName());

	@Inject
	private BrowserMobServiceBean browserMobServiceBean;

	@Inject
	@Qualifier("appScanEnterpriseRestService")
	private DASTApiService dastApiService;

	@Inject
	@Qualifier("dastDAOImpl")
	private DastDAO dao;
	
	@Autowired
	private ZapService zapService;

	
	/**
	 * @return the browserMobServiceBean
	 */
	public BrowserMobServiceBean getBrowserMobServiceBean() {
		return browserMobServiceBean;
	}

	/**
	 * @param browserMobServiceBean
	 *            the browserMobServiceBean to set
	 */
	public void setBrowserMobServiceBean(
			final BrowserMobServiceBean browserMobServiceBean) {
		this.browserMobServiceBean = browserMobServiceBean;
	}

	/**
	 * @return the dastApiService
	 */
	public DASTApiService getDastApiService() {
		return dastApiService;
	}

	/**
	 * @param dastApiService
	 *            the dastApiService to set
	 */
	public void setDastApiService(final DASTApiService dastApiService) {
		this.dastApiService = dastApiService;
	}

	/**
	 * @return the dao
	 */
	public DastDAO getDao() {
		return dao;
	}

	/**
	 * @param dao
	 *            the dao to set
	 */
	public void setDao(final DastDAO dao) {
		this.dao = dao;
	}

	@Scheduled(fixedRate = 120000)
	public void processSubmittedRecordings() {
		LOGGER.debug("Running processSubmittedRecordings");

		if (RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_RUN_CRON_JOBS).equalsIgnoreCase("true")) {

			List<ProxyEntity> proxyEntitiesToBeProcessed = null;

			try {

				proxyEntitiesToBeProcessed = dao.getEntities();

				if (LOGGER.isDebugEnabled() && !AppScanUtils.isNotNull(proxyEntitiesToBeProcessed)) {
					LOGGER.debug("ProxyEntitiesToBeProcessed from the database has returned no values");
				}

				for (final ProxyEntity proxyEntity : proxyEntitiesToBeProcessed) {
					LOGGER.debug("Inside the for loop for processing proxyEntity. Currently processing: "+ proxyEntity.toString());
					try {
						if (!AppScanUtils.isNotNull(proxyEntity.getErrorMessage())) {
							final String nameOfScan = proxyEntity.getScanConfiguration().getNameOfScan();
							final boolean startScanAutomatically = proxyEntity.getScanConfiguration().isStartScan();


							final String filePath = new StringBuilder(AppScanConstants.USER_HTD_FILES_LOCATION).append(File.separator).append(proxyEntity.getUser().getUserId()).append(File.separator).append(proxyEntity.getProxy().getHtdFileName()).toString();
							Scan successfullySetUpScan = new Scan();
							successfullySetUpScan.setReport(new Report());
							successfullySetUpScan.setFirstSetUp(new Date());
							successfullySetUpScan.setToBeTracked(true);
							successfullySetUpScan.setScanState(AppScanConstants.APPSCAN_JOB_READY_FOR_SCAN);
							
							if (successfullySetUpScan.getReport()==null) {
								successfullySetUpScan.setReport(new Report());
								successfullySetUpScan.getReport().setAseReportId("");
								dao.saveGenericEntity(successfullySetUpScan.getReport());
							}
							
							User user = new User();
							user.setUserId(proxyEntity.getUser().getUserId());
							successfullySetUpScan.setUser(user);

							dastApiService.setUpScanForUser(proxyEntity.getUser().getUserId(), "", filePath,nameOfScan, startScanAutomatically, successfullySetUpScan);
							if (AppScanUtils.isNotNull(successfullySetUpScan)) {

								successfullySetUpScan.setSetUpViaBluefin(true);
								successfullySetUpScan.setTestCaseName(proxyEntity.getTestCaseName());
								successfullySetUpScan.setTestSuiteName(proxyEntity.getTestCaseSuiteName());
								successfullySetUpScan.setBreezeUniqueTS(System.currentTimeMillis());
								if (proxyEntity.getTestsuiteDynamicIdentifier() != null ) {
									successfullySetUpScan.setTsDynamicIdentifier(proxyEntity.getTestsuiteDynamicIdentifier());
								} else {
									List<Scan> scans = dao.getRecentScansWithTestsuiteNameAndSameOwner(proxyEntity.getTestCaseSuiteName(), proxyEntity.getUser().getUserId());
									if (scans != null && scans.size() > 0){
										Scan scanFromDB = (Scan)scans.get(0);
										successfullySetUpScan.setTsDynamicIdentifier(scanFromDB.getTsDynamicIdentifier());
									} else {
										String randomStr = new BigInteger(130, new SecureRandom()).toString(32);
										successfullySetUpScan.setTsDynamicIdentifier("DynRan"+randomStr);
									}
								}
								dao.saveScan(successfullySetUpScan);
								DastUtils dastUtils = new DastUtils();
								Recording recording = dastUtils.createRecording(proxyEntity.getTestCaseName(), proxyEntity.getUser().getUserId(), filePath);
								RecordingBatch recordingBatch = dao.getRecBatchByTsDynamicIdentifier(proxyEntity.getUser().getUserId(), successfullySetUpScan.getTsDynamicIdentifier());
								if ( recordingBatch == null){
									recordingBatch = dastUtils.createRecordingBatch(proxyEntity.getTestCaseSuiteName(), proxyEntity.getUser().getUserId(), false, false);
									recordingBatch.setTestsuiteDynamicIdentifier(successfullySetUpScan.getTsDynamicIdentifier());
									dao.saveGenericEntity(recordingBatch);
								}

								dao.saveScan(successfullySetUpScan);

								ScanBatch scanBatch = dao.getScanBatchByRecordingBatchId(proxyEntity.getUser().getUserId(), recordingBatch.getId());

								if ( scanBatch == null){
									List<Scan> scans = new ArrayList<Scan>();
									scans.add(successfullySetUpScan);
									scanBatch = dastUtils.createScanBatch(recordingBatch.getId(), proxyEntity.getUser().getUserId(), proxyEntity.getTestCaseSuiteName(), scans);
									dao.saveGenericEntity(scanBatch);
								} else {
									scanBatch.getScans().add(successfullySetUpScan);
								}
								recording.setRecordingBatchId(recordingBatch.getId());
								successfullySetUpScan.setBatch(scanBatch);
								dao.saveGenericEntity(recording);
								successfullySetUpScan.setRecordingId(recording.getId());
								dao.saveScan(successfullySetUpScan);
								dao.removeEntity(proxyEntity);

								zapService.scanWithZap(recording.getHarFilename(), successfullySetUpScan.getReport());
								dao.saveGenericEntity(successfullySetUpScan.getReport());

							}
						} else {

							// There has been some error while trying to submit
							// the
							// scan at an earlier date.
							// The system will not try to submit it again.
							LOGGER.debug("The submitted scan has gone through a \'scan\' set up process. The error message recorded against this submission is: {}",proxyEntity.getErrorMessage());

						}
					} catch(IOException ioException){
						LOGGER.error(ioException);
						continue;
					}
					catch (Exception exception) {
						exception.printStackTrace();
						LOGGER.error("Error in processSubmittedRecording while trying to set up a scan for a particular Bluefin/Breeze/Selenium scan submission. The error is : "+ exception);
						LOGGER.error("exception.getErrorCode() = "+ ((DASTProxyException) exception).getErrorCode());
						// There is a possibility that the scans set up via
						// Bluefin/Breeze/Selenium could contain only external
						// URLS. In that case
						// the above logic will throw a custom exception with a
						// particular error code. Checking for that error code.
						// If it matches, then
						// I am sending an email out to the user explaining the
						// situation and then removing that submitted data from
						// our list.
						if ((exception instanceof DASTProxyException)) {
							proxyEntity.setErrorMessage(((DASTProxyException) exception).getErrorMessage());

							if (AppScanConstants.EXCEPTION_CODE_ONLY_EXTERNAL_URLS_FOR_SCAN.equalsIgnoreCase(((DASTProxyException) exception).getErrorCode())) {

								// parameters used to build an email template
								// for scan status
								/*
								Map<String, Object> emailModel = new HashMap<String, Object>();
								emailModel.put("testCaseName",proxyEntity.getTestCaseName());
								emailModel.put("reasonForRejection",AppScanConstants.ERROR_MESSAGE_TO_USER_ONLY_EXTERNAL_URLS_NOT_ACCEPTED);
								emailModel.put("contactUsSupportDl",AppScanConstants.APPSCAN_CONTACT_US_SUPPORT_DL);
								LOGGER.debug("--------------------------------Inside processSubmittedRecordings...exeption...4..RootConfiguration.getProperties().getProperty(AppScanConstants.EMAIL_DOMAIN)="+RootConfiguration.getProperties().getProperty(AppScanConstants.EMAIL_DOMAIN));
								String toEmail = proxyEntity.getUser().getUserId()+ RootConfiguration.getProperties().getProperty(AppScanConstants.EMAIL_DOMAIN);
								MailUtils.sendEmail(toEmail,AppScanConstants.APPSCAN_REPORT_SENDER,"Scan has been rejected",emailModel, "scanRejected.vm");
								*/
								dao.removeEntity(proxyEntity);
								continue;
							}
						}

						if(AppScanUtils.isNotNull(exception) && AppScanUtils.isNotNull(exception.getCause())){
							proxyEntity.setErrorMessage(exception.getCause().toString());
							dao.saveEntity(proxyEntity);
						}
						continue;
					}
				}
			} catch (Exception exception) {
				LOGGER.error("Error in processSubmittedRecording: " + exception);
			}
		} else {
			LOGGER.debug("Cron Jobs have been disabled. Exitting from processSubmittedRecording");
		}
	}
}
