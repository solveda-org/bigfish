package com.osafe.thirdparty.firstdata.globalgatewaye4;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 * <p>INTENDED FOR DEMONSTRATION PURPOSES ONLY.  NOT FOR PRODUCTION USE.</p>
 */
public class CreditCardType {

	private String _value_;
	
	/**
	 * @param value String value of the credit_card_type transaction parameter.
	 */
	protected CreditCardType(String value) {
		this._value_ = value;
	}
	
	private static final String _AmericanExpress = "American Express";
	private static final String _Visa = "Visa";
	private static final String _Mastercard = "Mastercard";
	private static final String _Discover = "Discover";
	private static final String _DinersClub = "Diners Club";
	private static final String _JCB = "JCB";
	private static final String _GiftCard = "Gift Card";
	private static final String _PayPal = "PayPal";
	private static final String _TeleCheck = "TeleCheck";
	public static final CreditCardType AmericanExpress = new CreditCardType(_AmericanExpress);
	public static final CreditCardType Visa = new CreditCardType(_Visa);
	public static final CreditCardType Mastercard = new CreditCardType(_Mastercard);
	public static final CreditCardType Discover = new CreditCardType(_Discover);
	public static final CreditCardType DinersClub = new CreditCardType(_DinersClub);
	public static final CreditCardType JCB = new CreditCardType(_JCB);
	public static final CreditCardType GiftCard = new CreditCardType(_GiftCard);
	public static final CreditCardType PayPal = new CreditCardType(_PayPal);
	public static final CreditCardType TeleCheck = new CreditCardType(_TeleCheck);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return _value_;
	}
}