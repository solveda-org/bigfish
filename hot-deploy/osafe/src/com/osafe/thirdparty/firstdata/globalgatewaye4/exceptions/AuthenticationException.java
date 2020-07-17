package com.osafe.thirdparty.firstdata.globalgatewaye4.exceptions;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 *
 */
public class AuthenticationException extends GlobalGatewayException {
	private static final long serialVersionUID = 4L;
		
	/**
	 * @param message {@link #String} message to display the reason for the Authentication exception.
	 */
	public AuthenticationException(String message) {
		super(message);
	}
}
