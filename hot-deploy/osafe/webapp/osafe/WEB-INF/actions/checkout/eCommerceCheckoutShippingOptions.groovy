package checkout;

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

cart = session.getAttribute("shoppingCart");
productStore = ProductStoreWorker.getProductStore(request);
party = null;
context.shoppingCart = cart;
context.userLogin = userLogin;
context.productStoreId = productStore.productStoreId;
context.productStore = productStore;


if (UtilValidate.isNotEmpty(cart)) 
{
	if (UtilValidate.isNotEmpty(userLogin)) 
	{
	    party = userLogin.getRelatedOneCache("Party");
	}

	shippingEstWpr = new ShippingEstimateWrapper(dispatcher, cart, 0);
	context.shippingEstWpr = shippingEstWpr;
	carrierShipmentMethodList = shippingEstWpr.getShippingMethods();

	boolean removeShippingCostEst = false;
	String inventoryMethod = Util.getProductStoreParm(request,"INVENTORY_METHOD");
	if(UtilValidate.isNotEmpty(inventoryMethod) && inventoryMethod.equalsIgnoreCase("BIGFISH"))
	{
        for(GenericValue cartItem : cart.items())
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

	//CHECK IF SHIPPING ADDRESS IS A PO BOX
    gvCartShippingAddress = cart.getShippingAddress();
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
				isPoBoxAddr = false;
				if (!UtilValidate.isNotPoBox(gvCartShippingAddress.get("address1")) || !UtilValidate.isNotPoBox(gvCartShippingAddress.get("address2")) || !UtilValidate.isNotPoBox(gvCartShippingAddress.get("address3")) )
				{
					isPoBoxAddr = true;
				}
				if (UtilValidate.isNotEmpty(allowPoBoxAddr) && "N".equals(allowPoBoxAddr) && isPoBoxAddr) {
	                returnShippingMethods.remove(method);
	                continue;
	            }
			}
	        carrierShipmentMethodList = returnShippingMethods;
		}
	}
	
	if (UtilValidate.isNotEmpty(carrierShipmentMethodList))
	{
	    context.carrierShipmentMethodList = carrierShipmentMethodList;
	}

	if (cart.getShipmentMethodTypeId() && cart.getCarrierPartyId()) 
	{
	    context.chosenShippingMethod = cart.getShipmentMethodTypeId() + '@' + cart.getCarrierPartyId();
	}
}

