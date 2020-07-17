package com.osafe.feeds.custrequest;

import java.util.List;

import javax.xml.bind.annotation.*;
@XmlRootElement(name= "BigFishRequestCatalogFeed")
public class OsafeRequestCatalogFeed {
 
	private List<CustomerRequest> customerRequest;
	
	@XmlElement(name="RequestCatalog")
	public void setCustomerRequest(List<CustomerRequest> customerRequest) {
		this.customerRequest = customerRequest;
	}

	public List<CustomerRequest> getCustomerRequest() {
		return customerRequest;
	}
}