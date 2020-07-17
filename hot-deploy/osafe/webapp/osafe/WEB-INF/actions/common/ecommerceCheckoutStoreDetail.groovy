package common;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.party.contact.ContactMechWorker;

storeId = parameters.storeId;
if (UtilValidate.isEmpty(storeId)) {
    shoppingCart = session.getAttribute("shoppingCart");
    if (UtilValidate.isNotEmpty(shoppingCart)){
        storeId = shoppingCart.getOrderAttribute("STORE_LOCATION");
        context.shoppingCart = shoppingCart;
    }
} else {
    context.shoppingCart = session.getAttribute("shoppingCart");
}

if (UtilValidate.isEmpty(storeId)) {
    orderId = parameters.orderId;
    if (UtilValidate.isNotEmpty(orderId)) {
        orderAttrPickupStore = delegator.findOne("OrderAttribute", ["orderId" : orderId, "attrName" : "STORE_LOCATION"], true);
        if (UtilValidate.isNotEmpty(orderAttrPickupStore)) {
            storeId = orderAttrPickupStore.attrValue;
        }
    }
}

if (UtilValidate.isNotEmpty(storeId)) {
    partyGroup = delegator.findOne("PartyGroup", [partyId : storeId], true);
    if (UtilValidate.isNotEmpty(partyGroup)) {
        context.storeInfo = partyGroup;
    }

    partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, storeId, false);
    if (partyContactMechValueMaps) {
        partyContactMechValueMaps.each { partyContactMechValueMap ->
            contactMechPurposes = partyContactMechValueMap.partyContactMechPurposes;
            contactMechPurposes.each { contactMechPurpose ->
                if (contactMechPurpose.contactMechPurposeTypeId.equals("GENERAL_LOCATION")) {
                    context.storeAddress = partyContactMechValueMap.postalAddress;
                } else if (contactMechPurpose.contactMechPurposeTypeId.equals("PRIMARY_PHONE")) {
                    context.storePhone = partyContactMechValueMap.telecomNumber;
                }
            }
        }
    }
}