package com.osafe.feeds.order;

import javax.xml.bind.annotation.*;
@XmlType(name= "OrderLine", propOrder = {"lineNumber", "orderLineId", "productId", "quantity", "price", "lineTotalGross", "salesTax", "shippingCharge", "shippingTax", "carrier", "carrierCustomInfo"})
public class OrderLine {
	private String lineNumber;
	private String orderLineId;
	private String productId;
	private int quantity;
	private String price;
	private String lineTotalGross;
	private String salesTax;
	private String shippingCharge;
	private String shippingTax;
	private String carrier;
	private String carrierCustomInfo;
	
	@XmlElement(name="LineNumber")
	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}
	public String getLineNumber() {
		return lineNumber;
	}
	
	@XmlElement(name="OrderLineId")
	public void setOrderLineId(String orderLineId) {
		this.orderLineId = orderLineId;
	}
	public String getOrderLineId() {
		return orderLineId;
	}
	
	@XmlElement(name="ProductId")
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductId() {
		return productId;
	}
	
	@XmlElement(name="Quantity")
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getQuantity() {
		return quantity;
	}
	
	@XmlElement(name="Price")
	public void setPrice(String price) {
		this.price = price;
	}
	public String getPrice() {
		return price;
	}
	
	@XmlElement(name="LineTotalGross")
	public void setLineTotalGross(String lineTotalGross) {
		this.lineTotalGross = lineTotalGross;
	}
	public String getLineTotalGross() {
		return lineTotalGross;
	}
	
	@XmlElement(name="SalesTax")
	public void setSalesTax(String salesTax) {
		this.salesTax = salesTax;
	}
	public String getSalesTax() {
		return salesTax;
	}
	
	@XmlElement(name="ShippingCharge")
	public void setShippingCharge(String shippingCharge) {
		this.shippingCharge = shippingCharge;
	}
	public String getShippingCharge() {
		return shippingCharge;
	}
	
	@XmlElement(name="ShippingTax")
	public void setShippingTax(String shippingTax) {
		this.shippingTax = shippingTax;
	}
	public String getShippingTax() {
		return shippingTax;
	}
	
	@XmlElement(name="Carrier")
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	public String getCarrier() {
		return carrier;
	}
	
	@XmlElement(name="CarrierCustomInfo")
	public void setCarrierCustomInfo(String carrierCustomInfo) {
		this.carrierCustomInfo = carrierCustomInfo;
	}
	public String getCarrierCustomInfo() {
		return carrierCustomInfo;
	}
		
}