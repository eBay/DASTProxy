var appScanUtil = {
		
		
		clearAuthenticationDetails: function(closeWindow){
			
			if(BrowserDetect !== null && BrowserDetect.browser !== null){
				
				if(BrowserDetect.browser.toUpperCase() == "Explorer".toUpperCase()){

					this.clearAuthDetailsForIE();
					
					if(closeWindow !== null && closeWindow === true){
						alert("You have been logged out. For security purposes, this window is going to close");
						this.closeWindow();	
					}
					
				}
				if(BrowserDetect.browser.toUpperCase() == "Mozilla".toUpperCase() || BrowserDetect.browser.toUpperCase() == "Firefox".toUpperCase()){
					
					this.foolBrowserToScrapeBasicAuthCredentials();		
				}
				else{

					this.foolBrowserToScrapeBasicAuthCredentials();
					if(closeWindow !== null && closeWindow === true){
						alert("You have been logged out. For security purposes, this window is going to close");
						this.closeWindow();	
					}
				}
			}
			
		},
		
		closeWindow: function(){
				  window.open('', '_self', '');
				  window.close();
		},
		
		foolBrowserToScrapeBasicAuthCredentials: function(){
			
			var currentUrl = $(location).attr('href');
			currentUrl = currentUrl.replace("http://","http://abc@");
			$.ajax({
				type: "GET",
				url: currentUrl,
				async: true,
				success: function(serverRespone){
				},
				error: function(){
				}
			});
		},
		
		// Function to clear authentication details cached in an IE browser window.
		clearAuthDetailsForIE: function(){
			// This is a function that works for IE 6+. It clears the basic authentication details for the browser.
			document.execCommand('ClearAuthenticationCache');
		}
		
};