package com.osafe.feeds.order;

import javax.xml.bind.annotation.*;
@XmlType(name= "OrderPayment", propOrder = {"cardType", "last4Digits", "authRequestToken", "authRequestId", "captureRequestToken", "captureRequestId", "expiryDate", "reconcilationId", "subscriptionId", "amount", "authDatetime", "authCode"})
public class OrderPayment {
	private String cardType;
	private String last4Digits;
	private String authRequestToken;
	private String authRequestId;
	private String captureRequestToken;
	private String captureRequestId;
	private String expiryDate;
	private String reconcilationId;
	private String subscriptionId;
	private String amount;
	private String authDatetime;
	private String authCode;
	
	@XmlElement(name="CardType")
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getCardType() {
		return cardType;
	}
	
	@XmlElement(name="Last4Digits")
	public void setLast4Digits(String last4Digits) {
		this.last4Digits = last4Digits;
	}
	public String getLast4Digits() {
		return last4Digits;
	}
	
	@XmlElement(name="AuthRequestToken")
	public void setAuthRequestToken(String authRequestToken) {
		this.authRequestToken = authRequestToken;
	}
	public String getAuthRequestToken() {
		return authRequestToken;
	}
	
	@XmlElement(name="AuthRequestId")
	public void setAuthRequestId(String authRequestId) {
		this.authRequestId = authRequestId;
	}
	public String getAuthRequestId() {
		return authRequestId;
	}
	
	@XmlElement(name="CaptureRequestToken")
	public void setCaptureRequestToken(String captureRequestToken) {
		this.captureRequestToken = captureRequestToken;
	}
	public String getCaptureRequestToken() {
		return captureRequestToken;
	}
	
	@XmlElement(name="CaptureRequestId")
	public void setCaptureRequestId(String captureRequestId) {
		this.captureRequestId = captureRequestId;
	}
	public String getCaptureRequestId() {
		return captureRequestId;
	}
	
	@XmlElement(name="ReconcilationId")
	public void setReconcilationId(String reconcilationId) {
		this.reconcilationId = reconcilationId;
	}
	public String getReconcilationId() {
		return reconcilationId;
	}
	
	@XmlElement(name="ExpiryDate")
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	
	@XmlElement(name="SubscriptionId")
	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	public String getSubscriptionId() {
		return subscriptionId;
	}
	
	@XmlElement(name="Amount")
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getAmount() {
		return amount;
	}
	
	@XmlElement(name="AuthDatetime")
	public void setAuthDatetime(String authDatetime) {
		this.authDatetime = authDatetime;
	}
	public String getAuthDatetime() {
		return authDatetime;
	}
	
	@XmlElement(name="AuthCode")
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	public String getAuthCode() {
		return authCode;
	}
	
}