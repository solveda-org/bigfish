package com.osafe.thirdparty.firstdata.globalgatewaye4.exceptions;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 *
 */
public class InvalidTransactionException extends GlobalGatewayException {
	private static final long serialVersionUID = 4L;
	
	public InvalidTransactionException(String message) {
		super(message);
	}
}
