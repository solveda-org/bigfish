package common;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.store.ProductStoreWorker;
import javolution.util.FastList;
import org.ofbiz.base.util.Debug

storeId = parameters.storeId;
shoppingCart = session.getAttribute("shoppingCart");
if (UtilValidate.isEmpty(storeId)) 
{
    if (UtilValidate.isNotEmpty(shoppingCart))
    {
        storeId = shoppingCart.getOrderAttribute("STORE_LOCATION");
        context.shoppingCart = shoppingCart;
		context.shoppingCartStoreId = storeId;
    }
} 
else 
{
    context.shoppingCart = session.getAttribute("shoppingCart");
}

if (UtilValidate.isEmpty(storeId)) 
{
    orderId = parameters.orderId;
    if (UtilValidate.isNotEmpty(orderId)) 
    {
        orderAttrPickupStore = delegator.findOne("OrderAttribute", ["orderId" : orderId, "attrName" : "STORE_LOCATION"], true);
        if (UtilValidate.isNotEmpty(orderAttrPickupStore)) 
        {
            storeId = orderAttrPickupStore.attrValue;
			context.shoppingCartStoreId = storeId;
        }
    }
}


//get a list of all the stores
productStore = ProductStoreWorker.getProductStore(request);
productStoreId=productStore.getString("productStoreId");
openStores = FastList.newInstance();
allStores = delegator.findByAndCache("ProductStoreRole", UtilMisc.toMap("productStoreId", productStoreId,"roleTypeId", "STORE_LOCATION"));
if (UtilValidate.isNotEmpty(allStores))
{
	for(GenericValue store : allStores)
	{
		storeParty = store.getRelatedOneCache("Party");
		if(UtilValidate.isNotEmpty(storeParty.statusId) && "PARTY_ENABLED".equals(storeParty.statusId))
		{
			openStores.add(storeParty);
		}
	}
}
if (UtilValidate.isNotEmpty(openStores))
{
	if(openStores.size() == 1)
	{
		oneStoreOpen = openStores.getAt(0);
		storeId = oneStoreOpen.partyId;
		context.oneStoreOpenStoreId = storeId;
	}
}



if (UtilValidate.isNotEmpty(storeId)) 
{
    party = delegator.findOne("Party", [partyId : storeId], true);
    if (UtilValidate.isNotEmpty(party))
    {
        partyGroup = party.getRelatedOneCache("PartyGroup");
        if (UtilValidate.isNotEmpty(partyGroup)) 
        {
            context.storeInfo = partyGroup;
        }

        partyContactMechPurpose = party.getRelatedCache("PartyContactMechPurpose");
        partyContactMechPurpose = EntityUtil.filterByDate(partyContactMechPurpose,true);

        partyGeneralLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "GENERAL_LOCATION"));
        partyGeneralLocations = EntityUtil.getRelatedCache("PartyContactMech", partyGeneralLocations);
        partyGeneralLocations = EntityUtil.filterByDate(partyGeneralLocations,true);
        partyGeneralLocations = EntityUtil.orderBy(partyGeneralLocations, UtilMisc.toList("fromDate DESC"));
        if (UtilValidate.isNotEmpty(partyGeneralLocations)) 
        {
        	partyGeneralLocation = EntityUtil.getFirst(partyGeneralLocations);
        	context.storeAddress = partyGeneralLocation.getRelatedOneCache("PostalAddress");
        }

        partyPrimaryPhones = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "PRIMARY_PHONE"));
        partyPrimaryPhones = EntityUtil.getRelatedCache("PartyContactMech", partyPrimaryPhones);
        partyPrimaryPhones = EntityUtil.filterByDate(partyPrimaryPhones,true);
        partyPrimaryPhones = EntityUtil.orderBy(partyPrimaryPhones, UtilMisc.toList("fromDate DESC"));
        if (UtilValidate.isNotEmpty(partyPrimaryPhones)) 
        {
        	partyPrimaryPhone = EntityUtil.getFirst(partyPrimaryPhones);
        	context.storePhone = partyPrimaryPhone.getRelatedOneCache("TelecomNumber");
        }
    }
}







