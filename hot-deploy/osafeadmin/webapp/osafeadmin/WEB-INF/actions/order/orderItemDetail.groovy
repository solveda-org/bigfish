package order;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityFunction;
import java.math.BigDecimal;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilNumber;

userLogin = session.getAttribute("userLogin");
orderId = StringUtils.trimToEmpty(parameters.orderId);
context.orderId = orderId;

orderHeader = null;
orderItems = null;
orderNotes = null;
partyId = null;
shipGroup = null;
messageMap=[:];
orderHeaderAdjustments = null;
notesCount = 0;

appliedPromoList = FastList.newInstance();
appliedLoyaltyPointsList = FastList.newInstance();

orderSubTotal = 0;
if (UtilValidate.isNotEmpty(orderId))
{
	orderHeader = delegator.findByPrimaryKey("OrderHeader", [orderId : orderId]);
	
	if (UtilValidate.isNotEmpty(orderHeader))
	{
		orderProductStore = orderHeader.getRelatedOne("ProductStore");
		if (UtilValidate.isNotEmpty(orderProductStore.storeName))
		{
			productStoreName = orderProductStore.storeName;
		}
		else
		{
			productStoreName = orderHeader.productStoreId;
		}
		context.productStoreName = productStoreName;
		
		
		messageMap.put("orderId", orderId);
		
		productStore = orderHeader.getRelatedOne("ProductStore");
		orderReadHelper = new OrderReadHelper(orderHeader);
		orderItems = orderReadHelper.getOrderItems();
		canceledPromoOrderItem = [:];
		orderItems.each { orderItem ->
			if("Y".equals(orderItem.get("isPromo")) && "ITEM_CANCELLED".equals(orderItem.get("statusId")))
			{
				canceledPromoOrderItem = orderItem;
			}
			orderItems.remove(canceledPromoOrderItem);
		}
		orderSubTotal = orderReadHelper.getOrderItemsSubTotal();
		orderAdjustments = orderReadHelper.getAdjustments();
		orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments();
		headerAdjustmentsToShow = orderReadHelper.filterOrderAdjustments(orderHeaderAdjustments, true, false, false, false, false);
		otherAdjustmentsList = FastList.newInstance();

		if (UtilValidate.isNotEmpty(headerAdjustmentsToShow) && headerAdjustmentsToShow.size() > 0)
		{
			for (GenericValue orderHeaderAdjustment : headerAdjustmentsToShow)
			{
				if (!"LOYALTY_POINTS".equals(orderHeaderAdjustment.orderAdjustmentTypeId) && UtilValidate.isEmpty(orderHeaderAdjustment.productPromoId))
				{
					otherAdjustmentsList.add(orderHeaderAdjustment);
				}
			}
		}
		otherAdjustmentsAmount = OrderReadHelper.calcOrderAdjustments(otherAdjustmentsList, orderSubTotal, true, false, false);
		otherAdjustmentsList = FastList.newInstance();
		otherAdjustmentsList = EntityUtil.filterByAnd(orderAdjustments, [EntityCondition.makeCondition("productPromoId", EntityOperator.EQUALS, null)]);
		otherAdjustmentsList = EntityUtil.filterByAnd(otherAdjustmentsList, [EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.NOT_EQUAL, "LOYALTY_POINTS")]);
		otherAdjustmentsAmount = OrderReadHelper.calcOrderAdjustments(otherAdjustmentsList, orderSubTotal, true, false, false);

		
		shippingAmount = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
		shippingAmount = shippingAmount.add(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true));
		orderItemShipGroups = orderReadHelper.getOrderItemShipGroups();
		shipGroupsSize = orderItemShipGroups.size();
		//headerAdjustmentsToShow = orderReadHelper.getOrderHeaderAdjustmentsToShow();
		taxAmount = OrderReadHelper.getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal;
		
		grandTotal = orderReadHelper.getOrderGrandTotal();
		currencyUomId = orderReadHelper.getCurrency();
		notes = orderHeader.getRelatedOrderBy("OrderHeaderNoteView", ["-noteDateTime"]);
		notesCount = notes.size();
		
		//get Adjustment Info
		if(UtilValidate.isNotEmpty(orderHeaderAdjustments) && orderHeaderAdjustments.size() > 0)
		{
			adjustments = orderHeaderAdjustments;
			//iterate through adjustments
			for (GenericValue cartAdjustment : adjustments)
			{
				adjustmentType = cartAdjustment.getRelatedOneCache("OrderAdjustmentType");
				adjustmentTypeDesc = adjustmentType.get("description",locale);
				//if loyalty points adjustment then store info
				if(adjustmentType.orderAdjustmentTypeId.equals("LOYALTY_POINTS"))
				{
					loyaltyPointsInfo = FastMap.newInstance();
					loyaltyPointsInfo.put("cartAdjustment", cartAdjustment);
					loyaltyPointsInfo.put("adjustmentTypeDesc", adjustmentTypeDesc);
					appliedLoyaltyPointsList.add(loyaltyPointsInfo);
				}
				//if promo adjustment then store info
				promoInfo = FastMap.newInstance();
				promoInfo.put("cartAdjustment", cartAdjustment);
				promoCodeText = "";
				productPromo = cartAdjustment.getRelatedOneCache("ProductPromo");
				if(UtilValidate.isNotEmpty(productPromo))
				{
					promoInfo.put("adjustmentTypeDesc", adjustmentTypeDesc);
					promoText = productPromo.promoText;
					promoInfo.put("promoText", promoText);
					productPromoCode = productPromo.getRelatedCache("ProductPromoCode");
					if(UtilValidate.isNotEmpty(productPromoCode))
					{
						promoCodesEntered = orderReadHelper.getProductPromoCodesEntered();
						if(UtilValidate.isNotEmpty(promoCodesEntered))
						{
							for (String promoCodeEntered : promoCodesEntered)
							{
								if(UtilValidate.isNotEmpty(promoCodeEntered))
								{
									for (GenericValue promoCode : productPromoCode)
									{
										promoCodeEnteredId = promoCodeEntered;
										promoCodeId = promoCode.productPromoCodeId;
										if(UtilValidate.isNotEmpty(promoCodeEnteredId))
										{
											if(promoCodeId == promoCodeEnteredId)
											{
												promoCodeText = promoCode.productPromoCodeId;
												promoInfo.put("promoCodeText", promoCodeText);
											}
										}
									}
								}
							}
							
						}
					}
					appliedPromoList.add(promoInfo);
				}
			}
		}
	}
}

context.orderId=orderId;
context.pageTitle = UtilProperties.getMessage("OSafeAdminUiLabels","OrderManagementOrderDetailTitle",messageMap, locale );
context.generalInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","OrderDetailInfoHeading",messageMap, locale );
context.notesCount = notesCount;
context.shippingAmount = shippingAmount;
context.taxAmount = taxAmount;
context.otherAdjustmentsAmount = otherAdjustmentsAmount;
context.grandTotal = grandTotal;
context.currencyUomId = currencyUomId;
context.headerAdjustmentsToShow = headerAdjustmentsToShow;
context.appliedPromoList = appliedPromoList;
context.appliedLoyaltyPointsList = appliedLoyaltyPointsList;
context.orderSubTotal = orderSubTotal;

// note these are overridden in the OrderViewWebSecure.groovy script if run
context.hasPermission = true;
context.canViewInternalDetails = true;

context.orderHeader = orderHeader;
context.orderReadHelper = orderReadHelper;
context.orderItems = orderItems;
context.orderNotes = notes;

	
//FOR EACH INDIVIDUAL ORDER ITEM SCREEN	
itemCancelledAmmount = 0;
orderItem = request.getAttribute("orderItem");
if(UtilValidate.isNotEmpty(orderItem))
{
	context.orderItem = orderItem;
	orderItemSeqId = orderItem.orderItemSeqId;
	messageMap=[:];
	messageMap.put("orderItemSeqId", orderItemSeqId);

	context.orderId=orderId;
	context.orderItemBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","OrderItemBoxHeading",messageMap, locale )
	
	statusItem = orderItem.getRelatedOne("StatusItem");
	context.statusItem = statusItem;
	
	//get Returned Quantity
	if(UtilValidate.isNotEmpty(orderHeader) && UtilValidate.isNotEmpty(orderReadHelper))
	{
		if("SALES_ORDER".equals(orderHeader.orderTypeId))
		{
			pickedQty = orderReadHelper.getItemPickedQuantityBd(orderItem);
			context.pickedQty = pickedQty;
		}
		
		// QUANTITY: get the returned quantity by order item map
		context.returnQuantityMap = orderReadHelper.getOrderItemReturnedQuantities();
	}
	
	//get cancelled quantity
	if("ITEM_CANCELLED".equals(orderItem.get("statusId")))
	{
		itemCancelledAmmount = orderItem.quantity;
	}
	
	//get shipGroup	
	shipDate = "";
	carrier = "";
	orderItemShipGroupAddress = null;
	orderItemShipDate = null;
	orderItemCarrier = null;
	orderItemTrackingNo = null;
	orderItemShipGroupAssocs = orderItem.getRelated("OrderItemShipGroupAssoc");
	if(UtilValidate.isNotEmpty(orderItemShipGroupAssocs))
	{
		for (GenericValue shipGroupAssoc: orderItemShipGroupAssocs)
		{
			if(UtilValidate.isNotEmpty(shipGroupAssoc.getRelatedOne("OrderItemShipGroup")))
			{
				shipGroup = shipGroupAssoc.getRelatedOne("OrderItemShipGroup");
				context.shipGroupAssoc = shipGroupAssoc;
			}
			if(UtilValidate.isNotEmpty(shipGroup.getRelatedOne("PostalAddress")))
			{
				orderItemShipGroupAddress = shipGroup.getRelatedOne("PostalAddress");
				orderItemShipDate = shipGroup.estimatedShipDate;
				orderItemCarrier = shipGroup.carrierPartyId + " " + shipGroup.shipmentMethodTypeId;
				orderItemTrackingNo = shipGroup.trackingNumber;
			}
			
		}
	}
	// Fetching the carrier tracking URL
	trackingURL = "";
	trackingURLPartyContents = null;
	if(UtilValidate.isNotEmpty(shipGroup))
	{
		trackingURLPartyContents = delegator.findByAnd("PartyContent", UtilMisc.toMap("partyId",shipGroup.carrierPartyId,"partyContentTypeId", "TRACKING_URL"));
	}
	if(UtilValidate.isNotEmpty(trackingURLPartyContents))
    {
        trackingURLPartyContent = EntityUtil.getFirst(trackingURLPartyContents);
        if(UtilValidate.isNotEmpty(trackingURLPartyContent))
        {
            content = trackingURLPartyContent.getRelatedOne("Content");
            if(UtilValidate.isNotEmpty(content))
            {
                dataResource = content.getRelatedOne("DataResource");
                if(UtilValidate.isNotEmpty(dataResource))
                {
                    electronicText = dataResource.getRelatedOne("ElectronicText");
                    trackingURL = electronicText.textData;
                    if(UtilValidate.isNotEmpty(trackingURL))
                    {
                        trackingURL = FlexibleStringExpander.expandString(trackingURL,  UtilMisc.toMap("TRACKING_NUMBER":orderItemTrackingNo))
                    }
                }
            }
                
        }
        
    }
    
    context.trackingURL = trackingURL;
	if(UtilValidate.isNotEmpty(shipGroup))
	{
		context.carrierPartyId = shipGroup.carrierPartyId;
	}
	context.shipGroup = shipGroup;
	context.orderItemShipGroupAssocs = orderItemShipGroupAssocs;
	context.orderItemShipGroupAddress = orderItemShipGroupAddress;
	context.orderItemShipDate = orderItemShipDate;
	context.orderItemCarrier = orderItemCarrier;
	context.orderItemTrackingNo = orderItemTrackingNo;
	
	context.itemCancelledAmmount =itemCancelledAmmount;
	
	//get order adjustments
	context.orderAdjustments = orderReadHelper.getAdjustments();

	//get planned shipment info 
	orderShipments = orderItem.getRelated("OrderShipment");
	GenericValue orderShipment = null;
	if(UtilValidate.isNotEmpty(orderShipments))
	{
		for (GenericValue orderShip: orderShipments)
		{
			if(UtilValidate.isNotEmpty(orderShip.shipmentId))
			{
				orderShipment = orderShip;
			}
		}
	}
	
	context.orderShipment = orderShipment;
	
	//item issuances
	itemIssuances = orderItem.getRelated("ItemIssuance");
	GenericValue itemIssuance = null;
	if(UtilValidate.isNotEmpty(itemIssuances))
	{
		for (GenericValue itemIssu: itemIssuances)
		{
			if(UtilValidate.isNotEmpty(itemIssu.shipmentId))
			{
				itemIssuance = itemIssu;
			}
		}
	}
	context.itemIssuance = itemIssuance;

	//get order item attributes 
	orderItemAttributes = orderItem.getRelated("OrderItemAttribute");
	context.orderItemAttributes = orderItemAttributes;
}	





