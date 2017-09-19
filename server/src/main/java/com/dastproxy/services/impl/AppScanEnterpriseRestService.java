/**
 * This is the class that does the actual connection and set up actions for AppScan Enterprise.
 * This has the code for:
 * 1. Logging into ASE via service account (the account of the server on which DAST Proxy is deployed)
 * 2. Setting up a scan based on a default template.
 * 3. Uploading a HTD file to configure that scan.
 *
 * PLEASE NOTE - Because of the time constraints a lot of the code is based on the sample example that IBM has developed to test their REST API.
 *
 * @author Kiran Shirali (kshirali@ebay.com)
 */

package com.dastproxy.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.http.protocol.HTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.dastproxy.common.constants.AppScanConstants;
import com.dastproxy.common.utils.AppScanUtils;
import com.dastproxy.common.utils.MailUtils;
import com.dastproxy.configuration.RootConfiguration;
import com.dastproxy.dao.impl.DastDAOImpl;
import com.dastproxy.model.DASTProxyException;
import com.dastproxy.model.FpReason;
import com.dastproxy.model.Issue;
import com.dastproxy.model.Report;
import com.dastproxy.model.Scan;
import com.dastproxy.services.DASTApiService;
import com.dastproxy.services.ScanStatusNotifier;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.lightbody.bmp.core.har.copy.Har;

@Service
@Qualifier("appScanEnterpriseRestService")
public class AppScanEnterpriseRestService implements DASTApiService {

	enum ScanValidationResult {
		SUCCESS, EXTERNAL_URLS, NO_URLS;
	}

	// Logger for the class
	private static final Logger LOGGER = LogManager
			.getLogger(AppScanEnterpriseRestService.class.getName());

	@Autowired
	private DastDAOImpl dastDAOImpl;

	// This will be used to hold any cookie that comes with the response from
	// ASE.
	private static String cookieContainer = "";
	private String templateId="1";
	private String userFolderId;

	private static String baseURL = RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_OPERATING_ENVIRONMENT_BASE_URL_IDENTIFIER);

	@Autowired
	private ScanStatusNotifier scanStatusNotifier;

	public ScanStatusNotifier getScanStatusNotifier() {
		return scanStatusNotifier;
	}

	public void setScanStatusNotifier(ScanStatusNotifier scanStatusNotifier) {
		this.scanStatusNotifier = scanStatusNotifier;
	}

	/*
	 * This sets up the namespace for the responses from ASE. We are going to
	 * use xpath to evaluate a lot of the responses
	 */
	private static NamespaceContext _nsContext;
	static {

		_nsContext = new NamespaceContext() {
			public String getNamespaceURI(String prefix) {
			if (prefix.equalsIgnoreCase("ase"))
					return "http://www.ibm.com/Rational/AppScanEnterprise";
				return XMLConstants.NULL_NS_URI;
			}

			public String getPrefix(String arg0) {
				return null;
			}

			public Iterator<?> getPrefixes(String arg0) {
				return null;
			}
		};

		// This part is to accept the connection from ASE. The present server of
		// ASE presents a self signed SSL certificate to all requests.
		// The following lets java accepts certificates of such type. In case
		// ASE is provided a certificate that is recognized by the JVM's
		// key store, we could possibly comment out this code.
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs,
					String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs,
					String authType) {
			}

		} };

		SSLContext sslContext;
		try {
			sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts,
					new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
					.getSocketFactory());
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			LOGGER.error("Exception when accepting the certificate from IBM AppScan Enterprise"
					+ noSuchAlgorithmException);
			// new
			// DASTProxyException("Exception when accepting the certificate from IBM AppScan Enterprise: "
			// + noSuchAlgorithmException.getMessage());
		} catch (KeyManagementException keyManagementException) {
			LOGGER.error("Exception when accepting the certificate from IBM AppScan Enterprise"
					+ keyManagementException);
			// new
			// DASTProxyException("Exception when accepting the certificate from IBM AppScan Enterprise: "
			// + keyManagementException.getMessage());
		}

		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

			/*
			 * The idea here is accept all SSL connections.
			 *
			 * @see javax.net.ssl.HostnameVerifier#verify(java.lang.String,
			 * javax.net.ssl.SSLSession)
			 */
			public boolean verify(final String hostname,
					final SSLSession session) {
				return true;
			}
		});

	}

	/**
	 * This is the primary function that service provides. Here the
	 * functionality is something like this:
	 *
	 * 1. The service will log into ASE based on the account the underlying
	 * account of the server on which DAST Proxy is deployed on. 2. It will then
	 * check for the id of the template that should be used to set up the scan.
	 * 3. It will find the folder of the user logged into DAST Proxy. 4. It will
	 * then set up a scan based on the template. 5. It will upload the htd file
	 * into scan to configure the manual URLs and form fill elements.
	 */
	public void setUpScanForUser(final String userName, final String password,
			String pathOfConfigFile, String nameOfScan,
			final boolean startScanAutomatically, Scan scan) throws Exception {

		// This function does the build up work to set up a scan. Those tasks
		// are:
		// 1. The service will log into ASE based on the account the underlying
		// account of the server on which DAST Proxy is deployed on.
		// 2. It will then check for the id of the template that should be used
		// to set up the scan.
		// 3. It will find the folder of the user logged into DAST Proxy.
		//loginToDASTScanner(userName, password);

		setUpConnectionDetailsForLoggedInUser(userName, password);

		// This will finally set up the scan for the user and upload the htd
		// file into the scan.
		setUpNewScanAndConfigureScan(pathOfConfigFile,userName, nameOfScan, startScanAutomatically, scan);
		// logoutFromDASTScanner();

		//return scanSetUp;
	}

	public void setUpScanForUserFromService(final String userName,
			String pathOfConfigFile, String nameOfScan,
			final boolean startScanAutomatically, Scan scan) throws Exception {
		LOGGER.debug("Inside setUpScanForUserFromService");
		setUpConnectionDetailsForLoggedInUser(userName, "");
		LOGGER.debug("Inside setUpScanForUserFromService...1");
		setUpNewScanAndConfigureScan(pathOfConfigFile,userName, nameOfScan, startScanAutomatically, scan);
		LOGGER.debug("Inside setUpScanForUserFromService===========Done1");
	}

	public void loginToDASTScanner(final String userName, final String password)
			throws ParserConfigurationException, IOException, SAXException,
			DASTProxyException {

		LOGGER.debug("Inside AppScanEnterpriseRestService.loginToDASTScanner. Logged in user is: {}",userName);

		// Initialization of XPath utilities
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(_nsContext);


		/*
		 * NOTE: In case NTLM authentication doesn't work for ASE, the following
		 * is code for ASE.
		 */
		String credentialsToBePosted = "userid="+userName+"&password="+password;
		Document response;


		/**
		 * TODO This check is a temporary fix for ASE timing out error. If the
		 * first request is not honored because of timeout, and there is a
		 * corresponding exception then send a request again. Once ASE has fixed
		 * the issue we can remove this check.
		 *
		 */
		boolean secondRequestAlreadySent = false;
		try {
			LOGGER.debug("Sending first request to get user and version info. This will check if the user is logged in or not");
			response = sendRESTRequestToASE("", null);

		} catch (Exception exception) {

			exception.printStackTrace();
			LOGGER.error("There was an error when sending the first request to get user and version info. Assuming that the user is not logged in. The details of the error are are: ",exception);
			LOGGER.error("Going to log in");

			secondRequestAlreadySent = true;

			// Relogging in. So removing any cookie information.
			cookieContainer = "";

			/*
			 * try { response = sendRESTRequestToASE(
			 * AppScanConstants.APPSCAN_USERS_LOGIN, credentialsToBePosted);
			 * LOGGER.error(credentialsToBePosted); } catch (Exception
			 * exception2) { // Funny thing is that I am getting a CRWAE2999E
			 * error when I try to log in and I need to do the call again
			 * response = sendRESTRequestToASE(
			 * AppScanConstants.APPSCAN_USERS_LOGIN, credentialsToBePosted);
			 *
			 * }
			 */
			response = sendRESTRequestToASE(AppScanConstants.APPSCAN_USERS_LOGIN, credentialsToBePosted);

		}
		/**
		 * TODO This check is a temporary fix for ASE timing out error. If the
		 * first request is not honored because of timeout, send a request
		 * again. Once ASE has fixed the issue we can remove this check.
		 *
		 */
		LOGGER.error("response.getDocumentElement().getTagName()");

		if ("error".equalsIgnoreCase(response.getDocumentElement().getTagName())&& !secondRequestAlreadySent) {

			LOGGER.error("The first call to ASE has resulted in an error XML being sent back. Sending log in request again.");
			// Relogging in. So removing any cookie information.
			cookieContainer = "";
			response = sendRESTRequestToASE(AppScanConstants.APPSCAN_USERS_LOGIN, credentialsToBePosted);

			LOGGER.error("After receiving an ERROR XML, login request was sent and completed. Now checking if everything is fine by requesting the version information.");
			response = sendRESTRequestToASE("", null);

		}

		checkForError(response, null);

	}

	/**
	 *
	 * @param userName
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws XPathExpressionException
	 * @throws DASTProxyException
	 */
	private void requestForUserAndVersionInfo(final String userName,
			final String password) throws ParserConfigurationException,
			IOException, SAXException, XPathExpressionException,
			DASTProxyException {

		LOGGER.debug("Inside AppScanEnterpriseRestService.requestForUserAndVersionInfo. Logged in user is: ",userName);

		// Initialization of XPath utilities
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(_nsContext);

		Document response = sendRESTRequestToASE("", null);
		checkForError(response, null);

		// Write out the logged in user
		final String userNameOfAccountAccessingASE = "UserName: "
				+ (String) xpath.evaluate("//ase:version/user-name/text()",
						response);

		if (!userNameOfAccountAccessingASE.equalsIgnoreCase(userName)
				&& !userNameOfAccountAccessingASE.contains(userName)) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ASE is being accessed using a system account. Because logged in user id is: "+ userName+ ". However the account logged into ASE is: "+ userNameOfAccountAccessingASE);
			}
		}

		// Write out the Server version
		String versionNumber = "Version Number: "
				+ (String) xpath.evaluate("//ase:version/ase:build/text()",
						response);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("The version of ASE being accessed is: "+ versionNumber);
		}

	}

	private void requestForTemplateInfo() throws ParserConfigurationException,
			IOException, SAXException, DASTProxyException,
			XPathExpressionException {

		LOGGER.debug("Inside AppScanEnterpriseRestService.requestForTemplateInfo");
		// Initialization of XPath utilities
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(_nsContext);

		// Log in
		Document response = sendRESTRequestToASE(AppScanConstants.APPSCAN_TEMPLATE_LIST_RELATIVE_URL, "");
		checkForError(response, null);

		if (LOGGER.isDebugEnabled()) {LOGGER.debug("Checking for the template: "+ AppScanConstants.APPSCAN_DEFAULT_TEMPLATE_NAME);
		}

		// Search for desired template in the list of returned templates
		templateId = (String) xpath.evaluate("//ase:content-scan-job[ase:name='"+ AppScanConstants.APPSCAN_DEFAULT_TEMPLATE_NAME+ "']/ase:id/text()", response, XPathConstants.STRING);

		if (!AppScanUtils.isNotNull(templateId)) {
			LOGGER.error(" Error inside AppScanEnterpriseRestService.requestForTemplateInfo. Unable to find the default template: "+ AppScanConstants.APPSCAN_DEFAULT_TEMPLATE_NAME);
			throw new DASTProxyException(AppScanConstants.APPSCAN_ERROR_CODE_TEMPLATE_NOT_FOUND,"Template Id has returned as null. Unable to get the default template. Contact the administrator");
		}

		LOGGER.debug("Id for that template is: {}", templateId);

	}

	// get the scan Id, create the scan object and set the properties
	private void requestForScanJobInfo(Document response, String userName,
			String nameOfScan, Scan scan) throws ParserConfigurationException,
			IOException, SAXException, DASTProxyException,
			XPathExpressionException {

		LOGGER.debug("Inside AppScanEnterpriseRestService.requestForScanJobInfo");
		// Initialization of XPath utilities
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(_nsContext);

		checkForError(response, null);

		LOGGER.debug("Checking for the Scan Job Id. ");

		// Search for desired template in the list of returned templates
		final String scanId = (String) xpath.evaluate(
				"//ase:content-scan-job/ase:id/text()", response,
				XPathConstants.STRING);

		final String scanName = (String) xpath.evaluate(
				"//ase:content-scan-job/ase:name/text()", response,
				XPathConstants.STRING);

		final String reportId = (String) xpath.evaluate(
				"//ase:report-pack/ase:id/text()", response,
				XPathConstants.STRING);

		if (!AppScanUtils.isNotNull(scanId)) {
			LOGGER.error(" Error inside AppScanEnterpriseRestService.requestForScanIdInfo. Unable to find the default Scan Job ID");
			throw new DASTProxyException(AppScanConstants.APPSCAN_ERROR_CODE_SCAN_JOB_ID_NOT_FOUND,"ScanJob Id has returned as null. Contact the administrator");
		}

		LOGGER.debug("Id for that Scan Job is: {} ", scanId);

		scan.setScanId(scanId);
		scan.setScanName(scanName);
		scan.setUserForlderId(userFolderId);
		scan.setScanState(AppScanConstants.APPSCAN_JOB_READY_FOR_SCAN);
		if (AppScanUtils.isNotNull(nameOfScan)) {
			scan.setTestCaseName(nameOfScan);
		} else {
			scan.setTestCaseName(AppScanConstants.APPSCAN_TEST_CASE_NAME_MANUAL_SETUP);
		}
		if (scan.getReport()==null) scan.setReport(new Report());
		scan.getReport().setAseReportId(reportId);
		//return newScanObject;

	}

	private void setUpConnectionDetailsForLoggedInUser(String userName,
			final String password) throws XPathExpressionException,
			ParserConfigurationException, IOException, SAXException,
			DASTProxyException {
		// Check if login to ASE is possible.
		//requestForUserAndVersionInfo(userName, password);

		// Check if we can locate the default template for ASE.
		//requestForTemplateInfo();


		// Initialization of XPath utilities
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(_nsContext);

		final Document response = sendRESTRequestToASE(AppScanConstants.APPSCAN_USERS_FOLDER_LIST_RELATIVE_URL, "");

		checkForError(response, null);

		// So at this point, we have been able to:
		// 1. Successfully login to ASE
		// 2. Get the default template details
		// 3. Get the folders of all the users.
		// Now we have to check if our user has a folder in the list of user
		// folders

		if (AppScanUtils.isNotNull(userName)) {


			// Before that strip off 'CORP' from the user name in case it is
			// there
			if (userName.contains("CORP\\")) {
				userName = userName.substring(userName.indexOf("\\") + 1);
			}

			userFolderId = (String) xpath.evaluate("//ase:folder[ase:contact='"
					+ userName + "']/ase:id/text()", response,
					XPathConstants.STRING);
			//userFolderId  = "2097";
			if (AppScanUtils.isNotNull(userFolderId)) {
				String appScanAdminUserName =RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_ID_IDENTIFIER);
				userFolderId = (String) xpath.evaluate("//ase:folder[ase:contact='"
						+ appScanAdminUserName + "']/ase:id/text()", response,
						XPathConstants.STRING);
			}
			if (!AppScanUtils.isNotNull(userFolderId)) {

				LOGGER.error("Could not find user folder. Probably the user has never signed into ASE. Please sign into ASE for the first time.");
				throw new DASTProxyException(
						AppScanConstants.APPSCAN_ERROR_CODE_USER_FOLDER_NOT_FOUND,
						"Could not find user folder. Probably the user has never signed into ASE. Please sign into ASE for the first time.");
			}

		} else {

			LOGGER.error("User to set up the scan is null");
			throw new DASTProxyException(
					AppScanConstants.APPSCAN_ERROR_CODE_USER_NULL,
					"User to set up the scan in null");
		}

	}

	/**
	 * @return the userName
	 * @throws DASTProxyException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws UnsupportedEncodingException
	 * @throws XPathExpressionException
	 * @throws URISyntaxException
	 */
	private void setUpNewScanAndConfigureScan(final String pathOfConfigFile,
			final String userName, final String nameOfScan,
			final boolean startScanAutomatically, Scan scan) throws DASTProxyException,
			UnsupportedEncodingException, ParserConfigurationException,
			IOException, SAXException, XPathExpressionException,
			URISyntaxException {
		LOGGER.debug("In AppScanEnterpriseRestService.requestForScanSetUp function");
		ScanValidationResult isScanRecordingValid = ScanValidationResult.SUCCESS;//isScanRecordingValid(pathOfConfigFile);//

		if (isScanRecordingValid != ScanValidationResult.SUCCESS) {
			LOGGER.debug("Scan is not valid because of the external URLs, suspending the scan.");
			//String scanBatchURL = baseURL + "scan_batch_report?scanBatchId=" + scan.getBatch().getId();
			//sendScanSuspendedEmail(scan.getUser().getUserId(),scanBatchURL,scan.getScanName());
			scan.setScanState(AppScanConstants.APPSCAN_JOB_SCAN_STATE_SUSPENDED);
			if (isScanRecordingValid==ScanValidationResult.EXTERNAL_URLS)
				scan.setSuspendedReason(AppScanConstants.DAST_SCAN_SUSPENDED_REASON_EXTERNAL_URLS);
			else if (isScanRecordingValid==ScanValidationResult.EXTERNAL_URLS)
				scan.setSuspendedReason(AppScanConstants.DAST_SCAN_SUSPENDED_REASON_NO_URLS_IN_SCAN);
			scan.setToBeTracked(false);
			dastDAOImpl.saveGenericEntity(scan);
			return;
		}

		// ***************************************************************************
		// Initialization of XPath utilities
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(_nsContext);

		long currentTimeMillis = System.currentTimeMillis();
		String nameToBeSetForScan = new StringBuilder(AppScanConstants.APPSCAN_USERS_NEW_SCAN_NAME_PREFIX).toString() + " " +currentTimeMillis;
		String descToBeSetForScan = AppScanConstants.APPSCAN_USERS_NEW_SCAN_DESCRIPTION  + " " +currentTimeMillis;

		if (AppScanUtils.isNotNull(nameOfScan)) {

			nameToBeSetForScan = new StringBuilder("Scan for ").append(nameOfScan)
							.append(" test case set up at ").toString()
							.replaceFirst(":", "HH").replaceFirst(":", "MM")+ " " +currentTimeMillis;

			descToBeSetForScan = new StringBuilder("Content Scan set up for ").append(nameOfScan).append(" set up via DASTProxy").toString() + " " +currentTimeMillis;
		}
		// Log in
		if (userFolderId==null){
			userFolderId =RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_UID);
		}


		final Document response = sendRESTRequestToASE("folders/"
				+ userFolderId + "/folderitems?templateid=" + templateId,
				"name=" + nameToBeSetForScan + "&description="
						+ descToBeSetForScan);

		checkForError(response, null);
		LOGGER.debug("=========Scan has been successfully set up");

		// Pick out created scan & report URL for use later
		final String scanURL = (String) xpath
				.evaluate("//ase:content-scan-job/@href", response,
						XPathConstants.STRING);

		// Pick out created scan options url for later use
		final String optionsURL = (String) xpath.evaluate(
				"//ase:content-scan-job/ase:options/@href", response,
				XPathConstants.STRING);

		LOGGER.debug("Scan urls have been extracted");

		setUpStartingUrlForScan(optionsURL, pathOfConfigFile);

		LOGGER.debug("Starting url has been set");

		boolean retValue = uploadHarToScanConfig(scanURL, pathOfConfigFile);
		// Check for the scan Job ID
		requestForScanJobInfo(response, userName,nameOfScan, scan);

		// This will start the scan for the user
		if (startScanAutomatically) {
			try {
				startScanForUser(scanURL);

			} catch (Exception e){
				LOGGER.error(e);
			}
		}

		//scan.setScanState(AppScanConstants.APPSCAN_JOB_SCAN_STATE_RUNNING);

		//return scan;

	}

	// Email to be sent for a summary report of scan completed
	private void sendScanSuspendedEmail(final String userId, final String scanBatchURL, final String scanName) {

		// parameters used to build an email template for summary report
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("userId", userFolderId);
		model.put("scanbatchURL", scanBatchURL);
		model.put("scanName", scanName);
		String ccAddress  = RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_ERROR_CONTACT_DL_IDENTIFIER);

		MailUtils.sendEmail(userId + RootConfiguration.getProperties().getProperty(AppScanConstants.EMAIL_DOMAIN),
				ccAddress, AppScanConstants.APPSCAN_REPORT_SENDER,
				"DAST - Scan Suspended", null,
				AppScanConstants.SCAN_SUSPENDED_TEMPLATE);
	}

	/*
	 * TODO Rework this entire class. It is a result of a lot of patchwork.
	 */
	public void logoutFromDASTScanner() throws ParserConfigurationException,
			IOException, SAXException, DASTProxyException {

		LOGGER.debug("In AppScanEnterpriseRestService.logoutFromDASTScanner function.");
		LOGGER.debug("Sending logout request");
		// Log out

		final DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true);
		// final DocumentBuilder builder = domFactory.newDocumentBuilder();

		// Create the URL that we have to hit
		final URL url = new URL(AppScanConstants.APPSCAN_BASE_URL
				+ AppScanConstants.APPSCAN_USERS_LOGOUT);

		final HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		httpURLConnection.setDoInput(true);
		httpURLConnection.addRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");

		if (cookieContainer.length() > 0) {
			httpURLConnection.setRequestProperty("Cookie", cookieContainer);
		}

		try {
			httpURLConnection.getInputStream();
		} finally {
			httpURLConnection.disconnect();

		}

		LOGGER.debug("Looks like logout request is successfull.");
	}

	/**
	 * TODO: This was added to check if only external URLS are being scanned. It
	 * is a patchwork approach till we get sometime to rework the code.
	 *
	 * @param optionsUrl
	 * @param pathOfConfigFile
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private ScanValidationResult isScanRecordingValid(final String pathOfConfigFile)
			throws JsonParseException, JsonMappingException, IOException {

		/*
		 * BufferedReader reader = new BufferedReader(new FileReader(new File(
		 * pathOfConfigFile.replace(AppScanConstants.HTD_FILE_EXTENSION,
		 * AppScanConstants.HAR_FILE_EXTENSION)))); StringBuilder
		 * tempStringBuilder = new StringBuilder(); String line = null;
		 *
		 * while ((line = reader.readLine()) != null) {
		 * tempStringBuilder.append(line); } reader.close();
		 */

		ObjectMapper jacksonObjectMapper = new ObjectMapper();

		Har har = jacksonObjectMapper.readValue(
				new File(pathOfConfigFile.replace(
						AppScanConstants.HTD_FILE_EXTENSION,
						AppScanConstants.HAR_FILE_EXTENSION)), Har.class);
		if (har.getLog().getEntries().size() ==0 ){
			return ScanValidationResult.NO_URLS;
		}

		if (AppScanUtils.isNotNull(har) && AppScanUtils.isNotNull(har.getLog())
				&& AppScanUtils.isNotNull(har.getLog().getEntries())
				&& har.getLog().getEntries().size() > 0) {

			int urlEntryCounter = 0;
			while (urlEntryCounter < har.getLog().getEntries().size()) {
				String startingUrl = har.getLog().getEntries().get(urlEntryCounter).getRequest().getUrl();
				Inet4Address urlAddress = null;
				try{
					urlAddress = (Inet4Address) InetAddress.getByName(new URL(startingUrl).getHost());

				} catch (UnknownHostException unhe){
					//If the host is not reachable from DAST Proxy making the scan invalid.
					return ScanValidationResult.EXTERNAL_URLS;
				}

				if (urlAddress!= null && (urlAddress.isSiteLocalAddress() || isWhiteListedPublicIP(urlAddress.getHostAddress()))) {
					break;
				}
				urlEntryCounter = urlEntryCounter + 1;
			}

			if (urlEntryCounter == har.getLog().getEntries().size()) {
				return ScanValidationResult.EXTERNAL_URLS;
			} else {
				return ScanValidationResult.SUCCESS;
			}

		}

		return ScanValidationResult.SUCCESS;
	}

	boolean isWhiteListedPublicIP(String ipAddress){
		String whitelistedIPs = RootConfiguration.getProperties().getProperty(AppScanConstants.WHITE_LIST_PIBLIC_IPS);
		LOGGER.debug("-----------------whitelistedIPs="+whitelistedIPs);
		LOGGER.debug("-----------------ipAddress="+ipAddress);
		if (!AppScanUtils.isNotNull(whitelistedIPs) || ipAddress == null) return false;
		StringTokenizer st = new StringTokenizer(whitelistedIPs, ",");
		while(st.hasMoreTokens()){
			String ipWhiteListed = st.nextToken();
			if (ipWhiteListed.equals(ipAddress)){

				return true;
			}
		}


		return false;
	}


	private void setUpStartingUrlForScan(final String optionsUrl,
			final String pathOfConfigFile) throws IOException,
			ParserConfigurationException, SAXException, DASTProxyException,
			URISyntaxException {

		/*
		 * BufferedReader reader = new BufferedReader(new FileReader(new File(
		 * pathOfConfigFile.replace(AppScanConstants.HTD_FILE_EXTENSION,
		 * AppScanConstants.HAR_FILE_EXTENSION)))); StringBuilder
		 * tempStringBuilder = new StringBuilder(); String line = null;
		 *
		 * while ((line = reader.readLine()) != null) {
		 * tempStringBuilder.append(line); } reader.close();
		 */

		ObjectMapper jacksonObjectMapper = new ObjectMapper();

		Har har = jacksonObjectMapper.readValue(
				new File(pathOfConfigFile.replace(
						AppScanConstants.HTD_FILE_EXTENSION,
						AppScanConstants.HAR_FILE_EXTENSION)), Har.class);

		// String url = har.getLog().getEntries().get(3).getRequest().getUrl();

		if (AppScanUtils.isNotNull(har) && AppScanUtils.isNotNull(har.getLog())
				&& AppScanUtils.isNotNull(har.getLog().getEntries())
				&& har.getLog().getEntries().size() > 0) {

			int urlEntryCounter = 0;
			while (urlEntryCounter < har.getLog().getEntries().size()) {
				String startingUrl = har.getLog().getEntries()
						.get(urlEntryCounter).getRequest().getUrl();
				Inet4Address urlAddress = (Inet4Address) InetAddress
						.getByName(new URL(startingUrl).getHost());

				if (urlAddress.isSiteLocalAddress()) {

					if (AppScanUtils.isNotNull(startingUrl)) {
						// Set Up Starting URL

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Options URL along with post data is: "
									+ optionsUrl
									+ "/"
									+ AppScanConstants.APPSCAN_STARTING_URL_OPTION
									+ ":"
									+ "value="
									+ URLEncoder.encode(startingUrl, "UTF-8"));
						}
						String startingUrlServiceUrl = null;

						if (optionsUrl.endsWith("/")) {
							startingUrlServiceUrl = new StringBuilder(
									optionsUrl)
									.append(AppScanConstants.APPSCAN_STARTING_URL_OPTION)
									.toString();
						} else {
							startingUrlServiceUrl = new StringBuilder(
									optionsUrl)
									.append("/")
									.append(AppScanConstants.APPSCAN_STARTING_URL_OPTION)
									.toString();
						}

						final Document response = sendRESTRequestToASE(
								startingUrlServiceUrl,
								"value="
										+ URLEncoder.encode(startingUrl,
												"UTF-8"));
						checkForError(response, null);
					}
					break;
				}

				urlEntryCounter = urlEntryCounter + 1;
			}
		}
	}

	private void startScanForUser(final String scanUrl)
			throws ParserConfigurationException, IOException, SAXException,
			DASTProxyException {

		try {
			final Document response = sendRESTRequestToASE(scanUrl, "action=2");
			checkForError(response, null);

		} catch (IOException ioException) {
			ioException.printStackTrace();
			String appScanAdminUserName =RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_ID_IDENTIFIER);
			String appScanAdminPassword =RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_PWD_IDENTIFIER);
			loginToDASTScanner(appScanAdminUserName, appScanAdminPassword);
			final Document response = sendRESTRequestToASE(scanUrl, "action=2");
			checkForError(response, null);

		}
	}

	private static String getDomainName(final String url)
			throws URISyntaxException {
		URI uri = new URI(url);
		String domain = uri.getHost();
		return domain.startsWith("www.") ? domain.substring(4) : domain;
	}

	/**
	 * Check if user folder is present
	 *
	 * @return userId
	 * @param userName
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws DASTProxyException
	 * @throws XPathExpressionException
	 */
	public String checkIfUserPresent(String userName)
			throws UnsupportedEncodingException, ParserConfigurationException,
			IOException, SAXException, DASTProxyException,
			XPathExpressionException {
		LOGGER.debug("Inside checkIfUserPresent....1");
		// Initialization of XPath utilities
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(_nsContext);

		Document response = null;
		try {
			response = sendRESTRequestToASE(AppScanConstants.APPSCAN_USERS_FOLDER_LIST_RELATIVE_URL, "");
			LOGGER.debug("Inside checkIfUserPresent....2...response="+response);
		} catch (ConnectException ce){
			LOGGER.error("Could not find the user with userId="+userName+" in the Backend.");
			LOGGER.error(ce);
			return null;
		}
		LOGGER.debug("Check if user exists in the system. ");
		checkForError(response, null);
		LOGGER.debug("Inside checkIfUserPresent....2.1");


		// Before that strip off 'CORP' from the user name in case it is
		// there
		if (userName.contains("CORP\\")) {
			userName = userName.substring(userName.indexOf("\\") + 1);
		}
		LOGGER.debug("Inside checkIfUserPresent....3");

		String userId = (String) xpath.evaluate("//ase:folder[ase:contact='"
				+ userName + "']/ase:id/text()", response,
				XPathConstants.STRING);
		LOGGER.debug("Inside checkIfUserPresent....4...userId="+userId);

		/*
		 * XPathExpression expr = xpath
		 * .compile("//ase:folders/ase:folder[contains(ase:name,'" + userName +
		 * "')]/ase:id/text()");
		 *
		 * String userId = (String) expr.evaluate(response,
		 * XPathConstants.STRING);
		 */
		if (AppScanUtils.isNotNull(userId)) {
			return userId;
		} else {
			return null;
		}
	}

	/**
	 * Check if the scan is not present in the user folder
	 *
	 * @return
	 * @param userId
	 * @param scanId
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws DASTProxyException
	 * @throws XPathExpressionException
	 */
	public boolean checkIfScanIsNotPresentForUser(final String userId, final String scanId) throws UnsupportedEncodingException,
			ParserConfigurationException, IOException, SAXException,
			DASTProxyException, XPathExpressionException {
		LOGGER.debug("Inside checkIfScanIsNotPresentForUser...1");

		boolean retValue = true;
		try {
		// Initialization of XPath utilities
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		LOGGER.debug("Inside checkIfScanIsNotPresentForUser...2");

		xpath.setNamespaceContext(_nsContext);

		Document response = sendRESTRequestToASE(
				AppScanConstants.APPSCAN_BASE_URL + "folders/" + userId
						+ "/folderitems", "");

		LOGGER.debug("Inside checkIfScanIsNotPresentForUser...3...response...="+response);
		LOGGER.debug("Check if scan exists in the system. ");
		checkForError(response, null);
		final XPathExpression expr = xpath
				.compile("//ase:folder-items/ase:content-scan-job[contains(ase:id,'"
						+ scanId + "')]/ase:id/text()");
		LOGGER.debug("Inside checkIfScanIsNotPresentForUser...4....expr="+expr);

		final String id = (String) expr.evaluate(response,
				XPathConstants.STRING);
		LOGGER.debug("Inside checkIfScanIsNotPresentForUser...5....id="+id);

		if (id != null && !id.isEmpty()) {
			LOGGER.debug("Inside checkIfScanIsNotPresentForUser...6");
			retValue = false;
		}
		} catch(ConnectException ce){
			LOGGER.error("Failed in checking if the scan is present for user...userId="+userId+" scanId="+scanId);
			LOGGER.error(ce);
		}catch(DASTProxyException de){
			LOGGER.error("Failed in checking if the scan is present for user DAST Proxy Exception...userId="+userId+" scanId="+scanId);
			LOGGER.error(de);
		}

		return retValue;
	}

	/**
	 * This method check the status of scan
	 *
	 * @return state
	 * @param scanId
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws DASTProxyException
	 * @throws XPathExpressionException
	 */
	public String checkForScanStarted(final String scanId)
			throws ParserConfigurationException, IOException, SAXException,
			DASTProxyException, XPathExpressionException {

		String stateId;
		String state;

		LOGGER.debug("Inside AppScanEnterpriseRestService.requestForScanJobInfo");
		// Initialization of XPath utilities
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(_nsContext);

		final Document response = sendRESTRequestToASE(AppScanConstants.APPSCAN_SERVICE_FOR_FOLDERITEM + scanId, "");
		checkForError(response, null);

		LOGGER.debug("Checking for the Scan Job Status. ");

		// Check for the action being performed for the scan
		stateId = (String) xpath.evaluate(
				"//ase:content-scan-job/ase:state/ase:id/text()", response,
				XPathConstants.STRING);
		state = (String) xpath.evaluate(
				"//ase:content-scan-job/ase:state/ase:name/text()", response,
				XPathConstants.STRING);

		if (!AppScanUtils.isNotNull(stateId)) {
			LOGGER.error(" Error inside AppScanEnterpriseRestService. checkForScanStarted.");
			throw new DASTProxyException(
					AppScanConstants.APPSCAN_ERROR_CODE_SCAN_JOB_ID_NOT_FOUND,
					"Scan Status for Job Id has returned as null. Contact the administrator");
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Scan Action ID: " + stateId);
			LOGGER.debug("Scan Action: " + state);
		}

		return state;
	}

	/**
	 *
	 * Get the last run of the scan
	 *
	 * @return latestRunScan
	 * @param scanId
	 * @throws UnsupportedEncodingException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws DASTProxyException
	 * @throws XPathExpressionException
	 */
	public String getLatestRunForScan(String scanId)
			throws UnsupportedEncodingException, ParserConfigurationException,
			IOException, SAXException, DASTProxyException,
			XPathExpressionException {

		// Initialization of XPath utilities
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(_nsContext);

		final Document response = sendRESTRequestToASE(AppScanConstants.APPSCAN_SERVICE_FOR_FOLDERITEM + scanId, "");

		LOGGER.debug("Checking for the last run for the Scan. ");

		// last run of the scan
		String latestRunScan = (String) xpath.evaluate(
				"//ase:content-scan-job/ase:last-run/text()", response,
				XPathConstants.STRING);
		checkForError(response, null);

		if (!AppScanUtils.isNotNull(scanId)) {
			LOGGER.error(" Error inside AppScanEnterpriseRestService.requestForScanIdInfo. "
					+ "Unable to find the default last run details for Scan Job ID");
			throw new DASTProxyException(
					AppScanConstants.APPSCAN_ERROR_CODE_LAST_RUN_FOR_SCAN_JOB_ID_NOT_FOUND,
					"Last run for ScanJob Id has returned as null. Contact the administrator");
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Scan Job is: " + scanId);
		}

		return latestRunScan;
	}

	public String getLatestRunForScanReport(String reportId)
			throws UnsupportedEncodingException, ParserConfigurationException,
			IOException, SAXException, DASTProxyException,
			XPathExpressionException {

		// Initialization of XPath utilities
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(_nsContext);

		final Document response = sendRESTRequestToASE(AppScanConstants.APPSCAN_SERVICE_FOR_FOLDERITEM + reportId, "");
		LOGGER.debug("Inside getLatestRunForScanReport...response.toString()="+response.toString());
		checkForError(response, null);

		// last run of the scan report
		String latestRunScanReport = (String) xpath.evaluate("//ase:report-pack/ase:last-run/text()", response,XPathConstants.STRING);

		if (!AppScanUtils.isNotNull(reportId)) {
			LOGGER.error(" Error inside AppScanEnterpriseRestService.requestForScanIdInfo. "
					+ "Unable to find the default last run details for Report");
			throw new DASTProxyException(
					AppScanConstants.APPSCAN_ERROR_CODE_LAST_RUN_FOR_SCAN_JOB_ID_NOT_FOUND,
					"Last run for Report job Id has returned as null. Contact the administrator");
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Report id is : " + reportId);
		}

		return latestRunScanReport;
	}

	/**
	 * Currently this method gets only OWASP Report
	 *
	 * @return issueLink
	 * @param scanId
	 * @throws UnsupportedEncodingException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws DASTProxyException
	 * @throws XPathExpressionException
	 */

	/*
	 * @author Kiran Shirali (kshirali@ebay.com)
	 *
	 * Discovered that this function is incorrectly named. It does not return a
	 * Report Object. Instead it returns a link to it. Not sure why the original
	 * author decided to not adhere to OOP principles.
	 *
	 * TODO: Have to rework this piece of code. (non-Javadoc)
	 *
	 * @see com.dastproxy.services.DASTApiService#getReport(java.lang.String)
	 */
	public String getReport(final String reportId)
			throws UnsupportedEncodingException, ParserConfigurationException,
			IOException, SAXException, DASTProxyException,
			XPathExpressionException {

		// Initialization of XPath utilities
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(_nsContext);
		final Document response = sendRESTRequestToASE(
				AppScanConstants.APPSCAN_SERVICE_FOR_FOLDERITEM + reportId
						+ "/reports", "");
		LOGGER.debug("Get the issue link for the scan. ");
		checkForError(response, null);
		final XPathExpression xPathExpression = xpath
				.compile("//ase:reports/ase:report[contains(ase:name,'"
						+ AppScanConstants.APPSCAN_REPORT_TYPE
						+ "')]/ase:issues/@href");

		final String issueLink = (String) xPathExpression.evaluate(response,
				XPathConstants.STRING);

		/*
		 * if (!AppScanUtils.isNotNull(issueLink)) {
		 * LOGGER.error(" Error inside AppScanEnterpriseRestService.getReport."
		 * + "Unable to find issue link."); throw new DASTProxyException(
		 * AppScanConstants.APPSCAN_ERROR_ISSUE_LINK_NOT_FOUND,
		 * "Issue Link has returned null. Contact the administrator"); }
		 */
		return issueLink;
	}

	/**
	 * @return issues
	 * @param issueURL
	 * @throws UnsupportedEncodingException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws DASTProxyException
	 * @throws XPathExpressionException
	 */
	public List<Issue> getIssuesFromReport(final String scanId,
			final Report report, final String issueURL)
			throws UnsupportedEncodingException, ParserConfigurationException,
			IOException, SAXException, DASTProxyException,
			XPathExpressionException {

		// Initialization of XPath utilities
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(_nsContext);

		final Document response = sendRESTRequestToASE(issueURL, "");
		LOGGER.debug("Get the issues...scanId="+scanId);
		LOGGER.debug("Get the issues...reportId="+report.getId());
		LOGGER.debug("Get the issues");
		checkForError(response, null);
		final XPathExpression xPathExpression = xpath
				.compile(new StringBuilder(
						"//ase:issues/ase:security-issue/@weblink | ")
						.append("//ase:issues/ase:security-issue/ase:id/text() | ")
						.append("//ase:issues/ase:security-issue/ase:severity/ase:name/text() |")
						.append("//ase:issues/ase:security-issue/ase:issue-type/text() | ")
						.append("//ase:issues/ase:security-issue/ase:test-url/text()")
						/*
						 * .append(
						 * "//ase:issues/ase:security-issue/ase:issue-details/ase:variants/text()"
						 * )
						 */
						.toString());
		final NodeList nodes = (NodeList) xPathExpression.evaluate(response, XPathConstants.NODESET);

		// List of issues for a scan
		List<Issue> listOfIssues = new LinkedList<Issue>();
		List<FpReason> fpReasons = dastDAOImpl.getFpReasonWithPattern();

		// create an issue object and set the properties
		for (int i = 0; i < nodes.getLength(); i = i + 5) {
			final Issue issue = new Issue();
			issue.setIssueUrl(nodes.item(i).getNodeValue());
			issue.setNativeIssueId(nodes.item(i + 1).getNodeValue());
			issue.setReport(report);
			issue.setSeverity(nodes.item(i + 2).getNodeValue());
			issue.setIssueType(nodes.item(i + 3).getNodeValue());
			issue.setTestUrl(nodes.item(i + 4).getNodeValue());


			listOfIssues.add(issue);
		}

		for (Issue issue : listOfIssues) {
			final XPathExpression xPathExpression2 = xpath
					.compile(new StringBuilder(
							"//ase:issues/ase:security-issue[contains(ase:id,'"
									+ issue.getNativeIssueId()
									+ "')]/ase:issue-details/ase:variants/ase:variant/ase:id/text() |")
							.append("//ase:issues/ase:security-issue[contains(ase:id,'"
									+ issue.getNativeIssueId()
									+ "')]/ase:issue-details/ase:variants/ase:variant/ase:traffic/ase:test-http-traffic/text() |")
							.append("//ase:issues/ase:security-issue[contains(ase:id,'"
									+ issue.getNativeIssueId()
									+ "')]/ase:issue-details/ase:variants/ase:variant/ase:traffic/ase:original-http-traffic/text()")
							.toString());
			NodeList nodesTrafficList = (NodeList) xPathExpression2.evaluate(
					response, XPathConstants.NODESET);

			for (int i = 0; i < nodesTrafficList.getLength(); i = i + 3) {
				issue.setTestHttpTraffic(nodesTrafficList.item(i + 1).getNodeValue());
				issue.setOriginalHttpTraffic(nodesTrafficList.item(i + 2).getNodeValue());
				issue.setScanEngine("ASE");

				for (FpReason reason : fpReasons){
					if ((issue.getSeverity().equals("High") || issue.getSeverity().equals("Medium")) && issue.getTestHttpTraffic().toLowerCase().indexOf(reason.getFpPattern().toLowerCase()) != -1){
						issue.setFp(true);
						issue.setFpReasonId(reason.getId());
						issue.setFpMarkedBy("SYSTEM");
						issue.setFpMarkedDate(new Date());
						issue.setFpComments(reason.getName());
					}
				}
			}
		}

		return listOfIssues;
	}

	// get details of a vulnerability
	public Issue getOneIssue(List<Issue> listOfIssues, String searchForIssue) {
		ListIterator<Issue> iterator = listOfIssues.listIterator();
		Issue issue;
		do {
			issue = iterator.next();
			if (issue.getNativeIssueId().equals(searchForIssue)) {
				return issue;
			}
		} while (iterator.hasNext());
		return null;
	}

	/**
	 * This is a generic method to function a request for AppScan and is user
	 * @param relativeURL
	 * @param postData
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private Document sendRESTRequestToASE(final String relativeURL,
			final String postData) throws ParserConfigurationException,
			IOException, SAXException {

		// Boiler plate code
		final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		final DocumentBuilder builder = domFactory.newDocumentBuilder();

		// Create the URL that we have to hit
		URL url = null;
		if (!relativeURL.contains(RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_APP_SCAN_SERVER_NAME_IDENTIFIER))) {
			url = new URL(AppScanConstants.APPSCAN_BASE_URL + relativeURL);
		} else {
			url = new URL(relativeURL);
		}

		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setDoInput(true);
		httpURLConnection.addRequestProperty(HTTP.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

		if (cookieContainer.length() > 0) {
			httpURLConnection.setRequestProperty("Cookie", cookieContainer);
		}

		// Check if this connection has data to send as A POST.
		// If so then we need to set this request as a POST
		if (AppScanUtils.isNotNull(postData)) {
			OutputStreamWriter outputStreamWriter = null;
			try {
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				final OutputStream outputStream = httpURLConnection.getOutputStream();
				outputStreamWriter = new OutputStreamWriter(outputStream);
				outputStreamWriter.write(postData);
				outputStreamWriter.flush();
			} finally {
				if (outputStreamWriter != null) {
					outputStreamWriter.close();
				}
			}
		}

		try {
			//if (httpURLConnection.getResponseCode() >= 400) throw new DASTProxyException(httpURLConnection.getResponseCode(), httpURLConnection.getResponseMessage());
			return builder.parse(new InputSource(httpURLConnection.getInputStream()));
		} catch (SAXException saxException) {

			LOGGER.error(saxException);
			//return builder.parse(new InputSource(httpURLConnection.getErrorStream()));
		} catch (IOException ioException) {
			ioException.printStackTrace();
			if (httpURLConnection.getResponseCode() ==401) {
				throw ioException;
			}
			return builder.parse(new InputSource(httpURLConnection.getErrorStream()));
		} finally {
			// Update cookies
			Map<String, List<String>> responseHeaders = httpURLConnection.getHeaderFields();

			List<String> cookies = responseHeaders.get("Set-Cookie");

			if (cookies != null && cookies.size() > 0) {
				for (String cookie : cookies) {
					LOGGER.debug("Inside sendRESTRequestToASE..cookie="+cookie);


					if (cookieContainer.length() > 0)
						cookieContainer += ", ";

					cookieContainer += cookie;
				}
			}
			httpURLConnection.disconnect();
			LOGGER.debug("Inside sendRESTRequestToASE..cookie="+cookieContainer);

		}
		return null;
	}


	private boolean uploadHarToScanConfig(final String scanUrl,
			final String pathOfConfigFile) throws ParserConfigurationException,
			IOException {
		boolean retValue = true;

		// Create the URL that we have to hit
		final URL url = new URL(scanUrl+ "/httptrafficdata?includeformfills=true");
		final HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		httpURLConnection.setDoInput(true);
		httpURLConnection.addRequestProperty("Content-Type",
				"application/octet-stream");

		httpURLConnection.setRequestMethod("POST");
		if (cookieContainer.length() > 0) {
			httpURLConnection.setRequestProperty("Cookie", cookieContainer);
		}

		httpURLConnection.setDoOutput(true);
		OutputStream outputStream = httpURLConnection.getOutputStream();

		final InputStream inputStream1 = new FileInputStream(new File(
				pathOfConfigFile));
		LOGGER.debug("File Being Uploaded is: {}", pathOfConfigFile);

		byte[] bytes = IOUtils.toByteArray(inputStream1);
		outputStream.write(bytes);
		outputStream.flush();

		if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			retValue = false;
			byte[] data = new byte[httpURLConnection.getErrorStream().available()];
			httpURLConnection.getErrorStream().read(data);
			LOGGER.debug("Error if any is: {}",new String (data));
		}
		// LOGGER.debug("Response Code:" +
		// httpURLConnection.getResponseCode());

		httpURLConnection.disconnect();
		return retValue;
	}

	/**
	 * Checks for an error in the response
	 *
	 * @param doc
	 * @param expectedCodes
	 *            a list of CRW** codes that are accepted
	 * @throws DASTProxyException
	 * @throws Exception
	 */
	private void checkForError(final Document document,
			List<String> expectedCodes) throws DASTProxyException {

		LOGGER.debug("Inside AppScanEnterpriseRestService.checkForError function.");

		final Element rootElement = document.getDocumentElement();

		LOGGER.debug("Inside AppScanEnterpriseRestService.checkForError function...rootElement="+rootElement);

		if ("error".equalsIgnoreCase(rootElement.getTagName())) {

			// if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Error has occured on request to ASE");
			LOGGER.debug("Returned Error Codes are:");


			final NodeList nodes = rootElement.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				final Node node = nodes.item(i);
				final String nodeName = node.getLocalName();

				if ("code".equalsIgnoreCase(nodeName)) {
					final String code = node.getChildNodes().item(0).getNodeValue();
					LOGGER.debug(code);
					if (expectedCodes != null && expectedCodes.contains(code)) {
						LOGGER.debug("Expected error code found. Returning to normal execution");
						return;
					}
				} else if ("message".equalsIgnoreCase(nodeName)) {
					LOGGER.debug(node.getChildNodes().item(0).getNodeValue());
				} else if ("help".equalsIgnoreCase(nodeName)) {
					LOGGER.debug(node.getAttributes().item(0).getNodeValue());
				}
				// }
			}
			LOGGER.debug("Inside AppScanEnterpriseRestService.checkForError function...exception..."+document.toString());
			throw new DASTProxyException("Unexpected error on return of response from ASE",AppScanConstants.DAST_SCAN_SUSPENDED_REASON_NO_ASE_FOLDER_CODE);
		}
		LOGGER.debug("Inside AppScanEnterpriseRestService.checkForError function...no error");
	}

	public DastDAOImpl getDastDAOImpl() {
		return dastDAOImpl;
	}

	public void setDastDAOImpl(DastDAOImpl dastDAOImpl) {
		this.dastDAOImpl = dastDAOImpl;
	}



}