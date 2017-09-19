// Code goes here
var dashboardApp = angular.module('dashboardApp', ['angularUtils.directives.dirPagination']);
function TsRecController($scope, $http) {
 $scope.currentPage = 1;
 $scope.pageSize = 10;
 $scope.batches = [];
		$http.get("rest/v1/recordingbatches").success(function(data){
			$scope.batches=data.data;
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
function ScanController($scope, $http) {
 $scope.currentPage = 1;
 $scope.pageSize = 10;
 $scope.paginationId = 'pagination2';
 $scope.batchscans = [];

_refreshData = function () {
	$http.get("rest/v1/scanbatches").success(function(data){

		if (data.data.length > 0 && $scope.batchscans.length > 0 && ((data.data.length - $scope.batchscans.length) > 0)) {
			newRows = data.data.length - $scope.batchscans.length;
		}
		$scope.batchscans=data.data;
			if ($scope.batchscans != 'undefined' && $scope.batchscans.length > 0){
				for (iter = 0 ; iter <$scope.batchscans.length; iter++){
					$scope.batchscans[iter].dateCreated = (new Date($scope.batchscans[iter].dateCreated)).toLocaleDateString("en-US", timeFormatOptions);
				}
			}

		 if (newRows > 0) {
				$('#scan_message').hide();
			setTimeout(function(){
				$("#scan_table tbody tr:nth-child(2)").css('background-color','cyan');
			}, 100);
			newRows = 0;
		}
	}).error(function(){
		alert('An unexpected error occured');
	});
};
_refreshData();
$scope.refreshData = function () {
	_refreshData();
}
  $scope.pageChangeHandler = function(num) {
      console.log('recordings page changed to ' + num);
  };
}
function ScanScrollController($scope) {
  $scope.paginationId='pagination2';
  $scope.pageChangeHandler = function(num) {
    console.log('going to page for scroll' + num);
  };
}
function SelectRecordingController($scope, $http) {
 $scope.currentPage = 1;
 $scope.pageSize = 10;
 $scope.recordings = [];
 $scope.show=false;
 $scope.batchId;


	$scope.setBatchId = function (batchId) {
		$scope.batchId=batchId;
			$http.get("rest/v1/recordings/"+$scope.batchId).success(function(data){
				$scope.recordings=data.data;
				if ($scope.recordings != 'undefined' && $scope.recordings.length > 0){
					for (iter = 0 ; iter <$scope.recordings.length; iter++){
						$scope.recordings[iter].dateCreated = (new Date($scope.recordings[iter].dateCreated)).toLocaleDateString("en-US", timeFormatOptions);
						$scope.recordings[iter].checked=false;
						$scope.recordings[iter].index=iter;
					}
				}
			}).error(function(){
				alert('An unexpected error occured');
			});
	}
	$scope.setCheckBoxState= function (iter, state) {
		$scope.recordings[iter].checked = state;
	}

  $scope.pageChangeHandler = function(num) {
      console.log('recordings page changed to ' + num);
  };
}
function SelectRecordingScrollController($scope) {
  $scope.pageChangeHandler = function(num) {
    console.log('going to page ' + num);
  };
}
dashboardApp.controller('TsRecController', TsRecController);
dashboardApp.controller('OtherController', OtherController);
dashboardApp.controller('ScanController', ScanController)
dashboardApp.controller('ScanScrollController', ScanScrollController)
dashboardApp.controller('SelectRecordingController', SelectRecordingController)
dashboardApp.controller('SelectRecordingScrollController', SelectRecordingScrollController)

