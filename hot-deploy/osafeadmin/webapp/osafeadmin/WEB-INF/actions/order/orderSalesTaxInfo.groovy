package order;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.math.BigDecimal;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.Debug;

orderId = StringUtils.trimToEmpty(parameters.orderId);

if (UtilValidate.isNotEmpty(orderId)) 
{
	orderHeader = delegator.findByPrimaryKey("OrderHeader", [orderId : orderId]);
	if (UtilValidate.isNotEmpty(orderHeader)) 
	{
	    orderReadHelper = new OrderReadHelper(orderHeader);
		orderAdjustments = orderReadHelper.getAdjustments();
		
	    // ship groups
	    shipGroups = orderHeader.getRelatedOrderBy("OrderItemShipGroup", ["-shipGroupSeqId"]);
	    context.shipGroups = shipGroups;
	    shipGroupsSize = shipGroups.size();
	    
		//This section will verify if each ship group has the same percentages applied for taxes, if so we can display them for each percentage
	    appliedTaxList = FastList.newInstance();
		List orderAdjustmentsSalesTax = FastList.newInstance();
		if(UtilValidate.isNotEmpty(orderAdjustments))
		{
			orderAdjustmentsSalesTax = EntityUtil.filterByAnd(orderAdjustments, UtilMisc.toMap("orderAdjustmentTypeId", "SALES_TAX"));
		}
		BigDecimal totalTaxPercent = BigDecimal.ZERO;
		
		shipGroupSalesTaxSame = true;
		taxAuthorityRateSeqIdsItemStr = "";
		//CHECK if taxes on shipping are the same for each ship group
		orderItemShipGroups = orderReadHelper.getOrderItemShipGroups();
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
		context.appliedTaxList = appliedTaxList;
		context.totalTaxPercent = totalTaxPercent.setScale(2, UtilNumber.getBigDecimalRoundingMode("order.rounding")).toString();
		context.shipGroupSalesTaxSame = shipGroupSalesTaxSame;
	}
}
