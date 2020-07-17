import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import com.osafe.util.Util;
import org.ofbiz.entity.GenericValue;

userLogin = session.getAttribute("userLogin");
context.userLogin = userLogin;
savedPaymentMethodValueMaps = FastList.newInstance();
partyId = null;
if(Util.isProductStoreParmTrue(request,"CHECKOUT_KEEP_PAYMENT_METHODS"))
{
    if(UtilValidate.isNotEmpty(userLogin))
    {
        partyId = userLogin.partyId;
    }
    if(UtilValidate.isNotEmpty(partyId))
    {
        paymentMethods = delegator.findByAndCache("PaymentMethod", UtilMisc.toMap("partyId", partyId), UtilMisc.toList("-lastUpdatedStamp"));
        paymentMethodValueMaps = FastList.newInstance();
        if(UtilValidate.isNotEmpty(paymentMethods))
        {
            paymentMethods = EntityUtil.filterByDate(paymentMethods, true);
            for (GenericValue paymentMethod : paymentMethods) 
            {
                valueMap = FastMap.newInstance();
                paymentMethodValueMaps.add(valueMap);
                valueMap.put("paymentMethod", paymentMethod);
                if ("CREDIT_CARD".equals(paymentMethod.getString("paymentMethodTypeId"))) 
                {
                    GenericValue creditCard = paymentMethod.getRelatedOneCache("CreditCard");
                    if (UtilValidate.isNotEmpty(creditCard))
                    {
                        valueMap.put("creditCard", creditCard);
                        
                    }
                }
            }
            savedPaymentMethodValueMaps= paymentMethodValueMaps;
        }
    }
    context.savedPaymentMethodValueMaps = savedPaymentMethodValueMaps;
}
