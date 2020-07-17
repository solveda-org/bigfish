package paymentmethod;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import com.osafe.util.Util;
import org.ofbiz.entity.GenericValue;
import org.apache.commons.lang.StringUtils;

userLogin = session.getAttribute("userLogin");
paymentMethodId = StringUtils.trimToEmpty(requestParameters.get("paymentMethodId"));

partySecurityCheck="N";
if(UtilValidate.isNotEmpty(paymentMethodId))
{
	if(UtilValidate.isNotEmpty(userLogin))
	{
	    partyId = userLogin.partyId;
	}
	if(UtilValidate.isNotEmpty(partyId))
	{
		fieldMap = UtilMisc.toMap("partyId", partyId,"paymentMethodId",paymentMethodId);
		if(UtilValidate.isNotEmpty(context.paymentMethodType))
		{
    		fieldMap.put("paymentMethodTypeId",context.paymentMethodType);
		}
	    paymentMethods = delegator.findByAndCache("PaymentMethod", fieldMap, UtilMisc.toList("lastUpdatedStamp"));
	    if(UtilValidate.isNotEmpty(paymentMethods))
	    {
	    	partySecurityCheck="Y";
	    }
	}
	
}

context.partySecurityCheck = partySecurityCheck;



