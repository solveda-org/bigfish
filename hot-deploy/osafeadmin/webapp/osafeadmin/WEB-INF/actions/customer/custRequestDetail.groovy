package customer;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;

if (UtilValidate.isNotEmpty(parameters.custReqId)) {
    custRequest = delegator.findOne("CustRequest",["custRequestId":parameters.custReqId], true);
    context.custRequest = custRequest;
    paramsExpr = FastList.newInstance();
    paramsExpr.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, parameters.custReqId));
    if (UtilValidate.isNotEmpty(paramsExpr)) {
        paramCond=EntityCondition.makeCondition(paramsExpr, EntityOperator.AND);
        mainCond=paramCond;
        session.setAttribute("custRequestCond", mainCond);
    }
}
