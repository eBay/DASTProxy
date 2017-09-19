/*
 * This is the 'MODEL' part of my MVC oriented JavaScript Architecture. This file contains all the functions that would send GET and POST
 * requests to my server.
 *
 *
 *
 */

var model = {

	getProxy : function(proxyIdentificationEntity) {
		recordingName = $('#recording_name').val();
		$('#recording_name').val("");

		var responseJsonObject = null;
		$.ajax({
			type : "POST",
			url : "rest/v1/proxyui",
			async : false,
			headers : {
				"Content-Type" : "application/json"
			},
			data : JSON.stringify(proxyIdentificationEntity),
			success : function(serverRespone) {
				responseJsonObject = JSON.parse(serverRespone);

			},
			error : function() {
				actionOnError();
			}
		});

		return responseJsonObject.data;
	},

	getProxyEntity : function(entity) {

	},

	getLoggedInUser : function() {

		var responseJsonObject = null;
		$.ajax({
			type : "GET",
			url : "rest/v1/user",
			async : false,
			dataType : "json",
			success : function(serverRespone) {
				responseJsonObject = serverRespone;
			},
			error : function() {
				actionOnError();
			}
		});

		return responseJsonObject.data;

	},

	getUserHtd : function(userId) {

		var responseJsonObject = null;
		$.ajax({
			type : "GET",

			url : "rest/v1/har/" + userId,
			async : false,
			dataType : "json",
			success : function(serverRespone) {
				responseJsonObject = serverRespone;
			},
			error : function() {
				actionOnError();
			}
		});

		return responseJsonObject.data;

	},

	getProxyRunningStatus : function(userId) {

		var responseJsonObject = null;
		$.ajax({
			type : "GET",
			url : "rest/v1/proxy/" + userId,
			async : false,
			dataType : "json",
			success : function(serverRespone) {
				responseJsonObject = serverRespone;
			},
			error : function() {
				actionOnError();
			}
		});

		return responseJsonObject.data;

	},

	getScanSetUp : function(userId, recordingName1, testsuiteName1) {
		if (recordingName1 ==''){
			recordingName1='none';
		}
		if (testsuiteName1 ==''){
			testsuiteName1='none';
		}

		var responseJsonObject = null;
		$.ajax({
			type : "GET",
			url : "rest/v1/security/" + userId + "/" + recordingName1+ "/" + testsuiteName1,
			async : false,
			dataType : "json",
			success : function(serverRespone) {
				responseJsonObject = serverRespone;
				if (responseJsonObject.error !== null
						&& responseJsonObject.error !== undefined) {

					window.open("rest/v1/htd/" + responseJsonObject.error);
					//view.showErrorDialogASE();
					//return responseJsonObject;
				}
			},
			error : function() {
				actionOnError();
			}
		});

		if (responseJsonObject.error !== null
				&& responseJsonObject.error !== undefined) {
			return responseJsonObject;
		}else{
			return responseJsonObject.data;
		}


	},

	logoutUser : function(userId) {

		//var responseJsonObject = null;
		$.ajax({
			type : "GET",
			url : "j_spring_security_logout",
			async : true,
			success : function(serverRespone) {
				//responseJsonObject = JSON.parse(serverRespone);
			},
			error : function() {
				actionOnError();
			}
		});

		//return responseJsonObject.data;

	},

	cancelScanRecording : function(userId) {

		var responseJsonObject = null;
		$.ajax({
			type : "POST",
			url : "rest/v1/proxy/cancel/" +userId,
			async : false,
			headers : {
				"Content-Type" : "application/json"
			},
			success : function(serverRespone) {
				responseJsonObject = serverRespone;
			},
			error : function() {
				actionOnError();
			}
		});

		return responseJsonObject.data;

	},

	contactUs : function(contactUsObject){
		var responseJsonObject = null;
		$.ajax({
			type : "POST",
			url : "rest/v1/contactus",
			async : false,
			headers : {
				"Content-Type" : "application/json"
			},
			data : JSON.stringify(contactUsObject),
			success : function(serverRespone) {

				responseJsonObject = JSON.parse(serverRespone);
			},
			error : function() {
				actionOnError();
			}
		});

		return responseJsonObject.data;
	},

	getIssue: function(reportId, issueId){
		var responseJsonObject = null;
		$.ajax({
			type : "GET",
			url : "rest/v1/reportnew/"+reportId+"/issuenew/"+issueId,
			async : false,
			dataType : "json",
			success : function(serverRespone) {
				responseJsonObject = serverRespone;
			},
			error : function() {
				actionOnError();
			}
		});

		return responseJsonObject.data;

	},

	getJiraId : function(jiraProjectId, reportId, issueId){

		var responseJsonObject = null;
		$.ajax({
			type : "GET",
			url : "rest/v1/jira/project/"+jiraProjectId+"/reportnew/"+reportId+"/issuenew/"+issueId,
			async : false,
			dataType : "json",
			success : function(serverRespone) {
				responseJsonObject = serverRespone;
			},
			error : function() {
				actionOnError();
			}
		});
		return responseJsonObject.data;
	}

};