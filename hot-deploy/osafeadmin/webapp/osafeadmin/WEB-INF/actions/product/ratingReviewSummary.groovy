package product;

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
statusExpr= FastList.newInstance();    
resultList = FastList.newInstance();
pendingOneToFiveReviews = FastList.newInstance();
pendingSixToTenReviews= FastList.newInstance();
pendingElevenToTwentyReviews = FastList.newInstance();
pendingTwentyPlusReviews = FastList.newInstance();
resultList = FastList.newInstance();
dateCond = null;
statusCond = null;
mainCond = null;
reviewRatingList = FastList.newInstance();
//Converting to TimeStamp and preparing entity condition.
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N")
{   
    //DATE CONDITION.
    if(UtilValidate.isNotEmpty(fromDateShort))
    {
        if(OsafeAdminUtil.isDateTime(fromDateShort, entryDateTimeFormat))
        {
            fromDate = ObjectType.simpleTypeConvert(fromDateShort, "Timestamp", entryDateFormat, locale);
            fromDate  = UtilDateTime.getDayStart(fromDate);
            dateExpr.add(EntityCondition.makeCondition("postedDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
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
            toDate  = UtilDateTime.getDayEnd(toDate);
            dateExpr.add(EntityCondition.makeCondition("postedDateTime", EntityOperator.LESS_THAN_EQUAL_TO, toDate));
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
    context.isValidDate = isValidDate;

    if(UtilValidate.isNotEmpty(dateExpr))
    {
        dateCond = EntityCondition.makeCondition(dateExpr, EntityOperator.AND);
    }
  
    //List of All the Review Rating.
    if(isValidDate)
    {
        reviewRatingList = delegator.findList("ProductReview", dateCond, UtilMisc.toSet("productId","statusId", "postedDateTime"), null, null, false);
    }
    //Approved Reviews
    approvedReviews = EntityUtil.filterByCondition(reviewRatingList, EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRR_APPROVED"));
    //Pending Reviews
    pendingReviews = EntityUtil.filterByCondition(reviewRatingList, EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRR_PENDING"));
    //Deleted Reviews
    deletedReviews = EntityUtil.filterByCondition(reviewRatingList, EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRR_DELETED"));
    //Segregating Pending Reviews.
    if(UtilValidate.isNotEmpty(reviewRatingList))
    {
        if(UtilValidate.isNotEmpty(toDateShort) && OsafeAdminUtil.isDateTime(toDateShort, entryDateTimeFormat))
        {
            toDate = ObjectType.simpleTypeConvert(toDate, "Timestamp", entryDateFormat, locale);
            toDate  = UtilDateTime.getDayEnd(toDate);
        }
        Timestamp fiveDaysAgo = UtilDateTime.addDaysToTimestamp(toDate,-5);
        Timestamp tenDaysAgo = UtilDateTime.addDaysToTimestamp(toDate,-10);
        Timestamp twentyDaysAgo = UtilDateTime.addDaysToTimestamp(toDate,-20);
        context.fiveDaysAgo = fiveDaysAgo;
        context.tenDaysAgo = tenDaysAgo;
        context.twentyDaysAgo = twentyDaysAgo;
        
        //Pending 1-5 Days
        pendingOneToFive = EntityCondition.makeCondition([
                EntityCondition.makeCondition("postedDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, fiveDaysAgo),
                EntityCondition.makeCondition("postedDateTime", EntityOperator.LESS_THAN, toDate),
                EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRR_PENDING")],EntityOperator.AND);
        pendingOneToFiveReviews = EntityUtil.filterByCondition(reviewRatingList, pendingOneToFive);
        //Pending 6-10 Days
        pendingSixToTen = EntityCondition.makeCondition([
                EntityCondition.makeCondition("postedDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, tenDaysAgo),
                EntityCondition.makeCondition("postedDateTime", EntityOperator.LESS_THAN, fiveDaysAgo),
                EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRR_PENDING")],EntityOperator.AND);
        pendingSixToTenReviews = EntityUtil.filterByCondition(reviewRatingList, pendingSixToTen);
        //Pending 11-20
        pendingElevenToTwenty = EntityCondition.makeCondition([
                EntityCondition.makeCondition("postedDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, twentyDaysAgo),
                EntityCondition.makeCondition("postedDateTime", EntityOperator.LESS_THAN, tenDaysAgo),
                EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRR_PENDING")],EntityOperator.AND);
        pendingElevenToTwentyReviews = EntityUtil.filterByCondition(reviewRatingList, pendingElevenToTwenty);
        //Pending Twenty Plus
        pendingTwentyPlus = EntityCondition.makeCondition([
                EntityCondition.makeCondition("postedDateTime", EntityOperator.LESS_THAN, twentyDaysAgo),
                EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRR_PENDING")],EntityOperator.AND);
        pendingTwentyPlusReviews = EntityUtil.filterByCondition(reviewRatingList, pendingTwentyPlus);
    }
    resultMap= FastMap.newInstance();
    resultMap.put("status",uiLabelMap.ApprovedLabel)
    resultMap.put("statusId","PRR_APPROVED")
    resultMap.put("count",approvedReviews.size());
    resultList.add(resultMap);
    resultMap= FastMap.newInstance();
    resultMap.put("status",uiLabelMap.PendingLabel)
    resultMap.put("statusId","PRR_PENDING")
    resultMap.put("count",pendingReviews.size());
    resultList.add(resultMap);
    resultMap= FastMap.newInstance();
    resultMap.put("status",uiLabelMap.DeletedLabel)
    resultMap.put("statusId","PRR_DELETED")
    resultMap.put("count",deletedReviews.size());
    resultList.add(resultMap);
    context.resultList = resultList;
    
    context.total = reviewRatingList.size();
    context.countOneToFive = pendingOneToFiveReviews.size();
    context.countSixToTen = pendingSixToTenReviews.size();
    context.countElevenToTwenty = pendingElevenToTwentyReviews.size();
    context.countTwentyPlus = pendingTwentyPlusReviews.size();
}

