package com.osafe.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.math.NumberUtils;
import org.jdom.JDOMException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.order.OrderServices;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo;
import org.ofbiz.order.shoppingcart.shipping.ShippingEvents;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.config.ProductConfigWorker;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.osafe.util.OsafeAdminUtil;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.common.DataModelConstants;

public class OsafeAdminOrderServices 
{
    public static final String module = OsafeAdminMediaContent.class.getName();
    private static final ResourceBundle OSAFE_PROPS = UtilProperties.getResourceBundle("OsafeProperties.xml", Locale.getDefault());
    public static final String resource_error = "OrderErrorUiLabels";
    
    public static final int taxDecimals = UtilNumber.getBigDecimalScale("salestax.calc.decimals");
    public static final int taxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");
    public static final int orderDecimals = UtilNumber.getBigDecimalScale("order.decimals");
    public static final int orderRounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
    
    public static Map recalcOrderShippingAmount(DispatchContext ctx, Map context) 
    {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");
        List<String> orderItemSequenceIds = (List)context.get("orderItemSequenceIds");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        Map<String, Object> resp = null;
        
        BigDecimal adjustmentAmountTotal = BigDecimal.ZERO;
        
        // get the order header
        GenericValue orderHeader = null;
        try 
        {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } 
        catch (GenericEntityException e) 
        {
        }
        
	        OrderReadHelper orh = new OrderReadHelper(orderHeader);
	        List shipGroups = orh.getOrderItemShipGroups();
	        if (shipGroups != null) 
	        {
	            Iterator i = shipGroups.iterator();
	            while (i.hasNext()) 
	            {
	                GenericValue shipGroup = (GenericValue) i.next();
	                String shipGroupSeqId = shipGroup.getString("shipGroupSeqId");
	
	                if (shipGroup.get("contactMechId") == null || shipGroup.get("shipmentMethodTypeId") == null) 
	                {
	                    // not shipped (face-to-face order)
	                    continue;
	                }
	
	                Map shippingEstMap = ShippingEvents.getShipEstimate(dispatcher, delegator, orh, shipGroupSeqId);
	                BigDecimal shippingTotal = null;
	                if (UtilValidate.isEmpty(getValidOrderItems(shipGroupSeqId, orderItemSequenceIds, orh))) 
	                {
	                    shippingTotal = BigDecimal.ZERO;
	                    Debug.logInfo("No valid order items found - " + shippingTotal, module);
	                } 
	                else 
	                {
	                    shippingTotal = UtilValidate.isEmpty(shippingEstMap.get("shippingTotal")) ? BigDecimal.ZERO : (BigDecimal)shippingEstMap.get("shippingTotal");
	                    shippingTotal = shippingTotal.setScale(orderDecimals, orderRounding);
	                    Debug.logInfo("Got new shipping estimate - " + shippingTotal, module);
	                }
	                if (Debug.infoOn()) 
	                {
	                    Debug.logInfo("New Shipping Total [" + orderId + " / " + shipGroupSeqId + "] : " + shippingTotal, module);
	                }
	
	                BigDecimal currentShipping = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orh.getOrderItemAndShipGroupAssoc(shipGroupSeqId), orh.getAdjustments(), false, false, true);
	                currentShipping = currentShipping.add(OrderReadHelper.calcOrderAdjustments(orh.getOrderHeaderAdjustments(shipGroupSeqId), orh.getOrderItemsSubTotal(), false, false, true));
	
	                if (Debug.infoOn()) 
	                {
	                    Debug.logInfo("Old Shipping Total [" + orderId + " / " + shipGroupSeqId + "] : " + currentShipping, module);
	                }
	
	                List errorMessageList = (List) shippingEstMap.get(ModelService.ERROR_MESSAGE_LIST);
	                if (errorMessageList != null) 
	                {
	                    Debug.logWarning("Problem finding shipping estimates for [" + orderId + "/ " + shipGroupSeqId + "] = " + errorMessageList, module);
	                    continue;
	                }
	
	                if ((shippingTotal != null) && (shippingTotal.compareTo(currentShipping) != 0)) 
	                {
	                    // place the difference as a new shipping adjustment
	                    BigDecimal adjustmentAmount = shippingTotal.subtract(currentShipping);
	                    adjustmentAmountTotal = adjustmentAmountTotal.add(adjustmentAmount);
	                }
	
	                // TODO: re-balance free shipping adjustment
	            }
	        }

	    if (resp == null) resp = ServiceUtil.returnSuccess();
        resp.put("adjustmentAmountTotal", adjustmentAmountTotal);
        return resp;
    }
    
    public static Map recalcOrderTaxAmount(DispatchContext ctx, Map context) 
    {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");
        List<String> orderItemSequenceIds = (List)context.get("orderItemSequenceIds");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> resp = null;
        BigDecimal adjustmentAmountTotal = BigDecimal.ZERO;

        // get the order header
        GenericValue orderHeader = null;
        try 
        {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } 
        catch (GenericEntityException e) 
        {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotGetOrderHeaderEntity",locale) + e.getMessage());
        }

        if (orderHeader == null) 
        {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorNoValidOrderHeaderFoundForOrderId", UtilMisc.toMap("orderId",orderId), locale));
        }

        // don't charge tax on purchase orders, better we still do.....
//        if ("PURCHASE_ORDER".equals(orderHeader.getString("orderTypeId"))) {
//            return ServiceUtil.returnSuccess();
//        }

        // Retrieve the order tax adjustments
        List orderTaxAdjustments = null;
        try 
        {
            orderTaxAdjustments = delegator.findByAnd("OrderAdjustment", UtilMisc.toMap("orderId", orderId, "orderAdjustmentTypeId", "SALES_TAX"));
        } 
        catch (GenericEntityException e) 
        {
            Debug.logError(e, "Unable to retrieve SALES_TAX adjustments for order : " + orderId, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderUnableToRetrieveSalesTaxAdjustments",locale));
        }

        // Accumulate the total existing tax adjustment
        BigDecimal totalExistingOrderTax = BigDecimal.ZERO;
        Iterator otait = UtilMisc.toIterator(orderTaxAdjustments);
        while (otait != null && otait.hasNext()) 
        {
            GenericValue orderTaxAdjustment = (GenericValue) otait.next();
            if (orderTaxAdjustment.get("amount") != null) 
            {
                totalExistingOrderTax = totalExistingOrderTax.add(orderTaxAdjustment.getBigDecimal("amount").setScale(taxDecimals, taxRounding));
            }
        }
        
        
        // Recalculate the taxes for the order
        for(String orderItemSequenceId: orderItemSequenceIds)
        {
        	totalExistingOrderTax = totalExistingOrderTax.add(adjustmentAmountTotal);
	        BigDecimal totalNewOrderTax = BigDecimal.ZERO;
	        OrderReadHelper orh = new OrderReadHelper(orderHeader);
	        List shipGroups = orh.getOrderItemShipGroups();
	        if (shipGroups != null) 
	        {
	            Iterator itr = shipGroups.iterator();
	            while (itr.hasNext()) 
	            {
	                GenericValue shipGroup = (GenericValue) itr.next();
	                String shipGroupSeqId = shipGroup.getString("shipGroupSeqId");
	
	                List validOrderItems = getValidOrderItems(shipGroupSeqId, orderItemSequenceIds, orh);
	                if (UtilValidate.isNotEmpty(validOrderItems)) 
	                {
	                    // prepare the inital lists
	                    List products = new ArrayList(validOrderItems.size());
	                    List amounts = new ArrayList(validOrderItems.size());
	                    List shipAmts = new ArrayList(validOrderItems.size());
	                    List itPrices = new ArrayList(validOrderItems.size());
	
	                    // adjustments and total
	                    List allAdjustments = orh.getAdjustments();
	                    List orderHeaderAdjustments = OrderReadHelper.getOrderHeaderAdjustments(allAdjustments, shipGroupSeqId);
	                    BigDecimal orderSubTotal = OrderReadHelper.getOrderItemsSubTotal(validOrderItems, allAdjustments);
	
	                    // shipping amount
	                    BigDecimal orderShipping = OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true);
	
	                    //promotions amount
	                    BigDecimal orderPromotions = OrderReadHelper.calcOrderPromoAdjustmentsBd(allAdjustments);
	
	                    // build up the list of tax calc service parameters
	                    for (int i = 0; i < validOrderItems.size(); i++) 
	                    {
	                        GenericValue orderItem = (GenericValue) validOrderItems.get(i);
	                        String productId = orderItem.getString("productId");
	                        try 
	                        {
	                            products.add(i, delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId)));  // get the product entity
	                            amounts.add(i, OrderReadHelper.getOrderItemSubTotal(orderItem, allAdjustments, true, false)); // get the item amount
	                            shipAmts.add(i, OrderReadHelper.getOrderItemAdjustmentsTotal(orderItem, allAdjustments, false, false, true)); // get the shipping amount
	                            itPrices.add(i, orderItem.getBigDecimal("unitPrice"));
	                        } 
	                        catch (GenericEntityException e) 
	                        {
	                            Debug.logError(e, "Cannot read order item entity : " + orderItem, module);
	                            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderCannotReadTheOrderItemEntity",locale));
	                        }
	                    }
	
	                    GenericValue shippingAddress = orh.getShippingAddress(shipGroupSeqId);
	                    // no shipping address, try the billing address
	                    if (shippingAddress == null) 
	                    {
	                        List billingAddressList = orh.getBillingLocations();
	                        if (billingAddressList.size() > 0) 
	                        {
	                            shippingAddress = (GenericValue) billingAddressList.get(0);
	                        }
	                    }
	
	                    // TODO and NOTE DEJ20070816: this is NOT a good way to determine if this is a face-to-face or immediatelyFulfilled order
	                    //this should be made consistent with the CheckOutHelper.makeTaxContext(int shipGroup, GenericValue shipAddress) method
	                    if (shippingAddress == null) 
	                    {
	                        // face-to-face order; use the facility address
	                        String facilityId = orderHeader.getString("originFacilityId");
	                        if (facilityId != null) 
	                        {
	                            GenericValue facilityContactMech = ContactMechWorker.getFacilityContactMechByPurpose(delegator, facilityId, UtilMisc.toList("SHIP_ORIG_LOCATION", "PRIMARY_LOCATION"));
	                            if (facilityContactMech != null) 
	                            {
	                                try 
	                                {
	                                    shippingAddress = delegator.findByPrimaryKey("PostalAddress",
	                                            UtilMisc.toMap("contactMechId", facilityContactMech.getString("contactMechId")));
	                                } 
	                                catch (GenericEntityException e) 
	                                {
	                                    Debug.logError(e, module);
	                                }
	                            }
	                        }
	                    }
	
	                    // if shippingAddress is still null then don't calculate tax; it may be an situation where no tax is applicable, or the data is bad and we don't have a way to find an address to check tax for
	                    if (shippingAddress == null) 
	                    {
	                        continue;
	                    }
	
	                    // prepare the service context
	                    Map serviceContext = UtilMisc.toMap("productStoreId", orh.getProductStoreId(), "itemProductList", products, "itemAmountList", amounts,
	                        "itemShippingList", shipAmts, "itemPriceList", itPrices, "orderShippingAmount", orderShipping);
	                    serviceContext.put("shippingAddress", shippingAddress);
	                    serviceContext.put("orderPromotionsAmount", orderPromotions);
	                    if (orh.getBillToParty() != null) serviceContext.put("billToPartyId", orh.getBillToParty().getString("partyId"));
	                    if (orh.getBillFromParty() != null) serviceContext.put("payToPartyId", orh.getBillFromParty().getString("partyId"));
	
	                    // invoke the calcTax service
	                    Map serviceResult = null;
	                    try 
	                    {
	                        serviceResult = dispatcher.runSync("calcTax", serviceContext);
	                    } 
	                    catch (GenericServiceException e) 
	                    {
	                        Debug.logError(e, module);
	                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemOccurredInTaxService",locale));
	                    }
	
	                    if (ServiceUtil.isError(serviceResult)) 
	                    {
	                        return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
	                    }
	
	                    // the adjustments (returned in order) from the tax service
	                    List orderAdj = (List) serviceResult.get("orderAdjustments");
	                    List itemAdj = (List) serviceResult.get("itemAdjustments");
	
	                    // Accumulate the new tax total from the recalculated header adjustments
	                    if (UtilValidate.isNotEmpty(orderAdj)) 
	                    {
	                        Iterator oai = orderAdj.iterator();
	                        while (oai.hasNext()) 
	                        {
	                            GenericValue oa = (GenericValue) oai.next();
	                            if (oa.get("amount") != null) 
	                            {
	                                totalNewOrderTax = totalNewOrderTax.add(oa.getBigDecimal("amount").setScale(taxDecimals, taxRounding));
	                            }
	                        }
	                    }
	
	                    // Accumulate the new tax total from the recalculated item adjustments
	                    if (UtilValidate.isNotEmpty(itemAdj)) 
	                    {
	                        for (int i = 0; i < itemAdj.size(); i++) 
	                        {
	                            List itemAdjustments = (List) itemAdj.get(i);
	                            Iterator ida = itemAdjustments.iterator();
	                            while (ida.hasNext()) 
	                            {
	                                GenericValue ia = (GenericValue) ida.next();
	                                if (ia.get("amount") != null) 
	                                {
	                                    totalNewOrderTax = totalNewOrderTax.add(ia.getBigDecimal("amount").setScale(taxDecimals, taxRounding));
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	            // Determine the difference between existing and new tax adjustment totals, if any
	            BigDecimal orderTaxDifference = totalNewOrderTax.subtract(totalExistingOrderTax).setScale(taxDecimals, taxRounding);
	
	            // If the total has changed, create an OrderAdjustment to reflect the fact
	            if (orderTaxDifference.signum() != 0) 
	            {
	            	adjustmentAmountTotal = adjustmentAmountTotal.add(orderTaxDifference);
	            }
	        }
        }
        
        
        if (resp == null) resp = ServiceUtil.returnSuccess();
        resp.put("adjustmentAmountTotal", adjustmentAmountTotal);
        return resp;
    }
    
    
    public static Map recalcOrderPromoAmount(DispatchContext ctx, Map context) 
    {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");
        List<String> orderItemSequenceIds = (List)context.get("orderItemSequenceIds");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> resp = ServiceUtil.returnSuccess();

        BigDecimal adjustmentAmountTotal = BigDecimal.ZERO;
        BigDecimal existingOrderAdjustmentTotal = BigDecimal.ZERO;
        BigDecimal newOrderAdjustmentTotal = BigDecimal.ZERO;
        BigDecimal orderAdjustmentTotalDifference = BigDecimal.ZERO;

        GenericValue orderHeader = null;
        OrderReadHelper orh = null;
        List<GenericValue> orderAdjustments = FastList.newInstance();
        
        try 
        {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            orh = new OrderReadHelper(orderHeader);
            orderAdjustments = orderHeader.getRelated("OrderAdjustment");
        } 
        catch (GenericEntityException e) 
        {
            Debug.logError(e, module);
        }
        
        if (UtilValidate.isEmpty(orderHeader))
        {
        	return resp;
        }
        
        /* Accumulate the total existing promotional adjustment */
	        if (UtilValidate.isNotEmpty(orderAdjustments))
	        {
	        	for(GenericValue orderAdjustment : orderAdjustments)
	        	{
	        		if ("PROMOTION_ADJUSTMENT".equals(orderAdjustment.getString("orderAdjustmentTypeId")))
	        		{
	        			existingOrderAdjustmentTotal = existingOrderAdjustmentTotal.add(orderAdjustment.getBigDecimal("amount"));
	        			
	        		}
	
	        	}
	        }
        
        /*Recalculate the promotions for the order 
         * Create new cart from remaining order items not canceled 
        */
	        Map serviceContext = UtilMisc.toMap("orderId", orderId, "skipInventoryChecks", Boolean.TRUE, "skipProductChecks", Boolean.TRUE, "includePriorItemAdjustments", Boolean.FALSE, "includePriorHeaderAdjustments", Boolean.FALSE, "orderItemSequenceIds", orderItemSequenceIds);
	        Map serviceResult = null;
	        ShoppingCart cart = null;
	        List<GenericValue> adjustments = FastList.newInstance();
         
	        try 
	        {
	            serviceResult = dispatcher.runSync("createCartFromOrder",serviceContext);
	            cart = (ShoppingCart)serviceResult.get("shoppingCart");
	            
	            adjustments = cart.makeAllAdjustments();
	        } 
	        catch (Exception e) 
	        {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemOccurredInTaxService",locale));
	        }
        
        /* Accumulate the new promotion total from the recalculated promotion adjustments */
	        if (UtilValidate.isNotEmpty(adjustments))
	        {
	        	for(GenericValue adjustment : adjustments)
	        	{
	        		if ("PROMOTION_ADJUSTMENT".equals(adjustment.getString("orderAdjustmentTypeId")))
	        		{
	        			newOrderAdjustmentTotal = newOrderAdjustmentTotal.add(adjustment.getBigDecimal("amount"));
	        			
	        		}
	        	}
	        	
	        }
        /* Determine the difference between existing and new promotion adjustment totals, if any */
	        orderAdjustmentTotalDifference = newOrderAdjustmentTotal.subtract(existingOrderAdjustmentTotal);
	        
	        if (orderAdjustmentTotalDifference.compareTo(BigDecimal.ZERO) != 0)
	        {
	        	adjustmentAmountTotal =orderAdjustmentTotalDifference; 
	        }
        Debug.logInfo("Java: newOrderAdjustmentTotal:" + newOrderAdjustmentTotal, module);
        Debug.logInfo("Java: existingOrderAdjustmentTotal:" + existingOrderAdjustmentTotal, module);
        Debug.logInfo("Java: orderAdjustmentTotalDifference:" + orderAdjustmentTotalDifference, module);
        resp.put("adjustmentAmountTotal", adjustmentAmountTotal);
        return resp;
        
    }
    
    
    public static List<GenericValue> getValidOrderItems(String shipGroupSeqId, List orderItemSequenceIds, OrderReadHelper orh)
    {
    	if (shipGroupSeqId == null) return getValidOrderItems(orderItemSequenceIds, orh);
        List<EntityExpr> exprs = UtilMisc.toList(
                EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"),
                EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"),
                EntityCondition.makeCondition("orderItemSeqId", EntityOperator.NOT_IN, orderItemSequenceIds),
                EntityCondition.makeCondition("shipGroupSeqId", EntityOperator.EQUALS, shipGroupSeqId));
        return EntityUtil.filterByAnd(orh.getOrderItemAndShipGroupAssoc(), exprs);
    }
    
    public static List<GenericValue> getValidOrderItems(List orderItemSequenceIds, OrderReadHelper orh)
    {
        List<EntityExpr> exprs = UtilMisc.toList(
                EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"),
                EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"),
                EntityCondition.makeCondition("orderItemSeqId", EntityOperator.NOT_IN, orderItemSequenceIds));
        return EntityUtil.filterByAnd(orh.getOrderItems(), exprs);
    }
    
    


    public static Map<String, Object>createCartFromOrder(DispatchContext dctx, Map<String, Object> context) 
    {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        Boolean skipInventoryChecks = (Boolean) context.get("skipInventoryChecks");
        Boolean skipProductChecks = (Boolean) context.get("skipProductChecks");
        boolean includePromoItems = Boolean.TRUE.equals(context.get("includePromoItems"));
        boolean includePriorItemAdjustments = Boolean.TRUE.equals(context.get("includePriorItemAdjustments"));
        boolean includePriorHeaderAdjustments = Boolean.TRUE.equals(context.get("includePriorHeaderAdjustments"));
        List<String> cancelOrderItemSequenceIds = (List)context.get("orderItemSequenceIds");
        Locale locale = (Locale) context.get("locale");

        if (UtilValidate.isEmpty(skipInventoryChecks)) 
        {
            skipInventoryChecks = Boolean.FALSE;
        }
        if (UtilValidate.isEmpty(skipProductChecks)) 
        {
            skipProductChecks = Boolean.FALSE;
        }

        // get the order header
        GenericValue orderHeader = null;
        try 
        {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) 
          {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
          }

        // initial require cart info
        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        String productStoreId = orh.getProductStoreId();
        String orderTypeId = orh.getOrderTypeId();
        String currency = orh.getCurrency();
        String website = orh.getWebSiteId();
        String currentStatusString = orh.getCurrentStatusString();

        // create the cart
        ShoppingCart cart = new ShoppingCart(delegator, productStoreId, website, locale, currency);
        cart.setDoPromotions(!includePromoItems);
        cart.setOrderType(orderTypeId);
        cart.setChannelType(orderHeader.getString("salesChannelEnumId"));
        cart.setInternalCode(orderHeader.getString("internalCode"));
        cart.setOrderDate(orderHeader.getTimestamp("orderDate"));
        cart.setOrderId(orderHeader.getString("orderId"));
        cart.setOrderName(orderHeader.getString("orderName"));
        cart.setOrderStatusId(orderHeader.getString("statusId"));
        cart.setOrderStatusString(currentStatusString);

        try 
        {
            cart.setUserLogin(userLogin, dispatcher);
        } catch (CartItemModifyException e) 
        {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // set the order name
        String orderName = orh.getOrderName();
        if (orderName != null) 
        {
            cart.setOrderName(orderName);
        }

        // set the role information
        GenericValue placingParty = orh.getPlacingParty();
        if (placingParty != null) 
        {
            cart.setPlacingCustomerPartyId(placingParty.getString("partyId"));
        }

        GenericValue billFromParty = orh.getBillFromParty();
        if (billFromParty != null) 
        {
            cart.setBillFromVendorPartyId(billFromParty.getString("partyId"));
        }

        GenericValue billToParty = orh.getBillToParty();
        if (billToParty != null) 
        {
            cart.setBillToCustomerPartyId(billToParty.getString("partyId"));
        }

        GenericValue shipToParty = orh.getShipToParty();
        if (shipToParty != null) {
            cart.setShipToCustomerPartyId(shipToParty.getString("partyId"));
        }

        GenericValue endUserParty = orh.getEndUserParty();
        if (endUserParty != null) 
        {
            cart.setEndUserCustomerPartyId(endUserParty.getString("partyId"));
            cart.setOrderPartyId(endUserParty.getString("partyId"));
        }

        // load order attributes
        List<GenericValue> orderAttributesList = null;
        try 
        {
            orderAttributesList = delegator.findByAnd("OrderAttribute", UtilMisc.toMap("orderId", orderId));
            if (UtilValidate.isNotEmpty(orderAttributesList)) 
            {
                for (GenericValue orderAttr : orderAttributesList) 
                {
                    String name = orderAttr.getString("attrName");
                    String value = orderAttr.getString("attrValue");
                    cart.setOrderAttribute(name, value);
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // load the payment infos
        List<GenericValue> orderPaymentPrefs = null;
        try 
        {
            List<EntityExpr> exprs = UtilMisc.toList(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
            exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_RECEIVED"));
            exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED"));
            exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_DECLINED"));
            exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_SETTLED"));
            EntityCondition cond = EntityCondition.makeCondition(exprs, EntityOperator.AND);
            orderPaymentPrefs = delegator.findList("OrderPaymentPreference", cond, null, null, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        if (UtilValidate.isNotEmpty(orderPaymentPrefs)) 
        {
            Iterator<GenericValue> oppi = orderPaymentPrefs.iterator();
            while (oppi.hasNext()) 
            {
                GenericValue opp = (GenericValue) oppi.next();
                String paymentId = opp.getString("paymentMethodId");
                if (paymentId == null) {
                    paymentId = opp.getString("paymentMethodTypeId");
                }
                BigDecimal maxAmount = opp.getBigDecimal("maxAmount");
                String overflow = opp.getString("overflowFlag");

                ShoppingCart.CartPaymentInfo cpi = null;

                if ((overflow == null || !"Y".equals(overflow)) && oppi.hasNext()) 
                {
                    cpi = cart.addPaymentAmount(paymentId, maxAmount);
                    Debug.log("Added Payment: " + paymentId + " / " + maxAmount, module);
                } else 
                {
                    cpi = cart.addPayment(paymentId);
                    Debug.log("Added Payment: " + paymentId + " / [no max]", module);
                }
                // for finance account the finAccountId needs to be set
                if ("FIN_ACCOUNT".equals(paymentId)) 
                {
                    cpi.finAccountId = opp.getString("finAccountId");
                }
                // set the billing account and amount
                cart.setBillingAccount(orderHeader.getString("billingAccountId"), orh.getBillingAccountMaxAmount());
            }
        } 
        else 
        {
            Debug.log("No payment preferences found for order #" + orderId, module);
        }

        List<GenericValue> orderItemShipGroupList = orh.getOrderItemShipGroups();
        for (GenericValue orderItemShipGroup: orderItemShipGroupList) 
        {
            // should be sorted by shipGroupSeqId
            int newShipInfoIndex = cart.addShipInfo();

            // shouldn't be gaps in it but allow for that just in case
            String cartShipGroupIndexStr = orderItemShipGroup.getString("shipGroupSeqId");
            int cartShipGroupIndex = NumberUtils.toInt(cartShipGroupIndexStr);

            if (newShipInfoIndex != (cartShipGroupIndex - 1)) 
            {
                int groupDiff = cartShipGroupIndex - cart.getShipGroupSize();
                for (int i = 0; i < groupDiff; i++) 
                {
                    newShipInfoIndex = cart.addShipInfo();
                }
            }

            CartShipInfo cartShipInfo = cart.getShipInfo(newShipInfoIndex);

            cartShipInfo.shipAfterDate = orderItemShipGroup.getTimestamp("shipAfterDate");
            cartShipInfo.shipBeforeDate = orderItemShipGroup.getTimestamp("shipByDate");
            cartShipInfo.shipmentMethodTypeId = orderItemShipGroup.getString("shipmentMethodTypeId");
            cartShipInfo.carrierPartyId = orderItemShipGroup.getString("carrierPartyId");
            cartShipInfo.supplierPartyId = orderItemShipGroup.getString("supplierPartyId");
            cartShipInfo.setMaySplit(orderItemShipGroup.getBoolean("maySplit"));
            cartShipInfo.giftMessage = orderItemShipGroup.getString("giftMessage");
            cartShipInfo.setContactMechId(orderItemShipGroup.getString("contactMechId"));
            cartShipInfo.shippingInstructions = orderItemShipGroup.getString("shippingInstructions");
            cartShipInfo.setFacilityId(orderItemShipGroup.getString("facilityId"));
            cartShipInfo.setVendorPartyId(orderItemShipGroup.getString("vendorPartyId"));
            cartShipInfo.setShipGroupSeqId(orderItemShipGroup.getString("shipGroupSeqId"));
        }

        List<GenericValue> orderItems = orh.getValidOrderItems();
        if (UtilValidate.isNotEmpty(cancelOrderItemSequenceIds))
        {
            for(GenericValue orderItem : orderItems)
            {
            	if (cancelOrderItemSequenceIds.contains(orderItem.getString("orderItemSeqId")))
            	{
            		orderItems.remove(orderItem);
            	}
            }
        	
        }
        
        long nextItemSeq = 0;
        if (UtilValidate.isNotEmpty(orderItems)) 
        {
            for (GenericValue item : orderItems) 
            {
                // get the next item sequence id
                String orderItemSeqId = item.getString("orderItemSeqId");
                orderItemSeqId = orderItemSeqId.replaceAll("\\P{Digit}", "");
                // get product Id
                String productId = item.getString("productId");
                GenericValue product = null;
                // creates survey responses for Gift cards same as last Order created
                Map surveyResponseResult = null;
                try 
                {
                    product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
                    if ("DIGITAL_GOOD".equals(product.getString("productTypeId"))) 
                    {
                        Map<String, Object> surveyResponseMap = FastMap.newInstance();
                        Map<String, Object> answers = FastMap.newInstance();
                        List<GenericValue> surveyResponseAndAnswers = delegator.findByAnd("SurveyResponseAndAnswer", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
                        if (UtilValidate.isNotEmpty(surveyResponseAndAnswers)) 
                        {
                            String surveyId = EntityUtil.getFirst(surveyResponseAndAnswers).getString("surveyId");
                            for (GenericValue surveyResponseAndAnswer : surveyResponseAndAnswers) 
                            {
                                answers.put((surveyResponseAndAnswer.get("surveyQuestionId").toString()), surveyResponseAndAnswer.get("textResponse"));
                            }
                            surveyResponseMap.put("answers", answers);
                            surveyResponseMap.put("surveyId", surveyId);
                            surveyResponseResult = dispatcher.runSync("createSurveyResponse", surveyResponseMap);
                        }
                    }
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                } catch (GenericServiceException e) {
                    Debug.logError(e.toString(), module);
                    return ServiceUtil.returnError(e.toString());
                }
                try 
                {
                    long seq = Long.parseLong(orderItemSeqId);
                    if (seq > nextItemSeq) {
                        nextItemSeq = seq;
                    }
                } catch (NumberFormatException e) 
                {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }

                // do not include PROMO items
                if (!includePromoItems && item.get("isPromo") != null && "Y".equals(item.getString("isPromo"))) 
                {
                    continue;
                }

                // not a promo item; go ahead and add it in
                BigDecimal amount = item.getBigDecimal("selectedAmount");
                if (amount == null) 
                {
                    amount = BigDecimal.ZERO;
                }
                BigDecimal quantity = item.getBigDecimal("quantity");
                if (quantity == null) 
                {
                    quantity = BigDecimal.ZERO;
                }

                BigDecimal unitPrice = null;
                if ("Y".equals(item.getString("isModifiedPrice"))) 
                {
                    unitPrice = item.getBigDecimal("unitPrice");
                }

                int itemIndex = -1;
                if (item.get("productId") == null) 
                {
                    // non-product item
                    String itemType = item.getString("orderItemTypeId");
                    String desc = item.getString("itemDescription");
                    try 
                    {
                        // TODO: passing in null now for itemGroupNumber, but should reproduce from OrderItemGroup records
                        itemIndex = cart.addNonProductItem(itemType, desc, null, unitPrice, quantity, null, null, null, dispatcher);
                    } catch (CartItemModifyException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(e.getMessage());
                    }
                } 
                else 
                {
                    // product item
                    String prodCatalogId = item.getString("prodCatalogId");

                    //prepare the rental data
                    Timestamp reservStart = null;
                    BigDecimal reservLength = null;
                    BigDecimal reservPersons = null;
                    String accommodationMapId = null;
                    String accommodationSpotId = null;

                    GenericValue workEffort = null;
                    String workEffortId = orh.getCurrentOrderItemWorkEffort(item);
                    if (workEffortId != null) 
                    {
                        try 
                        {
                            workEffort = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId));
                        } catch (GenericEntityException e) {
                            Debug.logError(e, module);
                        }
                    }
                    if (workEffort != null && "ASSET_USAGE".equals(workEffort.getString("workEffortTypeId"))) 
                    {
                        reservStart = workEffort.getTimestamp("estimatedStartDate");
                        reservLength = OrderReadHelper.getWorkEffortRentalLength(workEffort);
                        reservPersons = workEffort.getBigDecimal("reservPersons");
                        accommodationMapId = workEffort.getString("accommodationMapId");
                        accommodationSpotId = workEffort.getString("accommodationSpotId");

                    }    //end of rental data

                    //check for AGGREGATED products
                    ProductConfigWrapper configWrapper = null;
                    String configId = null;
                    try 
                    {
                        product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
                        if ("AGGREGATED_CONF".equals(product.getString("productTypeId"))) 
                        {
                            List<GenericValue>productAssocs = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productAssocTypeId", "PRODUCT_CONF", "productIdTo", product.getString("productId")));
                            productAssocs = EntityUtil.filterByDate(productAssocs);
                            if (UtilValidate.isNotEmpty(productAssocs)) 
                            {
                                productId = EntityUtil.getFirst(productAssocs).getString("productId");
                                configId = product.getString("configId");
                            }
                        }
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                    }

                    if (UtilValidate.isNotEmpty(configId)) 
                    {
                        configWrapper = ProductConfigWorker.loadProductConfigWrapper(delegator, dispatcher, configId, productId, productStoreId, prodCatalogId, website, currency, locale, userLogin);
                    }
                    try 
                    {
                        itemIndex = cart.addItemToEnd(productId, amount, quantity, unitPrice, reservStart, reservLength, reservPersons,accommodationMapId,accommodationSpotId, null, null, prodCatalogId, configWrapper, item.getString("orderItemTypeId"), dispatcher, null, unitPrice == null ? null : false, skipInventoryChecks, skipProductChecks);
                    } catch (ItemNotFoundException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(e.getMessage());
                    } catch (CartItemModifyException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(e.getMessage());
                    }
                }

                // flag the item w/ the orderItemSeqId so we can reference it
                ShoppingCartItem cartItem = cart.findCartItem(itemIndex);
                cartItem.setIsPromo(item.get("isPromo") != null && "Y".equals(item.getString("isPromo")));
                cartItem.setOrderItemSeqId(item.getString("orderItemSeqId"));

                try 
                {
                    cartItem.setItemGroup(cart.addItemGroup(item.getRelatedOneCache("OrderItemGroup")));
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }
                // attach surveyResponseId for each item
                if (UtilValidate.isNotEmpty(surveyResponseResult))
                {
                    cartItem.setAttribute("surveyResponseId",surveyResponseResult.get("surveyResponseId"));
                }
                // attach addition item information
                cartItem.setStatusId(item.getString("statusId"));
                cartItem.setItemType(item.getString("orderItemTypeId"));
                cartItem.setItemComment(item.getString("comments"));
                cartItem.setQuoteId(item.getString("quoteId"));
                cartItem.setQuoteItemSeqId(item.getString("quoteItemSeqId"));
                cartItem.setProductCategoryId(item.getString("productCategoryId"));
                cartItem.setDesiredDeliveryDate(item.getTimestamp("estimatedDeliveryDate"));
                cartItem.setShipBeforeDate(item.getTimestamp("shipBeforeDate"));
                cartItem.setShipAfterDate(item.getTimestamp("shipAfterDate"));
                cartItem.setShoppingList(item.getString("shoppingListId"), item.getString("shoppingListItemSeqId"));
                cartItem.setIsModifiedPrice("Y".equals(item.getString("isModifiedPrice")));
                cartItem.setName(item.getString("itemDescription"));

                // load order item attributes
                List<GenericValue> orderItemAttributesList = null;
                try 
                {
                    orderItemAttributesList = delegator.findByAnd("OrderItemAttribute", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
                    if (UtilValidate.isNotEmpty(orderAttributesList)) 
                    {
                        for(GenericValue orderItemAttr : orderItemAttributesList) 
                        {
                            String name = orderItemAttr.getString("attrName");
                            String value = orderItemAttr.getString("attrValue");
                            cartItem.setOrderItemAttribute(name, value);
                        }
                    }
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }

                // load order item contact mechs
                List<GenericValue> orderItemContactMechList = null;
                try 
                {
                    orderItemContactMechList = delegator.findByAnd("OrderItemContactMech", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
                    if (UtilValidate.isNotEmpty(orderItemContactMechList)) 
                    {
                        for (GenericValue orderItemContactMech : orderItemContactMechList) 
                        {
                            String contactMechPurposeTypeId = orderItemContactMech.getString("contactMechPurposeTypeId");
                            String contactMechId = orderItemContactMech.getString("contactMechId");
                            cartItem.addContactMech(contactMechPurposeTypeId, contactMechId);
                        }
                    }
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }

                // set the PO number on the cart
                cart.setPoNumber(item.getString("correspondingPoId"));

                if (includePriorItemAdjustments)
                {
                    List<GenericValue> itemAdjustments = orh.getOrderItemAdjustments(item);
                    if (itemAdjustments != null) 
                    {
                        for(GenericValue itemAdjustment : itemAdjustments) 
                        {
                            cartItem.addAdjustment(itemAdjustment);
                        }
                    }
                	
                }
            }

            // setup the OrderItemShipGroupAssoc records
            if (UtilValidate.isNotEmpty(orderItems)) 
            {
                int itemIndex = 0;
                for (GenericValue item : orderItems) 
                {

                    // set the item's ship group info
                    List<GenericValue> shipGroupAssocs = orh.getOrderItemShipGroupAssocs(item);
                    for (int g = 0; g < shipGroupAssocs.size(); g++) 
                    {
                        GenericValue sgAssoc = (GenericValue) shipGroupAssocs.get(g);
                        BigDecimal shipGroupQty = OrderReadHelper.getOrderItemShipGroupQuantity(sgAssoc);
                        if (shipGroupQty == null) 
                        {
                            shipGroupQty = BigDecimal.ZERO;
                        }

                        String cartShipGroupIndexStr = sgAssoc.getString("shipGroupSeqId");
                        int cartShipGroupIndex = NumberUtils.toInt(cartShipGroupIndexStr);

                        cartShipGroupIndex = cartShipGroupIndex - 1;
                        if (cartShipGroupIndex > 0) 
                        {
                            cart.positionItemToGroup(itemIndex, shipGroupQty, 0, cartShipGroupIndex, false);
                        }

                        cart.setItemShipGroupQty(itemIndex, shipGroupQty, cartShipGroupIndex);
                    }
                    itemIndex ++;
                }
            }

            // set the item seq in the cart
            if (nextItemSeq > 0) 
            {
                try 
                {
                    cart.setNextItemSeq(nextItemSeq);
                } catch (GeneralException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }
            }
        }

        if (includePromoItems) 
        {
            for (String productPromoCode: orh.getProductPromoCodesEntered()) 
            {
                cart.addProductPromoCode(productPromoCode, dispatcher);
            }
            for (GenericValue productPromoUse: orh.getProductPromoUse()) 
            {
                cart.addProductPromoUse(productPromoUse.getString("productPromoId"), productPromoUse.getString("productPromoCodeId"), productPromoUse.getBigDecimal("totalDiscountAmount"), productPromoUse.getBigDecimal("quantityLeftInActions"));
            }
        }

        if (includePriorHeaderAdjustments)
        {
            List adjustments = orh.getOrderHeaderAdjustments();
            if (!adjustments.isEmpty()) 
            {
                // The cart adjustments are added to the cart
                cart.getAdjustments().addAll(adjustments);
            }
        	
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("shoppingCart", cart);
        return result;
    }
}