package checkout;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.order.shoppingcart.shipping.ShippingEstimateWrapper;

cart = session.getAttribute("shoppingCart");
party = null;
if (UtilValidate.isNotEmpty(userLogin)) {
    party = userLogin.getRelatedOne("Party");
}

requestParams = UtilHttp.getParameterMap(request);
dummyContactMech = null;
dummyPostalAddress = null;
poatalCode = requestParams.get("postalCode") ?: request.getAttribute("postalCode");
if (UtilValidate.isNotEmpty(poatalCode)) {
    dummyContactMech = delegator.makeValue("ContactMech", [contactMechId:"dummyContactMechId", contactMechTypeId:"POSTAL_ADDRESS"]);
    dummyPostalAddress = delegator.makeValue("PostalAddress", [contactMechId : "dummyContactMechId", postalCode : poatalCode]);
    delegator.create(dummyContactMech);
    delegator.create(dummyPostalAddress);
    cart.setShippingContactMechId("dummyContactMechId");
}
productStore = ProductStoreWorker.getProductStore(request);

if (cart) {
    shippingEstWpr = new ShippingEstimateWrapper(dispatcher, cart, 0);
    context.shippingEstWpr = shippingEstWpr;
    context.carrierShipmentMethodList = shippingEstWpr.getShippingMethods();
}

context.shoppingCart = cart;
context.userLogin = userLogin;
context.productStoreId = productStore.productStoreId;
context.productStore = productStore;
if (UtilValidate.isNotEmpty(party)) {
    context.emailList = ContactHelper.getContactMechByType(party, "EMAIL_ADDRESS", false);
}
if (cart.getShipmentMethodTypeId() && cart.getCarrierPartyId()) {
    context.chosenShippingMethod = cart.getShipmentMethodTypeId() + '@' + cart.getCarrierPartyId();
}
if (UtilValidate.isNotEmpty(dummyContactMech) && UtilValidate.isNotEmpty(dummyPostalAddress)) {
    cart.setShippingContactMechId("");
    delegator.removeValue(dummyPostalAddress);
    delegator.removeValue(dummyContactMech);
}
