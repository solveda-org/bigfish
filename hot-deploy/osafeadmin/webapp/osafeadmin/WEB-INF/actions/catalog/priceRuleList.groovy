package catalog;

import javolution.util.FastList;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;

srchPriceRuleName = StringUtils.trimToEmpty(parameters.srchPriceRuleName);
initializedCB = StringUtils.trimToEmpty(parameters.initializedCB);
preRetrieved = StringUtils.trimToEmpty(parameters.preRetrieved);

if (UtilValidate.isNotEmpty(preRetrieved))
{
   context.preRetrieved=preRetrieved;
}
else
{
  preRetrieved = context.preRetrieved;
}

if (UtilValidate.isNotEmpty(initializedCB))
{
   context.initializedCB=initializedCB;
}

paramsExpr = FastList.newInstance();
if (UtilValidate.isNotEmpty(srchPriceRuleName))
{
    paramsExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("ruleName"),
            EntityOperator.LIKE, EntityFunction.UPPER("%"+srchPriceRuleName+"%")));
    context.srchPriceRuleName=srchPriceRuleName;
}
orderBy = ["fromDate"];
List<GenericValue> priceRuleList = FastList.newInstance();

if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N")
{
    priceRuleList = delegator.findList("ProductPriceRule", EntityCondition.makeCondition(paramsExpr, EntityOperator.AND), null, orderBy, null, false);
}

context.pagingList = priceRuleList;
context.pagingListSize = priceRuleList.size();