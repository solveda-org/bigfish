package common;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

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
        partyBillingLocations = EntityUtil.getRelatedCache("PartyContactMech", partyBillingLocations);
        partyBillingLocations = EntityUtil.filterByDate(partyBillingLocations,true);
        partyBillingLocations = EntityUtil.orderBy(partyBillingLocations, UtilMisc.toList("fromDate DESC"));
        if (UtilValidate.isNotEmpty(partyBillingLocations)) 
        {
        	partyBillingLocation = EntityUtil.getFirst(partyBillingLocations);
        	billingPostalAddress = partyBillingLocation.getRelatedOneCache("PostalAddress");
            context.BILLINGPostalAddress = billingPostalAddress;
            context.billingContactMechId = billingPostalAddress.contactMechId;
            billingContactMechList = EntityUtil.getRelated("ContactMech",partyBillingLocations);
            context.BILLINGContactMechList = billingContactMechList;
        }
        
        partyShippingLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "SHIPPING_LOCATION"));
        partyShippingLocations = EntityUtil.getRelatedCache("PartyContactMech", partyShippingLocations);
        partyShippingLocations = EntityUtil.filterByDate(partyShippingLocations,true);
        partyShippingLocations = EntityUtil.orderBy(partyShippingLocations, UtilMisc.toList("fromDate DESC"));
        if (UtilValidate.isNotEmpty(partyShippingLocations)) 
        {
            partyShippingLocation = EntityUtil.getFirst(partyShippingLocations);
            shippingPostalAddress = partyShippingLocation.getRelatedOneCache("PostalAddress");
            context.SHIPPINGPostalAddress = shippingPostalAddress;
            shippingContactMechList=EntityUtil.getRelated("ContactMech",partyShippingLocations);
            context.SHIPPINGContactMechList = shippingContactMechList;
        }
        
        partyPurposeEmails = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "PRIMARY_EMAIL"));
        partyPurposeEmails = EntityUtil.getRelatedCache("PartyContactMech", partyPurposeEmails);
        partyPurposeEmails = EntityUtil.filterByDate(partyPurposeEmails,true);
        partyPurposeEmails = EntityUtil.orderBy(partyPurposeEmails, UtilMisc.toList("fromDate DESC"));
        if (UtilValidate.isNotEmpty(partyPurposeEmails)) 
        {
        	partyPurposeEmail = EntityUtil.getFirst(partyPurposeEmails);
            contactMech = partyPurposeEmail.getRelatedOneCache("ContactMech");
            context.userEmailContactMech = contactMech;
            context.userEmailAddress = contactMech.infoString;
            userEmailContactMechList= EntityUtil.getRelated("ContactMech",partyPurposeEmails);
            context.userEmailContactMechList = userEmailContactMechList;
            context.userEmailAllowSolicitation= partyPurposeEmail.allowSolicitation;
            
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
	shippingApplies = shoppingCart.shippingApplies();
	context.shippingApplies = shippingApplies;
}