package order;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.party.contact.ContactHelper;
import java.util.Map;
import org.ofbiz.base.util.UtilHttp;
import java.math.BigDecimal;
import org.ofbiz.entity.GenericValue;

if (UtilValidate.isNotEmpty(parameters.orderId)) 
{
	orderHeader = delegator.findByPrimaryKey("OrderHeader", [orderId : parameters.orderId]);
}
BigDecimal orderItemAdjustmentTotal = BigDecimal.ZERO;
BigDecimal totalReturns = BigDecimal.ZERO;
if (UtilValidate.isNotEmpty(orderHeader)) 
{
	orderReadHelper = new OrderReadHelper(orderHeader);
	orderItems = orderReadHelper.getOrderItems();
	context.orderReadHelper = orderReadHelper;
	
	orderPayments = orderReadHelper.getOrderPayments();
    for(GenericValue orderPayment : orderPayments)
    {
        if((orderPayment.statusId).equals("PMNT_SENT"))
        {
            totalReturns = totalReturns.add(orderPayment.amount);
        }	
    }
	//totalReturns = orderReadHelper.getOrderReturnedTotal();
	
	returnedQuantityMap = orderReadHelper.getOrderItemReturnedQuantities();
	
	orderItems
}
BigDecimal remainingQuantityPriceTotal = BigDecimal.ZERO;
Map returnedQunatityParmMap = FastMap.newInstance();
for(int i = 0; i < orderItems.size() ; i++)
{
	BigDecimal returnedQuantity = BigDecimal.ZERO;
	BigDecimal returningQuantity = BigDecimal.ZERO;
	BigDecimal remainingQuantity = BigDecimal.ZERO;
	BigDecimal remainingQuantityPrice = BigDecimal.ZERO;
	String orderItemSeqId = parameters.get("orderItemSeqId-"+i);
	
	if(UtilValidate.isNotEmpty(orderItemSeqId))
	{
		orderItem = delegator.findByPrimaryKey("OrderItem", [orderId : parameters.orderId, orderItemSeqId: orderItemSeqId]);
		orderItemAdjustmentTotal = orderItemAdjustmentTotal.add(orderReadHelper.getOrderItemAdjustmentsTotal(orderItem));
		
		returnedQuantity = returnedQuantityMap.get(orderItemSeqId);
		
		try
		{
			if(parameters.get("statusId").equals("PRODUCT_RETURN"))
			{
				returningQuantity = new BigDecimal(parameters.get("returnQuantity_"+i));
			}
			else if(parameters.get("statusId").equals("ORDER_CANCELLED"))
			{
				returningQuantity = orderItem.quantity;
			}
				
		}
		catch(Exception e)
		{
			returningQuantity = BigDecimal.ZERO;
		}
		
		remainingQuantity = orderItem.quantity.subtract(returnedQuantity.add(returningQuantity));
		
		remainingQuantityPrice = remainingQuantity.multiply(orderItem.unitPrice);
		
		remainingQuantityPriceTotal = remainingQuantityPriceTotal.add(remainingQuantityPrice);
		
		returnedQunatityParmMap.put(orderItemSeqId, returnedQuantity);
		
	}
	
}

for(GenericValue orderItem : orderItems)
{
	if(!returnedQunatityParmMap.containsKey(orderItem.orderItemSeqId))
	{
		remainingQuantityPriceTotal = remainingQuantityPriceTotal.add((orderItem.quantity.subtract(returnedQuantityMap.get(orderItem.orderItemSeqId))).multiply(orderItem.unitPrice));
	}
}


context.orderItemAdjustmentTotal = orderItemAdjustmentTotal;
context.totalReturns = totalReturns;
context.remainingQuantityPriceTotal = remainingQuantityPriceTotal;