package product;

import org.ofbiz.base.util.UtilValidate;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import com.osafe.util.Util;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import com.ibm.icu.util.Calendar;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import java.sql.Date;
import com.osafe.util.OsafeAdminUtil;

String entryDateFormat = entryDateTimeFormat;
String srchProductId = StringUtils.trimToEmpty(parameters.srchProductId);
String srchReviewId = StringUtils.trimToEmpty(parameters.srchReviewId);
String srchReviewer = StringUtils.trimToEmpty(parameters.srchReviewer);
String srchReviewStatus = StringUtils.trimToEmpty(parameters.srchReviewStatus);
String srchDays = StringUtils.trimToEmpty(parameters.srchDays);
srchReviewPend = StringUtils.trimToEmpty(parameters.srchReviewPend);
srchReviewApprove = StringUtils.trimToEmpty(parameters.srchReviewApprove);
srchReviewReject=StringUtils.trimToEmpty(parameters.srchReviewReject);
searchOneStar = StringUtils.trimToEmpty(parameters.searchOneStar);
searchTwoStars = StringUtils.trimToEmpty(parameters.searchTwoStars);
searchThreeStars=StringUtils.trimToEmpty(parameters.searchThreeStars);
searchFourStars = StringUtils.trimToEmpty(parameters.searchFourStars);
searchFiveStars = StringUtils.trimToEmpty(parameters.searchFiveStars);
searchAll=StringUtils.trimToEmpty(parameters.searchAll);
srchAll=StringUtils.trimToEmpty(parameters.srchall);
context.srchall=srchAll;
initializedCB = StringUtils.trimToEmpty(parameters.initializedCB);
preRetrieved = StringUtils.trimToEmpty(parameters.preRetrieved);
fromDateShort = StringUtils.trimToEmpty(parameters.from);
toDateShort = StringUtils.trimToEmpty(parameters.to);
statusId = StringUtils.trimToEmpty(parameters.status);
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

nowTs = UtilDateTime.nowTimestamp();
session = context.session;
productStore = ProductStoreWorker.getProductStore(request);

Map<String, Object> svcCtx = FastMap.newInstance();
userLogin = session.getAttribute("userLogin");
context.userLogin=userLogin;

exprs = FastList.newInstance();
mainCond=null;
prodCond=null;
statusCond=null;
dateCond=null;
searchCond=null;
statusIdCond=null;
starsCond=null;
// Product Id
if(UtilValidate.isNotEmpty(srchProductId))
{
    productId=srchProductId;
    findProdCond = EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("internalName")), EntityOperator.EQUALS, srchProductId.toUpperCase());
    products = delegator.findList("Product",findProdCond, null, null, null, true);
    if (UtilValidate.isNotEmpty(products)) 
    {
        product=EntityUtil.getFirst(products);
        productId=product.productId;
    }

    exprs.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
    context.srchProductId=srchProductId
}
if(UtilValidate.isNotEmpty(srchReviewId))
{
    exprs.add(EntityCondition.makeCondition("productReviewId", EntityOperator.EQUALS, srchReviewId));
    context.srchReviewId=srchReviewId
}

// Review Status with DD implementation
if(UtilValidate.isNotEmpty(srchReviewStatus))
{
    exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, srchReviewStatus));
    context.srchReviewStatus=srchReviewStatus
}

if(UtilValidate.isNotEmpty(toDateShort) && OsafeAdminUtil.isDateTime(toDateShort, entryDateTimeFormat))
{
    nowTs = ObjectType.simpleTypeConvert(toDateShort, "Timestamp", entryDateFormat, locale);
    nowTs = UtilDateTime.getDayEnd(nowTs);
}


// Reviewer
if(UtilValidate.isNotEmpty(srchReviewer))
{
    exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("reviewNickName")), EntityOperator.LIKE, srchReviewer.toUpperCase() +"%"));
    context.srchReviewer=srchReviewer
}

if (UtilValidate.isNotEmpty(exprs))
{
    prodCond=EntityCondition.makeCondition(exprs, EntityOperator.AND);
    mainCond=prodCond;
}

// Review Status with CheckBox implementation
statusExpr= FastList.newInstance();
if(UtilValidate.isNotEmpty(srchReviewPend))
{
    statusExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRR_PENDING"));
   context.srchReviewPend=srchReviewPend

}
if(UtilValidate.isNotEmpty(srchReviewApprove))
{
    statusExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRR_APPROVED"));
   context.srchReviewApprove=srchReviewApprove

}
if(UtilValidate.isNotEmpty(srchReviewReject))
{
    statusExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRR_DELETED"));
   context.srchReviewReject=srchReviewReject

}

//Considering Stars Condition
starsExpr= FastList.newInstance();
if(UtilValidate.isNotEmpty(searchOneStar))
{
    starsExpr.add(EntityCondition.makeCondition("productRating", EntityOperator.EQUALS, new BigDecimal("1")));
}
if(UtilValidate.isNotEmpty(searchTwoStars))
{
    starsExpr.add(EntityCondition.makeCondition("productRating", EntityOperator.EQUALS, new BigDecimal("2")));
}
if(UtilValidate.isNotEmpty(searchThreeStars))
{
    starsExpr.add(EntityCondition.makeCondition("productRating", EntityOperator.EQUALS, new BigDecimal("3")));
}
if(UtilValidate.isNotEmpty(searchFourStars))
{
    starsExpr.add(EntityCondition.makeCondition("productRating", EntityOperator.EQUALS, new BigDecimal("4")));
}
if(UtilValidate.isNotEmpty(searchFiveStars))
{
    starsExpr.add(EntityCondition.makeCondition("productRating", EntityOperator.EQUALS, new BigDecimal("5")));
}

//Considering Date Condition
dateExpr= FastList.newInstance();
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

if(UtilValidate.isNotEmpty(dateExpr))
{   
    dateCond = EntityCondition.makeCondition(dateExpr, EntityOperator.AND);
    searchCond = dateCond;
}
if(UtilValidate.isNotEmpty(statusId))
{
    statusIdCond = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId);
    searchCond = statusIdCond;
}
if(UtilValidate.isNotEmpty(starsExpr))
{   
    starsCond = EntityCondition.makeCondition(starsExpr, EntityOperator.OR);
    searchCond = starsCond;
}
if(UtilValidate.isNotEmpty(statusIdCond) && UtilValidate.isNotEmpty(dateCond))
{
    searchCond = EntityCondition.makeCondition([dateCond, statusIdCond], EntityOperator.AND);
}
if(UtilValidate.isNotEmpty(statusIdCond) && UtilValidate.isNotEmpty(starsCond))
{
    searchCond = EntityCondition.makeCondition([statusIdCond, starsCond], EntityOperator.AND);
}
if(UtilValidate.isNotEmpty(starsCond) && UtilValidate.isNotEmpty(dateCond))
{
    searchCond = EntityCondition.makeCondition([dateCond, starsCond], EntityOperator.AND);
}
if(UtilValidate.isNotEmpty(statusIdCond) && UtilValidate.isNotEmpty(dateCond) && UtilValidate.isNotEmpty(starsCond))
{
    searchCond = EntityCondition.makeCondition([dateCond, statusIdCond, starsCond], EntityOperator.AND);
}

if (UtilValidate.isNotEmpty(statusExpr))
{
   statusCond = EntityCondition.makeCondition(statusExpr, EntityOperator.OR);
   if (UtilValidate.isNotEmpty(prodCond))
   {
      mainCond = EntityCondition.makeCondition([prodCond, statusCond], EntityOperator.AND);
   }
   else
   {
     mainCond=statusCond;
   }
}
if(UtilValidate.isNotEmpty(searchCond) && UtilValidate.isNotEmpty(mainCond))
{
    mainCond = EntityCondition.makeCondition([mainCond, searchCond], EntityOperator.AND);
}
if(UtilValidate.isNotEmpty(searchCond) && UtilValidate.isEmpty(mainCond))
{
    mainCond = searchCond;
    preRetrieved = "Y";
}

orderBy = ["productReviewId"];

productReviews=FastList.newInstance();
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N" && isValidDate) 
{
    productReviews = delegator.findList("ProductReview",mainCond, null, orderBy, null, false);
}
 
productSearchByCategoryList=FastList.newInstance();
if (UtilValidate.isNotEmpty(productReviews))
{
  if (UtilValidate.isNotEmpty(globalContext.currentCategories))
  {
    currentCategories =globalContext.currentCategories; 
    for (GenericValue productReview  : productReviews)
    {
        for (GenericValue currentCategory  : currentCategories)
        {
          if (CategoryWorker.isProductInCategory(delegator,productReview.productId,currentCategory.productCategoryId))
          {
            productSearchByCategoryList.add(productReview);
            break;
          }
        }
    }
  }  
}
 
pagingListSize=productSearchByCategoryList.size();
context.pagingListSize=pagingListSize;
context.pagingList = productSearchByCategoryList;


