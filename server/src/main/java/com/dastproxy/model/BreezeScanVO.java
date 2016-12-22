package com.dastproxy.model;

import java.util.Date;

public class BreezeScanVO {

	
	public String displayScanName;
	public int readyStatusCount;
	public int suspendedStatusCount;
	public int completedStatusCount;
	public int runningStatusCount;
	public String tsDynamicIdentifier;
	public Long breezeUniqueTS;
	public String breezeUniqueTSString;
	public String displayStatus;
	
	public String uniqueObjectId; //tsDynamicIdentifier + breezeUniqueTS 
	
	public String getDisplayScanName() {
		return displayScanName;
	}
	public void setDisplayScanName(String displayScanName) {
		this.displayScanName = displayScanName;
	}
	public int getReadyStatusCount() {
		return readyStatusCount;
	}
	public void setReadyStatusCount(int readyStatusCount) {
		this.readyStatusCount = readyStatusCount;
	}
	public int getSuspendedStatusCount() {
		return suspendedStatusCount;
	}
	public void setSuspendedStatusCount(int suspendedStatusCount) {
		this.suspendedStatusCount = suspendedStatusCount;
	}
	public int getCompletedStatusCount() {
		return completedStatusCount;
	}
	public void setCompletedStatusCount(int completedStatusCount) {
		this.completedStatusCount = completedStatusCount;
	}
	public String getTsDynamicIdentifier() {
		return tsDynamicIdentifier;
	}
	public void setTsDynamicIdentifier(String tsDynamicIdentifier) {
		this.tsDynamicIdentifier = tsDynamicIdentifier;
	}
	public Long getBreezeUniqueTS() {
		return breezeUniqueTS;
	}
	public void setBreezeUniqueTS(Long breezeUniqueTS) {
		this.breezeUniqueTS = breezeUniqueTS;
	}
	public String getUniqueObjectId() {
		return uniqueObjectId;
	}
	public void setUniqueObjectId(String uniqueObjectId) {
		this.uniqueObjectId = uniqueObjectId;
	}
	public String getDisplayStatus() {
		return displayStatus;
	}
	public void setDisplayStatus(String displayStatus) {
		this.displayStatus = displayStatus;
	}
	public String getBreezeUniqueTSString() {
		return breezeUniqueTSString;
	}
	public void setBreezeUniqueTSString(String breezeUniqueTSString) {
		this.breezeUniqueTSString = breezeUniqueTSString;
	}
	public int getRunningStatusCount() {
		return runningStatusCount;
	}
	public void setRunningStatusCount(int runningStatusCount) {
		this.runningStatusCount = runningStatusCount;
	}
	
}
