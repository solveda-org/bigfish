package com.osafe.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.osafe.util.Util;

public class InventoryServices {

    public static final String module = OrderServices.class.getName();

    public static Map<String, Object> getProductInventoryLevel(String productId, ServletRequest request) {
    	Delegator delegator = (Delegator)request.getAttribute("delegator");
    	Map<String, Object> ineventoryLevelMap = new HashMap<String, Object>();
    	
		try {
			List<GenericValue> inventoryProductAttributes =  delegator.findByAndCache("ProductAttribute", UtilMisc.toMap("productId",productId));
			ineventoryLevelMap = getProductInventoryLevel(inventoryProductAttributes,request);
			
		} catch (GenericEntityException ge) {
		    Debug.logError(ge, ge.getMessage(), module);
		}
    	return ineventoryLevelMap;
    }
    
    public static Map<String, Object> getProductInventoryLevel(List<GenericValue> productAttributes, ServletRequest request) {
    	String productStoreId = ProductStoreWorker.getProductStoreId(request);
    	Delegator delegator = (Delegator)request.getAttribute("delegator");
    	Map<String, Object> ineventoryLevelMap = new HashMap<String, Object>();
    	//get the Inventory Product Store Parameters
    	String inventoryMethod = Util.getProductStoreParm(productStoreId, "INVENTORY_METHOD");
    	
    	BigDecimal inventoryInStockFrom = new BigDecimal("-1");
    	BigDecimal inventoryOutOfStockTo = new BigDecimal("-1");
    	BigDecimal inventoryLevel = BigDecimal.ZERO;
    	BigDecimal inventoryWarehouseLevel = BigDecimal.ZERO;
    	
    	if(inventoryMethod != null && inventoryMethod.equalsIgnoreCase("BIGFISH"))
    	{
    	    GenericValue totalInventoryProductAttribute = null;
    	    GenericValue whInventoryProductAttribute = null;
    	    
			if(UtilValidate.isNotEmpty(productAttributes)) {
			    List<GenericValue> totalInventoryProductAttributes = EntityUtil.filterByAnd(productAttributes, UtilMisc.toMap("attrName", "BF_INVENTORY_TOT"));
			    totalInventoryProductAttribute = EntityUtil.getFirst(totalInventoryProductAttributes);
			    
			    List<GenericValue> whInventoryProductAttributes = EntityUtil.filterByAnd(productAttributes, UtilMisc.toMap("attrName", "BF_INVENTORY_WHS"));
			    whInventoryProductAttribute = EntityUtil.getFirst(whInventoryProductAttributes);
			}
    		if(totalInventoryProductAttribute!=null)
    		{
    		    String bigfishInventory = (String)totalInventoryProductAttribute.get("attrValue");
    			try {
    			    inventoryLevel = new BigDecimal(bigfishInventory);
    			} catch (Exception e) {
    			    inventoryLevel = BigDecimal.ZERO;
				}
    		}
    		
    		if(whInventoryProductAttribute!=null)
    		{
    		    String bigfishWHInventory = (String)whInventoryProductAttribute.get("attrValue");
    			try {
    				inventoryWarehouseLevel = new BigDecimal(bigfishWHInventory);
    			} catch (Exception e) {
    				inventoryWarehouseLevel = BigDecimal.ZERO;
				}
    		}
    		
    		String inventoryInStockFromStr = Util.getProductStoreParm(productStoreId, "INVENTORY_IN_STOCK_FROM");
    	    String inventoryOutOfStockToStr = Util.getProductStoreParm(productStoreId, "INVENTORY_OUT_OF_STOCK_TO");
    	    try {
    	        inventoryInStockFrom = new BigDecimal(inventoryInStockFromStr);
    	    } catch (Exception e) {
    	        inventoryInStockFrom = new BigDecimal("-1");
    		}
    	    	
    	    try {
    	        inventoryOutOfStockTo = new BigDecimal(inventoryOutOfStockToStr);
    	    } catch (Exception e) {
    	        inventoryOutOfStockTo = new BigDecimal("-1");
    		}
    	} 
    	ineventoryLevelMap.put("inventoryLevel", inventoryLevel);
    	ineventoryLevelMap.put("inventoryWarehouseLevel", inventoryWarehouseLevel);
    	ineventoryLevelMap.put("inventoryLevelInStockFrom", inventoryInStockFrom);
    	ineventoryLevelMap.put("inventoryLevelOutOfStockTo", inventoryOutOfStockTo);
    	return ineventoryLevelMap;
    }
    
    
    public static void setProductInventoryLevel(String productId, String productStoreId, BigDecimal quantity, String deliveryOption) {
    	
    	Delegator delegator = DelegatorFactory.getDelegator(null);
    	LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(null, delegator);
    	
    	String inventoryMethod = Util.getProductStoreParm(productStoreId, "INVENTORY_METHOD");
    	GenericValue userLogin = null;
		try {
			userLogin = delegator.findByPrimaryKeyCache("UserLogin",UtilMisc.toMap("userLoginId", "system"));
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		}
    	if(inventoryMethod != null && inventoryMethod.equalsIgnoreCase("BIGFISH"))
    	{
    		GenericValue totalInventoryProductAttribute = null;
    	    GenericValue whInventoryProductAttribute = null;
    	    BigDecimal currentInventoryLevel = BigDecimal.ZERO;
    	    BigDecimal currentInventoryWarehouseLevel = BigDecimal.ZERO;
    	    
    	    BigDecimal newInventoryLevel = BigDecimal.ZERO;
    	    BigDecimal newInventoryWarehouseLevel = BigDecimal.ZERO;
    	    
    		try {
    			List<GenericValue> inventoryProductAttributes =  delegator.findByAndCache("ProductAttribute", UtilMisc.toMap("productId",productId));
    			if(UtilValidate.isNotEmpty(inventoryProductAttributes)) {
    			    List<GenericValue> totalInventoryProductAttributes = EntityUtil.filterByAnd(inventoryProductAttributes, UtilMisc.toMap("attrName", "BF_INVENTORY_TOT"));
    			    totalInventoryProductAttribute = EntityUtil.getFirst(totalInventoryProductAttributes);
    			    
    			    List<GenericValue> whInventoryProductAttributes = EntityUtil.filterByAnd(inventoryProductAttributes, UtilMisc.toMap("attrName", "BF_INVENTORY_WHS"));
    			    whInventoryProductAttribute = EntityUtil.getFirst(whInventoryProductAttributes);
    			}
    		} catch (GenericEntityException ge) {
    		    Debug.logError(ge, ge.getMessage(), module);
			}
    		
    		//Get the current inventory level attributes from Product Attribute
    		
    		if(totalInventoryProductAttribute != null)
    		{
    		    String bigfishInventory = (String)totalInventoryProductAttribute.get("attrValue");
    			try {
    			    currentInventoryLevel = new BigDecimal(bigfishInventory);
    			} catch (Exception nfe) {
    			    currentInventoryLevel = BigDecimal.ZERO;
				}
    		}
    		
    		
    		if(whInventoryProductAttribute != null)
    		{
    		    String bigfishWHInventory = (String)whInventoryProductAttribute.get("attrValue");
    			try {
    			    currentInventoryWarehouseLevel = new BigDecimal(bigfishWHInventory);
    			} catch (Exception nfe) {
    			    currentInventoryWarehouseLevel = BigDecimal.ZERO;
				}
    		}
    		
    		//Calculate the new inventory levels after Increase or Decrease the quantity with the current inventory levels
    		if(UtilValidate.isNotEmpty(quantity)) 
    		{	
    			newInventoryLevel = currentInventoryLevel.subtract(quantity);
    			newInventoryWarehouseLevel = currentInventoryWarehouseLevel.subtract(quantity);
    		}
    		//Update the Inventory Level Attributes to Product Attribute
    		Map updateProductAttributeCtx = FastMap.newInstance();
    		Map createProductAttributeCtx = FastMap.newInstance();
            try {
            	if(totalInventoryProductAttribute != null) {
        		    updateProductAttributeCtx.put("userLogin",userLogin);
        		    updateProductAttributeCtx.put("productId",productId);
        		    updateProductAttributeCtx.put("attrName","BF_INVENTORY_TOT");
        		    updateProductAttributeCtx.put("attrValue",Integer.valueOf(newInventoryLevel.intValue()).toString());
        		    dispatcher.runSync("updateProductAttribute", updateProductAttributeCtx);
        		}
            	else
            	{
            		createProductAttributeCtx.put("userLogin",userLogin);
        			createProductAttributeCtx.put("productId",productId);
        			createProductAttributeCtx.put("attrName","BF_INVENTORY_TOT");
        			createProductAttributeCtx.put("attrValue",Integer.valueOf(newInventoryLevel.intValue()).toString());
        			dispatcher.runSync("createProductAttribute", createProductAttributeCtx);
            	}
                
            } catch (GenericServiceException e) {
                Debug.logWarning(e, module);
            }
            
            if(UtilValidate.isNotEmpty(deliveryOption) && (deliveryOption.equals("SHIP_TO") || deliveryOption.equals("SHIP_TO_MULTI"))) {
        		
                try {
                	if(whInventoryProductAttribute != null) {
                		updateProductAttributeCtx = UtilMisc.toMap("userLogin",userLogin);
                		updateProductAttributeCtx.put("productId",productId);
                		updateProductAttributeCtx.put("attrName","BF_INVENTORY_WHS");
                		updateProductAttributeCtx.put("attrValue",Integer.valueOf(newInventoryWarehouseLevel.intValue()).toString());
                		dispatcher.runSync("updateProductAttribute", updateProductAttributeCtx);
                	}
                	else
                	{
                		createProductAttributeCtx = UtilMisc.toMap("userLogin",userLogin);
            			createProductAttributeCtx.put("productId",productId);
            			createProductAttributeCtx.put("attrName","BF_INVENTORY_WHS");
            			createProductAttributeCtx.put("attrValue",Integer.valueOf(newInventoryWarehouseLevel.intValue()).toString());
            			dispatcher.runSync("createProductAttribute", createProductAttributeCtx);
                	}
                    
                } catch (GenericServiceException e) {
                    Debug.logWarning(e, module);
                }
            }
    	}
    }
}
