<!DOCTYPE html>


<!-- @author Kiran Shirali (kshirali@ebay.com) 
	 
	 This is the home page of the application. This page is an interface that would allow users to go and set up a proxy for their use.
-->

<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<link rel="icon" href="images/Interceptor.ico">

<title>DASTProxy v2.0</title>

<!-- Bootstrap core CSS -->
<link href="css/bootstrap.min.css" rel="stylesheet">

<!-- Custom styles for this template -->
<link href="css/sticky-footer-navbar.css" rel="stylesheet">
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
					src="images/imageMagnifier.jpg" /> <span>DAST Proxy <sup>Beta</sup></span>

				</span>
			</div>
			<div class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li id="recordedScanTab" class="active pointerCursor"><a
						id="recordedScanTabLink">Recorded Scan</a></li>
					<!-- <li id="spiderScanTab" class="pointerCursor"><a
						id="spiderScanTabLink">Spider Scan</a></li> -->
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

	<!-- Begin page content -->
	<div id="divForRecordedScan" class="container">

		<div class="page-header">
			<h1>
				Welcome to DAST Proxy <sup>Beta</sup>
				<version> v2.0 </version>
			</h1>
		</div>
		<p class="lead leadBottom">

			Option #1: Record the subset of pages that you want tested <span>
				Please configure your browser/selenium proxy to the following. To
				learn how to configure, click <a href="https://wiki.vip.corp.ebay.com/display/SENA/2.+How+to+complete+manual+flow-based+testing+with+AppScan+Enterprise+and+DAST+Proxy" 
				target="_blank">here</a>: </span> <span id="proxyValues"
				class="proxyValues"> <span> <span> Proxy Host:
				</span><span data-original-title="Tooltip"
					title="This is the IP address of the proxy's server" id="proxyHost">
				</span>
			</span> <span> <span> Proxy Port: </span><span
					data-original-title="Tooltip"
					title="This is the port on which the proxy is listening to HTTP/HTTPS traffic"
					id="proxyPort"></span>
			</span>
			</span> <span id="startRecordingSpan"> <span> To start
					recording please press the below button: </span>

				<button type="button" id="proxyRecordAction" data-toggle="tooltip"
					data-placement="bottom"
					title="Start recording HTTP/HTTPS traffic going through proxy"
					class="btn btn-large">Record Data</button>

			</span> <span id="stopRecordingSpan"> <span> Proxy is
					recording at above address/port. To stop recording traffic please
					press the following: </span>
				<button type="button" id="proxyStopRecordAction"
					data-toggle="tooltip" data-placement="bottom"
					title="Stop recording HTTP/HTTPS traffic going through proxy"
					class="btn btn-large">Stop Recording</button> <span
				class="alert alert-danger recordingAlertText" role="alert"> <strong>Alert:</strong>

					Please note that the proxy is currently in recording stage <i
					id="contactProxyPerson"></i>. Accidently stopping the record would
					result in a loss of work.

			</span>
			</span>


		</p>
	</div>

	<!-- Begin page content -->
	<div id="divForSpiderScan" class="container">

		<div class="page-header">
			<h1>
				Welcome to DAST Proxy <sup>Beta</sup>
				<version> v2.0 </version>
			</h1>
		</div>

		<p class="lead">

			Option #2: Test your whole application <span> Set up a new
				scan for an application: </span>

			<button type="button" id="startNewSpideringScan"
				data-toggle="tooltip" data-placement="bottom"
				title="Set up a scan which will test your entire application"
				class="btn btn-large">Set Up New Scan</button>


		</p>
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
						<b id="dastProxyProgessModalTital">Initializing Application </b>
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

	<div class="modal fade" id="recordStartedDialog" tabindex='-1'>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<h4 class="modal-title">
						<b>Recording has started </b>
					</h4>
				</div>
				<div class="modal-body">Proxy has commenced recording all
					Traffic. Please configure your tests with the given details and
					commence tests.</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	<div class="modal fade" id="recordInProgressDialog" tabindex='-1'>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<h4 class="modal-title">
						<b>Recording is in progress</b>
					</h4>
				</div>
				<div class="modal-body">Proxy is already recording for this
					user. Please continue to test and click 'Stop Recording' when all
					tests have finished.</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	<div class="modal fade" id="recordingSubmittedToDASTToolDialog"
		tabindex='-1'>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<h4 class="modal-title">
						<b>Scan Set Up Successful</b>
					</h4>
				</div>
				<div class="modal-body">Scan has been set up on your account
					in IBM AppScan Enterprise. You will also receive a mail that has a
					link to your scan.</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	<div class="modal fade" id="htdCreationSuccessfulDialog" tabindex='-1'>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<h4 class="modal-title">
						<b>HTD Creation Successful</b>
					</h4>
				</div>
				<div class="modal-body">Your HTD file has been created. Please
					'upload' this file in the manual link section in an IBM AppScan
					Enterprise Scan settings.</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	<div class="modal fade" id="contactUsSuccessfulDialog" tabindex='-1'>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<h4 class="modal-title">
						<b>Message Sent Successfully</b>
					</h4>
				</div>
				<div class="modal-body">Your message has been sent to the
					team. Someone will get back to you shortly !!</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	<div class="modal fade" id="contactTeamDialog" tabindex='-1'>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						onclick="resetContactUsModalValues();">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<h4 class="modal-title">
						<b>Contact Us</b>
					</h4>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<label for="contactUsReason">Why are you contacting Us?</label> <select
							id="contactUsReason">
							<option value="bug">Bug</option>
							<option value="compliment">Compliment</option>
							<option value="feature">Feature Enhancement</option>
						</select>
					</div>
					<div class="form-group">
						<label for="contactUsReasonDesc">Description</label>
						<textarea class="form-control" maxlength="250"
							id="contactUsReasonDesc"
							placeholder="Enter description of why you are contacting us (Max 250 characters)"></textarea>
					</div>

				</div>
				<div class="modal-footer">
					<button type="button" id="submitContactUs" class="btn btn-primary">Submit</button>
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

	<div class="modal fade" id="errorDialogASE">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title">
						<b>Error</b>
					</h4>
				</div>
				<div class="modal-body">
					<span style="float: left; margin-right: 0.1 em;"> <img
						src="images/error.png" alt="error" height="92"
						width="82" />
					</span> 
					
					<span style="margin-left: 0.1 em;"><strong>OOPS !! There has been a critical error on IBM
						AppScan Enterprise and our team is working on it. For now, please
						manually configure your scan. 
						<br>
						<br>
						There could be a number of reasons:
						<br>
						1. The AppScan Server is not responsive.<br>
						2. All the recordings were done on external addresses (only internal addresses can be scanned). <br>
						3. No recording was done.
						</span>
						</strong>
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

	<div class="modal fade" id="fileTypeDialog" tabindex='-1'>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<h4 class="modal-title">
						<b>Has recording completed? What should be done next? </b>
					</h4>
				</div>
				<div class="modal-body">
					<div id="carouselForStopRecordingOptions" class="carousel slide"
						data-ride="carousel">
						<!-- Indicators -->
						<!-- <ol class="carousel-indicators">
							<li data-target="#carousel-example-generic" data-slide-to="0"
								class="active"></li>
							<li data-target="#carousel-example-generic" data-slide-to="1"></li>
							<li data-target="#carousel-example-generic" data-slide-to="2"></li>
						</ol>
 -->
						<!-- Wrapper for slides -->
						<div class="carousel-inner">
							<div id="stopRecordingCarousalOption1" class="item active">
								<img src="images/setUpScanOnAppScan.jpg"
									alt="Set on scan automatically on AppScan Enterprise">
								<!-- <div class="carousel-caption">...</div> -->
							</div>
							<div id="stopRecordingCarousalOption2" class="item">
								<img src="images/getHTDFile.jpg"
									alt="Get Recordings in HTD File format that IBM AppScan Enterprise can accept">
								<!-- <div class="carousel-caption">...</div> -->
							</div>
						</div>

						<!-- Controls -->
						<a class="left carousel-control"
							onclick="$('#carouselForStopRecordingOptions').carousel('prev')"
							role="button" data-slide="prev"> <span
							class="glyphicon glyphicon-chevron-left"></span>
						</a> <a class="right carousel-control"
							onclick="$('#carouselForStopRecordingOptions').carousel('next')"
							role="button" data-slide="next"> <span
							class="glyphicon glyphicon-chevron-right"></span>
						</a>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" onclick="actionOnCancelRecording();"
						class="btn btn-default">Cancel Recording</button>
					<button type="button" id="stopRecordingSubmitAction"
						class="btn btn-primary">Submit</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	<div class="modal fade" id="dastSpiderScanSetUp" tabindex='-1'>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<!-- <button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button> -->
					<h4 class="modal-title">
						<b id="dastProxyProgessModalTital">Set Up New Scan </b>
					</h4>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<label for="nameOfSpiderScan">Name of Scan</label> <input
							type="text" class="form-control" id="nameOfSpiderScan"
							placeholder="Enter name for the Spider Scan (Optional)">
					</div>
					<div class="form-group">
						<label for="spiderScanStartingUrl">Application URL</label> <input
							type="text" class="form-control" id="spiderScanStartingUrl"
							placeholder="Enter Application URL">
					</div>
					<!-- <div class="form-group">
						<label for="exampleInputFile">File input</label> <input
							type="file" id="exampleInputFile">
						<p class="help-block">Example block-level help text here.</p>
					</div>
					<div class="checkbox">
						<label> <input type="checkbox"> Check me out
						</label>
					</div> -->
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					<button type="button" class="btn btn-primary">Set Up Scan</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="js/jquery-1.11.3.min.js"></script>
	<script src="js/bootstrap.min.js"></script>

	<!-- 
		Using maxlength component from http://mimo84.github.io/bootstrap-maxlength
		to show reamining characters of text area
		@author kshirali@ebay.com
	 -->
	<script src="js/bootstrap-maxlength.min.js"></script>
	<script src="js/common/dastProxyConstants.js"></script>
	<script src="js/model/mainModel.js"></script>
	<script src="js/view/dastProxyMainView.js"></script>
	<script src="js/dastProxyMain.js"></script>
</body>
</html>
