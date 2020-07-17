package content;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.*
import org.ofbiz.entity.util.*
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.transaction.*

import java.sql.Timestamp;

Timestamp now = UtilDateTime.nowTimestamp(); 
orderBy = ["contentId"];

contentTypeId=context.contentTypeId;
if(UtilValidate.isNotEmpty(contentTypeId))
{
    productStoreId = parameters.productStoreId?parameters.productStoreId:context.productStoreId;
    if (UtilValidate.isNotEmpty(productStoreId))
    {
        productStoreId = ProductStoreWorker.getProductStoreId(request);
    }
    if (UtilValidate.isNotEmpty(productStoreId))
    {
        List conds = FastList.newInstance();
        conds.add(EntityCondition.makeCondition([contentTypeId : contentTypeId]));
        conds.add(EntityCondition.makeCondition([productStoreId : ProductStoreWorker.getProductStoreId(request)]));
        xContentXrefList = delegator.findList("XContentXref",EntityCondition.makeCondition(conds, EntityOperator.AND), null, orderBy, null, false);
        if(UtilValidate.isNotEmpty(xContentXrefList))
        {
            contentIds = EntityUtil.getFieldListFromEntityList(xContentXrefList, "contentId", true);
            contentList = delegator.findList("Content", EntityCondition.makeCondition("contentId", EntityOperator.IN, contentIds), null, null, null, false);
            context.spotsList = contentList;
            context.contentList = contentList;
        }
    }
}

