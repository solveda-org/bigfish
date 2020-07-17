import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

paymentMethodTypeId = request.getParameter("paymentMethodTypeId");
paymentServiceTypeEnumId = request.getParameter("paymentServiceTypeEnumId");
customMethodsCond = null;

if (paymentMethodTypeId && paymentServiceTypeEnumId) 
{
    context.productStorePaymentSetting = delegator.findOne("ProductStorePaymentSetting",UtilMisc.toMap("productStoreId", productStoreId, "paymentMethodTypeId", paymentMethodTypeId, "paymentServiceTypeEnumId", paymentServiceTypeEnumId), false);
	customMethodTypeId = "";

    if (paymentServiceTypeEnumId == "PRDS_PAY_AUTH" )
	{
		if (paymentMethodTypeId == "CREDIT_CARD")
		{
		    customMethodTypeId = "CC_AUTH";
		}
		else if (paymentMethodTypeId == "GIFT_CARD")
		{
		    customMethodTypeId = "GIFT_AUTH";
		}
		else if (paymentMethodTypeId == "FIN_ACCOUNT")
		{
		    customMethodTypeId = "FIN_AUTH";
		}
		else if (paymentMethodTypeId == "EFT_ACCOUNT")
		{
		    customMethodTypeId = "EFT_AUTH";
		}
		else if (paymentMethodTypeId == "EXT_PAYPAL")
		{
		    customMethodTypeId = "PAYPAL_AUTH";
		}
		else if (paymentMethodTypeId == "EXT_EBS")
		{
		    customMethodTypeId = "EBS_AUTH";
		}
	} 
	else if (paymentServiceTypeEnumId == "PRDS_PAY_CAPTURE" )
	{
		if (paymentMethodTypeId == "CREDIT_CARD")
		{
		    customMethodTypeId = "CC_CAPTURE";
		}
		else if (paymentMethodTypeId == "GIFT_CARD")
		{
		    customMethodTypeId = "GIFT_CAPTURE";
		}
		else if (paymentMethodTypeId == "FIN_ACCOUNT")
		{
		    customMethodTypeId = "FIN_CAPTURE";
		}
		else if (paymentMethodTypeId == "EXT_PAYPAL")
		{
		    customMethodTypeId = "PAYPAL_CAPTURE";
		}
		else if (paymentMethodTypeId == "EXT_EBS")
		{
		    customMethodTypeId = "EBS_CAPTURE";
		}
		else if (paymentMethodTypeId == "EFT_ACCOUNT")
		{
		    customMethodTypeId = "EFT_CAPTURE";
		}
	} 
	else if (paymentServiceTypeEnumId == "PRDS_PAY_REAUTH" )
	{
		if (paymentMethodTypeId == "CREDIT_CARD")
		{
		    customMethodTypeId = "CC_AUTH";
		}
		else if (paymentMethodTypeId == "GIFT_CARD")
		{
		    customMethodTypeId = "GIFT_AUTH";
		}
		else if (paymentMethodTypeId == "FIN_ACCOUNT")
		{
		    customMethodTypeId = "FIN_AUTH";
		}
		else if (paymentMethodTypeId == "EXT_PAYPAL")
		{
		    customMethodTypeId = "PAYPAL_AUTH";
		}
		else if (paymentMethodTypeId == "EXT_EBS")
		{
		    customMethodTypeId = "EBS_AUTH";
		}
		else if (paymentMethodTypeId == "EFT_ACCOUNT")
		{
		    customMethodTypeId = "EFT_AUTH";
		}
	} 
	else if (paymentServiceTypeEnumId == "PRDS_PAY_REFUND" )
	{
		if (paymentMethodTypeId == "CREDIT_CARD")
		{
		    customMethodTypeId = "CC_REFUND";
		}
		else if (paymentMethodTypeId == "GIFT_CARD")
		{
		    customMethodTypeId = "GIFT_REFUND";
		}
		else if (paymentMethodTypeId == "FIN_ACCOUNT")
		{
		    customMethodTypeId = "FIN_REFUND";
		}
		else if (paymentMethodTypeId == "EXT_PAYPAL")
		{
		    customMethodTypeId = "PAYPAL_REFUND";
		}
		else if (paymentMethodTypeId == "EXT_EBS")
		{
		    customMethodTypeId = "EBS_REFUND";
		}
		else if (paymentMethodTypeId == "EFT_ACCOUNT")
		{
		    customMethodTypeId = "EFT_REFUND";
		}
	}
	else if (paymentServiceTypeEnumId == "PRDS_PAY_RELEASE" )
	{
		if (paymentMethodTypeId == "CREDIT_CARD")
		{
		    customMethodTypeId = "CC_RELEASE";
		}
		else if (paymentMethodTypeId == "GIFT_CARD")
		{
		    customMethodTypeId = "GIFT_RELEASE";
		}
		else if (paymentMethodTypeId == "EFT_ACCOUNT")
		{
		    customMethodTypeId = "EFT_RELEASE";
		}
		else if (paymentMethodTypeId == "FIN_ACCOUNT")
		{
		    customMethodTypeId = "FIN_RELEASE";
		}
		else if (paymentMethodTypeId == "EXT_PAYPAL")
		{
		    customMethodTypeId = "PAYPAL_RELEASE";
		}
		else if (paymentMethodTypeId == "EXT_EBS")
		{
		    customMethodTypeId = "EBS_RELEASE";
		}
	}

	if (UtilValidate.isNotEmpty(customMethodTypeId))
	{
	    customMethodsCond = EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, customMethodTypeId);
	}
} 

if (!paymentMethodTypeId || !paymentServiceTypeEnumId) 
{
    customMethods = FastList.newInstance();
    customMethods.add(EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "CC_AUTH"));
    customMethods.add(EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "CC_CAPTURE"));
    customMethods.add(EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "CC_REAUTH"));
    customMethods.add(EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "CC_REFUND"));
    customMethods.add(EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "CC_RELEASE"));
    customMethods.add(EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "CC_CREDIT"));
    customMethods.add(EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "EFT_AUTH"));
    customMethods.add(EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "EFT_RELEASE"));
    customMethods.add(EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "FIN_AUTH"));
    customMethods.add(EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "FIN_CAPTURE"));
    customMethods.add(EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "FIN_REFUND"));
    customMethods.add(EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "FIN_RELEASE"));
    customMethods.add(EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "GIFT_AUTH"));
    customMethods.add(EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "GIFT_CAPTURE"));
    customMethods.add(EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "GIFT_REFUND"));
    customMethods.add(EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "GIFT_RELEASE"));
    customMethodsCond = EntityCondition.makeCondition(customMethods, EntityOperator.OR);
}
if (paymentServiceTypeEnumId == "PRDS_PAY_EXTERNAL") 
{
    context.paymentCustomMethods = null;
} else 
{
    context.paymentCustomMethods = delegator.findList("CustomMethod", customMethodsCond, null, ["description"], null, false);
}