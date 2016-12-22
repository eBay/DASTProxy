var metricsModel = {
		
		getActiveUsers : function() {

			var responseJsonObject = null;
			$.ajax({
				type : "GET",
				url : "rest/metrics/uniqueusers",
				async : false,
				dataType : "json",
				success : function(serverRespone) {
					responseJsonObject = serverRespone;
				},
				error : function() {
					actionOnError();
				}
			});

			return responseJsonObject;

		},
		
		getScanCounts : function() {

			var responseJsonObject = null;
			$.ajax({
				type : "GET",
				url : "rest/metrics/scancounts",
				async : false,
				dataType : "json",
				success : function(serverRespone) {
					responseJsonObject = serverRespone;
				},
				error : function() {
					actionOnError();
				}
			});

			return responseJsonObject;

		},
		
		getMonthlyScanData : function() {

			var responseJsonObject = null;
			$.ajax({
				type : "GET",
				url : "rest/metrics/scanovermonths",
				async : false,
				dataType : "json",
				success : function(serverRespone) {
					responseJsonObject = serverRespone;
				},
				error : function() {
					actionOnError();
				}
			});

			return responseJsonObject;

		}
		
		
}