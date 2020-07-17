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

        Map<String, Object> resp = null;
        
        BigDecimal adjustmentAmountTotal = BigDecimal.ZERO;
        
        // get the order header
        GenericValue orderHeader = null;
        List<GenericValue> orderAdjustments = FastList.newInstance();
        try 
        {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            orderAdjustments = orderHeader.getRelated("OrderAdjustment");
        } 
        catch (GenericEntityException e) 
        {
        }
        BigDecimal existingOrderAdjustmentTotal = BigDecimal.ZERO;
        for(GenericValue orderAdjustment : orderAdjustments)
        {
        	if(UtilValidate.isNotEmpty(orderAdjustment.getString("productPromoId")))
        	{
        		existingOrderAdjustmentTotal = existingOrderAdjustmentTotal.add(orderAdjustment.getBigDecimal("amount"));
        	}
        }
        
        //CREATE CART FROM ORDER
        
         Map serviceContext = UtilMisc.toMap("orderId", orderId, "skipInventoryChecks", Boolean.TRUE, "skipProductChecks", Boolean.TRUE, "orderItemSequenceIds", orderItemSequenceIds);
         Map serviceResult = null;
         ShoppingCart cart = null;
         List<GenericValue> adjustments = FastList.newInstance();
         
        try 
        {
            serviceResult = dispatcher.runSync("createCartFromOrder", serviceContext);
            cart = (ShoppingCart)serviceResult.get("shoppingCart");
            
            adjustments = cart.makeAllAdjustments();
        } 
        catch (GenericServiceException e) 
        {
            Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemOccurredInTaxService",locale));
        }
        BigDecimal newOrderAdjustmentTotal = BigDecimal.ZERO;
        for(GenericValue adjustment : adjustments)
        {
        	if(UtilValidate.isNotEmpty(adjustment.getString("productPromoId")))
        	{
        		newOrderAdjustmentTotal = newOrderAdjustmentTotal.add(adjustment.getBigDecimal("amount"));
        	}
        }
        
        BigDecimal orderAdjustmentTotalDifference = BigDecimal.ZERO;
        orderAdjustmentTotalDifference = newOrderAdjustmentTotal.subtract(existingOrderAdjustmentTotal);
	    if (resp == null) resp = ServiceUtil.returnSuccess();
        resp.put("adjustmentAmountTotal", orderAdjustmentTotalDifference);
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
    
    
    public static Map<String, Object>createCartFromOrder(DispatchContext dctx, Map<String, Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        Boolean skipInventoryChecks = (Boolean) context.get("skipInventoryChecks");
        Boolean skipProductChecks = (Boolean) context.get("skipProductChecks");
        boolean includePromoItems = Boolean.TRUE.equals(context.get("includePromoItems"));
        Locale locale = (Locale) context.get("locale");
        List<String> orderItemSequenceIds = (List)context.get("orderItemSequenceIds");
        
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
        } 
        catch (GenericEntityException e) 
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
        } 
        catch (CartItemModifyException e) 
        {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        List<GenericValue> orderItems = FastList.newInstance();
        for(String orderItemSeqId : orderItemSequenceIds)
        {
        	GenericValue orderItem = null;
			try {
				orderItem = delegator.findByPrimaryKey("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	orderItems.add(orderItem);
        }
        
        //List<GenericValue> orderItems = getValidOrderItems(orderItemSequenceIds, orh);
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

                // do not include PROMO items
                if (!includePromoItems && item.get("isPromo") != null && "Y".equals(item.getString("isPromo"))) 
                {
                    continue;
                }

                BigDecimal amount = item.getBigDecimal("selectedAmount");
                if (amount == null) {
                    amount = BigDecimal.ZERO;
                }
                BigDecimal quantity = item.getBigDecimal("quantity");
                
                if (quantity == null) {
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
                    } 
                    catch (CartItemModifyException e) 
                    {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(e.getMessage());
                    }
                } 
                else 
                {
                    // product item
                    String prodCatalogId = item.getString("prodCatalogId");
                    
                    try 
                    {
                        itemIndex = cart.addItemToEnd(productId, amount, quantity, unitPrice, null, null, null,null,null, null, null, prodCatalogId, null, item.getString("orderItemTypeId"), dispatcher, null, unitPrice == null ? null : false, skipInventoryChecks, skipProductChecks);
                    } 
                    catch (ItemNotFoundException e) 
                    {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(e.getMessage());
                    } 
                    catch (CartItemModifyException e) 
                    {
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
                } 
                catch (GenericEntityException e) 
                {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }
                
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

        List adjustments = orh.getOrderHeaderAdjustments();
        // If applyQuoteAdjustments is set to false then standard cart adjustments are used.
        if (!adjustments.isEmpty()) 
        {
            // The cart adjustments are added to the cart
            cart.getAdjustments().addAll(adjustments);
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("shoppingCart", cart);
        return result;
    }
}