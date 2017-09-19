// Code goes here
var batchReportApp = angular.module('batchReportApp', ['angularUtils.directives.dirPagination']);
function ReportController($scope, $http) {
 $scope.showOmitted=false;
 $scope.currentPage = 1;
 $scope.pageSize = 10;
 $scope.issues = [];
 $scope.issuesAll = [];

	var url = "rest/v1/issues/"+scanBatchId;
	$http.get(url).success(function(data){
		$scope.issuesAll=data.data;
		for (i = 0; i < $scope.issuesAll.length; i++) {
		    $scope.issuesAll[i].testHTTPDivId = "testHTTPDivId"+i;
		    $scope.issuesAll[i].origHTTPDivId = "origHTTPDivId"+i;
		}		
		$scope.issues = data.data.filter(function (el) {
  			return (el.severity=='High' || el.severity=='Medium') && !el.fp && el.jiraURL==null;
		});
	}).error(function(){
		alert('An unexpected error occured');
	});

  $scope.pageChangeHandler = function(num) {
      console.log('recordings page changed to ' + num);
  };

	$scope.showAll = function (showAllflag) {
		$scope.showOmitted=showAllflag;
		if ($scope.showOmitted){
			$scope.issues=$scope.issuesAll;
		} else {
			$scope.issues = $scope.issuesAll.filter(function (el) {
	  			return (el.severity=='High' || el.severity=='Medium') && el.jiraURL==null && !el.fp;
			});
		}
	
	}  
}

function OtherController($scope) {
  $scope.pageChangeHandler = function(num) {
    console.log('going to page ' + num);
  };
}
function ScanScrollController($scope) {
  $scope.paginationId='pagination2';
  $scope.pageChangeHandler = function(num) {
    console.log('going to page for scroll' + num);
  };
}
function ScanController($scope, $http) {
 $scope.currentPage = 1;
 $scope.pageSize = 10;
 $scope.scans = [];

	var url = "rest/v1/scans/"+scanBatchId;
	$http.get(url).success(function(data){
		$scope.scans=data.data;
	}).error(function(){
		alert('An unexpected error occured')
	});
  $scope.pageChangeHandler = function(num) {
      console.log('recordings page changed to ' + num);
  };
}
batchReportApp.controller('ReportController', ReportController);
batchReportApp.controller('OtherController', OtherController);
batchReportApp.controller('ScanController', ScanController);
batchReportApp.controller('ScanScrollController', ScanScrollController);