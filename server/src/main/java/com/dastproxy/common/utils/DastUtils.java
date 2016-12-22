/**
 * This class will hold all utility methods (methods that would be required through
 * out the entire program). 
 * 
 * @author Srinivasa Rao (schirathanagandl@ebay.com)
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.dastproxy.common.constants.AppScanConstants;
import com.dastproxy.configuration.RootConfiguration;
import com.dastproxy.dao.DastDAO;
import com.dastproxy.dao.impl.DastDAOImpl;
import com.dastproxy.model.DASTProxyException;
import com.dastproxy.model.Issue;
import com.dastproxy.model.Recording;
import com.dastproxy.model.RecordingBatch;
import com.dastproxy.model.Report;
import com.dastproxy.model.Scan;
import com.dastproxy.model.ScanBatch;
import com.dastproxy.model.User;


public final class DastUtils {

	// Logger for this classed. Based on Log4j.
	private static final Logger LOGGER = LogManager
			.getLogger(DastUtils.class.getName());

	//Need to inject using spring
	private DastDAO dao = new DastDAOImpl();

	public Recording createRecording(String name, String owner, String harFilename){
		Recording recordingInstance = new Recording();
		if (name != null && !"none".equals(name)){
			if (name.length() > 45) name = name.substring(0, 44);
			recordingInstance.setTestcaseName(name);
		} else {
			recordingInstance.setTestcaseName(owner + AppScanUtils.returnDateInPredefinedFormat());
		}

		recordingInstance.setOwner(owner);
		recordingInstance.setHarFilename(harFilename);
		recordingInstance.setEnabled(true);
		Date now = new Date();
		recordingInstance.setDateCreated(now);
		recordingInstance.setLastModified(now);

		return recordingInstance;
	}

	public RecordingBatch createRecordingBatch(String testsuiteName, String userId, boolean isManual){
		RecordingBatch batch = new RecordingBatch();
		batch.setTestsuiteName(testsuiteName);
		batch.setEnabled(true);
		batch.setDateCreated(new Date());
		batch.setManualTestBatch(isManual);
		batch.setOwner(userId);

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
}
