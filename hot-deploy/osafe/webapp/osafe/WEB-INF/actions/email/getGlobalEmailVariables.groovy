package email;

import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.webapp.control.*;
import java.math.BigDecimal;
import org.ofbiz.order.order.*;

partyId = context.partyId;
person = context.person;
if (UtilValidate.isNotEmpty(person))
{
   partyId = person.partyId;
}

if (UtilValidate.isEmpty(partyId))
{
  userLogin = context.userLogin
  if (UtilValidate.isNotEmpty(userLogin))
  {
    globalContext.put("USER_LOGIN",userLogin);
    globalContext.put("USER_LOGIN_ID",userLogin.userLoginId);
    partyId = userLogin.partyId;
  }
}
productStoreId = parameters.productStoreId;
if (UtilValidate.isNotEmpty(productStoreId))
{
  globalContext.put("PRODUCT_STORE_ID",productStoreId);

}

emailType = parameters.emailType;
if (UtilValidate.isNotEmpty(emailType))
{
  globalContext.put("EMAIL_TYPE",emailType);

}

emailMessage = parameters.message;
if (UtilValidate.isNotEmpty(emailMessage))
{
  globalContext.put("EMAIL_MESSAGE",emailMessage);

}

subscriberEmail = parameters.SUBSCRIBER_EMAIL;
if (UtilValidate.isNotEmpty(subscriberEmail))
{
  globalContext.put("SUBSCRIBER_EMAIL",subscriberEmail);

}

subscriberFirstName = parameters.SUBSCRIBER_FIRST_NAME;
if (UtilValidate.isNotEmpty(subscriberFirstName))
{
  globalContext.put("SUBSCRIBER_FIRST_NAME",subscriberFirstName);

}

subscriberLastName = parameters.SUBSCRIBER_LAST_NAME;
if (UtilValidate.isNotEmpty(subscriberLastName))
{
  globalContext.put("SUBSCRIBER_LAST_NAME",subscriberLastName);

}


orderId=context.orderId;
globalContext.put("EMAIL_TITLE",context.title);

if (UtilValidate.isNotEmpty(partyId)) 
{
    gvParty = delegator.findByPrimaryKey("Party", [partyId : partyId]);
    if (UtilValidate.isNotEmpty(gvParty)) 
    {
        person=gvParty.getRelatedOne("Person");
        if (UtilValidate.isNotEmpty(person)) 
        {
          globalContext.put("PARTY_ID",partyId);
          globalContext.put("FIRST_NAME",person.firstName);
          globalContext.put("LAST_NAME",person.lastName);
          globalContext.put("MIDDLE_NAME",person.middleName);
          globalContext.put("GENDER",person.gender);
          globalContext.put("SUFFIX",person.suffix);
          globalContext.put("PERSONAL_TITLE",person.personalTitle);
          globalContext.put("NICKNAME",person.nickname);
        }
        userLogins=gvParty.getRelated("UserLogin");
        userLogin = EntityUtil.getFirst(userLogins);
        if (UtilValidate.isNotEmpty(userLogin)) 
        {
          globalContext.put("LOGIN_EMAIL",userLogin.userLoginId);
        }
    }
}

if (UtilValidate.isNotEmpty(orderId)) 
{
    orderHeader = delegator.findByPrimaryKey("OrderHeader", [orderId : orderId]);
    if (UtilValidate.isNotEmpty(orderHeader)) 
    {
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

       orderGrandTotal = OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);

       shippingLocations = orderReadHelper.getShippingLocations();
       shippingAddress = EntityUtil.getFirst(shippingLocations);

       billingLocations = orderReadHelper.getBillingLocations();
       billingAddress = EntityUtil.getFirst(billingLocations);
       
       orderPaymentPreferences = EntityUtil.filterByAnd(orderHeader.getRelated("OrderPaymentPreference"), [EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED")]);
       paymentMethods = [];
       paymentMethodType = "";
       orderPaymentPreferences.each { opp ->
       paymentMethod = opp.getRelatedOne("PaymentMethod");
        if (paymentMethod) 
        {
            paymentMethods.add(paymentMethod);
        } 
        else 
        {
          paymentMethodType = opp.getRelatedOne("PaymentMethodType");
          if (paymentMethodType) 
          {
                paymentMethodType = paymentMethodType;
          }
        }
       }
        
       osisCond = EntityCondition.makeCondition([orderId : orderId], EntityOperator.AND);
       osisOrder = ["shipmentId", "shipmentRouteSegmentId", "shipmentPackageSeqId"];
       osisFields = ["shipmentId", "shipmentRouteSegmentId", "carrierPartyId", "shipmentMethodTypeId"] as Set;
       osisFields.add("shipmentPackageSeqId");
       osisFields.add("trackingCode");
       osisFields.add("boxNumber");
       osisFindOptions = new EntityFindOptions();
       osisFindOptions.setDistinct(true);
       orderShipmentInfoSummaryList = delegator.findList("OrderShipmentInfoSummary", osisCond, osisFields, osisOrder, osisFindOptions, false);

       globalContext.put("ORDER_HELPER",orderReadHelper);
       globalContext.put("ORDER",orderHeader);
       globalContext.put("ORDER_ID",orderId);
       globalContext.put("ORDER_SUB_TOTAL",orderSubTotal);
       globalContext.put("ORDER_SHIP_TOTAL",orderShippingTotal);
       globalContext.put("ORDER_TAX_TOTAL",orderTaxTotal);
       globalContext.put("ORDER_TOTAL",orderGrandTotal);
       globalContext.put("ORDER_ITEMS",orderItems);
       globalContext.put("ORDER_ADJUSTMENTS",headerAdjustmentsToShow);
       globalContext.put("ORDER_SHIP_ADDRESS",shippingAddress);
       globalContext.put("ORDER_BILL_ADDRESS",billingAddress);
       globalContext.put("ORDER_PAYMENTS",paymentMethods);
       globalContext.put("ORDER_PAY_PREFERENCES",orderPaymentPreferences);
       globalContext.put("ORDER_PAYMENT_TYPE",paymentMethodType);
       globalContext.put("ORDER_SHIPPING_INFO",orderShipmentInfoSummaryList);
       globalContext.put("ORDER_ITEM_SHIP_GROUP",orderItemShipGroups);
    }

}
shoppingListId = context.shoppingListId;
if (UtilValidate.isNotEmpty(shoppingListId)) 
{
	shoppingCartInfoList = delegator.findByAnd("ShoppingListItem", [shoppingListId : shoppingListId]);
	globalContext.put("CART_ITEMS",shoppingCartInfoList);
}