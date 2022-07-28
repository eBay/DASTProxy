/**
 * This is the default exception handler for the project. We do not allow any exception to propogate to the user.
 * The default strategy is to send a JSON response with the data denoting error.
 * 
 * @author Kiran Shirali (kshirali@ebay.com)
 */

package com.dastproxy.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import com.dastproxy.common.constants.AppScanConstants;
import com.dastproxy.common.utils.AppScanUtils;
import com.dastproxy.model.DASTProxyException;

@Service
public class DASTExceptionHandler extends SimpleMappingExceptionResolver {
	
	private static final Logger LOGGER = LogManager.getLogger(DASTExceptionHandler.class.getName());
	
	@Autowired
	private View view;
	
	/**
	 * This is the actual method that intercepts all exceptions thrown 
	 * within the project. Here, there is a check to see if there is 
	 * details about the error. In case the custom exception DASTProxy 
	 * Exception has been intercepted, the message that it carries 
	 * is sent to the client.
	 * 
	 * In case it is some other error, then the message is logged and 
	 * a generic error message is sent to the client.
	 */
	@Override
	protected ModelAndView doResolveException(final HttpServletRequest request,
			final HttpServletResponse response, final Object handler, final Exception exception) {
		
		LOGGER.debug("In DASTExceptionHandler");
		response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		
		AppScanUtils.sendErrorMail(exception);

		if(exception instanceof DASTProxyException){
			if(AppScanUtils.isNotNull(((DASTProxyException) exception).getErrorCode())){
				return new ModelAndView(view, AppScanConstants.JSON_RESPONSE_ERROR_IDENTIFIER, ((DASTProxyException) exception).getErrorCode());
			}
		
			else{
				LOGGER.error(exception);
				return new ModelAndView(view, AppScanConstants.JSON_RESPONSE_ERROR_IDENTIFIER, ((DASTProxyException) exception).getErrorMessage());
			}
		}
		
		// In this scenario the error has not been willfully thrown 
		// at some point in the application. This means that the error
		// has details that we might not want the client to know about. 
		// So sending a generic message.
		return new ModelAndView(view, "error", "Exception has occured. Please contact the site administrator");
	}
}
