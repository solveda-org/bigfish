package common;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactHelper;

cart = session.getAttribute("shoppingCart");
party = userLogin.getRelatedOne("Party");
partyId = party.partyId;
context.party = party;

context.shippingContactMechList = ContactHelper.getContactMech(party, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
// This should return the current billing address
context.billingContactMechList = ContactHelper.getContactMech(party, "BILLING_LOCATION", "POSTAL_ADDRESS", false);
if(UtilValidate.isNotEmpty(context.billingContactMechList)){
    billingContactMech = context.billingContactMechList.get(0);

    // Moving the billing address to the front of the list
    if(UtilValidate.isNotEmpty(context.shippingContactMechList)){
        context.shippingContactMechList.remove(billingContactMech);
    }
    context.shippingContactMechList.add(0,billingContactMech);
    context.billingContactMechId=billingContactMech.contactMechId;
}

shippingContactMechList = context.shippingContactMechList
shippingContactMechPhoneMap = [:];
for (GenericValue contactMech : shippingContactMechList){
    phoneNumberMap = [:];
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
                phoneNumberMap[partyContactMechPurpose.contactMechPurposeTypeId]=telecomNumber;
            }
        }
    }
    shippingContactMechPhoneMap[contactMechIdFrom] = phoneNumberMap;
}
context.shippingContactMechPhoneMap = shippingContactMechPhoneMap;
billingAddressContactMech = EntityUtil.getFirst(context.billingContactMechList);
if (UtilValidate.isNotEmpty(billingAddressContactMech)) {
    billingPostalAddress = delegator.findOne("PostalAddress", [contactMechId : billingAddressContactMech.contactMechId], true);
    context.BILLINGPostalAddress = billingPostalAddress;
}
shippingAddressContactMech = EntityUtil.getFirst(context.shippingContactMechList);
if (UtilValidate.isNotEmpty(shippingAddressContactMech)) {
    shippingPostalAddress = delegator.findOne("PostalAddress", [contactMechId : shippingAddressContactMech.contactMechId], true);
    context.SHIPPINGPostalAddress = shippingPostalAddress;
}

