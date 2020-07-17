package com.osafe.feeds.customer;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
@XmlType(propOrder={"productStore", "customerId", "firstName", "lastName", "gender", "dateRegistered", "emailAddress", "emailFormat", "emailOptin", "billingAddress", "shippingAddress"})
public class Customer {
 
	private String productStore;
	private String customerId;
	private String firstName;
	private String lastName;
	private String gender;
	private String dateRegistered;
	private String emailAddress;
	private String emailFormat;
	private String emailOptin;
	private List<Address> billingAddress;
	private List<Address> shippingAddress;
	
	public String getProductStore() {
		return productStore;
	}
 
	@XmlElement(name="ProductStore")
	public void setProductStore(String productStore) {
		this.productStore = productStore;
	}

	public String getCustomerId() {
		return customerId;
	}
 
	@XmlElement(name="CustomerId")
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	public String getFirstName() {
		return firstName;
	}
 
	@XmlElement(name="FirstName")
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
 
	@XmlElement(name="LastName")
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getGender() {
		return gender;
	}
 
	@XmlElement(name="Gender")
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}
 
	@XmlElement(name="EmailAddress")
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public String getEmailFormat() {
		return emailFormat;
	}
 
	@XmlElement(name="EmailFormat")
	public void setEmailFormat(String emailFormat) {
		this.emailFormat = emailFormat;
	}
	
	public String getEmailOptin() {
		return emailOptin;
	}
 
	@XmlElement(name="EmailOptin")
	public void setEmailOptin(String emailOptin) {
		this.emailOptin = emailOptin;
	}
	
	@XmlElement(name= "BillingAddress")
	public List<Address> getBillingAddress() { 
	    return billingAddress; 
	}
	
	public void setBillingAddress(List<Address> billingAddress) { 
		this.billingAddress = billingAddress; 
    }
	
	@XmlElement(name= "ShippingAddress")
	public List<Address> getShippingAddress() { 
	    return shippingAddress; 
	}
	
	public void setShippingAddress(List<Address> shippingAddress) { 
		this.shippingAddress = shippingAddress; 
    }
	
	@XmlElement(name= "DateRegistered")
	public void setDateRegistered(String dateRegistered) {
		this.dateRegistered = dateRegistered;
	}

	public String getDateRegistered() {
		return dateRegistered;
	}
}