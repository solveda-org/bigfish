package common;

import org.ofbiz.base.util.UtilProperties;
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
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilNumber;

//String showThankYouStatus = parameters.showThankYouStatus;
String showThankYouStatus = context.showThankYouStatus;
if (UtilValidate.isEmpty(showThankYouStatus))
{
    context.showThankYouStatus ="N"
}
if("Y".equals (showThankYouStatus))
{
	context.OrderCompleteInfoMapped = UtilProperties.getMessage("OSafeUiLabels","OrderCompleteInfo", locale )
}

party = context.party;
partyId = context.partyId;
if (UtilValidate.isEmpty(partyId))
{
	if (UtilValidate.isNotEmpty(userLogin))
	{
		party = userLogin.getRelatedOneCache("Party");
		partyId = party.partyId;
	}
}
else
{
	party = delegator.findOne("Party", [partyId : partyId], true);
}

orderId = parameters.orderId;
if (UtilValidate.isEmpty(orderId))
{
	orderId = session.getAttribute("orderId");

}
orderHeader = null;
orderHeaderAdjustments = null;
orderSubTotal = 0;
currencyUomId = "";
roleTypeId = "PLACING_CUSTOMER";
context.roleTypeId = roleTypeId;
chosenShippingMethodDescription="";
shippingInstructions = "";
ordersList = FastList.newInstance();
shippingApplies = true;

if (UtilValidate.isNotEmpty(orderId)) 
{
   orderHeader = delegator.findOne("OrderHeader", [orderId : orderId], true);
   if (UtilValidate.isNotEmpty(orderHeader))
   {
	   productStore = orderHeader.getRelatedOneCache("ProductStore");
	   orderReadHelper = new OrderReadHelper(orderHeader);
	   orderItems = orderReadHelper.getOrderItems();
	   orderItemsTotalQty = orderReadHelper.getTotalOrderItemsQuantity();
	   orderAdjustments = orderReadHelper.getAdjustments();
	   orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments();
	   orderSubTotal = orderReadHelper.getOrderItemsSubTotal();
	   orderItemShipGroups = orderReadHelper.getOrderItemShipGroups();
	   shipGroupsSize = orderItemShipGroups.size();
	   headerAdjustmentsToShow = orderReadHelper.getOrderHeaderAdjustmentsToShow();
	   shippingApplies = orderReadHelper.shippingApplies();
   
	   orderShippingTotal = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
	   orderShippingTotal = orderShippingTotal.add(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true));

	   orderGrandTotal = OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);
	   orderPaymentPreferences = orderHeader.getRelatedCache("OrderPaymentPreference", UtilMisc.toList("orderPaymentPreferenceId"));
	   if (UtilValidate.isEmpty(userLogin))
	   {
		   userLogin = parameters.temporaryAnonymousUserLogin;
		   // This is another special case, when Order is placed by anonymous user from ecommerce and then Order is completed by admin(or any user) from Order Manager
		   // then userLogin is not found when Order Complete Mail is send to user.
		   if (UtilValidate.isEmpty(userLogin))
		   {
			   orderStatuses = orderHeader.getRelatedCache("OrderStatus");
			   filteredOrderStatusList = [];
			   extOfflineModeExists = false;
			   
			   // Handled the case of OFFLINE payment method.
			   filteredOrderPaymentPreferences = EntityUtil.filterByCondition(orderPaymentPreferences, EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.IN, ["EXT_OFFLINE"]));
			   if (UtilValidate.isNotEmpty(filteredOrderPaymentPreferences))
			   {
				   extOfflineModeExists = true;
			   }
			   if (extOfflineModeExists)
			   {
				   filteredOrderStatusList = EntityUtil.filterByCondition(orderStatuses, EntityCondition.makeCondition("statusId", EntityOperator.IN, ["ORDER_COMPLETED", "ORDER_APPROVED", "ORDER_CREATED"]));
			   }
			   else
			   {
				   filteredOrderStatusList = EntityUtil.filterByCondition(orderStatuses, EntityCondition.makeCondition("statusId", EntityOperator.IN, ["ORDER_COMPLETED", "ORDER_APPROVED"]));
			   }
			   if (UtilValidate.isNotEmpty(filteredOrderStatusList))
			   {
				   if (filteredOrderStatusList.size() < 2)
				   {
					   statusUserLogin = EntityUtil.getFirst(filteredOrderStatusList).statusUserLogin;
					   userLogin = delegator.findOne("UserLogin", [userLoginId : statusUserLogin], true);
				   }
				   else
				   {
					   filteredOrderStatusList.each { orderStatus ->
						   if ("ORDER_COMPLETED".equals(orderStatus.statusId))
						   {
							   statusUserLogin = orderStatus.statusUserLogin;
							   userLogin = delegator.findOne("UserLogin", [userLoginId :statusUserLogin], true);
						   }
					   }
				   }
			   }
		   }
		   context.userLogin = userLogin;
	   }

	   placingCustomerOrderRoles = delegator.findByAndCache("OrderRole", [orderId : orderId, roleTypeId : roleTypeId]);
	   placingCustomerOrderRole = EntityUtil.getFirst(placingCustomerOrderRoles);
	   placingCustomerPerson = placingCustomerOrderRole == null ? null : delegator.findByPrimaryKeyCache("Person", [partyId : placingCustomerOrderRole.partyId]);
   
	   billingAccount = orderHeader.getRelatedOneCache("BillingAccount");
   
	   cancelOrderPaymentPreferences = EntityUtil.filterByAnd(orderPaymentPreferences, [EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED")]);
	   paymentMethods = [];
	   cancelOrderPaymentPreferences.each { opp ->
		   paymentMethod = opp.getRelatedOneCache("PaymentMethod");
		   if (UtilValidate.isNotEmpty(paymentMethod))
		   {
			   paymentMethods.add(paymentMethod);
		   }
		   else
		   {
			   paymentMethodType = opp.getRelatedOneCache("PaymentMethodType");
			   if (UtilValidate.isNotEmpty(paymentMethodType))
			   {
				   context.paymentMethodType = paymentMethodType;
			   }
		   }
	   }
	   
	   webSiteId = orderHeader.webSiteId ?: CatalogWorker.getWebSiteId(request);
   
	   payToPartyId = productStore.payToPartyId;
	   paymentAddress =  PaymentWorker.getPaymentAddress(delegator, payToPartyId);
	   if (UtilValidate.isNotEmpty(paymentAddress))
		{
		  context.paymentAddress = paymentAddress;
		}
   
	   // get Shipment tracking info
	   osisCond = EntityCondition.makeCondition([orderId : orderId], EntityOperator.AND);
	   osisOrder = ["shipmentId", "shipmentRouteSegmentId", "shipmentPackageSeqId"];
	   osisFields = ["shipmentId", "shipmentRouteSegmentId", "carrierPartyId", "shipmentMethodTypeId"] as Set;
	   osisFields.add("shipmentPackageSeqId");
	   osisFields.add("trackingCode");
	   osisFields.add("boxNumber");
	   osisFindOptions = new EntityFindOptions();
	   osisFindOptions.setDistinct(true);
	   orderShipmentInfoSummaryList = delegator.findList("OrderShipmentInfoSummary", osisCond, osisFields, osisOrder, osisFindOptions, true);
   
	   customerPoNumberSet = new TreeSet();
	   orderItems.each { orderItemPo ->
		   correspondingPoId = orderItemPo.correspondingPoId;
		   if (correspondingPoId && !"(none)".equals(correspondingPoId))
		   {
			   customerPoNumberSet.add(correspondingPoId);
		   }
		 }
   
	   // check if there are returnable items
	   returned = 0.00;
	   totalItems = 0.00;
	   orderItems.each { oitem ->
		   totalItems += oitem.quantity;
		   ritems = oitem.getRelatedCache("ReturnItem");
		   ritems.each { ritem ->
			   rh = ritem.getRelatedOneCache("ReturnHeader");
			   if (!rh.statusId.equals("RETURN_CANCELLED"))
			   {
				   returned += ritem.returnQuantity;
			   }
			 }
		  }
   
	   if (totalItems > returned)
	   {
		   context.returnLink = "Y";
	   }
	   
	   offerPriceVisible = "";
	   if(UtilValidate.isNotEmpty(orderItems))
	   {
		   for (GenericValue orderItem : orderItems)
		   {
			   cartItemAdjustment = orderReadHelper.getOrderItemAdjustmentsTotal(orderItem);
			   if(cartItemAdjustment < 0)
			   {
				   offerPriceVisible= "Y";
				   break;
			   }
		   }
	   }
	   
	   //set store pickup to Y if a store location is set
	   shoppingCartStoreId = "";
	   String isStorePickUp = context.isStorePickUp;
	   orderAttrPickupStore = delegator.findOne("OrderAttribute", ["orderId" : orderId, "attrName" : "STORE_LOCATION"], true);
	   if (UtilValidate.isNotEmpty(orderAttrPickupStore))
	   {
		   storeId = orderAttrPickupStore.attrValue;
		   isStorePickUp = "Y";
		   shoppingCartStoreId = storeId;
	   }
   
	   
	   
	   //get Adjustment Info
	   appliedPromoList = FastList.newInstance();
	   appliedLoyaltyPointsList = FastList.newInstance();
	   if(UtilValidate.isNotEmpty(orderHeaderAdjustments) && orderHeaderAdjustments.size() > 0)
	   {
		   adjustments = orderHeaderAdjustments;
		   for (GenericValue cartAdjustment : adjustments)
		   {
			   promoInfo = FastMap.newInstance();
			   promoInfo.put("cartAdjustment", cartAdjustment);
			   promoCodeText = "";
			   adjustmentType = cartAdjustment.getRelatedOneCache("OrderAdjustmentType");
			   adjustmentTypeDesc = adjustmentType.get("description",locale);
			   //loyalty points
			   if(adjustmentType.orderAdjustmentTypeId.equals("LOYALTY_POINTS"))
			   {
				   loyaltyPointsInfo = FastMap.newInstance();
				   loyaltyPointsInfo.put("cartAdjustment", cartAdjustment);
				   loyaltyPointsInfo.put("adjustmentTypeDesc", adjustmentTypeDesc);
				   appliedLoyaltyPointsList.add(loyaltyPointsInfo);
			   }
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
	   
	   //get shipping method
	   selectedStoreId = "";
	   if(UtilValidate.isNotEmpty(orderItemShipGroups))
	   {
		   for(GenericValue shipGroup : orderItemShipGroups)
		   {
			   shippingInstructions = shipGroup.shippingInstructions;
			   if(UtilValidate.isNotEmpty(orderHeader))
			   {
				   orderAttrPickupStoreList = orderHeader.getRelatedByAndCache("OrderAttribute", UtilMisc.toMap("attrName", "STORE_LOCATION"));
				   if(UtilValidate.isNotEmpty(orderAttrPickupStoreList))
				   {
					   orderAttrPickupStore = EntityUtil.getFirst(orderAttrPickupStoreList);
					   selectedStoreId = orderAttrPickupStore.attrValue;
					   chosenShippingMethodDescription = uiLabelMap.StorePickupLabel;
				   }
				   if(UtilValidate.isEmpty(selectedStoreId))
				   {
					   shipmentMethodType = shipGroup.getRelatedOneCache("ShipmentMethodType");
					   carrierPartyId = shipGroup.carrierPartyId;
					   if(UtilValidate.isNotEmpty(shipmentMethodType))
					   {
						   carrier =  delegator.findByPrimaryKeyCache("PartyGroup", UtilMisc.toMap("partyId", shipGroup.carrierPartyId));
						   if(UtilValidate.isNotEmpty(carrier))
						   {
							   if(UtilValidate.isNotEmpty(carrier.groupName))
							   {
								   chosenShippingMethodDescription = carrier.groupName + " " + shipmentMethodType.description;
							   }
							   else
							   {
								   chosenShippingMethodDescription = carrier.partyId + " " + shipmentMethodType.description;
							   }
							   
						   }
					   }
				   }
			   }
		   }
	   }
	   
	   //set orderList for printPDF
	   ordersList.add(orderHeader);
	   
	   storePickupMap = [:];
	   storeInfo = null;
	   for(GenericValue orderHeader : ordersList)
	   {
		   storeId = "";
		   orderPickupDetailMap = [:];
		   deliveryOptionAttr = delegator.findOne("OrderAttribute", [orderId : orderHeader.orderId, attrName : "DELIVERY_OPTION"], true);
		   if (UtilValidate.isNotEmpty(deliveryOptionAttr))
		   {
			   deliveryOption =deliveryOptionAttr.attrValue;
		   }
		   if (UtilValidate.isNotEmpty(deliveryOption) && deliveryOption == "STORE_PICKUP")
		   {
			   orderPickupDetailMap.isStorePickup = "Y";
			   orderStoreLocationAttr = delegator.findOne("OrderAttribute", [orderId : orderHeader.orderId, attrName : "STORE_LOCATION"], true);
			   if (UtilValidate.isNotEmpty(orderStoreLocationAttr))
			   {
				   storeId = orderStoreLocationAttr.attrValue;
			   }
		   }
   
		   if (UtilValidate.isNotEmpty(storeId))
		   {
			   orderPickupDetailMap.storeId = storeId;
			   store = delegator.findOne("Party", [partyId : storeId], true);
			   orderPickupDetailMap.store = store;
			   storeInfo = delegator.findOne("PartyGroup", [partyId : storeId], true);
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
	   }
	   
	   //Retrieve CC Types for Display purposes
	   creditCardTypes = delegator.findByAndCache("Enumeration", [enumTypeId : "CREDIT_CARD_TYPE"], ["sequenceId"]);
	   creditCardTypesMap = [:];
	   for (GenericValue creditCardType :  creditCardTypes)
	   {
		  creditCardTypesMap[creditCardType.enumCode] = creditCardType.description;
	   }
	   
	   //BUILD CONTEXT MAP FOR PRODUCT_FEATURE_TYPE_ID and DESCRIPTION(EITHER FROM PRODUCT_FEATURE_GROUP OR PRODUCT_FEATURE_TYPE)
	   Map productFeatureTypesMap = FastMap.newInstance();
	   productFeatureTypesList = delegator.findList("ProductFeatureType", null, null, null, null, true);
	   
	   //get the whole list of ProductFeatureGroup and ProductFeatureGroupAndAppl
	   productFeatureGroupList = delegator.findList("ProductFeatureGroup", null, null, null, null, true);
	   productFeatureGroupAndApplList = delegator.findList("ProductFeatureGroupAndAppl", null, null, null, null, true);
	   productFeatureGroupAndApplList = EntityUtil.filterByDate(productFeatureGroupAndApplList);
	   
	   if(UtilValidate.isNotEmpty(productFeatureTypesList))
	   {
		   for (GenericValue productFeatureType : productFeatureTypesList)
		   {
			   //filter the ProductFeatureGroupAndAppl list based on productFeatureTypeId to get the ProductFeatureGroupId
			   productFeatureGroupAndAppls = EntityUtil.filterByAnd(productFeatureGroupAndApplList, UtilMisc.toMap("productFeatureTypeId", productFeatureType.productFeatureTypeId));
			   description = "";
			   if(UtilValidate.isNotEmpty(productFeatureGroupAndAppls))
			   {
				   productFeatureGroupAndAppl = EntityUtil.getFirst(productFeatureGroupAndAppls);
				   productFeatureGroups = EntityUtil.filterByAnd(productFeatureGroupList, UtilMisc.toMap("productFeatureGroupId", productFeatureGroupAndAppl.productFeatureGroupId));
				   productFeatureGroup = EntityUtil.getFirst(productFeatureGroups);
				   description = productFeatureGroup.description;
			   }
			   else
			   {
				   description = productFeatureType.description;
			   }
			   productFeatureTypesMap.put(productFeatureType.productFeatureTypeId,description);
		   }
	   }
	   
	   //Set Context Variables
	   context.shoppingCartStoreId = shoppingCartStoreId;
	   context.ordersList = ordersList;
	   context.storePickupMap = storePickupMap;
	   context.appliedPromoList = appliedPromoList;
	   context.appliedLoyaltyPointsList = appliedLoyaltyPointsList;
	   context.isStorePickUp = isStorePickUp;
	   context.offerPriceVisible = offerPriceVisible;
	   context.orderId = orderId;
	   context.orderHeader = orderHeader;
	   context.localOrderReadHelper = orderReadHelper;
	   context.orderItems = orderItems;
	   context.shoppingCartTotalQuantity = orderItemsTotalQty;
	   context.shoppingCartSize = orderItemsTotalQty;
	   context.orderAdjustments = orderAdjustments;
	   context.orderHeaderAdjustments = orderHeaderAdjustments;
	   context.orderSubTotal = orderSubTotal;
	   context.orderItemShipGroups = orderItemShipGroups;
	   context.headerAdjustmentsToShow = headerAdjustmentsToShow;
	   currencyUomId = orderReadHelper.getCurrency();
	   context.currencyUomId = currencyUomId;
	   context.orderShippingTotal = orderShippingTotal;
	   context.orderGrandTotal = orderGrandTotal;
	   context.placingCustomerPerson = placingCustomerPerson;
	   context.billingAccount = billingAccount;
	   context.paymentMethods = paymentMethods;
	   context.productStore = productStore;
	   context.orderShipmentInfoSummaryList = orderShipmentInfoSummaryList;
	   context.customerPoNumberSet = customerPoNumberSet;
	   orderItemChangeReasons = delegator.findByAndCache("Enumeration", [enumTypeId : "ODR_ITM_CH_REASON"], ["sequenceId"]);
	   context.orderItemChangeReasons = orderItemChangeReasons;
	   //Address Locations
	   billingLocations = orderReadHelper.getBillingLocations();
	   if (UtilValidate.isNotEmpty(billingLocations))
	   {
		  context.billingAddress = EntityUtil.getFirst(billingLocations);
	   }
	   shippingLocations = orderReadHelper.getShippingLocations();
	   if (UtilValidate.isNotEmpty(shippingLocations))
	   {
		  context.shippingAddress = EntityUtil.getFirst(shippingLocations);
	   }
	   context.shippingApplies = shippingApplies;
	   if(UtilValidate.isNotEmpty(chosenShippingMethodDescription))
	   {
		   context.chosenShippingMethodDescription = chosenShippingMethodDescription;
	   }
	   //Sub Total
	   context.cartSubTotal = orderSubTotal;
	   context.currencyUom = currencyUomId;
	   if(UtilValidate.isNotEmpty(shippingInstructions))
	   {
		   context.shippingInstructions = shippingInstructions;
	   }
	   context.creditCardTypesMap = creditCardTypesMap;
	   context.productFeatureTypesMap = productFeatureTypesMap;
	   if(UtilValidate.isNotEmpty(storeInfo))
	   {
		   context.storeInfo = storeInfo;
	   }
	   context.deliveryOption = deliveryOption;
   }
}

