/**
 * This class holds all the root configuration parameters that is looked up from Tomcat JNDI.
 * Consider this set as global properties for the project.
 *
 * @author Kiran Shirali (kshirali@ebay.com)
 */
package com.dastproxy.configuration;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dastproxy.common.constants.AppScanConstants;
import com.dastproxy.common.utils.AppScanUtils;

public final class RootConfiguration {

	// Logger for this class.
	private static final Logger LOGGER = LogManager
			.getLogger(RootConfiguration.class.getName());
	private static Properties properties;

	private RootConfiguration() {

	}

	/**
	 * At initialization of this class, the properties would be set up from JNDI
	 * lookup and would be available for the entire application.
	 */
	static {
		setUpApplicationProperties();
	}

	/**
	 * This function will perform the actual JNDI lookups and will put create a
	 * properties object.
	 */
	private static void setUpApplicationProperties() {

		if (!AppScanUtils.isNotNull(properties) || properties.isEmpty()) {

			properties = new Properties();
			try {

				Context context = (Context) new InitialContext()
						.lookup("java:comp/env");
				properties
						.setProperty(
								AppScanConstants.PROPERTIES_OPERATING_ENVIRONMENT_IDENTIFIER,
								(String) context
										.lookup(AppScanConstants.JNDI_ENVIRONMENT_IDENTIFIER));

				properties
						.setProperty(
								AppScanConstants.PROPERTIES_OPERATING_ENVIRONMENT_BASE_URL_IDENTIFIER,
								(String) context
										.lookup(AppScanConstants.JNDI_ENVIRONMENT_BASE_URL_IDENTIFIER));

				properties
						.setProperty(
								AppScanConstants.PROPERTIES_APP_SCAN_BASE_URL_IDENTIFIER,
								(String) context
										.lookup(AppScanConstants.JNDI_APPSCAN_BASE_URL_IDENTIFIER));
				properties
						.setProperty(
								AppScanConstants.PROPERTIES_APP_SCAN_BASE_SERVICES_URL_IDENTIFIER,
								(String) context
										.lookup(AppScanConstants.JNDI_APPSCAN_SERVICES_BASE_URL_IDENTIFIER));
				properties
						.setProperty(
								AppScanConstants.PROPERTIES_APP_SCAN_SERVER_NAME_IDENTIFIER,
								(String) context
										.lookup(AppScanConstants.JNDI_APPSCAN_SERVER_NAME_IDENTIFIER));

				properties
						.setProperty(
								AppScanConstants.PROPERTIES_ERROR_CONTACT_DL_IDENTIFIER,
								(String) context
										.lookup(AppScanConstants.JNDI_ERROR_MESSAGE_CONTACT_DL_IDENTIFIER));
				properties
						.setProperty(
								AppScanConstants.PROPERTIES_CONTACT_US_SUPPORT_DL_IDENTIFIER,
								(String) context
										.lookup(AppScanConstants.JNDI_CONTACT_US_SUPPORT_DL_IDENTIFIER));
				properties
						.setProperty(
								AppScanConstants.PROPERTIES_DO_NOT_REPLY_EMAIL_ID_IDENTIFIER,
								(String) context
										.lookup(AppScanConstants.JNDI_DO_NOT_REPLY_EMAIL_ID_IDENTIFIER));

				properties
						.setProperty(
								AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_ID_IDENTIFIER,
								(String) context
										.lookup(AppScanConstants.JNDI_DO_SERVICE_ACCOUNT_ID_IDENTIFIER));

				properties
						.setProperty(
								AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_PWD_IDENTIFIER,
								(String) context
										.lookup(AppScanConstants.JNDI_DO_SERVICE_ACCOUNT_PWD_IDENTIFIER));
				properties
				.setProperty(
						AppScanConstants.PROPERTIES_APPSCAN_SERVICE_ACCOUNT_UID,
						(String) context
								.lookup(AppScanConstants.JNDI_DO_SERVICE_ACCOUNT_UID));
				
				properties
						.setProperty(
								AppScanConstants.PROPERTIES_RUN_CRON_JOBS,
								(String) context
										.lookup(AppScanConstants.JNDI_DO_RUN_CRONJOB_SWITCH));
				properties.setProperty(
						AppScanConstants.PROPERTIES_JIRA_BASE_URL,
						(String) context
								.lookup(AppScanConstants.JNDI_JIRA_BASE_URL));
				properties
						.setProperty(
								AppScanConstants.PROPERTIES_JIRA_SERVICE_ACCOUNT_USERNAME,
								(String) context
										.lookup(AppScanConstants.JNDI_JIRA_SERVICE_ACCOUNT_USERNAME));
				properties
						.setProperty(
								AppScanConstants.PROPERTIES_JIRA_SERVICE_ACCOUNT_PASSWORD,
								(String) context
										.lookup(AppScanConstants.JNDI_JIRA_SERVICE_ACCOUNT_PASSWORD));

				properties
				.setProperty(
						AppScanConstants.PROPERTIES_SMTP_SERVER,
						(String) context
								.lookup(AppScanConstants.JNDI_SMTP_SERVER));

				properties
				.setProperty(
						AppScanConstants.PROPERTIES_ORG_HTTP_PROXY,
						(String) context
								.lookup(AppScanConstants.JNDI_ORG_HTTP_PROXY));

				properties
				.setProperty(
						AppScanConstants.PROPERTIES_DAST_SCANNER_SCANNING_TEMPLATE_NAME_OR_ID,
						(String) context
								.lookup(AppScanConstants.JNDI_DAST_SCANNER_SCANNING_TEMPLATE_NAME_OR_ID));

				properties
						.setProperty(
								AppScanConstants.EMAIL_DOMAIN,
								(String) context
										.lookup(AppScanConstants.JNDI_EMAIL_DOMAIN));
				properties
				.setProperty(
						AppScanConstants.WHITE_LIST_PIBLIC_IPS,
						(String) context
								.lookup(AppScanConstants.JNDI_WHITELIST_PUBLIC_IPS));
				properties
				.setProperty(
						AppScanConstants.ADMIN_GROUPS_LIST,
						(String) context
								.lookup(AppScanConstants.ADMIN_GROUPS_LIST));

				properties
				.setProperty(
						AppScanConstants.ADMIN_LOGIN_LIST,
						(String) context
								.lookup(AppScanConstants.ADMIN_LOGIN_LIST));
				
				properties.setProperty(AppScanConstants.META_REFERRER_CHECK_ON,(String) context.lookup(AppScanConstants.META_REFERRER_CHECK_ON));
				properties.setProperty(AppScanConstants.META_REFERRER_CHECK_EMAIL,(String) context.lookup(AppScanConstants.META_REFERRER_CHECK_EMAIL));
				properties.setProperty(AppScanConstants.META_REFERRER_CHECK_EMAIL_CC,(String) context.lookup(AppScanConstants.META_REFERRER_CHECK_EMAIL_CC));
				properties.setProperty(AppScanConstants.REPORT_ISSUES_EMAIL_CC_ADDRESS_IF_ISSUES,(String) context.lookup(AppScanConstants.REPORT_ISSUES_EMAIL_CC_ADDRESS_IF_ISSUES));
				
				

			} catch (NamingException namingException) {
				LOGGER.error("There has been an error in RootConfiguration while setting up JNDI Configured Properties. The details are {}",namingException);
				// TODO: Should throw a DASTProxy Exception. However, because
				// this is being used in a static block, cannot implement a
				// throws scenario for the bloc. Need to
				// find a work around.
			}
		}
	}

	/**
	 * Getter for the properties
	 *
	 * @return JNDI Properties object
	 */
	public static Properties getProperties() {
		return properties;
	}
}
