package com.osafe.thirdparty.firstdata.globalgatewaye4;

import java.math.BigDecimal;

/**
 * @author Steve Copous <steve.copous@firstdata.com>
 * <p>INTENDED FOR DEMONSTRATION PURPOSES ONLY.  NOT FOR PRODUCTION USE.</p>
 */
public class DemoValues {

	public enum CreditCardNumber {
		VISA("4111111111111111"),
		MASTERCARD("5500000000000004"),
		AMEX("340000000000009"),
		JCB("3566002020140006"),
		DISCOVER("6011000000000004"),
		DINERS("36438999960016");
		
		public String number;
		
		private CreditCardNumber(String number) {
			this.number = number;
		}
	}
	
	public enum TransactionAmount {
		APPROVAL("5000.00"),
		DECLINE("5200.00");
		
		public BigDecimal amount;
		
		private TransactionAmount(String amount) {
			this.amount = new BigDecimal(amount);
		}
	}
}
