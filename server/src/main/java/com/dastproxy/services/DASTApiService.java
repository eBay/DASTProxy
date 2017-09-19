package com.dastproxy.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;
import com.dastproxy.model.DASTProxyException;
import com.dastproxy.model.Issue;
import com.dastproxy.model.Report;
import com.dastproxy.model.Scan;

public interface DASTApiService {
	public void setUpScanForUser(String userName, String password,
			String pathOfConfigFile, String nameOfScan,
			boolean startScanAutomatically, Scan scan) throws Exception;

	public void loginToDASTScanner(final String userName, final String password)
			throws ParserConfigurationException, IOException, SAXException,
			DASTProxyException;

	public void logoutFromDASTScanner() throws ParserConfigurationException,
			IOException, SAXException, DASTProxyException;

	public String checkIfUserPresent(String userName)
			throws UnsupportedEncodingException, ParserConfigurationException,
			IOException, SAXException, DASTProxyException,
			XPathExpressionException;

	public boolean checkIfScanIsNotPresentForUser(String userId, String scanId)
			throws UnsupportedEncodingException, ParserConfigurationException,
			IOException, SAXException, DASTProxyException,
			XPathExpressionException;

	public String checkForScanStarted(String scanId)
			throws ParserConfigurationException, IOException, SAXException,
			DASTProxyException, XPathExpressionException;

	public String getLatestRunForScan(String scanId)
			throws UnsupportedEncodingException, ParserConfigurationException,
			IOException, SAXException, DASTProxyException,
			XPathExpressionException;

	public String getLatestRunForScanReport(String reportId)
			throws UnsupportedEncodingException, ParserConfigurationException,
			IOException, SAXException, DASTProxyException,
			XPathExpressionException;

	public List<Issue> getIssuesFromReport(String scanId, Report report,
			String issueURL) throws UnsupportedEncodingException,
			ParserConfigurationException, IOException, SAXException,
			DASTProxyException, XPathExpressionException;

	public String getReport(String scanId) throws UnsupportedEncodingException,
			ParserConfigurationException, IOException, SAXException,
			DASTProxyException, XPathExpressionException;

	public Issue getOneIssue(List<Issue> ListOfIssues, String searchForIssue);
	public void setUpScanForUserFromService(final String userName,
			String pathOfConfigFile, String nameOfScan,
			final boolean startScanAutomatically, Scan scan) throws Exception;
}
