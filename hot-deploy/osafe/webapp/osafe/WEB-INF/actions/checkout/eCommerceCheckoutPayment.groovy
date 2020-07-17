import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.store.*;

cart = session.getAttribute("shoppingCart");
currencyUomId = cart.getCurrency();
userLogin = session.getAttribute("userLogin");

// Starting custom changes
if (UtilValidate.isEmpty(userLogin)) {
    userLogin = cart.getUserLogin();
}
// Ending custom changes
partyId = cart.getPartyId();
party = delegator.findByPrimaryKeyCache("Party", [partyId : partyId]);
productStoreId = ProductStoreWorker.getProductStoreId(request);

checkOutPaymentId = "";
if (cart) {
    if (cart.getPaymentMethodIds()) {
        checkOutPaymentId = cart.getPaymentMethodIds().get(0);
    } else if (cart.getPaymentMethodTypeIds()) {
        checkOutPaymentId = cart.getPaymentMethodTypeIds().get(0);
    }
}

finAccounts = delegator.findByAnd("FinAccountAndRole", [partyId : partyId, roleTypeId : "OWNER"]);
finAccounts = EntityUtil.filterByDate(finAccounts, UtilDateTime.nowTimestamp(), "roleFromDate", "roleThruDate", true);
finAccounts = EntityUtil.filterByDate(finAccounts);
context.finAccounts = finAccounts;

context.shoppingCart = cart;
context.userLogin = userLogin;
context.productStoreId = productStoreId;
context.checkOutPaymentId = checkOutPaymentId;
context.paymentMethodList = EntityUtil.filterByDate(party.getRelated("PaymentMethod", null, ["paymentMethodTypeId"]), true);

billingAccountList = BillingAccountWorker.makePartyBillingAccountList(userLogin, currencyUomId, partyId, delegator, dispatcher);
if (billingAccountList) {
    context.selectedBillingAccountId = cart.getBillingAccountId();
    context.billingAccountList = billingAccountList;
}
