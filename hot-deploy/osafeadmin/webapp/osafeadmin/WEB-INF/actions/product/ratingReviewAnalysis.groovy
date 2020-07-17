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
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import org.apache.commons.lang.StringEscapeUtils;

fromDateShort = StringUtils.trimToEmpty(parameters.dateFrom);
toDateShort = StringUtils.trimToEmpty(parameters.dateTo);
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
String entryDateFormat = entryDateTimeFormat;
fromDate = null;
toDate = UtilDateTime.nowTimestamp();
dateExpr= FastList.newInstance();
statusExpr= FastList.newInstance();    
resultList = FastList.newInstance();
dateCond = null;
statusCond = null;
mainCond = null;
viewApproved= parameters.viewApproved;
viewDeleted= parameters.viewDeleted;
viewPending= parameters.viewPending;
viewAll= parameters.viewall;
reviewRatingList = FastList.newInstance();
List infoMsgList = FastList.newInstance();
Boolean isValidDate = true;
//Converting to TimeStamp and preparing entity condition.
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N")
{   
    //DATE CONDITION.
    if(UtilValidate.isNotEmpty(fromDateShort))
    {
        if(OsafeAdminUtil.isDateTime(fromDateShort, entryDateTimeFormat))
        {
            fromDate = ObjectType.simpleTypeConvert(fromDateShort, "Timestamp", entryDateFormat, locale);
            fromDate = UtilDateTime.getDayStart(fromDate);
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
    if(UtilValidate.isNotEmpty(dateExpr))
    {
        dateCond = EntityCondition.makeCondition(dateExpr, EntityOperator.AND);
        mainCond = dateCond;
    }
    //STATUS ID CONDITION
    if(UtilValidate.isNotEmpty(viewApproved))
    {
        statusExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRR_APPROVED"));
    }
    if(UtilValidate.isNotEmpty(viewDeleted))
    {
        statusExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRR_DELETED"));
    }
    if(UtilValidate.isNotEmpty(viewPending))
    {
        statusExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRR_PENDING"));
    }
    if(UtilValidate.isNotEmpty(statusExpr) && UtilValidate.isEmpty(viewAll))
    {
        statusCond = EntityCondition.makeCondition(statusExpr, EntityOperator.OR);
        mainCond = statusCond;
    }
    //STATUS ID AND DATE COMBINED CONDITION.
    if (UtilValidate.isNotEmpty(dateCond) && UtilValidate.isNotEmpty(statusCond))
    {
        mainCond = EntityCondition.makeCondition([dateCond, statusCond], EntityOperator.AND);
    }
    //List of All the Review Rating.
    if (isValidDate)
    {
        reviewRatingList = delegator.findList("ProductReview", mainCond, UtilMisc.toSet("productId", "postedDateTime"), null, null, false);
    }
    
    distinctValueList = EntityUtil.getFieldListFromEntityList(reviewRatingList,"productId",true);
    //Grouping Reviews Product Wise.
    for (String distinctValue : distinctValueList)
    {
        rowCount = 0;
        avgRating = 0;
        resultMap = FastMap.newInstance(); 
        List filteredList = EntityUtil.filterByAnd(reviewRatingList, [productId : distinctValue]);
        rowCount = filteredList.size();
        avgRatingGv = delegator.findByPrimaryKey("ProductCalculatedInfo",[productId : distinctValue]).averageCustomerRating;
        product = delegator.findOne("Product",["productId":distinctValue], false);
        if (UtilValidate.isNotEmpty(product)) 
         {
            productContentWrapper = new ProductContentWrapper(product, request);
            String productDetailHeading = "";
            if (UtilValidate.isNotEmpty(productContentWrapper))
            {
                productInternalName = StringEscapeUtils.unescapeHtml(productContentWrapper.get("PRODUCT_NAME").toString());
                if (UtilValidate.isEmpty(productInternalName)) 
                {
                    productInternalName = product.get("productName");
                }
                if (UtilValidate.isEmpty(productInternalName)) 
                {
                    productInternalName = product.get("internalName");
                }
            }
         }
        if(UtilValidate.isNotEmpty(avgRatingGv))
        {
            avgRating = (avgRatingGv).setScale(1, BigDecimal.ROUND_HALF_UP);
        }
        resultMap.put("productName",distinctValue+": "+productInternalName);
        resultMap.put("count",rowCount);
        resultMap.put("avgRating",avgRating);
        resultList.add(resultMap);
    }
        context.resultList = resultList;
}

