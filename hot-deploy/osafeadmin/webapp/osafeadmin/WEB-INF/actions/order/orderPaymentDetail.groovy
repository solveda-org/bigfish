package order;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.base.util.UtilValidate;

userLogin = session.getAttribute("userLogin");
orderId = StringUtils.trimToEmpty(parameters.orderId);
if(UtilValidate.isNotEmpty(orderId))
{
    //Fetching Data For PAYMENT METHOD INFO && PAYMENT PREFERENCE section(credit card or paypal)
    if(UtilValidate.isNotEmpty(parameters.orderPaymentPreferenceId))
    {
        paymentPrefId = parameters.orderPaymentPreferenceId;
        orderPaymentPreference = delegator.findByPrimaryKey("OrderPaymentPreference",UtilMisc.toMap("orderPaymentPreferenceId",paymentPrefId));
        if(UtilValidate.isNotEmpty(orderPaymentPreference))
        {
            paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod");
            if(UtilValidate.isNotEmpty(paymentMethod))
            {
                paymentMethodId = paymentMethod.getString("paymentMethodTypeId")
                if((paymentMethod.getString("paymentMethodTypeId")).equals("CREDIT_CARD"))
                {
                    paymentMethodInfo = paymentMethod.getRelatedOne("CreditCard");
                    context.creditCardInfo = paymentMethodInfo;
                    context.paymentMethodInfoHeading =  uiLabelMap.CreditCardTypeListHeading;
                }
                if((paymentMethod.getString("paymentMethodTypeId")).equals("EXT_PAYPAL"))
                {
                    paymentMethodInfo = paymentMethod.getRelatedOne("PayPalPaymentMethod");
                    context.payPalInfo = paymentMethodInfo;
                    context.paymentMethodInfoHeading =  uiLabelMap.PayPalPaymentMethodHeading;
                }
            }
        }
        context.paymentPrefInfo = orderPaymentPreference;
    }
    //Fetching Data For PAYMENT GATEWAY RESPONSE &&  PAYMENT INFO section.
    if(UtilValidate.isNotEmpty(orderPaymentPreference))
    {
        orderReadHelper = new OrderReadHelper(orderHeader);
        gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse");
        if(UtilValidate.isNotEmpty(gatewayResponses))
        {
            orderedList = EntityUtil.orderBy(gatewayResponses, ['lastUpdatedStamp']);
            context.gatewayResponseInfo = orderedList.getLast();
        }
        if(UtilValidate.isNotEmpty(orderReadHelper.getOrderPayments()))
        {
            context.paymentInfo = orderReadHelper.getOrderPayments().getFirst();
        }
    }
}