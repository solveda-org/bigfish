package checkout;


import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.order.shoppingcart.shipping.ShippingEstimateWrapper;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo.CartShipItemInfo;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import javolution.util.FastList;
import com.osafe.util.OsafeAdminUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.math.BigDecimal;
import org.ofbiz.order.shoppingcart.shipping.ShippingEvents;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilNumber;

shoppingCart = ShoppingCartEvents.getCartObject(request);

if(UtilValidate.isNotEmpty(shoppingCart))
{
	appliedTaxList = FastList.newInstance();
	int iShipInfoSize = shoppingCart.getShipInfoSize();
	List cartShipTaxAdjustments = FastList.newInstance();
	BigDecimal totalTaxPercent = BigDecimal.ZERO;
	
	
	shipGroupSalesTaxSame = true;
	taxAuthorityRateSeqIdsStr = "";
	taxAuthorityRateSeqIdsItemStr = "";
	
	for (int i=0; i < iShipInfoSize;i++)
	{
		CartShipInfo cartShipInfo = shoppingCart.getShipInfo(i);
		if(UtilValidate.isNotEmpty(cartShipInfo))
		{
			if(UtilValidate.isNotEmpty(cartShipInfo.shipTaxAdj))
			{
				cartShipTaxAdjustments.addAll(cartShipInfo.shipTaxAdj);
				//CHECK if taxes on shipping are the same for each ship group
				if(shipGroupSalesTaxSame)
				{
					if(iShipInfoSize > 1)
					{
						taxAuthorityRateSeqIdsCompareStr ="";
						for (GenericValue shippingTaxAdjust : cartShipInfo.shipTaxAdj)
						{
							if(i == 0)
							{
								//build string of taxAuthorityRateSeqIds for first ship group
								if(!(taxAuthorityRateSeqIdsStr.contains(shippingTaxAdjust.taxAuthorityRateSeqId)))
								{
									taxAuthorityRateSeqIdsStr = taxAuthorityRateSeqIdsStr + "-" + shippingTaxAdjust.taxAuthorityRateSeqId;
								}
							}
							else
							{
								//build string of taxAuthorityRateSeqIds for next ship group
								if(!(taxAuthorityRateSeqIdsCompareStr.contains(shippingTaxAdjust.taxAuthorityRateSeqId)))
								{
									taxAuthorityRateSeqIdsCompareStr = taxAuthorityRateSeqIdsCompareStr + "-" + shippingTaxAdjust.taxAuthorityRateSeqId;
								}
							}
						}
						if(i != 0)
						{
							//check if taxAuthorityRateSeqIdsStr and taxAuthorityRateSeqIdsCompareStr are equal
							if(!(taxAuthorityRateSeqIdsStr.equals(taxAuthorityRateSeqIdsCompareStr)))
							{
								shipGroupSalesTaxSame = false;
							}
						}
					}
				}
				//end CHECK
			}
	
			if(UtilValidate.isNotEmpty(cartShipInfo.shipItemInfo) && UtilValidate.isNotEmpty(cartShipInfo.shipItemInfo.values()))
			{
				taxAuthorityRateSeqIdsItemCompareStr ="";
				for (CartShipInfo.CartShipItemInfo info : cartShipInfo.shipItemInfo.values())
				{
					List infoItemTaxAdj = info.itemTaxAdj;
					for (GenericValue gvInfo : infoItemTaxAdj)
					{
						cartShipTaxAdjustments.add(gvInfo);
						//CHECK if taxes on shipping are the same for each ship group
						if(shipGroupSalesTaxSame)
						{
							if(iShipInfoSize > 1)
							{
								if(i == 0)
								{
									//build string of taxAuthorityRateSeqIdsItemStr for first ship group
									if(!(taxAuthorityRateSeqIdsItemStr.contains(gvInfo.taxAuthorityRateSeqId)))
									{
										taxAuthorityRateSeqIdsItemStr = taxAuthorityRateSeqIdsItemStr + "-" + gvInfo.taxAuthorityRateSeqId;
									}
								}
								else
								{
									//build string of taxAuthorityRateSeqIds for next ship group
									if(!(taxAuthorityRateSeqIdsItemCompareStr.contains(gvInfo.taxAuthorityRateSeqId)))
									{
										taxAuthorityRateSeqIdsItemCompareStr = taxAuthorityRateSeqIdsItemCompareStr + "-" + gvInfo.taxAuthorityRateSeqId;
									}
								}
							}
						}
						//end CHECK
					}
				}
				//check if taxAuthorityRateSeqIdsStr and taxAuthorityRateSeqIdsCompareStr are equal
				if(shipGroupSalesTaxSame)
				{
					if(iShipInfoSize > 1 && i != 0)
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
	
	for (GenericValue cartTaxAdjustment : cartShipTaxAdjustments)
	{
		amount = 0;
		taxAuthorityRateSeqId = cartTaxAdjustment.taxAuthorityRateSeqId;
		if(UtilValidate.isNotEmpty(taxAuthorityRateSeqId))
		{
			//check if this taxAuthorityRateSeqId is already in the list
			alreadyInList = "N";
			for(Map taxInfoMap : appliedTaxList)
			{
				taxAuthorityRateSeqIdInMap = taxInfoMap.get("taxAuthorityRateSeqId");
				if(UtilValidate.isNotEmpty(taxAuthorityRateSeqIdInMap) && taxAuthorityRateSeqIdInMap.equals(taxAuthorityRateSeqId))
				{
					amount = taxInfoMap.get("amount") + cartTaxAdjustment.amount;
					taxInfoMap.put("amount", amount);
					alreadyInList = "Y";
					break;
				}
			}
			if(("N").equals(alreadyInList))
			{
				taxInfo = FastMap.newInstance();
				taxInfo.put("taxAuthorityRateSeqId", taxAuthorityRateSeqId);
				taxInfo.put("amount", cartTaxAdjustment.amount);
				taxAdjSourceBD = new BigDecimal(cartTaxAdjustment.sourcePercentage);
				taxAdjSourceStr = taxAdjSourceBD.setScale(2, UtilNumber.getBigDecimalRoundingMode("order.rounding")).toString();
				taxInfo.put("sourcePercentage", taxAdjSourceStr);
				taxInfo.put("description", cartTaxAdjustment.comments);
				appliedTaxList.add(taxInfo);
				totalTaxPercent = totalTaxPercent.add(taxAdjSourceBD);
			}
		}
	}
	context.appliedTaxList = appliedTaxList;
	context.totalTaxPercent = totalTaxPercent.setScale(2, UtilNumber.getBigDecimalRoundingMode("order.rounding")).toString();
	context.shipGroupSalesTaxSame = shipGroupSalesTaxSame;
}