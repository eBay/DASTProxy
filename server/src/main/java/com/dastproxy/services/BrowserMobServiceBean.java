/**
 * This service bean is used to control browser mob proxy specific functionality. 
 * 
 * This class gives the functionality to :
 * 
 * 1. 'Create a Proxy'
 * 2. 'Destroy a proxy and to collect the HAR details'
 * 
 * @author Kiran Shirali (kshirali@ebay.com)
 * 
 */

package com.dastproxy.services;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.ProxyServer;

import com.dastproxy.common.constants.AppScanConstants;
import com.dastproxy.common.utils.AppScanUtils;
import com.dastproxy.configuration.RootConfiguration;
import com.dastproxy.model.DASTProxyException;

@Service
@Scope("singleton")
@Qualifier("browserMobServiceBean")
public class BrowserMobServiceBean {

	// Logger for this class.
	private static final Logger LOGGER = LogManager
			.getLogger(BrowserMobServiceBean.class.getName());

	// Default Constructor.
	public BrowserMobServiceBean() {
		super();
	}

	public ProxyServer setUpProxyAndStartRecordForUser(final String userId)
			throws DASTProxyException {
		final ProxyServer server = new ProxyServer();
		int retryCount = 0;
		while (true) {

			try {
				final int randomPort = AppScanUtils.getRandomPort();
				server.setPort(randomPort);
				server.start();
				Map<String, String> mobProxyServerOptions = new HashMap<String, String>();
				// Check if the organization has an upstream proxy in the network segment that the project is placed on.
				// If that is the case then browsermob proxy has to know about it
				if (AppScanUtils
						.isNotNull(RootConfiguration
								.getProperties()
								.getProperty(
										AppScanConstants.PROPERTIES_ORG_HTTP_PROXY))) {
					mobProxyServerOptions
							.put("httpProxy",
									RootConfiguration
											.getProperties()
											.getProperty(
													AppScanConstants.PROPERTIES_ORG_HTTP_PROXY));

				}
				server.setOptions(mobProxyServerOptions);
				LOGGER.debug("A new proxy has been set up");
				server.setCaptureHeaders(true);
				server.setCaptureContent(true);
				server.setCaptureBinaryContent(false);
				server.newHar(userId);
				break;
			} catch (Exception exception) {

				if (retryCount > AppScanConstants.PROXY_CREATION_MAX_RETRY_COUNT) {
					LOGGER.error(
							"There is an error when trying to create a new proxy. The details are: {}",
							exception);
					throw new DASTProxyException(
							"Unable to Set Up a new Proxy. Probably all ports are used up. Please contact Administrator");
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"There is an error when trying to create a new proxy. The details are: {}",
							exception);
					LOGGER.debug("Trying Again");
				}

				retryCount++;
				continue;
			}

		}

		return server;
	}

	public boolean stopServer(final ProxyServer proxyServer, final String userId)
			throws DASTProxyException {

		try {
			proxyServer.stop();
		} catch (Exception exception) {
			LOGGER.debug(
					"Error when stoping the server and discarding the recordings for user id {}. The details are: {}",
					userId, exception);
			throw new DASTProxyException(
					"Error when stoping the server and discarding the recordings.");
		}
		return true;
	}

	/**
	 * This service bean method creates a HTD file and returns the name to the
	 * calling function.
	 * 
	 * What it does is that it takes the Proxy Server object, extract the HAR
	 * file from it. It then saves the file in a predetermined place on the file
	 * system. It then invokes a .NET console application (to be changed into a
	 * web service in the future) to read this file and convert into a .HTD
	 * file. The .NET Application converts it with the same name as the HAR
	 * file, but with a .htd extension. So this file name is returned by the
	 * function.
	 * 
	 * @param proxyServer
	 * @param userId
	 * @return Name of the HTD file that has been created on the file system.
	 * @throws IOException
	 * @throws DASTProxyException
	 */
	public String stopServerAndReturnHar(final ProxyServer proxyServer,
			final String userId, final String identifierForHTDFile)
			throws IOException, DASTProxyException {

		LOGGER.debug(AppScanConstants.DEBUG_MSG__IN_FUNC_STOP_SERVER_RETURN_HAR);
		String nameOfHtd = null;
		FileOutputStream fileOutputStream = null;
		BufferedReader bufferedReader = null;

		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request is for the har with following identifier: "
						+ userId);
			}
			final String userFolder = AppScanUtils.createUserFolderIfNotExist(
					AppScanConstants.USER_HTD_FILES_LOCATION, userId) + "/";
			String nameFormatForUser = null;
			if (AppScanUtils.isNotNull(identifierForHTDFile)) {
				nameFormatForUser = "Recordings-" + identifierForHTDFile + "-"
						+ AppScanUtils.returnDateInPredefinedFormat();
			} else {
				nameFormatForUser = "Recordings-" + userId + "-"
						+ AppScanUtils.returnDateInPredefinedFormat();
			}

			final String userFilePath = userFolder + nameFormatForUser;
			final String pathOfHarFile = userFilePath
					+ AppScanConstants.HAR_FILE_EXTENSION;
			nameOfHtd = nameFormatForUser + AppScanConstants.HTD_FILE_EXTENSION;
			final String pathOfHtd = userFilePath
					+ AppScanConstants.HTD_FILE_EXTENSION;
			final Har har = proxyServer.getHar();
			fileOutputStream = new FileOutputStream(pathOfHarFile);
			har.writeTo(fileOutputStream);
			LOGGER.debug("A HAR file has been successfully written to the file system");
			final Process buildHar = new ProcessBuilder(
					AppScanConstants.HTD_CONVERTOR_APP_PATH, pathOfHarFile,
					pathOfHtd).start();
			final InputStream inputStream = buildHar.getInputStream();
			final InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);
			bufferedReader = new BufferedReader(inputStreamReader);

			if (LOGGER.isDebugEnabled()) {
				String line;
				while (AppScanUtils
						.isNotNull((line = bufferedReader.readLine()))) {
					LOGGER.debug("HTDConvertor Application is saying: " + line);
				}
				LOGGER.debug("HTDConvertor Application has terminated");
			}

			proxyServer.stop();
			LOGGER.debug(AppScanConstants.DEBUG_MSG_PROXY_STOPPED);
		} catch (Exception exception) {
			LOGGER.error(
					"Error in BrowserMobServiceBean.stopServerAndReturnHar function",
					exception);
			throw new DASTProxyException(
					"Error in creating a file containing all the recordings. Contact Administrator");
		} finally {
			// Clean Up Resources
			if (AppScanUtils.isNotNull(fileOutputStream)) {
				fileOutputStream.close();
			}
			if (AppScanUtils.isNotNull(bufferedReader)) {
				bufferedReader.close();
			}

		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Name of the HTD is being returned. The name is: "
					+ nameOfHtd);
		}

		return nameOfHtd;
	}

}
