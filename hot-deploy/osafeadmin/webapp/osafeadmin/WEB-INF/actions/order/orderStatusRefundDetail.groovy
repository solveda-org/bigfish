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
import org.ofbiz.base.util.UtilMisc;

if (UtilValidate.isNotEmpty(parameters.orderId)) 
{
	orderHeader = delegator.findByPrimaryKey("OrderHeader", [orderId : parameters.orderId]);
}

statusId = parameters.statusId;
BigDecimal originalOrderShippingAmount = BigDecimal.ZERO;
BigDecimal originalOrderTaxAmount = BigDecimal.ZERO;
BigDecimal originalOrderPromoAmount = BigDecimal.ZERO;
BigDecimal originalOrderOtherAmount = BigDecimal.ZERO;

BigDecimal priorItemAdjustmentTotal = BigDecimal.ZERO;
BigDecimal priorPromoAdjustmentTotal = BigDecimal.ZERO;
BigDecimal priorShippingAdjustmentTotal = BigDecimal.ZERO;
BigDecimal priorTaxAdjustmentTotal = BigDecimal.ZERO;
BigDecimal priorMiscAdjustmentTotal = BigDecimal.ZERO;
BigDecimal totalChargeRefunded = BigDecimal.ZERO;
List<String> processedReturnIds = FastList.newInstance();
if (UtilValidate.isNotEmpty(orderHeader)) 
{
	
	orderReadHelper = new OrderReadHelper(orderHeader);
	orderAdjustments = orderReadHelper.getAdjustments();
	context.orderAdjustments = orderAdjustments;
	orderPaymentPreferences = orderReadHelper.getPaymentPreferences();
	orderPaymentPreferences = EntityUtil.filterByAnd(orderPaymentPreferences, UtilMisc.toMap("statusId", "PAYMENT_SETTLED"));

	currencyUomId = orderReadHelper.getCurrency();
	orderItems = orderReadHelper.getOrderItems();
	context.orderReadHelper = orderReadHelper;
	context.orderHeader = orderHeader;
	orderShippingAdjustments = EntityUtil.filterByAnd(orderAdjustments, UtilMisc.toMap("orderAdjustmentTypeId", "SHIPPING_CHARGES"));
	if(UtilValidate.isNotEmpty(orderShippingAdjustments))
	{
		for(GenericValue orderShippingAdjustment : orderShippingAdjustments)
		{
			if(!((orderShippingAdjustment.getBigDecimal("amount")).compareTo(BigDecimal.ZERO) < 0))
			{
				originalOrderShippingAmount = originalOrderShippingAmount.add(orderShippingAdjustment.getBigDecimal("amount"));
			}
		}
	}
	orderTaxAdjustments = EntityUtil.filterByAnd(orderAdjustments, UtilMisc.toMap("orderAdjustmentTypeId", "SALES_TAX"));
	if(UtilValidate.isNotEmpty(orderTaxAdjustments))
	{
		for(GenericValue orderTaxAdjustment : orderTaxAdjustments)
		{
			if(UtilValidate.isNotEmpty(orderTaxAdjustment.getString("taxAuthorityRateSeqId")))
			{
				originalOrderTaxAmount = originalOrderTaxAmount.add(orderTaxAdjustment.getBigDecimal("amount"));
			}
		}
	}
	
	orderPromoAdjustments = EntityUtil.filterByAnd(orderAdjustments, UtilMisc.toMap("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT"));
	if(UtilValidate.isNotEmpty(orderPromoAdjustments))
	{
		for(GenericValue orderPromoAdjustment : orderPromoAdjustments)
		{
			if(UtilValidate.isNotEmpty(orderPromoAdjustment.getString("productPromoId")))
			{
				originalOrderPromoAmount = originalOrderPromoAmount.add(orderPromoAdjustment.getBigDecimal("amount"));
			}
		}
	}
	if(UtilValidate.isNotEmpty(orderAdjustments))
	{
		for(GenericValue orderAdjustment : orderAdjustments)
		{
			String orderAdjustmentTypeId=orderAdjustment.getString("orderAdjustmentTypeId");
			if (!orderAdjustmentTypeId.equalsIgnoreCase("PROMOTION_ADJUSTMENT") && !orderAdjustmentTypeId.equalsIgnoreCase("SALES_TAX") && !orderAdjustmentTypeId.equalsIgnoreCase("SHIPPING_CHARGES"))
			{
				if(orderAdjustmentTypeId.equalsIgnoreCase("PROMOTION_ADJUSTMENT"))
				{
					if(UtilValidate.isEmpty(orderAdjustment.getString("productPromoId")))
					{
						originalOrderOtherAmount = originalOrderOtherAmount.add(orderAdjustment.getBigDecimal("amount"));
					}
				}
				else
				{
					originalOrderOtherAmount = originalOrderOtherAmount.add(orderAdjustment.getBigDecimal("amount"));
					
				}
				
			}
		}
	}
	if(UtilValidate.isNotEmpty(orderPaymentPreferences))
	{
		for(GenericValue orderPaymentPreference : orderPaymentPreferences)
		{
			preferencePayments = orderPaymentPreference.getRelatedByAnd("Payment", ["paymentTypeId" : "CUSTOMER_REFUND","statusId" : "PMNT_SENT"]);
			if(UtilValidate.isNotEmpty(preferencePayments))
			{
				for(GenericValue payment : preferencePayments)
				{
					paymentGatewayResponse = payment.getRelatedOne("PaymentGatewayResponse");
					totalChargeRefunded = totalChargeRefunded.add(paymentGatewayResponse.getBigDecimal("amount"));
				}
			}
		}
	}
	
	if(statusId.equalsIgnoreCase("PRODUCT_RETURN"))
	{
		List<GenericValue> returnOrderItems = orderReadHelper.getOrderReturnItems();
		if(UtilValidate.isNotEmpty(returnOrderItems))
		{
			for(GenericValue returnOrderItem : returnOrderItems)
			{
				priorItemAdjustmentTotal = priorItemAdjustmentTotal.add((returnOrderItem.returnQuantity).multiply(returnOrderItem.returnPrice));
				
				//Calculate Return Adjustments
				if(!processedReturnIds.contains(returnOrderItem.returnId))
				{
					List<GenericValue> returnAdjustmets = delegator.findByAnd("ReturnAdjustment", UtilMisc.toMap("returnId", returnOrderItem.returnId));
					if(UtilValidate.isNotEmpty(returnAdjustmets))
					{
						//Shipping Adjustments
						List<GenericValue> returnAdjustmetsShipping = EntityUtil.filterByAnd(returnAdjustmets, UtilMisc.toMap("returnId",returnOrderItem.returnId, "returnTypeId", "RTN_REFUND", "returnAdjustmentTypeId", "RET_SHIPPING_ADJ"));
						if(UtilValidate.isNotEmpty(returnAdjustmetsShipping))
						{
							for(GenericValue returnAdjustmetShipping : returnAdjustmetsShipping)
							{
								priorShippingAdjustmentTotal = priorShippingAdjustmentTotal.add(returnAdjustmetShipping.amount);
							}
						}
						
						//Promo Adjustments
						List<GenericValue> returnAdjustmetsPromo = EntityUtil.filterByAnd(returnAdjustmets, UtilMisc.toMap("returnId",returnOrderItem.returnId, "returnTypeId", "RTN_REFUND", "returnAdjustmentTypeId", "RET_PROMOTION_ADJ"));
						if(UtilValidate.isNotEmpty(returnAdjustmetsPromo))
						{
							for(GenericValue returnAdjustmetPromo : returnAdjustmetsPromo)
							{
								priorPromoAdjustmentTotal = priorPromoAdjustmentTotal.add(returnAdjustmetPromo.amount);
							}
						}
						
						//Sales Tax Adjustments
						List<GenericValue> returnAdjustmetsTax = EntityUtil.filterByAnd(returnAdjustmets, UtilMisc.toMap("returnId",returnOrderItem.returnId, "returnTypeId", "RTN_REFUND", "returnAdjustmentTypeId", "RET_SALES_TAX_ADJ"));
						if(UtilValidate.isNotEmpty(returnAdjustmetsTax))
						{
							for(GenericValue returnAdjustmetTax : returnAdjustmetsTax)
							{
								priorTaxAdjustmentTotal = priorTaxAdjustmentTotal.add(returnAdjustmetTax.amount);
							}
						}
						
						//Misc Adjustments
						List<GenericValue> returnAdjustmentsMisc = EntityUtil.filterByAnd(returnAdjustmets, UtilMisc.toMap("returnId",returnOrderItem.returnId, "returnTypeId", "RTN_REFUND", "returnAdjustmentTypeId", "RET_MAN_ADJ"));
						if(UtilValidate.isNotEmpty(returnAdjustmentsMisc))
						{
							for(GenericValue returnAdjustmentMisc : returnAdjustmentsMisc)
							{
								priorMiscAdjustmentTotal = priorMiscAdjustmentTotal.add(returnAdjustmentMisc.amount);
							}
						}
					}
					processedReturnIds.add(returnOrderItem.returnId);
				}
			}
		}
	}
	
}

BigDecimal originalOrderItemSubtotal = BigDecimal.ZERO;

BigDecimal cancelOrderItemSubtotal = BigDecimal.ZERO;
BigDecimal returnOrderItemSubtotal = BigDecimal.ZERO;

List orderItemSequenceIds = FastList.newInstance();
Map returnItemSeqIdQuantityMap = FastMap.newInstance();
List<GenericValue> returnItems = FastList.newInstance();

for(int i = 0; i < orderItems.size() ; i++)
{
	originalOrderItem = orderItems.get(i);
	
	String orderItemSeqId = parameters.get("orderItemSeqId-"+i);
	
	String returnQuantity = parameters.get("returnQuantity_"+i);
	
	originalOrderItemTotal = (originalOrderItem.quantity).multiply(originalOrderItem.unitPrice);
	
	originalOrderItemSubtotal = originalOrderItemSubtotal + originalOrderItemTotal;
	
	if(UtilValidate.isNotEmpty(orderItemSeqId))
	{
		orderItemSequenceIds.add(orderItemSeqId);
		
		orderItem = delegator.findByPrimaryKey("OrderItem", [orderId : parameters.orderId, orderItemSeqId: orderItemSeqId]);

		//CALCULATE FOR ITEM CANCEL
		if(statusId.equalsIgnoreCase("ORDER_CANCELLED"))
		{
		    orderItemTotal = (orderItem.quantity).multiply(orderItem.unitPrice);
		    cancelOrderItemSubtotal = cancelOrderItemSubtotal + orderItemTotal;
		}
		
		
		//CALCULATE FOR ITEM RETURN
		if(statusId.equalsIgnoreCase("PRODUCT_RETURN") && UtilValidate.isNotEmpty(returnQuantity))
		{
			BigDecimal returnQuantityBD = BigDecimal.ZERO;
			try
			{
				returnQuantityBD = new BigDecimal(returnQuantity);
			}
			catch(NumberFormatException nfe)
			{
				// Exception Handelled
			}
			returnOrderItemTotal = (returnQuantityBD).multiply(orderItem.unitPrice);
			returnOrderItemSubtotal = returnOrderItemSubtotal + returnOrderItemTotal;
			returnItems.add(orderItem);
			returnItemSeqIdQuantityMap.put(orderItem.orderItemSeqId, returnQuantityBD);
		}
	}
}

//Calculate the Adjust Shipping Amount
BigDecimal adjustmentAmountTotalShipping = BigDecimal.ZERO;
if(statusId.equalsIgnoreCase("ORDER_CANCELLED"))
{
	Map svcShippingCtx = FastMap.newInstance();
	svcShippingCtx.put("orderId", parameters.orderId);
	svcShippingCtx.put("orderItemSequenceIds", orderItemSequenceIds);
	
	recalcOrderShippingAmountRes = dispatcher.runSync("recalcOrderShippingAmount", svcShippingCtx);
	adjustmentAmountTotalShipping = recalcOrderShippingAmountRes.get("adjustmentAmountTotal");
}

//Calculate the Adjust Tax Amount
BigDecimal adjustmentAmountTotalTax = BigDecimal.ZERO;
if(statusId.equalsIgnoreCase("ORDER_CANCELLED"))
{
	Map svcTaxCtx = FastMap.newInstance();
	svcTaxCtx.put("orderId", parameters.orderId);
	svcTaxCtx.put("orderItemSequenceIds", orderItemSequenceIds);

	recalcOrderTaxAmountRes = dispatcher.runSync("recalcOrderTaxAmount", svcTaxCtx);
	adjustmentAmountTotalTax = recalcOrderTaxAmountRes.get("adjustmentAmountTotal");
}

//Calculate the Adjust Promo Amount
BigDecimal adjustmentAmountTotalPromo = BigDecimal.ZERO;
Map svcPromoCtx = FastMap.newInstance();
svcPromoCtx.put("orderId", parameters.orderId);
svcPromoCtx.put("orderItemSequenceIds", orderItemSequenceIds);


recalcOrderPromoAmountRes = dispatcher.runSync("recalcOrderPromoAmount", svcPromoCtx);
adjustmentAmountTotalPromo = recalcOrderPromoAmountRes.get("adjustmentAmountTotal");

typeMap = [:];
returnItemTypeMap = delegator.findByAnd("ReturnItemTypeMap", [returnHeaderTypeId : "CUSTOMER_RETURN"]);
returnItemTypeMap.each { value ->
    typeMap[value.returnItemMapKey] = value.returnItemTypeId;
}
context.returnItemTypeMap = typeMap;


context.adjustmentAmountTotalShipping = adjustmentAmountTotalShipping;
context.adjustmentAmountTotalTax = adjustmentAmountTotalTax;
context.adjustmentAmountTotalPromo = adjustmentAmountTotalPromo; 

context.cancelOrderItemSubtotal = cancelOrderItemSubtotal;
context.returnOrderItemSubtotal = returnOrderItemSubtotal;

context.originalOrderItemSubtotal = originalOrderItemSubtotal;
context.originalOrderShippingAmount = originalOrderShippingAmount;
context.originalOrderTaxAmount = originalOrderTaxAmount;
context.originalOrderPromoAmount = originalOrderPromoAmount;
context.originalOrderOtherAmount = originalOrderOtherAmount;

context.priorItemAdjustmentTotal = priorItemAdjustmentTotal;
context.priorShippingAdjustmentTotal = priorShippingAdjustmentTotal;
context.priorPromoAdjustmentTotal = priorPromoAdjustmentTotal;
context.priorTaxAdjustmentTotal = priorTaxAdjustmentTotal;
context.priorMiscAdjustmentTotal = priorMiscAdjustmentTotal;
context.totalChargeRefunded=totalChargeRefunded;

context.orderItems = orderItems;
context.currencyUomId = currencyUomId;
context.statusId = statusId;
context.returnItems = returnItems;
context.returnItemSeqIdQuantityMap = returnItemSeqIdQuantityMap;