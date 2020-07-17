package com.osafe.feeds.order;

import java.util.List;
import javax.xml.bind.annotation.*;
import com.osafe.feeds.customer.*;
@XmlType(propOrder = {"productStore", "customer", "orderHeader", "cartPromotion", "orderLineItems", "orderPayment"})
public class Order {
 
	private Customer customer;
	private String productStore;
	private OrderHeader orderHeader; 
	private OrderPayment orderPayment;
	private OrderLineItems orderLineItems;
	private List<CartPromotion> cartPromotion;
	
	@XmlElement(name="ProductStore")
	public void setProductStore(String productStore) {
		this.productStore = productStore;
	}

	public String getProductStore() {
		return productStore;
	}
	
	@XmlElement(name="Customer")
	public Customer getCustomer() { 
	    return customer; 
	}
	
	public void setCustomer(Customer customer) { 
		this.customer = customer; 
    }

	@XmlElement(name="OrderHeader")
	public void setOrderHeader(OrderHeader orderHeader) {
		this.orderHeader = orderHeader;
	}

	public OrderHeader getOrderHeader() {
		return orderHeader;
	}
	
	@XmlElement(name="OrderPayment")
	public void setOrderPayment(OrderPayment orderPayment) {
		this.orderPayment = orderPayment;
	}

	public OrderPayment getOrderPayment() {
		return orderPayment;
	}

	@XmlElement(name="OrderLineItems")
	public void setOrderLineItems(OrderLineItems orderLineItems) {
		this.orderLineItems = orderLineItems;
	}

	public OrderLineItems getOrderLineItems() {
		return orderLineItems;
	}
	
	@XmlElement(name="CartPromotion")
	public void setCartPromotion(List<CartPromotion> cartPromotion) {
		this.cartPromotion = cartPromotion;
	}

	public List<CartPromotion> getCartPromotion() {
		return cartPromotion;
	}
}