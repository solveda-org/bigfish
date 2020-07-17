package com.osafe.thirdparty.firstdata.globalgatewaye4;

import java.lang.reflect.Type;

import com.google.gson.*;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 * <p>Transaction Type's available for processing transactions on Global Gateway E4</p>
 * <p>Available choices are:</p>
 * <ul>
 * <li>Purchase</li>
 * <li>PreAuthorization</li>
 * <li>PreAuthorizationCompletion</li>
 * <li>ForcedPost</li>
 * <li>Refund</li>
 * <li>PreAuthorizationOnly</li>
 * <li>PaypalOrder</li>
 * <li>Void</li>
 * <li>TaggedPreAuthorizationCompletion</li>
 * <li>TaggedVoid</li>
 * <li>TaggedRefund</li>
 * <li>VLCashOut</li>
 * <li>VLActivation</li>
 * <li>VLBalanceInquiry</li>
 * <li>VLReload</li>
 * <li>VLDeactivation</li>
 * </ul>
 *
 */
public class TransactionType {
	private String _value_;
	
	protected TransactionType(String value) {
		this._value_ = value;
	}

	private static final String _Purchase = "00";
	private static final String _PreAuthorization = "01";
	private static final String _PreAuthorizationCompletion = "02";
	private static final String _ForcedPost = "03";
	private static final String _Refund = "04";
	private static final String _PreAuthorizationOnly = "05";
	private static final String _PaypalOrder = "07";
	private static final String _Void = "13";
	private static final String _TaggedPreAuthorizationCompletion = "32";
	private static final String _TaggedVoid = "33";
	private static final String _TaggedRefund = "34";
	private static final String _VLCashOut = "83";
	private static final String _VLActivation = "85";
	private static final String _VLBalanceInquiry = "86";
	private static final String _VLReload = "88";
	private static final String _VLDeactivation = "89";
	
	/**
	 * Required data elements for the Purchase TransactionType using a Primary Account Number:
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#cc_number(String)}</li>
	 * <li>{@link Request#cc_expiry(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * </ul>
	 * <p>Required data elements for the Purchase TransactionType using a Transarmor Token:</p>
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#transarmor_token(String)}</li>
	 * <li>{@link Request#cc_expiry(String)}</li>
	 * <li>{@link Request#credit_card_type(CreditCardType)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * </ul>
	 * <p>Required data elements for the Purchase TransactionType using a Valuelink gift card:</p>
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#cc_number(String)}</li>
	 * <li>{@link Request#credit_card_type(CreditCardType.GiftCard)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * </ul>
	 * <p>Required data elements for the Purchase TransactionType using Paypal</p>
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * <li>{@link Request#credit_card_type(CreditCardType.PayPal)}</li>
	 * <li>{@link Request#paypal_transaction_details(PaypalRequest)}</li>
	 * <ul>
	 * 	<li>{@link PaypalRequest#authorization(String)}</li>
	 * 	<li>{@link PaypalRequest#payer_id(String)}</li>
	 *  <li>{@link PaypalRequest#success(String)}</li>
	 *  <li>{@link PaypalRequest#correlation_id(String)}</li>
	 *  <li>{@link PaypalRequest#message(String)}</li>
	 * </ul>
	 * </ul>
	 */
	public static final TransactionType Purchase = new TransactionType(_Purchase);
	
	/**
	 * Required data elements for the PreAuthorization TransactionType using a Primary Account Number:
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#cc_number(String)}</li>
	 * <li>{@link Request#cc_expiry(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * </ul>
	 * <p>Required data elements for the PreAuthorization TransactionType using a Transarmor Token:</p>
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#transarmor_token(String)}</li>
	 * <li>{@link Request#cc_expiry(String)}</li>
	 * <li>{@link Request#credit_card_type(CreditCardType)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * </ul>
	 * <p>Required data elements for the Purchase TransactionType using a Valuelink gift card:</p>
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#cc_number(String)}</li>
	 * <li>{@link Request#credit_card_type(CreditCardType.GiftCard)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * </ul>
	 * <p>Required data elements for the PreAuthorization TransactionType using Paypal</p>
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * <li>{@link Request#credit_card_type(CreditCardType.PayPal)}</li>
	 * <li>{@link Request#paypal_transaction_details(PaypalRequest)}</li>
	 * <ul>
	 * 	<li>{@link PaypalRequest#authorization(String)}</li>
	 * 	<li>{@link PaypalRequest#payer_id(String)}</li>
	 *  <li>{@link PaypalRequest#success(String)}</li>
	 *  <li>{@link PaypalRequest#correlation_id(String)}</li>
	 *  <li>{@link PaypalRequest#message(String)}</li>
	 * </ul>
	 * </ul>
	 */
	public static final TransactionType PreAuthorization = new TransactionType(_PreAuthorization);
	
	/**
	 * Required data elements for the PreAuthorizationCompletion TransactionType using a Primary Account Number:
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#cc_number(String)}</li>
	 * <li>{@link Request#cc_expiry(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * <li>{@link Request#authorization_num(String)}</li>
	 * </ul>
	 * <p>Required data elements for the PreAuthorizationCompletion TransactionType using a Transarmor Token:</p>
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#transarmor_token(String)}</li>
	 * <li>{@link Request#cc_expiry(String)}</li>
	 * <li>{@link Request#credit_card_type(CreditCardType)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * <li>{@link Request#authorization_num(String)}</li>
	 * </ul>
	 */
	public static final TransactionType PreAuthorizationCompletion = new TransactionType(_PreAuthorizationCompletion);
	
	/**
	 * Required data elements for the ForcedPost TransactionType using a Primary Account Number:
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#cc_number(String)}</li>
	 * <li>{@link Request#cc_expiry(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * <li>{@link Request#authorization_num(String)}</li>
	 * </ul>
	 * <p>Required data elements for the ForcedPost TransactionType using a Transarmor Token:</p>
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#transarmor_token(String)}</li>
	 * <li>{@link Request#cc_expiry(String)}</li>
	 * <li>{@link Request#credit_card_type(CreditCardType)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * <li>{@link Request#authorization_num(String)}</li>
	 * </ul>
	 */
	public static final TransactionType ForcedPost = new TransactionType(_ForcedPost);
	
	/**
	 * Required data elements for the Refund TransactionType using a Primary Account Number:
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#cc_number(String)}</li>
	 * <li>{@link Request#cc_expiry(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * </ul>
	 * <p>Required data elements for the Refund TransactionType using a Transarmor Token:</p>
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#transarmor_token(String)}</li>
	 * <li>{@link Request#cc_expiry(String)}</li>
	 * <li>{@link Request#credit_card_type(CreditCardType)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * </ul>
	 */
	public static final TransactionType Refund = new TransactionType(_Refund);
	
	/**
	 * Required data elements for the PreAuthorizationOnly TransactionType:
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#cc_number(String)}</li>
	 * <li>{@link Request#cc_expiry(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * </ul>
	 * <p>Required data elements for the PreAuthorizationOnly TransactionType using a Transarmor Token:</p>
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#transarmor_token(String)}</li>
	 * <li>{@link Request#cc_expiry(String)}</li>
	 * <li>{@link Request#credit_card_type(CreditCardType)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * </ul>
	 */
	public static final TransactionType PreAuthorizationOnly = new TransactionType(_PreAuthorizationOnly);
	
	/**
	 * <p>Required data elements for the PaypalOrder TransactionType</p>
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * <li>{@link Request#credit_card_type(CreditCardType.PayPal)}</li>
	 * <li>{@link Request#paypal_transaction_details(PaypalRequest)}</li>
	 * <ul>
	 * 	<li>{@link PaypalRequest#authorization(String)}</li>
	 * 	<li>{@link PaypalRequest#payer_id(String)}</li>
	 *  <li>{@link PaypalRequest#success(String)}</li>
	 *  <li>{@link PaypalRequest#correlation_id(String)}</li>
	 *  <li>{@link PaypalRequest#message(String)}</li>
	 * </ul>
	 * </ul>
	 */
	public static final TransactionType PaypalOrder = new TransactionType(_PaypalOrder);
	
	/**
	 * Required data elements for the Void TransactionType using a Primary Account Number:
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#cc_number(String)}</li>
	 * <li>{@link Request#cc_expiry(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * <li>{@link Request#authorization_num(String)}</li>
	 * </ul>
	 * <p>Required data elements for the Void TransactionType using a Valuelink gift card:</p>
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#cc_number(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * <li>{@link Request#credit_card_type(CreditCardType.GiftCard)}</li>
	 * <li>{@link Request#authorization_num(String)}</li>
	 * </ul>
	 */
	public static final TransactionType Void = new TransactionType(_Void);
	
	/**
	 * Required data elements for the TaggedPreAuthorizationCompletion TransactionType:
	 * <ul>
	 * <li>{@link Request#transaction_tag(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * <li>{@link Request#authorization_num(String)}</li>
	 * </ul>
	 * Required data elements for the TaggedPreAuthorizationCompletion TransactionType using Paypal:
	 * <ul>
	 * <li>{@link Request#transaction_tag(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * </ul>
	 */
	public static final TransactionType TaggedPreAuthorizationCompletion = new TransactionType(_TaggedPreAuthorizationCompletion);
	
	/**
	 * Required data elements for the TaggedVoid TransactionType:
	 * <ul>
	 * <li>{@link Request#transaction_tag(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * <li>{@link Request#authorization_num(String)}</li>
	 * </ul>
	 * Required data elements for the TaggedVoid TransactionType using Paypal:
	 * <ul>
	 * <li>{@link Request#transaction_tag(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * </ul>
	 */
	public static final TransactionType TaggedVoid = new TransactionType(_TaggedVoid);
	
	/**
	 * Required data elements for the TaggedRefund TransactionType:
	 * <ul>
	 * <li>{@link Request#transaction_tag(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * <li>{@link Request#authorization_num(String)}</li>
	 * </ul>
	 * Required data elements for the TaggedRefund TransactionType using Paypal:
	 * <ul>
	 * <li>{@link Request#transaction_tag(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * </ul>
	 */
	public static final TransactionType TaggedRefund = new TransactionType(_TaggedRefund);
	
	/**
	 * Required data elements for the VLCashOut TransactionType:
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#cc_number(String)}</li>
	 * <li>{@link Request#credit_card_type(CreditCardType.GiftCard)}</li>
	 * </ul>
	 */
	public static final TransactionType VLCashOut = new TransactionType(_VLCashOut);
	
	/**
	 * Required data elements for the VLActivation TransactionType:
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#cc_number(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * <li>{@link Request#card_cost(java.math.BigDecimal)}</li>
	 * <li>{@link Request#credit_card_type(CreditCardType.GiftCard)}</li>
	 * </ul>
	 */
	public static final TransactionType VLActivation = new TransactionType(_VLActivation);
	
	/**
	 * Required data elements for the VLBalanceInquiry TransactionType:
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#cc_number(String)}</li>
	 * <li>{@link Request#credit_card_type(CreditCardType.GiftCard)}</li>
	 * </ul>
	 */
	public static final TransactionType VLBalanceInquiry = new TransactionType(_VLBalanceInquiry);
	
	/**
	 * Required data elements for the VLReload TransactionType:
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#cc_number(String)}</li>
	 * <li>{@link Request#amount(java.math.BigDecimal)}</li>
	 * <li>{@link Request#credit_card_type(CreditCardType.GiftCard)}</li>
	 * </ul>
	 */
	public static final TransactionType VLReload = new TransactionType(_VLReload);
	
	/**
	 * Required data elements for the VLDeactivation TransactionType:
	 * <ul>
	 * <li>{@link Request#cardholder_name(String)}</li>
	 * <li>{@link Request#cc_number(String)}</li>
	 * <li>{@link Request#credit_card_type(CreditCardType.GiftCard)}</li>
	 * </ul>
	 */
	public static final TransactionType VLDeactivation = new TransactionType(_VLDeactivation);
	
	@Override
	public String toString() { 
		return _value_;
	}
}
