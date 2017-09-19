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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.NodeList;

import com.dastproxy.common.constants.AppScanConstants;
import com.dastproxy.common.utils.AppScanUtils;
import com.dastproxy.common.utils.MailUtils;
import com.dastproxy.configuration.RootConfiguration;
import com.dastproxy.dao.DastDAO;
import com.dastproxy.model.DASTProxyException;
import com.dastproxy.model.FpReason;
import com.dastproxy.model.Issue;
import com.dastproxy.model.Recording;
import com.dastproxy.model.RecordingBatch;
import com.dastproxy.model.Report;
import com.dastproxy.model.Scan;
import com.dastproxy.model.ScanBatch;
import com.dastproxy.model.User;
import com.dastproxy.services.impl.ZapService;

@Service
public class NightlyScanSubmitterService {

	private static final Logger LOGGER = LogManager.getLogger(NightlyScanSubmitterService.class.getName());
	private static String appScanAdminUserName =RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_ID_IDENTIFIER);
	private static String appScanAdminPassword =RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_PWD_IDENTIFIER);


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


	//@Scheduled(fixedRate = 900000)
	@Scheduled(cron = "0 0 1 * * ?")
	public void scheduleJob() {

		LOGGER.debug("Running NightlyScanSubmitterService---------------------------------------");

		if (RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_RUN_CRON_JOBS).equalsIgnoreCase("true")) {
				String appScanAdminUserName =RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_ID_IDENTIFIER);
				String appScanAdminPassword =RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_PWD_IDENTIFIER);
				try{
					LOGGER.debug("Running NightlyScanSubmitterService---------------------------------------1");
					dastApiService.loginToDASTScanner(appScanAdminUserName,appScanAdminPassword);
					LOGGER.debug("Running NightlyScanSubmitterService---------------------------------------2");
				} catch (Exception exception){
					LOGGER.debug("Running NightlyScanSubmitterService---------------------------------------3");
					LOGGER.error("Failed in logging to AppScan as administrator");
					LOGGER.error(exception);
				}
				List<RecordingBatch> nightlyBatches = getDao().getNightlyBatches();
				
				for (RecordingBatch batch : nightlyBatches){
					scanBatch(batch);
				}
				
				
		} else {
			LOGGER.debug("Cron Jobs have been disabled. Exitting from ScanStatusNotifier");
		}
	}
	
	//TODO : refactor the code for re-use 
	public boolean scanBatch(RecordingBatch recBatch) {
		LOGGER.debug("Running NightlyScanSubmitterService---------------------------------------4");
		boolean retValue = true;
		ZapService zapService = new ZapService();
		ScanBatch scanBatch = null;
		List<Recording> recordings = null;
		LOGGER.debug("Running NightlyScanSubmitterService---------------------------------------5");
		try {
			recordings = dao.getRecordingsByBatchId(recBatch.getId());
			if (recordings != null){
				if (recordings.size() > 0){
					LOGGER.debug("Running NightlyScanSubmitterService---------------------------------------6");
					scanBatch = new ScanBatch();
					scanBatch.setTestsuiteName(recBatch.getTestsuiteName());
					scanBatch.setOwner(recBatch.getOwner());
					LOGGER.debug("Running NightlyScanSubmitterService---------------------------------------6.1");
					scanBatch.setRecordingBatchId(recBatch.getId());
					scanBatch.setSubsetOfBatch(false);
					scanBatch.setDateCreated(new Date());
					scanBatch.setNightlyBatch(true);
					LOGGER.debug("Running NightlyScanSubmitterService---------------------------------------6.2");
					dao.saveScanBatch(scanBatch);
				}
				for (Recording recording: recordings){
					LOGGER.debug("Running NightlyScanSubmitterService---------------------------------------6.3");
					Scan scan = createScan(recording.getTestcaseName(), recording.getTestsuiteName(), recording.getOwner());
					scan.setNightlyScan(true);
					LOGGER.debug("Running NightlyScanSubmitterService---------------------------------------6.4");
					scan.setRecordingId(recording.getId());
					scan.setSetUpViaBluefin(false);
					scan.setBatch(scanBatch);
					dao.saveScan(scan);

					try{
						LOGGER.debug("Running NightlyScanSubmitterService---------------------------------------7");
						LOGGER.debug("Running NightlyScanSubmitterService---------------------------------------7..appScanAdminUserName="+appScanAdminUserName);
						dastApiService.setUpScanForUser(appScanAdminUserName, appScanAdminPassword, recording.getHarFilename(), recording.getTestcaseName(), true, scan);
					} catch(Exception exception){
						LOGGER.debug("Running NightlyScanSubmitterService---------------------------------------8");
						LOGGER.error("There is an issue in submitting the scan to the server. The operation is saved and will be tried again."+ exception);
					}
					LOGGER.debug("Running NightlyScanSubmitterService---------------------------------------9");
					System.out.println("-------------------------------------------------Inside NightlyScanSubmitterService...9");
					zapService.scanWithZap(recording.getHarFilename(), scan.getReport());
					System.out.println("-------------------------------------------------Inside NightlyScanSubmitterService...10");
					dao.saveGenericEntity(recording);
					System.out.println("-------------------------------------------------Inside NightlyScanSubmitterService...10.1");
					dao.saveScan(scan);
					System.out.println("-------------------------------------------------Inside NightlyScanSubmitterService...11");

				}
			}

		} catch (Exception exception) {
			retValue = false;
			LOGGER.debug("Running NightlyScanSubmitterService---------------------------------------10");
			LOGGER.error("There has been an error while trying to set the scan up (using the recording which was done earlier). The details of the error is: "+ exception);
		}


		return retValue;
	}
	private Scan createScan(String testCaseName, String testSuiteName, String userName){
		Scan scan = new Scan();
		try {

			scan.setReport(new Report());
			scan.setFirstSetUp(new Date());
			scan.setToBeTracked(true);
			scan.setTestCaseName(testCaseName);
			scan.setTestSuiteName(testSuiteName);
			scan.setScanState(AppScanConstants.APPSCAN_JOB_READY_FOR_SCAN);
			scan.getReport().setAseReportId("");

			User user = new User();
			user.setUserId(userName);
			scan.setUser(user);


			if (testCaseName != null && !"none".equals(testCaseName)){
				if (testCaseName.length() > 45) testCaseName = testCaseName.substring(0, 44);
				scan.setTestCaseName(testCaseName);
			} else {
				scan.setTestCaseName(AppScanUtils.getLoggedInUser().getUserId() + AppScanUtils.returnDateInPredefinedFormat());
			}
		} catch(Exception e){
			e.printStackTrace();
		}

		return scan;
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
