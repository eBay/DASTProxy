package com.dastproxy.controller.rest;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dastproxy.dao.DastDAO;

@Controller
public class DASTProxyMetricsRestController {


	@Inject
	@Qualifier("dastDAOImpl")
	private DastDAO dao;

	/**
	 * @return the dao
	 */
	public DastDAO getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(final DastDAO dao) {
		this.dao = dao;
	}
	
	@RequestMapping(value = { "/rest/metrics/uniqueusers" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List getUniqueUsers(){
		
		final List uniqueUserList = getDao().getActiveUserList();
		return uniqueUserList;
	}
	
	@RequestMapping(value = { "/rest/metrics/scancounts" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Long> getScanCounts(){
		
		final Long successfulRunScanCount = getDao().getNoOfScansSuccessfullyRun();
		final Long scanSetUpButNotRunCount = getDao().getNoOfScansSetUpButNotRun();
		final Long scanInErrorCount = getDao().getNoOfScansSetUpButInError();
		final Long scansSetUpViaBluefin = getDao().getNoOfScansSetupViaBluefin();
		final Long scansSetUpViaDASTUI = getDao().getNoOfScansSetupViaDASTUI();
		
		
		final List<Long> scanCounts = new ArrayList<Long>();
		scanCounts.add(successfulRunScanCount);
		scanCounts.add(scanSetUpButNotRunCount);
		scanCounts.add(scanInErrorCount);
		scanCounts.add(scansSetUpViaBluefin);
		scanCounts.add(scansSetUpViaDASTUI);
		
		return scanCounts;
	}
	
	@RequestMapping(value = { "/rest/metrics/scanovermonths" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List getScanDataOverMonths(){
		
		List monthAndScanData = getDao().getScanOverMonthsData();
		return monthAndScanData;
	}
}
