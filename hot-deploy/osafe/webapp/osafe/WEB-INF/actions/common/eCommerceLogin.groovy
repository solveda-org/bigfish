package common;

import org.ofbiz.base.util.*;
import org.ofbiz.common.CommonWorkers;
import org.ofbiz.webapp.control.*;

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
    messageMap=[:];
    context.infoMessage = UtilProperties.getMessage("OSafeUiLabels","ReviewLoginInfo",messageMap, locale );
}