package order;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*;

userLogin = session.getAttribute("userLogin");
orderId = StringUtils.trimToEmpty(parameters.orderId);
if(orderId && security.hasEntityPermission('SPER_ORDER_MGMT', '_VIEW', session))
{
    messageMap=[:];
    messageMap.put("orderId", orderId);

    context.orderId=orderId;
    context.pageTitle = UtilProperties.getMessage("OSafeAdminUiLabels","OrderStatusDetailTitle",messageMap, locale )
    context.generalInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","OrderDetailInfoHeading",messageMap, locale )
}
conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, "ORDER_STATUS"));
conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, ["ORDER_APPROVED", "ORDER_CANCELLED","ORDER_COMPLETED"]));
mainCond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
statusItems = delegator.findList("StatusItem", mainCond, null, ["sequenceId"], null, false);
context.statusItems = statusItems;


//is it a store pickup?
storeId = "";
orderDeliveryOptionAttr = delegator.findOne("OrderAttribute", [orderId : orderHeader.orderId, attrName : "DELIVERY_OPTION"], false);
if (UtilValidate.isNotEmpty(orderDeliveryOptionAttr) && orderDeliveryOptionAttr.attrValue == "STORE_PICKUP") 
{
	context.isStorePickup = "Y";
	orderStoreLocationAttr = delegator.findOne("OrderAttribute", [orderId : orderHeader.orderId, attrName : "STORE_LOCATION"], false);
	if (UtilValidate.isNotEmpty(orderStoreLocationAttr)) 
	{
		storeId = orderStoreLocationAttr.attrValue;
	}
}