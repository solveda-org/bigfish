package com.osafe.services;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import com.osafe.util.Util;

public class InventoryServices {

    public static final String module = OrderServices.class.getName();

    public static Map<String, Integer> getProductInventoryLevel(String productId, ServletRequest request) {
    	String productStoreId = ProductStoreWorker.getProductStoreId(request);
    	Delegator delegator = (Delegator)request.getAttribute("delegator");
    	Map<String, Integer> ineventoryLevelMap = new HashMap<String, Integer>();
    	//get the Inventory Product Store Parameters
    	String inventoryMethod = Util.getProductStoreParm(productStoreId, "INVENTORY_METHOD");
    	
    	Integer inventoryInStockFrom = -1;
    	Integer inventoryOutOfStockTo = -1;
    	Integer inventoryLevel = 0;
    	
    	if(inventoryMethod != null && inventoryMethod.equalsIgnoreCase("BIGFISH"))
    	{
    	    GenericValue productAttribute = null;
    		try {
    		    productAttribute = delegator.findOne("ProductAttribute", UtilMisc.toMap("productId",productId,"attrName","BIGFISH_INVENTORY"), true);
    		} catch (GenericEntityException ge) {
    		    Debug.logError(ge, ge.getMessage(), module);
			}
    		if(productAttribute!=null)
    		{
    		    String bigfishInventory = (String)productAttribute.get("attrValue");
    			try {
    			    inventoryLevel = Integer.parseInt(bigfishInventory);
    			} catch (NumberFormatException nfe) {
    			    inventoryLevel = 0;
				}
    		}
    		    
    		String inventoryInStockFromStr = Util.getProductStoreParm(productStoreId, "INVENTORY_IN_STOCK_FROM");
    	    String inventoryOutOfStockToStr = Util.getProductStoreParm(productStoreId, "INVENTORY_OUT_OF_STOCK_TO");
    	    try {
    	        inventoryInStockFrom = Integer.parseInt(inventoryInStockFromStr);
    	    } catch (NumberFormatException nfe) {
    	        inventoryInStockFrom = -1;
    		}
    	    	
    	    try {
    	        inventoryOutOfStockTo = Integer.parseInt(inventoryOutOfStockToStr);
    	    } catch (NumberFormatException nfe) {
    	        inventoryOutOfStockTo = -1;
    		}
    	} 
    	ineventoryLevelMap.put("inventoryLevel", inventoryLevel);
    	ineventoryLevelMap.put("inventoryLevelInStockFrom", inventoryInStockFrom);
    	ineventoryLevelMap.put("inventoryLevelOutOfStockTo", inventoryOutOfStockTo);
    	return ineventoryLevelMap;
    }
}
