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

// Paging variables
viewIndex = Integer.valueOf(parameters.viewIndex  ?: 1);
viewSize = Integer.valueOf(parameters.viewSize ?: UtilProperties.getPropertyValue("osafeAdmin", "default-view-size"));
context.viewIndex = viewIndex;
context.viewSize = viewSize;

Map<String, Object> svcCtx = FastMap.newInstance();
userLogin = session.getAttribute("userLogin");
svcCtx.put("userLogin", userLogin);

//ORDERS REQUIRING WORK
orderBy = ["sequenceId"];
statusItems = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "ORDER_STATUS"), orderBy);


// For each status, count the number of orders

// All Sales Orders
ecl = EntityCondition.makeCondition([
	EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
	EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER")
],
EntityOperator.AND);

total = 0;
ordersRequiringWorkList =[];
requiringWorkHeaders = delegator.findList("OrderHeader", ecl, null, null, null, false);
for(GenericValue status : statusItems)
{
	statusIdToExclude = status.statusId;
	statusIdHeaders = EntityUtil.filterByAnd(requiringWorkHeaders, ["statusId" : statusIdToExclude]);
	headerCount = statusIdHeaders.size();
	workMap = [:];
	workMap["description"] = status.description;
	workMap["count"] = headerCount;
	workMap["statusId"] = status.statusId;
	ordersRequiringWorkList.add(workMap);
	
	total = total + headerCount;
}
context.total = total;
context.ordersRequiringWork= ordersRequiringWorkList
//ORDERS REQUIRING WORK
