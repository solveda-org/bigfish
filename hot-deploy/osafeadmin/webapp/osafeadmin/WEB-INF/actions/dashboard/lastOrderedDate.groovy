package dashboard;

import org.ofbiz.base.util.ObjectType;

import java.util.Locale;
import java.util.TimeZone;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import com.osafe.util.OsafeAdminUtil;

import com.ibm.icu.util.Calendar;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression.Context;

orderStatusIncDashboard = globalContext.get("ORDER_STATUS_INC_DASHBOARD");
List includedOrderStatusList = FastList.newInstance();
if(UtilValidate.isNotEmpty(orderStatusIncDashboard))
{
    orderStatusIncDashboardList = StringUtil.split(orderStatusIncDashboard,",")
	for (String orderStatus : orderStatusIncDashboardList) 
	{
	    includedOrderStatusList.add(orderStatus.trim());
	}
}

// Last Order
ecl = EntityCondition.makeCondition([
        EntityCondition.makeCondition("statusId", EntityOperator.IN, includedOrderStatusList),
        EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER")
],
EntityOperator.AND);

List orderBy = UtilMisc.toList("-orderDate");

List lastOrderHeaders = delegator.findList("OrderHeader", ecl, null, orderBy, null, false);
if (UtilValidate.isNotEmpty(lastOrderHeaders))
{
    GenericValue lastOrder = EntityUtil.getFirst(lastOrderHeaders);
    context.lastOrderAmount = lastOrder.grandTotal ?: BigDecimal.ZERO;
    context.lastOrderDateTimeString = OsafeAdminUtil.convertDateTimeFormat(lastOrder.orderDate, preferredDateTimeFormat);
}
