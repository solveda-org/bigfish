package common;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;

storeId = parameters.storeId;
if (UtilValidate.isEmpty(storeId)) 
{
    shoppingCart = session.getAttribute("shoppingCart");
    if (UtilValidate.isNotEmpty(shoppingCart))
    {
        storeId = shoppingCart.getOrderAttribute("STORE_LOCATION");
        context.shoppingCart = shoppingCart;
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
        }
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