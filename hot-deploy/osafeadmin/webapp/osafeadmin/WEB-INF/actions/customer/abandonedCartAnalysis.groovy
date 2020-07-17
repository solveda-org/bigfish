package customer;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Date;
import java.sql.Timestamp;
import org.apache.commons.lang.StringUtils;
import com.osafe.util.Util;
import com.osafe.util.OsafeAdminUtil;

fromDateShort = StringUtils.trimToEmpty(parameters.dateFrom);
toDateShort = StringUtils.trimToEmpty(parameters.dateTo);
initializedCB = StringUtils.trimToEmpty(parameters.initializedCB);
preRetrieved = StringUtils.trimToEmpty(parameters.preRetrieved);
List infoMsgList = FastList.newInstance();
Boolean isValidDate = true;
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
String entryDateFormat = entryDateTimeFormat;
fromDate = null;
toDate = UtilDateTime.nowTimestamp();
dateExpr= FastList.newInstance();   
visitors= FastList.newInstance(); 
dateCond = null;
mainCond = EntityCondition.makeCondition("shoppingListTypeId", EntityOperator.EQUALS, "SLT_SPEC_PURP");
visitorList = FastList.newInstance();
//Converting to TimeStamp and preparing entity condition.
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N")
{
    if(UtilValidate.isNotEmpty(fromDateShort))
    {
        if(OsafeAdminUtil.isDateTime(fromDateShort, entryDateTimeFormat))
        {
            fromDate = ObjectType.simpleTypeConvert(fromDateShort, "Timestamp", entryDateFormat, locale);
            dateExpr.add(EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
        }
        else
        {
            infoMsgList.add(UtilProperties.getMessage("OSafeAdminUiLabels","InvalidFromDateInfo",locale));
        }
    }
    if(UtilValidate.isNotEmpty(toDateShort))
    {
        if(OsafeAdminUtil.isDateTime(toDateShort, entryDateTimeFormat))
        {
            toDate = ObjectType.simpleTypeConvert(toDateShort, "Timestamp", entryDateFormat, locale);
            dateExpr.add(EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN_EQUAL_TO, toDate));
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
    if (UtilValidate.isNotEmpty(dateCond))
    {
        mainCond = EntityCondition.makeCondition([mainCond, dateCond], EntityOperator.AND);
    }
    //List of All the visitors(REG & GUEST).
    if (isValidDate)
    {
        visitors = delegator.findList("ShoppingList", mainCond, UtilMisc.toSet("shoppingListId", "partyId", "lastUpdatedStamp"), ["lastUpdatedStamp ASC"], null, false);
    }
    shoppingListOps = new EntityFindOptions();
    shoppingListOps.setDistinct(true);
    shoppingListItemsList = delegator.findList("ShoppingListItem", null, null, null, shoppingListOps, false);
    shoppingListItemsIds = EntityUtil.getFieldListFromEntityList(shoppingListItemsList, "shoppingListId", true);
    
    anonCond = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, null);
    regCond = EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, null);
    itemsCond = EntityCondition.makeCondition("shoppingListId", EntityOperator.IN, shoppingListItemsIds);
    anonItemsCond = EntityCondition.makeCondition([anonCond, itemsCond], EntityOperator.AND);
    regItemsCond = EntityCondition.makeCondition([regCond, itemsCond], EntityOperator.AND);
    
    //filtered list of visitors 
    anonUsers = EntityUtil.filterByCondition(visitors, anonCond);
    regUsers = EntityUtil.filterByCondition(visitors, regCond);
    anonItemsUsers = EntityUtil.filterByCondition(visitors, anonItemsCond);
    regItemsUsers = EntityUtil.filterByCondition(visitors, regItemsCond);
    
    if(UtilValidate.isEmpty(fromDate) && UtilValidate.isNotEmpty(visitors))
    {
        fromDate = (EntityUtil.getFirst(visitors)).getTimestamp("lastUpdatedStamp");
    }
    
    if(UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(toDate) && UtilValidate.isNotEmpty(visitors))
    {
        tempFromDate = fromDate;
        tempToDate = UtilDateTime.getMonthEnd(tempFromDate, timeZone, locale);

        //Seperating GUEST && REG users Month-Wise
        while(tempFromDate <= toDate)
        {
            visitorMap = FastMap.newInstance();
            cond = EntityCondition.makeCondition([EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO, tempFromDate),
                                                  EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN_EQUAL_TO, tempToDate)], EntityOperator.AND);
            monthList = UtilDateTime.getMonthNames(locale);
            
            visitorMap.put("tempFromDate", tempFromDate);
            visitorMap.put("tempToDate", tempToDate);
            visitorMap.put("month", (monthList.get(UtilDateTime.getMonth(tempFromDate, timeZone, locale))).substring(0,3)+"-"+UtilDateTime.getYear(tempFromDate, timeZone, locale));
            visitorMap.put("anonVisitorCount", (EntityUtil.filterByCondition(anonUsers, cond)).size());
            visitorMap.put("regVisitorCount", (EntityUtil.filterByCondition(regUsers, cond)).size());
            visitorMap.put("anonItemsVisitorCount", (EntityUtil.filterByCondition(anonItemsUsers, cond)).size());
            visitorMap.put("regItemsVisitorCount", (EntityUtil.filterByCondition(regItemsUsers, cond)).size());
            visitorList.add(visitorMap);
            
            tempFromDate = UtilDateTime.getNextDayStart(tempToDate);
            tempToDate = UtilDateTime.getMonthEnd(tempFromDate, timeZone, locale);
        }
        
    }
}
pagingListSize=visitorList.size();
context.pagingListSize=pagingListSize;
context.pagingList = visitorList;