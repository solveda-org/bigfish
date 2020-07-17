package com.osafe.thirdparty.firstdata.globalgatewaye4;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 *
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
 * @see <a href="https://firstdata.zendesk.com/entries/21531261-Ecommerce-Flag-Values">https://firstdata.zendesk.com/entries/21531261-Ecommerce-Flag-Values</a>
 */
public class EcommerceFlagType {

	private String _value_;
	
	/**
	 * @param value String value of the ecommerce_flag transaction parameter.
	 * 
	 */
	protected EcommerceFlagType(String value) {
		this._value_ = value;
	}
	
	private static String _MOTOSingle = "1";
	private static String _MOTORecurring = "2";
	private static String _MOTOInstallment = "3";
	private static String _MOTODeferred = "4";
	private static String _ECISecure = "5";
	private static String _ECINonAuth = "6";
	private static String _ECIEncrypted = "7";
	private static String _ECINonSecure = "8";
	private static String _IVR = "I";
	private static String _Retail = "R";
	/**
	 * <p>Single Transaction mail/telephone order: designates a transaction where the cardholder is 
	 * not present at a merchant location and consummates the sale via the phone or through the mail. 
	 * The transaction is not for recurring services or product and does not include sales that are 
	 * processed via an installment plan.</p>
	 */
	public static EcommerceFlagType MOTOSingle = new EcommerceFlagType(_MOTOSingle);
	
	/**
	 * <p>Recurring Transaction: designates a transaction that represents an arrangement between a 
	 * cardholder and the merchant where transactions are going to occur on a periodic basis.</p>
	 */
	public static EcommerceFlagType MOTORecurring = new EcommerceFlagType(_MOTORecurring);
	
	/**
	 * <p>Installment Payment: designates a group of transactions that originated from a single 
	 * purchase where the merchant agrees to bill the cardholder in installments.</p>
	 */
	public static EcommerceFlagType MOTOInstallment = new EcommerceFlagType(_MOTOInstallment);
	
	/**
	 * <p>Deferred Transaction: designates a transaction that represents an order with a delayed 
	 * payment for a specified amount of time.</p>
	 */
	public static EcommerceFlagType MOTODeferred = new EcommerceFlagType(_MOTODeferred);
	
	/**
	 * <p>Secure Electronic Commerce Transaction: designates a transaction between a cardholder 
	 * and a merchant consummated via the Internet where the transaction was successfully authenticated 
	 * and includes the management of a cardholder certificate. (e.g. 3-D Secure Transactions)</p>
	 */
	public static EcommerceFlagType ECISecure = new EcommerceFlagType(_ECISecure);
	
	/**
	 * <p>Non-Authenticated Electronic Commerce Transaction: designates a transaction consummated via 
	 * the Internet at a 3-D Secure capable merchant that attempted to authenticate the cardholder 
	 * using 3-D Secure. (e.g. 3-D Secure includes Verified by Visa and MasterCard SecureCode)</p>
	 * <p>Attempts occur with Verified by Visa and MasterCard SecureCode transactions in the event of:</p>
	 * <ul>
	 * <li>a. A non-participating Issuer</li>
	 * <li>b. A non-participating cardholder of a participating Issuer</li>
	 * <li>c. A participating Issuer, but the authentication server is not available</li>
	 * </ul>
	 */
	public static EcommerceFlagType ECINonAuth = new EcommerceFlagType(_ECINonAuth);
	
	/**
	 * <p>Channel Encrypted Transaction: designates a transaction between a cardholder and a merchant 
	 * consummated via the Internet where the transaction includes the use of transaction encryption 
	 * such as SSL, but authentication was not performed. The cardholder payment data was protected 
	 * with a form of Internet security, such as SSL, but authentication was not performed.</p>
	 */
	public static EcommerceFlagType ECIEncrypted = new EcommerceFlagType(_ECIEncrypted);
	
	/**
	 * <p>Non-Secure Electronic Commerce Transaction: designates a transaction between a cardholder 
	 * and a merchant consummated via the Internet where the transaction does not include the use of 
	 * any transaction encryption such as SSL, no authentication performed, no management of a cardholder 
	 * certificate.</p>
	 */
	public static EcommerceFlagType ECINonSecure = new EcommerceFlagType(_ECINonSecure);
	
	/**
	 * <p>(PINless Debit only): designates a transaction where the cardholder consummates the 
	 * sale via an interactive voice response (IVR) system.</p>
	 * <p>If an "I" is sent, but MOP is not equal to PINless debit, the transaction will reject for 
	 * Response Reason Code 253 (Invalid Transaction Type)</p>
	 */
	public static EcommerceFlagType IVR = new EcommerceFlagType(_IVR);
	
	/**
	 * Retail Indicator designates a transaction where the cardholder was present at a merchant location.
	 * If an "R" is sent for a transaction with a MOTO Merchant Category Code MCC the transaction will downgrade.
	 */
	public static EcommerceFlagType Retail = new EcommerceFlagType(_Retail);
	
	public String toString() {
		return _value_;
	}
}
