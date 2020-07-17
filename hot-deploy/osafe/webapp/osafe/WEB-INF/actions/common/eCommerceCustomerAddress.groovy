package common;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactHelper;

partyId = null;

userLogin = context.userLogin;
if (userLogin) {
    partyId = userLogin.partyId;
}

if (partyId) {

    party = delegator.findByPrimaryKey("Party", [partyId : partyId]);
    person = party.getRelatedOne("Person");
    // This should return the current billing address
    billingContactMechList = ContactHelper.getContactMech(party, "BILLING_LOCATION", "POSTAL_ADDRESS", false);
    shippingContactMechList = ContactHelper.getContactMech(party, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
    userEmailContactMechList = ContactHelper.getContactMech(party, "PRIMARY_EMAIL", "EMAIL_ADDRESS", false)

    billingAddressContactMech = EntityUtil.getFirst(billingContactMechList);
    if (UtilValidate.isNotEmpty(billingAddressContactMech)) {
        billingPostalAddress = delegator.findOne("PostalAddress", [contactMechId : billingAddressContactMech.contactMechId], true);
        context.BILLINGPostalAddress = billingPostalAddress;
        context.billingContactMechId = billingPostalAddress.contactMechId;
    }

    shippingAddressContactMech = EntityUtil.getFirst(shippingContactMechList);
    if (UtilValidate.isNotEmpty(shippingAddressContactMech)) {
        shippingPostalAddress = delegator.findOne("PostalAddress", [contactMechId : shippingAddressContactMech.contactMechId], true);
        context.SHIPPINGPostalAddress = shippingPostalAddress;
    }

    userEmailContactMech = EntityUtil.getFirst(userEmailContactMechList);
    if (UtilValidate.isNotEmpty(userEmailContactMech)) {
        context.userEmailContactMech = userEmailContactMech;
        context.userEmailAddress = userEmailContactMech.infoString;
        partyContactMech = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId", userEmailContactMech.contactMechId));
        partyContactMech = EntityUtil.filterByDate(partyContactMech);
        if (UtilValidate.isNotEmpty(partyContactMech))
        {
          partyContactMech = EntityUtil.getFirst(partyContactMech);
          context.userEmailAllowSolicitation= partyContactMech.allowSolicitation;
        }
    }
    context.BILLINGContactMechList = billingContactMechList;
    context.SHIPPINGContactMechList = shippingContactMechList;
    context.userEmailContactMechList = userEmailContactMechList;
    context.party = party;
    context.partyId = partyId;
    context.person=person;
}

shoppingCart = session.getAttribute("shoppingCart");
if (UtilValidate.isNotEmpty(shoppingCart)){
    isSameAsBilling = shoppingCart.getAttribute("isSameAsBilling");
    if (UtilValidate.isNotEmpty(isSameAsBilling)){
        context.isSameAsBilling = isSameAsBilling;
    }
}