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

String lastName = StringUtils.trimToEmpty(parameters.lastName);
contactUsDateFrom = StringUtils.trimToEmpty(parameters.contactUsDateFrom);
contactUsDateTo = StringUtils.trimToEmpty(parameters.contactUsDateTo);
String entryDateFormat = preferredDateFormat;
String isDownloaded = "";
if(UtilValidate.isEmpty(parameters.downloadnew) & UtilValidate.isNotEmpty(parameters.downloadloaded)) {
    isDownloaded = "Y";
}
if(UtilValidate.isNotEmpty(parameters.downloadnew) & UtilValidate.isEmpty(parameters.downloadloaded)) {
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

attrExpr = FastList.newInstance();
attrNameExpr = FastList.newInstance();
attrExportExpr = FastList.newInstance();
custReqAttrList = FastList.newInstance();

if(UtilValidate.isNotEmpty(lastName)){
    attrNameExpr.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "LAST_NAME"));
    attrNameExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("attrValue"),
            EntityOperator.LIKE, "%"+lastName.toUpperCase()+"%"));
    custReqAttrList = delegator.findList("CustRequestAttribute",EntityCondition.makeCondition(attrNameExpr, EntityOperator.AND), null, null, null, false);
}

if(UtilValidate.isNotEmpty(isDownloaded)){
    attrExportExpr.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "IS_DOWNLOADED"));
    attrExportExpr.add(EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, isDownloaded));
    if(UtilValidate.isNotEmpty(custReqAttrList)) {
        custRequestIdList = EntityUtil.getFieldListFromEntityList(custReqAttrList, "custRequestId", true);
        attrExportExpr.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, custRequestIdList));
    }
    custReqAttrList = delegator.findList("CustRequestAttribute",EntityCondition.makeCondition(attrExportExpr, EntityOperator.AND), null, null, null, false);
} else if (UtilValidate.isEmpty(lastName) && UtilValidate.isNotEmpty(parameters.downloadall)) {
    custReqAttrList = delegator.findList("CustRequestAttribute",null, null, null, null, false);
}

paramsExpr = FastList.newInstance();
contactUsSearchList=FastList.newInstance();
dateExpr= FastList.newInstance();
dateCond = null;
mainCond = null;

if(UtilValidate.isNotEmpty(custReqAttrList)) {
    custRequestIdList = EntityUtil.getFieldListFromEntityList(custReqAttrList, "custRequestId", true);
    paramsExpr.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, custRequestIdList));
    mainCond=EntityCondition.makeCondition(paramsExpr, EntityOperator.AND);
}

if(UtilValidate.isNotEmpty(contactUsDateFrom)){
    contactUsDateFromTs = ObjectType.simpleTypeConvert(contactUsDateFrom, "Timestamp", entryDateFormat, locale);
    dateExpr.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, contactUsDateFromTs));
}
if(UtilValidate.isNotEmpty(contactUsDateTo)){
    contactUsDateToTs = ObjectType.simpleTypeConvert(contactUsDateTo, "Timestamp", entryDateFormat, locale);
    contactUsDateToTs = UtilDateTime.getDayEnd(contactUsDateToTs);
    dateExpr.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, contactUsDateToTs));
}
if(UtilValidate.isNotEmpty(dateExpr))
{
    dateCond = EntityCondition.makeCondition(dateExpr, EntityOperator.AND);
}

if (dateCond)
{
    if (mainCond) {
        mainCond = EntityCondition.makeCondition([mainCond, dateCond], EntityOperator.AND);
    } else {
        mainCond=dateCond;
    }
}

orderBy = ["lastName"];
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N" && UtilValidate.isNotEmpty(mainCond)) 
{
    mainCond = EntityCondition.makeCondition([mainCond, EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, custRequestType)], EntityOperator.AND);
    contactUsSearchList = delegator.findList("CustRequest",mainCond, null, null, null, false);
    session.setAttribute("custRequestCond", mainCond);
}
pagingListSize=contactUsSearchList.size();
context.pagingListSize=pagingListSize;
context.pagingList = contactUsSearchList;
