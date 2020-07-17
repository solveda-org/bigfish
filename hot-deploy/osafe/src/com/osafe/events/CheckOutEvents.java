/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.osafe.events;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.collections.CollectionUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.MessageString;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo;
import org.ofbiz.order.shoppingcart.shipping.ShippingEstimateWrapper;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.GeneralException;

import com.osafe.util.Util;
import com.osafe.services.TaxServices;

/**
 * Events used for processing checkout and orders.
 */
public class CheckOutEvents {

    public static final String module = CheckOutEvents.class.getName();
    private static final ResourceBundle PARAMETERS_RECURRENCE = UtilProperties.getResourceBundle("parameters_recurrence.xml", Locale.getDefault());
    private static final ResourceBundle OSAFE_UI_LABELS = UtilProperties.getResourceBundle("OSafeUiLabels.xml", Locale.getDefault());
    public static final int scale = UtilNumber.getBigDecimalScale("order.decimals");
    public static final int rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
    public static final int taxCalcScale = UtilNumber.getBigDecimalScale("salestax.calc.decimals");
    public static final int taxFinalScale = UtilNumber.getBigDecimalScale("salestax.final.decimals");
    public static final int taxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");
    

    public static String autoCaptureAuthPayments(HttpServletRequest request, HttpServletResponse response) {
        // warning there can only be ONE payment preference for this to work
        // you cannot accept multiple payment type when using an external gateway
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String orderId = (String) request.getAttribute("orderId");
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        String result = "success";

        //LS: Note: This service is automatically called from controller_ecommerce (autoCpaturePayments) checkout flow.
        //If a client wants to only 'approve' orders and not capture when order is placed update parameter CHECKOUT_CC_CAPTURE_FLAG=FALSE.
        String autoCapture =Util.getProductStoreParm(request, "CHECKOUT_CC_CAPTURE_FLAG");
        if (UtilValidate.isNotEmpty(autoCapture) && "FALSE".equals(autoCapture.toUpperCase()))
        {
            OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);        	
            return result;

        }

        
        try {
            /*
             * A bit of a hack here to get the admin user since to capture payments and complete the order requires a user who has
             * the proper security permissions
             */
            GenericValue sysLogin = delegator.findByPrimaryKeyCache("UserLogin", UtilMisc.toMap("userLoginId", "admin"));
            
            List<GenericValue> lOrderPaymentPreference = delegator.findByAnd("OrderPaymentPreference", UtilMisc.toMap("orderId", orderId, "statusId", "PAYMENT_AUTHORIZED"));
            if (UtilValidate.isNotEmpty(lOrderPaymentPreference)) 
            {
                /*
                 * This will complete the order generate invoice and capture any payments.
                 * OrderChangeHelper.completeOrder(dispatcher, sysLogin, orderId);
                 */

                /*
                 * To only capture payments and leave the order in approved status. Remove the complete order call,
                 */
            	//Get the amount to capture
                BigDecimal amountToCapture = BigDecimal.ZERO;
                for (GenericValue orderPaymentPreference : lOrderPaymentPreference) {
                	amountToCapture = amountToCapture.add(orderPaymentPreference.getBigDecimal("maxAmount")).setScale(scale, rounding);
                }

                Map<String, Object> serviceContext = UtilMisc.toMap("userLogin", sysLogin, "orderId", orderId, "captureAmount", amountToCapture);
                String captureResp = "";
                try
                {
                    Map callResult = dispatcher.runSync("captureOrderPayments", serviceContext);
                    if (ModelService.RESPOND_ERROR.equals(callResult.get(ModelService.RESPONSE_MESSAGE)))
                    {
                        captureResp = "ERROR";
                    }
                    else
                    {
                        captureResp = (String) callResult.get("processResult");
                    }
                    ServiceUtil.getMessages(request, callResult, null);
                } 
                catch (Exception e)
                {
                    captureResp = "ERROR";
                    Debug.logError(e, module);
                }
                if (captureResp.equals("FAILED") || captureResp.equals("ERROR"))
                {
                    OrderChangeHelper.cancelOrder(dispatcher, userLogin, orderId);
                    // Remove all payment method from cart except GIFT_CARD
                    List pmi = cart.getPaymentMethodIds();
                    List giftCards = cart.getGiftCards();
                    List giftCardPmi = new LinkedList();
                    if (UtilValidate.isNotEmpty(giftCards))
                    {
                        Iterator i = giftCards.iterator();
                        while (i.hasNext()) 
                        {
                            GenericValue gc = (GenericValue) i.next();
                            giftCardPmi.add(gc.getString("paymentMethodId"));
                        }
                        pmi.removeAll(giftCardPmi);
                    }
                    cart.clearPaymentMethodsById(pmi);
                    // Set order id as null in cart because order has been canceled
                    cart.setOrderId(null);
                    return "error";
                }
            }
            else
            {
                //If no payments were authorized, there are no payments to capture; approve the order.
                OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
            	
            }
            
            
        } catch (Exception e) {
            Debug.logError(e, module);
        }

        return result;
    }
    
    public static String processCartRecurrence(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        ShoppingCart sc = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        try 
        {
        	if (UtilValidate.isEmpty(userLogin))
        	{
        		userLogin = delegator.findByPrimaryKeyCache("UserLogin", UtilMisc.toMap("userLoginId", "admin"));
        		
        	}
        	for (Iterator<?> item = sc.iterator(); item.hasNext();) 
        	{
            	ShoppingCartItem sci = (ShoppingCartItem)item.next();
            	if ("SLT_AUTO_REODR".equals(sci.getShoppingListId())) 
            	{
                    Map serviceCtx = UtilMisc.toMap("userLogin", userLogin);
                    serviceCtx.put("partyId", sc.getOrderPartyId());
                    serviceCtx.put("productStoreId", sc.getProductStoreId());
                    serviceCtx.put("listName", "Shopping List Created From Shopping Cart for Product Id:" + sci.getProductId());
                    serviceCtx.put("shoppingListTypeId", "SLT_AUTO_REODR");
                    Map newListResult = null;
                    try 
                    {
                        newListResult = dispatcher.runSync("createShoppingList", serviceCtx);
                    } 
                    catch (GenericServiceException e) 
                    {
                        Debug.logError(e, "Problems creating new ShoppingList", module);
                    }

                    // check for errors
                    if (ServiceUtil.isError(newListResult)) 
                    {
                        Debug.logError("Problems creating new ShoppingList", module);
                    }

                    // get the new list id
                    if (UtilValidate.isNotEmpty(newListResult)) 
                    {
                    	sci.setShoppingList((String) newListResult.get("shoppingListId"), sci.getShoppingListItemSeqId());
                    }
            	}
        	}
        	
        } 
        catch (Exception e)
        {
            Debug.logError(e, "Problems creating new ShoppingList From Shopping Cart", module);
        	  
        }
        
        String result = "success";
        
        return result;
        
    }
    
    public static String processCartRecurrenceItems(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        ShoppingCart sc = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        String orderId = sc.getOrderId();
        
        try 
        {
        	if (UtilValidate.isNotEmpty(orderId))
        	{
                GenericValue orderHeader = null;
                orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
                OrderReadHelper orh = new OrderReadHelper(orderHeader);
                GenericValue paymentPref = EntityUtil.getFirst(orh.getPaymentPreferences());
                GenericValue shipGroup = EntityUtil.getFirst(orh.getOrderItemShipGroups());
                
            	if (UtilValidate.isEmpty(userLogin))
            	{
            		userLogin = delegator.findByPrimaryKeyCache("UserLogin", UtilMisc.toMap("userLoginId", "admin"));
            		
            	}
                
            	for (Iterator<?> item = sc.iterator(); item.hasNext();) 
            	{
                	ShoppingCartItem sci = (ShoppingCartItem)item.next();
                	if (UtilValidate.isNotEmpty(sci.getShoppingListId()))
                	{
                		String shoppingListId=sci.getShoppingListId();
                        Map shoppingListItemCtx = UtilMisc.toMap("userLogin", userLogin);
                        shoppingListItemCtx.put("shoppingListId", shoppingListId);
                        shoppingListItemCtx.put("productId", sci.getProductId());
                        shoppingListItemCtx.put("quantity", sci.getQuantity());
                        Map shoppingListItemResp = null;
                        try 
                        {
                            shoppingListItemResp = dispatcher.runSync("createShoppingListItem", shoppingListItemCtx);
                            GenericValue shoppingListItem = delegator.findByPrimaryKey("ShoppingListItem", UtilMisc.toMap("shoppingListId", shoppingListId,"shoppingListItemSeqId", (String) shoppingListItemResp.get("shoppingListItemSeqId")));
                            shoppingListItem.set("modifiedPrice", sci.getDisplayPrice());
                            shoppingListItem.store();
                        } 
                        catch (GenericServiceException e) 
                        {
                            Debug.logError(e, module);
                        }
                        

                        Map shoppingListCtx = new HashMap();
                        shoppingListCtx.put("shipmentMethodTypeId", shipGroup.get("shipmentMethodTypeId"));
                        shoppingListCtx.put("carrierRoleTypeId", shipGroup.get("carrierRoleTypeId"));
                        shoppingListCtx.put("carrierPartyId", shipGroup.get("carrierPartyId"));
                        shoppingListCtx.put("contactMechId", shipGroup.get("contactMechId"));
                        shoppingListCtx.put("paymentMethodId", paymentPref.get("paymentMethodId"));
                        shoppingListCtx.put("currencyUom", orh.getCurrency());
                        shoppingListCtx.put("isActive", "Y");
                        shoppingListCtx.put("startDateTime", UtilDateTime.nowTimestamp());
                        shoppingListCtx.put("lastOrderedDate", UtilDateTime.nowTimestamp());
                        shoppingListCtx.put("frequency", new Integer(4));
                        shoppingListCtx.put("intervalNumber", new Integer(PARAMETERS_RECURRENCE.getString("FREQUENCY")));
                        shoppingListCtx.put("shoppingListId", shoppingListId);
                        shoppingListCtx.put("userLogin", userLogin);
                        
                        Map shoppingListResp = null;
                        try 
                        {
                        	shoppingListResp = dispatcher.runSync("updateShoppingList", shoppingListCtx);
                        	
                        }
                        catch (GenericServiceException e) 
                        {
                            Debug.logError(e, module);
                        }
                	}
            	}
                
        	}

        } 
        catch (Exception e)
        {
            Debug.logError(e, "Problems creating new ShoppingList From Shopping Cart", module);
        	  
        }
        
        String result = "success";
        
        return result;
        
    }
    
    public static String processOrderAdjustmentAttributes(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        ShoppingCart sc = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        String orderId = sc.getOrderId();
        HttpSession session = request.getSession();
    	List orderAdjustmentAttributeList = (List) session.getAttribute("orderAdjustmentAttributeList");
    	List orderAdjustmentList = FastList.newInstance();
    	String result = "success";
    	
    	if (UtilValidate.isNotEmpty(orderId))
    	{
    		GenericValue orderHeader = null;
    		try
    		{
    			orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
	    	} 
    		catch (Exception e) 
	    	{
	            Debug.logError(e, module);
	        }
        	if (UtilValidate.isNotEmpty(orderHeader))
        	{
        		try
        		{
        			orderAdjustmentList = orderHeader.getRelated("OrderAdjustment");
        		}
        		catch (Exception e) 
    	    	{
    	            Debug.logError(e, module);
    	        }
        	}
        	
        	if (UtilValidate.isNotEmpty(orderAdjustmentList) && UtilValidate.isNotEmpty(orderAdjustmentAttributeList))
        	{
    	    	for(Object orderAdjustmentAttributeInfo : orderAdjustmentAttributeList)
    	    	{
    	    		Map orderAdjustmentAttributeInfoMap = (Map)orderAdjustmentAttributeInfo;
    	    		String loyaltyPointsIndex = (String) orderAdjustmentAttributeInfoMap.get("INDEX");
    	    		String loyaltyPointsAmount = (String) orderAdjustmentAttributeInfoMap.get("ADJUST_POINTS");
    	    		String adjustMethod = (String) orderAdjustmentAttributeInfoMap.get("ADJUST_METHOD");
    	            String loyaltyPointsId = (String) orderAdjustmentAttributeInfoMap.get("MEMBER_ID");
    	            String checkoutLoyaltyConversion = (String) orderAdjustmentAttributeInfoMap.get("CONVERSION_FACTOR");
    	            String expDate = (String) orderAdjustmentAttributeInfoMap.get("EXP_DATE");
    	            String currencyAmount = (String) orderAdjustmentAttributeInfoMap.get("CURRENCY_AMOUNT");
    	            BigDecimal currencyAmountBD = BigDecimal.ZERO;
    	            if (UtilValidate.isNotEmpty(currencyAmount)) 
    	            {
    	        		try 
    	                {
    	        			currencyAmountBD = new BigDecimal(currencyAmount);
    	                } 
    	                catch (NumberFormatException nfe) 
    	                {
    	                	Debug.logError(nfe, "Problems converting currencyAmount to BigDecimal", module);
    	                	currencyAmountBD = BigDecimal.ZERO;
    	                }
    	            }
    	            int loyaltyPointsIndexInt = -1;
    	            try {
    	            	loyaltyPointsIndexInt = Integer.parseInt(loyaltyPointsIndex);
    	            } 
    	            catch (Exception e) 
    	            {
    	                Debug.logError(e, module);
    	            }
    	            
    	        	GenericValue orderLoyaltyAdj = (GenericValue) orderAdjustmentList.get(loyaltyPointsIndexInt);
    	        	if (UtilValidate.isNotEmpty(orderLoyaltyAdj))
    	        	{
    	        		String orderAdjustmentId = (String) orderLoyaltyAdj.getString("orderAdjustmentId");
    	        		orderAdjustmentAttributeInfoMap.put("ORDER_ADJUSTMENT_ID", orderAdjustmentId);
    	        		try
    	                {
    		        		if (UtilValidate.isNotEmpty(loyaltyPointsAmount))
    		            	{
    		            		GenericValue orderAdjustmentAttr = delegator.makeValue("OrderAdjustmentAttribute");
    		            		orderAdjustmentAttr.set("orderAdjustmentId", orderAdjustmentId);
    		            		orderAdjustmentAttr.set("attrName", "ADJUST_METHOD");
    		            		orderAdjustmentAttr.set("attrValue", adjustMethod);
    		            		orderAdjustmentAttr.create();
    		            	}
    		        		
    		        		if (UtilValidate.isNotEmpty(loyaltyPointsId))
    		            	{
    		            		GenericValue orderAdjustmentAttr = delegator.makeValue("OrderAdjustmentAttribute");
    		            		orderAdjustmentAttr.set("orderAdjustmentId", orderAdjustmentId);
    		            		orderAdjustmentAttr.set("attrName", "MEMBER_ID");
    		            		orderAdjustmentAttr.set("attrValue", loyaltyPointsId);
    		            		orderAdjustmentAttr.create();
    		            	}
    		        		
    		        		if (UtilValidate.isNotEmpty(currencyAmount))
    		            	{
    		            		GenericValue orderAdjustmentAttr = delegator.makeValue("OrderAdjustmentAttribute");
    		            		orderAdjustmentAttr.set("orderAdjustmentId", orderAdjustmentId);
    		            		orderAdjustmentAttr.set("attrName", "ADJUST_POINTS");
    		            		orderAdjustmentAttr.set("attrValue", currencyAmount);
    		            		orderAdjustmentAttr.create();
    		            	}
    		        		
    		        		if (UtilValidate.isNotEmpty(expDate))
    		            	{
    		            		GenericValue orderAdjustmentAttr = delegator.makeValue("OrderAdjustmentAttribute");
    		            		orderAdjustmentAttr.set("orderAdjustmentId", orderAdjustmentId);
    		            		orderAdjustmentAttr.set("attrName", "EXP_DATE");
    		            		orderAdjustmentAttr.set("attrValue", expDate);
    		            		orderAdjustmentAttr.create();
    		            	}
    		        		
    		        		if (UtilValidate.isNotEmpty(checkoutLoyaltyConversion))
    		            	{
    		            		GenericValue orderAdjustmentAttr = delegator.makeValue("OrderAdjustmentAttribute");
    		            		orderAdjustmentAttr.set("orderAdjustmentId", orderAdjustmentId);
    		            		orderAdjustmentAttr.set("attrName", "CONVERSION_FACTOR");
    		            		orderAdjustmentAttr.set("attrValue", checkoutLoyaltyConversion);
    		            		orderAdjustmentAttr.create();
    		            	}
    	                }
    	        		catch (Exception e)
    	        		{
    	        			Debug.logError(e, "Problems creating new OrderAdjustmentAttribute", module);
    	                    return "error";
    	                }
    	        	}
    	    	}
        	}
    	}
    	
        return "success";
    }
    
    public static String redeemMemberLoyaltyPoints(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart sc = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        String orderId = sc.getOrderId();
        HttpSession session = request.getSession();
    	List orderAdjustmentAttributeList = (List) session.getAttribute("orderAdjustmentAttributeList");
    	String result = "success";
    	
    	if (UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(orderAdjustmentAttributeList))
    	{
    		//Call service to reduce user Loyatly points in Users Account
    		Map serviceContext = FastMap.newInstance();
	    	serviceContext.put("orderId", orderId);
	    	serviceContext.put("orderAdjustmentAttributeList", orderAdjustmentAttributeList);
	    	try 
	    	{            
	    		dispatcher.runSync("redeemLoyaltyPoints", serviceContext);
	        } 
	    	catch (Exception e) 
	    	{
	            String errMsg = "Error attempting to redeem loyalty points :" + e.toString();
	            Debug.logError(e, errMsg, module);
	            return "error";
	        }
	    	session.removeAttribute("orderAdjustmentAttributeList");
    	}
    	
        return "success";
    }
    
    
    
    public static String calcTaxOnStore(HttpServletRequest request, HttpServletResponse response) 
    {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        ShoppingCart shoppingCart = ShoppingCartEvents.getCartObject(request);
        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, shoppingCart);
        GenericValue storeAddress = null;
        
        if(UtilValidate.isNotEmpty(shoppingCart))
		{
        	String taxedStoreId = shoppingCart.getOrderAttribute("STORE_LOCATION");
			if(UtilValidate.isNotEmpty(taxedStoreId))
			{
				try 
				{
					GenericValue taxedParty = delegator.findOne("Party", UtilMisc.toMap("partyId", taxedStoreId), true);
					if (UtilValidate.isNotEmpty(taxedParty))
					{
						List taxedPartyContactMechPurpose = taxedParty.getRelatedCache("PartyContactMechPurpose");
						taxedPartyContactMechPurpose = EntityUtil.filterByDate(taxedPartyContactMechPurpose,true);
				
						List taxedPartyGeneralLocations = EntityUtil.filterByAnd(taxedPartyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "GENERAL_LOCATION"));
						taxedPartyGeneralLocations = EntityUtil.getRelatedCache("PartyContactMech", taxedPartyGeneralLocations);
						taxedPartyGeneralLocations = EntityUtil.filterByDate(taxedPartyGeneralLocations,true);
						taxedPartyGeneralLocations = EntityUtil.orderBy(taxedPartyGeneralLocations, UtilMisc.toList("fromDate DESC"));
						if(UtilValidate.isNotEmpty(taxedPartyGeneralLocations))
						{
							GenericValue taxedPartyGeneralLocation = EntityUtil.getFirst(taxedPartyGeneralLocations);
							//this DB call cannot use cache
							storeAddress = taxedPartyGeneralLocation.getRelatedOne("PostalAddress");
							try 
							{
								checkOutHelper.calcAndAddTax(storeAddress);
							} 
							catch (GeneralException e) 
							{
								String errMsg = "Error when trying to calculate tax on store Address : " + e.toString();
						        Debug.logError(e, errMsg, module);
				                return "error";
				            }
						}
					}
	            } 
				catch (GenericEntityException e) 
				{
					String errMsg = "Error when trying to get store Address to calculate tax : " + e.toString();
			        Debug.logError(e, errMsg, module);
	                return "error";
	            }
			}
		}
        
        request.setAttribute("isTaxedOnStore", "Y");
        request.setAttribute("taxedStoreAddress", storeAddress);
    	
        return "success";
    }
    
    
    
    
    
    
    
    public static String calcLoyaltyTax(HttpServletRequest request, HttpServletResponse response) 
    {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        
        String isTaxedOnStore = (String) request.getAttribute("isTaxedOnStore");
        GenericValue shipAddress = null;
        if(UtilValidate.isNotEmpty(isTaxedOnStore) && "Y".equalsIgnoreCase(isTaxedOnStore))
        {
        	shipAddress = (GenericValue) request.getAttribute("taxedStoreAddress");
        }       
        
        
        if (UtilValidate.isEmpty(cart.getShippingContactMechId()) && cart.getBillingAddress() == null && shipAddress == null) 
        {
            return "success";
        }

        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);
        BigDecimal orderAdjustmentOtherAmount = BigDecimal.ZERO;
        List<GenericValue> loyaltyAdjustments = EntityUtil.filterByAnd(cart.getAdjustments(), UtilMisc.toMap("orderAdjustmentTypeId", "LOYALTY_POINTS"));
        if (UtilValidate.isEmpty(loyaltyAdjustments))
        {
            return "success";
        	
        }
        
        BigDecimal loyaltyAdjTotal=BigDecimal.ZERO;
        
        Iterator<GenericValue> loyaltyAdjIter = loyaltyAdjustments.iterator();
        while (loyaltyAdjIter.hasNext()) 
        {
            GenericValue loyaltyAdjustment = loyaltyAdjIter.next();

            if (UtilValidate.isNotEmpty(loyaltyAdjustment)) 
            {
                BigDecimal amount = loyaltyAdjustment.getBigDecimal("amount").setScale(taxCalcScale, taxRounding);
                loyaltyAdjTotal = loyaltyAdjTotal.add(amount);
            }
        }
        
        if (loyaltyAdjTotal.compareTo(BigDecimal.ZERO) == 0)
        {
            return "success";
        	
        }

        
        
        int shipGroups = cart.getShipGroupSize();
        for (int i = 0; i < shipGroups; i++) 
        {
            Map shoppingCartItemIndexMap = new HashMap();
            Map serviceContext = null;
            ShoppingCart.CartShipInfo csi = cart.getShipInfo(i);
            if (shipAddress == null) 
            {
                shipAddress = cart.getShippingAddress(i);
                // Debug.logInfo("====== makeTaxContext set shipAddress to cart.getShippingAddress(shipGroup): " + shipAddress, module);
            }
            
            try 
            {
            	
                int totalItems = csi.shipItemInfo.size();

                List product = new ArrayList(totalItems);
                List amount = new ArrayList(totalItems);
                List price = new ArrayList(totalItems);
                List shipAmt = new ArrayList(totalItems);
                
                Iterator<ShoppingCartItem> it = csi.shipItemInfo.keySet().iterator();
                for (int itemIdx = 0; itemIdx < totalItems; itemIdx++) 
                {
                    ShoppingCartItem cartItem = it.next();
                    ShoppingCart.CartShipInfo.CartShipItemInfo itemInfo = csi.getShipItemInfo(cartItem);
                    product.add(i, cartItem.getProduct());
                    amount.add(i, cartItem.getItemSubTotal(itemInfo.quantity));
                    price.add(i, cartItem.getBasePrice());
                    shipAmt.add(i, BigDecimal.ZERO); // no per item shipping yet
                    shoppingCartItemIndexMap.put(Integer.valueOf(itemIdx), cartItem);
                }
                
           	
                serviceContext = UtilMisc.toMap("productStoreId", cart.getProductStoreId());
                serviceContext.put("payToPartyId", cart.getBillFromVendorPartyId());
                serviceContext.put("billToPartyId", cart.getBillToCustomerPartyId());
                serviceContext.put("itemProductList", product);
                serviceContext.put("itemAmountList", amount);
                serviceContext.put("itemPriceList", price);
                serviceContext.put("shippingAddress", shipAddress);
                serviceContext.put("orderLoyaltyAmount", loyaltyAdjTotal);
                Map serviceResult = null;

                try 
                {
                    serviceResult = dispatcher.runSync("calcAdjustmentTax", serviceContext);
                } 
                catch (GenericServiceException e) 
                {
                    Debug.logError(e, module);
                }

                if (ServiceUtil.isError(serviceResult)) 
                {
                    Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
                    return "success";
                }

                // the adjustments (returned in order) from taxware.
                List orderAdj = (List) serviceResult.get("orderAdjustments");
                csi.shipTaxAdj.addAll(orderAdj);
                
            } catch (Exception e) 
            {
                Debug.logError(e, module);
            }
            
            
        }
        return "success";
    }
    
    public static String setShipGroups(HttpServletRequest request, HttpServletResponse response) 
    {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        ShoppingCart shoppingCart = ShoppingCartEvents.getCartObject(request);
    	String sItemTotalQty = request.getParameter("itemTotalQuantity");
    	String sNumberOfItems = request.getParameter("numberOfItems");
    	int iNumberOfItems = Integer.valueOf(sNumberOfItems);
    	int iItemTotalQty = 0;
    	BigDecimal bcartItemTotalQty = shoppingCart.getTotalQuantity();
        Map shippingContactMechMap = FastMap.newInstance();
        Map cartLineShippingContactMechQtyMap = FastMap.newInstance();
        Map cartLineShippingContactMechGiftMsgMap = FastMap.newInstance();
        Map cartLineQtyMap = FastMap.newInstance();
        Map cartLineProductInfoMap  = FastMap.newInstance();
        Map cartItemQtyMap = FastMap.newInstance();
        List<MessageString> error_list = new ArrayList<MessageString>();
        MessageString messageString = null;
        String message = null;
        if (UtilValidate.isNotEmpty(sItemTotalQty))
        {
        	iItemTotalQty = Integer.valueOf(sItemTotalQty).intValue();
        }
    	
    	
        /* Build quantity-product maps based on items on page */
        for (int i=0; i < iItemTotalQty;i++)
        {
        	String shippingContactMechId = request.getParameter("shippingContactMechId_" + i);
        	String sCartLineIndex = request.getParameter("cartLineIndex_" + i);
    		int iCartLineIndex = Integer.valueOf(sCartLineIndex).intValue();
        	
        	String qtyInCart = request.getParameter("qtyInCart_" + i);
        	String productName = request.getParameter("productName_" + i);
        	String productId = request.getParameter("productId_" + i);
        	String productCategoryId = request.getParameter("productCategoryId_" + i);
        	String prodCatalogId = request.getParameter("prodCatelogId_" + i);
        	String unitPrice = request.getParameter("unitPrice_" + i);
        	Map productInfoMap = FastMap.newInstance();
        	productInfoMap.put("productName",productName);
        	productInfoMap.put("productId", productId);
        	productInfoMap.put("productCategoryId", productCategoryId);
        	productInfoMap.put("prodCatalogId", prodCatalogId);
        	productInfoMap.put("unitPrice", unitPrice);
        	String giftMsgFrom = request.getParameter("lineItemGiftFrom_" + i);
        	String giftMsgTo = request.getParameter("lineItemGiftTo_" + i);
        	String giftMsg = request.getParameter("lineItemGiftMsg_" + i);
        	Map giftMessageInfoMap = FastMap.newInstance();
    		if (UtilValidate.isNotEmpty(giftMsgFrom))
    		{
        	   giftMessageInfoMap.put("from",giftMsgFrom);
    		}
    		if (UtilValidate.isNotEmpty(giftMsgTo))
    		{
        	   giftMessageInfoMap.put("to", giftMsgTo);
    		}
    		if (UtilValidate.isNotEmpty(giftMsg))
    		{
        	   giftMessageInfoMap.put("msg", giftMsg);
    		}
        	BigDecimal bLineQty = BigDecimal.ZERO;
        	BigDecimal bShipQty = BigDecimal.ZERO;
        	BigDecimal bCartQty = BigDecimal.ZERO;
        	try 
        	{
        		if (UtilValidate.isNotEmpty(qtyInCart))
        		{
            		Double dQty = Double.valueOf(qtyInCart);
            		if (UtilValidate.isInteger(qtyInCart) && dQty >= 0)
            		{
                		bLineQty = BigDecimal.valueOf(dQty.doubleValue());
            		}
            		else
            		{
                		bLineQty = BigDecimal.ZERO;
                        message = OSAFE_UI_LABELS.getString("PDPQtyDecimalNumberError");
                        message = StringUtil.replaceString(message, "_PRODUCT_NAME_", productName);
                		messageString = new MessageString(message,"qtyInCart_" + i,true);
                    	error_list.add(messageString);
            			
            		}
        		}
        	}
        	catch (Exception e) 
        	{
        		bLineQty = BigDecimal.ZERO;
                message = OSAFE_UI_LABELS.getString("PDPQtyDecimalNumberError");
                message = StringUtil.replaceString(message, "_PRODUCT_NAME_", productName);
        		messageString = new MessageString(message,"qtyInCart_" + i,true);
            	error_list.add(messageString);
        		
        	}
        	shippingContactMechMap.put(shippingContactMechId,shippingContactMechId);
        	
        	if (cartLineShippingContactMechQtyMap.containsKey(sCartLineIndex +"_" + shippingContactMechId))
        	{
        		BigDecimal bTempQty = (BigDecimal)cartLineShippingContactMechQtyMap.get(sCartLineIndex +"_" + shippingContactMechId);
        		bShipQty = bShipQty.add(bTempQty);
        	}
        	bShipQty = bShipQty.add(bLineQty);
        	cartLineShippingContactMechQtyMap.put(sCartLineIndex +"_" + shippingContactMechId, bShipQty);
        	
        	if (UtilValidate.isNotEmpty(giftMessageInfoMap))
        	{
        		List lGiftMsg = null;
            	if (cartLineShippingContactMechGiftMsgMap.containsKey(sCartLineIndex +"_" + shippingContactMechId))
            	{
            		lGiftMsg = (List)cartLineShippingContactMechGiftMsgMap.get(sCartLineIndex +"_" + shippingContactMechId);
            	}
            	else
            	{
            		lGiftMsg = FastList.newInstance();
            	}
        		lGiftMsg.add(giftMessageInfoMap);
        		cartLineShippingContactMechGiftMsgMap.put(sCartLineIndex +"_" + shippingContactMechId, lGiftMsg);
        	}

        	
        	
        	if (cartLineQtyMap.containsKey(sCartLineIndex))
        	{
        		BigDecimal bTempQty = (BigDecimal)cartLineQtyMap.get(sCartLineIndex);
        		bCartQty = bCartQty.add(bTempQty);
        	}
        	bCartQty = bCartQty.add(bLineQty);
        	cartLineQtyMap.put(sCartLineIndex, bCartQty);
        	cartLineProductInfoMap.put(sCartLineIndex, productInfoMap);
        }
        
        /* Validate Quantities entered */
        if (UtilValidate.isNotEmpty(cartLineQtyMap))
        {
        	try {
        	
                String pdpQtyMin = Util.getProductStoreParm(request,"PDP_QTY_MIN");
                if (UtilValidate.isEmpty(pdpQtyMin))
                {
                	pdpQtyMin="1";
                }
                String pdpQtyMax = Util.getProductStoreParm(request,"PDP_QTY_MAX");
                if (UtilValidate.isEmpty(pdpQtyMax))
                {
                	pdpQtyMax="99";
                }
                BigDecimal bPdpQtyMin = BigDecimal.valueOf(Double.valueOf(pdpQtyMin).doubleValue());
                BigDecimal bPdpQtyMax = BigDecimal.valueOf(Double.valueOf(pdpQtyMax).doubleValue());
        		
            	Iterator<String> cartItemIter = cartLineQtyMap.keySet().iterator();
            	while (cartItemIter.hasNext())
            	{
            		String sCartLineIndex =cartItemIter.next();
            		BigDecimal bChangeQty = (BigDecimal)cartLineQtyMap.get(sCartLineIndex);
            		if (bChangeQty.compareTo(BigDecimal.ZERO) == 0)
            		{
            			continue;
            		}
            		int iCartLineIndex = Integer.valueOf(sCartLineIndex).intValue();
                	Map productInfoMap = (Map)cartLineProductInfoMap.get(sCartLineIndex);
                	GenericValue prodPdpQtyMin = delegator.findOne("ProductAttribute", UtilMisc.toMap("productId",productInfoMap.get("productId"),"attrName","PDP_QTY_MIN"), true);
                	GenericValue prodPdpQtyMax = delegator.findOne("ProductAttribute", UtilMisc.toMap("productId",productInfoMap.get("productId"),"attrName","PDP_QTY_MAX"), true);
                	if(UtilValidate.isNotEmpty(prodPdpQtyMin) && UtilValidate.isNotEmpty(prodPdpQtyMax))
                	{
                		bPdpQtyMin = BigDecimal.valueOf(Double.valueOf(prodPdpQtyMin.getString("attrValue")).doubleValue());
                		bPdpQtyMax = BigDecimal.valueOf(Double.valueOf(prodPdpQtyMax.getString("attrValue")).doubleValue());
                	}            		
                	if (bChangeQty.compareTo(bPdpQtyMin) < 0)
            		{
                        message = OSAFE_UI_LABELS.getString("PDPMinQtyError");
                        message = StringUtil.replaceString(message, "_PRODUCT_NAME_", ""+productInfoMap.get("productName"));
                        message = StringUtil.replaceString(message, "_PDP_QTY_MIN_", ""+ bPdpQtyMin.intValue());
                		messageString = new MessageString(message,"qtyInCart_" + sCartLineIndex,true);
                    	error_list.add(messageString);
            			
            		}
                	if (bChangeQty.compareTo(bPdpQtyMax) > 0)
            		{
                        message = OSAFE_UI_LABELS.getString("PDPMaxQtyError");
                        message = StringUtil.replaceString(message, "_PRODUCT_NAME_", ""+productInfoMap.get("productName"));
                        message = StringUtil.replaceString(message, "_PDP_QTY_MAX_", ""+ bPdpQtyMax.intValue());
                		messageString = new MessageString(message,"qtyInCart_" + sCartLineIndex,true);
                    	error_list.add(messageString);
            			
            		}
            		
            		
            	}
        	}
        	 catch (Exception e)
        	 {
        		 Debug.logError(e,"Error: updating cart quantity", module);
        	 }
        	
        }        
        if (error_list.size() != 0)
        {
        	request.setAttribute("_ERROR_MESSAGE_LIST_", error_list);
        	return "error";
        }
        /* Check the number of items passed from the screen matches the number of items in the cart.
         * If the number of items has changed remove all products from the cart and add back.
         * If the number of items have not changed remove zero quantities and set changed item quantities
         * The number of item check is essentially protecting against the usage of the back button.
         */
        if (UtilValidate.isNotEmpty(cartLineQtyMap))
        {
            if (shoppingCart.items().size() != iNumberOfItems)
            {
	             ShoppingCartItem shoppingCartItem=null;
	             try
	             {
	                Iterator<ShoppingCartItem> cartItemIter = shoppingCart.items().iterator();
	            	while (cartItemIter.hasNext())
	        		{
	            		shoppingCartItem = (ShoppingCartItem) cartItemIter.next();
	        			shoppingCart.removeCartItem(shoppingCartItem, dispatcher);
	        		}
	            	
	            	Iterator<String> cartLineItemIter = cartLineQtyMap.keySet().iterator();
	            	while (cartLineItemIter.hasNext())
	            	{
	            		String sCartLineIndex =cartLineItemIter.next();
	            		BigDecimal bChangeQty = (BigDecimal)cartLineQtyMap.get(sCartLineIndex);
	            		if (bChangeQty.compareTo(BigDecimal.ZERO) == 0)
	            		{
	            			continue;
	            		}
	                	Map productInfoMap = (Map)cartLineProductInfoMap.get(sCartLineIndex);
	                	String unitPrice = (String)productInfoMap.get("unitPrice");
	                	BigDecimal bUnitPrice = null;
	                	if (UtilValidate.isNotEmpty(unitPrice))
	                	{
	                		bUnitPrice = BigDecimal.valueOf(Double.valueOf(unitPrice).doubleValue());
	                	}
	                	if (UtilValidate.isEmpty(bUnitPrice))
	                	{
	                        message = OSAFE_UI_LABELS.getString("PDPMaxQtyError");
	                    	error_list.add(messageString);
	                    	request.setAttribute("_ERROR_MESSAGE_LIST_", error_list);
	                    	return "error";
	                		
	                	}
	                	
	                    ShoppingCartItem item = ShoppingCartItem.makeItem(null, ""+productInfoMap.get("productId"), null, bChangeQty, bUnitPrice, null, null, null, null, null, null, null, null, null, ""+productInfoMap.get("prodCatelogId"), null, null, null, dispatcher, shoppingCart, Boolean.TRUE, Boolean.FALSE, ""+productInfoMap.get("parentProductId"), Boolean.TRUE, Boolean.TRUE);
	                	shoppingCart.addItemToEnd(item);
	                	com.osafe.events.ShoppingCartEvents.setProductFeaturesOnCart(shoppingCart,""+productInfoMap.get("productId"));

	            	}
	            	
	             }
	        	 catch (Exception e)
	        	 {
	        		 Debug.logError("Error: removing cart item" + shoppingCartItem, module);
	        	 }
              

            	
            }
            else
            {
            	try {
            		
                	Iterator<String> cartItemIter = cartLineQtyMap.keySet().iterator();
                	while (cartItemIter.hasNext())
                	{
                		String sCartLineIndex =cartItemIter.next();
                		BigDecimal bChangeQty = (BigDecimal)cartLineQtyMap.get(sCartLineIndex);
                		int iCartLineIndex = Integer.valueOf(sCartLineIndex).intValue();
                		ShoppingCartItem shoppingCartItem = shoppingCart.findCartItem(iCartLineIndex);
                		if (bChangeQty.compareTo(BigDecimal.ZERO) == 0)
                		{
                			shoppingCart.removeCartItem(shoppingCartItem, dispatcher);
                			continue;
                		}
                		if (bChangeQty.compareTo(shoppingCartItem.getQuantity()) != 0)
                		{
                    		shoppingCartItem.setQuantity(bChangeQty, dispatcher, shoppingCart);
                		}
                		
                		
                	}
            	}
            	 catch (Exception e)
            	 {
            		 Debug.logError("Error: updating cart quantity", module);
            	 }
            	            	
            }
        	
        }
        

    	if(UtilValidate.isNotEmpty(shoppingCart.items()))
    	{
        	Iterator<ShoppingCartItem> cartItemIter = shoppingCart.items().iterator();
        	int iItemIndex=0;
        	while (cartItemIter.hasNext())
    		{
        		ShoppingCartItem shoppingCartItem = (ShoppingCartItem) cartItemIter.next();
    			BigDecimal itemQuantity = shoppingCartItem.getQuantity();
    	        cartItemQtyMap.put(""+ iItemIndex, itemQuantity);
    	        iItemIndex++;
    	        
    	        /* Clear Gift Messages per item.  Will be Reset
    	         * 
    	         */
        		Map<String, String> orderItemAttributesMap = shoppingCartItem.getOrderItemAttributes();
        		if (UtilValidate.isNotEmpty(orderItemAttributesMap))
        		{
            		for(Entry<String, String> itemAttr : orderItemAttributesMap.entrySet()) 
            		{
            			String sAttrName = (String)itemAttr.getKey();
            			if (sAttrName.startsWith("GIFT_MSG_FROM_"))
            			{
            				shoppingCartItem.removeOrderItemAttribute(sAttrName);

            			}
            			if (sAttrName.startsWith("GIFT_MSG_TO_"))
            			{
            				shoppingCartItem.removeOrderItemAttribute(sAttrName);

            			}
            			if (sAttrName.startsWith("GIFT_MSG_TEXT_"))
            			{
            				shoppingCartItem.removeOrderItemAttribute(sAttrName);

            			}
            			
            		}
        		}
    		}
    	}
    	else
    	{
        	return "emptyCart";
    		
    	}
        /* Clear item Ship Groups and create new ones */
        if (UtilValidate.isNotEmpty(shippingContactMechMap))
        {
        	Iterator<ShoppingCartItem> cartItemIter = shoppingCart.items().iterator();
        	while (cartItemIter.hasNext())
        	{
                shoppingCart.clearItemShipInfo(cartItemIter.next());

        	}
        	shoppingCart.cleanUpShipGroups();
        	
        	Iterator<String> shipGroupIter = shippingContactMechMap.keySet().iterator();
        	while (shipGroupIter.hasNext())
        	{
        		int shipGroupIndex = shoppingCart.addShipInfo();
        		String shippingContactMechId =shipGroupIter.next();
        		shoppingCart.setShippingContactMechId(shipGroupIndex, shippingContactMechId);
            	shippingContactMechMap.put(shippingContactMechId,Integer.valueOf(shipGroupIndex));
        		
        	}
        	
        }
        
        if (UtilValidate.isNotEmpty(cartLineShippingContactMechQtyMap))
        {
    		Map<ShoppingCartItem, String> cartItemMessageCount = FastMap.newInstance();
        	Iterator<String> cartLineShippingQtyIter = cartLineShippingContactMechQtyMap.keySet().iterator();
        	while (cartLineShippingQtyIter.hasNext())
        	{
        		String cartLineShippingContactMechKey =cartLineShippingQtyIter.next();
        		int iKeySeparator = cartLineShippingContactMechKey.indexOf('_');

        		String sCartLineIndex = cartLineShippingContactMechKey.substring(0,iKeySeparator);
                int iCartLineIndex = Integer.valueOf(sCartLineIndex);
        		int iItemGiftMsgCount=0;
        		
                String shippingContactMechId = cartLineShippingContactMechKey.substring(iKeySeparator + 1);

        		BigDecimal bShipQty = (BigDecimal)cartLineShippingContactMechQtyMap.get(cartLineShippingContactMechKey);
        		BigDecimal bCartItemQty = (BigDecimal)cartItemQtyMap.get(sCartLineIndex);
        		BigDecimal bCartQty = BigDecimal.ZERO;
        		if (UtilValidate.isNotEmpty(bCartItemQty))
        		{
        			bCartQty = bCartItemQty;
        		}
        		BigDecimal bTotalShipGroupQty = BigDecimal.ZERO;
        		BigDecimal bAddShipQty = BigDecimal.ZERO;

        		if (bShipQty.compareTo(BigDecimal.ZERO) > 0)
        		{
            		Map shipGroupQtyMap = shoppingCart.getShipGroups(iCartLineIndex);
            		Iterator shipGroupQtyIter = shipGroupQtyMap.keySet().iterator();
                	while (shipGroupQtyIter.hasNext())
                	{
                		BigDecimal bShipGroupQty =(BigDecimal)shipGroupQtyMap.get(shipGroupQtyIter.next());
                		bTotalShipGroupQty = bTotalShipGroupQty.add(bShipGroupQty);
                	}
                	/* Total quantity designated to Ship has already been met */
                	if (bTotalShipGroupQty.compareTo(bCartQty) == 0)
                	{
                		continue;
                	}
                	/* If the ship quantity is greater than the quantity in the cart, set the ship quantity equal to cart quantity. */
                	if (bShipQty.compareTo(bCartQty) > 0)
                	{
                		bShipQty = bCartQty;
                	}
                	/* Add the Ship quantity to total ship quantity, If greater set the ship quantity to the quantity left that can be shipped
                	 * (cart quantity minus total ship quantity) */
                	bAddShipQty = bShipQty.add(bTotalShipGroupQty);
                	if (bAddShipQty.compareTo(bCartQty) > 0)
                	{
                		bShipQty = bCartQty.subtract(bTotalShipGroupQty);
                	}
            		if (bShipQty.compareTo(BigDecimal.ZERO) > 0)
            		{
                		int shipGroupIndex = ((Integer)shippingContactMechMap.get(shippingContactMechId)).intValue();
                		shoppingCart.setItemShipGroupQty(iCartLineIndex, bShipQty, shipGroupIndex);
                		/* Check Cart item Gift Messages going to this Ship Group (Address)
                		 * 
                		 */
                	    List lGiftMsg = (List) cartLineShippingContactMechGiftMsgMap.get(cartLineShippingContactMechKey);
                	    if (UtilValidate.isNotEmpty(lGiftMsg))
                	    {
            	    		ShoppingCartItem cartItem = shoppingCart.findCartItem(iCartLineIndex);
                	    	for (int i =0; i < lGiftMsg.size();i++)
                	    	{
                	    		if (i > bShipQty.intValue())
                	    		{
                	    			break;
                	    		}
                	    		String sItemGiftMsgCount= (String)cartItemMessageCount.get(cartItem);
                	    		if (UtilValidate.isEmpty(sItemGiftMsgCount))
                	    		{
                	    		    iItemGiftMsgCount=1;
                	    		}
                	    		else
                	    		{
                	    			iItemGiftMsgCount = Integer.valueOf(sItemGiftMsgCount);
                	    			iItemGiftMsgCount++;

                	    		}
                	    		
            	    			sItemGiftMsgCount=""+iItemGiftMsgCount;
                            	cartItemMessageCount.put(cartItem,sItemGiftMsgCount);
                	    		
                	    		Map giftMsgMap = (Map)lGiftMsg.get(i);
                	    		String msgFrom = (String)giftMsgMap.get("from");
                            	if (UtilValidate.isNotEmpty(msgFrom))
                            	{
                            		cartItem.setOrderItemAttribute("GIFT_MSG_FROM_" + sItemGiftMsgCount + "_" + (shipGroupIndex + 1)  ,msgFrom);
                            		
                            	}
                	    		String msgTo = (String)giftMsgMap.get("to");
                            	if (UtilValidate.isNotEmpty(msgTo))
                            	{
                            		cartItem.setOrderItemAttribute("GIFT_MSG_TO_" + sItemGiftMsgCount + "_" + (shipGroupIndex + 1)  ,msgTo);
                            		
                            	}
                	    		String msg = (String)giftMsgMap.get("msg");
                            	if (UtilValidate.isNotEmpty(msg))
                            	{
                            		cartItem.setOrderItemAttribute("GIFT_MSG_TEXT_" + sItemGiftMsgCount + "_" + (shipGroupIndex + 1)  ,msg);
                            		
                            	}
                	    		
                	    	}
                	    }
            		}
        			
        		}

        		
        	}
        	
        	/* Now check all quantities of each cart items have been assigned to a ship group
        	 * If not calculate and add the missing quantity to the last ship group defined for the item
        	 * */
        	Iterator<ShoppingCartItem> cartItemIter = shoppingCart.items().iterator();
        	while (cartItemIter.hasNext())
    		{
        		ShoppingCartItem shoppingCartItem = (ShoppingCartItem) cartItemIter.next();
        		
        		BigDecimal bTotalShipGroupQty = BigDecimal.ZERO;
    			BigDecimal bCartQty = shoppingCartItem.getQuantity();
    			BigDecimal bShipGroupQty= BigDecimal.ZERO;
            	int iShipGroupIndex=0;
        		
        		Map shipGroupQtyMap = shoppingCart.getShipGroups(shoppingCartItem);
        		Iterator shipGroupQtyIter = shipGroupQtyMap.keySet().iterator();
            	while (shipGroupQtyIter.hasNext())
            	{
            		iShipGroupIndex=Integer.valueOf(shipGroupQtyIter.next().toString());
            		bShipGroupQty =(BigDecimal)shipGroupQtyMap.get(iShipGroupIndex);
            		bTotalShipGroupQty = bTotalShipGroupQty.add(bShipGroupQty);
            		
            	}
            	
            	if (bTotalShipGroupQty.compareTo(bCartQty) < 0)
            	{
            		BigDecimal bAddShipQty = bCartQty.subtract(bTotalShipGroupQty);
            		bAddShipQty = bAddShipQty.add(bShipGroupQty);

            		shoppingCart.setItemShipGroupQty(shoppingCartItem, bAddShipQty, iShipGroupIndex);
            	}
    		}
        	
           /* Clean up ship groups, if no quantities in group the group is removed.
            * If not calculate and add the missing quantity to the last ship group defined for the item
            * */
           	shoppingCart.cleanUpShipGroups();
            	
        	/* Check ship group with multiple items, if more than one item in the group check
        	 * the shipping options available for each item,; if different count the group is split. 
        	 * */

           	splitShipGroupByShipOptions(request,response);
           	
           	
        }

        return "success";
    }
        
	/* Check ship group with multiple items, if more than one item in the group check
	 * the shipping options available for each item,; if different split into two groups. 
	 * */
    
    public static String splitShipGroupByShipOptions(HttpServletRequest request, HttpServletResponse response) 
    {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
    	
    	try 
    	{
           	List<CartShipInfo> lCartShipGroups =  cart.getShipGroups();
            for (int i = 0; i < lCartShipGroups.size(); i++) 
            {
                CartShipInfo shipGroupShipInfo = cart.getShipInfo(i);
                Set shipItems = shipGroupShipInfo.getShipItems();
                if (UtilValidate.isNotEmpty(shipItems) && shipItems.size() > 1)
                {
            		StringBuffer sbProductStoreShipMethIdKey= new StringBuffer();
                    Map<String, String> mShipGroupTempIndexShipMethods = FastMap.newInstance();
                    Map<String, String> mShipMethodKeys = FastMap.newInstance();
                    Iterator shipItemsIterator = shipItems.iterator();
                    
                    int iShipItemIdx=0;
                    while(shipItemsIterator.hasNext()) 
                    {
                        ShoppingCartItem cartItem = (ShoppingCartItem) shipItemsIterator.next();
                    	BigDecimal cartItemQty = cart.getItemShipGroupQty(cartItem,i);

	                    /* Create a temp ShipGroup to send to the Shipping Estimate Wrapper
	                     * with just the single item in the group
	                   	 */
                		int iTempShipGroupIndex = cart.addShipInfo();
                        CartShipInfo tempCartShipInfo = cart.getShipInfo(iTempShipGroupIndex);
                        tempCartShipInfo.setContactMechId(shipGroupShipInfo.getContactMechId());
                    	
                        cart.positionItemToGroup(cartItem, cartItemQty, i, iTempShipGroupIndex, false);
                		sbProductStoreShipMethIdKey.setLength(0);

                		ShippingEstimateWrapper shippingEstWrapper = new ShippingEstimateWrapper(dispatcher, cart, iTempShipGroupIndex);
                        List<GenericValue> lCarrierShipmentMethods = shippingEstWrapper.getShippingMethods();
                        for (GenericValue carrierShipmentMethod : lCarrierShipmentMethods)
                        {
                        	sbProductStoreShipMethIdKey.append(carrierShipmentMethod.getString("productStoreShipMethId"));
                        }
                        String sProductStoreShipMethIdKey = sbProductStoreShipMethIdKey.toString();
                        if (iShipItemIdx == 0)
                        {
                            cart.positionItemToGroup(cartItem, cartItemQty, iTempShipGroupIndex, i, false);
                            mShipGroupTempIndexShipMethods.put(sProductStoreShipMethIdKey, ""+i);
                        	
                        }
                        else
                        {
                        	if (mShipGroupTempIndexShipMethods.containsKey(sProductStoreShipMethIdKey))
                        	{
                        		String sTempShipGroupIndex = mShipGroupTempIndexShipMethods.get(sProductStoreShipMethIdKey);
                                cart.positionItemToGroup(cartItem, cartItemQty, iTempShipGroupIndex, Integer.valueOf(sTempShipGroupIndex).intValue(), false);
                        		
                        	}
                        	else
                        	{
                               mShipGroupTempIndexShipMethods.put(sProductStoreShipMethIdKey, ""+iTempShipGroupIndex);
                        	}

                        }
                        
                        iShipItemIdx++;

                    }
                    
                    
                    /* Clean Up any empty Ship Groups
                   	 */
                   	cart.cleanUpShipGroups();
                	
                }
            }
           	lCartShipGroups =  cart.getShipGroups();
           	if (UtilValidate.isNotEmpty(lCartShipGroups) && (lCartShipGroups.size() > 1))
           	{
           		return "multiShipGroups";
           	}
            
            
            
    	}
    	catch (Exception e)
    	{
       	  Debug.logError(e,"Error: splitShipGroupbyShipOptions", module);
    		
    	}
        return "success";
    	
    }
    
    public static String setShipOptions(HttpServletRequest request, HttpServletResponse response) 
    {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        try 
        {
        	for (int i=0; i < cart.getShipInfoSize();i++)
        	{
        		CartShipInfo shipInfo = cart.getShipInfo(i);
            	String shippingMethod = request.getParameter("shipping_method_" + i);
                if (UtilValidate.isNotEmpty(shippingMethod) && shippingMethod.indexOf("@") != -1) 
                {
                    String shipmentMethodTypeId = shippingMethod.substring(0, shippingMethod.indexOf("@"));
                    String carrierPartyId = shippingMethod.substring(shippingMethod.indexOf("@")+1);
            		cart.setShipmentMethodTypeId(i, shipmentMethodTypeId);
            		cart.setCarrierPartyId(i, carrierPartyId);
                }
        	}
        	if (cart.getShipInfoSize() > 1)
        	{
            	cart.setOrderAttribute("DELIVERY_OPTION", "SHIP_TO_MULTI");
        	}
        	else
        	{
            	cart.setOrderAttribute("DELIVERY_OPTION", "SHIP_TO");
        		
        	}
        	
        }
         catch (Exception e)
         {
        	 Debug.logError(e,"Error: Setting Multi Ship Groups", module);
         }
        return "success";
    }

    
    
}