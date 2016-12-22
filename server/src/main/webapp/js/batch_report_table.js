// Code goes here
var batchReportApp = angular.module('batchReportApp', ['angularUtils.directives.dirPagination']);
function ReportController($scope, $http) {
 $scope.currentPage = 1;
 $scope.pageSize = 10;
 $scope.issues = [];
 		var url = "rest/v1/issues/"+scanBatchId;
		$http.get(url).success(function(data){
			$scope.issues=data.data;
			for (i = 0; i < $scope.issues.length; i++) {
			    $scope.issues[i].testHTTPDivId = "testHTTPDivId"+i;
			    $scope.issues[i].origHTTPDivId = "origHTTPDivId"+i;
			}
			//$scope.issues[4].severity='High';
			//$scope.issues[7].severity='Medium';
		}).error(function(){
			alert('An unexpected error occured');
		});

  $scope.pageChangeHandler = function(num) {
      console.log('recordings page changed to ' + num);
  };
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
