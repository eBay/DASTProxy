/**
 * This bean is used as a controller for my UI page. I decoupled this from the rest based controller because there might be direct 
 * class to that controller from Bluefin/Breeze/Selenium test cases.
 * 
 * Currently there is not much that this controller does. Maybe tomorrow there might be more UI pages. Who knows what the future holds right??
 * 
 * @author Kiran Shirali (kshirali@ebay.com)
 */

package com.dastproxy.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dastproxy.common.utils.DastUtils;
import com.dastproxy.dao.DastDAO;
import com.dastproxy.services.DASTApiService;

@Controller
public class DASTProxyHomeController {

	@Inject
	@Qualifier("appScanEnterpriseRestService")
	private DASTApiService dastApiService;

	@Inject
	@Qualifier("dastDAOImpl")
	private DastDAO dao;

	// Logger for this class.
	private static final Logger LOGGER = LogManager
			.getLogger(DASTProxyHomeController.class.getName());

	/**
	 * If a request comes to as "http://server name/App Name/" or
	 * "http://server name/App Name/index.html", this function will hold the
	 * rest.
	 * 
	 * @return Home Page
	 */
	@RequestMapping(value = { "/", "/index.html" }, method = RequestMethod.GET)
	public String returnHomeView() {
		return "newHomePage";
	}

	/**
	 * If a request comes to as "http://server name/App Name/recordings" or
	 * "http://server name/App Name/recordings", this function will hold the
	 * rest.
	 *
	 * @return recordings
	 */
	@RequestMapping(value = { "/recordings" }, method = RequestMethod.GET)
	public String returnRecordingsView() {
		return "recordings";
	}

	@RequestMapping(value = { "/report" }, method = RequestMethod.GET)
	public String getAllScansOfUser(Model model, HttpServletRequest request) {
		String scanId = request.getParameter("scanId");
        model.addAttribute("scanId", scanId);
		return "report";
	}

	@RequestMapping(value = { "/breeze_report" }, method = RequestMethod.GET)
	public String getBreezeScanReport(Model model, HttpServletRequest request) {
		String tsDynamicIdentifier = request.getParameter("tsDynamicIdentifier");
		String breezeUniqueTS = request.getParameter("breezeUniqueTS");

        model.addAttribute("tsDynamicIdentifier", tsDynamicIdentifier);
        model.addAttribute("breezeUniqueTS", breezeUniqueTS);

		return "breeze_report";
	}


	@RequestMapping(value = { "/breeze" }, method = RequestMethod.GET)
	public String getAllScansOfUser() {
		return "breeze";
	}

	@RequestMapping(value = { "/dashboard" }, method = RequestMethod.GET)
	public String getDashboard() {
		return "dashboard";
	}
	@RequestMapping(value = { "/scan_batch_report" }, method = RequestMethod.GET)
	public String getScanBatchReport(Model model, HttpServletRequest request) {
		//Not converting it to Long as it is rendered as String only.
		String scanBatchId = request.getParameter("scanBatchId");

        model.addAttribute("scanBatchId", scanBatchId);

		return "batch_report";
	}
	/**
	 * All errors are going to be redirected to the following page
	 * 
	 * @return Error Page
	 */
	@RequestMapping(value = { "/error", "/accessdenied" }, method = RequestMethod.GET)
	public String returnErrorView(HttpSession session) {
		Exception ex = (Exception)session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
		ex.printStackTrace();
		return "error";
	}

	/**
	 * This will redirect you to the logout page.
	 * 
	 * @return Logout Page
	 */
	@RequestMapping(value = { "/logout" }, method = RequestMethod.GET)
	public String returnLogoutView() {
		return "newLogout";
	}
	
	/**
	 * This will redirect you to the Issue Details page.
	 * 
	 * @return Logout Page
	 */
	@RequestMapping(value = { "/issue/**" }, method = RequestMethod.GET)
	public String returnIssueDetailsView(){
		
		return "issueDetails";
	}
	
	/**
	 * This will redirect you to the Issue Details page.
	 *
	 * @return Logout Page
	 */
	@RequestMapping(value = { "/issueNew/**" }, method = RequestMethod.GET)
	public String returnIssueDetailsViewNew(){

		return "issueDetailsNew";
	}	

	/**
	 * This will redirect you to the Metrics page.
	 * 
	 * @return Metrics Page
	 */
	@RequestMapping(value = { "/metrics" }, method = RequestMethod.GET)
	public String returnMetricsView(){
		
		return "metrics";
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

}
