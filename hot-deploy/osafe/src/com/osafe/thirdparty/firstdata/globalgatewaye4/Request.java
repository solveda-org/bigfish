package com.osafe.thirdparty.firstdata.globalgatewaye4;

import java.math.BigDecimal;

import com.google.gson.*;
import com.google.gson.annotations.*;
import com.osafe.thirdparty.firstdata.globalgatewaye4.util.Hmac;
import com.osafe.thirdparty.firstdata.globalgatewaye4.util.Http;
import com.osafe.thirdparty.firstdata.globalgatewaye4.util.Luhn;
import org.apache.commons.lang.Validate;
import org.ofbiz.base.util.Debug;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 * <p>INTENDED FOR DEMONSTRATION PURPOSES ONLY.  NOT FOR PRODUCTION USE.</p>
 */
public class Request {
	
	@Expose private String gateway_id;
	@Expose	private String password;
	@Expose private TransactionType transaction_type;
	@Expose private BigDecimal amount;
	@Expose private String cc_number;
	@Expose private String transaction_tag;
	@Expose private String track1;
	@Expose private String track2;
	@Expose private String authorization_num;
	@Expose private String cc_expiry;
	@Expose private String cardholder_name;
	@Expose private String cc_verification_str1;
	@Expose private String cc_verification_str2;
	@Expose private String cvd_presence_ind;
	@Expose private String reference_no;
	@Expose private String zip_code;
	@Expose private BigDecimal tax1_amount;
	@Expose private String tax1_number;
	@Expose private BigDecimal tax2_amount;
	@Expose private String tax2_number;
	@Expose private String customer_ref;
	@Expose private String reference_3;
	@Expose private String language;
	@Expose private String client_ip;
	@Expose private String client_email;
	@Expose private String user_name;
	@Expose private String currency_code;
	@Expose private Boolean partial_redemption;
	@Expose private String cavv;
	@Expose private String xid;
	@Expose private EcommerceFlagType ecommerce_flag;
	@Expose private String transarmor_token;
	@Expose private CreditCardType credit_card_type;
	@Expose private String ean;
	@Expose private String virtual_card;
	@Expose private BigDecimal card_cost;
	@Expose private String fraud_suspected;
	@Expose private LevelThree level3;
	@Expose private PaypalRequest paypal_transaction_details;
	@Expose private SoftDescriptor soft_descriptor;
	
	private GlobalGatewayE4 e4;
	
	/**
	 * Constructor for a new GGe4 transaction request.
	 * 
	 * @param e4 GlobalGatewayE4 object to be used for the request
	 * @param configuration Configuration ojbect to be used for the request
	 */
	public Request(GlobalGatewayE4 e4, Configuration configuration) {
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
	 * @return {@link #Request}
	 */
	public Request transaction_type(TransactionType transaction_type) {
		this.transaction_type = transaction_type;
		return this;
	}

	/**
	 * @param amount {@link #BigDecimal} dollar amount of transaction.  Max value of 999999.99
	 * @return {@link #Request}
	 */
	public Request amount(BigDecimal amount) {
		Validate.isTrue(amount.compareTo(new BigDecimal("1000000.00")) == -1, "Dollar amount is too large.  Maximum value is 999999.99.");
		this.amount = amount;
		return this;
	}
	
	/**
	 * @param cc_number {@link #String} value of the credit card primary account number.
	 * @return {@link #Request}
	 */
	//@Size(min=14,max=16)
	public Request cc_number(String cc_number) {
		Validate.isTrue(Luhn.luhnTest(cc_number), "cc_number is not valid.");
		this.cc_number = cc_number;
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
	 * @return {@link #Request}
	 */
	public Request transaction_tag(String transaction_tag) {
		Validate.notEmpty(transaction_tag, "transaction_tag cannot be empty.");
		this.transaction_tag = transaction_tag;
		return this;
	}

	/**
	 * @param track1 {@link #String} value of track1 data without sentinel characters.  
	 * Required for {@link TransactionType#Retail} transactions.
	 * @return {@link #Request}
	 */
	public Request track1(String track1) {
		Validate.notEmpty(track1, "track1 cannot be empty.");
		this.track1 = track1;
		return this;
	}

	/**
	 * @param track2 {@link #String} value of track2 data without sentinel characters.  
	 * Required for {@link TransactionType#Retail} transactions.
	 * @return {@link #Request}
	 */
	public Request track2(String track2) {
		Validate.notEmpty(track2, "track2 cannot be empty.");
		this.track2 = track2;
		return this;
	}

	/**
	 * @param authorization_num {@link #String} value of the authorization number. 
	 * <p>Required for the following {@link #TransactionType}s:</p>
	 * <ul>
	 * <li>{@link TransactionType#ForcedPost}</li>
	 * <li>{@link TransactionType#Refund}</li> 
	 * <li>{@link TransactionType#Void}</li>
	 * <li>{@link TransactionType#PreAuthorizationCompletion}</li>
	 * <li>{@link TransactionType#TaggedRefund}</li>
	 * <li>{@link TransactionType#TaggedVoid}</li> 
	 * <li>{@link TransactionType#TaggedPreAuthorizationCompletion}</li>
	 * </ul> 
	 * @return {@link #Request}
	 */
	public Request authorization_num(String authorization_num) {
		Validate.notEmpty(authorization_num, "authorization_num cannot be empty.");
		this.authorization_num = authorization_num;
		return this;
	}

	/**
	 * @param cc_expiry {@link #String}[4] value of the credit card expiration date in MMYY format.
	 * @return {@link #Request}
	 */
	public Request cc_expiry(String cc_expiry) {
		Validate.notEmpty(cc_expiry, "cc_expiry cannot be empty.");
		Validate.isTrue(cc_expiry.length() == 4, "Invalid length for cc_expiry.");
		this.cc_expiry = cc_expiry;
		return this;
	}

	/**
	 * @param cardholder_name {@link #String}[30] value of cardholder's name.  Required.
	 * @return {@link #Request}
	 */
	public Request cardholder_name(String cardholder_name) {
		Validate.notEmpty(cardholder_name, "cardholder_name cannot be empty.");
		Validate.isTrue(cardholder_name.length() <= 30, "cardholder_name contains too many characters.  Less than 30 characters required.");
		this.cardholder_name = cardholder_name;
		return this;
	}

	/**
	 * @param cc_verification_str1 This {@link #String}[40] is populated with the cardholders address information in a specific format. The address is verified and a result is returned (AVS property) that indicates how well the address matched.
	 * <p>VerificationStr1 is comprised of the following constituent address values: Street, Zip/Postal Code, City, State/Provence, Country.  They are separted by the Pipe Character "|".</p>
	 * <p>Street Address|Zip/Postal|City|State/Prov|Country</p>
	 * <p>Required Character Format is ASCII.  40 character maximum.</p>
	 * @return {@link #Request}
	 */
	public Request cc_verification_str1(String cc_verification_str1) {
		Validate.notEmpty(cc_verification_str1, "cc_verification_str1 cannot be empty.");
		Validate.isTrue(cc_verification_str1.length() <= 40, "cc_verification_str1 contains too many characters. Maximum of 40 characters.");
		this.cc_verification_str1 = cc_verification_str1;
		return this;
	}

	/**
	 * @param cc_verification_str2 This is the 0, 3, or 4-digit code on the back of the credit card sometimes called the CVV2 or CVD value.
	 * <p>{@link #cvd_presence_ind} must also be populated when passing this value.</p>
	 * @return {@link #Request}
	 */
	public Request cc_verification_str2(String cc_verification_str2) {
		this.cc_verification_str2 = cc_verification_str2;
		return this;
	}

	/**
	 * @param cvd_presence_ind This number indicates how the CVV2 value should be handled when processing.  This is required for CVV processing.
	 * <p>The value must be either null or the integer 0, 1, 2, or 9. Note that null defaults to 0.</p>
	 * <ul>
	 * <li>Null or 0 = Not Supported (Default)</li>
	 * <li>1 = Value provided by Cardholder</li>
	 * <li>2 = Value provided on card is Illegible</li>
	 * <li>9 = Cardholder states data is not available</li>
	 * </ul>
	 * @return {@link #Request}
	 */
	public Request cvd_presence_ind(String cvd_presence_ind) {
		this.cvd_presence_ind = cvd_presence_ind;
		return this;
	}

	/**
	 * @param reference_no A merchant defined value that can be used to internally identify the transaction. This value is passed through to the Global Gateway e4 unmodified, and may be searched in First Data Global Gateway e4 Real-time Payment Manager (RPM). It is not passed on to the financial institution. The following characters will be stripped from this field: ; ` " / % as well as -- (2 consecutive dashes).
	 * <p>NOTE: For non-international transactions, DO NOT USE the following characters: pipe (|), caret (^), percent symbol (%), backslash (\), or forward slash (/).</p>
	 * <p>For international transactions DO NOT USE the following punctuation: caret (^), backslash (\), openbracket ([), closed bracket (]), tilde (~) or accent key (`). If used the transaction will reject for Response Reason Code 225 (Invalid field data)</p>
	 * @return {@link #Request}
	 */
	public Request reference_no(String reference_no) {
		Validate.isTrue(reference_no.length() <= 20, "reference_no contains too many characters.  Maximum of 20 characters.");
		this.reference_no = reference_no;
		return this;
	}

	/**
	 * @param zip_code Customer zip code used for qualifying transactions, only applicable to merchants passing level 2 (Corporate Credit Card - Level II) data.
	 * <p>Global Gateway E4 only supports Level II processing for Visa and MasterCard. It does not support American Express Level II processing.</p>
	 * @return {@link #Request}
	 */
	public Request zip_code(String zip_code) {
		this.zip_code = zip_code;
		return this;
	}

	/**
	 * @param tax1_amount Tax value included in total amount, only applicable to merchants passing level 2 (Corporate Credit Card - Level II) data. For Canadian merchants this field is the PST amount.
	 * <p>Global Gateway E4 only supports Level II processing for Visa and MasterCard. It does not support American Express Level II processing.</p>
	 * @return {@link #Request}
	 */
	public Request tax1_amount(BigDecimal tax1_amount) {
		this.tax1_amount = tax1_amount;
		return this;
	}

	/**
	 * @param tax1_number Registered number associated with the tax value. Used for reference or government claims purposes and only applicable to merchants passing level 2 (Corporate Credit Card - Level II) data. For Canadian merchants this field is the PST number.
	 * <p>Global Gateway E4 only supports Level II processing for Visa and MasterCard. It does not support American Express Level II processing.</p>
	 * @return {@link #Request}
	 */
	public Request tax1_number(String tax1_number) {
		this.tax1_number = tax1_number;
		return this;
	}

	/**
	 * @param tax2_amount Tax value included in total amount, only applicable to merchants passing level 2 (Corporate Credit Card - Level II) data. For Canadian merchants this field is the GST amount.
	 * <p>Global Gateway E4 only supports Level II processing for Visa and MasterCard. It does not support American Express Level II processing.</p>
	 * @return {@link #Request}
	 */
	public Request tax2_amount(BigDecimal tax2_amount) {
		this.tax2_amount = tax2_amount;
		return this;
	}

	/**
	 * @param tax2_number Registered number associated with the tax value. Used for reference or government claims purposes and only applicable to merchants passing level 2 (Corporate Credit Card - Level II) data. For Canadian merchants this field is the GST number.
	 * <p>Global Gateway E4 only supports Level II processing for Visa and MasterCard. It does not support American Express Level II processing.</p>
	 * @return {@link #Request}
	 */
	public Request tax2_number(String tax2_number) {
		this.tax2_number = tax2_number;
		return this;
	}

	/**
	 * @param customer_ref {@link #String}[20] A merchant defined value that can be used to internally identify the transaction. This value is passed through to the Global Gateway E4 unmodified, and may be searched in First Data Global Gateway E4 Real-time Payment Manager (RPM). It is not passed on to the financial institution. The following characters will be stripped from this field: ; ` " / % as well as -- (2 consecutive dashes).
	 * <p>Required Character Format is ASCII.</p>
	 * @return {@link #Request}
	 */
	public Request customer_ref(String customer_ref) {
		Validate.isTrue(customer_ref.length() <= 20, "customer_ref contains too many characters.  Maximum of 20 characters.");
		this.customer_ref = customer_ref;
		return this;
	}

	/**
	 * @param reference_3 {@link #String}[30] A merchant defined value that can be used to internally identify the transaction. This value is passed through to the Global Gateway E4 unmodified. It is not searchable and is not passed on to the financial institution. The following characters will be stripped from this field: ; ` " / % as well as -- (2 consecutive dashes). Also used to pass on the Foreign Access Code for ValueLink transactions.
	 * <p>Required Character Format is ASCII.</p>
	 * @return {@link #Request}
	 */
	public Request reference_3(String reference_3) {
		Validate.isTrue(reference_3.length() <= 20,  "reference_3 contains too many characters.  Maximum of 30 characters.");
		this.reference_3 = reference_3;
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
	 * @return {@link #Request}
	 */
	public Request language(String language) {
		this.language = language;
		return this;
	}

	/**
	 * @param client_ip {@link #String}[15] This is the IP address of the customer (i.e. client browser) connecting to the merchant. This value is stored for fraud investigation. It is not passed on to the financial institution.
	 * @return {@link #Request}
	 */
	public Request client_ip(String client_ip) {
		this.client_ip = client_ip;
		return this;
	}

	/**
	 * @param client_email {@link #String}[30] This is the email address of the customer connecting to the merchant. This value is stored for fraud investigation. It is not passed on to the financial institution.
	 * @return {@link #Request}
	 */
	public Request client_email(String client_email) {
		this.client_email = client_email;
		return this;
	}
	
	/**
	 * @param user_name {@link #String}[30] This is the user_name of the user processing the transaction.  This field is visible in the Real Time Payment manager as the "User ID" and defaults to "API-(ExactID)". 
	 * @return {@link #Request}
	 */
	public Request user_name(String user_name) {
		this.user_name = user_name;
		return this;
	}

	/**
	 * @param currency_code {@link #String}[3] A currency code used for charging the transaction.
	 * @see <a href="https://firstdata.zendesk.com/entries/450214-supported-currencies">https://firstdata.zendesk.com/entries/450214-supported-currencies</a>
	 * @return {@link #Request}
	 */
	public Request currency_code(String currency_code) {
		this.currency_code = currency_code;
		return this;
	}

	/**
	 * @param partial_redemption {@link #Boolean} Submit true to allow partial redemptions, false otherwise. A partial redemption will be returned if only a portion of the requested funds are available. For example, if a transaction is submitted for $100, but only $80 is available on the customer's card, the $80 will be authorized or captured when this property is set to true. This property can be used for all types of pre-authorization and purchase transactions.
	 * @return {@link #Request}
	 */
	public Request partial_redemption(Boolean partial_redemption) {
		this.partial_redemption = partial_redemption;
		return this;
	}

	/**
	 * @param cavv {@link #String}[40] 3-D Secure/Verified by Visa value returned by Cardinal Commerce
	 * @return {@link #Request}
	 */
	public Request cavv(String cavv) {
		this.cavv = cavv;
		return this;
	}

	/**
	 * @param xid 3-D {@link #String}[40] Secure/Verified by Visa value returned by Cardinal Commerce
	 * @return {@link #Request}
	 */
	public Request xid(String xid) {
		this.xid = xid;
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
	 * @return {@link #Request}
	 * @see com.firstdata.globalgatewaye4.EcommerceFlagType#EcommerceFlagType EcommerceFlagType
	 */
	public Request ecommerce_flag(EcommerceFlagType ecommerce_flag) {
		this.ecommerce_flag = ecommerce_flag;
		return this;
	}

	/**
	 * @param transarmor_token Used to submit a TransArmor token for transaction processing rather than using a credit card number for processing. If using this property, CardType must also be populated.
	 * <p>Note that when a terminal is set up with TransArmor, a value will be returned in this property each time a transaction is submitted using a regular card number.</p>
	 * @return {@link #Request}
	 */
	public Request transarmor_token(String transarmor_token) {
		Validate.notEmpty(transarmor_token, "transarmor_token cannot be empty.");
		this.transarmor_token = transarmor_token;
		return this;
	}

	/**
	 * @param credit_card_type CardType value associated with a TransArmor token, PayPal transaction, or if submitting a ValueLink transaction, this property's value is required to be "Gift." Required for TransArmor transactions, PayPal transactions, and ValueLink transactions that submit a card number. This property can only be used with the v9 or higher endpoint of the API.
	 * <p>Note that when a terminal is set up with TransArmor, a value will be returned in this property each time a transaction is submitted using a regular card number.</p>
	 * @return {@link #Request}
	 * @see com.firstdata.globalgatewaye4.CreditCardType#CreditCardType CreditCardType
	 */
	public Request credit_card_type(CreditCardType credit_card_type) {
		this.credit_card_type = credit_card_type;
		return this;
	}

	/**
	 * @param ean ValueLink card verification value similar to CVV2 numbers on the back of credit cards. This can only be used for ValueLink transactions.
	 * @return {@link #Request}
	 */
	public Request ean(String ean) {
		this.ean = ean;
		return this;
	}

	/**
	 * @param virtual_card Flag used to indicate whether a ValueLink card is virtual or not. This can only be used for ValueLink transactions.
	 * @return {@link #Request}
	 */
	public Request virtual_card(String virtual_card) {
		this.virtual_card = virtual_card;
		return this;
	}

	/**
	 * @param card_cost {@link #String} Used for ValueLink activation transactions. This can only be used for ValueLink transactions.
	 * @return {@link #Request}
	 */
	public Request card_cost(BigDecimal card_cost) {
		this.card_cost = card_cost;
		return this;
	}

	/**
	 * @param fraud_suspected {@link #String}  This property can only be used with the refund or void transaction types, and is only applicable to MasterCard transactions using the v12 endpoint. When fraud is suspected for a MasterCard transaction, this property should be populated with a value of "200" when issuing the refund.
	 * @return {@link #Request}
	 */
	public Request fraud_suspected(String fraud_suspected) {
		this.fraud_suspected = fraud_suspected;
		return this;
	}

	/**
	 * @param level3 {@link LevelThree} object containing Level Three transaction information.
	 * @return {@link #Request}
	 * @see com.firstdata.globalgatewaye4.LevelThree#LevelThree LevelThree
	 */
	public Request level3(LevelThree level3) {
		this.level3 = level3;
		return this;
	}
	
	/**
	 * @param paypalRequest {@link #PaypalRequest} object containing Paypal request information.
	 * @return {@link #Request}
	 * @see com.firstdata.globalgatewaye4.PaypalRequest#PaypalRequest PaypalRequest
	 */
	public Request paypal_transaction_details(PaypalRequest paypalRequest) {
		this.paypal_transaction_details = paypalRequest;
		return this;
	}
	
	/**
	 * @param softDescriptor {@link #SoftDescriptor} object containing soft merchant information.
	 * @return {@link #Request}
	 * @see com.firstdata.globalgatewaye4.SoftDescriptor#SoftDescriptor SoftDescriptor
	 */
	public Request soft_descriptor(SoftDescriptor softDescriptor) {
		this.soft_descriptor = softDescriptor;
		return this;
	}
}
