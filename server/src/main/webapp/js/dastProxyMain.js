/*
 * This is the main Java Script file of the application. I have designed the JavaScript functionality on a MVC structure
 *
 *
 *
 * 				 					to update the view, invoke
 * 					Controller   --------------------------------> View
 *
 * 						|
 * 						|
 * 						|    Get data from the server
 * 						|    by invoking functions in model
 * 						|
 * 						|
 * 			            v
 *   				   Model
 *
 *   All the files are based on the above structure. Currently there are only three files dastProxyMainModel.js, dastProxyMainView.js and this file. Based on concept mimicking namespace, I have put all functionality
 *   of the view under the view file. So if you need to see functionality that updates the view, then check there. If you want to see where data is being received from the server, then check the
 *   model files. For everything else, check the controller files.
 *
 * @Author Kiran Shirali (kshirali@ebay.com)
 */

var loggedInUser;
var recordingName;
var testsuiteName;

// Handler for .ready() called.
$(document).ready(function() {
	'use strict';

	initalStartUp();
	$('textarea').maxlength({
        alwaysShow: true
    });

	$('#stopRecordingSubmitAction').click(function(clickEvent) {
		'user strict';

		var optionSelected = '#' + $('#carouselForStopRecordingOptions .carousel-inner div.active').attr('id');
		//alert(optionSelected);
		view.closeModalWindow(ID_APPLICATION_SUBMIT_RECORDING_OPTIONS_MODAL_WINDOW);
		view.showApplicationPrgoressModalWindow("Initating Action for " + loggedInUser.userId,"30%");
		view.updateProxyDetailsView('','','');
		view.hideStopRecordingHomePageOption();
		view.showStartRecordingHomePageOption();

		if(optionSelected === ID_SET_UP_SCAN_ON_APPSCAN){

			view.showApplicationPrgoressModalWindow("Setting Up a Scan on IBM AppScan Enterprise for " + loggedInUser.userId,"50%");
			var responseObject = model.getScanSetUp(loggedInUser.userId, recordingName, testsuiteName);
			if(responseObject.error == null || responseObject.error == undefined ){
				view.closeModalWindow(ID_APPLICATION_PROGRESS_BAR_MODAL_WINDOW);
				view.showDialog(ID_APPLICATION_SCAN_SET_UP_SUCCESSFUL_NOTIFICATION_DIALOG, true);
				setTimeout(function() {
					//alert("hide");
					view.closeModalWindow(ID_APPLICATION_SCAN_SET_UP_SUCCESSFUL_NOTIFICATION_DIALOG);
				}, DIALOG_BOX_VISIBLE_TIME_MILLISECONDS);
			}else{
				//view.hideStopRecordingDialog();
				view.closeModalWindow(ID_APPLICATION_PROGRESS_BAR_MODAL_WINDOW);
				view.showErrorDialogASE();
			}

		}
		else if(optionSelected === ID_GET_RECORDING_AS_HTD){
			view.showApplicationPrgoressModalWindow("Creating HTD File for " + loggedInUser.userId,"50%");
			var htdFileName = model.getUserHtd(loggedInUser.userId);
			if (htdFileName != null && htdFileName != "") {

				window.open("rest/v1/htd/" + htdFileName);
				view.showStartRecordingHomePageOption();
				view.closeModalWindow(ID_APPLICATION_PROGRESS_BAR_MODAL_WINDOW);
				view.showDialog(ID_APPLICATION_HTD_CREATION_SUCCESSFUL_NOTIFICATION_DIALOG, true);
				setTimeout(function() {
					view.closeModalWindow(ID_APPLICATION_HTD_CREATION_SUCCESSFUL_NOTIFICATION_DIALOG);
				}, DIALOG_BOX_VISIBLE_TIME_MILLISECONDS + 3000);
			} else {
				//view.hideStopRecordingDialog();
				actionOnError();
			}
		}
		else{
			//close
		}
	});


	$("#proxyRecordAction").click(function(clickEvent) {
		actionOnSetUpProxy(loggedInUser.userId);
	});

	$("#recordStartedDialog,#recordInProgressDialog").on('hidden.bs.modal', function (e) {
			view.hideStartRecordingHomePageOption();
			view.showStopRecordingHomePageOption();
	});

	$("#proxyStopRecordAction").click(function(clickEvent) {

		view.showfileTypeDialog();
	});

	$("#spiderScanTabLink").click(function(clickEvent) {

		view.showSpiderScanPageView();
	});

	$('#recordedScanTabLink').click(function(clickEvent) {

		view.showRecordedScanPageView();
	});

	$('#contactUsLink').click(function(clickEvent) {

		view.showDialog(ID_APPLICATION_CONTACT_US_DIALOG,false);
	});

	$('#helpLink').click(function(clickEvent) {

		view.showDialog(ID_APPLICATION_HELP_DIALOG,true);
	});

	$("#logoutLink").click(function(clickEvent) {
		window.location.href = "j_spring_security_logout";
	});

	$(ID_SET_UP_NEW_SPIDER_SCAN_ACTION).click(function(clickEvent) {
		view.showDialog('#dastSpiderScanSetUp',true);
	});

	$(ID_CONTACT_US_SUBMIT_ACTION).click(function(clickEvent) {

		var contactUsObject = new Object();
		contactUsObject.issueType = $(ID_CONTACT_US_ISSUE_TYPE_DROP_DOWN).val();
		contactUsObject.user = loggedInUser;
		contactUsObject.desc = $(ID_CONTACT_US_ISSUE_DESC).val();
		view.closeModalWindow(ID_APPLICATION_CONTACT_US_DIALOG);
		view.showApplicationPrgoressModalWindow("Sending message for " + loggedInUser.userId,"30%");
		resetContactUsModalValues();
		view.showApplicationPrgoressModalWindow("Sending message for " + loggedInUser.userId,"50%");
		model.contactUs(contactUsObject);
		view.closeModalWindow(ID_APPLICATION_PROGRESS_BAR_MODAL_WINDOW);
		view.showDialog(ID_APPLICATION_CONTACT_US_SUCCESSFUL_NOTIFICATION_DIALOG, true);
	});

});

/*
 * This function sets up the view for the application. It brings up the progress bar, makes call to get the name of the
 * logged in user and also checks if the user also has a running proxy.
 *
 * Depending on whether a proxy is already set up or not, it displays the relevant view.
 *
 */
function initalStartUp() {
	"use strict";

	$('.carousel').each(function(){
        $(this).carousel({
            interval: false
        });
    });

	view.setUpToolTips();
	view.setUpMainPageView();
	view.showApplicationPrgoressModalWindow();
	view.hideElement(START_RECORDING_ELEMENTS);
	view.hideElement(STOP_RECORDING_ELEMENTS);
	view.showApplicationPrgoressModalWindow("Getting Logged In User Details",
			"75%");
	loggedInUser = model.getLoggedInUser();

	if (loggedInUser !== null && loggedInUser !== undefined && loggedInUser.userId !== null && loggedInUser.userId !== undefined) {
		view.updateLoggedInUserBanner(loggedInUser.userId);
	}

	if (model.getProxyRunningStatus(loggedInUser.userId)) {
		actionOnSetUpProxy(null);
	} else {

		view.showStartRecordingHomePageOption();
		view.closeModalWindow(ID_APPLICATION_PROGRESS_BAR_MODAL_WINDOW);
	}
}

/*
 * This functions is for setting up the proxy. It does the action of setting up the progress bar,
 * making the relevant API calls and then displaying the values on the screen.
 *
 */
function actionOnSetUpProxy(emailId) {
	'use strict';
	view.showApplicationPrgoressModalWindow("Setting Up Proxy for " + loggedInUser.userId, "55%");

	var proxyIdentificationEntity = new Object();
	proxyIdentificationEntity.user = loggedInUser;
	proxyIdentificationEntity.proxyIdentifier = emailId;
	var proxyDetailsResponse = model.getProxy(proxyIdentificationEntity);

	if (proxyDetailsResponse !== null && proxyDetailsResponse !== undefined && proxyDetailsResponse.proxy !== null && proxyDetailsResponse.proxy !== undefined && proxyDetailsResponse.proxy.proxyAddress !== null && proxyDetailsResponse.proxy.proxyPort !== null) {
		view.updateProxyDetailsView(proxyDetailsResponse.proxy.proxyAddress,
				proxyDetailsResponse.proxy.proxyPort,
				proxyDetailsResponse.proxyIdentifier);
	}

	view.setProgressBarStatus(ID_APPLICATION_PROGRESS_BAR_MODAL_WINDOW, "100%");
	view.closeModalWindow(ID_APPLICATION_PROGRESS_BAR_MODAL_WINDOW);

	if (proxyDetailsResponse !== null && proxyDetailsResponse !== undefined && proxyDetailsResponse.proxy !== null && proxyDetailsResponse.proxy !== undefined && proxyDetailsResponse.proxy.newlyCreated !== null && proxyDetailsResponse.proxy.newlyCreated === true) {
		actionOnNewProxySetUpEvent();
	} else {
		actionOnExistingProxyEvent();
	}

}

function actionOnNewProxySetUpEvent(){
	'use strict';
	//alert(1);
	//if($("#StartRecordingSpan").is(":visible")){
		view.hideStartRecordingHomePageOption();
	//}

	view.showRecordStartedDialog();
	setTimeout(function() {
		//alert("hide");
		view.closeModalWindow(ID_APPLICATION_RECORDING_STARTED_NOTIFICATION_DIALOG);
	}, DIALOG_BOX_VISIBLE_TIME_MILLISECONDS);
}

function actionOnExistingProxyEvent() {
	'use strict';
	view.showDialog(ID_APPLICATION_RECORDING_IN_PROGRESS_NOTIFICATION_DIALOG,true);
	setTimeout(function() {
		//alert("hide");
		view.closeModalWindow(ID_APPLICATION_RECORDING_IN_PROGRESS_NOTIFICATION_DIALOG);
	}, DIALOG_BOX_VISIBLE_TIME_MILLISECONDS);
}

function actionOnCancelRecording() {
	'use strict';
	model.cancelScanRecording(loggedInUser.userId);
	location.reload();
}

function actionOnError() {
	'use strict';
	view.closeModalWindow('.modal');
	view.showDialog(ID_APPLICATION_ERROR_OCCURRED_NOTIFICATION_DIALOG,false);

}

function resetContactUsModalValues(){
	$(ID_CONTACT_US_ISSUE_TYPE_DROP_DOWN).val("bug");
	$(ID_CONTACT_US_ISSUE_DESC).val("");
}
