package order;

import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.order.order.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.accounting.payment.*;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import com.osafe.util.OsafeAdminUtil;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilNumber;

orderId = parameters.orderId;
userLogin = session.getAttribute("userLogin");

session = context.session;
svcCtx = session.getAttribute("orderPDFMap");
if (UtilValidate.isEmpty(svcCtx)) 
{
    Map<String, Object> svcCtx = FastMap.newInstance();
}

if (UtilValidate.isNotEmpty(orderId)) 
{
    svcCtx.put("orderId", orderId);
}

if (UtilValidate.isNotEmpty(svcCtx)) 
{
    svcCtx.put("viewSize",  Integer.valueOf("1000"));
    svcCtx.put("showAll", "Y");
    Map<String, Object> svcRes = dispatcher.runSync("searchOrders", svcCtx);
    List<GenericValue> orderPDFList = UtilGenerics.checkList(svcRes.get("completeOrderList"), GenericValue.class);
    context.ordersList = orderPDFList;
    if (UtilValidate.isNotEmpty(orderPDFList)) 
    {
		firstOrder = orderPDFList.getFirst();
		firstOrderId = firstOrder.orderId;
		orderPDFName = firstOrderId;
        if (orderPDFList.size() > 1) 
        {
			lastOrder = orderPDFList.getLast();
			lastOrderId = lastOrder.orderId;
			orderPDFName= orderPDFName + " " + uiLabelMap.ToLabel + " "+ lastOrderId;
        }
		
		if (UtilValidate.isNotEmpty(orderPDFName))
		{
			response.setHeader("Content-Disposition","attachment; filename=\"" + orderPDFName + ".pdf" + "\";");
		}
        Map<String, Object> upOrderHeaderCtx = FastMap.newInstance();
        upOrderHeaderCtx.put("userLogin",userLogin);
        upOrderHeaderCtx.put("isDownloaded","Y");
        upOrderHeaderCtx.put("datetimeDownloaded",UtilDateTime.nowTimestamp());
        


        storePickupMap = [:];
		taxDisplayMap = [:];
        for(GenericValue orderHeader : orderPDFList) 
        {
        
            orderAttrIsDownloaded = delegator.findOne("OrderAttribute", ["orderId" : orderHeader.orderId, "attrName" : "IS_DOWNLOADED"], false);
        
            Map<String, Object> isDownloadedOrderAttrCtx = FastMap.newInstance();
            isDownloadedOrderAttrCtx.put("orderId", orderHeader.orderId);
            isDownloadedOrderAttrCtx.put("userLogin",userLogin);
            isDownloadedOrderAttrCtx.put("attrName","IS_DOWNLOADED");
            isDownloadedOrderAttrCtx.put("attrValue","Y");
            Map<String, Object> isDownloadOrderAttrMap = null;
            if (UtilValidate.isNotEmpty(orderAttrIsDownloaded)) 
            {
                isDownloadOrderAttrMap = dispatcher.runSync("updateOrderAttribute", isDownloadedOrderAttrCtx);
            } else 
            {
               isDownloadOrderAttrMap = dispatcher.runSync("createOrderAttribute", isDownloadedOrderAttrCtx);
            }
        
            orderAttrDateTimeDownloaded = delegator.findOne("OrderAttribute", ["orderId" : orderHeader.orderId, "attrName" : "DATETIME_DOWNLOADED"], false);
            Map<String, Object> dateTimeDownloadedOrderAttrCtx = FastMap.newInstance();
            dateTimeDownloadedOrderAttrCtx.put("orderId", orderHeader.orderId);
            dateTimeDownloadedOrderAttrCtx.put("userLogin",userLogin);
            dateTimeDownloadedOrderAttrCtx.put("attrName","DATETIME_DOWNLOADED");
            dateTimeDownloadedOrderAttrCtx.put("attrValue",UtilDateTime.nowTimestamp().toString());
            Map<String, Object> dateTimeDownloadedOrderAttrMap = null;
            if (UtilValidate.isNotEmpty(orderAttrDateTimeDownloaded)) 
            {
                dateTimeDownloadedOrderAttrMap = dispatcher.runSync("updateOrderAttribute", dateTimeDownloadedOrderAttrCtx);
            } else 
            {
                dateTimeDownloadedOrderAttrMap = dispatcher.runSync("createOrderAttribute", dateTimeDownloadedOrderAttrCtx);
            }
        
            //upOrderHeaderCtx.put("orderId",orderHeader.orderId);
            //Map<String, Object> resultMap = dispatcher.runSync("updateOrderHeader", upOrderHeaderCtx);

            storeId = "";
            orderPickupDetailMap = [:];
            orderDeliveryOptionAttr = delegator.findOne("OrderAttribute", [orderId : orderHeader.orderId, attrName : "DELIVERY_OPTION"], false);
            if (UtilValidate.isNotEmpty(orderDeliveryOptionAttr) && orderDeliveryOptionAttr.attrValue == "STORE_PICKUP") 
            {
                orderPickupDetailMap.isStorePickup = "Y";
                orderStoreLocationAttr = delegator.findOne("OrderAttribute", [orderId : orderHeader.orderId, attrName : "STORE_LOCATION"], false);
                if (UtilValidate.isNotEmpty(orderStoreLocationAttr)) 
                {
                    storeId = orderStoreLocationAttr.attrValue;
                }
            }

            if (UtilValidate.isNotEmpty(storeId)) 
            {
                orderPickupDetailMap.storeId = storeId;
                store = delegator.findOne("Party", [partyId : storeId], false);
                orderPickupDetailMap.store = store;
                storeInfo = delegator.findOne("PartyGroup", [partyId : storeId], false);
                if (UtilValidate.isNotEmpty(storeInfo)) 
                {
                    orderPickupDetailMap.storeInfo = storeInfo;
                }
                
                partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, storeId, false);
                if (UtilValidate.isNotEmpty(partyContactMechValueMaps)) 
                {
                    partyContactMechValueMaps.each { partyContactMechValueMap ->
                        contactMechPurposes = partyContactMechValueMap.partyContactMechPurposes;
                        contactMechPurposes.each { contactMechPurpose ->
                            if (contactMechPurpose.contactMechPurposeTypeId.equals("GENERAL_LOCATION")) 
                            {
                                orderPickupDetailMap.storeContactMechValueMap = partyContactMechValueMap;
                            }
                        }
                    }
                }
            }
            storePickupMap.put(orderHeader.orderId, orderPickupDetailMap);
			
			orderReadHelper = new OrderReadHelper(orderHeader);
			orderAdjustments = orderReadHelper.getAdjustments();
			orderItemShipGroups = orderReadHelper.getOrderItemShipGroups();
			shipGroupsSize = orderItemShipGroups.size();
			
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
			//END CHECK if taxes on shipping are the same for each ship group
			
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
			
			taxDisplayDetailMap = [:];
			taxDisplayDetailMap.appliedTaxList = appliedTaxList;
			taxDisplayDetailMap.totalTaxPercent = totalTaxPercent.setScale(2, UtilNumber.getBigDecimalRoundingMode("order.rounding")).toString();
			taxDisplayDetailMap.shipGroupSalesTaxSame = shipGroupSalesTaxSame;
			taxDisplayMap.put(orderHeader.orderId, taxDisplayDetailMap);
        }
        context.storePickupMap = storePickupMap;
		context.taxDisplayMap = taxDisplayMap;
    }
}

context.checkoutSuppressTaxIfZero = OsafeAdminUtil.isProductStoreParmTrue(request,"CHECKOUT_SUPPRESS_TAX_IF_ZERO");
context.checkoutShowSalesTaxMulti = OsafeAdminUtil.isProductStoreParmTrue(request,"CHECKOUT_SHOW_SALES_TAX_MULTI");

