/**
 * This bean is used as a controller for my UI page. I decoupled this from the rest based controller because there might be direct 
 * class to that controller from Bluefin/Breeze/Selenium test cases.
 * 
 * Currently there is not much that this controller does. Maybe tomorrow there might be more UI pages. Who knows what the future holds right??
 * 
 * @author Kiran Shirali (kshirali@ebay.com)
 */

package com.dastproxy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DASTProxyHomeController {

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
	 * All errors are going to be redirected to the following page
	 * 
	 * @return Error Page
	 */
	@RequestMapping(value = { "/error", "/accessdenied" }, method = RequestMethod.GET)
	public String returnErrorView() {
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
	 * This will redirect you to the Metrics page.
	 * 
	 * @return Metrics Page
	 */
	@RequestMapping(value = { "/metrics" }, method = RequestMethod.GET)
	public String returnMetricsView(){
		
		return "metrics";
	}
	
}
