
package order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;


import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.StringUtil;
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
import org.ofbiz.base.util.UtilMisc;

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

initializedCB = StringUtils.trimToEmpty(parameters.initializedCB);
preRetrieved = StringUtils.trimToEmpty(parameters.preRetrieved);


List<String> orderStatusIds = FastList.newInstance();
orderStatusIncSearch = globalContext.get("ORDER_STATUS_INC_SEARCH");

if(UtilValidate.isNotEmpty(orderStatusIncSearch))
{
    orderStatusIncSearchList = StringUtil.split(orderStatusIncSearch, ",");
	for (String orderStatus : orderStatusIncSearchList) 
	{
	    statusItem = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", orderStatus.trim()));
	    statusDescription = "";
	    if(UtilValidate.isNotEmpty(statusItem))
	    {
	        statusDescription = statusItem.description;
	    }
	    context.put('view'+statusDescription.toLowerCase(), StringUtils.trimToEmpty(parameters.get('view'+statusDescription.toLowerCase())));
	    if(UtilValidate.isNotEmpty(statusId)) 
	    {
	        if((orderStatus.trim()).equals(statusId)) 
	        {
	            context.put('view'+statusDescription.toLowerCase(), statusId);
         	}
        }
	    if(UtilValidate.isNotEmpty(context.get('view'+statusDescription.toLowerCase())))
	    { 
	        orderStatusIds.add(orderStatus.trim());
	    }
    }
}

//if the statusId is not defined in the system param, but a specific status is being searched
if (UtilValidate.isEmpty(context.viewcreated))
{
	if(UtilValidate.isNotEmpty(statusId))
	{
		if("ORDER_CREATED".equals(statusId))
		{
			orderStatusIds.add(statusId);
		}
	}	
}

if (UtilValidate.isEmpty(context.viewapproved))
{
	if(UtilValidate.isNotEmpty(statusId))
	{
		if("ORDER_APPROVED".equals(statusId))
		{
			orderStatusIds.add(statusId);
		}
	}	
}

if (UtilValidate.isEmpty(context.viewprocessing))
{
	if(UtilValidate.isNotEmpty(statusId))
	{
		if("ORDER_PROCESSING".equals(statusId))
		{
			orderStatusIds.add(statusId);
		}
	}
}

if (UtilValidate.isEmpty(context.viewsent))
{
	if(UtilValidate.isNotEmpty(statusId))
	{
		if("ORDER_SENT".equals(statusId))
		{
			orderStatusIds.add(statusId);
		}
	}
}

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

if(UtilValidate.isNotEmpty(orderId))
{
    svcCtx.put("orderId", orderId.toUpperCase());
}

if(UtilValidate.isNotEmpty(orderDateFrom))
{
    try 
    {
          orderDateFrom = ObjectType.simpleTypeConvert(orderDateFrom, "Timestamp", preferredDateFormat, locale);
    } catch (Exception e) 
    {
        errMsg = "Parse Exception orderDateFrom: " + orderDateFrom;
        Debug.logError(e, errMsg, "orderManagementOrderList.groovy");
    }
    svcCtx.put("minDate", orderDateFrom.toString());
}

if(UtilValidate.isNotEmpty(productPromoCodeId))
{
    svcCtx.put("productPromoCodeId", productPromoCodeId);
}

if(UtilValidate.isNotEmpty(orderDateTo))
{
    try
    {
         orderDateTo = ObjectType.simpleTypeConvert(orderDateTo, "Timestamp", preferredDateFormat, locale);
	} 
	catch (Exception e) 
	{
        errMsg = "Parse Exception orderDateTo: " + orderDateTo;
        Debug.logError(e, errMsg, "orderManagementOrderList.groovy");
    }
    svcCtx.put("maxDate", orderDateTo.toString());
}

if(UtilValidate.isNotEmpty(partyId))
{
    svcCtx.put("partyId", partyId);
}

if (UtilValidate.isNotEmpty(srchShipTo)) 
{
    svcCtx.put("attrName", "DELIVERY_OPTION");
    svcCtx.put("attrValue", "SHIP_TO");
}
if (UtilValidate.isNotEmpty(srchStorePickup)) 
{
    svcCtx.put("attrName", "DELIVERY_OPTION");
    svcCtx.put("attrValue", "STORE_PICKUP");
}

List orderContactMechIds = FastList.newInstance();
if (UtilValidate.isNotEmpty(orderEmail)) 
{
	context.orderEmail = orderEmail;
    contactMechs = delegator.findByAnd("PartyContactWithPurpose", [infoString : orderEmail, contactMechTypeId : "EMAIL_ADDRESS", contactMechPurposeTypeId : "PRIMARY_EMAIL"]);
    if (UtilValidate.isNotEmpty(contactMechs)) 
	{
		for(GenericValue contactMech : contactMechs)
		{
			orderContactMechIds.add(contactMech.contactMechId);
		}
		svcCtx.put("orderContactMechIds", orderContactMechIds);
    } 
    else
	{
		//if no contactMechs are found, add a dummy Id of '0' so that no results will be displayed
		orderContactMechIds.add("0");
        svcCtx.put("orderContactMechIds", orderContactMechIds);
    }
}


if(UtilValidate.isNotEmpty(orderStatusIds)) 
{
    svcCtx.put("orderStatusId", orderStatusIds);
}
if(UtilValidate.isEmpty(parameters.downloadnew) & UtilValidate.isNotEmpty(parameters.downloadloaded)) 
{
    svcCtx.put("isDownloaded", "Y");
}
if(UtilValidate.isNotEmpty(parameters.downloadnew) & UtilValidate.isEmpty(parameters.downloadloaded)) 
{
    svcCtx.put("isDownloaded", "N");
}

if(UtilValidate.isEmpty(globalContext.previousDisplay) || globalContext.previousDisplay == "FALSE") 
{
    globalContext.previousDisplay = request.getParameter("enterCriteriaInfo") ?: "FALSE";
}

svcCtx.put("viewIndex", viewIndex);
svcCtx.put("viewSize", viewSize);
svcCtx.put("showAll", "N");

Map<String, Object> svcRes;

List<GenericValue> orderList = FastList.newInstance();

if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N") 
{
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
