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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dastproxy.common.constants.AppScanConstants;
import com.dastproxy.common.utils.AppScanUtils;
import com.dastproxy.common.utils.MailUtils;
import com.dastproxy.configuration.RootConfiguration;
import com.dastproxy.dao.DastDAO;
import com.dastproxy.model.DASTProxyException;
import com.dastproxy.model.Issue;
import com.dastproxy.model.Recording;
import com.dastproxy.model.Report;
import com.dastproxy.model.Scan;
import com.dastproxy.model.User;
import com.dastproxy.services.impl.ZapService;

@Service
public class ScanStatusNotifier {

	private static final Logger LOGGER = LogManager.getLogger(ScanStatusNotifier.class.getName());

	@Autowired
	private ZapService zapService;

	@Autowired
	private DASTApiService dastApiService;

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

	public ZapService getZapService() {
		return zapService;
	}

	public void setZapService(ZapService zapService) {
		this.zapService = zapService;
	}

	// Email to be sent for a summary report of scan completed
	private void sendReportAfterScanCompleteToUser(final String userId, final String scanId, final String scanName, final String state, List<Issue> issues, String testCaseName) {

		// parameters used to build an email template for summary report
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("scanId", scanId);
		model.put("scanName", scanName);
		model.put("issueDetails", issues);
		model.put("testCaseName", testCaseName);
		String cc = null;
		if (issues!=null && issues.size() > 0) cc = RootConfiguration.getProperties().getProperty(AppScanConstants.REPORT_EMAIL_CC_ADDRESS_IF_ISSUES);
		// Send email using Mail Utils
		MailUtils.sendEmail(userId
				+ RootConfiguration.getProperties().getProperty(AppScanConstants.EMAIL_DOMAIN),cc,
				AppScanConstants.APPSCAN_REPORT_SENDER,
				AppScanConstants.APPSCAN_REPORT_FOR_SCAN + " \"" + scanName + "\"", model,
				AppScanConstants.REPORT_MAIL_BODY_TEMPLATE);
	}


	@Scheduled(fixedRate = 360000)
	public void scheduleJob() {

		LOGGER.debug("Running ScanStatusNotifier");

		if (RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_RUN_CRON_JOBS).equalsIgnoreCase("true")) {
				String appScanAdminUserName =RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_ID_IDENTIFIER);
				String appScanAdminPassword =RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_PWD_IDENTIFIER);
				try{
					dastApiService.loginToDASTScanner(appScanAdminUserName,appScanAdminPassword);
				} catch (Exception exception){
					LOGGER.error("Failed in logging to AppScan as administrator");
					LOGGER.error(exception);
				}
				final List<Scan> listOfScansToBeTracked = getDao().getScansToBeTracked();

				if (listOfScansToBeTracked != null) LOGGER.debug("Running ScanStatusNotifier..listOfScansToBeTracked="+listOfScansToBeTracked.size());
				for (Scan eScan : listOfScansToBeTracked) {

					LOGGER.debug("Processing the scan with id="+eScan.getId() +", submitted by"+eScan.getUser().getUserId());
					LOGGER.debug("Processing the scan with id eScan.getReport()="+eScan.getReport() +", submitted by"+eScan.getUser().getUserId());

					if (eScan.getReport() != null) LOGGER.debug("Processing the scan with id eScan.getReport().getAseReportId()="+eScan.getReport().getAseReportId() +", submitted by"+eScan.getUser().getUserId());

					try{
						if (eScan.getScanId() == null || eScan.getScanId().isEmpty() || eScan.getReport()==null || eScan.getReport().getAseReportId()==null || "".equals(eScan.getReport().getAseReportId())) {
							try {
								LOGGER.debug("Resubmitting the scan with id="+eScan.getId());
								Recording recording = dao.getRecording(eScan.getRecordingId());
								if (recording !=null && recording.getHarFilename() !=null)
								dastApiService.setUpScanForUser(appScanAdminUserName, appScanAdminPassword,recording.getHarFilename().replace(AppScanConstants.HTD_FILE_EXTENSION,AppScanConstants.HAR_FILE_EXTENSION), null,true, eScan);
								dao.mergeScan(eScan);

							} catch(UnknownHostException uhe){
								LOGGER.error("Trying to submit the scan from ScanStatusNotifier...UnknownHostException...uhe.getMessage()"+uhe.getMessage());
								LOGGER.error(uhe);
								eScan.setScanState(AppScanConstants.APPSCAN_JOB_SCAN_STATE_SUSPENDED);
								eScan.setToBeTracked(false);
								eScan.setSuspendedReason(AppScanConstants.DAST_SCAN_SUSPENDED_REASON_EXTERNAL_URLS);
								getDao().saveScan(eScan);

							} catch(Exception e){
								LOGGER.error("Trying to submit the scan from ScanStatusNotifier."+e);
							}
							continue;
						}

						User user = dao.getUser(eScan.getUser().getUserId());
					
					String userId = eScan.getUser().getAppScanUserId();
					if (userId==null){
						userId = dastApiService.checkIfUserPresent(eScan.getUser().getUserId());
						LOGGER.debug("0.91..........userId from AppScan="+userId);
						if (userId==null){
							eScan.setScanState(AppScanConstants.APPSCAN_JOB_SCAN_STATE_SUSPENDED);
							eScan.setSuspendedReason(AppScanConstants.DAST_SCAN_SUSPENDED_REASON_NO_ASE_FOLDER);
							eScan.setToBeTracked(false);
							getDao().saveScan(eScan);
						} else {
							user.setAppScanUserId(userId);
							dao.saveGenericEntity(user);
						}
					}
					LOGGER.debug("1..........userId="+userId);
					

					final String scanState = dastApiService.checkForScanStarted(eScan.getScanId());
					LOGGER.debug("1.........scanState="+scanState+ " for scan with id ="+eScan.getScanId());

					if (scanState.equals(AppScanConstants.APPSCAN_JOB_SCAN_STATE_SUSPENDED)) {
						//sendEmailForScanStatusToUser(eScan.getUser().getUserId(),checkForScanId,eScan.getScanName(),AppScanConstants.APPSCAN_JOB_SCAN_STATE_SUSPENDED,userId);
						eScan.setScanState(scanState);
						eScan.setToBeTracked(false);
						getDao().mergeScan(eScan);
					} else if (scanState.equals(AppScanConstants.APPSCAN_JOB_SCAN_STATE_RUNNING)) {
						//sendEmailForScanStatusToUser(eScan.getUser().getUserId(),checkForScanId,eScan.getScanName(),AppScanConstants.APPSCAN_JOB_SCAN_STATE_RUNNING,userId);
						if (!eScan.getScanState().equals(AppScanConstants.APPSCAN_JOB_SCAN_STATE_RUNNING)){
							eScan.setScanState(scanState);
							getDao().mergeScan(eScan);
						}
					} else if (scanState.equals(AppScanConstants.APPSCAN_JOB_SCAN_STATE_READY)){
						LOGGER.debug("Updating the report as the scan jod is finished in the backend...scanId="+eScan.getScanId());
						final String latestLastRunForScan = dastApiService.getLatestRunForScan(eScan.getScanId());
						LOGGER.debug("Updating the report...latestLastRunForScan="+latestLastRunForScan);
						LOGGER.debug("Updating the report...eScan.getScanLastRun()="+eScan.getScanLastRun());

						if (AppScanUtils.isNotNull(latestLastRunForScan) && !latestLastRunForScan.equalsIgnoreCase(eScan.getScanLastRun())) {
							LOGGER.debug("Updating the report...eScan.getReport().getReportId()="+eScan.getReport().getAseReportId());

							final String latestLastRunForScanReport = dastApiService.getLatestRunForScanReport(eScan.getReport().getAseReportId());
							LOGGER.debug("Updating the report...latestLastRunForScanReport="+latestLastRunForScanReport);

							//&& !latestLastRunForScanReport.equalsIgnoreCase(lastRunForScanReport)
							if (AppScanUtils.isNotNull(latestLastRunForScanReport)) {

								String reportFromScanEngine = dastApiService.getReport(eScan.getReport().getAseReportId());
								LOGGER.debug("Updating the report...reportFromScanEngine="+reportFromScanEngine);
								LOGGER.debug("Updating the report...eScan.getReport().getId()="+eScan.getReport().getId());

								if (AppScanUtils.isNotNull(reportFromScanEngine)) {
									LOGGER.debug("Updating the report...");
									Report scanReport = eScan.getReport();
									LOGGER.debug("Updating the report...Before adding ASE...scanReport.getIssues().size()="+scanReport.getIssues().size() + " for eScan.getReport().getId()="+eScan.getReport().getId());
									List<Issue> issuesFromASE = dastApiService.getIssuesFromReport(eScan.getScanId(),eScan.getReport(),reportFromScanEngine);
									scanReport.getIssues().addAll(issuesFromASE);
									LOGGER.debug("Updating the report...After adding ASE...scanReport.getIssues().size()="+scanReport.getIssues().size() + " for eScan.getReport().getId()="+eScan.getReport().getId());
									LOGGER.debug("Updating the report...issuesFromASE.size()="+issuesFromASE.size() + " for eScan.getReport().getId()="+eScan.getReport().getId());
									LOGGER.debug("Updating the report...eScan.getReport().getIssues()="+eScan.getReport().getIssues());
									dao.saveGenericEntity(scanReport);

									List<Issue> issuesInScan = AppScanUtils.returnDASTProxyRelativeUrlIssueList(scanReport);
									if (!eScan.isSetUpViaBluefin() || user.getEnableEmailForAutomatedScan())
									eScan.getReport().setReportLastRun(latestLastRunForScanReport);
									eScan.setEmailSent(false);
									eScan.setScanLastRun(latestLastRunForScan);
									eScan.setScanState(scanState);
									eScan.setReport(scanReport);
									eScan.setToBeTracked(false);
									
									dao.saveGenericEntity(eScan.getReport());
									if (eScan.isNightlyScan()) eScan.setNightlyState(Scan.NIGHTLY_SCAN_STATE_COMPLETED);
									getDao().mergeScan(eScan);
									System.out.println("*************************************7");
									if (!eScan.isNightlyScan())
										sendReportAfterScanCompleteToUser(eScan.getUser().getUserId(),eScan.getScanId(),eScan.getScanName(),AppScanConstants.APPSCAN_JOB_SCAN_STATE_COMPLETED, filterIssueForEmail(eScan.getReport().getIssues()),eScan.getTestCaseName());

								} else {
									eScan.setScanState(AppScanConstants.APPSCAN_JOB_SCAN_STATE_SUSPENDED);
									eScan.setSuspendedReason(AppScanConstants.DAST_SCAN_SUSPENDED_REASON_BACKEND_ISSUE);
									eScan.setToBeTracked(false);
									getDao().saveScan(eScan);
								}
							}
						} else {
							eScan.setScanState(AppScanConstants.APPSCAN_JOB_SCAN_STATE_SUSPENDED);
							eScan.setSuspendedReason(AppScanConstants.DAST_SCAN_SUSPENDED_REASON_BACKEND_ISSUE);
							eScan.setToBeTracked(false);
							getDao().saveScan(eScan);
	
						}
					} 
					} catch (MalformedURLException exception) {
						System.out.println("*************************************8");
						LOGGER.error("A " + exception.getClass().getSimpleName()+ " has occured in the application in the scheduler. ",exception);
					} catch (DASTProxyException dastProxyException) {
						System.out.println("*************************************8.5");
						LOGGER.error("A " + dastProxyException.getClass().getSimpleName()+ " has occured in the application in the scheduler. ",dastProxyException);
						if (dastProxyException.getErrorCode().equals(AppScanConstants.DAST_SCAN_SUSPENDED_REASON_NO_ASE_FOLDER_CODE)){
							eScan.setScanState(AppScanConstants.APPSCAN_JOB_SCAN_STATE_SUSPENDED);
							eScan.setToBeTracked(false);
							eScan.setSuspendedReason(AppScanConstants.DAST_SCAN_SUSPENDED_REASON_NO_ASE_FOLDER);
							getDao().saveScan(eScan);
						}
						//AppScanUtils.sendErrorMail(exception);
					} catch (Exception exception) {
						System.out.println("*************************************9");
						LOGGER.error("A " + exception.getClass().getSimpleName()+ " has occured in the application in the scheduler. ",exception);
						//AppScanUtils.sendErrorMail(exception);
					}
				}

		} else {
			LOGGER.debug("Cron Jobs have been disabled. Exitting from ScanStatusNotifier");
		}
	}

	List<Issue> filterIssueForEmail(List<Issue> listOfIssues){
		List<Issue> filteredListOfIssuesForEmail = new ArrayList<Issue>();
		for (Issue issue: listOfIssues){
			if ((issue.getSeverity().equals("High") || issue.getSeverity().equals("Medium")) && !issue.isFp())
				filteredListOfIssuesForEmail.add(issue);
		}
		return filteredListOfIssuesForEmail;
	}
}
