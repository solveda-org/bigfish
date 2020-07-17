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

//String showThankYouStatus = parameters.showThankYouStatus;
String showThankYouStatus = context.showThankYouStatus;
if (UtilValidate.isEmpty(showThankYouStatus)){
    context.showThankYouStatus ="N"
}

orderId = parameters.orderId;
orderHeader = null;
roleTypeId = "PLACING_CUSTOMER";
context.roleTypeId = roleTypeId;
if (UtilValidate.isNotEmpty(orderId)) 
 {
    orderHeader = delegator.findOne("OrderHeader", [orderId : orderId], false);
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


if (!userLogin) 
{
    userLogin = parameters.temporaryAnonymousUserLogin;
    // This is another special case, when Order is placed by anonymous user from ecommerce and then Order is completed by admin(or any user) from Order Manager
    // then userLogin is not found when Order Complete Mail is send to user.
    if (!userLogin) 
    {
        if (orderId) 
        {
            orderStatuses = orderHeader.getRelated("OrderStatus");
            filteredOrderStatusList = [];
            extOfflineModeExists = false;
            
            // Handled the case of OFFLINE payment method.
            orderPaymentPreferences = orderHeader.getRelated("OrderPaymentPreference", UtilMisc.toList("orderPaymentPreferenceId"));
            filteredOrderPaymentPreferences = EntityUtil.filterByCondition(orderPaymentPreferences, EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.IN, ["EXT_OFFLINE"]));
            if (filteredOrderPaymentPreferences) {
                extOfflineModeExists = true;
            }
            if (extOfflineModeExists) {
                filteredOrderStatusList = EntityUtil.filterByCondition(orderStatuses, EntityCondition.makeCondition("statusId", EntityOperator.IN, ["ORDER_COMPLETED", "ORDER_APPROVED", "ORDER_CREATED"]));
            } else {
                filteredOrderStatusList = EntityUtil.filterByCondition(orderStatuses, EntityCondition.makeCondition("statusId", EntityOperator.IN, ["ORDER_COMPLETED", "ORDER_APPROVED"]));
            }            
            if (UtilValidate.isNotEmpty(filteredOrderStatusList)) 
            {
                if (filteredOrderStatusList.size() < 2) 
                {
                    statusUserLogin = EntityUtil.getFirst(filteredOrderStatusList).statusUserLogin;
                    userLogin = delegator.findOne("UserLogin", [userLoginId : statusUserLogin], true);
                } else 
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
    }
    context.userLogin = userLogin;
}


if (orderHeader) 
{
    productStore = orderHeader.getRelatedOneCache("ProductStore");
    orderReadHelper = new OrderReadHelper(orderHeader);
    orderItems = orderReadHelper.getOrderItems();
    orderAdjustments = orderReadHelper.getAdjustments();
    orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments();
    orderSubTotal = orderReadHelper.getOrderItemsSubTotal();
    orderItemShipGroups = orderReadHelper.getOrderItemShipGroups();
    headerAdjustmentsToShow = orderReadHelper.getOrderHeaderAdjustmentsToShow();

    orderShippingTotal = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
    orderShippingTotal = orderShippingTotal.add(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true));

    orderTaxTotal = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, true, false);
    orderTaxTotal = orderTaxTotal.add(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, true, false));

    placingCustomerOrderRoles = delegator.findByAndCache("OrderRole", [orderId : orderId, roleTypeId : roleTypeId]);
    placingCustomerOrderRole = EntityUtil.getFirst(placingCustomerOrderRoles);
    placingCustomerPerson = placingCustomerOrderRole == null ? null : delegator.findByPrimaryKeyCache("Person", [partyId : placingCustomerOrderRole.partyId]);

    billingAccount = orderHeader.getRelatedOne("BillingAccount");

    orderPaymentPreferences = EntityUtil.filterByAnd(orderHeader.getRelated("OrderPaymentPreference"), [EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED")]);
    paymentMethods = [];
    orderPaymentPreferences.each { opp ->
        paymentMethod = opp.getRelatedOne("PaymentMethod");
        if (paymentMethod) {
            paymentMethods.add(paymentMethod);
        } else {
            paymentMethodType = opp.getRelatedOneCache("PaymentMethodType");
            if (paymentMethodType) {
                context.paymentMethodType = paymentMethodType;
            }
        }
    }
    
    webSiteId = orderHeader.webSiteId ?: CatalogWorker.getWebSiteId(request);

    payToPartyId = productStore.payToPartyId;
    paymentAddress =  PaymentWorker.getPaymentAddress(delegator, payToPartyId);
    if (paymentAddress) 
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
    orderShipmentInfoSummaryList = delegator.findList("OrderShipmentInfoSummary", osisCond, osisFields, osisOrder, osisFindOptions, false);

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
        ritems = oitem.getRelated("ReturnItem");
        ritems.each { ritem ->
            rh = ritem.getRelatedOne("ReturnHeader");
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

    context.orderId = orderId;
    context.orderHeader = orderHeader;
    context.localOrderReadHelper = orderReadHelper;
    context.orderItems = orderItems;
    context.orderAdjustments = orderAdjustments;
    context.orderHeaderAdjustments = orderHeaderAdjustments;
    context.orderSubTotal = orderSubTotal;
    context.orderItemShipGroups = orderItemShipGroups;
    context.headerAdjustmentsToShow = headerAdjustmentsToShow;
    context.currencyUomId = orderReadHelper.getCurrency();

    context.orderShippingTotal = orderShippingTotal;
    context.orderTaxTotal = orderTaxTotal;
    context.orderGrandTotal = OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);
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
    
}

if("Y".equals (showThankYouStatus)) 
{

    ResourceBundle orderNumberCaptionBundle = UtilProperties.getResourceBundle ("OSafeUiLabels", locale);
    String orderNumberCaption = orderNumberCaptionBundle.getString("OrderNumberCaption");
    messageMap=[:];
    messageMap.put("orderNumberCaption", orderNumberCaption);
    messageMap.put("orderId", orderId);
    context.OrderCompleteInfoMapped = UtilProperties.getMessage("OSafeUiLabels","OrderCompleteInfo",messageMap, locale )
}


//Retrieve CC Types for Display purposes
creditCardTypes = delegator.findByAndCache("Enumeration", [enumTypeId : "CREDIT_CARD_TYPE"], ["sequenceId"]);
creditCardTypesMap = [:];
 for (GenericValue creditCardType :  creditCardTypes) 
 {
    creditCardTypesMap[creditCardType.enumCode] = creditCardType.description;
 }

context.creditCardTypesMap = creditCardTypesMap;

if (UtilValidate.isNotEmpty(orderId)) 
{
    orderAttrPickupStore = delegator.findOne("OrderAttribute", ["orderId" : orderId, "attrName" : "STORE_LOCATION"], false);
    if (UtilValidate.isNotEmpty(orderAttrPickupStore)) 
    {
        storeId = orderAttrPickupStore.attrValue;
        context.isStorePickUp = "true"
    }
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

context.productFeatureTypesMap = productFeatureTypesMap;