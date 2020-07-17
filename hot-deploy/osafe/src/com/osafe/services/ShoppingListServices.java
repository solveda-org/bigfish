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
package com.osafe.services;

import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.Iterator;
import java.util.HashMap;
import java.math.BigDecimal;
import java.sql.Timestamp;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.calendar.RecurrenceInfo;
import org.ofbiz.service.calendar.RecurrenceInfoException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.product.config.ProductConfigWorker;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreWorker;

import com.osafe.util.Util;

/**
 * Shopping List Services
 */
public class ShoppingListServices {

    public static final String module = ShoppingListServices.class.getName();

    public static Map createListReorders(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        boolean beganTransaction = false;
        try {
            beganTransaction = TransactionUtil.begin();

            List exprs = UtilMisc.toList(EntityCondition.makeCondition("shoppingListTypeId", EntityOperator.EQUALS, "SLT_AUTO_REODR"),
                    EntityCondition.makeCondition("isActive", EntityOperator.EQUALS, "Y"));
            EntityCondition cond = EntityCondition.makeCondition(exprs, EntityOperator.AND);
            List order = UtilMisc.toList("-lastOrderedDate");

            EntityListIterator eli = null;
            eli = delegator.find("ShoppingList", cond, null, null, order, null);

            if (UtilValidate.isNotEmpty(eli)) 
            {
                GenericValue shoppingList;
                while (((shoppingList = (GenericValue) eli.next()) != null)) 
                {
                    Timestamp lastOrder = shoppingList.getTimestamp("lastOrderedDate");
                    GenericValue recurrenceInfo = null;
                    recurrenceInfo = shoppingList.getRelatedOne("RecurrenceInfo");
                    Timestamp startDateTime = recurrenceInfo.getTimestamp("startDateTime");
                    RecurrenceInfo recurrence = null;
                    if (recurrenceInfo != null) 
                    {
                        try 
                        {
                            recurrence = new RecurrenceInfo(recurrenceInfo);
                        } 
                        catch (RecurrenceInfoException e) 
                        {
                            Debug.logError(e, module);
                        }
                    }

                    // check the next recurrence
                    if (UtilValidate.isNotEmpty(recurrence)) 
                    {
                        long next = lastOrder == null ? recurrence.next(startDateTime.getTime()) : recurrence.next(lastOrder.getTime());
                        Timestamp now = UtilDateTime.nowTimestamp();
                        Timestamp nextOrder = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(next));

                        if (nextOrder.after(now)) 
                        {
                            continue;
                        }
                    } 
                    else 
                    {
                        continue;
                    }

                    ShoppingCart listCart = makeShoppingListCart(dispatcher, shoppingList, locale);
                    CheckOutHelper helper = new CheckOutHelper(dispatcher, delegator, listCart);

                    // store the order
                    Map createResp = helper.createOrder(userLogin);
                    if (UtilValidate.isNotEmpty(createResp) && ServiceUtil.isError(createResp)) 
                    {
                        Debug.logError("Cannot create order for shopping list - " + shoppingList, module);
                    } 
                    else 
                    {
                        String orderId = (String) createResp.get("orderId");

                        // authorize the payments
                        Map payRes = null;
                        try {
                            payRes = helper.processPayment(ProductStoreWorker.getProductStore(listCart.getProductStoreId(), delegator), userLogin);
                            if (!ServiceUtil.isError(payRes))
                            {
                                String autoCapture =Util.getProductStoreParm(delegator,listCart.getProductStoreId(), "CHECKOUT_CC_CAPTURE_FLAG");
                                if (!(UtilValidate.isNotEmpty(autoCapture) && "FALSE".equals(autoCapture.toUpperCase())))
                                {
                                    List lOrderPaymentPreference = delegator.findByAnd("OrderPaymentPreference", UtilMisc.toMap("orderId", orderId, "statusId", "PAYMENT_AUTHORIZED"));
                                    if (UtilValidate.isNotEmpty(lOrderPaymentPreference)) 
                                    {
                                        /*
                                         * This will complete the order generate invoice and capture any payments.
                                         * OrderChangeHelper.completeOrder(dispatcher, sysLogin, orderId);
                                         * To only capture payments and leave the order in approved status. Remove the complete order call,
                                         */
                                        GenericValue gvOrderPayment = EntityUtil.getFirst(lOrderPaymentPreference);
                                        Map<String, Object> serviceContext = UtilMisc.toMap("userLogin", userLogin, "orderId", orderId, "captureAmount", gvOrderPayment.getBigDecimal("maxAmount"));
                                        Map callResult = dispatcher.runSync("captureOrderPayments", serviceContext);
                                        if (callResult != null && ServiceUtil.isError(callResult)) 
                                        {
                                            Debug.logError("Payment processing problems with capturing for shopping list - " + shoppingList, module);
                                        }
                                    }
                                	
                                }
                            	
                            	
                            }
                        } catch (GeneralException e) 
                        {
                            Debug.logError(e, module);
                        }

                        if (payRes != null && ServiceUtil.isError(payRes)) 
                        {
                            Debug.logError("Payment processing problems with shopping list - " + shoppingList, module);
                        }

                        shoppingList.set("lastOrderedDate", UtilDateTime.nowTimestamp());
                        shoppingList.store();

                        // send notification
                        try 
                        {
                            dispatcher.runAsync("sendOrderPayRetryNotification", UtilMisc.toMap("orderId", orderId));
                        } catch (GenericServiceException e) 
                        {
                            Debug.logError(e, module);
                        }

                        // increment the recurrence
                        recurrence.incrementCurrentCount();
                    }
                }

                eli.close();
            }

            return ServiceUtil.returnSuccess();
            
        } 
        catch (GenericEntityException e) 
        {
            try 
            {
                // only rollback the transaction if we started one...
                TransactionUtil.rollback(beganTransaction, "Error creating shopping list auto-reorders", e);
            } 
            catch (GenericEntityException e2) 
            {
                Debug.logError(e2, "[Delegator] Could not rollback transaction: " + e2.toString(), module);
            }

            String errMsg = "Error while creating new shopping list based automatic reorder" + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
            
        } 
        finally 
        {
            try 
            {
                // only commit the transaction if we started one... this will throw an exception if it fails
                TransactionUtil.commit(beganTransaction);
            } 
            catch (GenericEntityException e) 
            {
                Debug.logError(e, "Could not commit transaction for creating new shopping list based automatic reorder", module);
            }
        }
    }


    public static ShoppingCart makeShoppingListCart(LocalDispatcher dispatcher, GenericValue shoppingList, Locale locale) {
        return makeShoppingListCart(null, dispatcher, shoppingList, locale); 
    }

    /**
     * Add a shoppinglist to an existing shoppingcart
     *
     * @param shoppingCart
     * @param dispatcher
     * @param shoppingList
     * @param locale
     * @return
     */
    public static ShoppingCart makeShoppingListCart(ShoppingCart listCart, LocalDispatcher dispatcher, GenericValue shoppingList, Locale locale) {
        Delegator delegator = dispatcher.getDelegator();
        if (UtilValidate.isNotEmpty(shoppingList) && UtilValidate.isNotEmpty(shoppingList.get("productStoreId"))) 
        {
            String productStoreId = shoppingList.getString("productStoreId");
            String currencyUom = shoppingList.getString("currencyUom");
            if (currencyUom == null) 
            {
                GenericValue productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
                if (UtilValidate.isEmpty(productStore)) 
                {
                    return null;
                }
                
                currencyUom = productStore.getString("defaultCurrencyUomId");
            }
            if (UtilValidate.isEmpty(locale))
            {
                locale = Locale.getDefault();
            }

            List items = null;
            try 
            {
                items = shoppingList.getRelated("ShoppingListItem", UtilMisc.toList("shoppingListItemSeqId"));
            } 
            catch (GenericEntityException e) 
            {
                Debug.logError(e, module);
            }

            if (UtilValidate.isNotEmpty(items)) 
            {
                if (UtilValidate.isEmpty(listCart)) 
                {
                    listCart = new ShoppingCart(delegator, productStoreId, locale, currencyUom);
                    listCart.setOrderPartyId(shoppingList.getString("partyId"));
                    listCart.setAutoOrderShoppingListId(shoppingList.getString("shoppingListId"));
                } 
                else 
                {
                    if (!listCart.getPartyId().equals(shoppingList.getString("partyId"))) 
                    {
                        Debug.logError("CANNOT add shoppingList: " + shoppingList.getString("shoppingListId")
                                + " of partyId: " + shoppingList.getString("partyId")
                                + " to a shoppingcart with a different orderPartyId: "
                                + listCart.getPartyId(), module);
                        return listCart;
                    }
                }


                Iterator i = items.iterator();
                ProductConfigWrapper configWrapper = null;
                while (i.hasNext()) 
                {
                    GenericValue shoppingListItem = (GenericValue) i.next();
                    String productId = shoppingListItem.getString("productId");
                    BigDecimal quantity = shoppingListItem.getBigDecimal("quantity");
                    BigDecimal modifiedPrice = shoppingListItem.getBigDecimal("modifiedPrice");
                    Timestamp reservStart = shoppingListItem.getTimestamp("reservStart");
                    BigDecimal reservLength = null;
                    String configId = shoppingListItem.getString("configId");
                    if (UtilValidate.isNotEmpty(shoppingListItem.get("reservLength"))) 
                    {
                        reservLength = shoppingListItem.getBigDecimal("reservLength");
                    }
                    BigDecimal reservPersons = null;
                    if (UtilValidate.isNotEmpty(shoppingListItem.get("reservPersons"))) 
                    {
                        reservPersons = shoppingListItem.getBigDecimal("reservPersons");
                    }
                    if (UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(quantity)) 
                    {

	                    if (UtilValidate.isNotEmpty(configId)) 
	                    {
	                        configWrapper = ProductConfigWorker.loadProductConfigWrapper(delegator, dispatcher, configId, productId, listCart.getProductStoreId(), null, listCart.getWebSiteId(), listCart.getCurrency(), listCart.getLocale(), listCart.getAutoUserLogin());
	                    }
                        // list items are noted in the shopping cart
                        String listId = shoppingListItem.getString("shoppingListId");
                        String itemId = shoppingListItem.getString("shoppingListItemSeqId");
                        Map attributes = UtilMisc.toMap("shoppingListId", listId, "shoppingListItemSeqId", itemId);

                        try 
                        {
                            int itemIndex = listCart.addOrIncreaseItem(productId, null, quantity, reservStart, reservLength, reservPersons, null, null, null, null, null, attributes, null, configWrapper, null, null, null, dispatcher);
                        	for (Iterator<?> item = listCart.iterator(); item.hasNext();) 
                        	{
                            	ShoppingCartItem sci = (ShoppingCartItem)item.next();
                            	if (listCart.getItemIndex(sci) == itemIndex) 
                            	{
                            		sci.setBasePrice(modifiedPrice);
                            		sci.setDisplayPrice(modifiedPrice);
                            		sci.setIsModifiedPrice(true);
                            		break;
                            	}
                            }	
                        } 
                        catch (CartItemModifyException e) 
                        {
                            Debug.logError(e, "Unable to add product to List Cart - " + productId, module);
                        } 
                        catch (ItemNotFoundException e) 
                        {
                            Debug.logError(e, "Product not found - " + productId, module);
                        }
                    }
                }

                if (listCart.size() > 0) 
                {
                    if (UtilValidate.isNotEmpty(shoppingList.get("paymentMethodId"))) 
                    {
                        listCart.addPayment(shoppingList.getString("paymentMethodId"));
                    }
                    if (UtilValidate.isNotEmpty(shoppingList.get("contactMechId"))) 
                    {
                        listCart.setShippingContactMechId(0, shoppingList.getString("contactMechId"));
                    }
                    if (UtilValidate.isNotEmpty(shoppingList.get("shipmentMethodTypeId"))) 
                    {
                        listCart.setShipmentMethodTypeId(0, shoppingList.getString("shipmentMethodTypeId"));
                    }
                    if (UtilValidate.isNotEmpty(shoppingList.get("carrierPartyId")))
                    {
                        listCart.setCarrierPartyId(0, shoppingList.getString("carrierPartyId"));
                    }
                    if (UtilValidate.isNotEmpty(shoppingList.getString("productPromoCodeId")))
                    {
                        listCart.addProductPromoCode(shoppingList.getString("productPromoCodeId"), dispatcher);
                    }
                }
            }
        }
        return listCart;
    }

}
