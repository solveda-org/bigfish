package com.osafe.events;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;  
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.MessageString;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartPaymentInfo;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.order.shoppingcart.ShoppingCartHelper;
import org.apache.commons.lang.StringUtils;
import com.osafe.util.OsafeAdminUtil;

public class OsafeAdminCheckoutEvents {
	
	public static final String module = OsafeAdminCheckoutEvents.class.getName();
    public static final int rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");


	public static String addOrderAdjustment(HttpServletRequest request, HttpServletResponse response) 
    {
		HttpSession session = request.getSession();
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        ShoppingCart cart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        Map parameters = UtilHttp.getParameterMap(request);
        String orderAdjustmentTypeId = (String) parameters.get("orderAdjustmentTypeId");
        String description = (String) parameters.get("orderAdjustment_description");
        String amount = (String) parameters.get("orderAdjustment_amount");
        BigDecimal amountBD = BigDecimal.ZERO;
        if(UtilValidate.isNotEmpty(amount))
        {
        	amountBD = new BigDecimal(amount);
        }
        List orderAdjustmentList = (List) session.getAttribute("orderAdjustmentList");
        if(UtilValidate.isNotEmpty(cart))
        {
	    	if(UtilValidate.isEmpty(orderAdjustmentList))
	    	{
	    		orderAdjustmentList = FastList.newInstance();
	    	}
	    	
	    	//create orderAdjustment and apply to cart
	    	String orderAdjustmentId = "";
	    	GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment");
	        try 
	        {
	        	orderAdjustmentId = delegator.getNextSeqId("OrderAdjustment");
	        	orderAdjustment.set("orderAdjustmentId", orderAdjustmentId);
		        orderAdjustment.set("orderAdjustmentTypeId", orderAdjustmentTypeId);
		        orderAdjustment.set("description", description);
		        orderAdjustment.set("amount", amountBD);
		        orderAdjustment.create();
			} 
	        catch (GenericEntityException e) 
	        {
				Debug.logError(e, module);
			}
	        
	        if(UtilValidate.isNotEmpty(orderAdjustmentId))
	        {
	        	cart.addAdjustment(orderAdjustment);
	            
	            Map<String, Object>  orderAdjustmentInfoMap = FastMap.newInstance();
	            //create Order Adjustment Attributes to be saved to the order
	            orderAdjustmentInfoMap.put("ADJUST_ID", orderAdjustmentId);
	            orderAdjustmentInfoMap.put("ADJUST_TYPE", orderAdjustmentTypeId);
	            orderAdjustmentInfoMap.put("ADJUST_DESCRIPTION", description);
	            orderAdjustmentInfoMap.put("ADJUST_AMOUNT", amount);
	            
	            //add to session object
	            orderAdjustmentList.add(orderAdjustmentInfoMap);
	            session.setAttribute("orderAdjustmentList", orderAdjustmentList);
	        }
        }
        
        return "success";
    }
	
	public static String removeOrderAdjustment(HttpServletRequest request, HttpServletResponse response) 
    {
		HttpSession session = request.getSession();
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        ShoppingCart cart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        Map parameters = UtilHttp.getParameterMap(request);
        List orderAdjustmentList = (List) session.getAttribute("orderAdjustmentList");
        String orderAdjustmentId = (String) parameters.get("orderAdjustmentId");
        if(UtilValidate.isNotEmpty(cart) && cart.size() > 0)
        {
	        if(UtilValidate.isNotEmpty(orderAdjustmentId))
	    	{
	        	try 
	        	{
	                GenericValue orderAdjustment = delegator.findOne("OrderAdjustment", true, UtilMisc.toMap("orderAdjustmentId", orderAdjustmentId));
	                if(UtilValidate.isNotEmpty(orderAdjustment))
	                {
	                	orderAdjustment.remove();
	                }
	            } 
	        	catch (GenericEntityException e) 
	        	{
	                Debug.logError(e, module);
	            }
	        	
	        	if(UtilValidate.isNotEmpty(orderAdjustmentList))
		    	{
		        	int listIndxCount = 0;
		        	for(Object orderAdjustmentInfo : orderAdjustmentList)
			    	{
			    		Map orderAdjustmentInfoMap = (Map)orderAdjustmentInfo;
	    	    		String adjustmentId = (String) orderAdjustmentInfoMap.get("ADJUST_ID");
	    	    		if(UtilValidate.isNotEmpty(adjustmentId) && orderAdjustmentId.equalsIgnoreCase(adjustmentId))
			    		{
	    	    			orderAdjustmentList.remove(listIndxCount);
			    		}
	    	    		listIndxCount++;
			    	}
		    	}
		        
	    		List cartAdjustments = cart.getAdjustments();
	    		if(UtilValidate.isNotEmpty(cartAdjustments))
		    	{
	    			int cartAdjIndxCount = 0;
			    	for(Object cartAdjustmentObj : cartAdjustments)
			    	{
			    		GenericValue cartAdjustment = (GenericValue) cartAdjustmentObj;
			    		String cartAdjustmentId = cartAdjustment.getString("orderAdjustmentId");
			    		if(UtilValidate.isNotEmpty(cartAdjustmentId) && orderAdjustmentId.equalsIgnoreCase(cartAdjustmentId))
			    		{
			    			cart.removeAdjustment(cartAdjIndxCount);
			    		}
			    		cartAdjIndxCount++;
			    	}
		    	}	        	
	    	}	        
        }     
        return "success";
    }

	public static String addLoyaltyPoints(HttpServletRequest request, HttpServletResponse response) 
    {
    	HttpSession session = request.getSession();
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        ShoppingCart cart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        Map parameters = UtilHttp.getParameterMap(request);
        String productStoreId = ProductStoreWorker.getProductStoreId(request);
        String loyaltyPointsId = (String) parameters.get("loyaltyPointsId");
    	String checkoutLoyaltyMethod = OsafeAdminUtil.getProductStoreParm(request, "CHECKOUT_LOYALTY_METHOD");  
    	String checkoutLoyaltyConversion = OsafeAdminUtil.getProductStoreParm(request, "CHECKOUT_LOYALTY_CONVERSION"); 
    	BigDecimal checkoutLoyaltyConversionBD = BigDecimal.ZERO;
    	if (UtilValidate.isNotEmpty(checkoutLoyaltyConversion) && UtilValidate.isInteger(checkoutLoyaltyConversion)) 
        {
    		try 
            {
    			checkoutLoyaltyConversionBD = new BigDecimal(checkoutLoyaltyConversion);
            } 
            catch (NumberFormatException nfe) 
            {
            	Debug.logError(nfe, "Problems converting amount to BigDecimal", module);
            	checkoutLoyaltyConversionBD = BigDecimal.ONE;
            }
        }
    	BigDecimal totalLoyaltyPointsAmountBD = (BigDecimal) request.getAttribute("loyaltyPointsAmount");
    	
    	List orderAdjustmentAttributeList = (List) session.getAttribute("orderAdjustmentAttributeList");
    	if(UtilValidate.isEmpty(orderAdjustmentAttributeList))
    	{
    		orderAdjustmentAttributeList = FastList.newInstance();
    	}
    	else
    	{
    		for(Object orderAdjustmentAttributeInfo : orderAdjustmentAttributeList)
	    	{
	    		Map orderAdjustmentAttributeInfoMap = (Map)orderAdjustmentAttributeInfo;
	    		String adjustmentType = (String) orderAdjustmentAttributeInfoMap.get("ADJUST_TYPE");
	    		if(UtilValidate.isNotEmpty(adjustmentType) && "LOYALTY_POINTS".equalsIgnoreCase(adjustmentType))
	    		{
	    			return "success";
	    		}
	    	}
    	}
    	//retrieve loyalty points information
    	Map serviceContext = FastMap.newInstance();
    	serviceContext.put("loyaltyPointsId", loyaltyPointsId);
    	serviceContext.put("productStoreId", productStoreId);
    	Map loyaltyInfoMap = FastMap.newInstance();
    	try 
    	{            
    		loyaltyInfoMap = dispatcher.runSync("getLoyaltyPointsInfoMap", serviceContext);
        } 
    	catch (Exception e) 
    	{
            String errMsg = "Error when retriving loyalty points information :" + e.toString();
            Debug.logError(e, errMsg, module);
            return "error";
        }
    	
    	String expDate = "";
    	if(UtilValidate.isNotEmpty(loyaltyInfoMap))
    	{
    		expDate = (String) loyaltyInfoMap.get("expDate");
    	}
    	
    	//convert Total Loyalty Points Amount to Currency
    	serviceContext = FastMap.newInstance();
    	serviceContext.put("loyaltyPointsAmount", totalLoyaltyPointsAmountBD);
    	serviceContext.put("checkoutLoyaltyConversion", checkoutLoyaltyConversionBD);
    	Map loyaltyConversionMap = FastMap.newInstance();
    	try 
    	{            
    		loyaltyConversionMap = dispatcher.runSync("convertLoyaltyPoints", serviceContext);
        } 
    	catch (Exception e) 
    	{
            String errMsg = "Error when converting loyalty points to currency :" + e.toString();
            Debug.logError(e, errMsg, module);
            return "error";
        }
    	
    	BigDecimal totalLoyaltyPointsCurrencyBD = BigDecimal.ZERO;
    	if(UtilValidate.isNotEmpty(loyaltyConversionMap))
    	{
    		totalLoyaltyPointsCurrencyBD = (BigDecimal) loyaltyConversionMap.get("loyaltyPointsCurrency");
    	}
    	
    	//Add Loyalty Points Adjustment to the Cart and add orderAdjustmentAttributeList Object to session
    	if(UtilValidate.isNotEmpty(cart) && cart.size() > 0)
    	{
    		//initially set the applied values to the totals
    		BigDecimal loyaltyPointsCurrencyBD = totalLoyaltyPointsCurrencyBD;
    		BigDecimal loyaltyPointsAmountBD = totalLoyaltyPointsAmountBD;
    		//compare loyaltyPointsCurrency applied by Loyalty Points to the balance left on the order
    		BigDecimal cartGrandTotal = cart.getGrandTotal();
    		BigDecimal cartTotalTaxes = cart.getTotalSalesTax();
    		cartGrandTotal  = cartGrandTotal.subtract(cartTotalTaxes);
    		if ((loyaltyPointsCurrencyBD.compareTo(cartGrandTotal) > 0)) 
            {
    			//show warning mesage
    			session.setAttribute("showLoyaltyPointsAdjustedWarning", "Y");
    			request.setAttribute("showLoyaltyPointsAdjustedWarning", "Y");
    			loyaltyPointsCurrencyBD = cartGrandTotal;
    			
    			//convert Currency to Loyalty Points Amount
            	serviceContext = FastMap.newInstance();
            	serviceContext.put("loyaltyPointsCurrency", loyaltyPointsCurrencyBD);
            	serviceContext.put("checkoutLoyaltyConversion", checkoutLoyaltyConversionBD);
            	loyaltyConversionMap = FastMap.newInstance();
            	try 
            	{            
            		loyaltyConversionMap = dispatcher.runSync("convertCurrencyToLoyaltyPoints", serviceContext);
                } 
            	catch (Exception e) 
            	{
                    String errMsg = "Error when converting currency to loyalty points :" + e.toString();
                    Debug.logError(e, errMsg, module);
                    return "error";
                }
            	
            	if(UtilValidate.isNotEmpty(loyaltyConversionMap))
            	{
            		loyaltyPointsAmountBD = (BigDecimal) loyaltyConversionMap.get("loyaltyPointsAmount");
            	}
            }
    		else
    		{
    			//remove warning mesage if it exists
    			String showLoyaltyPointsAdjustedWarning = (String) session.getAttribute("showLoyaltyPointsAdjustedWarning");
    			if(UtilValidate.isNotEmpty(showLoyaltyPointsAdjustedWarning))
    			{
    				session.removeAttribute("showLoyaltyPointsAdjustedWarning");
    			}
    		}
    		
    		//negate the value so it can be applied to cart
    		BigDecimal loyaltyPointsCurrencyNegateBD = loyaltyPointsCurrencyBD.multiply(BigDecimal.valueOf(-1));
    		
    		//create orderAdjustment and apply to cart
    		GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment");
            orderAdjustment.set("orderAdjustmentTypeId", "LOYALTY_POINTS");
            orderAdjustment.set("amount", loyaltyPointsCurrencyNegateBD);
            orderAdjustment.set("includeInTax", "Y");
            int indexOfAdjInt = cart.addAdjustment(orderAdjustment);
            String indexOfAdj = String.valueOf(indexOfAdjInt);
            
            Map<String, Object>  orderAdjustmentAttributeInfoMap = FastMap.newInstance();
            //create Order Adjustment Attributes to be saved to the order
            orderAdjustmentAttributeInfoMap.put("INDEX", indexOfAdj);
            orderAdjustmentAttributeInfoMap.put("ADJUST_TYPE", "LOYALTY_POINTS");
            orderAdjustmentAttributeInfoMap.put("ADJUST_METHOD", checkoutLoyaltyMethod);
            orderAdjustmentAttributeInfoMap.put("MEMBER_ID", loyaltyPointsId);
            orderAdjustmentAttributeInfoMap.put("ADJUST_POINTS", loyaltyPointsAmountBD);
            if (UtilValidate.isNotEmpty(expDate)) 
            {
            	orderAdjustmentAttributeInfoMap.put("EXP_DATE", expDate);
            }
            orderAdjustmentAttributeInfoMap.put("CONVERSION_FACTOR", checkoutLoyaltyConversion);
            orderAdjustmentAttributeInfoMap.put("CURRENCY_AMOUNT", loyaltyPointsCurrencyBD.setScale(2, rounding));
            //these Adjustment Attributes will not be saved to Order, but are used for processing
            orderAdjustmentAttributeInfoMap.put("TOTAL_POINTS", totalLoyaltyPointsAmountBD);
            orderAdjustmentAttributeInfoMap.put("TOTAL_AMOUNT", totalLoyaltyPointsCurrencyBD);
            orderAdjustmentAttributeInfoMap.put("INCLUDE_IN_TAX", "Y");
            
            //add to session object
            orderAdjustmentAttributeList.add(orderAdjustmentAttributeInfoMap);
            session.setAttribute("orderAdjustmentAttributeList", orderAdjustmentAttributeList);  
    	}
    	//code to calculate remaining user points
    	
    	//Send this request variable to updateCartOnChange
    	String doCartLoyalty = "N";
    	session.setAttribute("DO_CART_LOYALTY", doCartLoyalty);
    	return "success";
    }
    
    public static String removeLoyaltyPoints(HttpServletRequest request, HttpServletResponse response) 
    {
    	Map parameters = UtilHttp.getParameterMap(request);
        HttpSession session = request.getSession();
    	List orderAdjustmentAttributeList = (List) session.getAttribute("orderAdjustmentAttributeList");
    	ShoppingCart cart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
    	if(UtilValidate.isNotEmpty(cart))
        {
    		int cartAdjIndexCount = 0;
    		List cartAdjustments = cart.getAdjustments();
	    	for(Object cartAdjustmentObj : cartAdjustments)
	    	{
	    		GenericValue cartAdjustment = (GenericValue) cartAdjustmentObj;
	    		String cartAdjustmentTypeId = cartAdjustment.getString("orderAdjustmentTypeId");
	    		if(UtilValidate.isNotEmpty(cartAdjustmentTypeId) && "LOYALTY_POINTS".equalsIgnoreCase(cartAdjustmentTypeId))
	    		{
	    			cart.removeAdjustment(cartAdjIndexCount);
	    		}
	    		cartAdjIndexCount++;
	    	}
        }
    	if(UtilValidate.isNotEmpty(orderAdjustmentAttributeList))
    	{
    		int listIndexCount = 0;
	    	for(Object orderAdjustmentAttributeInfo : orderAdjustmentAttributeList)
	    	{
	    		Map orderAdjustmentAttributeInfoMap = (Map)orderAdjustmentAttributeInfo;
	    		String orderAdjType = (String) orderAdjustmentAttributeInfoMap.get("ADJUST_TYPE");
	    		if(UtilValidate.isNotEmpty(orderAdjType) && "LOYALTY_POINTS".equalsIgnoreCase(orderAdjType))
	    		{
	    			orderAdjustmentAttributeList.remove(listIndexCount);
	            }
	    		listIndexCount++;
	    	}
    	}
    	//remove any warning messages that may be set
    	session.removeAttribute("showLoyaltyPointsAdjustedWarning");
    	//Send this request variable to updateCartOnChange
    	String doCartLoyalty = "N";
    	session.setAttribute("DO_CART_LOYALTY", doCartLoyalty);
    	return "success";
    } 
    public static String updateLoyaltyPoints(HttpServletRequest request, HttpServletResponse response) 
    {
    	Map parameters = UtilHttp.getParameterMap(request);
    	Locale locale = UtilHttp.getLocale(request);
        String loyaltyPointsAmountStr = (String) parameters.get("update_loyaltyPointsAmount");
        HttpSession session = request.getSession();
    	List orderAdjustmentAttributeList = (List) session.getAttribute("orderAdjustmentAttributeList");
    	ShoppingCart cart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	
    	BigDecimal loyaltyPointsAmountBD = new BigDecimal(loyaltyPointsAmountStr);

    	if(UtilValidate.isNotEmpty(cart))
        {
    		BigDecimal loyaltyPointsCurrencyBD = BigDecimal.ZERO;
    		for(Object orderAdjustmentAttributeInfo : orderAdjustmentAttributeList)
	    	{
	    		Map orderAdjustmentAttributeInfoMap = (Map)orderAdjustmentAttributeInfo;
	    		String orderAdjType = (String) orderAdjustmentAttributeInfoMap.get("ADJUST_TYPE");
	    		if(UtilValidate.isNotEmpty(orderAdjType) && "LOYALTY_POINTS".equalsIgnoreCase(orderAdjType))
	    		{
	    			String checkoutLoyaltyConversionStr = (String) orderAdjustmentAttributeInfoMap.get("CONVERSION_FACTOR");
	    			BigDecimal checkoutLoyaltyConversionBD = new BigDecimal(checkoutLoyaltyConversionStr);
	    			
	    			//convert Total Loyalty Points Amount to Currency
	    			Map serviceContext = FastMap.newInstance();
	    	    	serviceContext.put("loyaltyPointsAmount", loyaltyPointsAmountBD);
	    	    	serviceContext.put("checkoutLoyaltyConversion", checkoutLoyaltyConversionBD);
	    	    	Map loyaltyConversionMap = FastMap.newInstance();
	    	    	try 
	    	    	{            
	    	    		loyaltyConversionMap = dispatcher.runSync("convertLoyaltyPoints", serviceContext);
	    	        } 
	    	    	catch (Exception e) 
	    	    	{
	    	            String errMsg = "Error when converting loyalty points to currency :" + e.toString();
	    	            Debug.logError(e, errMsg, module);
	    	            return "error";
	    	        }
	    	    	
	    	    	if(UtilValidate.isNotEmpty(loyaltyConversionMap))
	    	    	{
	    	    		loyaltyPointsCurrencyBD = (BigDecimal) loyaltyConversionMap.get("loyaltyPointsCurrency");
	    	    	}
    	    		BigDecimal currentLoyaltyPointsCurrencyBD = (BigDecimal) orderAdjustmentAttributeInfoMap.get("CURRENCY_AMOUNT");
	    	    	BigDecimal cartGrandTotal = cart.getGrandTotal().add(currentLoyaltyPointsCurrencyBD);
	    	    	BigDecimal cartTotalTaxes = cart.getTotalSalesTax();
	        		cartGrandTotal  = cartGrandTotal.subtract(cartTotalTaxes);
	    	    	if(loyaltyPointsCurrencyBD.compareTo(cartGrandTotal) > 0) 
		            {
	    	    		//show warning mesage
	        			session.setAttribute("showLoyaltyPointsAdjustedWarning", "Y");
	        			request.setAttribute("showLoyaltyPointsAdjustedWarning", "Y");
		    			loyaltyPointsCurrencyBD = cartGrandTotal;
		    			
		    			//convert Currency to Loyalty Points Amount
		            	serviceContext = FastMap.newInstance();
		            	serviceContext.put("loyaltyPointsCurrency", loyaltyPointsCurrencyBD);
		            	serviceContext.put("checkoutLoyaltyConversion", checkoutLoyaltyConversionBD);
		            	loyaltyConversionMap = FastMap.newInstance();
		            	try 
		            	{            
		            		loyaltyConversionMap = dispatcher.runSync("convertCurrencyToLoyaltyPoints", serviceContext);
		                } 
		            	catch (Exception e) 
		            	{
		                    String errMsg = "Error when converting currency to loyalty points :" + e.toString();
		                    Debug.logError(e, errMsg, module);
		                    return "error";
		                }
		            	
		            	if(UtilValidate.isNotEmpty(loyaltyConversionMap))
		            	{
		            		loyaltyPointsAmountBD = (BigDecimal) loyaltyConversionMap.get("loyaltyPointsAmount");
		            	}
		            }
	    	    	else
	        		{
	        			//remove warning mesage if it exists
	        			String showLoyaltyPointsAdjustedWarning = (String) session.getAttribute("showLoyaltyPointsAdjustedWarning");
	        			if(UtilValidate.isNotEmpty(showLoyaltyPointsAdjustedWarning))
	        			{
	        				session.removeAttribute("showLoyaltyPointsAdjustedWarning");
	        			}
	        		}
	    	    	orderAdjustmentAttributeInfoMap.put("ADJUST_POINTS", loyaltyPointsAmountBD);
	    			orderAdjustmentAttributeInfoMap.put("CURRENCY_AMOUNT", loyaltyPointsCurrencyBD.setScale(2, rounding));
	    		}
	    	}
    		
    		if(UtilValidate.isNotEmpty(loyaltyPointsCurrencyBD))
    		{
    			List cartAdjustments = cart.getAdjustments();
    			BigDecimal loyaltyPointsCurrencyNegateBD = loyaltyPointsCurrencyBD.multiply(BigDecimal.valueOf(-1));
        		for(Object cartAdjustmentObj : cartAdjustments)
    	    	{
        			GenericValue cartAdjustment = (GenericValue) cartAdjustmentObj;
        			String cartAdjustmentTypeId = cartAdjustment.getString("orderAdjustmentTypeId");
        			if(UtilValidate.isNotEmpty(cartAdjustmentTypeId) && "LOYALTY_POINTS".equalsIgnoreCase(cartAdjustmentTypeId))
        			{
        				cartAdjustment.set("amount", loyaltyPointsCurrencyNegateBD);
        			}
    	    	}
    		}
        }
    	//Send this request variable to updateCartOnChange
    	String doCartLoyalty = "N";
    	session.setAttribute("DO_CART_LOYALTY", doCartLoyalty);
    	return "success";
    } 
    public static String modifyLoyaltyPoints(HttpServletRequest request, HttpServletResponse response) 
    {
    	Map parameters = UtilHttp.getParameterMap(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
    	List orderAdjustmentAttributeList = (List) session.getAttribute("orderAdjustmentAttributeList");
    	ShoppingCart cart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        
    	if(UtilValidate.isNotEmpty(cart) && cart.size() > 0)
        {
    		if(UtilValidate.isNotEmpty(orderAdjustmentAttributeList))
            {
	    		for(Object orderAdjustmentAttributeInfo : orderAdjustmentAttributeList)
		    	{
		    		Map orderAdjustmentAttributeInfoMap = (Map)orderAdjustmentAttributeInfo;
    	    		String adjustmentType = (String) orderAdjustmentAttributeInfoMap.get("ADJUST_TYPE");
    	    		if(UtilValidate.isNotEmpty(adjustmentType) && "LOYALTY_POINTS".equalsIgnoreCase(adjustmentType))
		    		{
    	    			String checkoutLoyaltyConversionStr = (String) orderAdjustmentAttributeInfoMap.get("CONVERSION_FACTOR");
    	    			BigDecimal checkoutLoyaltyConversionBD = new BigDecimal(checkoutLoyaltyConversionStr);
    	    			BigDecimal currentLoyaltyPointsCurrencyBD = (BigDecimal) orderAdjustmentAttributeInfoMap.get("CURRENCY_AMOUNT");
    	    	    	BigDecimal cartGrandTotal = cart.getGrandTotal().add(currentLoyaltyPointsCurrencyBD);
    	    	    	BigDecimal cartTotalTaxes = cart.getTotalSalesTax();
    	        		cartGrandTotal  = cartGrandTotal.subtract(cartTotalTaxes);
			    		if(currentLoyaltyPointsCurrencyBD.compareTo(cartGrandTotal) > 0) 
			            {
			    			//show warning mesage
			    			session.setAttribute("showLoyaltyPointsAdjustedWarning", "Y");
			    			request.setAttribute("showLoyaltyPointsAdjustedWarning", "Y");
			    			BigDecimal loyaltyPointsCurrencyBD = cartGrandTotal;
			    			String loyaltyPointsCurrencyStr = loyaltyPointsCurrencyBD.toString();
			    			
			    			//convert Currency to Loyalty Points Amount
			            	Map serviceContext = FastMap.newInstance();
			            	serviceContext.put("loyaltyPointsCurrency", loyaltyPointsCurrencyBD);
			            	serviceContext.put("checkoutLoyaltyConversion", checkoutLoyaltyConversionBD);
			            	Map loyaltyConversionMap = FastMap.newInstance();
			            	try 
			            	{            
			            		loyaltyConversionMap = dispatcher.runSync("convertCurrencyToLoyaltyPoints", serviceContext);
			                } 
			            	catch (Exception e) 
			            	{
			                    String errMsg = "Error when converting currency to loyalty points :" + e.toString();
			                    Debug.logError(e, errMsg, module);
			                    return "error";
			                }
			            	
			            	if(UtilValidate.isNotEmpty(loyaltyConversionMap))
			            	{
			            		BigDecimal loyaltyPointsAmountBD = (BigDecimal) loyaltyConversionMap.get("loyaltyPointsAmount");
			            		if (UtilValidate.isNotEmpty(loyaltyPointsAmountBD)) 
			                    {
			            			orderAdjustmentAttributeInfoMap.put("ADJUST_POINTS", loyaltyPointsAmountBD);
					            	orderAdjustmentAttributeInfoMap.put("CURRENCY_AMOUNT", loyaltyPointsCurrencyBD.setScale(2, rounding));
			            			
			            			List cartAdjustments = cart.getAdjustments();
			            			BigDecimal loyaltyPointsCurrencyNegateBD = loyaltyPointsCurrencyBD.multiply(BigDecimal.valueOf(-1));
			                		for(Object cartAdjustmentObj : cartAdjustments)
			            	    	{
			                			GenericValue cartAdjustment = (GenericValue) cartAdjustmentObj;
			                			String cartAdjustmentTypeId = cartAdjustment.getString("orderAdjustmentTypeId");
			                			if(UtilValidate.isNotEmpty(cartAdjustmentTypeId) && "LOYALTY_POINTS".equalsIgnoreCase(cartAdjustmentTypeId))
			                			{
			                				cartAdjustment.set("amount", loyaltyPointsCurrencyNegateBD.setScale(2, rounding));
			                			}
			            	    	}
			                    }
			            	}
			            }
			    		else
			    		{
			    			//remove warning mesage if it exists
			    			String showLoyaltyPointsAdjustedWarning = (String) session.getAttribute("showLoyaltyPointsAdjustedWarning");
			    			if(UtilValidate.isNotEmpty(showLoyaltyPointsAdjustedWarning))
			    			{
			    				session.removeAttribute("showLoyaltyPointsAdjustedWarning");
			    			}
			    		}
		    		}
		    	}
            }
        }
    	return "success";
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
    	    		BigDecimal loyaltyPointsAmountBD = (BigDecimal) orderAdjustmentAttributeInfoMap.get("ADJUST_POINTS");
    	    		String adjustMethod = (String) orderAdjustmentAttributeInfoMap.get("ADJUST_METHOD");
    	            String loyaltyPointsId = (String) orderAdjustmentAttributeInfoMap.get("MEMBER_ID");
    	            String checkoutLoyaltyConversion = (String) orderAdjustmentAttributeInfoMap.get("CONVERSION_FACTOR");
    	            String expDate = (String) orderAdjustmentAttributeInfoMap.get("EXP_DATE");
    	            BigDecimal currencyAmountBD = (BigDecimal) orderAdjustmentAttributeInfoMap.get("CURRENCY_AMOUNT");
    	            if (UtilValidate.isEmpty(loyaltyPointsAmountBD)) 
    	            {
    	            	loyaltyPointsAmountBD = BigDecimal.ZERO;
    	            }
    	            if (UtilValidate.isEmpty(currencyAmountBD)) 
    	            {
    	            	currencyAmountBD = BigDecimal.ZERO;
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
    		        		if (UtilValidate.isNotEmpty(loyaltyPointsAmountBD))
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
    		        		
    		        		if (UtilValidate.isNotEmpty(currencyAmountBD))
    		            	{
    		            		GenericValue orderAdjustmentAttr = delegator.makeValue("OrderAdjustmentAttribute");
    		            		orderAdjustmentAttr.set("orderAdjustmentId", orderAdjustmentId);
    		            		orderAdjustmentAttr.set("attrName", "ADJUST_POINTS");
    		            		orderAdjustmentAttr.set("attrValue", currencyAmountBD.toPlainString());
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
    
    
    public static String processPayment(HttpServletRequest request, HttpServletResponse response) 
    {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Locale locale = UtilHttp.getLocale(request);
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	Map parameters = UtilHttp.getParameterMap(request);
        String orderId = (String) parameters.get("orderId");
        String maxAmountStr = (String) request.getAttribute("maxAmount");
        BigDecimal maxAmount = BigDecimal.ZERO;
        String orderPaymentPreferenceId = (String) request.getAttribute("orderPaymentPreferenceId");
        Map serviceContext = FastMap.newInstance();
        Map<String, Object> serviceResult = null;
        List<MessageString> error_list = new ArrayList<MessageString>();
        MessageString errorString = new MessageString(UtilProperties.getMessage("OSafeAdminUiLabels", "AddPaymentCCProcessingError", locale),"maxAmount",true);
        error_list.add(errorString);
        GenericValue sysUserLogin = null;
        try 
    	{            
        	sysUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "system"));
        } 
    	catch (Exception e) 
    	{
            String errMsg = e.toString();
            Debug.logError(e, errMsg, module);
            request.setAttribute("_ERROR_MESSAGE_LIST_", error_list);
        	return "error";
        }
        
        if (UtilValidate.isNotEmpty(orderId))
    	{
	    	//CALL SERVICE TO AUTH PAYMENTS
	    	serviceContext.put("orderPaymentPreferenceId", orderPaymentPreferenceId);
	    	serviceContext.put("userLogin", sysUserLogin);
	    	try 
	    	{            
	    		serviceResult = dispatcher.runSync("authOrderPaymentPreference", serviceContext);
	        } 
	    	catch (Exception e) 
	    	{
	            String errMsg = "Error attempting to authenticate order payment preference :" + e.toString();
	            Debug.logError(e, errMsg, module);
	            request.setAttribute("_ERROR_MESSAGE_LIST_", error_list);
	            return "error";
	        }
	    	if (UtilValidate.isNotEmpty(serviceResult))
	    	{
	    		if (ServiceUtil.isError(serviceResult)) 
	            { 
	    			request.setAttribute("_ERROR_MESSAGE_LIST_", error_list);
	            	return "error";
	            }
	    	}
	    	
	    	serviceContext.clear();
	    	serviceResult.clear();
	    	//CALL SERVICE TO CAPTURE PAYMENTS
	    	serviceContext.put("orderId", orderId);
	    	if (UtilValidate.isNotEmpty(maxAmountStr))
	    	{
	    		maxAmount = new BigDecimal(maxAmountStr);
	    	}
	    	serviceContext.put("captureAmount", maxAmount);
	    	serviceContext.put("userLogin", sysUserLogin);
	    	try 
	    	{            
	    		serviceResult = dispatcher.runSync("captureOrderPayments", serviceContext);
	        } 
	    	catch (Exception e) 
	    	{
	            String errMsg = "Error attempting to authenticate order payment preference :" + e.toString();
	            Debug.logError(e, errMsg, module);
	            request.setAttribute("_ERROR_MESSAGE_LIST_", error_list);
	            return "error";
	        }
	    	String captureResp = "";
	    	if (UtilValidate.isNotEmpty(serviceResult))
	    	{
		    	if (ModelService.RESPOND_ERROR.equals(serviceResult.get(ModelService.RESPONSE_MESSAGE)))
	            {
	                captureResp = "ERROR";
	            }
	            else
	            {
	                captureResp = (String) serviceResult.get("processResult");
	            }
		    	if (captureResp.equals("FAILED") || captureResp.equals("ERROR"))
	            {
		    		request.setAttribute("_ERROR_MESSAGE_LIST_", error_list);
	            	return "error";
	            }
	    	}
    	}
        return "sucess";
    	
    }
    
}
