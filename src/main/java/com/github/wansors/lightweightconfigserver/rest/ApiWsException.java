package com.github.wansors.lightweightconfigserver.rest;

/**
 * Exception which returns the existing errors from API rest
 *
 */
public class ApiWsException extends RuntimeException {
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 8052053842313350903L;

	private transient ErrorTypeCode errorTypeCode;
	private final int statusCode;
	private final String errorType;

	/**
	 * Constructor with {@code ErrorTypeCode} errorTypeCode.
	 *
	 * @param errorTypeCode
	 * @param message
	 */
	public ApiWsException(final ErrorTypeCode errorTypeCode) {
		this.setErrorTypeCode(errorTypeCode);
		statusCode = errorTypeCode.getHttpCode().getCode();
		errorType = errorTypeCode.getErrorType();

	}

	/**
	 * Constructor with {@code ErrorTypeCode} errorTypeCode and {@code Exception}
	 * exception.
	 *
	 * @param errorTypeCode
	 * @param exception
	 */
	public ApiWsException(final ErrorTypeCode errorTypeCode, final Exception exception) {

		super(exception);
		this.setErrorTypeCode(errorTypeCode);
		statusCode = errorTypeCode.getHttpCode().getCode();
		errorType = errorTypeCode.getErrorType();
	}

	/**
	 * Constructor with {@code String} message and {@code ErrorMessages}
	 * errorMessages.
	 *
	 * @param message
	 * @param errorTypeCode
	 */
	public ApiWsException(String message, final ErrorTypeCode errorTypeCode) {
		super(message);
		this.setErrorTypeCode(errorTypeCode);
		statusCode = errorTypeCode.getHttpCode().getCode();
		errorType = errorTypeCode.getErrorType();
	}

	public ErrorTypeCode getErrorTypeCode() {
		return errorTypeCode;
	}

	public void setErrorTypeCode(ErrorTypeCode errorTypeCode) {
		this.errorTypeCode = errorTypeCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getErrorType() {
		return errorType;
	}

}
