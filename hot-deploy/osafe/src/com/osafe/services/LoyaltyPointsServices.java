package com.osafe.services;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import com.osafe.util.Util;  
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import javolution.util.FastMap;
import javolution.util.FastList;
import java.util.List;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class LoyaltyPointsServices {

    public static String module = LoyaltyPointsServices.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";
   
    /** Service to verify Loyalty Points Id is valid. */
    public static Map<String, Object> validateLoyaltyMember(DispatchContext dctx, Map<String, ?> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> responseMap = ServiceUtil.returnSuccess();
        String loyaltyPointsId = (String) context.get("loyaltyPointsId");
        String productStoreId = (String) context.get("productStoreId");
        String checkoutLoyaltyMethod = Util.getProductStoreParm(productStoreId, "CHECKOUT_LOYALTY_METHOD");
        String isValid = "N";
        
        if (UtilValidate.isEmpty(loyaltyPointsId)) 
        {
        	responseMap.put("isValid",isValid);
        	return responseMap;
        }
        else
        {
        	if (UtilValidate.isNotEmpty(checkoutLoyaltyMethod)) 
            {
	        	//list supported LOYALTY METHODS
	        	if("TEST".equalsIgnoreCase(checkoutLoyaltyMethod))
	        	{
	        		//test method will always be valid as long as user enters loyaltyPointsId
	        		isValid = "Y";
	        		responseMap.put("isValid",isValid);
	            	return responseMap;
	        	}
	        	//add code for other methods
            }
        }
        responseMap.put("isValid",isValid);
        return responseMap;
    }

    /** Service to get the amount of Loyalty Points for a user. */
    public static Map<String, Object> getLoyaltyPointsInfoMap(DispatchContext dctx, Map<String, ?> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> loyaltyInfoMap = ServiceUtil.returnSuccess();
    	String loyaltyPointsId = (String) context.get("loyaltyPointsId");
    	String productStoreId = (String) context.get("productStoreId");
    	String checkoutLoyaltyMethod = Util.getProductStoreParm(productStoreId, "CHECKOUT_LOYALTY_METHOD");
    	String prefDateFormat = Util.getProductStoreParm(productStoreId, "FORMAT_DATE");
        
        if (UtilValidate.isEmpty(loyaltyPointsId)) 
        {
        	loyaltyInfoMap.put("loyaltyPointsAmount", BigDecimal.ZERO);
        	return loyaltyInfoMap;
        }
        else
        {
        	if (UtilValidate.isNotEmpty(checkoutLoyaltyMethod)) 
            {
	        	//list supported LOYALTY METHODS
	        	if("TEST".equalsIgnoreCase(checkoutLoyaltyMethod))
	        	{
	        		//test method will return value passed to the loyaltyPointsId
	        		BigDecimal loyaltyPointsIdBD = BigDecimal.ZERO;
	        		if(Util.isNumber(loyaltyPointsId))
	        		{
	        			loyaltyPointsIdBD = new BigDecimal(loyaltyPointsId);
	        		}
	        		loyaltyInfoMap.put("loyaltyPointsAmount", loyaltyPointsIdBD);
	        		GregorianCalendar gcStart = new GregorianCalendar();
                    gcStart.setTimeInMillis(UtilDateTime.nowTimestamp().getTime());
                    gcStart.add(Calendar.MONTH, 1);
                    Timestamp now = new Timestamp(gcStart.getTimeInMillis());
            		String newExpDate = Util.convertDateTimeFormat(now, prefDateFormat);
            		loyaltyInfoMap.put("expDate", newExpDate);
            		return loyaltyInfoMap;
	        	}
	        	//add code for other methods
            }
        }
        loyaltyInfoMap.put("loyaltyPointsAmount", BigDecimal.ZERO);
        return loyaltyInfoMap;
    }
 
    /** Service to convert Loyalty Points to currency. */
    public static Map<String, Object> convertLoyaltyPoints(DispatchContext dctx, Map<String, ?> context) 
    {
    	Map<String, Object> responseMap = ServiceUtil.returnSuccess();
    	BigDecimal loyaltyPointsAmount = (BigDecimal) context.get("loyaltyPointsAmount");
    	BigDecimal checkoutLoyaltyConversion = (BigDecimal) context.get("checkoutLoyaltyConversion");
    	
    	if (UtilValidate.isEmpty(loyaltyPointsAmount)) 
        {
        	responseMap.put("loyaltyPointsCurrency", BigDecimal.ZERO);
            return responseMap;
        }
    	else if(UtilValidate.isEmpty(checkoutLoyaltyConversion)) 
        {
    		checkoutLoyaltyConversion = BigDecimal.ONE;
        }
    	
    	try 
		{
    		int roundingMode = BigDecimal.ROUND_FLOOR;
        	responseMap.put("loyaltyPointsCurrency",loyaltyPointsAmount.divide(checkoutLoyaltyConversion, roundingMode));
		} 
		catch (ArithmeticException ae) 
		{
			// TODO: handle exception
		}
        return responseMap;
    }
    
    /** Service to convert currency to Loyalty Points. */
    public static Map<String, Object> convertCurrencyToLoyaltyPoints(DispatchContext dctx, Map<String, ?> context) 
    {
    	Map<String, Object> responseMap = ServiceUtil.returnSuccess();
    	BigDecimal loyaltyPointsCurrency = (BigDecimal) context.get("loyaltyPointsCurrency");
    	BigDecimal checkoutLoyaltyConversion = (BigDecimal) context.get("checkoutLoyaltyConversion");
    	
    	if (UtilValidate.isEmpty(loyaltyPointsCurrency)) 
        {
        	responseMap.put("loyaltyPointsCurrency", BigDecimal.ZERO);
            return responseMap;
        }
    	else if(UtilValidate.isEmpty(checkoutLoyaltyConversion)) 
        {
    		checkoutLoyaltyConversion = BigDecimal.ONE;
        }
    	responseMap.put("loyaltyPointsAmount",loyaltyPointsCurrency.multiply(checkoutLoyaltyConversion));
        return responseMap;
    }

    /** Service to reduce the amount of points in a users account after completing an order. */
    public static Map<String, Object> redeemLoyaltyPoints(DispatchContext dctx, Map<String, ?> context) 
    {
    	Delegator delegator = dctx.getDelegator();
    	Map<String, Object> responseMap = ServiceUtil.returnSuccess();
    	String orderId = (String) context.get("orderId");
    	List orderAdjustmentAttributeList = (List) context.get("orderAdjustmentAttributeList");
    	List orderAdjustmentList = FastList.newInstance();
    	
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
    	}
    	
    	if (UtilValidate.isNotEmpty(orderAdjustmentList))
    	{
    		for(Object orderAdjustmentOb : orderAdjustmentList)
	    	{
    			GenericValue orderAdjustment = (GenericValue) orderAdjustmentOb;
	    		if("LOYALTY_POINTS".equalsIgnoreCase(orderAdjustment.getString("orderAdjustmentTypeId")))
	    		{
	    			String orderAdjustmentId = orderAdjustment.getString("orderAdjustmentId");
	    			for(Object orderAdjustmentAttributeInfo : orderAdjustmentAttributeList)
	    	    	{
	    				Map orderAdjustmentAttributeInfoMap = (Map)orderAdjustmentAttributeInfo;
	    				String attrListOrderAdjustmentId = (String) orderAdjustmentAttributeInfoMap.get("ORDER_ADJUSTMENT_ID");
	    				if(orderAdjustmentId.equalsIgnoreCase(attrListOrderAdjustmentId))
	    	    		{
	    					BigDecimal loyaltyPointsAmount = (BigDecimal) orderAdjustmentAttributeInfoMap.get("ADJUST_POINTS");
	    		    		String adjustMethod = (String) orderAdjustmentAttributeInfoMap.get("ADJUST_METHOD");
	    		            String loyaltyPointsId = (String) orderAdjustmentAttributeInfoMap.get("MEMBER_ID");
	    		            String checkoutLoyaltyConversion = (String) orderAdjustmentAttributeInfoMap.get("CONVERSION_FACTOR");
	    		            String expDate = (String) orderAdjustmentAttributeInfoMap.get("EXP_DATE");
	    		            BigDecimal currencyAmount = (BigDecimal) orderAdjustmentAttributeInfoMap.get("CURRENCY_AMOUNT");
	    		            
	    		            if (UtilValidate.isNotEmpty(adjustMethod))
	    		        	{
	    		            	if("TEST".equalsIgnoreCase(adjustMethod))
	    			    		{
	    		            		//call some service to reduce member points
	    		            		System.out.println("###### TEST method has successfully redeemed Loyalty Points ######");
		    		        	}
	    		            	//TODO: add other methods to redeem points
	    		        	}
	    	    		}
	    	    	}
	    		}
	    	}
    	}
    	 return responseMap;
    }
}