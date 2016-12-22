<%@page import="com.dastproxy.common.utils.AppScanUtils"%>
<!DOCTYPE html>
<html ng-app="batchReportApp">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<link rel="icon" href="images/Interceptor.ico">

<title>DASTProxy v3.0</title>

<!-- Bootstrap core CSS -->
<link href="css/bootstrap.min.css" rel="stylesheet">

<!-- Custom styles for this template -->
<link href="css/dastProxyBootstrapCustom.css" rel="stylesheet">

<!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
<!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
<script src="../../assets/js/ie-emulation-modes-warning.js"></script>

<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="../../assets/js/ie10-viewport-bug-workaround.js"></script>

<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
<link href="css/data_table.css" rel="stylesheet">
<link href="css/main.css" rel="stylesheet">
<link href="css/overlay.css" rel="stylesheet">
<script src="js/jquery-1.11.3.min.js"></script>
<script src="js/angular.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/pagination/dirPagination.js"></script>
<script src="js/common/dastProxyConstants.js"></script>
<script src="js/model/mainModel.js"></script>
<script src="js/view/dastProxyMainView.js"></script>
<script src="js/URI.js"></script>

<script>
	var scanBatchId='${scanBatchId}';
	var selectedIssueId;
	var selectedReportId;

	// The following JS code has to be moved to a new JS file and has to be re-used for all non-main pages
	var loggedInUser = '<%=AppScanUtils.getLoggedInUser().getUserId()%>';
	var timeFormatOptions = {weekday:"short", year: "numeric", month:"short", day: "numeric", hour:"2-digit", minute:"2-digit", second: "2-digit"};

   	$(document).ready(function() {
		if (loggedInUser !== null && loggedInUser !== undefined) {
			$("#loggedInUserSpan").text(loggedInUser);
		}
		function showDialog(idOfDialog, keyBoardDissmissable){

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
		}
	
	$('#contactUsLink').click(function(clickEvent) {

		showDialog('#contactTeamDialog',false);
	});

	$('#helpLink').click(function(clickEvent) {

		showDialog('#helpDialog',true);
	});

	$("#logoutLink").click(function(clickEvent) {
		window.location.href = "j_spring_security_logout";
	});	


		$('#cancelPublishToJiraActionModalButton').click(
		function(clickEvent) {
			'use strict';

			$('#jiraProjectKey').val('');
			$('#jiraProjectDisplayDiv').hide();

		});

		$('#publishToJiraActionModalButton').click(
		function(clickEvent) {
			'use strict';

			if ($('#jiraProjectKey').val() !== null
					&& $('#jiraProjectKey').val() !== undefined
					&& $('#jiraProjectKey').val() !== "") {
				view.removeErrorOutlineForJIRAPRojectIdTextBox();
				view.closeModalWindow(ID_SUBMIT_TO_JIRA_MODAL_WINDOW);
				view.showApplicationPrgoressModalWindow("Publishing data to JIRA","50%");
				var publishedJiraObject = model.getJiraId($('#jiraProjectKey').val(),selectedReportId,selectedIssueId);
				if (publishedJiraObject !== null && publishedJiraObject !== undefined
						&& publishedJiraObject.self !== null
						&& publishedJiraObject.self !== undefined) {
					$("#issueStatusDiv").text("Open (Jira Id:" + publishedJiraObject.key + ")");
				}

				console.log(publishedJiraObject);

				view.closeModalWindow(ID_APPLICATION_PROGRESS_BAR_MODAL_WINDOW);
				view.showDialog(ID_SUBMIT_TO_JIRA_SUCCESSFUL_NOTIFICATION_DIALOG,true);
				setTimeout(
					function() {
						view.closeModalWindow(ID_SUBMIT_TO_JIRA_SUCCESSFUL_NOTIFICATION_DIALOG);
					},
					DIALOG_BOX_VISIBLE_TIME_MILLISECONDS + 2000);
			} else {
				view.showErrorOutlineForJIRAPRojectIdTextBox();
			}

		});

		$('#jiraProjectKey').keyup(function() {

			if ($('#jiraProjectKey').val() == '') {

				if ($('#jiraProjectDisplayDiv').is(":visible")) {
					$('#jiraProjectDisplayDiv').hide()
				}
			} else {

				if ($('#jiraProjectDisplayDiv').is(":hidden")) {
					$('#jiraProjectDisplayDiv').show()
				}
				$('#jiraProjectSpan').text($(this).val());
			}

		});	
	});
	function openNav(elt) {
		    $('body').css('foreground-color','red');

	    //$('body').addClass('html_overlay');
	    document.getElementById(elt.parentNode.children[0].id).style.width = "100%";
	    $(elt.parentNode.children[0]).addClass('overlay_border');
	}

	function closeNav(elt) {
	    $('body').removeClass('html_overlay');
	    document.getElementById(elt.parentNode.id).style.width = "0%";
	    $(elt.parentNode).removeClass('overlay_border');
	}	
	function openJIRADialog(comp){
	selectedIssueId = comp.getAttribute("issueId");
	selectedReportId = comp.getAttribute("reportId");
	
	'use strict';
		$(ID_SUBMIT_TO_JIRA_MODAL_WINDOW).modal({
		keyboard : false,
		backdrop : 'static'
		});
	}	
</script>
<script src="js/batch_report_table.js"></script>
</head>
<body>
	<div class="modal fade" id="dastProxySubmitToJiraModal" tabindex='-1'>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title">
						<b id="dastProxyProgessModalTital">Publish security bug to JIRAP Project </b>
					</h4>
				</div>
				<div class="modal-body">

					<div class="alert alert-warning" role="alert">
						<strong> Heads Up! </strong> A service account will be used to
						publish to JIRA. Please ensure that the type security is enabled
						for your project before trying to publish an issue.
					</div>
					<div class="form-group">
						<label for="nameOfSpiderScan" class="control-label">Jira Project Key:</label> <input type="text" class="form-control"
							id="jiraProjectKey" placeholder="Enter the JIRA Project key">
					</div>

					<div id="jiraProjectDisplayDiv" style="display: none">
						https://JIRA_URL.com/jira/browse/<span id="jiraProjectSpan"></span>
					</div>
					<!-- <img width="100%" height="100%" src="images/comingSoon.png" />
					 -->


				</div>

				<div class="modal-footer">
					<button id="cancelPublishToJiraActionModalButton" type="button"
						class="btn btn-default" data-dismiss="modal">Cancel</button>
					<button id="publishToJiraActionModalButton" type="button"
						class="btn btn-primary">Publish</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	<div class="modal fade" id="errorInPageDialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title">
						<b>Error</b>
					</h4>
				</div>
				<div class="modal-body">
					<img width="100%" height="100%" src="images/errorPopUp.jpg" />
				</div>
				<div class="modal-footer">
					<button type="button" onclick="location.reload();"
						class="btn btn-primary">Start Over</button>
				</div>
			</div>

			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	<div class="modal fade" id="issueSubmittedSeccussfullyToJira"
		tabindex='-1'>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<h4 class="modal-title">
						<b>JIRA Submission Successful</b>
					</h4>
				</div>
				<div class="modal-body">Issue details has been successfully
					published to the JIRA Project. You will get an email with a link to
					the JIRA issue.</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>

	<!-- Fixed navbar -->
	<div class="navbar navbar-default navbar-fixed" role="navigation">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>

				<span class="navbar-brand"> <img
					style="max-width: 40px; max-heigth: 40px; margin-top: -7px;"
					src="images/imageMagnifier.jpg" /> <span>DAST Proxy 3.0</span>

				</span>
			</div>
			<div class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li id="recordedScanTab" class="pointerCursor"><a id="recordedScanTabLink" href="/DASTProxy">Home</a></li>
					<li id="spiderScanTab" class="pointerCursor"><a id="spiderScanTabLink" href="/DASTProxy/dashboard">Dashboard</a></li>
					<li id="spiderScanTab" class="active pointerCursor"><a id="spiderScanTabLink" href="/DASTProxy/scan_batch_report?scanBatchId=${scanBatchId}">Scan Batch Report</a></li>

				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown">Welcome <span id="loggedInUserSpan">User</span>
							<span class="caret"></span></a>
						<ul class="dropdown-menu" role="menu">
							<li class="pointerCursor"><a id="helpLink">Help</a></li>
							<li class="pointerCursor"><a id="contactUsLink">Contact</a></li>
							<li class="divider"></li>
							<li class="pointerCursor"><a id="logoutLink">Logout</a></li>
						</ul></li>
				</ul>
			</div>
		</div>
	</div>
<div class="container">
	<div ng-controller="ScanController" id="sc_id">
		<dir-pagination-controls boundary-links="true" on-page-change="pageChangeHandler(newPageNumber)" template-url="js/pagination/dirPagination.tpl.html" pagination-id="scan"></dir-pagination-controls>		
		<h3>Scans of the Batch</h3>
		<table class="CSSTableGenerator">
			<tr>
				<td>Testcase Name</td>
				<td>Scan Run Date</td>
				<td>Breeze?</td>
				<td>Status</td>
			</tr>
			<tr ng-if="scans.length === 0">
				<td>&nbsp;</td>
				<td><span class="no_data">No data available!</span></td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>					
			<tr dir-paginate="scan in scans | filter:q | itemsPerPage: pageSize" current-page="currentPage" pagination-id="scan">
				<td >{{ scan.testCaseName }}</td>
				<td >{{ scan.scanLastRun }}</td>
				<td >{{ scan.setUpViaBluefin?'Yes':'No'}}</td>
				<td >{{ scan.scanState}}</td>
			</tr>
		</table>
	</div>
	<div ng-controller="ReportController" style="height: 400px;">
		<dir-pagination-controls boundary-links="true" on-page-change="pageChangeHandler(newPageNumber)" template-url="js/pagination/dirPagination.tpl.html" pagination-id="scan_report_pagination"></dir-pagination-controls>
		<h3>Issues of the Scan Batch</h3>
		<table class="CSSTableGenerator">
			<tr>
				<td>Test Case Name</td>
				<td>Priority</td>
				<td>Vulnerability</td>
				<td>Test URL</td>
				<td>Test HTTP traffic</td>
				<td>Original HTTP traffic</td>
				<td>JIRA</td>
			</tr>
			<tr ng-if="issues.length === 0">
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td><span class="no_data">No data available!</span></td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>					
			<tr dir-paginate="issue in issues | filter:q | itemsPerPage: pageSize" current-page="currentPage" pagination-id="scan_report_pagination">
				<td >{{ issue.testcaseName}}</td>
				<td ng-if="issue.severity=='High'" style="background-color:red">{{ issue.severity }}</td>
				<td ng-if="issue.severity=='Medium'" style="background-color:#FBB917">{{ issue.severity }}</td>
				<td ng-if="issue.severity=='Low'" style="background-color:#cfcf00">{{ issue.severity }}</td>
				<td ng-if="issue.severity=='Information'" style="background-color:#eeee00">{{ issue.severity }}</td>
				<td >{{ issue.issueType}}</td>
				<td ><a target="_blank" href="{{issue.testUrl}}">{{issue.testUrl}}</a></td>
				<td>
					<div class='overlay' id='{{issue.testHTTPDivId}}'><a href='javascript:void(0)' class='closebtn' onclick='closeNav(this)'>x</a><pre class='panel-body'>{{issue.testHTTPtraffic}}</pre></div>
					<a href='javascript:void(0);' onclick='openNav(this)'>Click to Open</a>
				</td>
				<td>
					<div class='overlay' id='{{issue.origHTTPDivId}}'><a href='javascript:void(0)' class='closebtn' onclick='closeNav(this)'>x</a><pre class='panel-body'>{{issue.origHTTPtraffic}}</pre></div>
					<a href='javascript:void(0);' onclick='openNav(this)'>Click to Open</a>
				</td>
				
				<td ng-if="issue.jiraURL==null"><a href='javascript:void(0);' reportId='{{issue.reportId}}' issueId='{{issue.issueId}}' onclick='openJIRADialog(this)'>Publish to JIRA</a></td>
				<td ng-if="issue.jiraURL!=null"><a target="_blank" href='https://JIRA_URL.com/browse/{{issue.jiraURL}}'>Open in JIRA</a></td>
			</tr>
		</table>
	</div>
</div>
<div class="footer">
	<div class="container">
		<p class="text-muted">
			Copyright &#169; 1995-2015 eBay Inc. All Rights Reserved <br />
			CONFIDENTIALITY NOTICE: This website is intended only for eBay Inc.
			employees, and may contain information that is privileged,
			confidential and exempt from disclosure under applicable law. Use of
			this website constitutes acceptance of our Code of Business Conduct,
			Privacy Policy and eBay Mutual Nondisclosure Agreement.
		</p>
	</div>
</div>
</body>
</html>