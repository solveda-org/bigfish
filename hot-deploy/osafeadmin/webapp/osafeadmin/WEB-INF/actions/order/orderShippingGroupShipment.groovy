package order;

import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastList;
import javolution.util.FastMap;

orderItemShipGroupAssoc = FastList.newInstance();
orderItemShipGroup = request.getAttribute("orderItemShipGroup");
if (UtilValidate.isNotEmpty(orderItemShipGroup))
{
	orderItemShipGroupAssoc = orderItemShipGroup.getRelated("OrderItemShipGroupAssoc");
	
}

context.orderItemShipGroup = orderItemShipGroup;
context.orderItemShipGroupAssoc = orderItemShipGroupAssoc;
