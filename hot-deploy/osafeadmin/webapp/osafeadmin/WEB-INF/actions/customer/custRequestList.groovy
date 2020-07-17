package customer;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import com.osafe.util.OsafeAdminUtil;
import java.sql.Timestamp;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import com.osafe.util.Util;


String partyId = StringUtils.trimToEmpty(parameters.partyId);
String lastName = StringUtils.trimToEmpty(parameters.lastName);
contactUsDateFrom = StringUtils.trimToEmpty(parameters.contactUsDateFrom);
contactUsDateTo = StringUtils.trimToEmpty(parameters.contactUsDateTo);
productStoreall = StringUtils.trimToEmpty(parameters.productStoreall);
String entryDateFormat = entryDateTimeFormat;
List infoMsgList = FastList.newInstance();
Boolean isValidDate = true;
String isDownloaded = "";
party=null;
if(UtilValidate.isEmpty(parameters.downloadnew) & UtilValidate.isNotEmpty(parameters.downloadloaded)) 
{
    isDownloaded = "Y";
}
if(UtilValidate.isNotEmpty(parameters.downloadnew) & UtilValidate.isEmpty(parameters.downloadloaded)) 
{
    isDownloaded = "N";
}
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

Timestamp contactUsDateFromTs = null;
Timestamp contactUsDateToTs = null;
paramsExpr = FastList.newInstance();
contactUsSearchList=FastList.newInstance();
lCustRequest=FastList.newInstance();
dateExpr= FastList.newInstance();

dateCond = null;
mainCond = EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, custRequestType);
if(UtilValidate.isNotEmpty(partyId)) 
{
    party = delegator.findByPrimaryKey("Party", [partyId : partyId]);
    mainCond = EntityCondition.makeCondition([mainCond, EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, partyId)], EntityOperator.AND);
}
if(UtilValidate.isNotEmpty(contactUsDateFrom))
{
    if(OsafeAdminUtil.isDateTime(contactUsDateFrom, entryDateTimeFormat))
    {
        contactUsDateFromTs = ObjectType.simpleTypeConvert(contactUsDateFrom, "Timestamp", entryDateFormat, locale);
        dateExpr.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, contactUsDateFromTs));
    }
    else
    {   
        infoMsgList.add(UtilProperties.getMessage("OSafeAdminUiLabels","InvalidFromDateInfo",locale));
    }
}
if(UtilValidate.isNotEmpty(contactUsDateTo))
{
    if(OsafeAdminUtil.isDateTime(contactUsDateTo, entryDateTimeFormat))
    {
        contactUsDateToTs = ObjectType.simpleTypeConvert(contactUsDateTo, "Timestamp", entryDateFormat, locale);
        contactUsDateToTs = UtilDateTime.getDayEnd(contactUsDateToTs);
        dateExpr.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, contactUsDateToTs));
    }
    else
    {   
        infoMsgList.add(UtilProperties.getMessage("OSafeAdminUiLabels","InvalidToDateInfo",locale));
    }
}
if(UtilValidate.isNotEmpty(infoMsgList))
{
	isValidDate = false;
	request.setAttribute("_INFO_MESSAGE_LIST_", infoMsgList);
}
if(UtilValidate.isNotEmpty(dateExpr))
{
    dateCond = EntityCondition.makeCondition(dateExpr, EntityOperator.AND);
}

if (dateCond)
{
    if (mainCond) 
    {
        mainCond = EntityCondition.makeCondition([mainCond, dateCond], EntityOperator.AND);
    } else 
    {
        mainCond=dateCond;
    }
}

orderBy = ["lastName"];
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N" && UtilValidate.isNotEmpty(mainCond)&& isValidDate) 
{
	if (UtilValidate.isEmpty(productStoreall))
	{
	    mainCond = EntityCondition.makeCondition([mainCond, EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId)], EntityOperator.AND);
	}
    lCustRequest = delegator.findList("CustRequest",mainCond, null, null, null, false);
    if (UtilValidate.isNotEmpty(lCustRequest))
    {
        for (GenericValue custRequest: lCustRequest) 
        {
            custRequestAttrList = custRequest.getRelated("CustRequestAttribute");
            if (UtilValidate.isNotEmpty(isDownloaded) || UtilValidate.isNotEmpty(lastName))
            {
               custRequestAttrMap = FastMap.newInstance();
               if (UtilValidate.isNotEmpty(custRequestAttrList))
               {
                  attrlIter = custRequestAttrList.iterator();
                  while (attrlIter.hasNext()) 
                  {
                      attr = (GenericValue) attrlIter.next();
                      custRequestAttrMap.put(attr.getString("attrName"),attr.getString("attrValue"));
                  }
                  
                    attrLastName =custRequestAttrMap.get("LAST_NAME");
                    if (UtilValidate.isNotEmpty(attrLastName))
                 {
                    attrLastName = attrLastName.toUpperCase();
                 }
                    attrDownloaded =custRequestAttrMap.get("IS_DOWNLOADED");
                 if (UtilValidate.isNotEmpty(isDownloaded) && UtilValidate.isNotEmpty(lastName))
                 {
                    if (isDownloaded.equals(attrDownloaded) && attrLastName.contains(lastName.toUpperCase()))
                    {
                        contactUsSearchList.add(UtilMisc.toMap("CustRequest", custRequest,"CustRequestAttributeList", custRequestAttrList));
                    }
                 }else if (UtilValidate.isNotEmpty(lastName))
                 {
                      if (attrLastName.contains(lastName.toUpperCase()))
                    {
                        contactUsSearchList.add(UtilMisc.toMap("CustRequest", custRequest,"CustRequestAttributeList", custRequestAttrList));
                    }
                 }else if (UtilValidate.isNotEmpty(isDownloaded))
                 {
                     if (isDownloaded.equals(attrDownloaded))
                    {
                        contactUsSearchList.add(UtilMisc.toMap("CustRequest", custRequest,"CustRequestAttributeList", custRequestAttrList));
                    }
                     
                 }
               }
            }
            else
            {
                contactUsSearchList.add(UtilMisc.toMap("CustRequest", custRequest,"CustRequestAttributeList", custRequestAttrList));
            }
        }
    }
}
pagingListSize=contactUsSearchList.size();
if (UtilValidate.isNotEmpty(party) && pagingListSize > 0)
 {
        context.party=party;
 }

context.pagingListSize=pagingListSize;
context.pagingList = contactUsSearchList;
session.setAttribute("custRequestList", contactUsSearchList);


