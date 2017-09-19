var view = {

		setUpMainPageView : function() {
			$(STOP_RECORDING_ELEMENTS).hide();
			$(START_RECORDING_ELEMENTS).hide();

			if(!$(ID_RECORD_SCAN_TAB).hasClass("active")){
				$(ID_RECORD_SCAN_TAB).addClass("active");
			}
			$("#proxy_values").hide();
			$("#scan_input").show();
		},

		showSpiderScanPageView : function(){
			$("#divForRecordedScan").hide();

			if($(ID_RECORD_SCAN_TAB).hasClass("active")){
				$(ID_RECORD_SCAN_TAB).removeClass("active");
			}

			if(!$("#spiderScanTab").hasClass("active")){
				$("#spiderScanTab").addClass("active");
			}
		},

		showRecordedScanPageView : function(){
			$("#divForRecordedScan").show();

			if($("#spiderScanTab").hasClass("active")){
				$("#spiderScanTab").removeClass("active");
			}

			if(!$(ID_RECORD_SCAN_TAB).hasClass("active")){
				$(ID_RECORD_SCAN_TAB).addClass("active");
			}
		},

		setProgressBarStatus: function(id,progressAmount){
			$(id).css("width",progressAmount);
		},

		setUpToolTips: function(){
			$("#proxyRecordAction").tooltip();
		},

		showApplicationPrgoressModalWindow: function(titleValue, progessAmount){

			if(titleValue !== null && titleValue !== undefined){
				$(ID_APPLICATION_PROGRESS_BAR_MODAL_WINDOW_TITLE).text(titleValue);
			}

			if(progessAmount !== null && progessAmount !== undefined){
				this.setProgressBarStatus(ID_APPLICATION_PROGRESS_BAR, progessAmount);
			}

			$(ID_APPLICATION_PROGRESS_BAR_MODAL_WINDOW).modal({
				keyboard : false,
				backdrop : 'static'
			});
		},

		closeModalWindow: function(idOfModalWindow){
			$(idOfModalWindow).modal("hide");
		},

		updateLoggedInUserBanner: function (loggedInUser){

			$(LOGGED_IN_USER_WELCOME_BANNER).text(loggedInUser);
		},

		showStartRecordingHomePageOption : function() {
			$(START_RECORDING_ELEMENTS).slideDown("slow");
		},

		showStopRecordingHomePageOption : function() {

			$(STOP_RECORDING_ELEMENTS).slideDown("slow");
		},

		hideStopRecordingHomePageOption : function() {
			$(STOP_RECORDING_ELEMENTS).slideUp(0);
		},

		hideStartRecordingHomePageOption : function() {
			$(START_RECORDING_ELEMENTS).slideUp(0);
		},

		hideElement : function(elementId){

			$(elementId).hide();
		},

		updateProxyDetailsView : function(proxyHost, proxyPort,userId) {
			$("#scan_input").hide();
			$("#proxy_values").show();
			$(ID_PROXY_HOST_DETAILS).text(proxyHost);
			$(ID_PROXY_PORT_DETAILS).text(proxyPort);

			if(userId){
				$("#contactProxyPerson").text("(User Id: "+userId+")");
			}
		},

		showRecordStartedDialog : function(){

			$(ID_APPLICATION_RECORDING_STARTED_NOTIFICATION_DIALOG).modal({
				keyboard : true,
				backdrop : 'static'
			});
		},

		/*
		 * showRecordInProgressDialog: function(){
		 *
		 * $('#recordInProgressDialog').modal({ keyboard : true, backdrop :
		 * 'static' }); },
		 */

		showfileTypeDialog: function(){

			$('#fileTypeDialog').modal({
				keyboard : true,
				backdrop : 'static'
			});

		},

		showDialog: function(idOfDialog, keyBoardDissmissable){

			// NOTE: from @author kshirali@ebay.com
			// Directly passing on keyBoardDissmissable boolean value to
			// 'keyboard' is not working. Weird !!
			// So this workaround.
			if(keyBoardDissmissable === true){
				$(idOfDialog).modal({
					keyboard : true,
					backdrop : 'static'
				});
			}
			else{
				$(idOfDialog).modal({
					keyboard : false,
					backdrop : 'static'
				});
			}
			$("#scan_input").show();
			$("#proxy_values").hide();
		},

		showErrorDialogASE: function(){

			'use strict';
			view.closeModalWindow('.modal');
			view.showDialog('#errorDialogASE', true);
		},

		showErrorOutlineForJIRAPRojectIdTextBox: function(){

			'use strict';

			if($('#jiraProjectKey') !== null
					&& $($('#jiraProjectKey') !== undefined
							&& $('#jiraProjectKey').parent() !== null
								&& $('#jiraProjectKey').parent() !== undefined
									&& !$('#jiraProjectKey').parent().hasClass('has-error'))){
				$('#jiraProjectKey').parent().addClass('has-error');
			}
		},

		removeErrorOutlineForJIRAPRojectIdTextBox: function(){

			'use strict';

			if($('#jiraProjectKey') !== null
					&& $($('#jiraProjectKey') !== undefined
							&& $('#jiraProjectKey').parent() !== null
								&& $('#jiraProjectKey').parent() !== undefined
									&& $('#jiraProjectKey').parent().hasClass('has-error'))){
				$('#jiraProjectKey').parent().removeClass('has-error');
			}
		}
};