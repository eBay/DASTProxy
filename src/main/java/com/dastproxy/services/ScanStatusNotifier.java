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
import com.dastproxy.common.constants.AppScanConstants;
import com.dastproxy.common.utils.AppScanUtils;
import com.dastproxy.common.utils.MailUtils;
import com.dastproxy.configuration.RootConfiguration;
import com.dastproxy.dao.DastDAO;
import com.dastproxy.model.Issue;
import com.dastproxy.model.Report;
import com.dastproxy.model.Scan;
import com.dastproxy.services.impl.AppScanEnterpriseRestService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScanStatusNotifier {

	private static final Logger LOGGER = LogManager
			.getLogger(ScanStatusNotifier.class.getName());
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
				+ AppScanConstants.APPSCAN_JOB_SCAN_STATUS_RECEIVER,
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
				+ AppScanConstants.APPSCAN_JOB_SCAN_STATUS_RECEIVER,
				AppScanConstants.APPSCAN_REPORT_SENDER,
				AppScanConstants.APPSCAN_REPORT_FOR_SCAN + " \"" + scanName
						+ "\"", model,
				AppScanConstants.REPORT_MAIL_BODY_TEMPLATE);
	}

	@Scheduled(cron = "0 0/1 * * * ?")
	public void scheduleJob() {
		LOGGER.debug("Running ScanStatusNotifier");
		if (RootConfiguration.getProperties()
				.getProperty(AppScanConstants.PROPERTIES_RUN_CRON_JOBS)
				.equalsIgnoreCase("true")) {
			try {

				dastApiService
						.loginToDASTScanner(
								RootConfiguration
										.getProperties()
										.getProperty(
												AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_ID_IDENTIFIER),
								RootConfiguration
										.getProperties()
										.getProperty(
												AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_PWD_IDENTIFIER));

				final List<Scan> listOfScansToBeTracked = getDao()
						.getScansToBeTracked();
				for (Scan eScan : listOfScansToBeTracked) {

					final String userId = dastApiService
							.checkIfUserPresent(eScan.getUser().getUserId());

					if (!AppScanUtils.isNotNull(userId)) {
						eScan.setToBeTracked(false);
						getDao().saveScan(eScan);

					} else {
						final String checkForScanId = eScan.getScanId();
						if (dastApiService.checkIfScanIsPresentForUser(userId,
								checkForScanId)) {
							eScan.setToBeTracked(false);
							getDao().saveScan(eScan);
						} else {
							final String scanState = dastApiService
									.checkForScanStarted(checkForScanId);
							if (scanState
									.equals(AppScanConstants.APPSCAN_JOB_SCAN_STATE_SUSPENDED)) {
								sendEmailForScanStatusToUser(
										eScan.getUser().getUserId(),
										checkForScanId,
										eScan.getScanName(),
										AppScanConstants.APPSCAN_JOB_SCAN_STATE_SUSPENDED,
										userId);
								eScan.setScanState(scanState);
								eScan.setToBeTracked(false);
								getDao().saveScan(eScan);
							} else {
								if (scanState
										.equals(AppScanConstants.APPSCAN_JOB_SCAN_STATE_RUNNING)
										&& !eScan.isEmailSent()) {
									sendEmailForScanStatusToUser(
											eScan.getUser().getUserId(),
											checkForScanId,
											eScan.getScanName(),
											AppScanConstants.APPSCAN_JOB_SCAN_STATE_RUNNING,
											userId);
									eScan.setScanState(scanState);
									eScan.setEmailSent(true);
									getDao().saveScan(eScan);
								} else {
									final String latestLastRunForScan = dastApiService
											.getLatestRunForScan(checkForScanId);
									if (AppScanUtils
											.isNotNull(latestLastRunForScan)
											&& !latestLastRunForScan
													.equalsIgnoreCase(eScan
															.getScanLastRun())) {

										final String latestLastRunForScanReport = dastApiService
												.getLatestRunForScanReport(eScan
														.getReport()
														.getReportId());
										final String lastRunForScanReport = eScan
												.getReport().getReportLastRun();
										if (AppScanUtils
												.isNotNull(latestLastRunForScanReport)
												&& !latestLastRunForScanReport
														.equalsIgnoreCase(lastRunForScanReport)) {

											if (AppScanUtils
													.isNotNull(dastApiService
															.getReport(eScan
																	.getReport()
																	.getReportId()))) {

												Report scanReport = eScan
														.getReport();
												scanReport
														.setIssues(dastApiService
																.getIssuesFromReport(
																		checkForScanId,
																		eScan.getReport(),
																		dastApiService
																				.getReport(eScan
																						.getReport()
																						.getReportId())));

												List<Issue> issuesInScan = AppScanUtils
														.returnDASTProxyRelativeUrlIssueList(scanReport);
												sendReportAfterScanCompleteToUser(
														eScan.getUser()
																.getUserId(),
														checkForScanId,
														eScan.getScanName(),
														AppScanConstants.APPSCAN_JOB_SCAN_STATE_COMPLETED,
														userId, issuesInScan,
														eScan.getTestCaseName());
												eScan.getReport()
														.setReportLastRun(
																latestLastRunForScanReport);
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

							}

						}
					}

				}

			} catch (MalformedURLException exception) {
				LOGGER.error("A " + exception.getClass().getSimpleName()
						+ " has occured in the application in the scheduler. ",
						exception);
			} catch (Exception exception) {
				LOGGER.error("A " + exception.getClass().getSimpleName()
						+ " has occured in the application in the scheduler. ",
						exception);
				AppScanUtils.sendErrorMail(exception);
			}
		} else {
			LOGGER.debug("Cron Jobs have been disabled. Exitting from ScanStatusNotifier");
		}

	}
}
