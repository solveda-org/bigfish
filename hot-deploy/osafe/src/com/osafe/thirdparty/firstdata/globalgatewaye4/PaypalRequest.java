package com.osafe.thirdparty.firstdata.globalgatewaye4;

import com.google.gson.annotations.*;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 * <p>The following PayPal properties are used to record PayPal transactions that have already been processed into the Global Gateway e4 system. To use any of the PayPal transaction types, the "CardType" property must be set to "PayPal". Note that the below properties are only applicable to PayPal Purchase, Pre-Authorization, or Order transactions. To perform a refund, pre-authorization completion, or void against a PayPal transaction, the normal tagged transaction types can be used. Alternately, these types of secondary transactions can be performed through the Realtime Payment Manager interface.</p>
 * <p>NOTE: In order to perform tagged transactions against PayPal transactions via the API, a Payment Page must be set up with the same PayPal credentials as used for the original transactions, and the API terminal used for these tagged transactions must be the same terminal used by the Payment Page for processing. Information on entering these credentials can be found <a href="https://firstdata.zendesk.com/entries/407522-first-data-global-gateway-e4sm-payment-pages-integration-manual#4.2">here</a>.</p>
 *
 * @see <a href="https://firstdata.zendesk.com/entries/407522-first-data-global-gateway-e4sm-payment-pages-integration-manual#4.2">https://firstdata.zendesk.com/entries/407522-first-data-global-gateway-e4sm-payment-pages-integration-manual#4.2</a>
 */
public class PaypalRequest {

	@Expose private String correlation_id;
	@Expose private String timestamp;
	@Expose private String authorization;
	@Expose private String payer_id;
	@Expose private String gross_amount_currency_id;
	@Expose private String success;
	@Expose private String message;
	
	/**
	 * @param correlation_id Correlation ID of a PayPal transaction, which uniquely ties it to PayPal
	 * @return {@link #PaypalRequest}
	 */
	public PaypalRequest correlation_id(String correlation_id) {
		this.correlation_id = correlation_id;
		return this;
	}
	
	/**
	 * @param timestamp Applicable timestamp for a PayPal transaction
	 * @return {@link #PaypalRequest}
	 */
	public PaypalRequest timestamp(String timestamp) {
		this.timestamp = timestamp;
		return this;
	}
	
	/**
	 * @param authorization Authorization data returned from PayPal for a transaction
	 * @return {@link #PaypalRequest}
	 */
	public PaypalRequest authorization(String authorization) {
		this.authorization = authorization;
		return this;
	}
	
	/**
	 * @param payer_id PayerID used for a PayPal transaction
	 * @return {@link #PaypalRequest}
	 */
	public PaypalRequest payer_id(String payer_id) {
		this.payer_id = payer_id;
		return this;
	}
	
	/**
	 * @param gross_amount_currency_id Currency ID applicable for the gross value of a PayPal transaction.
	 * @return {@link #PaypalRequest}
	 */
	public PaypalRequest gross_amount_currency_id(String gross_amount_currency_id) {
		this.gross_amount_currency_id = gross_amount_currency_id;
		return this;
	}
	
	/**
	 * @param success Success indicator of a PayPal transaction
	 * @return {@link #PaypalRequest}
	 */
	public PaypalRequest success(String success) {
		this.success = success;
		return this;
	}
	
	/**
	 * @param message Explanatory message returned by PayPal for a PayPal transaction
	 * @return {@link #PaypalRequest}
	 */
	public PaypalRequest message(String message) {
		this.message = message;
		return this;
	}
}
