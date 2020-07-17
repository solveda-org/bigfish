package com.osafe.thirdparty.firstdata.globalgatewaye4.exceptions;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 *
 */
public class GlobalGatewayException extends RuntimeException {
	private static final long serialVersionUID = 4L;
	
	public GlobalGatewayException(String message, Throwable cause) {
		super(message, cause);
	}

	public GlobalGatewayException(String message) {
		super(message);
	}
	
	public GlobalGatewayException() {
		super();
	}
}
