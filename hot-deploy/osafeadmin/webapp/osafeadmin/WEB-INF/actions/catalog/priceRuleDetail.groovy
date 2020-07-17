package catalog;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil
import org.ofbiz.base.util.UtilValidate;

priceRuleId = StringUtils.trimToEmpty(parameters.priceRuleId);

if (UtilValidate.isNotEmpty(priceRuleId))
{
   context.productPriceRule = delegator.findOne("ProductPriceRule", [productPriceRuleId : priceRuleId], false);

   findProductPriceCondMap = ["productPriceRuleId" : priceRuleId];
   context.productPriceCondList = delegator.findByAnd("ProductPriceCond", findProductPriceCondMap, ["productPriceCondSeqId"]);

   findProductPriceActionMap = ["productPriceRuleId" : priceRuleId];
   productPriceActionList = delegator.findByAnd("ProductPriceAction", findProductPriceActionMap, ["productPriceActionSeqId"]);
   if (UtilValidate.isNotEmpty(productPriceActionList))
   {
       context.productPriceAction = EntityUtil.getFirst(productPriceActionList);
   }
}

context.inputParamEnums = delegator.findList("Enumeration", EntityCondition.makeCondition([enumTypeId : 'PROD_PRICE_IN_PARAM']), null, ['sequenceId'], null, false);

context.condOperEnums = delegator.findList("Enumeration", EntityCondition.makeCondition([enumTypeId : 'PROD_PRICE_COND']), null, ['sequenceId'], null, false);

context.productPriceActionTypes = delegator.findList("ProductPriceActionType", null, null, ['description'], null, false);