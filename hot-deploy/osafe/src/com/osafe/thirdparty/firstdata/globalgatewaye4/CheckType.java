package com.osafe.thirdparty.firstdata.globalgatewaye4;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 * <p>INTENDED FOR DEMONSTRATION PURPOSES ONLY.  NOT FOR PRODUCTION USE.</p>
 */
public class CheckType {
	private String _value_;
	
	protected CheckType(String value) {
		this._value_ = value;
	}
	
	private static final String _Personal = "P";
	private static final String _Corporate = "C";
	
	public static final CheckType PersonalCheck = new CheckType(_Personal);
	public static final CheckType CorporateCheck = new CheckType(_Corporate);
	
	@Override
	public String toString() {
		return _value_;
	}
}
