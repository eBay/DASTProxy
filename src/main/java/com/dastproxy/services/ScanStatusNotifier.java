/**
 * 
 * This class acts as a notifier when the scan start 
 * This has code for:
 * 1. Scheduling a job 
 * 2. Checking the scan status
 * 3. Sending an email when scan starts running with the link that shows the 
 *    scan metrics to respective user
 * 4. Sending a summary report for every scan completed to respective user
 * 
 * @author Rajvi Shah (rajvshah@paypal.com)
 * 
 */
package com.dastproxy.services;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dastproxy.common.constants.AppScanConstants;
import com.dastproxy.common.utils.AppScanUtils;
import com.dastproxy.common.utils.MailUtils;
import com.dastproxy.configuration.RootConfiguration;
import com.dastproxy.dao.DastDAO;
import com.dastproxy.model.Issue;
import com.dastproxy.model.Report;
import com.dastproxy.model.Scan;
import com.dastproxy.services.impl.AppScanEnterpriseRestService;

@Service
public class ScanStatusNotifier {

	private static final Logger LOGGER = LogManager.getLogger(ScanStatusNotifier.class.getName());
	private DASTApiService dastApiService = new AppScanEnterpriseRestService();

	@Inject
	@Qualifier("dastDAOImpl")
	private DastDAO dao;

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

	/**
	 * 
	 * @return dastApiService
	 */
	public DASTApiService getDastApiService() {
		return dastApiService;
	}

	public void setDastApiService(final DASTApiService dastApiService) {
		this.dastApiService = dastApiService;
	}

	// Email to be sent for scan running with the link
	private void sendEmailForScanStatusToUser(final String userId,
			final String scanId, final String scanName, final String state,
			final String userFolderId) {

		// parameters used to build an email template for scan status
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("userId", userFolderId);
		model.put("scanId", scanId);
		model.put("scanName", scanName);
		model.put("scanStatus", state);
		model.put("baseURL", AppScanConstants.APPSCAN_JOB_SCAN_STATUS_URL);
		model.put("contactUsSupportDl",
				AppScanConstants.APPSCAN_CONTACT_US_SUPPORT_DL);

		// Send email using Mail Utils

		MailUtils.sendEmail(userId
				+ RootConfiguration.getProperties().getProperty(AppScanConstants.EMAIL_DOMAIN),
				AppScanConstants.APPSCAN_REPORT_SENDER,
				AppScanConstants.APPSCAN_STATUS_FOR_SCAN + " \"" + scanName
						+ "\"", model,
				AppScanConstants.STATUS_MAIL_BODY_TEMPLATE);
	}

	// Email to be sent for a summary report of scan completed
	private void sendReportAfterScanCompleteToUser(final String userId,
			final String scanId, final String scanName, final String state,
			final String userFolderId, List<Issue> issues, String testCaseName) {

		// parameters used to build an email template for summary report
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("userId", userFolderId);
		model.put("scanId", scanId);
		model.put("scanName", scanName);
		model.put("issueDetails", issues);
		model.put("testCaseName", testCaseName);

		// Send email using Mail Utils
		MailUtils.sendEmail(userId
				+ RootConfiguration.getProperties().getProperty(AppScanConstants.EMAIL_DOMAIN),
				AppScanConstants.APPSCAN_REPORT_SENDER,
				AppScanConstants.APPSCAN_REPORT_FOR_SCAN + " \"" + scanName
						+ "\"", model,
				AppScanConstants.REPORT_MAIL_BODY_TEMPLATE);
	}


	@Scheduled(fixedRate = 120000)
	public void scheduleJob() {
		LOGGER.debug("Running ScanStatusNotifier");

		if (RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_RUN_CRON_JOBS).equalsIgnoreCase("true")) {
			try {
				dastApiService.loginToDASTScanner(RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_ID_IDENTIFIER),
								RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_PWD_IDENTIFIER));
				final List<Scan> listOfScansToBeTracked = getDao().getScansToBeTracked();

				if (listOfScansToBeTracked != null) LOGGER.debug("Running ScanStatusNotifier..listOfScansToBeTracked="+listOfScansToBeTracked.size());
				for (Scan eScan : listOfScansToBeTracked) {
					//The following is a duplicate try, have to fix it. But if an exception is thrown while updating a status it is not processing the remaining transactions.
					// Temporary fix. Need to optimize the try-catch statements in this method - Srinivas
					try{
					String userId = dastApiService.checkIfUserPresent(eScan.getUser().getUserId());
					LOGGER.debug("1..userId="+userId);
					/*
					if (!AppScanUtils.isNotNull(userId)) {
						dastApiService.loginToDASTScanner(RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_ID_IDENTIFIER),
								RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_PWD_IDENTIFIER));
						userId = dastApiService.checkIfUserPresent(eScan.getUser().getUserId());
					}
					*/
					boolean isScanNotPresentForUser = dastApiService.checkIfScanIsNotPresentForUser(userId,eScan.getScanId());
					LOGGER.debug("1..checkIfScanIsNotPresentForUser="+isScanNotPresentForUser);
					/*
					if (isScanNotPresentForUser){
						dastApiService.loginToDASTScanner(RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_ID_IDENTIFIER),
								RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_PWD_IDENTIFIER));
						try {
							isScanNotPresentForUser = dastApiService.checkIfScanIsNotPresentForUser(userId,eScan.getScanId());
						} catch (Exception e){
							isScanNotPresentForUser = true;
						}
					}
					LOGGER.debug("2..userId="+userId);
					LOGGER.debug("2..checkIfScanIsNotPresentForUser="+isScanNotPresentForUser);
					*/

					if (!AppScanUtils.isNotNull(userId) || isScanNotPresentForUser) {
						//eScan.setToBeTracked(false);
						//getDao().saveScan(eScan);
						continue;
					}
					final String scanState = dastApiService.checkForScanStarted(eScan.getScanId());

					if (scanState.equals(AppScanConstants.APPSCAN_JOB_SCAN_STATE_SUSPENDED)) {
						//sendEmailForScanStatusToUser(eScan.getUser().getUserId(),checkForScanId,eScan.getScanName(),AppScanConstants.APPSCAN_JOB_SCAN_STATE_SUSPENDED,userId);
								eScan.setScanState(scanState);
								eScan.setToBeTracked(false);
								getDao().saveScan(eScan);
					} else if (scanState.equals(AppScanConstants.APPSCAN_JOB_SCAN_STATE_RUNNING)) {
						//sendEmailForScanStatusToUser(eScan.getUser().getUserId(),checkForScanId,eScan.getScanName(),AppScanConstants.APPSCAN_JOB_SCAN_STATE_RUNNING,userId);
						if (!eScan.getScanState().equals(AppScanConstants.APPSCAN_JOB_SCAN_STATE_RUNNING)){
									eScan.setScanState(scanState);
									getDao().saveScan(eScan);
						}
					} else if (scanState.equals(AppScanConstants.APPSCAN_JOB_SCAN_STATE_READY)){
						LOGGER.debug("Updating the report as the scan jod is finished in the backend...scanId="+eScan.getScanId());
						final String latestLastRunForScan = dastApiService.getLatestRunForScan(eScan.getScanId());
						LOGGER.debug("Updating the report...latestLastRunForScan="+latestLastRunForScan);
						LOGGER.debug("Updating the report...eScan.getScanLastRun()="+eScan.getScanLastRun());

						if (AppScanUtils.isNotNull(latestLastRunForScan) && !latestLastRunForScan.equalsIgnoreCase(eScan.getScanLastRun())) {
							LOGGER.debug("Updating the report...eScan.getReport().getReportId()="+eScan.getReport().getReportId());

							final String latestLastRunForScanReport = dastApiService.getLatestRunForScanReport(eScan.getReport().getReportId());
							final String lastRunForScanReport = eScan.getReport().getReportLastRun();
							LOGGER.debug("Updating the report...latestLastRunForScanReport="+latestLastRunForScanReport);

							if (AppScanUtils.isNotNull(latestLastRunForScanReport)&& !latestLastRunForScanReport.equalsIgnoreCase(lastRunForScanReport)) {
								String reportFromScanEngine = dastApiService.getReport(eScan.getReport().getReportId());
								LOGGER.debug("Updating the report...reportFromScanEngine="+reportFromScanEngine);

								if (AppScanUtils.isNotNull(reportFromScanEngine)) {
									LOGGER.debug("Updating the report...");
									Report scanReport = eScan.getReport();
									scanReport.setIssues(dastApiService.getIssuesFromReport(eScan.getScanId(),eScan.getReport(),reportFromScanEngine));
									List<Issue> issuesInScan = AppScanUtils.returnDASTProxyRelativeUrlIssueList(scanReport);
									if (!eScan.isSetUpViaBluefin())
									sendReportAfterScanCompleteToUser(eScan.getUser().getUserId(),eScan.getScanId(),eScan.getScanName(),AppScanConstants.APPSCAN_JOB_SCAN_STATE_COMPLETED,userId, issuesInScan,eScan.getTestCaseName());
									eScan.getReport().setReportLastRun(latestLastRunForScanReport);
												eScan.setEmailSent(false);
												eScan.setScanLastRun(latestLastRunForScan);
												eScan.setScanState(scanState);
												eScan.setReport(scanReport);
												eScan.setToBeTracked(false);
												getDao().saveScan(eScan);
											}
										}
									}
								}
					} catch (MalformedURLException exception) {
						LOGGER.error("A " + exception.getClass().getSimpleName()+ " has occured in the application in the scheduler. ",exception);
					} catch (Exception exception) {
						LOGGER.error("A " + exception.getClass().getSimpleName()+ " has occured in the application in the scheduler. ",exception);
						AppScanUtils.sendErrorMail(exception);
					}
				}

			} catch (MalformedURLException exception) {
				LOGGER.error("A " + exception.getClass().getSimpleName()+ " has occured in the application in the scheduler. ",exception);
			} catch (Exception exception) {
				LOGGER.error("A " + exception.getClass().getSimpleName()+ " has occured in the application in the scheduler. ",exception);
				AppScanUtils.sendErrorMail(exception);
			}
		} else {
			LOGGER.debug("Cron Jobs have been disabled. Exitting from ScanStatusNotifier");
		}
	}
}
