/**
 * This class will hold all utility methods (methods that would be required through
 * out the entire program).
 *
 * @author Kiran Shirali (kshirali@ebay.com)
 */

package com.dastproxy.common.utils;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dastproxy.common.constants.AppScanConstants;
import com.dastproxy.configuration.RootConfiguration;
import com.dastproxy.dao.DastDAO;
import com.dastproxy.model.DASTProxyException;
import com.dastproxy.model.Issue;
import com.dastproxy.model.Recording;
import com.dastproxy.model.RecordingBatch;
import com.dastproxy.model.Report;
import com.dastproxy.model.Scan;
import com.dastproxy.model.ScanBatch;
import com.dastproxy.model.User;

public final class AppScanUtils {

	// Logger for this classed. Based on Log4j.
	private static final Logger LOGGER = LogManager.getLogger(AppScanUtils.class.getName());

	@Inject
	@Qualifier("dastDAOImpl")
	private DastDAO dao;

	private AppScanUtils() {

	}

	/**
	 * This utility method will return the user object of the logged in user.
	 *
	 * @return logged in user object.
	 */
	public static User getLoggedInUser() {

		final User loggedInUser = new User();
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		loggedInUser.setUserId(auth.getName());
		loggedInUser.setPassword(auth.getCredentials().toString());
		return loggedInUser;
	}

	/**
	 * This utility method will return the IP address of the system.
	 *
	 * @return Internal Subnet IP address in String Format
	 */
	public static String getIpAddress() {

		// A very strange observations in 'server' systems is that there are a
		// couple of address that have been configured. For example I saw that
		// there are
		// two ip addresses like 169.* and 10.* configured into the server. What
		// I am doing here
		// is that in case the default address is not a 10.* based address, then
		// I check
		// for all possible addresses.
		String ipAddress = null;
		try {
			if (InetAddress.getLocalHost() != null) {
				ipAddress = InetAddress.getLocalHost().toString();

				// TODO There is a mismatch between file separator and what is
				// getting returned in ip address. Need to check it out.
				// if(ipAddress.contains(File.separator)){
				if (ipAddress.contains("/")) {

					// TODO There is a mismatch between file separator and what
					// is getting returned in ip address. Need to check it out.
					// ipAddress =
					// ipAddress.substring(ipAddress.lastIndexOf(File.separator)+1);
					ipAddress = ipAddress
							.substring(ipAddress.lastIndexOf("/") + 1);
				}

				if (!ipAddress.startsWith("10.")) {
					Enumeration<NetworkInterface> networkInterfaces;
					try {
						networkInterfaces = NetworkInterface
								.getNetworkInterfaces();

						while (networkInterfaces.hasMoreElements()) {
							NetworkInterface e = networkInterfaces
									.nextElement();
							Enumeration<InetAddress> inetAddress = e
									.getInetAddresses();
							while (inetAddress.hasMoreElements()) {
								InetAddress addr = inetAddress.nextElement();
								String tempAddress = addr.getHostAddress();
								if (tempAddress.contains("/")) {
									tempAddress = tempAddress
											.substring(tempAddress
													.lastIndexOf("/") + 1);
								}
								if (tempAddress.startsWith("10.")) {
									ipAddress = tempAddress;
									break;
								}
							}
						}
					} catch (SocketException socketException) {
						LOGGER.error(
								AppScanConstants.ERROR_MSG_ERROR_OCCURRED_IN_GET_IP_ADDRESS,
								socketException);
					}
				}

			}
		} catch (UnknownHostException exception) {
			LOGGER.error(
					AppScanConstants.ERROR_MSG_ERROR_OCCURRED_IN_GET_IP_ADDRESS,
					exception);
		}

		return ipAddress;
	}

	/**
	 * This utility method returns a random port number (all the port numbers
	 * are above 1023)
	 *
	 * @return A Random Port Number
	 */
	public static int getRandomPort() {

		final int EPHEMERAL_LARGEST_PORT_VALUE = 1023;
		// We do not want to interfere with the ephemeral ports (lesser than
		// 1023 ports)
		final Random random = new Random();
		// Need a random port till the maximum value.
		int randomNumber = random.nextInt(65535);
		if (randomNumber <= EPHEMERAL_LARGEST_PORT_VALUE) {
			randomNumber = randomNumber + 1023;
		}

		return randomNumber;
	}

	/**
	 * Note: This works only on a windows machine.
	 *
	 * @param Path
	 * @param userId
	 * @return Absolute Path of the folder for the user
	 * @throws DASTProxyException
	 */
	public static String createUserFolderIfNotExist(final String Path,
			final String userId) throws DASTProxyException {

		// TODO This does not work for Linux.
		// Unfortunately the File.sperator is not working for our windows set
		// up.
		final File userFolder = new File(Path + "/" + userId);

		if (!userFolder.exists()) {
			final boolean resultOfOperation = userFolder.mkdir();

			if (!resultOfOperation) {
				throw new DASTProxyException(
						"Unable to create user folder for storing recordings");
			}
		}

		return userFolder.getAbsolutePath();
	}

	/**
	 * This utility method returns a date in a predefined format.
	 *
	 * @return Date as a String
	 */
	public static String returnDateInPredefinedFormat() {
		return new SimpleDateFormat(AppScanConstants.DATE_FORMAT)
				.format(new Date());
	}

	/**
	 * This utility method checks whether a particular String is null or not.
	 *
	 * @param String
	 *            to be checked
	 *
	 * @return True/False
	 */
	public static boolean isNotNull(final String element) {

		boolean isNotNull = false;
		if (element != null && !element.isEmpty()) {
			isNotNull = true;
		}

		return isNotNull;
	}

	/**
	 * This utility method checks whether a particular object is null or not.
	 *
	 * @param object
	 *            to be checked
	 *
	 * @return True/False
	 */
	public static boolean isNotNull(final Object object) {

		boolean isNotNull = false;
		if (object != null) {
			isNotNull = true;
		}

		return isNotNull;
	}

	/**
	 * This utility method sends an mail out to the developers DL when an error
	 * occurs.
	 *
	 * @param Exception
	 *            that has been thrown
	 */
	public static void sendErrorMail(final Exception exception) {

		final Map<String, Object> exceptionModel = new HashMap<String, Object>();

		if (AppScanUtils.isNotNull(RootConfiguration.getProperties())
				&& AppScanUtils
						.isNotNull(RootConfiguration
								.getProperties()
								.get(AppScanConstants.PROPERTIES_OPERATING_ENVIRONMENT_IDENTIFIER))) {
			exceptionModel
					.put(AppScanConstants.EXCEPTION_MAIL_TEMPLATE_ENV_DENOTION,
							RootConfiguration
									.getProperties()
									.get(AppScanConstants.PROPERTIES_OPERATING_ENVIRONMENT_IDENTIFIER));
		} else {
			exceptionModel.put(
					AppScanConstants.EXCEPTION_MAIL_TEMPLATE_ENV_DENOTION, "");
		}

		exceptionModel
				.put(AppScanConstants.EXCEPTION_MAIL_TEMPLATE_EXCEPTION_CLASS_DENOTION,
						exception.getClass().getSimpleName());
		if (AppScanUtils.isNotNull(exception.getMessage())) {
			exceptionModel
					.put(AppScanConstants.EXCEPTION_MAIL_TEMPLATE_EXCEPTION_MESSAGE_DENOTION,
							exception.getMessage());
		} else {
			exceptionModel
					.put(AppScanConstants.EXCEPTION_MAIL_TEMPLATE_EXCEPTION_MESSAGE_DENOTION,
							"");
		}
		exceptionModel
				.put(AppScanConstants.EXCEPTION_MAIL_TEMPLATE_EXCEPTION_STACKTRACE_DENOTION,
						ExceptionUtils.getStackTrace(exception));
		exceptionModel
				.put("exceptionDL",
						RootConfiguration
								.getProperties()
								.getProperty(
										AppScanConstants.PROPERTIES_ERROR_CONTACT_DL_IDENTIFIER));

		LOGGER.error("Mail being sent to explain error. The exception is: ",
				exception);

		MailUtils
				.sendEmail(
						RootConfiguration
								.getProperties()
								.getProperty(
										AppScanConstants.PROPERTIES_ERROR_CONTACT_DL_IDENTIFIER),
								null,
						RootConfiguration
								.getProperties()
								.getProperty(
										AppScanConstants.PROPERTIES_ERROR_CONTACT_DL_IDENTIFIER),
						AppScanConstants.EXCEPTION_MAIL_SUBJECT,
						exceptionModel,
						AppScanConstants.EXCEPTION_MAIL_BODY_TEMPLATE);

	}

	/**
	 * There are a couple of characters that Windows will not accept in a file
	 * name. This function will remove/change those characters and will return a
	 * windows safe file system name.
	 *
	 * @param nameOfFile
	 * @return Windows Safe File System Name
	 */
	public static String returnWindowsFileAppropriateName(String nameOfFile) {
		return nameOfFile.replaceAll(" ", "").replace(":", "-");
	}

	/**
	 * This function is used to create a list with a transitive url to the bug UI. This function will be used for reporting purposes.
	 *
	 * @param issuesToBeTransformed
	 * @return issuesList
	 */
	public static List<Issue> returnDASTProxyRelativeUrlIssueList(
			final Report reportFromWhichIssuesToBeTransformed) {

		List<Issue> issuesToBeTransformed = reportFromWhichIssuesToBeTransformed.getIssues();
		List<Issue> finalIssuesList = new LinkedList<Issue>();
		for (Issue issue : issuesToBeTransformed) {
			System.out.println("-------------------Inside returnDASTProxyRelativeUrlIssueList issue.getId()="+issue.getId());
			String dastProxyRelativeBugUIUrl = RootConfiguration.getProperties().getProperty(AppScanConstants.PROPERTIES_OPERATING_ENVIRONMENT_BASE_URL_IDENTIFIER)
					+ "issueNew?report="+reportFromWhichIssuesToBeTransformed.getId()+"&issue=" + issue.getId();
			issue.setDastProxyBugUIIssueUrl(dastProxyRelativeBugUIUrl);
			finalIssuesList.add(issue);
		}

		return finalIssuesList;
	}

}
