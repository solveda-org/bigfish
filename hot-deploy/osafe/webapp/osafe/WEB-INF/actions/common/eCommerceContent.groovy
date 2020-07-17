package common;

import org.ofbiz.base.util.*;
import javolution.util.FastList;
import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.product.store.ProductStoreWorker;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

if (UtilValidate.isNotEmpty(context.contentId) && UtilValidate.isNotEmpty(context.productStoreId)) 
{
    List conds = FastList.newInstance();
    conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition([contentId : context.contentId]), EntityOperator.OR,
            EntityCondition.makeCondition([bfContentId : context.contentId])));
    conds.add(EntityCondition.makeCondition([productStoreId : context.productStoreId]));
    xContentXrefList = delegator.findList("XContentXref",EntityCondition.makeCondition(conds, EntityOperator.AND), null, null, null, false);
    if (UtilValidate.isNotEmpty(xContentXrefList))
    {
        xContentXref = EntityUtil.getFirst(xContentXrefList);
        content = delegator.findOne("Content",["contentId": xContentXref.contentId], true);
        context.content = content;
    }
}
