package com.osafe.services.bluedart;

import java.math.BigDecimal;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.order.shoppingcart.ShoppingCart;  

public class BlueDartServices 
{

    public static final String module = BlueDartServices.class.getName();

    public static Map<String, ?> checkDelivery(DispatchContext ctx, Map<String, ?> context) {
    	String pincode = (String)context.get("pincode");
    	Delegator delegator = ctx.getDelegator();
    	String deliveryAvailable = "N";
    	Map<String, Object> response = FastMap.newInstance();
    	if(UtilValidate.isNotEmpty(pincode))
    	{
    		GenericValue blueDartPrepaid = null;
			try 
			{
				blueDartPrepaid = delegator.findByPrimaryKeyCache("BlueDartPrepaid", UtilMisc.toMap("pincode",pincode));
			} 
			catch (GenericEntityException e) 
			{
				e.printStackTrace();
			}
    		if(UtilValidate.isNotEmpty(blueDartPrepaid))
    		{
    			deliveryAvailable = "Y";
    		}
    	}
    	else
    	{
    		Debug.logInfo("Pincode Not Found", module);
    	}
    	response.put("deliveryAvailable", deliveryAvailable);
    	return response;
    }
    
    public static Map<String, ?> checkCodLimit(DispatchContext ctx, Map<String, ?> context) {
    	String pincode = (String)context.get("pincode");
    	Delegator delegator = ctx.getDelegator();
    	BigDecimal codLimit = BigDecimal.ZERO;
    	Map<String, Object> response = FastMap.newInstance();
    	if(UtilValidate.isNotEmpty(pincode))
	    {
		    GenericValue blueDartCodpin = null;
		    try 
		    {
		    	blueDartCodpin = delegator.findByPrimaryKeyCache("BlueDartCodpin", UtilMisc.toMap("pincode",pincode));
		    }  
		    catch (GenericEntityException e) 
		    {
			    e.printStackTrace();
		    }
		    if(UtilValidate.isNotEmpty(blueDartCodpin))
		    {
		    	if(UtilValidate.isNotEmpty(blueDartCodpin.getString("blueDartlimit")))
		    	{
		    		try
		    		{
		    			codLimit = new BigDecimal(blueDartCodpin.getString("blueDartlimit"));
		    		}
		    	    catch (Exception e) 
		    	    {
		    	    	codLimit = BigDecimal.ZERO;
				    }
		    	}
		    }
	    }
    	else
    	{
    		Debug.logInfo("Pincode Not Found", module);
    	}
    	response.put("codLimit", codLimit);
    	return response;
    }
    
    public static Map<String, ?> blueDartCheckoutPincode(DispatchContext ctx, Map<String, ?> context) 
    {
    	//String pincode = (String)context.get("pincode");
    	String pincode = null;
    	ShoppingCart shoppingCart = (ShoppingCart)context.get("shoppingCart");
    	if(UtilValidate.isNotEmpty(shoppingCart))
	    {
    		GenericValue postalAddress = shoppingCart.getShippingAddress();
    		if(UtilValidate.isNotEmpty(postalAddress))
    	    {
    			pincode = (String)postalAddress.getString("postalCode");
    	    }
	    }
    	Map<String, Object> response = FastMap.newInstance();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	
    	String isAvailable = "N";
    	
    	if(UtilValidate.isNotEmpty(pincode))
	    {
    		Map<String, Object> svcCtx = FastMap.newInstance();
    		svcCtx.put("pincode", pincode);
    		//context.put("pincode", pincode);
    		Map<String, Object> processorResult = FastMap.newInstance();
    		try {
    			processorResult = dispatcher.runSync("checkDelivery", svcCtx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
            }
    		
    		if(UtilValidate.isNotEmpty(processorResult))
    	    {
    			isAvailable = (String)processorResult.get("deliveryAvailable");
    	    }
	    }
    	
    	response.put("isAvailable", isAvailable);
    	return response;
    }
}
