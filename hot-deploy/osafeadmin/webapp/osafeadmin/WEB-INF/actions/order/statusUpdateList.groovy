package order;

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




orderBy = ["orderStatusId"];
// Paging variables
viewIndex = Integer.valueOf(parameters.viewIndex  ?: 1);
viewSize = Integer.valueOf(parameters.viewSize ?: UtilProperties.getPropertyValue("osafeAdmin", "default-view-size"));
context.viewIndex = viewIndex;
context.viewSize = viewSize;

Map<String, Object> svcCtx = FastMap.newInstance();
userLogin = session.getAttribute("userLogin");
svcCtx.put("userLogin", userLogin);



orderId = StringUtils.trimToEmpty(parameters.orderId);
exprs = FastList.newInstance();
mainCond=null;

// order Id
if(orderId)
{
	exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("orderId"), EntityOperator.EQUALS, orderId.toUpperCase()));
	context.orderId=orderId;
}

if (UtilValidate.isNotEmpty(exprs)) {
	mainCond=EntityCondition.makeCondition(exprs, EntityOperator.AND);
}

contentList = delegator.findList("OrderStatus",mainCond, null, orderBy, null, false);
context.resultList = contentList;