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
// Paging variables
viewIndex = Integer.valueOf(parameters.viewIndex  ?: 1);
viewSize = Integer.valueOf(parameters.viewSize ?: UtilProperties.getPropertyValue("osafeAdmin", "default-view-size"));
context.viewIndex = viewIndex;
context.viewSize = viewSize;

Map<String, Object> svcCtx = FastMap.newInstance();
userLogin = session.getAttribute("userLogin");
svcCtx.put("userLogin", userLogin);

context.userLoginId = userLogin.userLoginId;
contentList = FastList.newInstance();
contentTypeId=context.contentTypeId;
if(UtilValidate.isNotEmpty(contentTypeId))
{
    List conds = FastList.newInstance();
    conds.add(EntityCondition.makeCondition([contentTypeId : contentTypeId]));
    conds.add(EntityCondition.makeCondition([productStoreId : productStoreId]));
    contentList = delegator.findList("XContentXref",EntityCondition.makeCondition(conds, EntityOperator.AND), null, orderBy, null, false);
    context.resultList = contentList;
}
context.resultList = contentList;
