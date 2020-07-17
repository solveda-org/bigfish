package common;

import com.osafe.services.bluedart.BlueDartServices;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilValidate;
import com.osafe.util.Util;
import java.math.BigDecimal;

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
String deliveryAvailable = "";
BigDecimal codLimit = BigDecimal.ZERO;

if(UtilValidate.isNotEmpty(pincode))
{
	deliveryAvailable = BlueDartServices.getBlueDartDeliveryAvailablity(pincode);
	if(UtilValidate.isNotEmpty(deliveryAvailable))
	{
		codLimit = BlueDartServices.getBlueDartCODLimit(pincode);
	}
	context.pincode = pincode;
}
if(UtilValidate.isNotEmpty(deliveryAvailable))
{
	context.deliveryAvailable = deliveryAvailable;
}
if(UtilValidate.isNotEmpty(codLimit))
{
	context.codLimit = codLimit;
}

