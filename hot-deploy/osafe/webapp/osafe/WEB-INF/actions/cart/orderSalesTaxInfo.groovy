import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.store.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.order.order.*;
import org.ofbiz.product.catalog.*;
import com.osafe.util.Util;
import java.math.BigDecimal;
import org.ofbiz.base.util.UtilNumber;

orderId = parameters.orderId;
if (UtilValidate.isNotEmpty(orderId))
{
	orderHeader = delegator.findOne("OrderHeader", [orderId : orderId], true);
	if (UtilValidate.isNotEmpty(orderHeader))
	{
		orderReadHelper = new OrderReadHelper(orderHeader);
		if (UtilValidate.isNotEmpty(orderReadHelper))
		{
			orderItems = orderReadHelper.getOrderItems();
			orderAdjustments = orderReadHelper.getAdjustments();
			orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments();
			orderSubTotal = orderReadHelper.getOrderItemsSubTotal();
			orderTaxTotal = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, true, false);
			orderTaxTotal = orderTaxTotal.add(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, true, false));
			orderItemShipGroups = orderReadHelper.getOrderItemShipGroups();
			shipGroupsSize = orderItemShipGroups.size();
			appliedTaxList = FastList.newInstance();
			BigDecimal totalTaxPercent = BigDecimal.ZERO;
			shipGroupSalesTaxSame = true;
			
			orderAdjustments = orderReadHelper.getAdjustments();
			if (UtilValidate.isNotEmpty(orderAdjustments))
			{
				//This section will verify if each ship group has the same percentages applied for taxes, if so we can display them for each percentage
				List orderAdjustmentsSalesTax = FastList.newInstance();
				if(UtilValidate.isNotEmpty(orderAdjustments))
				{
					orderAdjustmentsSalesTax = EntityUtil.filterByAnd(orderAdjustments, UtilMisc.toMap("orderAdjustmentTypeId", "SALES_TAX"));
				}
				taxAuthorityRateSeqIdsItemStr = "";
				 //CHECK if taxes on shipping are the same for each ship group
				 if(UtilValidate.isNotEmpty(orderItemShipGroups))
				 {
					 if(shipGroupsSize > 1)
					 {
						 for(GenericValue orderItemShipGroup : orderItemShipGroups)
						 {
							 if(shipGroupSalesTaxSame)
							 {
								 if(UtilValidate.isNotEmpty(orderAdjustmentsSalesTax))
								 {
									 shipGrpSeqId = orderItemShipGroup.shipGroupSeqId;
									 shipGrpOrderAdjustmentsSalesTaxList = EntityUtil.filterByAnd(orderAdjustmentsSalesTax, UtilMisc.toMap("shipGroupSeqId", shipGrpSeqId));
									 taxAuthorityRateSeqIdsItemCompareStr = "";
									 for (GenericValue shipGrpOrderAdjustmentsSalesTax : shipGrpOrderAdjustmentsSalesTaxList)
									 {
										 if(shipGrpSeqId.equals("00001"))
										 {
											 //build string of taxAuthorityRateSeqIdsItemStr for first ship group
											 if(!(taxAuthorityRateSeqIdsItemStr.contains(shipGrpOrderAdjustmentsSalesTax.taxAuthorityRateSeqId)))
											 {
												 taxAuthorityRateSeqIdsItemStr = taxAuthorityRateSeqIdsItemStr + "-" + shipGrpOrderAdjustmentsSalesTax.taxAuthorityRateSeqId;
											 }
										 }
										 else
										 {
											 //build string of taxAuthorityRateSeqIds for next ship group
											 if(!(taxAuthorityRateSeqIdsItemCompareStr.contains(shipGrpOrderAdjustmentsSalesTax.taxAuthorityRateSeqId)))
											 {
												 taxAuthorityRateSeqIdsItemCompareStr = taxAuthorityRateSeqIdsItemCompareStr + "-" + shipGrpOrderAdjustmentsSalesTax.taxAuthorityRateSeqId;
											 }
										 }
									 }
									 
									 if(!(shipGrpSeqId.equals("00001")))
									 {
										 if(!(taxAuthorityRateSeqIdsItemStr.equals(taxAuthorityRateSeqIdsItemCompareStr)))
										 {
											 shipGroupSalesTaxSame = false;
										 }
									 }
								 }
							 }
						 }
					 }
				 }
				
				if(UtilValidate.isNotEmpty(orderAdjustmentsSalesTax) && orderAdjustmentsSalesTax.size() > 0)
				{
					for (GenericValue orderTaxAdjustment : orderAdjustmentsSalesTax)
					{
						amount = 0;
						taxAuthorityRateSeqId = orderTaxAdjustment.taxAuthorityRateSeqId;
						if(UtilValidate.isNotEmpty(taxAuthorityRateSeqId))
						{
							//check if this taxAuthorityRateSeqId is already in the list
							alreadyInList = "N";
							for(Map taxInfoMap : appliedTaxList)
							{
								taxAuthorityRateSeqIdInMap = taxInfoMap.get("taxAuthorityRateSeqId");
								if(UtilValidate.isNotEmpty(taxAuthorityRateSeqIdInMap) && taxAuthorityRateSeqIdInMap.equals(taxAuthorityRateSeqId))
								{
									amount = taxInfoMap.get("amount") + orderTaxAdjustment.amount;
									taxInfoMap.put("amount", amount);
									alreadyInList = "Y";
									break;
								}
							}
							if(("N").equals(alreadyInList))
							{
								taxInfo = FastMap.newInstance();
								taxInfo.put("taxAuthorityRateSeqId", taxAuthorityRateSeqId);
								taxInfo.put("amount", orderTaxAdjustment.amount);
								taxAdjSourceBD = new BigDecimal(orderTaxAdjustment.sourcePercentage);
								taxAdjSourceStr = taxAdjSourceBD.setScale(2, UtilNumber.getBigDecimalRoundingMode("order.rounding")).toString();
								taxInfo.put("sourcePercentage", taxAdjSourceStr);
								taxInfo.put("description", orderTaxAdjustment.comments);
								appliedTaxList.add(taxInfo);
								totalTaxPercent = totalTaxPercent.add(taxAdjSourceBD);
							}
						}
					}
				}
			}
			context.appliedTaxList = appliedTaxList;
			context.totalTaxPercent = totalTaxPercent.setScale(2, UtilNumber.getBigDecimalRoundingMode("order.rounding")).toString();
			context.shipGroupSalesTaxSame = shipGroupSalesTaxSame;
			context.orderTaxTotal = orderTaxTotal;
			context.checkoutSuppressTaxIfZero = Util.isProductStoreParmTrue(request,"CHECKOUT_SUPPRESS_TAX_IF_ZERO");
			context.checkoutShowSalesTaxMulti = Util.isProductStoreParmTrue(request,"CHECKOUT_SHOW_SALES_TAX_MULTI");
		}
	}
}

