package com.osafe.feeds.custrequest;

import java.util.List;

import javax.xml.bind.annotation.*;
@XmlRootElement(name= "BigFishContactUsFeed")
public class OsafeContactUsFeed {
 
	private List<CustomerRequest> customerRequest;
	
	@XmlElement(name="ContactUs")
	public void setCustomerRequest(List<CustomerRequest> customerRequest) {
		this.customerRequest = customerRequest;
	}

	public List<CustomerRequest> getCustomerRequest() {
		return customerRequest;
	}
}