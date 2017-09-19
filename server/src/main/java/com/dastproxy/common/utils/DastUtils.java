/**
 * This class will hold all utility methods (methods that would be required through
 * out the entire program).
 *
 * @author Srinivasa Rao (schirathanagandl@ebay.com)
 */

package com.dastproxy.common.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dastproxy.common.constants.AppScanConstants;
import com.dastproxy.configuration.RootConfiguration;
import com.dastproxy.dao.DastDAO;
import com.dastproxy.model.Recording;
import com.dastproxy.model.RecordingBatch;
import com.dastproxy.model.Scan;
import com.dastproxy.model.ScanBatch;
import com.dastproxy.model.User;


public final class DastUtils {

	// Logger for this classed. Based on Log4j.
	private static final Logger LOGGER = LogManager.getLogger(DastUtils.class.getName());

	public Recording createRecording(String name, String owner, String htdFilename){
		Recording recordingInstance = new Recording();
		if (name != null && !"none".equals(name)){
			if (name.length() > 45) name = name.substring(0, 44);
			recordingInstance.setTestcaseName(name);
		} else {
			recordingInstance.setTestcaseName(owner + AppScanUtils.returnDateInPredefinedFormat());
		}

		recordingInstance.setOwner(owner);
		recordingInstance.setHtdFilename(htdFilename);
		if (htdFilename!= null)
			recordingInstance.setHarFilename(htdFilename.replace(AppScanConstants.HTD_FILE_EXTENSION,AppScanConstants.HAR_FILE_EXTENSION));
		recordingInstance.setEnabled(true);
		Date now = new Date();
		recordingInstance.setDateCreated(now);
		recordingInstance.setLastModified(now);

		return recordingInstance;
	}

	public RecordingBatch createRecordingBatch(String testsuiteName, String userId, boolean isManual, boolean isNightly){
		RecordingBatch batch = new RecordingBatch();
		batch.setTestsuiteName(testsuiteName);
		batch.setEnabled(true);
		batch.setDateCreated(new Date());
		batch.setManualTestBatch(isManual);
		batch.setOwner(userId);
		batch.setNightlyBatch(isNightly);

		return batch;
	}

	public ScanBatch createScanBatch(Long recordingBatchId, String userId, String testsuiteName, List<Scan> scans){
		ScanBatch batch = new ScanBatch();
		batch.setOwner(userId);
		batch.setTestsuiteName(testsuiteName);
		batch.setDateCreated(new Date());
		batch.setSubsetOfBatch(true);
		batch.setRecordingBatchId(recordingBatchId);
		batch.setScans(scans);

		return batch;
	}
	public boolean isAdmin(String userId){
		String attr = (String)getHttpSession().getAttribute("isAdmin");
		boolean attrBool = (attr!=null && attr.equals("true"))?true:false;
		if (!attrBool && userId !=null){
			String adminLoginList = RootConfiguration.getProperties().getProperty(AppScanConstants.ADMIN_LOGIN_LIST);
			if (adminLoginList != null && (adminLoginList.toUpperCase().contains(userId.toUpperCase()+",") || adminLoginList.toUpperCase().endsWith(userId.toUpperCase()))){
				attrBool = true;
			}
		}

		return attrBool;
	}

	public HttpSession getHttpSession() {
	    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	    return attr.getRequest().getSession(true); // true == allow create
	}

	public void createScanErrorRecord(String userId, String testcaseName, User user, DastDAO dao){
		try{
		Scan scan = new Scan();
		scan.setToBeTracked(false);
		scan.setScanName(testcaseName);
		scan.setTestCaseName("Error Record");
		scan.setScanState("Error");
		scan.setSetUpViaBluefin(false);
		scan.setUser(user);
		dao.saveScan(scan);
		scan.setTestCaseName(testcaseName);
		dao.saveScan(scan);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
