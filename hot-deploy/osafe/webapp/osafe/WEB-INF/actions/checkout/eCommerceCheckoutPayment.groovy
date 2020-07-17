import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.store.*;
import org.ofbiz.base.util.UtilMisc;

cart = session.getAttribute("shoppingCart");
currencyUomId = cart.getCurrency();
userLogin = session.getAttribute("userLogin");
productStoreId = ProductStoreWorker.getProductStoreId(request);

// Starting custom changes
if (UtilValidate.isEmpty(userLogin)) 
{
    userLogin = cart.getUserLogin();
}
// Ending custom changes

checkOutPaymentId = "";
if (UtilValidate.isNotEmpty(cart))
{
    if (UtilValidate.isNotEmpty(cart.getPaymentMethodIds())) 
    {
        checkOutPaymentId = cart.getPaymentMethodIds().get(0);
    } else if (UtilValidate.isNotEmpty(cart.getPaymentMethodTypeIds())) 
    {
        checkOutPaymentId = cart.getPaymentMethodTypeIds().get(0);
    }
}

context.shoppingCart = cart;
context.userLogin = userLogin;
context.productStoreId = productStoreId;
context.checkOutPaymentId = checkOutPaymentId;

