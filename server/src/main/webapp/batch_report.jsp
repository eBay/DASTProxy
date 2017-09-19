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

<title>DASTProxy 5.0 beta</title>

<link href="css/bootstrap.min.css" rel="stylesheet">

<link href="css/dastProxyBootstrapCustom.css" rel="stylesheet">

<script src="../../assets/js/ie-emulation-modes-warning.js"></script>

<script src="../../assets/js/ie10-viewport-bug-workaround.js"></script>

<link href="css/data_table.css" rel="stylesheet">
<link href="css/main.css" rel="stylesheet">
<link href="css/overlay.css" rel="stylesheet">
<script src="js/jquery-1.11.3.min.js"></script>
<script src="js/angular.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/pagination/dirPagination.js"></script>
<script src="js/common/dastProxyConstants.js"></script>
<script src="js/model/mainModelNew.js"></script>
<script src="js/view/dastProxyMainView.js"></script>
<script src="js/URI.js"></script>

<script>
	var scanBatchId='${scanBatchId}';
	var selectedIssueId;
	var selectedReportId;
	var commentsIssueId;

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

	function markFP(comp){
		commentsIssueId = comp.getAttribute("issueId");
		selectedReportId = comp.getAttribute("reportId");
		var textAreaComp = $('textarea[commentsIssueId='+commentsIssueId+']');
		var dropdownComp = $('select[reasonIssueId='+commentsIssueId+']');
		//alert("dropdownComp.val()="+dropdownComp.val());
		if (dropdownComp.val() == -1){
			alert("Please select a reason to mark the issue as false positive.");
			return false;
		}

		var comments = textAreaComp.val();

		if (dropdownComp.val() == 1000 && comments.trim().length==0){
			alert("Please select either the false positive reason or enter the comments.");
			return false;
		}
		var data = {issueId:commentsIssueId, reportId : selectedReportId, comments:comments, fpReasonId : dropdownComp.val()};
		$.ajax({
			type : "POST",
			url : "rest/v1/mark_fp",
			async : false,
			headers : {
				"Content-Type" : "application/json"
			},
			data : JSON.stringify(data),
			success : function(serverRespone) {
				responseJsonObject = JSON.parse(serverRespone);
				textAreaComp.replaceWith(comments);
				comp.hide();
			},
			error : function() {
				alert('There is some problem in updating the comments.');
			}
		});
	}
	function showAll(showAllflag){
		angular.element($("[ng-controller='ReportController']")).scope().showAll(showAllflag);
		angular.element($("[ng-controller='ReportController']")).scope().$apply();
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
						https://jira.hostname.domain.com/jira/browse/<span id="jiraProjectSpan"></span>
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
		</div>
	</div>

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
					src="images/imageMagnifier.jpg" /> <span>DAST Proxy 5.0 beta</span>

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
				<td ng-if="scan.scanState!='Suspended'">{{ scan.scanState}}</td>
				<td ng-if="scan.scanState=='Suspended'">{{ scan.suspendedReason}}</td>
			</tr>
		</table>
	</div>
	<div ng-controller="ReportController" style="height: 400px;">
		<dir-pagination-controls boundary-links="true" on-page-change="pageChangeHandler(newPageNumber)" template-url="js/pagination/dirPagination.tpl.html" pagination-id="scan_report_pagination"></dir-pagination-controls>
		<h3>Issues of the Scan Batch</h3>
		<div><input type="checkbox" onchange="showAll(this.checked)" id="ng-change-example1" /> Display Omitted Results</div>
		<table class="CSSTableGenerator">
			<tr>
				<td>Details (Opens in New Window)</td>
				<td>Test Case Name</td>
				<td>Priority</td>
				<td>Vulnerability</td>
				<td>Test URL</td>
				<td>Scan Engine</td>
				<td>Test HTTP traffic</td>
				<td>Original HTTP traffic</td>
				<td>JIRA</td>
				<td>False Positive Reason</td>
				<td>FP Comments</td>
				<td>Mark False Positive</td>

			</tr>
			<tr ng-if="issues.length === 0">
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td><span class="no_data">No issues found!</span></td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>

			</tr>
			<tr dir-paginate="issue in issues | filter:q | itemsPerPage: pageSize" current-page="currentPage" pagination-id="scan_report_pagination">
				<td ><a target="_blank" href="{{issue.issueDastUrl}}">{{issue.issueId}}</a></td>
				<td >{{ issue.testcaseName}}</td>
				<td ng-if="issue.severity=='High'" style="background-color:red">{{ issue.severity }}</td>
				<td ng-if="issue.severity=='Medium'" style="background-color:#FBB917">{{ issue.severity }}</td>
				<td ng-if="issue.severity=='Low'" style="background-color:#cfcf00">{{ issue.severity }}</td>
				<td ng-if="issue.severity=='Information'" style="background-color:#eeee00">{{ issue.severity }}</td>
				<td ng-if="issue.severity=='Informational'" style="background-color:#eeee00">{{ issue.severity }}</td>

				<td >{{ issue.issueType}}</td>
				<td ><a target="_blank" href="{{issue.testUrl}}">{{issue.testUrl}}</a></td>
				<td >{{issue.scanEngine}}</td>
				<td>
					<div class='overlay' id='{{issue.testHTTPDivId}}'><a href='javascript:void(0)' class='closebtn' onclick='closeNav(this)'>x</a><pre class='panel-body'>{{issue.testHTTPtraffic}}</pre></div>
					<a href='javascript:void(0);' onclick='openNav(this)' ng-if="issue.scanEngine!='ZAP'">Click to Open</a>
				</td>
				<td>
					<div class='overlay' id='{{issue.origHTTPDivId}}'><a href='javascript:void(0)' class='closebtn' onclick='closeNav(this)'>x</a><pre class='panel-body'>{{issue.origHTTPtraffic}}</pre></div>
					<a href='javascript:void(0);' onclick='openNav(this)' ng-if="issue.scanEngine!='ZAP'">Click to Open</a>
				</td>
				<td ng-if="issue.jiraURL==null"><a href='javascript:void(0);' reportId='{{issue.reportId}}' issueId='{{issue.issueId}}' onclick='openJIRADialog(this)'>Publish to JIRA</a></td>
				<td ng-if="issue.jiraURL!=null"><a target="_blank" href='https://jira.hostname.domain.com/browse/{{issue.jiraURL}}'>Open in JIRA</a></td>
				<td>
				    <select name="fpReason" ng-if="(issue.severity=='High' || issue.severity=='Medium') && issue.jiraURL==null && !issue.fp" id="singleSelect" reasonIssueId='{{issue.issueId}}' ng-disabled='{{issue.fp}}'>
				      <option value="-1">---Please select---</option>
				      <option value="1" ng-selected="{{issue.fpReasonId==1}}">Error Page - PNR</option>
				      <option value="2" ng-selected="{{issue.fpReasonId==2}}">Error Page - Invalid char</option>
				      <option value="1000" ng-selected="{{issue.fpReasonId==1000}}">Other</option>
				    </select>
				</td>
				<td>
					<textarea ng-if="(issue.severity=='High' || issue.severity=='Medium') && issue.jiraURL==null && !issue.fp" commentsIssueId='{{issue.issueId}}'></textarea>
					<span ng-if="!((issue.severity=='High' || issue.severity=='Medium') && issue.jiraURL==null&& !issue.fp)">{{issue.fpComments}}</span>
				</td>
				<td width="8%">
					<div ng-if="(issue.severity=='High' || issue.severity=='Medium') && issue.jiraURL==null && !issue.fp" id="mark_fp_button"><a href="javascript:void(0)" issueId='{{issue.issueId}}' reportId='{{issue.reportId}}' onclick="javascript:markFP(this);"><span class="mark_fp">Mark as FP</span></a></div>
				</td>
			</tr>
		</table>
	</div>
</div>
</body>
</html>
