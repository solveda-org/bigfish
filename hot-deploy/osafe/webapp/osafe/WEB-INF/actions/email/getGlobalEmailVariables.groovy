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
    partyId = userLogin.partyId;
  }
}

orderId=context.orderId;
context.put("EMAIL_TITLE",context.title);

if (UtilValidate.isNotEmpty(partyId)) 
{
    gvParty = delegator.findByPrimaryKey("Party", [partyId : partyId]);
    if (UtilValidate.isNotEmpty(gvParty)) 
    {
        person=gvParty.getRelatedOne("Person");
        if (UtilValidate.isNotEmpty(person)) 
        {
          context.put("PARTY_ID",partyId);
          context.put("FIRST_NAME",person.firstName);
          context.put("LAST_NAME",person.lastName);
          context.put("MIDDLE_NAME",person.middleName);
          context.put("GENDER",person.gender);
          context.put("SUFFIX",person.suffix);
          context.put("PERSONAL_TITLE",person.personalTitle);
          context.put("NICKNAME",person.nickname);
        }
        userLogins=gvParty.getRelated("UserLogin");
        userLogin = EntityUtil.getFirst(userLogins);
        if (UtilValidate.isNotEmpty(userLogin)) 
        {
          context.put("LOGIN_EMAIL",userLogin.userLoginId);
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

       context.put("ORDER_HELPER",orderReadHelper);
       context.put("ORDER",orderHeader);
       context.put("ORDER_ID",orderId);
       context.put("ORDER_SUB_TOTAL",orderSubTotal);
       context.put("ORDER_SHIP_TOTAL",orderShippingTotal);
       context.put("ORDER_TAX_TOTAL",orderTaxTotal);
       context.put("ORDER_TOTAL",orderGrandTotal);
       context.put("ORDER_ITEMS",orderItems);
       context.put("ORDER_ADJUSTMENTS",headerAdjustmentsToShow);
       context.put("ORDER_SHIP_ADDRESS",shippingAddress);
       context.put("ORDER_BILL_ADDRESS",billingAddress);
       context.put("ORDER_PAYMENTS",paymentMethods);
       context.put("ORDER_PAY_PREFERENCES",orderPaymentPreferences);
       context.put("ORDER_PAYMENT_TYPE",paymentMethodType);
       context.put("ORDER_SHIPPING_INFO",orderShipmentInfoSummaryList);
       context.put("ORDER_ITEM_SHIP_GROUP",orderItemShipGroups);
    }

}
shoppingListId = context.shoppingListId;
if (UtilValidate.isNotEmpty(shoppingListId)) 
{
	shoppingCartInfoList = delegator.findByAnd("ShoppingListItem", [shoppingListId : shoppingListId]);
	context.put("CART_ITEMS",shoppingCartInfoList);
}