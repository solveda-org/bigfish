package common;

import com.osafe.services.bluedart.BlueDartServices;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilProperties;
import com.osafe.util.Util;
import java.math.BigDecimal;
import org.ofbiz.service.LocalDispatcher;
import javolution.util.FastMap;

CURRENCY_UOM_DEFAULT = Util.getProductStoreParm(request,"CURRENCY_UOM_DEFAULT");
context.currencyUom = CURRENCY_UOM_DEFAULT;

currencyRounding=2;
roundCurrency = Util.getProductStoreParm(request,"CURRENCY_UOM_ROUNDING");
if (UtilValidate.isNotEmpty(roundCurrency) && Util.isNumber(roundCurrency))
{
	currencyRounding = Integer.parseInt(roundCurrency);
}
context.currencyRounding = currencyRounding;

String pincode = StringUtils.trimToEmpty(parameters.pincode);
String isValidPinCode = StringUtils.trimToEmpty(parameters.isValidPinCode);
String deliveryAvailable = "";
BigDecimal codLimit = BigDecimal.ZERO;
Map<String, Object> svcCtx = FastMap.newInstance();
if(UtilValidate.isNotEmpty(pincode))
{
	svcCtx.put("pincode", pincode);
	checkDeliverySvcRes = dispatcher.runSync("checkDelivery", svcCtx);
	deliveryAvailable = (String)checkDeliverySvcRes.get("deliveryAvailable");
	if(UtilValidate.isNotEmpty(deliveryAvailable))
	{
		checkCodLimitSvcRes = dispatcher.runSync("checkCodLimit", svcCtx);
		codLimit = (BigDecimal)checkCodLimitSvcRes.get("codLimit");
	}
	context.pincode = pincode;
}
if(UtilValidate.isNotEmpty(deliveryAvailable))
{
	context.deliveryAvailable = deliveryAvailable;
	 if (deliveryAvailable == "N" && isValidPinCode == "Y")
	 {
		request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("OSafeUiLabels", "DeliveryNotAvailableInfo", locale));
		return "error";
	 }
}
if(UtilValidate.isNotEmpty(codLimit))
{
	context.codLimit = codLimit;
}

