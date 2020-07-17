package order;

import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastList;
import javolution.util.FastMap;


orderItemShipGroup = request.getAttribute("orderItemShipGroup");
orderItemShipGroupAssoc = orderItemShipGroup.getRelated("OrderItemShipGroupAssoc");

context.orderItemShipGroup = orderItemShipGroup;
context.orderItemShipGroupAssoc = orderItemShipGroupAssoc;
