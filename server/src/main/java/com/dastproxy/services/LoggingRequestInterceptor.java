package com.dastproxy.services;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

	    ClientHttpResponse response = execution.execute(request, body);
	    log(request,body,response);
	    return response;
	}
	
	private void log(HttpRequest request,byte[] body,ClientHttpResponse response) throws IOException{
		
		HttpHeaders headers = request.getHeaders();
		System.out.println("Intercepted Accept is" + request.getHeaders().getAccept());
		System.out.println("Intercepted Authorization is" + request.getHeaders().get(HttpHeaders.AUTHORIZATION));
		System.out.println("Intercepted Content Type is: "  + request.getHeaders().getContentType());
		System.out.println("Intercepted Body is: " + new String(body));
		
	}
}
