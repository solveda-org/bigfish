package com.osafe.thirdparty.firstdata.globalgatewaye4;

import java.math.BigDecimal;

import com.google.gson.*;
import com.google.gson.annotations.*;
import com.osafe.thirdparty.firstdata.globalgatewaye4.util.Hmac;
import com.osafe.thirdparty.firstdata.globalgatewaye4.util.Http;
import com.osafe.thirdparty.firstdata.globalgatewaye4.util.Luhn;
import org.apache.commons.lang.Validate;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 * <p>INTENDED FOR DEMONSTRATION PURPOSES ONLY.  NOT FOR PRODUCTION USE.</p>
 */
public class CheckRequest {
	@Expose private String gateway_id;
	@Expose	private String password;
	@Expose private TransactionType transaction_type;
	@Expose private BigDecimal amount;
	@Expose private String check_number;
	@Expose private CheckType check_type;
	@Expose private String micr;
	@Expose private String account_number;
	@Expose private String bank_id;
	@Expose private String transaction_tag;
	@Expose private String authorization_num;
	@Expose private String cardholder_name;
	@Expose private String cc_verification_str1;
	@Expose private EcommerceFlagType ecommerce_flag;
	@Expose private String reference_no;
	@Expose private String customer_ref;
	@Expose private String language;
	@Expose private String client_ip;
	@Expose private String client_email;
	@Expose private String user_name;
	@Expose private CustomerIdType customer_id_type;
	@Expose private String customer_id_number;
	@Expose private ReleaseType release_type;
	@Expose private BigDecimal gift_card_amount;
	@Expose private String date_of_birth;
	@Expose private Boolean vip;
	@Expose private String registration_no;
	@Expose private String registration_date;
	@Expose private String clerk_id;
	@Expose private String device_id;
	
	private GlobalGatewayE4 e4;
	
	/**
	 * Constructor for a new GGe4 TeleCheck transaction request.
	 * 
	 * @param e4 GlobalGatewayE4 object to be used for the request
	 * @param configuration Configuration ojbect to be used for the request
	 */
	public CheckRequest(GlobalGatewayE4 e4, Configuration configuration) {
		this.gateway_id = configuration.gateway_id;
		this.password = configuration.password;
		this.e4 = e4;
	}
	
	/**
	 * @return Returns a {@link #Response} object with the results of the transaction.
	 * @throws Exception
	 */
	public Response submit() throws Exception {
		Response response = new Response(e4);
		String content = this.toJson();
		Hmac hmac = new Hmac(e4, content);
		Http http = new Http(e4.getUrl(), e4.getVersion(), hmac);
		
		String responseString = http.doRequest(this);
		
		Gson gson = e4.getGson();
		response = gson.fromJson(responseString, Response.class);
		
		return response;
	}
	
	/**
	 * @return Returns a JSON string of the current request values.
	 */
	public String toJson() {
		Gson gson = e4.getGson();
		return gson.toJson(this);
	}
	
	/**
	 * @param transaction_type {@link #TransactionType} to be used for the transaction.
	 * @return {@link #CheckRequest}
	 */
	//@NotNull
	public CheckRequest transaction_type(TransactionType transaction_type) {
		this.transaction_type = transaction_type;
		return this;
	}

	/**
	 * @param amount {@link #BigDecimal} dollar amount of transaction.  Max value of 999999.99
	 * @return {@link #CheckRequest}
	 */
	//@NotNull
	public CheckRequest amount(BigDecimal amount) {
		Validate.isTrue(amount.compareTo(new BigDecimal("1000000.00")) == -1, "Dollar amount is too large.  Maximum value is 999999.99.");
		this.amount = amount;
		return this;
	}
	
	/**
	 * @param check_number {@link #String}[30] Check number being used for the payment.
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest check_number(String check_number) {
		this.check_number = check_number;
		return this;
	}
	
	/**
	 * @param checkType {@link #CheckType} type of check to be used for the transaction.
	 * @return {@link #CheckRequest}
	 */
	//@NotNull
	public CheckRequest check_type(CheckType checkType) {
		this.check_type = checkType;
		return this;
	}
	
	
	/**
	 * @param micr {@link #String}[30] MICR associated with the transaction.
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest micr(String micr) {
		this.micr = micr;
		return this;
	}
	
	/**
	 * @param account_number {@link #String}[30] The bank account number of the check being used to complete payment.
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest account_number(String account_number) {
		Validate.isTrue(account_number.length() <= 30, "account_number contains too many digits. Less than 30 digits required.");
		this.account_number = account_number;
		return this;
	}
	
	/**
	 * @param bank_id {@link #String}[30] The bank routing number of the check being used to complete payment. 
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest bank_id(String bank_id) {
		Validate.isTrue(bank_id.length() <= 30, "bank_id contains too many digits. Less than 30 digits required.");
		this.bank_id = bank_id;
		return this;
	}
	
	/**
	 * @param transaction_tag {@link #String} value of the transaction tag.
	 * <p>Required for the following {@link #TransactionType}s:</p>
	 * <ul>
	 * <li>{@link TransactionType#TaggedRefund}</li>
	 * <li>{@link TransactionType#TaggedVoid}</li>
	 * <li>{@link TransactionType#TaggedPreAuthorizationCompletion}</li>
	 * </ul>
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest transaction_tag(String transaction_tag) {
		Validate.notEmpty(transaction_tag, "transaction_tag cannot be empty.");
		this.transaction_tag = transaction_tag;
		return this;
	}
	
	/**
	 * @param authorization_num {@link #String} value of the authorization number. 
	 * <p>Required for the following {@link #TransactionType}s:</p>
	 * <ul>
	 * <li>{@link TransactionType#Void}</li>
	 * <li>{@link TransactionType#PreAuthorizationCompletion}</li>
	 * <li>{@link TransactionType#TaggedRefund}</li>
	 * <li>{@link TransactionType#TaggedVoid}</li> 
	 * </ul> 
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest authorization_num(String authorization_num) {
		Validate.notEmpty(authorization_num, "authorization_num cannot be empty.");
		this.authorization_num = authorization_num;
		return this;
	}
	
	/**
	 * @param cardholder_name {@link #String}[30] value of customer name.  Required.
	 * @return {@link #CheckRequest}
	 */
	//@NotNull
	public CheckRequest cardholder_name(String cardholder_name) {
		Validate.notEmpty(cardholder_name, "cardholder_name cannot be empty.");
		Validate.isTrue(cardholder_name.length() <= 30, "cardholder_name contains too many characters.  Less than 30 characters required.");
		this.cardholder_name = cardholder_name;
		return this;
	}
	
	/**
	 * @param cc_verification_str1 This {@link #String}[40] is populated with the cardholders address information in a specific format. The address is verified and a result is returned (AVS property) that indicates how well the address matched.
	 * <p>VerificationStr1 is comprised of the following constituent address values: Street, Zip/Postal Code, City, State/Provence, Country.  They are separted by the Pipe Character "|".</p>
	 * <p>Street Address|Zip/Postal|City|State/Prov|Country|Phone</p>
	 * <p>The "Phone" value should be prepended with the letter D, H, N, or W for day, home, night, and work respectively. For example, "H5555555555".</p>
	 * <p>Required Character Format is ASCII.  40 character maximum.</p>
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest cc_verification_str1(String cc_verification_str1) {
		Validate.notEmpty(cc_verification_str1, "cc_verification_str1 cannot be empty.");
		Validate.isTrue(cc_verification_str1.length() <= 40, "cc_verification_str1 contains too many characters. Maximum of 40 characters.");
		this.cc_verification_str1 = cc_verification_str1;
		return this;
	}
	
	/**
	 * @param ecommerce_flag The value passed in this flag can be used to classify the type of payment being performed.
	 * <p>Possible values:</p>
	 * <ul>
	 * <li>{@link EcommerceFlagType#MOTOSingle} = 1</li>
	 * <li>{@link EcommerceFlagType#MOTORecurring} = 2</li>
	 * <li>{@link EcommerceFlagType#MOTOInstallment} = 3</li>
	 * <li>{@link EcommerceFlagType#MOTODeferred} = 4</li>
	 * <li>{@link EcommerceFlagType#ECISecure} = 5</li>
	 * <li>{@link EcommerceFlagType#ECINonAuth} = 6</li>
	 * <li>{@link EcommerceFlagType#ECIEncrypted} = 7</li>
	 * <li>{@link EcommerceFlagType#ECINonSecure} = 8</li>
	 * <li>{@link EcommerceFlagType#IVR} = I</li>
	 * <li>{@link EcommerceFlagType#Retail} = R</li>
	 * </ul> 
	 * @return {@link #CheckRequest}
	 * @see com.firstdata.globalgatewaye4.EcommerceFlagType#EcommerceFlagType EcommerceFlagType
	 */
	public CheckRequest ecommerce_flag(EcommerceFlagType ecommerce_flag) {
		this.ecommerce_flag = ecommerce_flag;
		return this;
	}
	
	/**
	 * @param reference_no {@link #String}[20] A merchant defined value that can be used to internally identify the transaction. This value is passed through to the Global Gateway e4 unmodified, and may be searched in First Data Global Gateway e4 Real-time Payment Manager (RPM). It is not passed on to the financial institution. The following characters will be stripped from this field: ; ` " / % as well as -- (2 consecutive dashes).
	 * <p>NOTE: For non-international transactions, DO NOT USE the following characters: pipe (|), caret (^), percent symbol (%), backslash (\), or forward slash (/).</p>
	 * <p>For international transactions DO NOT USE the following punctuation: caret (^), backslash (\), openbracket ([), closed bracket (]), tilde (~) or accent key (`). If used the transaction will reject for Response Reason Code 225 (Invalid field data)</p>
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest reference_no(String reference_no) {
		Validate.isTrue(reference_no.length() <= 20, "reference_no contains too many characters.  Maximum of 20 characters.");
		this.reference_no = reference_no;
		return this;
	}
	
	/**
	 * @param customer_ref {@link #String}[20] A merchant defined value that can be used to internally identify the transaction. This value is passed through to the Global Gateway E4 unmodified, and may be searched in First Data Global Gateway E4 Real-time Payment Manager (RPM). It is not passed on to the financial institution. The following characters will be stripped from this field: ; ` " / % as well as -- (2 consecutive dashes).
	 * <p>Required Character Format is ASCII.</p>
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest customer_ref(String customer_ref) {
		Validate.isTrue(customer_ref.length() <= 20, "customer_ref contains too many characters.  Maximum of 20 characters.");
		this.customer_ref = customer_ref;
		return this;
	}
	
	/**
	 * @param language {@link #String}[2] Selects the language the CTR is to appear in. 
	 * <p>Supported Values:</p>
	 * <ul>
	 * <li>EN {Default}</li>
	 * <li>FR</li>
	 * <li>ES</li>
	 * </ul>
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest language(String language) {
		this.language = language;
		return this;
	}
	
	/**
	 * @param client_ip {@link #String}[15] This is the IP address of the customer (i.e. client browser) connecting to the merchant. This value is stored for fraud investigation. It is not passed on to the financial institution.
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest client_ip(String client_ip) {
		this.client_ip = client_ip;
		return this;
	}

	/**
	 * @param client_email {@link #String}[30] This is the email address of the customer connecting to the merchant. This value is stored for fraud investigation. It is not passed on to the financial institution.
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest client_email(String client_email) {
		this.client_email = client_email;
		return this;
	}
	
	/**
	 * @param user_name {@link #String}[30] This is the user_name of the user processing the transaction.  This field is visible in the Real Time Payment manager as the "User ID" and defaults to "API-(ExactID)".
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest user_name(String user_name) {
		this.user_name = user_name;
		return this;
	}
	
	/**
	 * @param customerIdType {@link #CustomerIdType} The type of ID used to validate the identity of the check holder.
	 * <p>Allowed values are:</p>
	 * <ul>
	 * <li>Driver's license: 0</li>
	 * <li>Social Security Number: 1</li>
	 * <li>Tax ID: 2</li>
	 * <li>Military ID: 3</li>
	 * </ul>
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest customer_id_type(CustomerIdType customer_id_type) {
		this.customer_id_type = customer_id_type;
		return this;
	}
	
	/**
	 * @param customer_id_number {@link #String}[30] The ID number of the Customer ID specified in {@link #customer_id_type}
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest customer_id_number(String customer_id_number) {
		this.customer_id_number = customer_id_number;
		return this;
	}
	
	/**
	 * @param release_type {@link #ReleaseType} 
	 * <p>Possible Values:</p>
	 * <ul>
	 * <li>D  Home Delivery</li>
	 * <li>P  Preorder</li>
	 * <li>S Ship to store</li>
	 * <li>X Express Home Delivery</li>
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest release_type(ReleaseType release_type) {
		this.release_type = release_type;
		return this;
	}
	
	/**
	 * @param gift_card_amount {@link #BigDecimal}[30] Gift card amount associated with the transaction.
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest gift_card_amount(BigDecimal gift_card_amount) {
		this.gift_card_amount = gift_card_amount;
		return this;
	}
	
	/**
	 * @param date_of_birth {@link #String}[8] Customer's date of birth in MMDDYYYY format.
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest date_of_birth(String date_of_birth) {
		this.date_of_birth = date_of_birth;
		return this;
	}
	
	/**
	 * @param vip {@link #Boolean} VIP status of the transaction.
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest vip(Boolean vip) {
		this.vip = vip;
		return this;
	}
	
	/**
	 * @param registration_no {@link #String}[30] Registration number associated with the transaction. 
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest registration_no(String registration_no) {
		this.registration_no = registration_no;
		return this;
	}
	
	/**
	 * @param registration_date {@link #String}[8] Registration date in MMDDYYYY format. 
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest registration_date(String registration_date) {
		this.registration_date = registration_date;
		return this;
	}
	
	/**
	 * @param clerk_id {@link #String}[6] Telecheck Point of Interaction - Clerk / Agent ID
	 * @return {@link #CheckRequest}
	 */
	public CheckRequest clerk_id(String clerk_id) {
		this.clerk_id = clerk_id;
		return this;
	}
	
	/**
	 * @param device_id {@link #String}[25] Mobile Device ID - IMEI (or IMEISV or MEID or ESN) code of mobile device.
	 * @return
	 */
	public CheckRequest device_id(String device_id) {
		this.device_id = device_id;
		return this;
	}
}
