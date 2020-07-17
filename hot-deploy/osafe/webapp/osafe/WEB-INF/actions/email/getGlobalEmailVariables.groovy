package email;

import org.ofbiz.base.util.*
import org.ofbiz.entity.*
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.util.*
import org.ofbiz.order.order.*
import org.ofbiz.webapp.control.*
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import com.osafe.control.SeoUrlHelper;

emailParameters = UtilProperties.getResourceBundleMap("parameters_email_styles.xml", locale);
if (UtilValidate.isNotEmpty(emailParameters))
{
    for (Map.Entry emailParameterEntry : emailParameters.entrySet()) 
    {
        globalContext.put(emailParameterEntry.getKey(),emailParameterEntry.getValue());
    }
}

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
  //PRODUCT STORE PARMS
  productStoreParmList = delegator.findByAndCache("XProductStoreParm",UtilMisc.toMap("productStoreId",productStoreId));
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

if (UtilValidate.isNotEmpty(parameters.scheduledJobName))
{
  globalContext.put("SCHED_JOB_NAME",parameters.scheduledJobName);
}

if (UtilValidate.isNotEmpty(parameters.scheduledJobStatus))
{
  globalContext.put("SCHED_JOB_STATUS",parameters.scheduledJobStatus);
}

if (UtilValidate.isNotEmpty(parameters.scheduledJobMessage))
{
  globalContext.put("SCHED_JOB_INFO",parameters.scheduledJobMessage);
}

orderId=context.orderId;
globalContext.put("EMAIL_TITLE",context.title);

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

       orderGrandTotal = orderReadHelper.getOrderGrandTotal();

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
          paymentMethodType = opp.getRelatedOneCache("PaymentMethodType");
          if (paymentMethodType) 
          {
                paymentMethodType = paymentMethodType;
          }
        }
       }
       if (UtilValidate.isEmpty(partyId))
       {
	       party = orderReadHelper.getPlacingParty();
	       partyId = party.partyId;
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
       globalContext.put("ORDER_CURRENCY",orderReadHelper.getCurrency());
       globalContext.put("ORDER",orderHeader);
       globalContext.put("ORDER_ID",orderId);
       globalContext.put("ORDER_SUB_TOTAL",orderSubTotal);
       globalContext.put("ORDER_SHIP_TOTAL",orderShippingTotal);
       globalContext.put("ORDER_TAX_TOTAL",orderTaxTotal);
       globalContext.put("ORDER_TOTAL",orderGrandTotal);
       globalContext.put("ORDER_ITEMS",orderItems);
       globalContext.put("CART_ITEMS",orderItems);
       globalContext.put("ORDER_ADJUSTMENTS",headerAdjustmentsToShow);
       globalContext.put("ORDER_SHIP_ADDRESS",shippingAddress);
       globalContext.put("ORDER_BILL_ADDRESS",billingAddress);
       //Not sure why these were populated with payment methods opposed to preferences
       globalContext.put("ORDER_PAYMENTS",orderReadHelper.getPaymentPreferences());
       globalContext.put("ORDER_PAY_PREFERENCES",orderPaymentPreferences);
       globalContext.put("ORDER_PAYMENT_TYPE",paymentMethodType);
       globalContext.put("ORDER_SHIPPING_INFO",orderShipmentInfoSummaryList);
       globalContext.put("ORDER_ITEM_SHIP_GROUP",orderItemShipGroups);
    }

}

if (UtilValidate.isNotEmpty(partyId)) 
{
    gvParty = delegator.findByPrimaryKeyCache("Party", [partyId : partyId]);
    if (UtilValidate.isNotEmpty(gvParty)) 
    {
        person=gvParty.getRelatedOneCache("Person");
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
        userLogins=gvParty.getRelatedCache("UserLogin");
        userLogin = EntityUtil.getFirst(userLogins);
        if (UtilValidate.isNotEmpty(userLogin)) 
        {
	      globalContext.put("USER_LOGIN",userLogin);
	      globalContext.put("USER_LOGIN_ID",userLogin.userLoginId);
          globalContext.put("LOGIN_EMAIL",userLogin.userLoginId);
        }
    }
}
productId = parameters.productId;
if (UtilValidate.isNotEmpty(productId)) 
{
	product =  delegator.findOne("Product", UtilMisc.toMap("productId",productId), false);
	if (UtilValidate.isNotEmpty(product))
	{
		urlProductId = product.productId;
		productCategoryId=null;
        productCategoryMemberList = product.getRelated("ProductCategoryMember");
        productCategoryMemberList = EntityUtil.filterByDate(productCategoryMemberList,true);
        productCategoryMemberList = EntityUtil.orderBy(productCategoryMemberList,UtilMisc.toList("sequenceNum"));
        if(UtilValidate.isNotEmpty(productCategoryMemberList))
        {
            productCategoryMember = EntityUtil.getFirst(productCategoryMemberList);
            productCategoryId = productCategoryMember.productCategoryId; 
        }    
		
         globalContext.put("PRODUCT",product);
         globalContext.put("PRODUCT_URL_ID",urlProductId);
         globalContext.put("PRODUCT_ID",productId);
         globalContext.put("PRODUCT_CATEGORY_ID",productCategoryId);
	}
}

shoppingListId = context.shoppingListId;
if (UtilValidate.isNotEmpty(shoppingListId)) 
{
	shoppingCartInfoList = delegator.findByAndCache("ShoppingListItem", [shoppingListId : shoppingListId]);
	globalContext.put("CART_ITEMS",shoppingCartInfoList);
}