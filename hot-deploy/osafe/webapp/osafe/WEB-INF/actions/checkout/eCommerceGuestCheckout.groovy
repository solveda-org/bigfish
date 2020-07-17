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
if (UtilValidate.isNotEmpty(partyId)) 
{
    emailContactMechId = cart.getContactMech("ORDER_EMAIL");
    if (UtilValidate.isNotEmpty(emailContactMechId))
    {
        emailContactMech = delegator.findByPrimaryKeyCache("ContactMech", [contactMechId : emailContactMechId]);
        if (UtilValidate.isNotEmpty(emailContactMech))
        {
            context.emailAddress = emailContactMech.infoString;
            requestParameters.USERNAME = emailContactMech.infoString;
        }
    }

    party = delegator.findOne("Party", [partyId : partyId], true);
    if (UtilValidate.isNotEmpty(party)) 
    {
        context.person = party.getRelatedOneCache("Person");

        partyContactMechPurpose = party.getRelatedCache("PartyContactMechPurpose");
        partyContactMechPurpose = EntityUtil.filterByDate(partyContactMechPurpose,true);
        
        shippingPostalAddress = cart.getShippingAddress();
        shippingAddressContactMechId ="";
        if (UtilValidate.isEmpty(shippingPostalAddress)) 
        {
            partyShippingLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "SHIPPING_LOCATION"));
            if (UtilValidate.isNotEmpty(partyShippingLocations)) 
            {
                partyShippingLocation = EntityUtil.getFirst(partyShippingLocations);
                shippingPostalAddress = partyShippingLocation.getRelatedOneCache("PostalAddress");
            }
        }
        if (UtilValidate.isNotEmpty(shippingPostalAddress)) 
        {
            context.SHIPPINGPostalAddress = shippingPostalAddress;
            context.SHIPPINGPhoneNumberMap = getPhoneNumberMap(shippingPostalAddress);
            shippingAddressContactMechId=shippingPostalAddress.contactMechId;
        }
    

        billingAddressContactMechId = cart.getContactMech("BILLING_LOCATION");
        partyBillingLocations="";
        if (UtilValidate.isEmpty(billingAddressContactMechId)) 
        {
            partyBillingLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "BILLING_LOCATION"));
        }
        else
        {
            partyBillingLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechId", billingAddressContactMechId));
        	
        }
        if (UtilValidate.isNotEmpty(partyBillingLocations)) 
        {
        	partyBillingLocation = EntityUtil.getFirst(partyBillingLocations);
        	billingPostalAddress = partyBillingLocation.getRelatedOneCache("PostalAddress");
            billingAddressContactMechId = partyBillingLocation.contactMechId;
            context.BILLINGPostalAddress = billingPostalAddress;
            context.BILLINGPhoneNumberMap = getPhoneNumberMap(billingPostalAddress);
        }

        if (UtilValidate.isNotEmpty(shippingAddressContactMechId) && UtilValidate.isNotEmpty(billingAddressContactMechId)) 
        {
            if (billingAddressContactMechId.equals(shippingPostalAddress.contactMechId)) 
            {
                context.isSameAsBilling = "Y";
            }
        }
    
    }



}
Map getPhoneNumberMap(GenericValue postalAddress) 
{
    phoneNumberMap = [:];
    if(UtilValidate.isNotEmpty(postalAddress))
    {
        contactMechIdFrom = postalAddress.contactMechId;
        contactMechLinkList = delegator.findByAndCache("ContactMechLink", UtilMisc.toMap("contactMechIdFrom", contactMechIdFrom))

        for (GenericValue link: contactMechLinkList)
        {
            contactMechIdTo = link.contactMechIdTo
            contactMech = delegator.findByPrimaryKeyCache("ContactMech", [contactMechId : contactMechIdTo]);
            phonePurposeList  = contactMech.getRelatedCache("PartyContactMechPurpose");
            phonePurposeList  = EntityUtil.filterByDate(phonePurposeList, true);
            partyContactMechPurpose = EntityUtil.getFirst(phonePurposeList)

            telecomNumber = null;
            if(UtilValidate.isNotEmpty(partyContactMechPurpose)) 
            {
                telecomNumber = partyContactMechPurpose.getRelatedOneCache("TelecomNumber");
            }

            if(UtilValidate.isNotEmpty(telecomNumber)) 
            {
                phoneNumberMap[partyContactMechPurpose.contactMechPurposeTypeId]=telecomNumber;
            }
        }
    }
    return phoneNumberMap;
}