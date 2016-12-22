package com.dastproxy.model;

public class DASTProxyException extends Exception{

	private static final long serialVersionUID = -2885700812514286158L;
	private String errorCode;
	private String errorMessage;

	public DASTProxyException(final String errorCode, final String errorMessage) {
		super();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	public DASTProxyException(final String errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(final String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/** 
	 * 
	 */
	@Override
	public String toString() {
		return "Exception has occured in DASTIntegration Project. The details of the exception are: " + errorMessage;
	}
	
	
}
