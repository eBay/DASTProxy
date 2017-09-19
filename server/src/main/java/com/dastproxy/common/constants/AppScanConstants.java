/**
 * This class holds all the constants required for the application. If there are
 * any changes that has to be done, it would be here.
 *
 * @author Kiran Shirali (kshirali@ebay.com)
 *
 */
package com.dastproxy.common.constants;

import java.io.File;
import com.dastproxy.configuration.RootConfiguration;

public class AppScanConstants {

	/* ****************************** GlOBAL PROPERTIES IDENTIFIERS ************************************************/
	public static final String PROPERTIES_OPERATING_ENVIRONMENT_IDENTIFIER = "currenEnvironment";
	public static final String PROPERTIES_OPERATING_ENVIRONMENT_BASE_URL_IDENTIFIER = "currenEnvironmentBaseUrl";
	public static final String PROPERTIES_APP_SCAN_BASE_URL_IDENTIFIER = "appScanBaseUrl";
	public static final String PROPERTIES_APP_SCAN_BASE_SERVICES_URL_IDENTIFIER = "appScanServicesBaseUrl";
	public static final String PROPERTIES_APP_SCAN_SERVER_NAME_IDENTIFIER = "appScanServerName";
	public static final String PROPERTIES_ERROR_CONTACT_DL_IDENTIFIER= "errorMessageContactDL";
	public static final String PROPERTIES_CONTACT_US_SUPPORT_DL_IDENTIFIER= "contactUsSupportDL";
	public static final String PROPERTIES_DO_NOT_REPLY_EMAIL_ID_IDENTIFIER= "doNotReplyEmailId";
	public static final String PROPERTIES_APPSCAN_SERVICE_ACCOUNT_ID_IDENTIFIER= "appScanServiceAccountId";
	public static final String PROPERTIES_APPSCAN_SERVICE_ACCOUNT_PWD_IDENTIFIER= "appScanServiceAccountPwd";
	public static final String PROPERTIES_APPSCAN_SERVICE_ACCOUNT_UID= "AppScanServiceAccountUid";

	public static final String PROPERTIES_RUN_CRON_JOBS = "runCronJobs";
	public static final String PROPERTIES_JIRA_BASE_URL = "jiraBaseUrl";
	public static final String PROPERTIES_JIRA_SERVICE_ACCOUNT_USERNAME = "jiraServiceAccountUsername";
	public static final String PROPERTIES_JIRA_SERVICE_ACCOUNT_PASSWORD = "jiraServiceAccountPassword";
	public static final String PROPERTIES_SMTP_SERVER = "smtpServerUrl";
	public static final String PROPERTIES_ORG_HTTP_PROXY = "orgHttpProxy";
	public static final String PROPERTIES_DAST_SCANNER_SCANNING_TEMPLATE_NAME_OR_ID = "dastScannerScanningTemplateIdOrName";
	public static final String EMAIL_DOMAIN = "emailDomain";
	public static final String REPORT_ISSUES_EMAIL_CC_ADDRESS_IF_ISSUES = "reportIssuesEmailCCAddressIfIssues";
	
	public static final String REPORT_EMAIL_CC_ADDRESS_IF_ISSUES = RootConfiguration.getProperties().getProperty(REPORT_ISSUES_EMAIL_CC_ADDRESS_IF_ISSUES);
	public static final String WHITE_LIST_PIBLIC_IPS = "whitelist_public_ips";
	public static final String ADMIN_GROUPS_LIST = "admin_groups_list";
	public static final String ADMIN_LOGIN_LIST = "admin_login_list";


	// During proxy registration at a certain proxy port, this is the number of times
	// the application will try again.
	public static final int PROXY_CREATION_MAX_RETRY_COUNT = 25;

	public static final String HTD_CONVERTOR_APP_PATH = "C:" + File.separator +"HARtoHTDConvertor" + File.separator +"HtdConvertor.exe";
	public static final String USER_HTD_FILES_LOCATION = "C:" + File.separator +"HARtoHTDConvertor" + File.separator +"Users";
	public static final String HAR_FILE_EXTENSION = ".har";
	public static final String HTD_FILE_EXTENSION = ".htd";

	public static final String DATE_FORMAT = "dd-MM-yyyy_hh.mm.ss";

	// Mail Utilities
	public static final String SMTP_SERVER = RootConfiguration.getProperties().getProperty(PROPERTIES_SMTP_SERVER);
	public static final String APPSCAN_STATUS_FOR_SCAN = "Status of scan";
	public static final String APPSCAN_REPORT_FOR_SCAN = "Summary Report for scan";
	public static final String APPSCAN_REPORT_FOR_SCAN_BATCH = "Summary Report for scan batch";

	/* ************************************** LOG4j - Logging Messages*********************************************/

	// ERROR MESSAGES
	public static final String ERROR_MSG_ERROR_OCCURRED_IN_SIMPLE_SEND_MAIL = "Error has occured in the simple MailUtils.sendMail function. The error is: ";
	public static final String ERROR_MSG_ERROR_OCCURRED_IN_GET_IP_ADDRESS = "Error in AppScanUtils.getIpAddress function. Error while trying to get all IP addresses of the system. The error is: ";

	// DEBUG MESSAGES

	public static final String DEBUG_MSG_PROXY_STOPPED = "The proxy server has been stopped and the port has been freed up";
	public static final String DEBUG_MSG__IN_FUNC_STOP_SERVER_RETURN_HAR = "In the function stopServerAndReturnHar";
	public static final String DEBUG_MSG_IN_SIMPLE_SEND_MAIL_FUNCTION = "Inside MailUtils.sendMail function (only to and from address)";
	public static final String DEBUG_MSG_MAIL_TO_ADDRESS_SET = "The to address to send mail to is: ";
	public static final String DEBUG_MSG_MAIL_FROM_ADDRESS_SET = "The from address to send mail is: ";
	public static final String DEBUG_MSG_MAIL_HAS_BEEN_SENT = "The mail has been set";

	// ERROR MESSAGES SENT TO USER
	public static final String ERROR_MESSAGE_TO_USER_ONLY_EXTERNAL_URLS_NOT_ACCEPTED = "The test case has only external URLS which cannot be scanned as per our policy. The submitted scan has been rejected.";
	/* ************************************** End of Logging Messages*********************************************/

	/* ***************************  APPSCAN RELATED CONSTANTS ****************************************************/

	public static final String APPSCAN_BASE_URL = RootConfiguration.getProperties().getProperty(PROPERTIES_APP_SCAN_BASE_SERVICES_URL_IDENTIFIER);
	public static final String APPSCAN_DEFAULT_TEMPLATE_NAME = RootConfiguration.getProperties().getProperty(PROPERTIES_DAST_SCANNER_SCANNING_TEMPLATE_NAME_OR_ID);
	public static final String APPSCAN_TEMPLATE_LIST_RELATIVE_URL = "templates";
	public static final String APPSCAN_STARTING_URL_OPTION = "epcsCOTListOfStartingUrls";
	public static final String APPSCAN_USERS_LOGIN = "login";
	public static final String APPSCAN_USERS_FOLDER_LIST_RELATIVE_URL = "folders/3/folders";
	public static final String APPSCAN_USERS_LOGOUT = "logout";

	public static final String APPSCAN_USERS_NEW_SCAN_NAME_PREFIX = "Content Scan created via DAST Proxy ";
	public static final String APPSCAN_USERS_NEW_SCAN_DESCRIPTION = "Scan Created via DAST Proxy";

	public static final String APPSCAN_ERROR_CODE_TEMPLATE_NOT_FOUND = "TEMPLATENOTFOUND";
	public static final String APPSCAN_ERROR_CODE_USER_FOLDER_NOT_FOUND = "USERFOLDERNOTFOUND";
	public static final String APPSCAN_ERROR_CODE_USER_NULL = "LOGGEDINUSERNULL";
	public static final String APPSCAN_ERROR_CODE_SCAN_JOB_ID_NOT_FOUND = "SCANJOBIDNOTFOUND";
	public static final String APPSCAN_ERROR_CODE_LAST_RUN_FOR_SCAN_JOB_ID_NOT_FOUND = "LASTRUNNOTFOUND";
	public static final String APPSCAN_ERROR_ISSUE_LINK_NOT_FOUND = "ISSUELINKNOTFOUND";
	public static final String APPSSCAN_ERROR_LIST_OF_ISSUES_NOT_FOUND = "LISTOFISSUESNOTFOUND";
	public static final String APPSCAN_ERROR_ISSUE_NOT_FOUND = "ISSUENOTFOUND";
	public static final String APPSCAN_SCAN_TO_BE_TRACKED = "toBeTracked";
	public static final String APPSCAN_AUTOMATIC_START_SCAN = "START_SCAN_AUTOMATIC";
	public static final String APPSCAN_TEST_CASE_NAME_MANUAL_SETUP = "Manual Test";

	//Email
	public static final String APPSCAN_JOB_SCAN_STATE_NEW="New";
	public static final String APPSCAN_JOB_SCAN_STATE_RUNNING="Running";
	public static final String APPSCAN_JOB_SCAN_STATE_READY = "Ready";
	public static final String APPSCAN_JOB_SCAN_STATE_COMPLETED="Post Processing";
	public static final String APPSCAN_JOB_SCAN_STATE_SUSPENDED="Suspended";
	public static final String DAST_SCAN_SUSPENDED_REASON_EXTERNAL_URLS="Suspended - The scan has external URLs.";
	public static final String DAST_SCAN_SUSPENDED_REASON_BACKEND_ISSUE="Suspended - The scan failed because of a backend issue. Please re-submit.";
	public static final String DAST_SCAN_SUSPENDED_REASON_NO_ASE_FOLDER="Suspended - AppScan Folder does not exist. Please login to go/ase once and re-submit scan.";
	public static final String DAST_SCAN_SUSPENDED_REASON_NO_URLS_IN_SCAN="Suspended - No URLs in the scan.";
	public static final String DAST_SCAN_SUSPENDED_REASON_NO_ASE_FOLDER_CODE="NO_ASE_USER_FOLDER";
	public static final String APPSCAN_JOB_SCAN_STATUS_URL = RootConfiguration.getProperties().getProperty(PROPERTIES_APP_SCAN_BASE_URL_IDENTIFIER) +"Jobs/JobStatistics.aspx?";
	public static final String APPSCAN_SERVICE_FOR_FOLDERITEM ="folderitems/";
	public static final String APPSCAN_JOB_READY_FOR_SCAN="New";
	public static final String APPSCAN_REPORT_SENDER = RootConfiguration.getProperties().getProperty(PROPERTIES_DO_NOT_REPLY_EMAIL_ID_IDENTIFIER);

	public static final String APPSCAN_CONTACT_US_SUPPORT_DL=RootConfiguration.getProperties().getProperty(PROPERTIES_CONTACT_US_SUPPORT_DL_IDENTIFIER);
	public static final String APPSCAN_REPORT_TYPE = "OWASP";

	public static final String APPSCAN_MANUAL_TEST_SUITE = "Manual Test Suite";

	/* ***************************  END OF APPSCAN RELATED CONSTANTS ***********************************************/
	/* ***************************  MAILING TEMPLATES *************************************************************/
	public static final String EXCEPTION_MAIL_BODY_TEMPLATE = "errorMailTemplate.vm";
	public static final String REPORT_MAIL_BODY_TEMPLATE = "scanReportEmail.vm";
	public static final String REPORT_SCAN_BATCH_MAIL_BODY_TEMPLATE = "scanBatchReportEmail.vm";
	public static final String SCAN_SUSPENDED_TEMPLATE = "scanSuspendedEmail.vm";
	public static final String REPORT_MAIL_META_REFERRER_BODY_TEMPLATE = "scanReportMetaReferrer.vm";
	public static final String STATUS_MAIL_BODY_TEMPLATE = "scanStatusLinkEmail.vm";
	public static final String SCAN_SETUP_MAIL_BODY_TEMPLATE = "scanSuccessfullySetUp.vm";
	public static final String SCAN_REJECTED_MAIL_BODY_TEMPLATE = "scanRejected.vm";
	public static final String JIRA_ISSUE_RAISED_TEMPLATE = "jiraIssueRaised.vm";

	public static final String EXCEPTION_MAIL_TEMPLATE_ENV_DENOTION = "currentEnvironment";
	public static final String EXCEPTION_MAIL_TEMPLATE_EXCEPTION_CLASS_DENOTION = "exceptionClass";
	public static final String EXCEPTION_MAIL_TEMPLATE_EXCEPTION_MESSAGE_DENOTION = "exceptionMessage";
	public static final String EXCEPTION_MAIL_TEMPLATE_EXCEPTION_STACKTRACE_DENOTION = "exceptionStackTrace";

	public static final String EXCEPTION_MAIL_SUBJECT = "Exception in DAST Proxy";
	public static final String JIRA_ISSUE_RAISED_SUBJECT = "JIRA issue has been successfully raised";

	/* ***************************  END OF MAILING TEMPLATES ******************************************************/

	/* ***************************  JNDI Constants ****************************************************************/

	public static final String JNDI_ENVIRONMENT_IDENTIFIER = "prop/CurrentEnvironment";
	public static final String JNDI_ENVIRONMENT_BASE_URL_IDENTIFIER = "prop/CurrentEnvironmentBaseUrl";
	public static final String JNDI_APPSCAN_BASE_URL_IDENTIFIER = "prop/AppScanBaseUrl";
	public static final String JNDI_APPSCAN_SERVICES_BASE_URL_IDENTIFIER = "prop/AppScanServicesBaseUrl";
	public static final String JNDI_APPSCAN_SERVER_NAME_IDENTIFIER = "prop/AppScanServerName";
	public static final String JNDI_ERROR_MESSAGE_CONTACT_DL_IDENTIFIER = "prop/ErrorMessageContactDL";
	public static final String JNDI_CONTACT_US_SUPPORT_DL_IDENTIFIER = "prop/ContactUsSupportDL";
	public static final String JNDI_DO_NOT_REPLY_EMAIL_ID_IDENTIFIER = "prop/DoNotReplyEmailId";
	public static final String JNDI_DO_SERVICE_ACCOUNT_ID_IDENTIFIER = "prop/AppScanServiceAccountId";
	public static final String JNDI_DO_SERVICE_ACCOUNT_PWD_IDENTIFIER = "prop/AppScanServiceAccountPwd";
	public static final String JNDI_DO_SERVICE_ACCOUNT_UID = "prop/AppScanServiceAccountUid";

	public static final String JNDI_DO_RUN_CRONJOB_SWITCH = "prop/RunCronJobs";
	public static final String JNDI_JIRA_BASE_URL = "prop/JIRABaseUrl";
	public static final String JNDI_JIRA_SERVICE_ACCOUNT_USERNAME = "prop/JIRAServiceAccountUsername";
	public static final String JNDI_JIRA_SERVICE_ACCOUNT_PASSWORD = "prop/JIRAServiceAccountPassword";
	public static final String JNDI_SMTP_SERVER = "prop/smtpServerDomain";
	public static final String JNDI_ORG_HTTP_PROXY = "prop/orgHttpProxy";
	public static final String JNDI_DAST_SCANNER_SCANNING_TEMPLATE_NAME_OR_ID = "prop/dastScannerScanningTemplateIdOrName";
	public static final String JNDI_EMAIL_DOMAIN = "prop/emailDomain";
	public static final String JNDI_WHITELIST_PUBLIC_IPS = "prop/whiteListPublicIPs";


	/* ***************************  End of JNDI Constants  ********************************************************/

	public static final String META_REFERRER_CHECK_ON = "metaReferrerCheckOn";
	public static final String META_REFERRER_CHECK_EMAIL = "metaReferrerCheckEmail";
	public static final String META_REFERRER_CHECK_EMAIL_CC = "metaReferrerCheckEmailCC";


	/* ***************************  REST Call Related Constants  **************************************************/

	public static final String JSON_RESPONSE_DATA_IDENTIFIER = "data";
	public static final String JSON_RESPONSE_ERROR_IDENTIFIER = "error";

	/* ***************************  End of REST Call Related Constants  **************************************************/

	/* ***************************  General Exception Constants  **************************************************/

	public static final String EXCEPTION_CODE_ONLY_EXTERNAL_URLS_FOR_SCAN = "RECORDINGONEXTADDRESSONLY";


	/* ***************************  End of General Exception Constants  **************************************************/

	/* ***************************  JIRA Constants  **************************************************/

	// Risk Classification: Extreme Risk, High Risk, Risk, Low Risk
	public static final String JIRA_CUSTOM_FIELD_1_LABEL = "customfield_17206";
	/* Threat Classification: Abuse of functionality, Anti-Automation, Authentication, Authorization, Business Logic Abuse
	 Clickjacking, Command Injection, Confidentiality (weak or flawed encryption keys, etc)
	 Content Spoofing, Cross-Site Request Forgery, Cross-Site Scripting, Directory Listing
	 Information Leakage, Insecure Transport, Open Redirect, Other, Security Misconfiguration
	 SQL Injection XML Attacks */
	public static final String JIRA_CUSTOM_FIELD_2_LABEL = "customfield_17207";
	// Where Found (bug): None, xStage, Sandbox, CI, Development, QA, LnP, Staging, Pre-Production, Beta, Production
	public static final String JIRA_CUSTOM_FIELD_3_LABEL = "customfield_11206";
	// When Found: None, Development, Testing, Pre-Merge, Post-Merge, Beta-Testing, Post-Release
	public static final String JIRA_CUSTOM_FIELD_4_LABEL = "customfield_11208";
	// System Where Issue Observed: Mobile Native App: Android, Native Mobile App: iPhone, Mobile Native App: iPad, Mobile Web Application, Web Application
	// Web Service, Windows Mobile, Other
	public static final String JIRA_CUSTOM_FIELD_5_LABEL = "customfield_17209";
	// Who Found: None, External: Auditors, External: Other User, Internal: Other User, Internal: User Reported, Internal: Security
	public static final String JIRA_CUSTOM_FIELD_6_LABEL = "customfield_17203";
	// Likelihood/Ease of Exploit: Easy, Medium, Hard, Very Hard
	public static final String JIRA_CUSTOM_FIELD_7_LABEL = "customfield_17204";
	// Issue Impact: Small, Moderate, Large, Critical
	public static final String JIRA_CUSTOM_FIELD_8_LABEL = "customfield_17200";
	// Known Attack?:  Yes (Select Date), No, Don't Know
	public static final String JIRA_CUSTOM_FIELD_9_LABEL = "customfield_17201";
	// Issue ArchType: Violation of Regulatory Compliance, Vulnerability, Weakness/Violation of best practice or guidelines, Attack
	public static final String JIRA_CUSTOM_FIELD_10_LABEL = "customfield_17213";
	// How Discovered: External: Pentest, External: Public Disclosure, External: Bug Bounty, External: Responsible Public Disclosure, Internal: Pentest
	// Internal: Production Automation Tool, Internal: QA Automation Tool, Internal: Security Testing, Internal: Automation Tool (Other), Other (explain)
	public static final String JIRA_CUSTOM_FIELD_11_LABEL = "customfield_17214";


	/* ***************************  End of JIRA Constants  **************************************************/


}
