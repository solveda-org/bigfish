import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import java.sql.Timestamp;

import java.util.*;
import org.ofbiz.product.store.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.apache.commons.lang.StringUtils;

productStoreId = ProductStoreWorker.getProductStoreId(request);
String orderId = StringUtils.trimToEmpty(parameters.orderId);

customerOrderList = delegator.findByAndCache("OrderHeaderAndRoles", UtilMisc.toMap("roleTypeId","PLACING_CUSTOMER", "partyId",userLogin.partyId), UtilMisc.toList("-orderDate"));
context.customerOrderList = customerOrderList;
customerOrderIdList = EntityUtil.getFieldListFromEntityList(customerOrderList, "orderId", true);	

prodCond = FastList.newInstance();
paramsExpr = FastList.newInstance();

if (UtilValidate.isNotEmpty(orderId))
{
	paramsExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("orderId"), EntityOperator.EQUALS, orderId.toUpperCase()));
	context.orderId = orderId;
}
else
{
	//make all orderIds uppercase
	customerOrderIdListUpper = FastList.newInstance();
	if(UtilValidate.isNotEmpty(customerOrderIdList))
	{
		for(Object customerOrderId : customerOrderIdList)
		{
			customerOrderIdListUpper.add(customerOrderId.toUpperCase());
		}
	}
	paramsExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("orderId"), EntityOperator.IN, customerOrderIdListUpper));
}
List orderedItemsList = FastList.newInstance();
if (UtilValidate.isNotEmpty(paramsExpr))
{
	mainCond = EntityCondition.makeCondition(paramsExpr, EntityOperator.AND);
	orderedItemsList = delegator.findList("OrderHeaderAndItems", mainCond, null, UtilMisc.toList("-orderDate"), null, true);
}
                                 
context.orderedItemsList = orderedItemsList;

