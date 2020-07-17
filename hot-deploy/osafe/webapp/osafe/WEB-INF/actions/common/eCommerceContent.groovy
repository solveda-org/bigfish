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
    xContentXref = delegator.findByPrimaryKeyCache("XContentXref", [bfContentId : context.contentId, productStoreId : context.productStoreId]);
    if (UtilValidate.isNotEmpty(xContentXref))
    {
        content = xContentXref.getRelatedOneCache("Content");
        context.content = content;
    }
    else
    {
    	context.content = "";
    }
}
