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
import java.util.stream.Collectors;

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
public class NightlyScanPostProcessorService {

	private static final Logger LOGGER = LogManager.getLogger(NightlyScanPostProcessorService.class.getName());
	public static String pathPrefix = RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_OPERATING_ENVIRONMENT_BASE_URL_IDENTIFIER);
	static FpReason fpReasonDuplicate = null;
	

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

	@Scheduled(fixedRate = 9000000)
	//@Scheduled(cron = "0 0 7 * * ?")
	public void scheduleJob() {

		LOGGER.debug("Running NightlyScanPostProcessorService---------------------------------------");

		if (RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_RUN_CRON_JOBS).equalsIgnoreCase("true")) {

				List<ScanBatch> nightlyBatches = getDao().getNightlyCompletedScanBatches();
				
				for (ScanBatch batch : nightlyBatches){
					processScanBatch(batch);
				}
				
				
		} else {
			LOGGER.debug("Cron Jobs have been disabled. Exitting from ScanStatusNotifier");
		}
	}
	
	private void processScanBatch(ScanBatch batch){
		try {
			System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------1...batch.getId()="+batch.getId());
			List<Scan> scans = batch.getScans();
			int processedCount = 0;
			Map<String,List<Issue>> emailDataMap = new HashMap<String,List<Issue>>();
			System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------1...scans.size()="+scans.size());
			for (Scan scan : scans){
				if (scan.getNightlyState() == Scan.NIGHTLY_SCAN_STATE_COMPLETED){
					processedCount++;
					List<Issue> currentIssues = scan.getReport().getIssues().stream().filter(issue -> (issue.getSeverity().equals("High") || issue.getSeverity().equals("Medium")) && !issue.isFp()).collect(Collectors.toList());
					
					//1. Find the previous scan from the same recording batch
					Scan previousScan = dao.getRecentNightlyScanByRecordingId(scan.getRecordingId(), scan.getId());
					System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------3...scanId="+scan.getId()+" ,previousScan.getId()="+previousScan.getId());
	
					//2. Compare the issues and mark the issues duplicates - Technically marking as FP with DUPLICATE type
					System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------4...scan.getReport().getIssues().size()()="+scan.getReport().getIssues().size());
					System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------4...previousScan.getReport().getIssues().size()()="+previousScan.getReport().getIssues().size());
					
					List<Issue> nonDuplicateIssues = new ArrayList<Issue>();
					if (previousScan==null){
						nonDuplicateIssues=currentIssues;
					} else {
					
						List<Issue> previousIssues = previousScan.getReport().getIssues().stream().filter(issue -> (issue.getSeverity().equals("High") || issue.getSeverity().equals("Medium")) && !issue.isFp()).collect(Collectors.toList());
						System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------4...currentIssues.size()="+currentIssues.size());
						System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------4...previousIssues.size()="+previousIssues.size());
						
						
						for (Issue currentIssue: currentIssues){
							System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------5..."+currentIssue.getId());
							for (Issue previousIssue: previousIssues){
								if (currentIssue.getSeverity().equals(previousIssue.getSeverity())
										&& currentIssue.getIssueType().equals(previousIssue.getIssueType())
										&& currentIssue.getTestUrl().equals(previousIssue.getTestUrl())
										&& currentIssue.getScanEngine().equals(previousIssue.getScanEngine())){
									currentIssue.setFp(true);
									currentIssue.setFpComments("Duplicate of issue with id="+previousIssue.getId());
									currentIssue.setFpMarkedBy("SYSTEM");
									currentIssue.setFpMarkedDate(new Date());
									if (fpReasonDuplicate==null) getFpReasonDuplicate();
									currentIssue.setFpReasonId(fpReasonDuplicate.getId());
									dao.saveIssue(currentIssue);
									
								} else {
									System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------5.1..."+currentIssue.getId());
									nonDuplicateIssues.add(currentIssue);
								}
							}
						}
					}
					System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------6");
					if (nonDuplicateIssues.size() > 0){
						for (Issue issue : nonDuplicateIssues) {
							System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------6.1 issue.getId()="+issue.getId());
							String dastProxyRelativeBugUIUrl = pathPrefix + "issueNew?report="+scan.getReport().getId()+"&issue=" + issue.getId();
							issue.setDastProxyBugUIIssueUrl(dastProxyRelativeBugUIUrl);
							System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------6.1 issue.getDastProxyBugUIIssueUrl()="+issue.getDastProxyBugUIIssueUrl());
						}						
						emailDataMap.put(scan.getScanName(), nonDuplicateIssues);
					}
					//3. Mark the scan as Post processing done.
					scan.setNightlyState(Scan.NIGHTLY_SCAN_STATE_POST_PROCESSING_DONE);
					dao.saveScan(scan);
					System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------7");
				} else if (scan.getNightlyState()==Scan.NIGHTLY_SCAN_STATE_POST_PROCESSING_DONE){
					System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------8");
					processedCount++;
					List<Issue> nonDuplicateIssues = scan.getReport().getIssues().stream().filter(issue -> (issue.getSeverity().equals("High") || issue.getSeverity().equals("Medium") && !issue.isFp())).collect(Collectors.toList());
					if (nonDuplicateIssues.size() > 0)emailDataMap.put(scan.getScanName(), nonDuplicateIssues);
				} else if (scan.getNightlyState()==Scan.NIGHTLY_SCAN_STATE_CREATED && scan.getScanState().equals("Suspended")){
					processedCount++;
				}
				
			}
			
			System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------9..processedCount="+processedCount);
			System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------9..scans.size()="+scans.size());
			System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------9..emailDataMap.keySet().size()="+emailDataMap.keySet().size());
			
			if (processedCount == scans.size()){
				System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------9");
				//1. send email.
				if (emailDataMap.keySet().size()>0) sendEmailReport(batch.getOwner(), emailDataMap, batch.getTestsuiteName());
				System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------10");
				//2. Mark the batch post processing done.
				batch.setNightlyBatchState(ScanBatch.COMPLETED);
				dao.saveScanBatch(batch);
				System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------11");
			}
			System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------12");
		} catch (Exception exception){
			System.out.println(exception);
			System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------13");
		}
	}
	
	public void getFpReasonDuplicate(){
		List<FpReason> fpReasons = dao.getFpReasonWithPattern();
		for (FpReason reason : fpReasons){
			if (reason.getAbbr().equals("DUPLICATE-NIGHTLY")){
				fpReasonDuplicate = reason;
			}
		}
	}
	//sendReportAfterScanCompleteToUser(eScan.getUser().getUserId(),eScan.getScanId(),eScan.getScanName(),AppScanConstants.APPSCAN_JOB_SCAN_STATE_COMPLETED,userId, filterIssueForEmail(eScan.getReport().getIssues()),eScan.getTestCaseName());
	private void sendEmailReport(final String userId,final Map<String,List<Issue>> scanIssuesMap, final String scanBatchName) {

		// parameters used to build an email template for summary report
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("scanIssuesMap", scanIssuesMap);
		model.put("scanBatchName", scanBatchName);
		String ccAddress = AppScanConstants.REPORT_EMAIL_CC_ADDRESS_IF_ISSUES;
		String toAddress = userId + RootConfiguration.getProperties().getProperty(AppScanConstants.EMAIL_DOMAIN);
		System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------10.1...ccAddress="+ccAddress);
		System.out.println("Running NightlyScanPostProcessorService...processScanBatch---------------------------------------10.1...toAddress="+toAddress);
		
		// Send email using Mail Utils
		MailUtils.sendEmail(toAddress,ccAddress, AppScanConstants.APPSCAN_REPORT_SENDER, AppScanConstants.APPSCAN_REPORT_FOR_SCAN_BATCH + " \"" + scanBatchName + "\"", model,AppScanConstants.REPORT_SCAN_BATCH_MAIL_BODY_TEMPLATE);
	}	
}
