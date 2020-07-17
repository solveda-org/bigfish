/* 
 * This is the groovy script that provide the previous
 * search criteria for all the list page and must be 
 * add before the list groovy
*/
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilGenerics;
import java.util.Map;

if (UtilValidate.isNotEmpty(context.searchCriteriaKey)) {
    if ((UtilValidate.isNotEmpty(parameters.osafeSuccessMessageList) || UtilValidate.isNotEmpty(parameters.backActionFlag)) && UtilValidate.isNotEmpty(session.getAttribute(searchCriteriaKey))) {
        previousParamMap = UtilGenerics.checkMap(session.getAttribute(searchCriteriaKey), String.class, Object.class);
        for (previousParamEntry in previousParamMap.entrySet()) parameters.put(previousParamEntry.getKey(), previousParamEntry.getValue());
    } else {
        session.setAttribute(searchCriteriaKey, UtilHttp.getParameterMap(request));
    }
}