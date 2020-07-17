package com.osafe.thirdparty.firstdata.globalgatewaye4;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 * <p>INTENDED FOR DEMONSTRATION PURPOSES ONLY.  NOT FOR PRODUCTION USE.</p>
 */
public class CustomerIdType {

	private String _value_;
	
	protected CustomerIdType(String value) {
		this._value_ = value;
	}
	
	private static final String _DriversLicense = "0";
	private static final String _SocialSecurityNumber = "1";
	private static final String _TaxID = "2";
	private static final String _MilitaryID = "3";
	
	public static final CustomerIdType driversLicense = new CustomerIdType(_DriversLicense);
	public static final CustomerIdType socialSecurityNumber = new CustomerIdType(_SocialSecurityNumber);
	public static final CustomerIdType taxId = new CustomerIdType(_TaxID);
	public static final CustomerIdType militaryId = new CustomerIdType(_MilitaryID);
	
	@Override
	public String toString() {
		return _value_;
	}
	
}
