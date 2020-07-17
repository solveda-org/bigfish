package com.osafe.feeds.custrequest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"productStore", "contactUsId", "requestCatalogId", "firstName", "lastName", "country", "address1", "address2", "address3", "city", "state", "zip", "emailAddress", "contactPhone", "orderId", "comment"})
public class CustomerRequest {
 
	private String productStore;
	private String contactUsId;
	private String requestCatalogId;
	private String firstName;
	private String lastName;
	private String country;
	private String address1;
	private String address2;
	private String address3;
	private String city;
	private String state;
	private String zip;
	private String emailAddress;
	private String contactPhone;
	private String orderId;
	private String comment;
	
	public String getProductStore() {
		return productStore;
	}
 
	@XmlElement(name="ProductStore")
	public void setProductStore(String productStore) {
		this.productStore = productStore;
	}
	
	@XmlElement(name="ContactUsId")
	public void setContactUsId(String contactUsId) {
		this.contactUsId = contactUsId;
	}
	
	@XmlElement(name="RequestCatalogId")
	public void setRequestCatalogId(String requestCatalogId) {
		this.requestCatalogId = requestCatalogId;
	}

	public String getRequestCatalogId() {
		return requestCatalogId;
	}
	
	public String getContactUsId() {
		return contactUsId;
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
	
	public String getEmailAddress() {
		return emailAddress;
	}
 
	@XmlElement(name="Country")
	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountry() {
		return country;
	}
	
	@XmlElement(name="Address1")
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress1() {
		return address1;
	}

	@XmlElement(name="Address2")
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress2() {
		return address2;
	}

	@XmlElement(name="Address3")
	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getAddress3() {
		return address3;
	}
	
	@XmlElement(name="CityTown")
	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return city;
	}

	@XmlElement(name="StateProvince")
	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	@XmlElement(name="ZipPostCode")
	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getZip() {
		return zip;
	}
	
	@XmlElement(name="EmailAddress")
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@XmlElement(name="ContactPhone")
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	@XmlElement(name="OrderId")
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderId() {
		return orderId;
	}

	@XmlElement(name="Comment")
	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

}