package com.dastproxy.services;

import java.io.File;
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
import com.dastproxy.common.utils.AppScanUtils;
import com.dastproxy.common.utils.MailUtils;
import com.dastproxy.configuration.RootConfiguration;
import com.dastproxy.dao.DastDAO;
import com.dastproxy.model.DASTProxyException;
import com.dastproxy.model.ProxyEntity;
import com.dastproxy.model.Scan;

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

	@Scheduled(cron = "0 0/2 * * * ?")
	public void processSubmittedRecordings() {

		LOGGER.debug("Running processSubmittedRecordings");

		if (RootConfiguration.getProperties()
				.getProperty(AppScanConstants.PROPERTIES_RUN_CRON_JOBS)
				.equalsIgnoreCase("true")) {

			List<ProxyEntity> proxyEntitiesToBeProcessed = null;

			try {

				proxyEntitiesToBeProcessed = dao.getEntities();

				if (LOGGER.isDebugEnabled()
						&& !AppScanUtils.isNotNull(proxyEntitiesToBeProcessed)) {
					LOGGER.debug("ProxyEntitiesToBeProcessed from the database has returned no values");
				}

				for (final ProxyEntity proxyEntity : proxyEntitiesToBeProcessed) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Inside the for loop for processing proxyEntity. Currently processing: "
								+ proxyEntity.toString());
					}
					try {
						if (!AppScanUtils.isNotNull(proxyEntity
								.getErrorMessage())) {
							final String nameOfScan = proxyEntity
									.getScanConfiguration().getNameOfScan();
							final boolean startScanAutomatically = proxyEntity
									.getScanConfiguration().isStartScan();

							final String filePath = new StringBuilder(
									AppScanConstants.USER_HTD_FILES_LOCATION)
									.append(File.separator)
									.append(proxyEntity.getUser().getUserId())
									.append(File.separator)
									.append(proxyEntity.getProxy()
											.getHtdFileName()).toString();

							final Scan successfullySetUpScan = dastApiService
									.setUpScanForUser(proxyEntity.getUser()
											.getUserId(), "", filePath,
											nameOfScan, startScanAutomatically);

							if (AppScanUtils.isNotNull(successfullySetUpScan)) {
								dao.removeEntity(proxyEntity);

								successfullySetUpScan.setSetUpViaBluefin(true);
								dao.saveScan(successfullySetUpScan);
								Map<String, Object> model = new HashMap<String, Object>();
								model.put("scanName",
										successfullySetUpScan.getScanName());
								model.put("testCaseName",
										successfullySetUpScan.getTestCaseName());
								model.put(
										"contactUsSupportDl",
										AppScanConstants.APPSCAN_CONTACT_US_SUPPORT_DL);
								MailUtils
										.sendEmail(
												successfullySetUpScan.getUser()
														.getUserId()
														+ AppScanConstants.APPSCAN_JOB_SCAN_STATUS_RECEIVER,
												AppScanConstants.APPSCAN_REPORT_SENDER,
												successfullySetUpScan
														.getScanName()
														+ " is Successfully set up",
												model,
												AppScanConstants.SCAN_SETUP_MAIL_BODY_TEMPLATE);
							}
						} else {

							// There has been some error while trying to submit
							// the
							// scan at an earlier date.
							// The system will not try to submit it again.
							LOGGER.debug(
									"The submitted scan has gone through a \'scan\' set up process. The error message recorded against this submission is: {}",
									proxyEntity.getErrorMessage());

						}
					} catch (Exception exception) {

						LOGGER.error("Error in processSubmittedRecording while trying to set up a scan for a particular Bluefin/Breeze/Selenium scan submission. The error is : "
								+ exception);

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
							proxyEntity
									.setErrorMessage(((DASTProxyException) exception)
											.getErrorMessage());

							if (AppScanConstants.EXCEPTION_CODE_ONLY_EXTERNAL_URLS_FOR_SCAN
									.equalsIgnoreCase(((DASTProxyException) exception)
											.getErrorCode())) {

								// parameters used to build an email template
								// for scan status
								Map<String, Object> emailModel = new HashMap<String, Object>();
								emailModel.put("testCaseName",
										proxyEntity.getTestCaseName());
								emailModel
										.put("reasonForRejection",
												AppScanConstants.ERROR_MESSAGE_TO_USER_ONLY_EXTERNAL_URLS_NOT_ACCEPTED);
								emailModel
										.put("contactUsSupportDl",
												AppScanConstants.APPSCAN_CONTACT_US_SUPPORT_DL);

								MailUtils
										.sendEmail(
												proxyEntity.getUser()
														.getUserId()
														+ AppScanConstants.APPSCAN_JOB_SCAN_STATUS_RECEIVER,
												AppScanConstants.APPSCAN_REPORT_SENDER,
												"Scan has been rejected",
												emailModel, "scanRejected.vm");

								dao.removeEntity(proxyEntity);
								continue;
							}

						}
						
						if(AppScanUtils.isNotNull(exception) && AppScanUtils.isNotNull(exception.getCause())){
							proxyEntity.setErrorMessage(exception.getCause()
									.toString());
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
