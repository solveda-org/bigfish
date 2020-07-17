package com.osafe.feeds.customer;

import java.util.List;

import javax.xml.bind.annotation.*;
@XmlRootElement(name= "BigfishCustomerFeed")
public class OsafeCustomerFeed {
 
	private List<Customer> customer;
	@XmlElement(name="Customer")
	public List<Customer> getCustomer() { 
	    return customer; 
	}
	
	public void setCustomer(List<Customer> customer) { 
		this.customer = customer; 
    }
}