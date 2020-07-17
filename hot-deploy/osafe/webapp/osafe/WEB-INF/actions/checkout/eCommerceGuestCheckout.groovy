package checkout;

import java.util.List;
import java.util.Map;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactHelper;

cart = session.getAttribute("shoppingCart");

partyId = cart.getPartyId();
if (UtilValidate.isNotEmpty(partyId)) {
    party = delegator.findOne("Party", [partyId : partyId], true);
    context.person = delegator.findOne("Person", [partyId : partyId], true);

    emailContactMechId = cart.getContactMech("ORDER_EMAIL");
    if (UtilValidate.isNotEmpty(emailContactMechId)){
        emailContactMech = delegator.findByPrimaryKey("ContactMech", [contactMechId : emailContactMechId]);
        if (UtilValidate.isNotEmpty(emailContactMech)){
            context.emailAddress = emailContactMech.infoString;
            requestParameters.USERNAME = emailContactMech.infoString;
        }
    }

    shippingPostalAddress = cart.getShippingAddress();
    if (UtilValidate.isNotEmpty(shippingPostalAddress)) {
        context.SHIPPINGPhoneNumberMap = getPhoneNumberMap(shippingPostalAddress);
        context.SHIPPINGPostalAddress = shippingPostalAddress;
    }

    billingAddressContactMechId = cart.getContactMech("BILLING_LOCATION");
    if (UtilValidate.isEmpty(billingAddressContactMechId)) {
        billingContactMech = EntityUtil.getFirst(ContactHelper.getContactMech(party, "BILLING_LOCATION", "POSTAL_ADDRESS", false));
        if (UtilValidate.isNotEmpty(billingContactMech)) {
            billingAddressContactMechId = billingContactMech.contactMechId;
        }
    }
    if (UtilValidate.isNotEmpty(billingAddressContactMechId)) {
        billingPostalAddress = delegator.findOne("PostalAddress", [contactMechId : billingAddressContactMechId], true);
        if (UtilValidate.isNotEmpty(billingPostalAddress)) {
            context.BILLINGPhoneNumberMap = getPhoneNumberMap(billingPostalAddress);
            context.BILLINGPostalAddress = billingPostalAddress;
        }
    }
    if (UtilValidate.isNotEmpty(shippingPostalAddress) && UtilValidate.isNotEmpty(billingAddressContactMechId)) {
        if (billingAddressContactMechId.equals(shippingPostalAddress.contactMechId)) {
            context.isSameAsBilling = "Y";
        }
    }
}
Map getPhoneNumberMap(GenericValue postalAddress) {
    phoneNumberMap = [:];
    if(UtilValidate.isNotEmpty(postalAddress)){
        contactMechIdFrom = postalAddress.contactMechId;
        contactMechLinkList = delegator.findByAnd("ContactMechLink", UtilMisc.toMap("contactMechIdFrom", contactMechIdFrom))

        for (GenericValue link: contactMechLinkList){
            contactMechIdTo = link.contactMechIdTo
            contactMech = delegator.findByPrimaryKey("ContactMech", [contactMechId : contactMechIdTo]);
            phonePurposeList  = EntityUtil.filterByDate(contactMech.getRelated("PartyContactMechPurpose"), true);
            partyContactMechPurpose = EntityUtil.getFirst(phonePurposeList)

            telecomNumber = null;
            if(UtilValidate.isNotEmpty(partyContactMechPurpose)) {
                telecomNumber = partyContactMechPurpose.getRelatedOne("TelecomNumber");
            }

            if(UtilValidate.isNotEmpty(telecomNumber)) {
                phoneNumberMap[partyContactMechPurpose.contactMechPurposeTypeId]=telecomNumber;
            }
        }
    }
    return phoneNumberMap;
}