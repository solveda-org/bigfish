package order;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.order.order.OrderReadHelper;
import com.osafe.util.OsafeAdminUtil;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilFormatOut;

userLogin = session.getAttribute("userLogin");
orderId = StringUtils.trimToEmpty(parameters.orderId);

orderHeader = null;

if (UtilValidate.isNotEmpty(orderId)) 
{
	orderHeader = delegator.findByPrimaryKey("OrderHeader", [orderId : orderId]);
	context.orderHeader = orderHeader;
	
	orderProductStore = orderHeader.getRelatedOne("ProductStore");
	if (UtilValidate.isNotEmpty(orderProductStore.storeName))
	{
		productStoreName = orderProductStore.storeName;
	}
	else
	{
		productStoreName = orderHeader.productStoreId;
	}
	context.productStoreName = productStoreName;
	
}

if(UtilValidate.isNotEmpty(orderId))
{
    List summaryPaymentInfo = FastList.newInstance();
    //Fetching Data For PAYMENT METHOD INFO && PAYMENT PREFERENCE section(credit card or paypal)
    if (UtilValidate.isNotEmpty(parameters.orderPaymentPreferenceId)) 
    {
    	context.paraOrderPaymentPreferenceId = parameters.orderPaymentPreferenceId;
    }
    context.summaryPaymentInfo = delegator.findList("OrderPaymentPreference",EntityCondition.makeCondition([orderId : orderId]), null, null, null, false);
    GenericValue orderPaymentPreference = null;
    
    if(UtilValidate.isNotEmpty(context.orderPaymentPreferenceId))
    {
        paymentPrefId = context.orderPaymentPreferenceId;
        orderPaymentPreference = delegator.findByPrimaryKey("OrderPaymentPreference",UtilMisc.toMap("orderPaymentPreferenceId",paymentPrefId));
        context.date = OsafeAdminUtil.convertDateTimeFormat(orderPaymentPreference.createdDate, preferredDateFormat);
        context.time = OsafeAdminUtil.convertDateTimeFormat(orderPaymentPreference.createdDate, preferredTimeFormat);
        
        orderHeader = orderPaymentPreference.getRelatedOne("OrderHeader");
        orderReadHelper = new OrderReadHelper(orderHeader);
        if(UtilValidate.isNotEmpty(orderPaymentPreference.maxAmount))
        {
        	maxAmountFormatted = UtilFormatOut.formatCurrency(orderPaymentPreference.maxAmount, orderReadHelper.getCurrency(), locale, globalContext.currencyRounding);
            context.maxAmount = maxAmountFormatted;	
        }
        
        context.methodType = orderPaymentPreference.paymentMethodTypeId;
        paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType");
        context.description=paymentMethodType.description;
        status = orderPaymentPreference.getRelatedOne("StatusItem");
        context.statusDescription=status.description;
        context.createdByUserLogin=orderPaymentPreference.createdByUserLogin;
        
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
                if((paymentMethod.getString("paymentMethodTypeId")).equals("EXT_EBS"))
                {
                    paymentMethodInfo = paymentMethod.getRelatedOne("EbsPaymentMethod");
                    context.ebsInfo = paymentMethodInfo;
                    context.paymentMethodInfoHeading =  uiLabelMap.EbsPaymentMethodHeading;
                }
                if((paymentMethod.getString("paymentMethodTypeId")).equals("GIFT_CARD"))
                {
                    paymentMethodInfo = paymentMethod.getRelatedOne("GiftCard");
                    context.giftInfo = paymentMethodInfo;
                    context.paymentMethodInfoHeading =  uiLabelMap.GiftCardPaymentMethodHeading;
                }
            }
        }
        context.paymentPrefInfo = orderPaymentPreference;
    }
    //Fetching Data For PAYMENT GATEWAY RESPONSE &&  PAYMENT INFO section.
    if(UtilValidate.isNotEmpty(orderPaymentPreference))
    {
    	GenericValue paymentInfo = null;
        orderReadHelper = new OrderReadHelper(orderHeader);
        gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse");
        if(UtilValidate.isNotEmpty(gatewayResponses))
        {
            context.gatewayResponseInfoList = EntityUtil.orderBy(gatewayResponses, ['lastUpdatedStamp']);
        }
        if(UtilValidate.isNotEmpty(orderReadHelper.getOrderPayments()))
        {
        	payment = delegator.findList("Payment", EntityCondition.makeCondition([paymentPreferenceId: context.orderPaymentPreferenceId]), null, null, null, false);
        	if(UtilValidate.isNotEmpty(payment))
        	{
        		context.paymentInfo = payment.getFirst();
        	}
        }
    }
}