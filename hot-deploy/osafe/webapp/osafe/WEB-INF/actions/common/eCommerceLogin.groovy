package common;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilHttp;

context.autoUserLogin = session.getAttribute("autoUserLogin");

previousParams = session.getAttribute("_PREVIOUS_PARAMS_");
if (UtilValidate.isNotEmpty(previousParams)) 
{
    previousParams = UtilHttp.stripNamedParamsFromQueryString(previousParams, ["USERNAME", "PASSWORD"]);
    previousParams = "?" + previousParams;
} else 
{
    previousParams = "";
}
context.previousParams = previousParams;

if (UtilValidate.isNotEmpty(parameters.review) && "review".equals(parameters.review)) 
{
    context.infoMessage = UtilProperties.getMessage("OSafeUiLabels","ReviewLoginInfo", locale );
}