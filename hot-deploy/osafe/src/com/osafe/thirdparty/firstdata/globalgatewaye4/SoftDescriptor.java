package com.osafe.thirdparty.firstdata.globalgatewaye4;

import com.google.gson.annotations.*;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 * 
 * <p>The following properties are used to set soft descriptor information for a transaction on a case-by-case basis. These properties are only compatible with v11 and v12 of the API and can only be used with the following transaction types:</p>
 * <ul>
 * <li>{@link TransactionType#Purchase}</li>
 * <li>{@link TransactionType#PreAuthorization}</li>
 * <li>{@link TransactionType#PreAuthorizationCompletion}</li>
 * <li>{@link TransactionType#Void}</li>
 * <li>{@link TransactionType#Refund}</li>
 * </ul>
 * <p>Important Note About Using Soft Descriptors: Dynamic soft descriptors are submitted at authorization and at settlement of a transaction.  If you would like to use Soft Descriptors, please contact your First Data Relationship Manager or Sales Rep and have them set your "Foreign Indicator" in your "North Merchant Manager File" to "5".</p>
 * <pre>
 * request.soft_descriptor(new SoftDescriptor().dba_name("Soft Merchant"));
 * </pre>
 *
 */
public class SoftDescriptor {
	
	@Expose private String dba_name;
	@Expose private String street;
	@Expose private String city;
	@Expose private String region;
	@Expose private String postal_code;
	@Expose private String country_code;
	@Expose private String mid;
	@Expose private String mcc;
	@Expose private String merchant_contact_info;
	
	/**
	 * @param dba_name Business name
	 * @return
	 */
	public SoftDescriptor dba_name(String dba_name) {
		this.dba_name = dba_name;
		return this;
	}
	
	/**
	 * @param street Business address
	 * @return
	 */
	public SoftDescriptor street(String street) {
		this.street = street;
		return this;
	}
	
	/**
	 * @param city Business city
	 * @return
	 */
	public SoftDescriptor city(String city) {
		this.city = city;
		return this;
	}
	
	/**
	 * @param region Business region
	 * @return
	 */
	public SoftDescriptor region(String region) {
		this.region = region;
		return this;
	}
	
	/**
	 * @param postal_code Business postal/zip code
	 * @return
	 */
	public SoftDescriptor postal_code(String postal_code) {
		this.postal_code = postal_code;
		return this;
	}
	
	/**
	 * @param country_code Business country
	 * @return
	 */
	public SoftDescriptor country_code(String country_code) {
		this.country_code = country_code;
		return this;
	}
	
	/**
	 * @param mid Merchant ID
	 * @return
	 */
	public SoftDescriptor mid(String mid) {
		this.mid = mid;
		return this;
	}
	
	/**
	 * @param mcc Merchant Category Code
	 * @return
	 */
	public SoftDescriptor mcc(String mcc) {
		this.mcc = mcc;
		return this;
	}
	
	/**
	 * @param merchant_contact_info Merchant contact information
	 * @return
	 */
	public SoftDescriptor merchant_contact_info(String merchant_contact_info) {
		this.merchant_contact_info = merchant_contact_info;
		return this;
	}

	public String getDba_name() {
		return dba_name;
	}

	public String getStreet() {
		return street;
	}

	public String getCity() {
		return city;
	}

	public String getRegion() {
		return region;
	}

	public String getPostal_code() {
		return postal_code;
	}

	public String getCountry_code() {
		return country_code;
	}

	public String getMid() {
		return mid;
	}

	public String getMcc() {
		return mcc;
	}

	public String getMerchant_contact_info() {
		return merchant_contact_info;
	}
	
}
