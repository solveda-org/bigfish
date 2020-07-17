package com.osafe.thirdparty.firstdata.globalgatewaye4;

import com.google.gson.annotations.*;
/**
 * @author Steve Copous <steve.copous@firstdata.com>
 * 
 * <p>This class contains the Ship to Address information required for Level Three transaction processing.</p>
 * <pre>
 * LevelThree levelThree = new LevelThree();
 * levelThree.ship_to_address(new ShipToAddress().customer_number("123"));
 * </pre>
 *
 */
public class ShipToAddress {

	// address1 removed, response field fixed in 14.2 release 1/21/2014
	//@Expose private String address1;
	@Expose private String address_1;
	@Expose private String city;
	@Expose private String state;
	@Expose private String zip;
	@Expose private String country;
	@Expose private String customer_number;
	@Expose private String email;
	@Expose private String name;
	@Expose private String phone;
	
	/**
	 * @param address_1 The Street Address of the "ship to" location.
	 * <p>Required Character Format is UPPER.</p>
	 * @return {@link #ShipToAddress}
	 */
	public ShipToAddress address_1(String address_1) {
		this.address_1 = address_1;
		return this;
	}
	
	/**
	 * @param city The City of the "ship to" location.
	 * <p>Required Character Format is UPPER</p>
	 * @return {@link #ShipToAddress}
	 */
	public ShipToAddress city(String city) {
		this.city = city;
		return this;
	}
	
	/**
	 * @param state The State of the "ship to" location.
	 * <p>Required Character Format is UPPER.</p>
	 * @return {@link #ShipToAddress}
	 */
	public ShipToAddress state(String state) {
		this.state = state;
		return this;
	}
	
	/**
	 * @param zip The Zip/postal code of the "ship to" location.
	 * @return {@link #ShipToAddress}
	 */
	public ShipToAddress zip(String zip) {
		this.zip = zip;
		return this;
	}
	
	/**
	 * @param country The ISO-assigned code of the country to which the goods were shipped.
	 * @return {@link #ShipToAddress}
	 * @see <a href="https://firstdata.zendesk.com/entries/23105182-ISO-Country-Codes">https://firstdata.zendesk.com/entries/23105182-ISO-Country-Codes</a>
	 */
	public ShipToAddress country(String country) {
		this.country = country;
		return this;
	}
	
	/**
	 * @param customer_number Purchase order or other number used by merchants customer to track the order.
	 * @return {@link #ShipToAddress}
	 */
	public ShipToAddress customer_number(String customer_number) {
		this.customer_number = customer_number;
		return this;
	}
	
	/**
	 * @param email The Accountholders email address associated with the transaction. 
	 * @return {@link #ShipToAddress}
	 */
	public ShipToAddress email(String email) {
		this.email = email;
		return this;
	}
	
	/**
	 * @param name The Accountholders name associated with the transaction. 
	 * <p>asterisk should precede last name ex: <b>FIRST *LAST</b></p>
	 * @return {@link #ShipToAddress}
	 */
	public ShipToAddress name(String name) {
		this.name = name;
		return this;
	}
	
	/**
	 * @param phone The Accountholders phone number associated with the transaction. 
	 * <p>Format of AAAEEENNNNXXXX where: 
	 * <ul>
	 * <li>AAA = Area Code</li>
	 * <li>EEE = Exchange</li>
	 * <li>NNNN = Number</li>
	 * <li>XXXX = Extension</li>
	 * </ul></p>
	 * @return {@link #ShipToAddress}
	 */
	public ShipToAddress phone(String phone) {
		this.phone = phone;
		return this;
	}

	public String getAddress_1() {
		return address_1;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getZip() {
		return zip;
	}

	public String getCountry() {
		return country;
	}

	public String getCustomer_number() {
		return customer_number;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}
}
