import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;

paymentMethodTypeId = request.getParameter("paymentMethodTypeId");
paymentServiceTypeEnumId = request.getParameter("paymentServiceTypeEnumId");
customMethodsCond = null;

if (paymentMethodTypeId && paymentServiceTypeEnumId) 
{
    context.productStorePaymentSetting = delegator.findOne("ProductStorePaymentSetting",UtilMisc.toMap("productStoreId", productStoreId, "paymentMethodTypeId", paymentMethodTypeId, "paymentServiceTypeEnumId", paymentServiceTypeEnumId), false);
    if (paymentMethodTypeId == "CREDIT_CARD" && paymentServiceTypeEnumId == "PRDS_PAY_AUTH" ) 
    {
        customMethodsCond = EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "CC_AUTH");
    } else if (paymentMethodTypeId == "CREDIT_CARD" && paymentServiceTypeEnumId == "PRDS_PAY_CAPTURE" ) 
    {
        customMethodsCond = EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "CC_CAPTURE");
    } else if (paymentMethodTypeId == "CREDIT_CARD" && paymentServiceTypeEnumId == "PRDS_PAY_REAUTH" ) 
    {
        customMethodsCond = EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "CC_AUTH");
    } else if (paymentMethodTypeId == "CREDIT_CARD" && paymentServiceTypeEnumId == "PRDS_PAY_REFUND" ) 
    {
        customMethodsCond = EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "CC_REFUND");
    } else if (paymentMethodTypeId == "CREDIT_CARD" && paymentServiceTypeEnumId == "PRDS_PAY_RELEASE" ) 
    {
        customMethodsCond = EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "CC_RELEASE");
    } else if (paymentMethodTypeId == "EFT_ACCOUNT" && paymentServiceTypeEnumId == "PRDS_PAY_AUTH" ) 
    {
        customMethodsCond = EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "EFT_AUTH");
    } else if (paymentMethodTypeId == "EFT_ACCOUNT" && paymentServiceTypeEnumId == "PRDS_PAY_RELEASE" ) 
    {
        customMethodsCond = EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "EFT_RELEASE");
    } else if (paymentMethodTypeId == "FIN_ACCOUNT" && paymentServiceTypeEnumId == "PRDS_PAY_AUTH" ) 
    {
        customMethodsCond = EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "FIN_AUTH");
    } else if (paymentMethodTypeId == "FIN_ACCOUNT" && paymentServiceTypeEnumId == "PRDS_PAY_CAPTURE" ) 
    {
        customMethodsCond = EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "FIN_CAPTURE");
    } else if (paymentMethodTypeId == "FIN_ACCOUNT" && paymentServiceTypeEnumId == "PRDS_PAY_REFUND" ) 
    {
        customMethodsCond = EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "FIN_REFUND");
    } else if (paymentMethodTypeId == "FIN_ACCOUNT" && paymentServiceTypeEnumId == "PRDS_PAY_RELEASE" ) 
    {
        customMethodsCond = EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "FIN_RELEASE");
    } else if (paymentMethodTypeId == "GIFT_CARD" && paymentServiceTypeEnumId == "PRDS_PAY_AUTH" ) 
    {
        customMethodsCond = EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "GIFT_AUTH");
    } else if (paymentMethodTypeId == "GIFT_CARD" && paymentServiceTypeEnumId == "PRDS_PAY_CAPTURE" ) 
    {
        customMethodsCond = EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "GIFT_CAPTURE");
    } else if (paymentMethodTypeId == "GIFT_CARD" && paymentServiceTypeEnumId == "PRDS_PAY_REFUND" ) 
    {
        customMethodsCond = EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "GIFT_REFUND");
    } else if (paymentMethodTypeId == "GIFT_CARD" && paymentServiceTypeEnumId == "PRDS_PAY_RELEASE" ) 
    {
        customMethodsCond = EntityCondition.makeCondition("customMethodTypeId", EntityOperator.EQUALS, "GIFT_RELEASE");
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