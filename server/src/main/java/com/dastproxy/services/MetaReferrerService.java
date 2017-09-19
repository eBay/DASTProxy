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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dastproxy.common.constants.AppScanConstants;
import com.dastproxy.common.utils.MailUtils;
import com.dastproxy.configuration.RootConfiguration;
import com.dastproxy.dao.DastDAO;
import com.dastproxy.model.Scan;
import com.dastproxy.model.ScanVO;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.lightbody.bmp.core.har.copy.Har;
import net.lightbody.bmp.core.har.copy.HarEntry;

@Service
public class MetaReferrerService {

	private static final Logger LOGGER = LogManager.getLogger(MetaReferrerService.class.getName());

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

	// Email to be sent for a summary report of scan completed
	private void sendReportToAdmins(List<ScanVO> metaReferrerScans, List<ScanVO> noHTTPSscans, String to, String cc) {

		// parameters used to build an email template for summary report
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("metaReferrerScans", metaReferrerScans);
		model.put("noHTTPSscans", noHTTPSscans);

		// Send email using Mail Utils
		MailUtils.sendEmail(to, cc,AppScanConstants.APPSCAN_REPORT_SENDER,"DAST scans with Meta Referrer and HTTP (no SSL) URLs",model,AppScanConstants.REPORT_MAIL_META_REFERRER_BODY_TEMPLATE);
	}

	String baseURL = RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_OPERATING_ENVIRONMENT_BASE_URL_IDENTIFIER);

	@Scheduled(cron="0 1 1 * * ?")
	//@Scheduled(fixedRate = 3600000)
	public void scheduleJob() {

		LOGGER.debug("Running MetaReferrerService");
		System.out.println("Running MetaReferrerService...1");
		String baseURL = RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_OPERATING_ENVIRONMENT_BASE_URL_IDENTIFIER);
		if (RootConfiguration.getProperties().getProperty(AppScanConstants.META_REFERRER_CHECK_ON).equalsIgnoreCase("true")){
			try {
				System.out.println("Running MetaReferrerService...2");
				final List<Scan> scans = getDao().getAllYesterdaysScans();
				ObjectMapper jacksonObjectMapper = new ObjectMapper();
				if (scans != null) LOGGER.debug("Running MetaReferrerService..scans="+scans.size());
				List<ScanVO> scansWithMetaReferrer = new ArrayList<ScanVO>();
				List<ScanVO> scansWithHTTPurls = new ArrayList<ScanVO>();
				ScanVO scanVO = null;
				for (Scan scan : scans) {
					try{
						String fileName = dao.getRecording(scan.getRecordingId()).getHarFilename();
						
						LOGGER.debug("Running MetaReferrerService..fileName="+fileName);
						String responseHTMLContent = null;
						Har har = jacksonObjectMapper.readValue(new File(fileName), Har.class);
						boolean hasMetaFound = false;
						String projectUrl = null;
						List<String> noHTTPSurls = new ArrayList<String>();
						
						List<HarEntry> harEntries = har.getLog().getEntries();
						if (harEntries != null && harEntries.size() > 0){
							projectUrl = har.getLog().getEntries().get(0).getRequest().getUrl();
							for (HarEntry harEntry : harEntries){
								String url = harEntry.getRequest().getUrl();
								if (url!=null && url.indexOf(".firefox.com/")!=-1) continue;
								if (url!=null && url.toLowerCase().startsWith("http://")) noHTTPSurls.add(url);
								if (harEntry.getResponse()==null || harEntry.getResponse().getContent() ==null || harEntry.getResponse().getContent().getText()==null) continue;
								responseHTMLContent = harEntry.getResponse().getContent().getText().toLowerCase();
								if (responseHTMLContent != null && responseHTMLContent.contains("<meta name=\"referrer\"") || responseHTMLContent.contains("<meta name='referrer'")) hasMetaFound = true;
							}
						}
						System.out.println("Running MetaReferrerService...2");
						if (hasMetaFound) {
							scanVO = new ScanVO();
							System.out.println("--------------------fileName="+fileName);
							System.out.println("--------------------fileName...last index="+fileName.lastIndexOf("\\"));
							if (fileName !=null && fileName.lastIndexOf("\\") !=-1){
								fileName = fileName.substring(fileName.lastIndexOf("\\"));
							}
							scanVO.setHarUrl(baseURL+"rest/v1/har/"+scan.getUser().getUserId()+"/"+fileName);
							Integer scanBatchId= dao.getScanBatchIdOfScan(scan.getId());
							scanVO.setScanBatchLink(baseURL + "scan_batch_report?scanBatchId=" + scanBatchId);
							scanVO.setScanId(scan.getId());
							scanVO.setScanName(scan.getScanName());
							if (projectUrl!=null) scanVO.setProjectUrl(projectUrl);
							
							scansWithMetaReferrer.add(scanVO);
						}
						System.out.println("Running MetaReferrerService...3...noHTTPSurls.size()="+noHTTPSurls.size());
						if (noHTTPSurls.size() > 0) {
							System.out.println("Running MetaReferrerService...4");
							scanVO = new ScanVO();
							System.out.println("--------------------fileName="+fileName);
							System.out.println("--------------------fileName...last index="+fileName.lastIndexOf("\\"));
							System.out.println("Running MetaReferrerService...5");
							if (fileName !=null && fileName.lastIndexOf("\\") !=-1){
								fileName = fileName.substring(fileName.lastIndexOf("\\"));
							}
							scanVO.setHarUrl(baseURL+"rest/v1/har/"+scan.getUser().getUserId()+"/"+fileName);
							Integer scanBatchId= dao.getScanBatchIdOfScan(scan.getId());
							System.out.println("Running MetaReferrerService...6");
							scanVO.setScanBatchLink(baseURL + "scan_batch_report?scanBatchId=" + scanBatchId);
							scanVO.setScanId(scan.getId());
							scanVO.setScanName(scan.getScanName());
							scanVO.setHttpUrls(noHTTPSurls);
							
							System.out.println("--------------------scanVO.setHarUrl="+scanVO.getHarUrl());
							System.out.println("--------------------scanVO.getScanName="+scanVO.getScanName());
							System.out.println("--------------------scanVO.getScanBatchLink="+scanVO.getScanBatchLink());
							System.out.println("--------------------scanVO.getScanId="+scanVO.getScanId());
							
							scansWithHTTPurls.add(scanVO);
						}
						
					} catch (Exception exception) {
						System.out.println("*************************************9");
						LOGGER.error("A " + exception.getClass().getSimpleName()+ " has occured in the application in the scheduler. ",exception);
						exception.printStackTrace();
						//AppScanUtils.sendErrorMail(exception);
					}
				}
				String emailto = RootConfiguration.getProperties().getProperty(AppScanConstants.META_REFERRER_CHECK_EMAIL);
				String emailCC = RootConfiguration.getProperties().getProperty(AppScanConstants.META_REFERRER_CHECK_EMAIL_CC);
				
				if (scansWithMetaReferrer.size() > 0 || scansWithHTTPurls.size() >0) sendReportToAdmins(scansWithMetaReferrer, scansWithHTTPurls, emailto, emailCC);

			}  catch (RuntimeException exception) {
				exception.printStackTrace();
				System.out.println("*************************************11");
				LOGGER.error("A " + exception.getClass().getSimpleName()+ " has occured in the application in the scheduler. ",exception);
				//AppScanUtils.sendErrorMail(exception);
			}
		}

	}


}

