/**
 * This is the class which would hold all the functions for rest based services 
 * that our API would provide.
 * 
 * @author Kiran Shirali (kshirali@ebay .com)
 */
package com.dastproxy.controller.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.xml.sax.SAXException;

import com.dastproxy.common.constants.AppScanConstants;
import com.dastproxy.common.utils.AppScanUtils;
import com.dastproxy.common.utils.DastUtils;
import com.dastproxy.common.utils.MailUtils;
import com.dastproxy.configuration.RootConfiguration;
import com.dastproxy.dao.DastDAO;
import com.dastproxy.model.ContactUsIssue;
import com.dastproxy.model.DASTProxyException;
import com.dastproxy.model.FpReason;
import com.dastproxy.model.Issue;
import com.dastproxy.model.IssueVO;
import com.dastproxy.model.Proxy;
import com.dastproxy.model.ProxyEntity;
import com.dastproxy.model.Recording;
import com.dastproxy.model.RecordingBatch;
import com.dastproxy.model.Report;
import com.dastproxy.model.Scan;
import com.dastproxy.model.ScanBatch;
import com.dastproxy.model.ScanConfiguration;
import com.dastproxy.model.User;
import com.dastproxy.model.jira.JiraIssueResponse;
import com.dastproxy.services.BrowserMobServiceBean;
import com.dastproxy.services.DASTApiService;
import com.dastproxy.services.JiraPublisherService;
import com.dastproxy.services.impl.ZapService;
import com.dastproxy.value.FpInput;

import net.lightbody.bmp.proxy.ProxyServer;

@RestController
public class DASTProxyHomeRestController {

	// Logger for this class.
	private static final Logger LOGGER = LogManager.getLogger(DASTProxyHomeRestController.class.getName());

	@Autowired
	private View view;

	@Autowired
	private BrowserMobServiceBean browserMobServiceBean;

	@Autowired
	private DASTApiService dastApiService;

	@Inject
	@Qualifier("dastDAOImpl")
	private DastDAO dao;


	@Autowired
	private ZapService zapService;

	public ZapService getZapService() {
		return zapService;
	}

	public void setZapService(ZapService zapService) {
		this.zapService = zapService;
	}


	@Autowired
	private JiraPublisherService jiraPublisherService;

	private Map<String, ProxyEntity> openProxyServers;

	DastUtils dastUtils = new DastUtils();

	/**
	 * Initializing openProxyServers list in the constructor. This list will
	 * hold information about all the open servers that are currently being
	 * used.
	 */
	public DASTProxyHomeRestController() {
		super();

		if (openProxyServers == null) {
			openProxyServers = new HashMap<String, ProxyEntity>();
		}
	}

	// This function is my test function to try to build new things.
	// It should be ALWAYS commented before released to Prod
	@RequestMapping(value = { "/rest/v1/jira/project/{jiraProjectId}/reportnew/{reportId}/issuenew/{issueId}" }, method = RequestMethod.GET)
	public ModelAndView publishToJIRA(@PathVariable final String jiraProjectId,
			@PathVariable final String reportId,
			@PathVariable final String issueId, HttpServletResponse response)
			throws Exception {
		System.out.println("----------------------Inside publishToJIRA...issueId="+issueId);
		final Issue issueToBePushedToJira = dao.getIssueById(new Long(issueId));
		LOGGER.debug("-----------------------------issueToBePushedToJira"+issueToBePushedToJira.getId());


		// If the issue is valid then push to JIRA
		if (AppScanUtils.isNotNull(issueToBePushedToJira)) {

			final JiraIssueResponse jiraIssueResponse = jiraPublisherService
					.publishToJIRAProject(
							jiraProjectId,
							issueToBePushedToJira,
							RootConfiguration
									.getProperties()
									.getProperty(
											AppScanConstants.PROPERTIES_JIRA_SERVICE_ACCOUNT_USERNAME),
							RootConfiguration
									.getProperties()
									.getProperty(
											AppScanConstants.PROPERTIES_JIRA_SERVICE_ACCOUNT_PASSWORD));

			// Since jira issue has been successfully raised, it is time to send
			// a mail to the user.
			Map<String, Object> jiraIssueRaisedMailModal = new HashMap<String, Object>();
			jiraIssueRaisedMailModal.put("jiraKey", jiraIssueResponse.getKey());
			jiraIssueRaisedMailModal.put(
					"jiraUrl",
					RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_JIRA_BASE_URL)
							+ "/browse/" + jiraIssueResponse.getKey());
			jiraIssueRaisedMailModal.put("contactUsSupportDl",AppScanConstants.APPSCAN_CONTACT_US_SUPPORT_DL);

			MailUtils.sendEmail(AppScanUtils.getLoggedInUser().getUserId()
					+ RootConfiguration.getProperties().getProperty(AppScanConstants.EMAIL_DOMAIN), null,
					AppScanConstants.APPSCAN_REPORT_SENDER,
					AppScanConstants.JIRA_ISSUE_RAISED_SUBJECT,
					jiraIssueRaisedMailModal, AppScanConstants.JIRA_ISSUE_RAISED_TEMPLATE);

			issueToBePushedToJira.setJira(jiraIssueResponse);
			dao.saveIssue(issueToBePushedToJira);

			return new ModelAndView(view,
					AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER,
					jiraIssueResponse);
		} else {
			return new ModelAndView(view,
					AppScanConstants.JSON_RESPONSE_ERROR_IDENTIFIER,
					"Issue is invalid");
		}

	}

	// This function is my test function to try to build new things.
	// It should be ALWAYS commented before released to Prod
	@RequestMapping(value = { "/rest/v1/jira/project/{jiraProjectId}/report/{reportId}/issue/{issueId}" }, method = RequestMethod.GET)
	public ModelAndView returnCookie(@PathVariable final String jiraProjectId,
			@PathVariable final String reportId,
			@PathVariable final String issueId, HttpServletResponse response)
			throws Exception {

		final Issue issueToBePushedToJira = dao.getIssueByNativeId(new Long(issueId));

		// If the issue is valid then push to JIRA
		if (AppScanUtils.isNotNull(issueToBePushedToJira)) {

			final JiraIssueResponse jiraIssueResponse = jiraPublisherService
					.publishToJIRAProject(
							jiraProjectId,
							issueToBePushedToJira,
							RootConfiguration
									.getProperties()
									.getProperty(
											AppScanConstants.PROPERTIES_JIRA_SERVICE_ACCOUNT_USERNAME),
							RootConfiguration
									.getProperties()
									.getProperty(
											AppScanConstants.PROPERTIES_JIRA_SERVICE_ACCOUNT_PASSWORD));

			// Since jira issue has been successfully raised, it is time to send
			// a mail to the user.
			Map<String, Object> jiraIssueRaisedMailModal = new HashMap<String, Object>();
			jiraIssueRaisedMailModal.put("jiraKey", jiraIssueResponse.getKey());
			jiraIssueRaisedMailModal.put(
					"jiraUrl",
					RootConfiguration.getProperties().getProperty(
							AppScanConstants.PROPERTIES_JIRA_BASE_URL)
							+ "/browse/" + jiraIssueResponse.getKey());
			jiraIssueRaisedMailModal.put("contactUsSupportDl",
					AppScanConstants.APPSCAN_CONTACT_US_SUPPORT_DL);

			MailUtils.sendEmail(AppScanUtils.getLoggedInUser().getUserId()
					+ RootConfiguration.getProperties().getProperty(AppScanConstants.EMAIL_DOMAIN), null,
					AppScanConstants.APPSCAN_REPORT_SENDER,
					AppScanConstants.JIRA_ISSUE_RAISED_SUBJECT,
					jiraIssueRaisedMailModal, AppScanConstants.JIRA_ISSUE_RAISED_TEMPLATE);

			issueToBePushedToJira.setJira(jiraIssueResponse);
			dao.saveIssue(issueToBePushedToJira);

			return new ModelAndView(view,
					AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER,
					jiraIssueResponse);
		} else {
			return new ModelAndView(view,
					AppScanConstants.JSON_RESPONSE_ERROR_IDENTIFIER,
					"Issue is invalid");
		}

	}

	/**
	 * This API end point returns the details of a proxy for a request. Now
	 * there are two conditions here:
	 * 
	 * 1. Request comes in from the front end of this application
	 * 
	 * The application has been designed such that a user has to log into the
	 * application (Phase 2). Since there would be a unique user, the proxy can
	 * be mapped to him. So in a 'Proxy Entity' Object, the defining mapping
	 * would be the user object.
	 * 
	 * 2. Request comes in from a Bluefin/Breeze/Selenium test case
	 * 
	 * When there is finally integration support from Bluefin/Breeze/Selenium,
	 * request will come in from test cases themselves. In this case, we require
	 * that Bluefin/Breeze/Selenium sends in an empty 'Proxy Entity' Object with
	 * only the 'Proxy Identifier' value set. In this case, a new 'Proxy Server'
	 * will be created and sent back. So the unique identifier for individual
	 * proxy servers would be 'proxyAddress:proxyPort:proxyIdentifier' in that
	 * format. So unless Bluefin/Breeze/Selenium sends back these three values,
	 * there would be no way to track an open proxy.
	 * 
	 * TODO Have to eliminate open and abandoned proxies
	 * 
	 * @param proxyEntity
	 * @return
	 * @throws DASTProxyException
	 */

	@RequestMapping(value = { "/rest/v1/proxyui" }, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ModelAndView returnProxyDetails(@RequestBody ProxyEntity proxyEntity)
			throws DASTProxyException {

		if (proxyEntity != null) {

			// Check if the user is non existent. If there is a user, then the
			// request has come from the
			// front end of the application.
			if (proxyEntity.getUser() != null
					&& proxyEntity.getUser().getUserId() != null) {

				// Instead of values given via the front end, check if a user is
				// logged in. Consider this as a
				// security check.
				if (AppScanUtils.getLoggedInUser() != null && AppScanUtils.getLoggedInUser().getUserId() != null) {

					// So we have a logged in user. Check if the user already
					// has a ProxyServer associated with him/her.
					if (openProxyServers.containsKey(AppScanUtils
							.getLoggedInUser().getUserId())) {
						// It seems that the user has a ProxyServer. Just return
						// the same details. NO USER can have more than one
						// proxy at a time.
						proxyEntity = openProxyServers.get(AppScanUtils
								.getLoggedInUser().getUserId());
						proxyEntity.getProxy().setNewlyCreated(false);
					}
					// The poor user doesn't have a proxy. Let us create one,
					// store the details and send it to him.
					else {
						// Invoke the Browser Mob Service bean and set up a
						// proxy. Get the proxy running too.
						final ProxyServer newProxyServerForUser = getBrowserMobServiceBean()
								.setUpProxyAndStartRecordForUser(
										AppScanUtils.getLoggedInUser()
												.getUserId());
						// Check if there is a valid proxy server returned by
						// the Service Bean. (In my experience Java Beans
						// aren't your best friend. They always fail in
						// production and put you into trouble. So always double
						// check)
						if (AppScanUtils.isNotNull(newProxyServerForUser)) {
							// Extract the details and set them into a 'Proxy
							// Entity'
							// This is what will be returned to the user. Also
							// store the 'Proxy Server' so that we can
							// track it later.
							proxyEntity.setUser(AppScanUtils.getLoggedInUser());
							proxyEntity.setProxyServer(newProxyServerForUser);
							proxyEntity.setProxy(new Proxy(AppScanUtils
									.getIpAddress(), newProxyServerForUser
									.getPort(), true));

							openProxyServers.put(AppScanUtils.getLoggedInUser().getUserId(), proxyEntity);
						} else {
							LOGGER.error("Error in returnProxyDetails function. Browser Mob Service bean didn't send me a proxy server");
							// TODO throw custom exception condition
						}
					}
				}

				// There is no user logged in. Either this is an error or
				// something wrong in authentication.
				// Raise an error.
				else {
					LOGGER.error("Error in returnProxyDetails function. There is no logged in user, even though a proxy entity has arrived claiming to be from a logged in user.");
					// TODO Exception Condition - No logged in user.
				}

			}

			// Otherwise the request has come from a Bluefin/Breeze/Selenium
			// test case.
			else {
				// Since the request has come from a Bluefin/Breeze/Selenium
				// test case, the way
				// to handle it is totally different.
				// If there is an identifier and no proxy port and proxy IP
				// associated, then just create a new proxy server and send
				// it back. Doesn't matter even if it is from the same
				// person/test case. For a test case to associate itself with
				// a proxy server that has already been created, it has to send
				// a combination of 'proxy IP AND proxy port AND proxy
				// identifier (which
				// is a string)'
				if (openProxyServers.containsKey(proxyEntity.toString())) {
					final ProxyEntity proxyEntityForBluefinTestCase = openProxyServers
							.get(proxyEntity.toString());
					proxyEntity = proxyEntityForBluefinTestCase;
				} else {

					if (!AppScanUtils.isNotNull(proxyEntity
							.getProxyIdentifier())) {
						throw new DASTProxyException(
								"Please provide a valid AppScan Test Identifier");
					}

					if (!AppScanUtils.isNotNull(proxyEntity.getUser())
							|| !AppScanUtils.isNotNull(proxyEntity.getUser()
									.getUserId())) {
						throw new DASTProxyException(
								"Please provide a valid AppScan User Id");
					}
					final ProxyServer newProxyServerFoBluefinTestCase = getBrowserMobServiceBean()
							.setUpProxyAndStartRecordForUser(
									proxyEntity.getProxyIdentifier());
					proxyEntity.setProxy(new Proxy(AppScanUtils.getIpAddress(),
							newProxyServerFoBluefinTestCase.getPort(), true));
					proxyEntity.setProxyServer(newProxyServerFoBluefinTestCase);
					openProxyServers.put(proxyEntity.toString(), proxyEntity);

				}
			}

		} else {

			LOGGER.error("Error in returnProxyDetails function. The 'ProxyEntity' sent is null. No way to track a proxy server.");
			// TODO Exception - The 'ProxyEntity' sent is null. No way to track
			// a proxy server

		}

		return new ModelAndView(view,
				AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, proxyEntity);
	}

	/**
	 * Because of a problem with piping information over Bluefin/Breeze/Selenium
	 * authentication, I have had to make a separate rest call. In the future,
	 * the previous function has to be modified to ensure that the following
	 * functionality is taken care off.
	 * 
	 * TODO Remove this function and make sure the previous one handles both the
	 * scenarios
	 * 
	 * @param proxyEntity
	 * @return
	 * @throws DASTProxyException
	 */
	@RequestMapping(value = { "/rest/v1/proxy" }, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ModelAndView returnProxyDetailsForBluefin(@RequestBody ProxyEntity proxyEntity) throws DASTProxyException {
		// TODO Need to implement OWASP ESAPI to clean up the input coming from
		// Bluefin/Breeze/Selenium.

		if (proxyEntity != null) {

			// Since the request has come from a Bluefin/Breeze/Selenium test
			// case, the way
			// to handle it is totally different.
			// If there is an identifier and no proxy port and proxy IP
			// associated, then just create a new proxy server and send
			// it back. Doesn't matter even if it is from the same
			// person/test case. For a test case to associate itself with
			// a proxy server that has already been created, it has to send
			// a combination of 'proxy IP AND proxy port AND proxy
			// identifier (which
			// is a string)'
			if (openProxyServers.containsKey(proxyEntity.toString())) {
				final ProxyEntity proxyEntityForBluefinTestCase = openProxyServers
						.get(proxyEntity.toString());
				proxyEntity = proxyEntityForBluefinTestCase;
			} else {

				if (!AppScanUtils.isNotNull(proxyEntity.getProxyIdentifier())) {
					throw new DASTProxyException(
							"Please provide a valid AppScan Test Identifier");
				}

				if (!AppScanUtils.isNotNull(proxyEntity.getUser())
						|| !AppScanUtils.isNotNull(proxyEntity.getUser()
								.getUserId())) {
					throw new DASTProxyException("Please provide a valid AppScan User Id");
				}
				final ProxyServer newProxyServerFoBluefinTestCase = getBrowserMobServiceBean().setUpProxyAndStartRecordForUser(proxyEntity.getProxyIdentifier());
				proxyEntity.setProxy(new Proxy(AppScanUtils.getIpAddress(),newProxyServerFoBluefinTestCase.getPort(), true));
				proxyEntity.setProxyServer(newProxyServerFoBluefinTestCase);
				openProxyServers.put(proxyEntity.toString(), proxyEntity);

			}

		} else {

			// The 'ProxyEntity' sent is null. No way to track a proxy server
			LOGGER.error("Error in returnProxyDetails function. The 'ProxyEntity' sent is null. No way to track a proxy server.");
			throw new DASTProxyException(
					"Error in the data being sent. It is not in the correct format.");

		}

		return new ModelAndView(view,
				AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, proxyEntity);
	}

	/**
	 * This API end point is to find out if there is a proxy already running for
	 * a user id. Currently proxy servers are coupled to a user. Going forward
	 * we will have to move away from this.
	 * 
	 * @param userId
	 * @return true/false (in JSON format)
	 */
	@RequestMapping(value = { "/rest/v1/proxy/{userId}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView isProxyRunning(@PathVariable final String userId) {
		boolean isRunning = false;

		if (AppScanUtils.isNotNull(userId)
				&& !openProxyServers.isEmpty()
				&& openProxyServers.containsKey(AppScanUtils.getLoggedInUser()
						.getUserId())) {
			isRunning = true;
		}

		return new ModelAndView(view,
				AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, isRunning);
	}

	@RequestMapping(value = { "/rest/v1/proxy/cancel/{userId}" }, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ModelAndView stopProxyAndDiscardRecording(
			@PathVariable final String userId) throws DASTProxyException {
		LOGGER.debug("Inside stopProxyAndDiscardRecording function");

		if (AppScanUtils.isNotNull(AppScanUtils.getLoggedInUser().getUserId())
		/*
		 * && !AppScanUtils.getLoggedInUser().getUserId()
		 * .equalsIgnoreCase(AppScanConstants.BLUEFIN_USER_NAME)
		 */) {
			if (!openProxyServers.isEmpty() && openProxyServers.containsKey(AppScanUtils.getLoggedInUser().getUserId())) {
				browserMobServiceBean.stopServer(openProxyServers.get(AppScanUtils.getLoggedInUser().getUserId()).getProxyServer(), AppScanUtils.getLoggedInUser().getUserId());
				openProxyServers.remove(AppScanUtils.getLoggedInUser().getUserId());
			}

		} else {
			throw new DASTProxyException("No User Exists");
		}

		return new ModelAndView(view,
				AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, "success");
	}

	@RequestMapping(value = { "/rest/v1/proxy/cancel" }, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ModelAndView stopProxyAndDiscardRecording(
			@RequestBody final ProxyEntity proxyEntity)
			throws DASTProxyException {
		LOGGER.debug("Inside stopProxyAndDiscardRecording function");

		if (AppScanUtils.isNotNull(proxyEntity)) {

			if (!openProxyServers.isEmpty()
					&& openProxyServers.containsKey(proxyEntity.toString())) {

				browserMobServiceBean.stopServer(
						openProxyServers.get(proxyEntity.toString())
								.getProxyServer(), proxyEntity.getUser()
								.getUserId());
				openProxyServers.remove(proxyEntity.toString());

			} else {
				throw new DASTProxyException(
						"No record of Proxy being created for the combination of given user id, proxy identifier, ip address, port and optional parameters of testcase suite and test case name");
			}

		} else {
			throw new DASTProxyException("Incorrect Proxy Instance Details");
		}

		return new ModelAndView(view,
				AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, "success");
	}

	/**
	 * This API end point will return the object of the user in JSON format.
	 * 
	 * @return Logged in User Object (in JSON format)
	 */
	@RequestMapping(value = { "/rest/v1/user" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ModelAndView returnLoggedInUser() {

		// For the sake of security I am blanking out the password here.
		final User loggedInUser = AppScanUtils.getLoggedInUser();
		loggedInUser.setPassword("");
		return new ModelAndView(view,
				AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, loggedInUser);
	}

	@RequestMapping(value = { "/rest/v1/har/{userId}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ModelAndView generateAndReturnCustomizedConfigFile(
			@PathVariable final String userId) throws DASTProxyException,
			IOException {
		final String nameOfHTD = generateCustomizedConfigFile(userId, null,
				false);
		return new ModelAndView(view,
				AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, nameOfHTD);
	}

	private String generateCustomizedConfigFile(final String userId,
			final ProxyEntity proxyEntity, final boolean apiCall)
			throws DASTProxyException, IOException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside generateCustomizedConfigFile");
			LOGGER.debug("Request is for htd file with the identifier "+ userId);
			LOGGER.debug("Request is for htd file with the identifier apiCall"+ apiCall);
			LOGGER.debug("Request is for htd file with the identifier openProxyServers.containsKey(userId)"+ openProxyServers.containsKey(userId));
		}

		String nameOfHTD = null;

		if (!apiCall) {
			LOGGER.debug("Inside generateCustomizedConfigFile...2");
			if (!openProxyServers.isEmpty() && openProxyServers.containsKey(userId)) {
				LOGGER.debug("Inside generateCustomizedConfigFile...3");
				nameOfHTD = getBrowserMobServiceBean().stopServerAndReturnHar(openProxyServers.get(userId).getProxyServer(),userId, null);
				openProxyServers.remove(userId);
			}
		} else {
			/*
			 * if (AppScanUtils.getLoggedInUser().getUserId()
			 * .equalsIgnoreCase(AppScanConstants.BLUEFIN_USER_NAME)) {
			 */

			if (!openProxyServers.isEmpty() && openProxyServers.containsKey(userId)) {
				nameOfHTD = getBrowserMobServiceBean().stopServerAndReturnHar(
						openProxyServers.get(userId).getProxyServer(),
						proxyEntity.getUser().getUserId(),
						AppScanUtils.returnWindowsFileAppropriateName(proxyEntity.toString()));
				LOGGER.debug("Inside generateCustomizedConfigFile...8");
				openProxyServers.remove(userId);
			}
			/*
			 * } else { throw new DASTProxyException("401",
			 * "Unauthorized Access"); }
			 */
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Leaving generateCustomizedConfigFile. The htd name for the identifier "
					+ userId + " is " + nameOfHTD);
		}

		return nameOfHTD;

	}

	@RequestMapping(value = { "/rest/v1/htd/" + "{fileId:.+}" }, method = RequestMethod.GET)
	public void returnHTDFile(@PathVariable final String fileId,
			final HttpServletResponse response) throws Exception {
		// TODO Need to ensure authentication
		// TODO Handle Exception

		final String filePath = new StringBuilder(
				AppScanConstants.USER_HTD_FILES_LOCATION)
				.append(File.separator)
				.append(AppScanUtils.getLoggedInUser().getUserId())
				.append(File.separator).append(fileId).toString();

		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ fileId + "\"");
		IOUtils.copy(new FileInputStream(new File(filePath)),
				response.getOutputStream());
		response.flushBuffer();
	}

	@RequestMapping(value = { "/rest/v1/har/{userId}/{fileId}"}, method = RequestMethod.GET)
	public void returnHARFile(@PathVariable final String userId, @PathVariable final String fileId,
			final HttpServletResponse response) throws Exception {
		// TODO Need to ensure authentication
		// TODO Handle Exception

		final String filePath = new StringBuilder(
				AppScanConstants.USER_HTD_FILES_LOCATION)
				.append(File.separator)
				.append(AppScanUtils.getLoggedInUser().getUserId())
				.append(File.separator).append(fileId).toString()+".har";

		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileId + ".har");
		IOUtils.copy(new FileInputStream(new File(filePath)),response.getOutputStream());
		response.flushBuffer();
	}


	@RequestMapping(value = { "/rest/v1/logout" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView logout() {
		SecurityContextHolder.getContext().setAuthentication(null);

		return new ModelAndView(view,
				AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, "success");
	}

	@RequestMapping(value = { "/rest/v1/security/{userId}/{recordingName}/{testsuiteName}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView setUpScan(@PathVariable final String userId, @PathVariable final String recordingName, @PathVariable final String testsuiteName) throws DASTProxyException {
		try {
			final String userIdFromSession = AppScanUtils.getLoggedInUser().getUserId();
			final String passwordFromSession = AppScanUtils.getLoggedInUser().getPassword();

			new Thread(new Runnable() {
			    public void run() {
		String nameOfConfig = null;
		String filePath = null;
		Scan scan = null;
					Recording recording = null;

		try {
			nameOfConfig = generateCustomizedConfigFile(userId, null, false);
							scan = new Scan();
							scan.setFirstSetUp(new Date());
							scan.setToBeTracked(true);
							scan.setTestSuiteName(testsuiteName);
							scan.setScanState(AppScanConstants.APPSCAN_JOB_READY_FOR_SCAN);

							if (scan.getReport()==null) {
								scan.setReport(new Report());
								scan.getReport().setAseReportId("");
								dao.saveGenericEntity(scan.getReport());
							}

							User user = dao.getUser(userIdFromSession);

							if (user==null){
								user = new User();
								user.setUserId(userIdFromSession);
								dao.saveGenericEntity(user);
								LOGGER.debug("Nightly recording batch is not there the user "+userIdFromSession+", and hence creating one.");
								RecordingBatch nightlyrecordingBatch = dastUtils.createRecordingBatch("Nightly Batch", userIdFromSession, false, true);
								dao.saveGenericEntity(nightlyrecordingBatch);
							}

							scan.setUser(user);

							if (recordingName != null && !"none".equals(recordingName)){
								String tempRecName =recordingName;
								if (recordingName.length() > 45) tempRecName = recordingName.substring(0, 44);
								scan.setTestCaseName(tempRecName);
							} else {
								scan.setTestCaseName(userIdFromSession + AppScanUtils.returnDateInPredefinedFormat());
							}

			filePath = new StringBuilder(
					AppScanConstants.USER_HTD_FILES_LOCATION)
					.append(File.separator)
									.append(userIdFromSession)
					.append(File.separator).append(nameOfConfig).toString();

		DastUtils dastUtils = new DastUtils();
		// Once all the scan set up work is done, then save the scan for metric
		// purposes.
		scan.setSetUpViaBluefin(false);
						recording = dastUtils.createRecording(recordingName, userId, filePath);
						RecordingBatch recordingBatch = dao.getManualRecordingBatch(userIdFromSession);
		if ( recordingBatch == null){
							recordingBatch = dastUtils.createRecordingBatch("Manual Test Suite", userIdFromSession, true, false);
			dao.saveGenericEntity(recordingBatch);
		}
		dao.saveScan(scan);
		List<Scan> scans = new ArrayList<Scan>();
		scans.add(scan);
						ScanBatch scanBatch = dastUtils.createScanBatch(recordingBatch.getId(), userIdFromSession, AppScanConstants.APPSCAN_MANUAL_TEST_SUITE, scans);
		dao.saveGenericEntity(scanBatch);

		scan.setTestCaseName(recording.getTestcaseName());
		recording.setRecordingBatchId(recordingBatch.getId());
		scan.setBatch(scanBatch);
						scan.setTestSuiteName(testsuiteName);
						scan.setScanState(AppScanConstants.APPSCAN_JOB_SCAN_STATE_NEW);
						dao.saveGenericEntity(recording);
						scan.setRecordingId(recording.getId());
						dao.saveGenericEntity(scanBatch);
		dao.saveScan(scan);
						dastApiService.setUpScanForUser(userIdFromSession, passwordFromSession, filePath, null, true, scan);

					} catch (Exception exception) {
						LOGGER.error("There has been an error while trying to set the scan up. The details of the error is: "+ exception);
						LOGGER.error("Another attempt will be made to re-submit the scan.");

					}

					//Need to get this from factory...
					zapService.scanWithZap(recording.getHarFilename(), scan.getReport());
		dao.saveGenericEntity(recording);
					dao.saveGenericEntity(scan.getReport());
					dao.saveScan(scan);

			    }
				}).start();
			} catch (Exception exception) {
				exception.printStackTrace();
				LOGGER.error("There has been an error while trying to set the scan up. The details of the error is: "+ exception);
			}
		return new ModelAndView(view, AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, "success");
	}



	@RequestMapping(value = { "/rest/v1/selenium/dastscan" }, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ModelAndView setUpScanViaBluefin(
			@RequestBody final ProxyEntity proxyEntity)
			throws DASTProxyException {

		String nameOfConfig = null;
		if (AppScanUtils.isNotNull(proxyEntity) && AppScanUtils.isNotNull(proxyEntity.getUser())) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Call is from "
						+ proxyEntity.getUser().getUserId()
						+ ". The test suite and test case name is "
						+ proxyEntity.getTestCaseSuiteName() + " and "
						+ proxyEntity.getTestCaseName()
						+ ". The IP and Port Values are "
						+ proxyEntity.getProxy().getProxyAddress() + " and "
						+ proxyEntity.getProxy().getProxyPort());
			}
			try {
				proxyEntity.setTestCaseName(URLEncoder.encode(proxyEntity.getTestCaseName(), "UTF-8"));
			} catch(UnsupportedEncodingException unc){
				LOGGER.error(unc);
			}

			final ScanConfiguration scanConfiguration = new ScanConfiguration();
			proxyEntity.setScanConfiguration(scanConfiguration);

			// String nameOfScan = null;
			// boolean startScanAutomatically = false;
			if (!openProxyServers.isEmpty() && openProxyServers.containsKey(proxyEntity.toString())) {
				// TODO Input validation
				proxyEntity.getScanConfiguration().setNameOfScan(
						proxyEntity.getTestCaseName());

				for (final String configParam : proxyEntity.getScanConfigurationParameters()) {
					if (configParam.equalsIgnoreCase(AppScanConstants.APPSCAN_AUTOMATIC_START_SCAN)) {
						// startScanAutomatically = true;
						proxyEntity.getScanConfiguration().setStartScan(true);
					}
				}

			}

			try {
				nameOfConfig = generateCustomizedConfigFile(proxyEntity.toString(), proxyEntity, true);
			} catch (Exception exception) {
				LOGGER.error(exception);
				throw new DASTProxyException("Exception in getting your recording. Contact your Administrator");
			}
			proxyEntity.getProxy().setHtdFileName(nameOfConfig);

			try {
				dao.saveEntity(proxyEntity);
			} catch (Exception exception) {
				LOGGER.error(exception);
				throw new DASTProxyException("Unable to submit the recording to set up a scan. Contact your Administrator");
			}

		} else {

			throw new DASTProxyException("Error in information regarding the recording for which scan has to be set up. Contact Administrator");
		}

		return new ModelAndView(
				view,
				"data",
				"Recording has been submitted for setting up a scan. The scan will be set up in a short while.");
	}

	@RequestMapping(value = { "/rest/contactus", "/rest/v1/contactus" }, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ModelAndView sendEmailBasedOnCustomerFeedback(
			@RequestBody final ContactUsIssue contactUsIssue)
			throws DASTProxyException {

		if (AppScanUtils.isNotNull(contactUsIssue)) {
			if (AppScanUtils.isNotNull(AppScanUtils.getLoggedInUser())
					&& AppScanUtils.isNotNull(AppScanUtils.getLoggedInUser()
							.getUserId())) {

				if (AppScanUtils.isNotNull(contactUsIssue.getUser())
						&& AppScanUtils.isNotNull(contactUsIssue.getUser()
								.getUserId())) {

					if (!AppScanUtils
							.getLoggedInUser()
							.getUserId()
							.equalsIgnoreCase(
									contactUsIssue.getUser().getUserId())) {

						LOGGER.error("The contact us message has different users. Logged in user is: "
								+ AppScanUtils.getLoggedInUser().getUserId()
								+ " and contact us user is :"
								+ contactUsIssue.getUser().getUserId());
					}

					final Map<String, Object> contactUsModel = new HashMap<String, Object>();
					contactUsModel
							.put("currentEnvironment",
									RootConfiguration
											.getProperties()
											.get(AppScanConstants.PROPERTIES_OPERATING_ENVIRONMENT_IDENTIFIER));
					contactUsModel.put("issueType",
							contactUsIssue.getIssueType());
					contactUsModel.put("userId", AppScanUtils.getLoggedInUser()
							.getUserId());
					contactUsModel.put("issueDescription",
							contactUsIssue.getDesc());

					MailUtils
							.sendEmail(
									RootConfiguration
											.getProperties()
											.getProperty(
													AppScanConstants.PROPERTIES_CONTACT_US_SUPPORT_DL_IDENTIFIER),
											null,
									AppScanUtils.getLoggedInUser().getUserId()
											+ RootConfiguration.getProperties().getProperty(AppScanConstants.EMAIL_DOMAIN),
									"Contact Us Message From "
											+ AppScanUtils.getLoggedInUser()
													.getUserId(),
									contactUsModel, "contactUs.vm");
				}
			}
		} else {
			throw new DASTProxyException(
					"There has been no details when user contacted us");
		}

		return new ModelAndView(view,
				AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, "success");
	}


	/**
	 * 
	 * @param scanId
	 * @param issueId
	 * @return issue
	 * @throws DASTProxyException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	@RequestMapping(value = { "/rest/v1/report/{reportId}/issue/{issueId}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ModelAndView getDetailsOfOneIssue(
			@PathVariable final String reportId,
			@PathVariable final String issueId)
			throws UnsupportedEncodingException, XPathExpressionException,
			ParserConfigurationException, IOException, SAXException,
			DASTProxyException {

		final Issue issue = dao.getIssueByNativeId(new Long(issueId));
		IssueVO issueVO = new IssueVO();
		issueVO.setId(issue.getId()+"");
		issueVO.setIssueId(issue.getNativeIssueId());

		issueVO.setIssueType(issue.getIssueType());
		issueVO.setIssueUrl(issue.getIssueUrl());
		issueVO.setSeverity(issue.getSeverity());
		issueVO.setTestUrl(issue.getTestUrl());
		issueVO.setReportId(reportId);
		if (issue.getJira() != null) {
			issueVO.setJiraURL(issue.getJira().getSelf());
			issueVO.setJiraKey(issue.getJira().getKey());
		}
		issueVO.setTestHTTPtraffic(issue.getTestHttpTraffic());
		issueVO.setOrigHTTPtraffic(issue.getOriginalHttpTraffic());

		if (issue.getFpComments() != null) issueVO.setFpComments(issue.getFpComments());
		issueVO.setFp(issue.isFp());
		issueVO.setScanEngine(issue.getScanEngine());
		if (issue.getFpReasonId() != null) {
			issueVO.setFpReasonId(issue.getFpReasonId());
			//Need to cache this.
			FpReason fpReason = (FpReason)dao.getEntity(FpReason.class, issue.getFpReasonId());

			if (fpReason.getAbbr().equalsIgnoreCase("OTHER")){
				issueVO.setFpComments(fpReason.getAbbr() + "-" + issue.getFpComments());
		} else {
				issueVO.setFpComments(fpReason.getName());
		}
	}
		return new ModelAndView(view,AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, issueVO);
	}


	private List<IssueVO> convertIssueEntitiestoVO(List<Issue> issues, String testcaseName){
		List<IssueVO> issuesVO = new ArrayList<IssueVO>();
		try {
			String environmentURL = (String)RootConfiguration.getProperties().get(AppScanConstants.PROPERTIES_OPERATING_ENVIRONMENT_BASE_URL_IDENTIFIER);
		if (issues != null){
			for (Issue issue : issues){
				IssueVO issueVO = new IssueVO();
				issueVO.setIssueId(issue.getId()+"");
				issueVO.setIssueType(issue.getIssueType());
				issueVO.setIssueUrl(issue.getIssueUrl());
				issueVO.setSeverity(issue.getSeverity());
				issueVO.setTestUrl(issue.getTestUrl());
				issueVO.setIssueDastUrl(environmentURL+"issueNew?report="+issue.getReport().getId()+"&issue="+issue.getId());
				issueVO.setTestcaseName(testcaseName);
				issueVO.setReportId(issue.getReport().getAseReportId());
				if (issue.getJira() != null) issueVO.setJiraURL(issue.getJira().getKey());
				issueVO.setTestHTTPtraffic(issue.getTestHttpTraffic());
				issueVO.setOrigHTTPtraffic(issue.getOriginalHttpTraffic());
				if (issue.getFpComments() != null) issueVO.setFpComments(issue.getFpComments());
				issueVO.setFp(issue.isFp());
				issueVO.setScanEngine(issue.getScanEngine());
				if (issue.getFpReasonId() != null) {
					issueVO.setFpReasonId(issue.getFpReasonId());
					//Need to cache this.
					FpReason fpReason = (FpReason)dao.getEntity(FpReason.class, issue.getFpReasonId());

					if (fpReason.getAbbr().equalsIgnoreCase("OTHER")){
						issueVO.setFpComments(fpReason.getAbbr() + "-" + issue.getFpComments());
					} else {
						issueVO.setFpComments(fpReason.getName());
					}
				}


				issuesVO.add(issueVO);
			}
		}
		} catch (Exception e){
			e.printStackTrace();
		}
		return issuesVO;
	}


	@RequestMapping(value = { "/rest/v1/recordingbatches" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ModelAndView getRecordingBatches(){

		final List<RecordingBatch> recordingBatches= dao.getRecordingBatches(AppScanUtils.getLoggedInUser().getUserId());
		return new ModelAndView(view,
				AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, recordingBatches);
	}
	@RequestMapping(value = { "/rest/v1/scanbatches" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ModelAndView getScanBatches(){
		DastUtils dastUtils = new DastUtils();
		List<ScanBatch> scanBatches= null;
		try{

		scanBatches = dao.getScanBatches(AppScanUtils.getLoggedInUser().getUserId(), dastUtils.isAdmin(AppScanUtils.getLoggedInUser().getUserId()));
		for (ScanBatch batch : scanBatches){
			long readyCount = batch.getScans().stream().filter(scan -> "New".equals(scan.getScanState())).count();
			readyCount += batch.getScans().stream().filter(scan -> scan.getScanState() == null).count();
			long runningCount = batch.getScans().stream().filter(scan -> "Running".equals(scan.getScanState())).count();
			long suspendedCount = batch.getScans().stream().filter(scan -> "Suspended".equals(scan.getScanState())).count();
			long completedCount = batch.getScans().stream().filter(scan -> "Ready".equals(scan.getScanState())).count();
			batch.setDisplayStatus("Completed: " + completedCount + ", Running : " + runningCount + ", Suspended : " + suspendedCount+ ", New  : " + readyCount);
			//Setting this to null to avoid unnecessary scans data to be emitted as JSON. We are getting the scans data from DB only for displaying the status summary.
			batch.setScans(null);
		}
		} catch(Exception e){
			e.printStackTrace();
		}

		return new ModelAndView(view, AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, scanBatches);
	}

	@RequestMapping(value = { "/scanbatch/v1/{recordingBatchId}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView scanBatch(@PathVariable final Long recordingBatchId) throws DASTProxyException {
		String retMessage="success";
		ScanBatch scanBatch = null;
		List<Recording> recordings = null;
		try {
			RecordingBatch recBatch = dao.getRecordingBatch(recordingBatchId);
			recordings = dao.getRecordingsByBatchId(recordingBatchId);
			if (recordings != null){
				if (recordings.size() > 0){
					scanBatch = new ScanBatch();
					scanBatch.setTestsuiteName(recBatch.getTestsuiteName());
					scanBatch.setOwner(AppScanUtils.getLoggedInUser().getUserId());
					scanBatch.setRecordingBatchId(recordingBatchId);
					scanBatch.setSubsetOfBatch(false);
					scanBatch.setDateCreated(new Date());
					dao.saveScanBatch(scanBatch);
				}
				for (Recording recording: recordings){
					Scan scan = createScan(recording.getTestcaseName(), recording.getTestsuiteName(), AppScanUtils.getLoggedInUser().getUserId());
					scan.setRecordingId(recording.getId());
					scan.setSetUpViaBluefin(false);
					scan.setBatch(scanBatch);
					dao.saveScan(scan);

					try{
						dastApiService.setUpScanForUser(AppScanUtils
								.getLoggedInUser().getUserId(), AppScanUtils
								.getLoggedInUser().getPassword(), recording.getHarFilename(), recording.getTestcaseName(), true, scan);
					} catch(Exception exception){
						LOGGER.error("There is an issue in submitting the scan to the server. The operation is saved and will be tried again."+ exception);
					}

					zapService.scanWithZap(recording.getHarFilename(), scan.getReport());
					dao.saveGenericEntity(recording);
					dao.saveScan(scan);


				}
			}

		} catch (Exception exception) {
			retMessage="error";
			LOGGER.error("There has been an error while trying to set the scan up (using the recording which was done earlier). The details of the error is: "+ exception);
		}

		return new ModelAndView(view,
				AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, retMessage);
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
	
	
	@RequestMapping(value = { "/addtonightlyrecordingbatch/v1/{recordingIds}/{batchId}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView addToNightlyRecordingBatch(@PathVariable final String recordingIds, @PathVariable final Long batchId) throws DASTProxyException {
		final String userIdFromSession = AppScanUtils.getLoggedInUser().getUserId();

		String retMessage="success";
		List<Recording> recordings = null;
		try {
			RecordingBatch nightlyrecordingBatch = dao.getNightlyRecordingBatch(userIdFromSession);
			if ( nightlyrecordingBatch == null){
				LOGGER.debug("Nightly recording batch was not found for the user "+userIdFromSession+", and hence creating one.");
				nightlyrecordingBatch = dastUtils.createRecordingBatch("Nightly Batch", userIdFromSession, false, true);
				dao.saveGenericEntity(nightlyrecordingBatch);
			}
			
			recordings = dao.getRecordingsByBatchId(batchId);
			if (recordings != null){
				for (Recording recording: recordings){
					if (!recordingIds.contains("-"+recording.getId()+"-")) continue;
					LOGGER.debug("-------------------------------------------------Inside scanselectedrecordings...Moving the recording with id="+recording.getId() +" to nightly batch.");
					recording.setRecordingBatchId(nightlyrecordingBatch.getId());
					dao.saveGenericEntity(recording);
				}
			}
		} catch (Exception exception) {
			retMessage="error";
			LOGGER.error("There has been an error in moving the recording to nightly batch for user "+ userIdFromSession);
			LOGGER.error(exception);
		}		

		return new ModelAndView(view, AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, retMessage);
	}
	
	@RequestMapping(value = { "/scanselectedrecordings/v1/{recordingIds}/{batchId}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView scanselectedrecordings(@PathVariable final String recordingIds, @PathVariable final Long batchId)
			throws DASTProxyException {
		String retMessage="success";
		ScanBatch scanBatch = null;
		List<Recording> recordings = null;
		try {
			RecordingBatch recBatch = dao.getRecordingBatch(batchId);
			//StringTokenizer tokenizer = new StringTokenizer(recordingIds, "-");

			recordings = dao.getRecordingsByBatchId(batchId);
			if (recordings != null){
				if (recordings.size() > 0){
					scanBatch = new ScanBatch();
					scanBatch.setTestsuiteName(recBatch.getTestsuiteName());
					scanBatch.setOwner(AppScanUtils.getLoggedInUser().getUserId());
					scanBatch.setRecordingBatchId(batchId);
					scanBatch.setSubsetOfBatch(false);
					scanBatch.setDateCreated(new Date());
					dao.saveScanBatch(scanBatch);
				}
				for (Recording recording: recordings){
					if (!recordingIds.contains("-"+recording.getId()+"-")) continue;
					Scan scan = createScan(recording.getTestcaseName(), recording.getTestsuiteName(), AppScanUtils.getLoggedInUser().getUserId());
					scan.setRecordingId(recording.getId());
					scan.setSetUpViaBluefin(false);
					scan.setBatch(scanBatch);

					dao.saveScan(scan);
					try{
						dastApiService.setUpScanForUser(AppScanUtils
								.getLoggedInUser().getUserId(), AppScanUtils
								.getLoggedInUser().getPassword(), recording.getHarFilename(), recording.getTestcaseName(), true, scan);
					} catch(Exception exception){
						LOGGER.error("There is an issue in submitting the scan to the server. The operation is saved and will be tried again."+ exception);
					}
					if (scan.getReport()==null) {
						scan.setReport(new Report());
						scan.getReport().setAseReportId("");
					};
					zapService.scanWithZap(recording.getHarFilename(), scan.getReport());
					dao.saveGenericEntity(recording);
					dao.saveScan(scan);

				}
			}

		} catch (Exception exception) {
			retMessage="error";
			LOGGER.error("There has been an error while trying to set the scan up (using the recording which was done earlier). The details of the error is: "+ exception);
		}


		return new ModelAndView(view, AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, retMessage);
	}
	@RequestMapping(value = { "/rest/v1/issues/{scanBatchId}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ModelAndView getIssuesOfScanBatch(@PathVariable final Long scanBatchId) {
		List<IssueVO> issuesVO = new ArrayList<IssueVO>();
		try {
			DastUtils dastUtils = new DastUtils();
			boolean isAdmin = dastUtils.isAdmin(AppScanUtils.getLoggedInUser().getUserId());
			ScanBatch scanBatch = dao.getScanBatch(AppScanUtils.getLoggedInUser().getUserId(), scanBatchId, isAdmin);
		
			if (scanBatch == null) return new ModelAndView(view,AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, issuesVO);
		

		List<Scan> scans = scanBatch.getScans();

		for (Scan scan: scans){
			issuesVO.addAll(convertIssueEntitiestoVO(scan.getReport().getIssues(), scan.getTestCaseName()));
		}
		} catch (Exception e){
			e.printStackTrace();
		}

		return new ModelAndView(view,AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, issuesVO);
	}
	@RequestMapping(value = { "/rest/v1/reportnew/{reportId}/issuenew/{issueId}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ModelAndView getIssue(@PathVariable final Long issueId,@PathVariable final String reportId) {
		Issue issue = dao.getIssueById(issueId);
		IssueVO issueVO = new IssueVO();
		issueVO.setId(issue.getId()+"");
		issueVO.setIssueId(issue.getNativeIssueId());

		issueVO.setIssueType(issue.getIssueType());
		issueVO.setIssueUrl(issue.getIssueUrl());
		issueVO.setSeverity(issue.getSeverity());
		issueVO.setTestUrl(issue.getTestUrl());
		issueVO.setReportId(reportId);
		if (issue.getJira() != null) {
			issueVO.setJiraURL(issue.getJira().getSelf());
			issueVO.setJiraKey(issue.getJira().getKey());
		}
		issueVO.setTestHTTPtraffic(issue.getTestHttpTraffic());
		issueVO.setOrigHTTPtraffic(issue.getOriginalHttpTraffic());

		if (issue.getFpComments() != null) issueVO.setFpComments(issue.getFpComments());
		issueVO.setFp(issue.isFp());
		issueVO.setScanEngine(issue.getScanEngine());
		if (issue.getFpReasonId() != null) {
			issueVO.setFpReasonId(issue.getFpReasonId());
			//Need to cache this.
			FpReason fpReason = (FpReason)dao.getEntity(FpReason.class, issue.getFpReasonId());

			if (fpReason.getAbbr().equalsIgnoreCase("OTHER")){
				issueVO.setFpComments(fpReason.getAbbr() + "-" + issue.getFpComments());
			} else {
				issueVO.setFpComments(fpReason.getName());
			}
		}

		return new ModelAndView(view,AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, issueVO);
	}

	@RequestMapping(value = { "/rest/v1/scans/{scanBatchId}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ModelAndView getScans(@PathVariable final Long scanBatchId) {
		DastUtils dastUtils = new DastUtils();
		ScanBatch scanBatch = dao.getScanBatch(AppScanUtils.getLoggedInUser().getUserId(), scanBatchId, dastUtils.isAdmin(AppScanUtils.getLoggedInUser().getUserId()));
		
		if (scanBatch == null){
			return new ModelAndView(view,AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, new ArrayList());

		}
		List<Scan> scans = scanBatch.getScans();
		
		for (Scan scan : scans){
			scan.setBatch(null);
		}

		return new ModelAndView(view,AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, scans);
	}

	@RequestMapping(value = { "/rest/v1/recordings/{recordingBatchId}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView getRecordingsByBatchId(@PathVariable final Long recordingBatchId)
			throws DASTProxyException {
		List<Recording> recordings = null;
		recordings = dao.getRecordingsByBatchId(recordingBatchId);

		return new ModelAndView(view, AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, recordings);
	}

	@RequestMapping(value = { "/rest/v1/mark_fp" }, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ModelAndView markAsFP(@RequestBody FpInput fpInput) {
		boolean retValue = true;

		try {
			Issue issue = (Issue) dao.getIssueById(new Long(fpInput.issueId));
			issue.setFpComments(fpInput.comments);
			issue.setFpMarkedBy(AppScanUtils.getLoggedInUser().getUserId());
			issue.setFpReasonId(fpInput.fpReasonId);
			issue.setFp(true);
			issue.setFpMarkedDate(new Date());

			dao.saveIssue(issue);
		} catch (Exception exception){
			retValue = false;
			LOGGER.error(exception);
			exception.printStackTrace();
		}

		return new ModelAndView(view,AppScanConstants.JSON_RESPONSE_DATA_IDENTIFIER, retValue);
	}

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
	public void setBrowserMobServiceBean(final BrowserMobServiceBean browserMobServiceBean) {
		this.browserMobServiceBean = browserMobServiceBean;
	}

	/**
	 * @return the view
	 */
	public View getView() {
		return view;
	}

	/**
	 * @param view
	 *            the view to set
	 */
	public void setView(View view) {
		this.view = view;
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

	/**
	 * @return the jiraPublisherService
	 */
	public JiraPublisherService getJiraPublisherService() {
		return jiraPublisherService;
	}

	/**
	 * @param jiraPublisherService
	 *            the jiraPublisherService to set
	 */
	public void setJiraPublisherService(
			final JiraPublisherService jiraPublisherService) {
		this.jiraPublisherService = jiraPublisherService;
	}


}
