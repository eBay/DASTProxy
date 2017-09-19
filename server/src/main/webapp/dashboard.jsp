<%@page import="com.dastproxy.common.utils.AppScanUtils"%>
<!DOCTYPE html>
<html ng-app="dashboardApp">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<link rel="icon" href="images/Interceptor.ico">

<title>DASTProxy 5.0 beta</title>

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
<link href="css/main.css" rel="stylesheet">
<script src="js/jquery-1.11.3.min.js"></script>
<script src="js/jquery-ui.min.js"></script>
<script src="js/angular.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/pagination/dirPagination.js"></script>
<script src="js/dashboard.js"></script>
<link href="css/data_table.css" rel="stylesheet">
<link href="css/overlay.css" rel="stylesheet">
<script>
	// The following JS code has to be moved to a new JS file and has to be re-used for all non-main pages
	var loggedInUser = '<%=AppScanUtils.getLoggedInUser().getUserId()%>';
	var selectedRecordingIds='';
	var batchId;
	var newRows = 0;
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
	});
	function startScanFromRecording (uniqueId) {
		$.ajax({
			type : "GET",
			url : "scanbatch/v1/" + uniqueId,
			async : true,
			dataType : "json",
			beforeSend: function () {
			    $('#scan_message').show().hide().show();
			},			
			success : function(serverRespone) {
				if (serverRespone.data == "success") {
					angular.element($("[ng-controller='ScanController']")).scope().refreshData();				
					angular.element($("[ng-controller='ScanController']")).scope().$apply();
				} else {
					$('#scan_message').hide();
					$('#scan_error').show();
				}
			},
			error : function() {
				$('#scan_error').show();
			}
		});

	}
	function scanSelectedRecordings () {
		selectedRecordingIds = selectedRecordingIds.replace(/,,+/g, '-').replace(/,+/g, '-');
		var url = "scanselectedrecordings/v1/" + selectedRecordingIds+"/"+batchId;
		closeNav();
		$.ajax({
			type : "GET",
			url : url,
			async : true,
			dataType : "json",
			beforeSend: function () {
			    $('#scan_message').show().hide().show();
			},			
			success : function(serverRespone) {
				if (serverRespone.data == "success") {
					angular.element($("[ng-controller='ScanController']")).scope().refreshData();									
					angular.element($("[ng-controller='ScanController']")).scope().$apply();					
				} else {
					$('#scan_message').hide();
					$('#scan_error').show();
				}
			},
			error : function() {
				$('#scan_error').show();
			}
		});
	}
	
	function addToNightlyRecordings () {
		selectedRecordingIds = selectedRecordingIds.replace(/,,+/g, '-').replace(/,+/g, '-');
		var url = "addtonightlyrecordingbatch/v1/" + selectedRecordingIds+"/"+batchId;
		closeNavNightly();
		$.ajax({
			type : "GET",
			url : url,
			async : true,
			dataType : "json",
			beforeSend: function () {
			    $('#scan_message_nightly').show().hide().show();
			},			
			success : function(serverRespone) {
				if (serverRespone.data == "success") {
					angular.element($("[ng-controller='ScanController']")).scope().refreshData();									
					angular.element($("[ng-controller='ScanController']")).scope().$apply();					
				} else {
					$('#scan_message_nightly').hide();
					$('#scan_error').show();
				}
			},
			error : function() {
				$('#scan_error').show();
			}
		});
	}
	
	function selectForScan(comp){
		batchId = comp.getAttribute("batchId");
		angular.element($("[ng-controller='SelectRecordingController']")).scope().setBatchId(batchId);
		angular.element($("[ng-controller='SelectRecordingController']")).scope().$apply();
		openNav();
	}
	function addToNightlyBatch(comp){
		batchId = comp.getAttribute("batchId");
		angular.element($("[ng-controller='SelectRecordingController']")).scope().setBatchId(batchId);
		angular.element($("[ng-controller='SelectRecordingController']")).scope().$apply();
		openNavNightly();
	}	
	
	function openNav() {
	    $('#overlayDiv').css("width", "100%");
	    $("#overlayDiv").addClass("overlay_border");
	    $("#add_to_nightly_sub").hide();
	    $("#start_scan_sub").show();
	    $("#overlay_header").html("Select Recordings To Start Scan");
	}

	function closeNav() {
	    $('#overlayDiv').css("width", "0%");
	    $("#overlayDiv").removeClass('overlay_border');
	    $("#start_scan_sub").hide();

	}	
	function openNavNightly() {
	    $('#overlayDiv').css("width", "100%");
	    $("#overlayDiv").addClass("overlay_border");
	    $("#add_to_nightly_sub").show();
	    $("#start_scan_sub").hide();
	    $("#overlay_header").html("Select Recordings To Move to Nightly Recording Batch");
	}

	function closeNavNightly() {
	    $('#overlayDiv').css("width", "0%");
	    $("#overlayDiv").removeClass('overlay_border');
	    $("#add_to_nightly_sub").hide();
	    
	}		
	function processSelection(comp){
		var selectedId = comp.getAttribute("recordingId");
		var selectedIndex = comp.getAttribute("index");
		if (comp.checked){
			selectedRecordingIds +=','+selectedId+',';
			angular.element($("[ng-controller='SelectRecordingController']")).scope().setCheckBoxState(selectedIndex, true);
			angular.element($("[ng-controller='SelectRecordingController']")).scope().$apply();
		} else {
			selectedRecordingIds = selectedRecordingIds.replace(','+selectedId+',','');
			angular.element($("[ng-controller='SelectRecordingController']")).scope().setCheckBoxState(selectedIndex, false);
			angular.element($("[ng-controller='SelectRecordingController']")).scope().$apply();
		}
	}
	
</script>
</head>
<body>

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

				<span class="navbar-brand"> <img style="max-width: 40px; max-heigth: 40px; margin-top: -7px;" src="images/imageMagnifier.jpg" /> <span>DAST Proxy 5.0 beta</span>

				</span>
			</div>
			<div class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li id="recordedScanTab" class="pointerCursor"><a id="recordedScanTabLink" href="/DASTProxy">Home</a></li>
					<li id="spiderScanTab" class="active pointerCursor"><a id="spiderScanTabLink" href="/DASTProxy/dashboard">Dashboard</a></li>
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
	<div id="scan_message" class="success" style="display:none;"><span>A scan is submitted from the selected recordings. Please check the status in the scans table below in few seconds.</span></div>
	<div id="scan_message_nightly" class="success" style="display:none;"><span>The selected scans are successfully moved to the nightly batch.</span></div>
	<div id="scan_error" class="error" style="display:none;"><span class="message">There is some problem in starting the scan. Please try again or contact the administrator.</span></div>
	<div ng-controller="TsRecController">
		<dir-pagination-controls boundary-links="true" on-page-change="pageChangeHandler(newPageNumber)" template-url="js/pagination/dirPagination.tpl.html" pagination-id="recording"></dir-pagination-controls>
		<h3>Test Suite Recordings</h3>
		<table class="CSSTableGenerator">
			<tr>
				<td>Test Suite Name</td>
				<td>Owner</td>
				<td>Move to Nightly Batch</td>
				<td>Scan Selected</td>
				<td>Scan All</td>
			</tr>
			<tr ng-if="batches.length === 0">
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td><span class="no_data">No data available!</span></td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<tr dir-paginate="batch in batches | filter:q | itemsPerPage: pageSize" current-page="currentPage" pagination-id="recording">
				<td >{{ batch.testsuiteName}}</td>			
				<td >{{ batch.owner }}</td>
				<td ><a href="javascript:void(0);" batchId="{{batch.id}}" onclick="addToNightlyBatch(this);">Move to Nightly Batch</a></td>
				<td ><a href="javascript:void(0);" batchId="{{batch.id}}" onclick="selectForScan(this);">Scan Selected</a></td>
				<td ><a href="javascript:void(0);" id="{{batch.id}}" onclick="startScanFromRecording(this.id);">Scan All</a></td>
			</tr>
		</table>
	</div>
	<div class='overlay' id='overlayDiv'>
		<a href='javascript:void(0)' class='closebtn' onclick='closeNav(this)'>x</a>
		<div ng-controller="SelectRecordingController" style="width:90%; align: center; margin-top: 100px;margin-left: 90px;line-height:inherit;" id="recordings_sec">
			<dir-pagination-controls boundary-links="true" on-page-change="pageChangeHandler(newPageNumber)" template-url="js/pagination/dirPagination.tpl.html" pagination-id="selected"></dir-pagination-controls>
			<h3 id='overlay_header'></h3>
			<table class="CSSTableGenerator">
				<tr>
					<td>Select</td>
					<td>Recording Name</td>
					<td>Owner</td>
					<td>Date Created</td>
				</tr>
				<tr ng-if="recordings.length === 0">
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td><span class="no_data">No data available!</span></td>
					<td>&nbsp;</td>
				</tr>
				<tr dir-paginate="recording in recordings | filter:q | itemsPerPage: pageSize" current-page="currentPage" pagination-id="selected">
					<td ><input type="checkbox" index="{{recording.index}}" ng-checked="{{recording.checked}}" recordingId="{{recording.id}}" onClick="processSelection(this)"/></td>
					<td >{{ recording.testcaseName}}</td>
					<td >{{ recording.owner}}</td>
					<td >{{ recording.dateCreated }}</td>
				</tr>
			</table>
		</div>
		<div id='start_scan_sub' class='submit_btn' onClick="scanSelectedRecordings()" style="display:none;">Start Scan of Selected Recordings</div>
		<div id='add_to_nightly_sub' class='submit_btn' onClick="addToNightlyRecordings()" style="display:none;">Move to Nightly Scan</div>
	</div>
	<div ng-controller="ScanController" id="sc_id">
		<dir-pagination-controls boundary-links="true" on-page-change="pageChangeHandler(newPageNumber)" template-url="js/pagination/dirPagination.tpl.html" pagination-id="scan"></dir-pagination-controls>		
		<h3>My Scans</h3>
		<table class="CSSTableGenerator" id='scan_table'>
			<tr>
				<td>Scan Name</td>
				<td>Created By</td>
				<td>Scan Run Date</td>
				<td>Status</td>
				<td>Report</td>
			</tr>
			<tr ng-if="batchscans.length === 0">
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td><span class="no_data">No data available!</span></td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>				
			<tr dir-paginate="sb in batchscans | filter:q | itemsPerPage: pageSize" current-page="currentPage" pagination-id="scan">
				<td>{{sb.testsuiteName}}</td>
				<td>{{sb.owner}}</td>
				<td>{{sb.dateCreated}}</td>
				<td>{{sb.displayStatus}}</td>
				<td><a href='scan_batch_report?scanBatchId={{sb.id}}'>Click here to go to report page</a></td>
			</tr>
		</table>
	</div>
</div>
</body>
</html>