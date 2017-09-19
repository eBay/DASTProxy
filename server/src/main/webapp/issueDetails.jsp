<!DOCTYPE html>


<!-- @author Kiran Shirali (kshirali@ebay.com) 
	 
	 This is the page where users will be allowed to see details of an issue
-->

<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<link rel="icon" href="/DASTProxy/images/Interceptor.ico">
<!-- Bootstrap core CSS -->
<link href="/DASTProxy/css/bootstrap.min.css" rel="stylesheet">

<!-- Custom styles for this template -->
<link href="/DASTProxy/css/sticky-footer-navbar.css" rel="stylesheet">
<link href="/DASTProxy/css/dastProxyBootstrapCustom.css"
	rel="stylesheet">


<link href="/DASTProxy/css/issueView.css" rel="stylesheet">


<!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
<!--[if lt IE 9]><script src="./DASTProxy/assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
<script src="./DASTProxy/assets/js/ie-emulation-modes-warning.js"></script>

<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="./DASTProxy/assets/js/ie10-viewport-bug-workaround.js"></script>

<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->


<title>DASTProxy 5.0 beta</title>
</head>

<body>
	<!-- Fixed navbar -->
	<div class="navbar navbar-default navbar-fixed-top" role="navigation">
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
					src="/DASTProxy/images/imageMagnifier.jpg" /> <span>DAST
						Proxy <sup>Beta</sup>
				</span>

				</span>
			</div>
			<div class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li id="securityIssueDetailsTab" class="active pointerCursor"><a
						id="securityIssueDetailsLink">Security Issue</a></li>
				</ul>

				<ul class="nav navbar-nav navbar-right">
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown">Welcome <span id="loggedInUserSpan">User</span>
							<span class="caret"></span></a>
						<ul class="dropdown-menu" role="menu">
							<li class="pointerCursor"><a id="helpLink">Help</a></li>
							<!-- <li class="pointerCursor"><a id="contactUsLink">Contact</a></li> -->
							<li class="divider"></li>
							<li class="pointerCursor"><a id="logoutLink">Logout</a></li>
						</ul></li>
				</ul>
			</div>
		</div>
	</div>

	<!-- Begin page content -->
	<div id="divForIssueDetails" class="container flowingContainer">
		<div class="lead">
			<div class="divTable">

				<div class="divRow">
					<div class="divCell divCellLabel issueLabelText">Issue Id:</div>
					<div class="divCell issueDetailsText" id="issueIdDiv">N/A</div>
					<div class="divCell divCellLabel issueLabelText">Status:</div>
					<div class="divCell wordwrap issueDetailsText" id="issueStatusDiv">N/A</div>
				</div>
				<div class="divRow">
					<div class="divCell divCellLabel issueLabelText">Security
						Type:</div>
					<div class="divCell wordwrap issueDetailsText" id="issueTypeDiv">N/A</div>
					<div class="divCell divCellLabel issueLabelText">Severity:</div>
					<div class="divCell issueDetailsText" id="issueSeverityDiv">N/A</div>
				</div>
				<div class="divRow">
					<div class="divCell divCellLabel issueLabelText">Test Case
						Name:</div>
					<div class="divCell issueDetailsText" id="testCaseNameDiv">N/A</div>
					<div class="divCell divCellLabel issueLabelText">Test Suite
						Name:</div>
					<div class="divCell issueDetailsText" id="testSuiteNameDiv">N/A</div>
				</div>
				<div class="divRow">
					<div class="divCell issueLabelText">Test Url:</div>
					<div class="divCell wordwrap threeFourthWifth issueDetailsText"
						id="issueTestUrlDiv">N/A</div>
				</div>
			</div>
		</div>

		<div class="divTable">
			<div class="panel panel-default" id="panel1">
				<div class="panel-heading">
					<h4 class="panel-title issueLabelText">
						<a data-toggle="collapse" data-target="#collapseOne"
							href="#collapseOne" class="collapsed"> Variants/Successully Exploitable Payloads </a>
					</h4>
				</div>
				<div id="collapseOne" class="panel-collapse collapse">
					<div class="panel-body">
						<pre>Below are the sections in which you will see the traffic data based on which the tool has confirmed that there is an exploitable vulnerability.</pre>
						<b>Payload List:</b>
						<br/>
						<ul class="pagination pagination-sm" id="payloadVariantPaginationList">
							No Payload Data Available
						</ul>
						<br/>
						<b>Reasoning as to why this is a bug:</b>
						<br/>
						<br/>
						<pre id="differencePreBlock">No Data Available.</pre>	
					</div>
				</div>
			</div>

			<div class="panel panel-default" id="panel2">
				<div class="panel-heading">
					<h4 class="panel-title issueLabelText">
						<a data-toggle="collapse" data-target="#collapseTwo"
							href="#collapseTwo" class="collapsed"> Test HTTP Traffic </a>
					</h4>
				</div>
				<div id="collapseTwo" class="panel-collapse collapse">
					<div class="panel-body">
						<pre id="testHttpTrafficPreBlock">
							<img width="25%" height="25%" src="images/comingSoon.png" />
						</pre>
					</div>
				</div>
			</div>

			<div class="panel panel-default" id="panel3">
				<div class="panel-heading">
					<h4 class="panel-title issueLabelText">
						<a data-toggle="collapse" data-target="#collapseThree"
							href="#collapseThree" class="collapsed"> Original HTTP
							Traffic </a>
					</h4>
				</div>
				<div id="collapseThree" class="panel-collapse collapse">
					<div class="panel-body">
						<pre id="originalHttpTrafficPreBlock">
							<img width="25%" height="25%" src="images/comingSoon.png" />	
						</pre>
					</div>
				</div>
			</div>


		</div>
		<button type="button" id="publishToJIRAAction" data-toggle="tooltip"
			data-placement="bottom" title="Publish to JIRA"
			class="btn btn-large publishToJiraButton">Publish to JIRA</button>

	</div>

	<div id="divForNoIssueDetailsFound" class="container flowingContainer">
		<div class="row">
			<div class="col-md-12">
				<div class="error-template">
					<h1>Oops!</h1>
					<h2>Issue Not Found</h2>
					<div class="error-details">Sorry, an error has occured.
						Either the issue is not present or you are not authorized to see
						details of the issue.</div>
				</div>
			</div>
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

	<div class="modal fade" id="dastProxyProgessModal" tabindex='-1'>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title">
						<b id="dastProxyProgessModalTital">Retrieving data for the
							security issue </b>
					</h4>
				</div>
				<div class="modal-body">
					<div class="progress">
						<div id="dastProxyProgessModalProgessBar"
							class="progress-bar progress-bar-striped active"
							role="progressbar" aria-valuenow="45" aria-valuemin="0"
							aria-valuemax="100" style="width: 45%">
							<span class="sr-only">45% Complete</span>
						</div>
					</div>
				</div>
				<!-- <div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					<button type="button" class="btn btn-primary">Save changes</button>
				</div> -->
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	<div class="modal fade" id="helpDialog" tabindex="-1">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<h4 class="modal-title">
						<b>Help</b>
					</h4>
				</div>
				<div class="modal-body">
					<div id="carousel-example-generic" class="carousel slide"
						data-ride="carousel">
						<!-- Indicators -->
						<ol class="carousel-indicators">
							<li data-target="#carousel-example-generic" data-slide-to="0"
								class="active"></li>
							<li data-target="#carousel-example-generic" data-slide-to="1"></li>
							<!-- <li data-target="#carousel-example-generic" data-slide-to="2"></li> -->
						</ol>

						<!-- Wrapper for slides -->
						<div class="carousel-inner">
							<div class="item active">
								<img width="100%" height="100%" src="images/comingSoon.png" />
								<!-- <div class="carousel-caption">...</div> -->
							</div>
							<div class="item">
								<img width="100%" height="100%" src="images/comingSoon.png" />
								<!-- <div class="carousel-caption">...</div> -->
							</div>
						</div>

						<!-- Controls -->
						<a class="left carousel-control" role="button" data-slide="prev">
							<span class="glyphicon glyphicon-chevron-left"></span>
						</a> <a class="right carousel-control" role="button" data-slide="next">
							<span class="glyphicon glyphicon-chevron-right"></span>
						</a>
					</div>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	<div class="modal fade" id="dastProxySubmitToJiraModal" tabindex='-1'>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title">
						<b id="dastProxyProgessModalTital">Publish security bug to
							eBay JIRAP Project </b>
					</h4>
				</div>
				<div class="modal-body">

					<div class="alert alert-warning" role="alert">
						<strong> Heads Up! </strong> A service account will be used to
						publish to JIRA. Please ensure that the type security is enabled
						for your project before trying to publish an issue.
					</div>
					<div class="form-group">
						<label for="nameOfSpiderScan" class="control-label">eBay
							Jira Project Key:</label> <input type="text" class="form-control"
							id="jiraProjectKey" placeholder="Enter the eBay JIRA Project key">
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
	<!-- /.modal -->

	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="/DASTProxy/js/jquery-1.11.3.min.js"></script>
	<script src="/DASTProxy/js/bootstrap.min.js"></script>

	<!-- 
		Using maxlength component from http://mimo84.github.io/bootstrap-maxlength
		to show reamining characters of text area
		@author kshirali@ebay.com
	 -->
	<script src="/DASTProxy/js/bootstrap-maxlength.min.js"></script>
	<script src="/DASTProxy/js/common/dastProxyConstants.js"></script>
	<script src="/DASTProxy/js/model/mainModel.js"></script>
	<script src="/DASTProxy/js/view/dastProxyMainView.js"></script>
	<script src="/DASTProxy/js/URI.js"></script>
	<script src="/DASTProxy/js/issuesMain.js"></script>
</body>





</html>
