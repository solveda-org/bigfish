package com.osafe.feeds.order;

import java.util.List;
import javax.xml.bind.annotation.*;

@XmlRootElement(name= "BigfishOrderFeed")
public class OsafeOrderFeed {
 
	private List<Order> order;
	
	@XmlElement(name="Order")
	public void setOrder(List<Order> order) {
		this.order = order;
	}

	public List<Order> getOrder() {
		return order;
	}
}