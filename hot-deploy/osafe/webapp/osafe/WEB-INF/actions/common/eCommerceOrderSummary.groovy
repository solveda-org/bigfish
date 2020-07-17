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

party = userLogin.getRelatedOneCache("Party");
partyId = party.partyId;

person = party.getRelatedOneCache("Person");

// Billing
if(person) 
{
    context.billingPersonFirstName = person.firstName?person.firstName:"";
    context.billingPersonLastName = person.lastName?person.lastName:"";
}

billingAddress = shoppingCart.getBillingAddress();
if (UtilValidate.isNotEmpty(billingAddress))
{
    context.billingAddress = billingAddress;
    context.billingContactMechId = billingAddress.contactMechId;
}
else
{
    billingAddressContactMechId =shoppingCart.getContactMech("BILLING_LOCATION");
    if (UtilValidate.isNotEmpty(billingAddressContactMechId))
    {
      billingAddress = delegator.findOne("PostalAddress", [contactMechId :billingAddressContactMechId], true);
      if (UtilValidate.isNotEmpty(billingAddress))
      {
        context.billingAddress = billingAddress;
        context.billingContactMechId = billingAddress.contactMechId;
      }
    
    }
    else
    {
      billingContactMechAddressList = ContactHelper.getContactMech(party, "BILLING_LOCATION", "POSTAL_ADDRESS", false);
      billingContactMechAddress = EntityUtil.getFirst(billingContactMechAddressList);
      billingAddress=billingContactMechAddress.getRelatedOneCache("PostalAddress");
      context.billingAddress = billingAddress;
      context.billingContactMechId = billingAddress.contactMechId;
    }
}

// Shipping
context.shippingAddress = shoppingCart.getShippingAddress();

// Credit Card Info
creditCardTypes = delegator.findByAndCache("Enumeration", [enumTypeId : "CREDIT_CARD_TYPE"], ["sequenceId"]);
creditCardTypesMap = [:];
for (GenericValue creditCardType :  creditCardTypes)
{
    creditCardTypesMap[creditCardType.enumCode] = creditCardType.description;
}

context.creditCardTypesMap = creditCardTypesMap;

// Selected Shipping Method
if (shoppingCart.getShipmentMethodTypeId() && shoppingCart.getCarrierPartyId()) 
{
    context.chosenShippingMethod = shoppingCart.getShipmentMethodTypeId() + '@' + shoppingCart.getCarrierPartyId();
    context.chosenShippingMethodDescription = shoppingCart.getCarrierPartyId() + " " + shoppingCart.getShipmentMethodType(0).description;
}
