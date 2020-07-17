package common;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactHelper;

partyId = null;
userLogin = context.userLogin;

productStore = ProductStoreWorker.getProductStore(request);
context.productStoreId = productStore.productStoreId;
context.productStore = productStore;

context.createAllowPassword = "Y".equals(productStore.allowPassword);
context.getUsername = !"Y".equals(productStore.usePrimaryEmailUsername);

previousParams = parameters._PREVIOUS_PARAMS_;
if (UtilValidate.isNotEmpty(previousParams)) 
{
    previousParams = "?" + previousParams;
} else 
{
    previousParams = "";
}
context.previousParams = previousParams;

if (UtilValidate.isNotEmpty(userLogin)) 
{
    partyId = userLogin.partyId;
}

if (UtilValidate.isNotEmpty(partyId)) 
{

    party = delegator.findByPrimaryKeyCache("Party", [partyId : partyId]);
    if (UtilValidate.isNotEmpty(party)) 
    {
        context.party = party;
        context.partyId = partyId;
        context.person = party.getRelatedOneCache("Person");
        
        partyContactMechPurpose = party.getRelatedCache("PartyContactMechPurpose");
        partyContactMechPurpose = EntityUtil.filterByDate(partyContactMechPurpose,true);

        partyBillingLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "BILLING_LOCATION"));
        if (UtilValidate.isNotEmpty(partyBillingLocations)) 
        {
        	partyBillingLocation = EntityUtil.getFirst(partyBillingLocations);
        	billingPostalAddress = partyBillingLocation.getRelatedOneCache("PostalAddress");
            context.BILLINGPostalAddress = billingPostalAddress;
            context.billingContactMechId = billingPostalAddress.contactMechId;
            billingContactMechList = EntityUtil.getRelatedByAnd("ContactMech", UtilMisc.toMap("contactMechTypeId", "POSTAL_ADDRESS"), partyBillingLocations);
            context.BILLINGContactMechList = billingContactMechList;
        }
        
        partyShippingLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "SHIPPING_LOCATION"));
        if (UtilValidate.isNotEmpty(partyShippingLocations)) 
        {
            partyShippingLocation = EntityUtil.getFirst(partyShippingLocations);
            shippingPostalAddress = partyShippingLocation.getRelatedOneCache("PostalAddress");
            context.SHIPPINGPostalAddress = shippingPostalAddress;
            shippingContactMechList=EntityUtil.getRelatedByAnd("ContactMech", UtilMisc.toMap("contactMechTypeId", "POSTAL_ADDRESS"), partyShippingLocations);
            context.SHIPPINGContactMechList = shippingContactMechList;
        }
        
        partyPurposeEmails = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "PRIMARY_EMAIL"));
        if (UtilValidate.isNotEmpty(partyPurposeEmails)) 
        {
        	partyPurposeEmail = EntityUtil.getFirst(partyPurposeEmails);
            contactMech = partyPurposeEmail.getRelatedOneCache("ContactMech");
            context.userEmailContactMech = contactMech;
            context.userEmailAddress = contactMech.infoString;
            userEmailContactMechList= EntityUtil.getRelatedByAnd("ContactMech", UtilMisc.toMap("contactMechTypeId", "EMAIL_ADDRESS"), partyPurposeEmails);
            context.userEmailContactMechList = userEmailContactMechList;
            partyContactMechs = partyPurposeEmail.getRelatedCache("PartyContactMech");
            partyContactMechs = EntityUtil.filterByAnd(partyContactMechs, UtilMisc.toMap("contactMechId", contactMech.contactMechId));
            if (UtilValidate.isNotEmpty(partyContactMechs))
            {
            	partyContactMech = EntityUtil.getFirst(partyContactMechs);
                context.userEmailAllowSolicitation= partyContactMech.allowSolicitation;
            	
            }
            
        }
        
    }
}

shoppingCart = session.getAttribute("shoppingCart");
if (UtilValidate.isNotEmpty(shoppingCart))
{
    isSameAsBilling = shoppingCart.getAttribute("isSameAsBilling");
    if (UtilValidate.isNotEmpty(isSameAsBilling))
    {
        context.isSameAsBilling = isSameAsBilling;
    }
}