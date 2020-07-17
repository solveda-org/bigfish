package setup;

import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.common.CommonWorkers;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.webapp.control.*;
import com.osafe.util.Util;
import org.ofbiz.order.order.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import java.math.BigDecimal;
import org.ofbiz.base.util.UtilNumber;

//SET USER_VARIABLES GLOSSARY
productStore = ProductStoreWorker.getProductStore(request);
//PRODUCT STORE PARMS
if (UtilValidate.isNotEmpty(productStore))
{
  productStoreParmList = productStore.getRelatedCache("XProductStoreParm");
  if (UtilValidate.isNotEmpty(productStoreParmList))
  {
    parmIter = productStoreParmList.iterator();
    while (parmIter.hasNext()) 
    {
      prodStoreParm = (GenericValue) parmIter.next();
      globalContext.put(prodStoreParm.getString("parmKey"),prodStoreParm.getString("parmValue"));
    }
  }
 
}

//SHOPPING CART INFORMATION
shoppingCart = ShoppingCartEvents.getCartObject(request);
if (UtilValidate.isNotEmpty(shoppingCart))
{
   context.CART_SIZE = shoppingCart?.size() ?: 0;
   context.CART = shoppingCart;
   context.CART_ITEMS = shoppingCart.items();
   context.CART_ITEMS_QTY = shoppingCart.getTotalQuantity();
   context.CART_ITEMS_MONEY = shoppingCart.getSubTotal();
   context.CART_TOTAL_PROMO = shoppingCart.getOrderOtherAdjustmentTotal();
   context.CART_TOTAL_SHIP = shoppingCart.getTotalShipping();
   context.CART_TOTAL_TAX = shoppingCart.getTotalSalesTax();
   context.CART_TOTAL_MONEY = shoppingCart.getGrandTotal();
   context.CART_TOTAL_NET = shoppingCart.getGrandTotal().setScale(2, UtilNumber.getBigDecimalRoundingMode("order.rounding")).toString();
}


//USER LOGIN PARTY INFORMATION
userLogin = session.getAttribute("userLogin");
if (UtilValidate.isNotEmpty(userLogin))
{
    context.put("USER_LOGIN",userLogin);
    context.put("USER_LOGIN_ID",userLogin.userLoginId);
    context.put("LOGIN_EMAIL",userLogin.userLoginId);
    partyId = userLogin.partyId;
	if (UtilValidate.isNotEmpty(partyId))
	{
    	context.put("PARTY_ID",partyId);
        person=userLogin.getRelatedOneCache("Person");
        if (UtilValidate.isNotEmpty(person)) 
        {
        	context.put("FIRST_NAME",person.firstName);
        	context.put("LAST_NAME",person.lastName);
        	context.put("MIDDLE_NAME",person.middleName);
        	context.put("GENDER",person.gender);
        	context.put("SUFFIX",person.suffix);
        	context.put("PERSONAL_TITLE",person.personalTitle);
        	context.put("NICKNAME",person.nickname);
        }
        partyGroup=userLogin.getRelatedOneCache("PartyGroup");
        if (UtilValidate.isNotEmpty(partyGroup)) 
        {
        	context.put("GROUP_NAME",partyGroup.groupName);
        }
	}
}	

//ORDER INFORMATION
orderId=context.orderId;
if (UtilValidate.isNotEmpty(orderId)) 
{
    orderHeader = delegator.findByPrimaryKey("OrderHeader", [orderId : orderId]);
    if (UtilValidate.isNotEmpty(orderHeader)) 
    {
       orderReadHelper = new OrderReadHelper(orderHeader);
       orderItems = orderReadHelper.getOrderItems();
       orderItemsQty=orderReadHelper.getTotalOrderItemsQuantity();
       orderAdjustments = orderReadHelper.getAdjustments();
       orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments();
       orderSubTotal = orderReadHelper.getOrderItemsSubTotal();
       orderItemShipGroups = orderReadHelper.getOrderItemShipGroups();
       headerAdjustmentsToShow = orderReadHelper.getOrderHeaderAdjustmentsToShow();

       orderShippingTotal = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
       orderShippingTotal = orderShippingTotal.add(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true));

       orderTaxTotal = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, true, false);
       orderTaxTotal = orderTaxTotal.add(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, true, false));

       orderAdjustTotal = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, true, false, false);
       orderAdjustTotal = orderAdjustTotal.add(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, true, false, false));


       orderGrandTotal = OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);

       shippingLocations = orderReadHelper.getShippingLocations();
       shippingAddress = EntityUtil.getFirst(shippingLocations);

       billingLocations = orderReadHelper.getBillingLocations();
       billingAddress = EntityUtil.getFirst(billingLocations);
       
       orderPaymentPreferences = EntityUtil.filterByAnd(orderHeader.getRelatedCache("OrderPaymentPreference"), [EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED")]);
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

       context.ORDER_ITEMS_QTY=orderItemsQty;
       context.ORDER_ID=orderId;
       context.ORDER_ITEMS_MONEY=orderSubTotal;
       context.ORDER_TOTAL_SHIP=orderShippingTotal;
       context.ORDER_TOTAL_TAX=orderTaxTotal;
       context.ORDER_TOTAL_PROMO=orderAdjustTotal;
       context.ORDER_TOTAL_MONEY=orderGrandTotal;
       context.ORDER_TOTAL_NET=orderGrandTotal.setScale(2, UtilNumber.getBigDecimalRoundingMode("order.rounding")).toString();

       //Not Used Yet
       context.ORDER_HELPER=orderReadHelper;
       context.ORDER=orderHeader;
       context.ORDER_ITEMS=orderItems;
       context.ORDER_ADJUSTMENTS=headerAdjustmentsToShow;
       context.ORDER_SHIP_ADDRESS=shippingAddress;
       context.ORDER_BILL_ADDRESS=billingAddress;
       context.ORDER_PAYMENTS=paymentMethods;
       context.ORDER_PAY_PREFERENCES=orderPaymentPreferences;
       context.ORDER_PAYMENT_TYPE=paymentMethodType;
       context.ORDER_SHIPPING_INFO=orderShipmentInfoSummaryList;
       context.ORDER_ITEM_SHIP_GROUP=orderItemShipGroups;
    }

}