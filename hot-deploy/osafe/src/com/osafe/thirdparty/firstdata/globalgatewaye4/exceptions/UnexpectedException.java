package com.osafe.thirdparty.firstdata.globalgatewaye4.exceptions;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 *
 */
public class UnexpectedException extends GlobalGatewayException {
	private static final long serialVersionUID = 4L;

	public UnexpectedException(String message) {
		super(message);
	}
	
	public UnexpectedException(String message, Throwable cause) {
		super(message, cause);
	}
}
