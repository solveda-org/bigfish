package common;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;

shoppingCart = session.getAttribute("shoppingCart");

// retrieve the product store id from the cart
productStoreId = shoppingCart.getProductStoreId();

party = userLogin.getRelatedOneCache("Party");
partyId = party.partyId;

person = party.getRelatedOneCache("Person");

// Billing
billingPersonFirstName = "";
billingPersonLastName = "";
if(UtilValidate.isNotEmpty(person)) 
{
    billingPersonFirstName = person.firstName?person.firstName:"";
    billingPersonLastName = person.lastName?person.lastName:"";
}

billingAddress = shoppingCart.getBillingAddress();
billingContactMechId = "";
if (UtilValidate.isNotEmpty(billingAddress))
{
    billingAddress = billingAddress;
    billingContactMechId = billingAddress.contactMechId;
}
else
{
    billingAddressContactMechId =shoppingCart.getContactMech("BILLING_LOCATION");
    if (UtilValidate.isNotEmpty(billingAddressContactMechId))
    {
      billingAddress = delegator.findOne("PostalAddress", [contactMechId :billingAddressContactMechId], true);
      if (UtilValidate.isNotEmpty(billingAddress))
      {
        billingAddress = billingAddress;
        billingContactMechId = billingAddress.contactMechId;
      }
    
    }
    else
    {
      billingContactMechAddressList = ContactHelper.getContactMech(party, "BILLING_LOCATION", "POSTAL_ADDRESS", false);
      billingContactMechAddress = EntityUtil.getFirst(billingContactMechAddressList);
      billingAddress=billingContactMechAddress.getRelatedOneCache("PostalAddress");
      billingAddress = billingAddress;
      billingContactMechId = billingAddress.contactMechId;
    }
}

// Shipping
shippingAddress = shoppingCart.getShippingAddress();

// Credit Card Info
creditCardTypes = delegator.findByAndCache("Enumeration", [enumTypeId : "CREDIT_CARD_TYPE"], ["sequenceId"]);
creditCardTypesMap = [:];
for (GenericValue creditCardType :  creditCardTypes)
{
    creditCardTypesMap[creditCardType.enumCode] = creditCardType.description;
}

// Selected Shipping Method
chosenShippingMethod = "";
chosenShippingMethodDescription = "";
if (shoppingCart.getShipmentMethodTypeId() && shoppingCart.getCarrierPartyId()) 
{
    chosenShippingMethod = shoppingCart.getShipmentMethodTypeId() + '@' + shoppingCart.getCarrierPartyId();
    chosenShippingMethodDescription = shoppingCart.getCarrierPartyId() + " " + shoppingCart.getShipmentMethodType(0).description;
}

//BUILD CONTEXT MAP FOR PRODUCT_FEATURE_TYPE_ID and DESCRIPTION(EITHER FROM PRODUCT_FEATURE_GROUP OR PRODUCT_FEATURE_TYPE)
Map productFeatureTypesMap = FastMap.newInstance();
productFeatureTypesList = delegator.findList("ProductFeatureType", null, null, null, null, true);

//get the whole list of ProductFeatureGroup and ProductFeatureGroupAndAppl
productFeatureGroupList = delegator.findList("ProductFeatureGroup", null, null, null, null, true);
productFeatureGroupAndApplList = delegator.findList("ProductFeatureGroupAndAppl", null, null, null, null, true);
productFeatureGroupAndApplList = EntityUtil.filterByDate(productFeatureGroupAndApplList);

if(UtilValidate.isNotEmpty(productFeatureTypesList))
{
    for (GenericValue productFeatureType : productFeatureTypesList)
    {
    	//filter the ProductFeatureGroupAndAppl list based on productFeatureTypeId to get the ProductFeatureGroupId
    	productFeatureGroupAndAppls = EntityUtil.filterByAnd(productFeatureGroupAndApplList, UtilMisc.toMap("productFeatureTypeId", productFeatureType.productFeatureTypeId));
    	description = "";
    	if(UtilValidate.isNotEmpty(productFeatureGroupAndAppls))
    	{
    		productFeatureGroupAndAppl = EntityUtil.getFirst(productFeatureGroupAndAppls);
        	productFeatureGroups = EntityUtil.filterByAnd(productFeatureGroupList, UtilMisc.toMap("productFeatureGroupId", productFeatureGroupAndAppl.productFeatureGroupId));
        	productFeatureGroup = EntityUtil.getFirst(productFeatureGroups);
        	description = productFeatureGroup.description;
    	}
    	else
    	{
    		description = productFeatureType.description;
    	}
    	productFeatureTypesMap.put(productFeatureType.productFeatureTypeId,description);
    }
	
}

context.productFeatureTypesMap = productFeatureTypesMap;
if(UtilValidate.isNotEmpty(chosenShippingMethod))
{
	context.chosenShippingMethod = chosenShippingMethod;
}
if(UtilValidate.isNotEmpty(chosenShippingMethodDescription))
{
	context.chosenShippingMethodDescription = chosenShippingMethodDescription;
}
context.shoppingCart  = shoppingCart;
context.productStoreId = productStoreId;

context.billingPersonFirstName = billingPersonFirstName;
context.billingPersonLastName = billingPersonLastName;
context.billingAddress = billingAddress;
context.billingContactMechId = billingContactMechId;

context.shippingAddress = shippingAddress;
context.creditCardTypesMap = creditCardTypesMap;