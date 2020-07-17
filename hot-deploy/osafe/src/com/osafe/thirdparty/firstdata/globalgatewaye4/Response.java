package com.osafe.thirdparty.firstdata.globalgatewaye4;

import java.math.BigDecimal;

import com.google.gson.Gson;
import com.google.gson.annotations.*;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 * <p>INTENDED FOR DEMONSTRATION PURPOSES ONLY.  NOT FOR PRODUCTION USE.</p>
 */
public class Response {

	@Expose private String customer_ref;
	@Expose private String cc_number;
	@Expose private String bank_resp_code_2;
	@Expose private String cavv_algorithm;
	@Expose private String merchant_city;
	@Expose private String merchant_name;
	@Expose private String client_ip;
	@Expose private String sequence_no;
	@Expose private BigDecimal amount_requested;
	@Expose private String currency_code;
	@Expose private String ean;
	@Expose private String pan;
	@Expose private BigDecimal tax2_amount;
	@Expose private String gross_amount_currency_id;
	@Expose private String retrieval_ref_no;
	@Expose private String virtual_card;
	@Expose private String authorization;
	@Expose private Boolean transaction_approved;
	@Expose private String merchant_country;
	@Expose private String bank_message;
	@Expose private String xid;
	@Expose private String error_number;
	@Expose private BigDecimal surcharge_amount;
	@Expose private String credit_card_type;
	@Expose private String correlation_id;
	@Expose private String cvv2;
	@Expose private String track2;
	@Expose private String avs;
	@Expose private BigDecimal tax1_amount;
	@Expose private String user_name;
	@Expose private BigDecimal amount;
	@Expose private String message;
	@Expose private String fraud_suspected;
	@Expose private String merchant_url;
	@Expose private String bank_resp_code;
	@Expose private Boolean transaction_error;
	@Expose private String reference_3;
	@Expose private String cvd_presence_ind;
	@Expose private String transarmor_token;
	@Expose private String partial_redemption;
	@Expose private String merchant_address;
	@Expose private String card_cost;
	@Expose private String gateway_id;
	@Expose private String ctr;
	@Expose private String cardholder_name;
	@Expose private String zip_code;
	@Expose private String track1;
	@Expose private String exact_message;
	@Expose private String secure_auth_result;
	@Expose private String transaction_type;
	@Expose private String cc_verification_str2;
	@Expose private String ecommerce_flag;
	@Expose private String payer_id;
	@Expose private String merchant_province;
	@Expose private String reference_no;
	@Expose private String success;
	@Expose private String logon_message;
	@Expose private String cavv;
	@Expose private String previous_balance;
	@Expose private String cc_expiry;
	@Expose private String tax2_number;
	@Expose private String exact_resp_code;
	@Expose private String merchant_postal;
	@Expose private String transaction_tag;
	@Expose private String timestamp;
	@Expose private String secure_auth_required;
	@Expose private String error_description;
	@Expose private String client_email;
	@Expose private String password;
	@Expose private String cc_verification_str1;
	@Expose private String cavv_response;
	@Expose private String current_balance;
	@Expose private String language;
	@Expose private String authorization_num;
	@Expose private String tax1_number;
	@Expose private SoftDescriptor soft_descriptor;
	@Expose private LevelThree level3;
	// Telecheck Response Fields
	@Expose private String check_number;
	@Expose private String check_type;
	@Expose private String bank_id;
	
	private static GlobalGatewayE4 e4;
	
	public Response(GlobalGatewayE4 e4) {
		this.e4 = e4;
	}
	
	public String toJson() {
		Gson gson = e4.getGson();
		return gson.toJson(this);
	}

	public String customer_ref() {
		return customer_ref;
	}

	public String cc_number() {
		return cc_number;
	}

	public String bank_resp_code_2() {
		return bank_resp_code_2;
	}

	public String cavv_algorithm() {
		return cavv_algorithm;
	}

	public String merchant_city() {
		return merchant_city;
	}

	public String merchant_name() {
		return merchant_name;
	}

	public String client_ip() {
		return client_ip;
	}

	public String sequence_no() {
		return sequence_no;
	}

	public BigDecimal amount_requested() {
		return amount_requested;
	}

	public String currency_code() {
		return currency_code;
	}

	public String ean() {
		return ean;
	}

	public String pan() {
		return pan;
	}

	public BigDecimal tax2_amount() {
		return tax2_amount;
	}

	public String gross_amount_currency_id() {
		return gross_amount_currency_id;
	}

	public String retrieval_ref_no() {
		return retrieval_ref_no;
	}

	public String virtual_card() {
		return virtual_card;
	}

	public String authorization() {
		return authorization;
	}

	public Boolean transaction_approved() {
		return transaction_approved;
	}

	public String merchant_country() {
		return merchant_country;
	}

	public String bank_message() {
		return bank_message;
	}

	public String xid() {
		return xid;
	}

	public String error_number() {
		return error_number;
	}

	public BigDecimal surcharge_amount() {
		return surcharge_amount;
	}

	public String credit_card_type() {
		return credit_card_type;
	}

	public String correlation_id() {
		return correlation_id;
	}

	public String cvv2() {
		return cvv2;
	}

	public String track2() {
		return track2;
	}

	public String avs() {
		return avs;
	}

	public BigDecimal tax1_amount() {
		return tax1_amount;
	}

	public String user_name() {
		return user_name;
	}

	public BigDecimal amount() {
		return amount;
	}

	public String message() {
		return message;
	}

	public String fraud_suspected() {
		return fraud_suspected;
	}

	public String merchant_url() {
		return merchant_url;
	}

	public String bank_resp_code() {
		return bank_resp_code;
	}

	public Boolean transaction_error() {
		return transaction_error;
	}

	public String reference_3() {
		return reference_3;
	}

	public String cvd_presence_ind() {
		return cvd_presence_ind;
	}

	public String transarmor_token() {
		return transarmor_token;
	}

	public String partial_redemption() {
		return partial_redemption;
	}

	public String merchant_address() {
		return merchant_address;
	}

	public String card_cost() {
		return card_cost;
	}

	public String gateway_id() {
		return gateway_id;
	}

	public String ctr() {
		return ctr;
	}

	public String cardholder_name() {
		return cardholder_name;
	}

	public String zip_code() {
		return zip_code;
	}

	public String track1() {
		return track1;
	}

	public String exact_message() {
		return exact_message;
	}

	public String secure_auth_result() {
		return secure_auth_result;
	}

	public String transaction_type() {
		return transaction_type;
	}

	public String cc_verification_str2() {
		return cc_verification_str2;
	}

	public String ecommerce_flag() {
		return ecommerce_flag;
	}

	public String payer_id() {
		return payer_id;
	}

	public String merchant_province() {
		return merchant_province;
	}

	public String reference_no() {
		return reference_no;
	}

	public String success() {
		return success;
	}

	public String logon_message() {
		return logon_message;
	}

	public String cavv() {
		return cavv;
	}

	public String previous_balance() {
		return previous_balance;
	}

	public String cc_expiry() {
		return cc_expiry;
	}

	public String tax2_number() {
		return tax2_number;
	}

	public String exact_resp_code() {
		return exact_resp_code;
	}

	public String merchant_postal() {
		return merchant_postal;
	}

	public String transaction_tag() {
		return transaction_tag;
	}

	public String timestamp() {
		return timestamp;
	}

	public String secure_auth_required() {
		return secure_auth_required;
	}

	public String error_description() {
		return error_description;
	}

	public String client_email() {
		return client_email;
	}

	public String password() {
		return password;
	}

	public String cc_verification_str1() {
		return cc_verification_str1;
	}

	public String cavv_response() {
		return cavv_response;
	}

	public String current_balance() {
		return current_balance;
	}

	public String language() {
		return language;
	}

	public String authorization_num() {
		return authorization_num;
	}

	public String tax1_number() {
		return tax1_number;
	}
	
	public SoftDescriptor soft_descriptor() {
		return soft_descriptor;
	}
	
	public LevelThree level3() {
		return level3;
	}
	
	public String check_number() {
		return check_number;
	}
	
	public String check_type() {
		return check_type;
	}
	
	public String bank_id() {
		return bank_id;
	}
}
