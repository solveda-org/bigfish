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

cart = session.getAttribute("shoppingCart");
party = null;
if (UtilValidate.isNotEmpty(userLogin)) {
    party = userLogin.getRelatedOne("Party");
}

requestParams = UtilHttp.getParameterMap(request);
dummyContactMech = null;
dummyPostalAddress = null;
poatalCode = requestParams.get("postalCode") ?: request.getAttribute("postalCode");
if (UtilValidate.isNotEmpty(poatalCode)) {
    dummyContactMech = delegator.makeValue("ContactMech", [contactMechId:"dummyContactMechId", contactMechTypeId:"POSTAL_ADDRESS"]);
    dummyPostalAddress = delegator.makeValue("PostalAddress", [contactMechId : "dummyContactMechId", postalCode : poatalCode]);
    delegator.create(dummyContactMech);
    delegator.create(dummyPostalAddress);
    cart.setShippingContactMechId("dummyContactMechId");
}
productStore = ProductStoreWorker.getProductStore(request);

if (cart) {
    shippingEstWpr = new ShippingEstimateWrapper(dispatcher, cart, 0);
    context.shippingEstWpr = shippingEstWpr;
    carrierShipmentMethodList = shippingEstWpr.getShippingMethods();
}

boolean removeShippingCostEst = false;
String inventoryMethod = globalContext.get("INVENTORY_METHOD");
if(UtilValidate.isNotEmpty(inventoryMethod) && inventoryMethod.equalsIgnoreCase("BIGFISH"))
{
    if (cart) 
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
}
String removeShippingCostEstIdParm = globalContext.get("CHECKOUT_REMOVE_SHIP_COST_EST");

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

if(carrierShipmentMethodList)
{
    context.carrierShipmentMethodList = carrierShipmentMethodList;
}

context.shoppingCart = cart;
context.userLogin = userLogin;
context.productStoreId = productStore.productStoreId;
context.productStore = productStore;
if (UtilValidate.isNotEmpty(party)) {
    context.emailList = ContactHelper.getContactMechByType(party, "EMAIL_ADDRESS", false);
}
if (cart.getShipmentMethodTypeId() && cart.getCarrierPartyId()) {
    context.chosenShippingMethod = cart.getShipmentMethodTypeId() + '@' + cart.getCarrierPartyId();
}
if (UtilValidate.isNotEmpty(dummyContactMech) && UtilValidate.isNotEmpty(dummyPostalAddress)) {
    cart.setShippingContactMechId("");
    delegator.removeValue(dummyPostalAddress);
    delegator.removeValue(dummyContactMech);
}
