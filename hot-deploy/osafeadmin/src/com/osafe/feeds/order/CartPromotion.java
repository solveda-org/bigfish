package com.osafe.feeds.order;

import javax.xml.bind.annotation.*;
@XmlType(propOrder = {"promotionCode", "promotionAmount"})
public class CartPromotion {
	private String promotionCode;
	private String promotionAmount;
	
	@XmlElement(name="PromotionCode")
	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}
	public String getPromotionCode() {
		return promotionCode;
	}
	
	@XmlElement(name="PromotionAmount")
	public void setPromotionAmount(String promotionAmount) {
		this.promotionAmount = promotionAmount;
	}
	public String getPromotionAmount() {
		return promotionAmount;
	}
	
			
}