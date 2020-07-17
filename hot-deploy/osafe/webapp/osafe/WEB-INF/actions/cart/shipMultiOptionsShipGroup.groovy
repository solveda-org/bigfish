package common;

import java.math.BigDecimal;
import java.util.List;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import com.osafe.util.Util;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.Delegator;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo.CartShipItemInfo;
import org.ofbiz.order.shoppingcart.shipping.ShippingEstimateWrapper;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;



ShoppingCart shoppingCart = session.getAttribute("shoppingCart");
CartShipInfo cartShipInfo = request.getAttribute("cartShipInfo");
ShoppingCartItem cartLine = request.getAttribute("cartLine");
shipGroupIndex = request.getAttribute("shipGroupIndex");
int iShipGroupIndex = Integer.valueOf(""+shipGroupIndex).intValue();
shipGroupLastIndex = request.getAttribute("shipGroupLastIndex");
lineIndex = request.getAttribute("lineIndex");

shippingEstWpr = new ShippingEstimateWrapper(dispatcher, shoppingCart,iShipGroupIndex);
context.shippingEstWpr = shippingEstWpr;
carrierShipmentMethodList = shippingEstWpr.getShippingMethods();
defaultShipMethodId = Util.getProductStoreParm(request, "CHECKOUT_CART_DEFAULT_SHIP_METHOD");


boolean removeShippingCostEst = false;
String inventoryMethod = Util.getProductStoreParm(request,"INVENTORY_METHOD");
if(UtilValidate.isNotEmpty(inventoryMethod) && inventoryMethod.equalsIgnoreCase("BIGFISH"))
{
    for(ShoppingCartItem  cartItem : shoppingCart.items())
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
    
    shipGroupAddress = shoppingCart.getShippingAddress(iShipGroupIndex);
    BigDecimal bdCartTotalWeight = shoppingCart.getShippableWeight(iShipGroupIndex);
    cartTotalWeight = bdCartTotalWeight.intValue();
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
			if (!UtilValidate.isNotPoBox(shipGroupAddress.get("address1")) || !UtilValidate.isNotPoBox(shipGroupAddress.get("address2")) || !UtilValidate.isNotPoBox(shipGroupAddress.get("address3")) )
			{
				isPoBoxAddr = true;
			}
			if ((UtilValidate.isNotEmpty(allowPoBoxAddr) && "N".equals(allowPoBoxAddr) && isPoBoxAddr)|| (cartTotalWeight != 0 && ((UtilValidate.isNotEmpty(maxWeight)&& maxWeight < cartTotalWeight) || (UtilValidate.isNotEmpty(minWeight) && cartTotalWeight < minWeight ))))
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
								Map<String, Object> customMethodContext = FastMap.newInstance();
								//add cart to context
								if (UtilValidate.isNotEmpty(shoppingCart))
								{
									customMethodContext.put("shoppingCart", shoppingCart);
								}
								
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




context.cartShipInfo=cartShipInfo;
context.shipGroupIndex=shipGroupIndex;
context.shipGroupLastIndex=shipGroupLastIndex;
context.carrierShipmentMethodList=carrierShipmentMethodList;
context.lineIndex = lineIndex;
context.defaultShipMethodId=defaultShipMethodId;
