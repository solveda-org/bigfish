package checkout;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.order.shoppingcart.shipping.ShippingEstimateWrapper;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import com.osafe.util.Util;
import org.ofbiz.common.geo.GeoWorker;
import javolution.util.FastMap;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;

import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

cart = ShoppingCartEvents.getCartObject(request);
productStore = ProductStoreWorker.getProductStore(request);
party = null;
context.shoppingCart = cart;
context.userLogin = userLogin;
context.productStoreId = productStore.productStoreId;
context.productStore = productStore;
deliveryOption="";

if (UtilValidate.isNotEmpty(cart)) 
{
	if (UtilValidate.isNotEmpty(userLogin)) 
	{
	    party = userLogin.getRelatedOneCache("Party");
	}

	shippingEstWpr = new ShippingEstimateWrapper(dispatcher, cart, 0);
	context.shippingEstWpr = shippingEstWpr;
	carrierShipmentMethodList = shippingEstWpr.getShippingMethods();
	deliveryOption = cart.getOrderAttribute("DELIVERY_OPTION");

	boolean removeShippingCostEst = false;
	String inventoryMethod = Util.getProductStoreParm(request,"INVENTORY_METHOD");
	if(UtilValidate.isNotEmpty(inventoryMethod) && inventoryMethod.equalsIgnoreCase("BIGFISH"))
	{
        for(ShoppingCartItem cartItem : cart.items())
        {
            try {
                BigDecimal bfWareHouseInventoryBD = BigDecimal.ZERO;
                GenericValue bfWarehouseProductAttribute = delegator.findOne("ProductAttribute", UtilMisc.toMap("productId",cartItem.productId,"attrName","BF_INVENTORY_WHS"), true);
                
                if(UtilValidate.isNotEmpty(bfWarehouseProductAttribute))
                {
                    bfWareHouseInventory = bfWarehouseProductAttribute.attrValue;
                    bfWareHouseInventoryBD = new BigDecimal(bfWareHouseInventory);
                }
                if(bfWareHouseInventoryBD.compareTo(BigDecimal.ZERO) <= 0)
                {
                    removeShippingCostEst = true;
                    break;
                }
            } catch(Exception e) {
            }
        }
	}
	String removeShippingCostEstIdParm = Util.getProductStoreParm(request,"CHECKOUT_REMOVE_SHIP_COST_EST");

	if(UtilValidate.isNotEmpty(carrierShipmentMethodList))
	{
	    productStoreShipMethIdList = EntityUtil.getFieldListFromEntityList(carrierShipmentMethodList, "productStoreShipMethId", true);
	    
	    if(removeShippingCostEst && UtilValidate.isNotEmpty(removeShippingCostEstIdParm))
	    {
	        removeShippingCostEstIdList = StringUtil.split(removeShippingCostEstIdParm, ",");
	        
	        if(UtilValidate.isNotEmpty(productStoreShipMethIdList))
	        {
	            productStoreShipMethIdList.removeAll(removeShippingCostEstIdList);
	        }
	        carrierShipmentMethodList = EntityUtil.filterByAnd(carrierShipmentMethodList, [EntityCondition.makeCondition("productStoreShipMethId", EntityOperator.IN, productStoreShipMethIdList)]);
	    }
	}

	//Generic Custom Method Variable Context
	Map<String, Object> customMethodContext = FastMap.newInstance();
	//add cart to context
	if (UtilValidate.isNotEmpty(cart))
	{
		customMethodContext.put("shoppingCart", cart);
	}
	
	//CHECK IF SHIPPING ADDRESS IS A PO BOX
    gvCartShippingAddress = cart.getShippingAddress();
    BigDecimal bdCartTotalWeight = cart.getShippableWeight(0);
    gvCartTotalWeight = bdCartTotalWeight.intValue();
	if (UtilValidate.isNotEmpty(gvCartShippingAddress))
	{
		if(UtilValidate.isNotEmpty(carrierShipmentMethodList))
		{
			// clone the list for concurrent modification
	        returnShippingMethods = UtilMisc.makeListWritable(carrierShipmentMethodList);
	        for (GenericValue method: carrierShipmentMethodList)
			{
	        	psShipmentMeth = delegator.findByPrimaryKeyCache("ProductStoreShipmentMeth", [productStoreShipMethId : method.productStoreShipMethId]);
				allowPoBoxAddr = psShipmentMeth.getString("allowPoBoxAddr");
				minWeight = psShipmentMeth.getBigDecimal("minWeight");
				maxWeight = psShipmentMeth.getBigDecimal("maxWeight");
				isPoBoxAddr = false;
				if (!UtilValidate.isNotPoBox(gvCartShippingAddress.get("address1")) || !UtilValidate.isNotPoBox(gvCartShippingAddress.get("address2")) || !UtilValidate.isNotPoBox(gvCartShippingAddress.get("address3")) )
				{
					isPoBoxAddr = true;
				}
				if ((UtilValidate.isNotEmpty(allowPoBoxAddr) && "N".equals(allowPoBoxAddr) && isPoBoxAddr)|| (gvCartTotalWeight != 0 && ((UtilValidate.isNotEmpty(maxWeight)&& maxWeight < gvCartTotalWeight) || (UtilValidate.isNotEmpty(minWeight) && gvCartTotalWeight < minWeight ))))
				{
	                returnShippingMethods.remove(method);
	                continue;
	            }
				//Check shipment CUSTOM METHOD
				if(UtilValidate.isNotEmpty(psShipmentMeth))
				{
					shipmentCustomMethodId = psShipmentMeth.getString("shipmentCustomMethodId");
					if(UtilValidate.isNotEmpty(shipmentCustomMethodId))
					{
						//get the shipment CUSTOM METHOD
						shipmentCustomMeth = delegator.findByPrimaryKeyCache("CustomMethod", [customMethodId : shipmentCustomMethodId]);
						if(UtilValidate.isNotEmpty(shipmentCustomMeth))
						{
							customMethodName = shipmentCustomMeth.customMethodName;
							if(UtilValidate.isNotEmpty(customMethodName))
							{
								//run the custom method
								processorResult = null;
								try {
									processorResult = dispatcher.runSync(customMethodName, customMethodContext);
								} catch (GenericServiceException e)
								{
									//Debug.logError(e, module);
								}
								
								isAvailable = "N";
								if(UtilValidate.isNotEmpty(processorResult))
								{
									isAvailable = processorResult.get("isAvailable");
								}
								
								//if shipping option is not available for this customer then remove from the displayed list
								if("N".equals(isAvailable))
								{
									returnShippingMethods.remove(method);
								}
							}
						}
					}
				}
				
			}
	        carrierShipmentMethodList = returnShippingMethods;
		}
	}
	
	if (UtilValidate.isNotEmpty(carrierShipmentMethodList))
	{
	    context.carrierShipmentMethodList = carrierShipmentMethodList;
	}

	if (UtilValidate.isNotEmpty(cart.getShipmentMethodTypeId()) && UtilValidate.isNotEmpty(cart.getCarrierPartyId())) 
	{
	    context.chosenShippingMethod = cart.getShipmentMethodTypeId() + '@' + cart.getCarrierPartyId();
	}
	
    context.deliveryOption = deliveryOption;
}

