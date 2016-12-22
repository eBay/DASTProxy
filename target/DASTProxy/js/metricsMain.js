
$(document).ready(function() {

	var uniqueUserList = metricsModel.getActiveUsers();
	var scanCounts = metricsModel.getScanCounts();
	var monthlyScanData = metricsModel.getMonthlyScanData();

	drawActiveUsersColumnChart(uniqueUserList);
	drawScanCountPieChart(scanCounts);
	drawScanTypePieChart(scanCounts);
	drawMonthlyScanLineChart(monthlyScanData);

});

function drawActiveUsersColumnChart(uniqueUserList) {

	// Create our data table.
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'User');
	data.addColumn('number', 'No. Of Scans');
	for ( var i in uniqueUserList) {
		data.addRow([ uniqueUserList[i][0], uniqueUserList[i][1] ]);
	}

	// Instantiate and draw our chart, passing in some options.
	var chart = new google.visualization.ColumnChart(document
			.getElementById('chart_div1'));
	chart.draw(data, {
		title : 'Active Product Team Users (No InfoSec Org Users)',
		is3D : true,
		width : 500,
		height : 440
	});
}

function drawScanCountPieChart(scanCounts) {

	// Create our data table.
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Scan Type');
	data.addColumn('number', 'No. Of Scans');

	data.addRows([ [ 'Scans Successfully Run', scanCounts[0] ],
			[ 'Scans in Error State', scanCounts[2] ],
			[ 'Scans Successfully Set Up but Not Run', scanCounts[1] ] ]);

	// Instantiate and draw our chart, passing in some options.
	var chart = new google.visualization.PieChart(document
			.getElementById('chart_div2'));
	chart.draw(data, {
		title : 'Distribution of Scan States',
		is3D : true,
		width : 500,
		height : 440
	});

}

function drawScanTypePieChart(scanCounts) {

	// Create our data table.
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Scan Type');
	data.addColumn('number', 'No. Of Scans');

	data.addRows([ [ 'Scans set up via Bluefin/Breeze/Selenium', scanCounts[3] ],
			[ 'Scans set up via DAST UI', scanCounts[4] ]

	]);

	var chart = new google.visualization.PieChart(document
			.getElementById('chart_div3'));
	chart.draw(data, {
		title : 'Scan Type (only successful scans)',
		is3D : true,
		width : 500,
		height : 440
	});
}

function drawMonthlyScanLineChart(monthlyScanData) {

	'use strict';
	// Create our data table.
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Scan Type');
	data.addColumn('number', 'No. Of Successful Scans');

	for ( var i in monthlyScanData) {
		data.addRow([ monthlyScanData[i][0], monthlyScanData[i][1] ]);
	}

	var chart = new google.visualization.LineChart(document
			.getElementById('chart_div4'));
	chart
			.draw(
					data,
					{
						title : 'Monthly distribution of Successful Scans (No InfoSec Org Scans)',
						is3D : true,
						width : 500,
						height : 440
					});
}

function actionOnError() {
	'use strict';
	view.closeModalWindow('.modal');
	view.showDialog(ID_APPLICATION_ERROR_OCCURRED_NOTIFICATION_DIALOG, false);

}