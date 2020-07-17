package com.osafe.thirdparty.firstdata.globalgatewaye4;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 * <p>INTENDED FOR DEMONSTRATION PURPOSES ONLY.  NOT FOR PRODUCTION USE.</p>
 */
public class ReleaseType {
	private String _value_;
	
	protected ReleaseType(String value) {
		this._value_ = value;
	}
	
	private static final String _homeDelivery = "D";
	private static final String _preOrder = "P";
	private static final String _shipToStore = "S";
	private static final String _expressHomeDelivery = "X";
	
	/**
	 * Release type for Home Delivery - "D"
	 */
	public static final ReleaseType HomeDelivery = new ReleaseType(_homeDelivery);
	
	/**
	 * Release Type for PreOrders - "P"
	 */
	public static final ReleaseType PreOrder = new ReleaseType(_preOrder);
	
	/**
	 * Release Type for Ship to Store - "S"
	 */
	public static final ReleaseType ShipToStore = new ReleaseType(_shipToStore);
	
	/**
	 * Release Type for Express Home Delivery - "X"
	 */
	public static final ReleaseType ExpressHomeDelivery = new ReleaseType(_expressHomeDelivery);
	
	@Override
	public String toString() {
		return _value_;
	}
}
