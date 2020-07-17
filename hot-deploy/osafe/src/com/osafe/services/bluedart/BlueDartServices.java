package com.osafe.services.bluedart;

import java.math.BigDecimal;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class BlueDartServices 
{

    public static final String module = BlueDartServices.class.getName();

    public static String getBlueDartDeliveryAvailablity(String pincode) 
    {
    	Delegator delegator = DelegatorFactory.getDelegator(null);
    	String deliveryAvailable = "N";
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
    	return deliveryAvailable; 
    }
    
    
    public static BigDecimal getBlueDartCODLimit(String pincode) 
    {
    	Delegator delegator = DelegatorFactory.getDelegator(null);
	    BigDecimal codLimit = BigDecimal.ZERO;
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
	    return codLimit;
	}
}
