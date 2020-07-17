package com.osafe.feeds.order;

import javax.xml.bind.annotation.*;
@XmlType(name= "OrderHeader", propOrder = {"orderId", "orderDate", "orderTotalItem", "orderTotalAdjustment", "orderShippingChargeGross", "orderTotalTax", "orderTotalNet", "currency", "itemsToRemove", "validateWebTotals"})
public class OrderHeader {
	private String orderId;
	private String orderDate;
	private String orderTotalItem;
	private String orderTotalAdjustment;
	private String orderShippingChargeGross;
	private String orderTotalTax;
	private String orderTotalNet;
	private String currency;
	private String itemsToRemove;
	private String validateWebTotals;
	
	@XmlElement(name="OrderId")
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderId() {
		return orderId;
	}
	
	@XmlElement(name="OrderDate")
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getOrderDate() {
		return orderDate;
	}
	
	@XmlElement(name="OrderTotalItem")
	public void setOrderTotalItem(String orderTotalItem) {
		this.orderTotalItem = orderTotalItem;
	}
	public String getOrderTotalItem() {
		return orderTotalItem;
	}
	
	@XmlElement(name="OrderTotalAdjustment")
	public void setOrderTotalAdjustment(String orderTotalAdjustment) {
		this.orderTotalAdjustment = orderTotalAdjustment;
	}
	public String getOrderTotalAdjustment() {
		return orderTotalAdjustment;
	}
	
	@XmlElement(name="OrderShippingChargeGross")
	public void setOrderShippingChargeGross(String orderShippingChargeGross) {
		this.orderShippingChargeGross = orderShippingChargeGross;
	}
	public String getOrderShippingChargeGross() {
		return orderShippingChargeGross;
	}
	
	@XmlElement(name="OrderTotalTax")
	public void setOrderTotalTax(String orderTotalTax) {
		this.orderTotalTax = orderTotalTax;
	}
	public String getOrderTotalTax() {
		return orderTotalTax;
	}
	
	@XmlElement(name="OrderTotalNet")
	public void setOrderTotalNet(String orderTotalNet) {
		this.orderTotalNet = orderTotalNet;
	}
	public String getOrderTotalNet() {
		return orderTotalNet;
	}
	
	@XmlElement(name="Currency")
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getCurrency() {
		return currency;
	}
	
	@XmlElement(name="ItemsToRemove")
	public void setItemsToRemove(String itemsToRemove) {
		this.itemsToRemove = itemsToRemove;
	}
	public String getItemsToRemove() {
		return itemsToRemove;
	}
	
	@XmlElement(name="ValidateWebTotals")
	public void setValidateWebTotals(String validateWebTotals) {
		this.validateWebTotals = validateWebTotals;
	}
	public String getValidateWebTotals() {
		return validateWebTotals;
	}
	
}