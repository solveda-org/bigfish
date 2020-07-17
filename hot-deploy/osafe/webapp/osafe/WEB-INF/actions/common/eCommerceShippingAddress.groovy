package common;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactHelper;

cart = session.getAttribute("shoppingCart");
party = userLogin.getRelatedOneCache("Party");
partyId = party.partyId;
context.party = party;

context.shippingContactMechList = FastList.newInstance();
context.billingContactMechList = FastList.newInstance();
if (UtilValidate.isNotEmpty(party))
{
	partyContactMechPurpose = party.getRelatedCache("PartyContactMechPurpose");
	partyContactMechPurpose = EntityUtil.filterByDate(partyContactMechPurpose,true);

	// This should return the current billing address
	partyBillingLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "BILLING_LOCATION"));
	partyBillingLocations = EntityUtil.getRelatedCache("PartyContactMech", partyBillingLocations);
	partyBillingLocations = EntityUtil.filterByDate(partyBillingLocations,true);
	partyBillingLocations = EntityUtil.orderBy(partyBillingLocations, UtilMisc.toList("fromDate DESC"));
    if (UtilValidate.isNotEmpty(partyBillingLocations)) 
    {
        billingContactMechList = EntityUtil.getRelated("ContactMech",partyBillingLocations);
        context.billingContactMechList = billingContactMechList;
    }
	
    partyShippingLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "SHIPPING_LOCATION"));
    partyShippingLocations = EntityUtil.getRelatedCache("PartyContactMech", partyShippingLocations);
    partyShippingLocations = EntityUtil.filterByDate(partyShippingLocations,true);
    partyShippingLocations = EntityUtil.orderBy(partyShippingLocations, UtilMisc.toList("fromDate DESC"));
    if (UtilValidate.isNotEmpty(partyShippingLocations)) 
    {
        shippingContactMechList=EntityUtil.getRelated("ContactMech",partyShippingLocations);
        context.shippingContactMechList = shippingContactMechList;
    }
    
}

if(UtilValidate.isNotEmpty(context.billingContactMechList))
{
    billingContactMech = context.billingContactMechList.get(0);

    // Moving the billing address to the front of the list
    if(UtilValidate.isNotEmpty(context.shippingContactMechList))
    {
        context.shippingContactMechList.remove(billingContactMech);
    }
    context.shippingContactMechList.add(0,billingContactMech);
    context.billingContactMechId=billingContactMech.contactMechId;
}

shippingContactMechList = context.shippingContactMechList
shippingContactMechPhoneMap = [:];
for (GenericValue contactMech : shippingContactMechList)
{
    phoneNumberMap = [:];
    if(contactMech)
    {
        contactMechIdFrom = contactMech.contactMechId;
        contactMechLinkList = delegator.findByAndCache("ContactMechLink", UtilMisc.toMap("contactMechIdFrom", contactMechIdFrom))

        for (GenericValue link: contactMechLinkList){
            contactMechIdTo = link.contactMechIdTo
            contactMech = delegator.findByPrimaryKeyCache("ContactMech", [contactMechId : contactMechIdTo]);
            phonePurposeList  = EntityUtil.filterByDate(contactMech.getRelatedCache("PartyContactMechPurpose"), true);
            partyContactMechPurpose = EntityUtil.getFirst(phonePurposeList)

            if(partyContactMechPurpose) 
            {
                telecomNumber = partyContactMechPurpose.getRelatedOneCache("TelecomNumber");
                phoneNumberMap[partyContactMechPurpose.contactMechPurposeTypeId]=telecomNumber;
            }
        }
    }
    shippingContactMechPhoneMap[contactMechIdFrom] = phoneNumberMap;
}
context.shippingContactMechPhoneMap = shippingContactMechPhoneMap;
billingAddressContactMech = EntityUtil.getFirst(context.billingContactMechList);
if (UtilValidate.isNotEmpty(billingAddressContactMech)) 
{
    billingPostalAddress = delegator.findOne("PostalAddress", [contactMechId : billingAddressContactMech.contactMechId], true);
    context.BILLINGPostalAddress = billingPostalAddress;
}
shippingAddressContactMech = EntityUtil.getFirst(context.shippingContactMechList);
if (UtilValidate.isNotEmpty(shippingAddressContactMech)) 
{
    shippingPostalAddress = delegator.findOne("PostalAddress", [contactMechId : shippingAddressContactMech.contactMechId], true);
    context.SHIPPINGPostalAddress = shippingPostalAddress;
}

