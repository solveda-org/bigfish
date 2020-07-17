
package order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;


import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.ObjectType;

orderId = StringUtils.trimToEmpty(parameters.orderId);
orderDateFrom = StringUtils.trimToEmpty(parameters.orderDateFrom);
orderDateTo = StringUtils.trimToEmpty(parameters.orderDateTo);
partyId = StringUtils.trimToEmpty(parameters.partyId);
srchShipTo = StringUtils.trimToEmpty(parameters.srchShipTo);
srchStorePickup = StringUtils.trimToEmpty(parameters.srchStorePickup);
orderEmail = parameters.orderEmail;
session = context.session;
statusId = StringUtils.trimToEmpty(parameters.statusId);
productPromoCodeId = StringUtils.trimToEmpty(parameters.productPromoCodeId);
viewCompleted = StringUtils.trimToEmpty(parameters.viewcompleted);
viewCancelled = StringUtils.trimToEmpty(parameters.viewcancelled);
viewApproved = StringUtils.trimToEmpty(parameters.viewapproved);
viewSent = StringUtils.trimToEmpty(parameters.viewsent);
viewCreated = StringUtils.trimToEmpty(parameters.viewcreated);
viewRejected = StringUtils.trimToEmpty(parameters.viewrejected);
viewHold = StringUtils.trimToEmpty(parameters.viewhold);
initializedCB = StringUtils.trimToEmpty(parameters.initializedCB);
preRetrieved = StringUtils.trimToEmpty(parameters.preRetrieved);

if (UtilValidate.isNotEmpty(preRetrieved))
{
   context.preRetrieved=preRetrieved;
}
else
{
   preRetrieved = context.preRetrieved;
}

if (UtilValidate.isNotEmpty(initializedCB))
{
   context.initializedCB=initializedCB;
}

// Paging variables
viewIndex = Integer.valueOf(parameters.viewIndex  ?: 1);
viewSize = Integer.valueOf(parameters.viewSize ?: UtilProperties.getPropertyValue("osafeAdmin", "default-view-size"));

// Order List
Map<String, Object> svcCtx = FastMap.newInstance();
userLogin = session.getAttribute("userLogin");
svcCtx.put("userLogin", userLogin);
List<String> lProductStoreId = FastList.newInstance();
lProductStoreId.add(globalContext.productStoreId);
svcCtx.put("productStoreId",lProductStoreId);

if(orderId){
    svcCtx.put("orderId", orderId.toUpperCase());
}

if(orderDateFrom){
    try {
          orderDateFrom = ObjectType.simpleTypeConvert(orderDateFrom, "Timestamp", preferredDateFormat, locale);
    } catch (Exception e) {
        errMsg = "Parse Exception orderDateFrom: " + orderDateFrom;
        Debug.logError(e, errMsg, "orderManagementOrderList.groovy");
    }
    svcCtx.put("minDate", orderDateFrom.toString());
}

if(productPromoCodeId){
    svcCtx.put("productPromoCodeId", productPromoCodeId);
}

if(orderDateTo){
    try{
         orderDateTo = ObjectType.simpleTypeConvert(orderDateTo, "Timestamp", preferredDateFormat, locale);
	} catch (Exception e) {
        errMsg = "Parse Exception orderDateTo: " + orderDateTo;
        Debug.logError(e, errMsg, "orderManagementOrderList.groovy");
    }
    svcCtx.put("maxDate", orderDateTo.toString());
}

if(partyId){
    svcCtx.put("partyId", partyId);
}

if (UtilValidate.isNotEmpty(srchShipTo)) {
    svcCtx.put("attrName", "DELIVERY_OPTION");
    svcCtx.put("attrValue", "SHIP_TO");
}
if (UtilValidate.isNotEmpty(srchStorePickup)) {
    svcCtx.put("attrName", "DELIVERY_OPTION");
    svcCtx.put("attrValue", "STORE_PICKUP");
}

List orderContactMechIds = FastList.newInstance();
if (orderEmail) {
	context.orderEmail = orderEmail;
    contactMechs = delegator.findByAnd("PartyContactWithPurpose", [infoString : orderEmail, contactMechTypeId : "EMAIL_ADDRESS", contactMechPurposeTypeId : "PRIMARY_EMAIL"]);
    if (UtilValidate.isNotEmpty(contactMechs)) 
	{
		for(GenericValue contactMech : contactMechs)
		{
			orderContactMechIds.add(contactMech.contactMechId);
		}
		svcCtx.put("orderContactMechIds", orderContactMechIds);
    } else
	{
		//if no contactMechs are found, add a dummy Id of '0' so that no results will be displayed
		orderContactMechIds.add("0");
        svcCtx.put("orderContactMechIds", orderContactMechIds);
    }
}

if(statusId) {
    if("ORDER_COMPLETED".equals(statusId)) {
        viewCompleted=statusId;
    }
    if("ORDER_CANCELLED".equals(statusId)) {
        viewCancelled=statusId;
    }
    if("ORDER_REJECTED".equals(statusId)) {
        viewRejected=statusId;
    }
    if("ORDER_APPROVED".equals(statusId)) {
        viewApproved=statusId;
    }
    if("ORDER_SENT".equals(statusId)) {
        viewSent=statusId;
    }
    if("ORDER_CREATED".equals(statusId)) {
        viewCreated=statusId;
    }
    if("ORDER_HOLD".equals(statusId)) {
        viewHold=statusId;
    }
}


List<String> orderStatusIds = FastList.newInstance();
if(viewCompleted) {
    orderStatusIds.add("ORDER_COMPLETED");
    context.viewcompleted=viewCompleted;
}
if(viewCancelled) {
    orderStatusIds.add("ORDER_CANCELLED");
    context.viewcompleted=viewCancelled;
}
if(viewRejected) {
    orderStatusIds.add("ORDER_REJECTED");
    context.viewrejected=viewRejected;
}
if(viewApproved) {
    orderStatusIds.add("ORDER_APPROVED");
    context.viewapproved=viewApproved;
}
if(viewSent) {
    orderStatusIds.add("ORDER_SENT");
    context.viewsent=viewSent;
}
if(viewCreated) {
    orderStatusIds.add("ORDER_CREATED");
    context.viewcreated=viewCreated;
}
if(viewHold) {
    orderStatusIds.add("ORDER_HOLD");
    context.viewhold=viewHold;
}
if(UtilValidate.isNotEmpty(orderStatusIds)) {
    svcCtx.put("orderStatusId", orderStatusIds);
}
if(UtilValidate.isEmpty(parameters.downloadnew) & UtilValidate.isNotEmpty(parameters.downloadloaded)) {
    svcCtx.put("isDownloaded", "Y");
}
if(UtilValidate.isNotEmpty(parameters.downloadnew) & UtilValidate.isEmpty(parameters.downloadloaded)) {
    svcCtx.put("isDownloaded", "N");
}

if(UtilValidate.isEmpty(globalContext.previousDisplay) || globalContext.previousDisplay == "FALSE") {
    globalContext.previousDisplay = request.getParameter("enterCriteriaInfo") ?: "FALSE";
}

svcCtx.put("viewIndex", viewIndex);
svcCtx.put("viewSize", viewSize);
svcCtx.put("showAll", "N");

Map<String, Object> svcRes;

List<GenericValue> orderList = FastList.newInstance();

if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N") {
     svcRes = dispatcher.runSync("searchOrders", svcCtx);

     orderList = UtilGenerics.checkList(svcRes.get("orderList"), GenericValue.class);
     orderList = EntityUtil.orderBy(orderList,["orderId"]);

     session.setAttribute("orderPDFMap", svcCtx);

     context.pagingList = svcRes.get("completeOrderList");
     context.pagingListSize = svcRes.get("orderListSize");
}
else
{
     context.pagingList = orderList;
     context.pagingListSize = orderList.size();
}
