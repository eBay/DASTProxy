var loggedInUser;
var issue;

$(document)
		.ready(
				function() {
					'use strict;'

					var uri = URI();

					$('#divForNoIssueDetailsFound').hide();

					if (uri.hasQuery("report") && uri.hasQuery("issue")) {

						var queryParams = uri.search(true);
						issue = setUpIssueDetails(queryParams.report,
								queryParams.issue);

					}

					$('#helpLink').click(function(clickEvent) {

						view.showDialog(ID_APPLICATION_HELP_DIALOG, true);
					});

					$("#logoutLink").click(function(clickEvent) {
						window.location.href = "j_spring_security_logout";
					});

					$('#publishToJIRAAction').click(function(clickEvent) {
						'use strict';

						$(ID_SUBMIT_TO_JIRA_MODAL_WINDOW).modal({
							keyboard : false,
							backdrop : 'static'
						});

					});

					$('#cancelPublishToJiraActionModalButton').click(
							function(clickEvent) {
								'use strict';

								$('#jiraProjectKey').val('');
								$('#jiraProjectDisplayDiv').hide();

							});

					$('#publishToJiraActionModalButton')
							.click(
									function(clickEvent) {
										'use strict';

										if ($('#jiraProjectKey').val() !== null
												&& $('#jiraProjectKey').val() !== undefined
												&& $('#jiraProjectKey').val() !== "") {

											view
													.removeErrorOutlineForJIRAPRojectIdTextBox();

											view
													.closeModalWindow(ID_SUBMIT_TO_JIRA_MODAL_WINDOW);
											view
													.showApplicationPrgoressModalWindow(
															"Publishing data to JIRA",
															"50%");
											var queryParams = uri.search(true);
											var publishedJiraObject = model.getJiraId(
													$('#jiraProjectKey').val(),
													queryParams.report,
													queryParams.issue);
											
											if (publishedJiraObject !== null && publishedJiraObject !== undefined
													&& publishedJiraObject.self !== null
													&& publishedJiraObject.self !== undefined) {

												$("#issueStatusDiv").text(
														"Open (Jira Id:" + publishedJiraObject.key + ")");
											}
											
											console.log(publishedJiraObject);
											
											view
													.closeModalWindow(ID_APPLICATION_PROGRESS_BAR_MODAL_WINDOW);
											view
													.showDialog(
															ID_SUBMIT_TO_JIRA_SUCCESSFUL_NOTIFICATION_DIALOG,
															true);
											setTimeout(
													function() {
														// alert("hide");
														view
																.closeModalWindow(ID_SUBMIT_TO_JIRA_SUCCESSFUL_NOTIFICATION_DIALOG);
													},
													DIALOG_BOX_VISIBLE_TIME_MILLISECONDS + 2000);
										} else {
											view
													.showErrorOutlineForJIRAPRojectIdTextBox();
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

					$('#payloadVariantPaginationList').find("li a").click(
							function(clickEvent) {
								'use strict';
								console.log(this.id);

							});
				});

function setUpIssueDetails(reportId, issueId) {
	'use strict';

	view.showApplicationPrgoressModalWindow();

	// Get the logged in user
	loggedInUser = model.getLoggedInUser();
	if (loggedInUser !== null && loggedInUser !== undefined
			&& loggedInUser.userId !== null
			&& loggedInUser.userId !== undefined) {
		view.updateLoggedInUserBanner(loggedInUser.userId);
	}

	var issueDetails = model.getIssue(reportId, issueId);

	if (issueDetails !== null && issueDetails !== undefined) {
		if (issueDetails.issueUrl !== null
				&& issueDetails.issueUrl !== undefined) {
			$("#issueIdDiv").html(
					"<a target='_blank' href='" + issueDetails.issueUrl
							+ "' title='" + issueDetails.issueUrl + "'>"
							+ issueDetails.issuePrimaryKey.issueId + "</a>");
		} else {
			$("#issueIdDiv").text(issueDetails.issuePrimaryKey.issueId);
		}

		$("#issueSeverityDiv").text(issueDetails.severity);

		if (issueDetails.jira !== null && issueDetails.jira !== undefined
				&& issueDetails.jira.self !== null
				&& issueDetails.jira.self !== undefined) {

			$("#issueStatusDiv").text(
					"Open (Jira Id:" + issueDetails.jira.key + ")");
		} else {
			$("#issueStatusDiv").text("Open (Not published to JIRA)");
		}

		if (issueDetails.severity === "High") {
			$("#issueSeverityDiv").addClass("severityHigh")
		} else if (issueDetails.severity === "Medium") {
			$("#issueSeverityDiv").addClass("severityMedium")
		} else if (issueDetails.severity === "Low") {
			$("#issueSeverityDiv").addClass("severityLow")
		}
		$("#issueTypeDiv").text(issueDetails.issueType);

		$("#issueTestUrlDiv").text(issueDetails.testUrl);
		view.closeModalWindow(ID_APPLICATION_PROGRESS_BAR_MODAL_WINDOW);

		if (issueDetails.testUrl != null && issueDetails.testUrl != undefined) {
			$("#issueTestUrlDiv").html(
					"<span><a id='issueTestURLLink' target='_blank' href='"
							+ issueDetails.testUrl + "'></a></span>");
			$("#issueTestURLLink").text(issueDetails.testUrl);
		}

		if (issueDetails.issueVariants !== null
				&& issueDetails.issueVariants !== undefined
				&& issueDetails.issueVariants.length > 0) {

			var payloadVariantCounter = 1;
			var payloadVariantCounterPaginationHTML = "";
			/*
			 * while (payloadVariantCounter <=
			 * issueDetails.issueVariants.length) {
			 * payloadVariantCounterPaginationHTML =
			 * payloadVariantCounterPaginationHTML + "<li><a
			 * id='payloadPaginationIndex"+payloadVariantCounter+"'>" +
			 * payloadVariantCounter + "</a></li>"; payloadVariantCounter =
			 * payloadVariantCounter + 1; }
			 * $("#payloadVariantPaginationList").html(
			 * payloadVariantCounterPaginationHTML);
			 */

			if (issueDetails.issueVariants[0].traffic !== null
					&& issueDetails.issueVariants[0].traffic !== undefined
					&& issueDetails.issueVariants[0].traffic.testHttpTraffic !== null
					&& issueDetails.issueVariants[0].traffic.testHttpTraffic !== undefined) {
				$("#testHttpTrafficPreBlock").text(
						issueDetails.issueVariants[0].traffic.testHttpTraffic);
			}

			if (issueDetails.issueVariants[0].traffic !== null
					&& issueDetails.issueVariants[0].traffic !== undefined
					&& issueDetails.issueVariants[0].traffic.originalHttpTraffic !== null
					&& issueDetails.issueVariants[0].traffic.originalHttpTraffic !== undefined) {
				$("#originalHttpTrafficPreBlock")
						.text(
								issueDetails.issueVariants[0].traffic.originalHttpTraffic);
			}
		}

	} else {
		$('#divForIssueDetails').hide();
		$('#divForNoIssueDetailsFound').show();
		view.closeModalWindow(ID_APPLICATION_PROGRESS_BAR_MODAL_WINDOW);
	}

	return issueDetails;

}

function actionOnError() {
	'use strict';
	view.closeModalWindow('.modal');
	view.showDialog(ID_APPLICATION_ERROR_OCCURRED_NOTIFICATION_DIALOG, false);
}
