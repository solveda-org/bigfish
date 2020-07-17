package com.osafe.feeds.order;

import java.util.List;
import javax.xml.bind.annotation.*;

import com.osafe.feeds.customer.*;
@XmlType()
public class OrderLineItems {
 
	private List<OrderLine> orderLine;

	@XmlElement(name="OrderLine")
	public void setOrderLine(List<OrderLine> orderLine) {
		this.orderLine = orderLine;
	}

	public List<OrderLine> getOrderLine() {
		return orderLine;
	}
}