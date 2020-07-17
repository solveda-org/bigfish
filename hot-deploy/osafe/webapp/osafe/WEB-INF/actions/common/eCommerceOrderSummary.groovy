package common;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.store.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastList;

shoppingCart = session.getAttribute("shoppingCart");
context.shoppingCart  = shoppingCart;

// retrieve the product store id from the cart
productStoreId = shoppingCart.getProductStoreId();
context.productStoreId = productStoreId;

party = userLogin.getRelatedOne("Party");
partyId = party.partyId;

person = party.getRelatedOne("Person");

// Billing
if(person) {
    context.billingPersonFirstName = person.firstName?person.firstName:"";
    context.billingPersonLastName = person.lastName?person.lastName:"";
}
billingContactMechAddressList = ContactHelper.getContactMech(party, "BILLING_LOCATION", "POSTAL_ADDRESS", false);
context.billingContactMechAddress = EntityUtil.getFirst(billingContactMechAddressList);

contactMech = context.billingContactMechAddress;
billingPhoneNumberMap = [:];
if(contactMech){
    context.billingContactMechId = contactMech.contactMechId;
    contactMechIdFrom = contactMech.contactMechId;
    contactMechLinkList = delegator.findByAnd("ContactMechLink", UtilMisc.toMap("contactMechIdFrom", contactMechIdFrom))

    for (GenericValue link: contactMechLinkList){
        contactMechIdTo = link.contactMechIdTo
        contactMech = delegator.findByPrimaryKey("ContactMech", [contactMechId : contactMechIdTo]);
        phonePurposeList  = EntityUtil.filterByDate(contactMech.getRelated("PartyContactMechPurpose"), true);
        partyContactMechPurpose = EntityUtil.getFirst(phonePurposeList)

        telecomNumber = null;
        if(partyContactMechPurpose) {
            telecomNumber = partyContactMechPurpose.getRelatedOne("TelecomNumber");
        }

        if(telecomNumber) {
            billingPhoneNumberMap[partyContactMechPurpose.contactMechPurposeTypeId]=telecomNumber;
        }
    }
}
context.billingPhoneNumberMap = billingPhoneNumberMap;

// Shipping
shippingContactMechList = ContactHelper.getContactMech(party, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
shippingContactMechPhoneMap = [:];
for (GenericValue contactMech : shippingContactMechList){
    shippingPhoneNumberMap = [:];
    if(contactMech){
        contactMechIdFrom = contactMech.contactMechId;
        contactMechLinkList = delegator.findByAnd("ContactMechLink", UtilMisc.toMap("contactMechIdFrom", contactMechIdFrom))

        for (GenericValue link: contactMechLinkList){
            contactMechIdTo = link.contactMechIdTo
            contactMech = delegator.findByPrimaryKey("ContactMech", [contactMechId : contactMechIdTo]);
            phonePurposeList  = EntityUtil.filterByDate(contactMech.getRelated("PartyContactMechPurpose"), true);
            partyContactMechPurpose = EntityUtil.getFirst(phonePurposeList)

            telecomNumber = null;
            if(partyContactMechPurpose) {
                telecomNumber = partyContactMechPurpose.getRelatedOne("TelecomNumber");
            }

            if(telecomNumber) {
                shippingPhoneNumberMap[partyContactMechPurpose.contactMechPurposeTypeId]=telecomNumber;
            }
        }
    }
    shippingContactMechPhoneMap[contactMechIdFrom] = shippingPhoneNumberMap;
}
context.shippingContactMechPhoneMap = shippingContactMechPhoneMap;

// Credit Card Info
creditCardTypes = delegator.findByAnd("Enumeration", [enumTypeId : "CREDIT_CARD_TYPE"], ["sequenceId"]);
creditCardTypesMap = [:];
for (GenericValue creditCardType :  creditCardTypes)
{
    creditCardTypesMap[creditCardType.enumCode] = creditCardType.description;
}

context.creditCardTypesMap = creditCardTypesMap;

// Selected Shipping Method
if (shoppingCart.getShipmentMethodTypeId() && shoppingCart.getCarrierPartyId()) {
    context.chosenShippingMethod = shoppingCart.getShipmentMethodTypeId() + '@' + shoppingCart.getCarrierPartyId();
    context.chosenShippingMethodDescription = shoppingCart.getCarrierPartyId() + " " + shoppingCart.getShipmentMethodType(0).description;
}
//Add Order Attribute IS DOWNLOADED
shoppingCart.setOrderAttribute("IS_DOWNLOADED","N");
