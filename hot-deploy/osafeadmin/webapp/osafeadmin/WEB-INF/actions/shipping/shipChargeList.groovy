package product;

import org.ofbiz.base.util.UtilValidate;

import java.sql.Timestamp;
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

String srchProductId = StringUtils.trimToEmpty(parameters.srchProductId);
String srchReviewId = StringUtils.trimToEmpty(parameters.srchReviewId);
String srchReviewer = StringUtils.trimToEmpty(parameters.srchReviewer);
String srchReviewStatus = StringUtils.trimToEmpty(parameters.srchReviewStatus);
String srchDays = StringUtils.trimToEmpty(parameters.srchDays);
srchReviewPend = StringUtils.trimToEmpty(parameters.srchReviewPend);
srchReviewApprove = StringUtils.trimToEmpty(parameters.srchReviewApprove);
srchReviewReject=StringUtils.trimToEmpty(parameters.srchReviewReject);
srchAll=StringUtils.trimToEmpty(parameters.srchall);
context.srchall=srchAll;
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

// Product Id
if(srchProductId){

    productId=srchProductId;
    findProdCond = EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("internalName")), EntityOperator.EQUALS, srchProductId.toUpperCase());
    products = delegator.findList("Product",findProdCond, null, null, null, true);
    if (products) {
        product=EntityUtil.getFirst(products);
        productId=product.productId;
    }

    exprs.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
    context.srchProductId=srchProductId
}
if(srchReviewId){

    exprs.add(EntityCondition.makeCondition("productReviewId", EntityOperator.EQUALS, srchReviewId));
    context.srchReviewId=srchReviewId
}

// Review Status with DD implementation
if(UtilValidate.isNotEmpty(srchReviewStatus)){
    exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, srchReviewStatus));
    context.srchReviewStatus=srchReviewStatus
}

// Days
if(UtilValidate.isNotEmpty(srchDays)){

    Calendar fiveDaysAgoCal = UtilDateTime.toCalendar(nowTs);
    fiveDaysAgoCal.add(Calendar.DAY_OF_MONTH, -5);
    Timestamp fiveDaysAgo = new Timestamp(fiveDaysAgoCal.getTimeInMillis());

    Calendar tenDaysAgoCal = UtilDateTime.toCalendar(nowTs);
    tenDaysAgoCal.add(Calendar.DAY_OF_MONTH, -10);
    Timestamp tenDaysAgo = new Timestamp(tenDaysAgoCal.getTimeInMillis());

    if("oneToFive".equals(srchDays)){
        // 1-5 Days
        // Now - 5 Days

        ecl = EntityCondition.makeCondition([
            EntityCondition.makeCondition("postedDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, fiveDaysAgo),
            EntityCondition.makeCondition("postedDateTime", EntityOperator.LESS_THAN_EQUAL_TO, nowTs)
        ],
        EntityOperator.AND);
        exprs.add(ecl);
    }else if("fiveToTen".equals(srchDays)){
        // 5-10 Days
        // 5 - 10 Days

        ecl = EntityCondition.makeCondition([
            EntityCondition.makeCondition("postedDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, tenDaysAgo),
            EntityCondition.makeCondition("postedDateTime", EntityOperator.LESS_THAN_EQUAL_TO, fiveDaysAgo)
        ],
        EntityOperator.AND);
        exprs.add(ecl);
    }else if("tenPlus".equals(srchDays)){
        // 10+ Days
        // 10 - ... Days
        ecl = EntityCondition.makeCondition([
            EntityCondition.makeCondition("postedDateTime", EntityOperator.LESS_THAN_EQUAL_TO, tenDaysAgo)
        ],
        EntityOperator.AND);
        exprs.add(ecl);
    }
    context.srchDays=srchDays;
}

// Reviewer
if(srchReviewer){
    exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("reviewNickName")), EntityOperator.LIKE, srchReviewer.toUpperCase() +"%"));
    context.srchReviewer=srchReviewer
}

if (UtilValidate.isNotEmpty(exprs)) {
    prodCond=EntityCondition.makeCondition(exprs, EntityOperator.AND);
    mainCond=prodCond;
}

// Review Status with CheckBox implementation
statusExpr= FastList.newInstance();
if(srchReviewPend){
    statusExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRR_PENDING"));
   context.srchReviewPend=srchReviewPend

}
if(srchReviewApprove){
    statusExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRR_APPROVED"));
   context.srchReviewApprove=srchReviewApprove

}
if(srchReviewReject){
    statusExpr.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRR_DELETED"));
   context.srchReviewReject=srchReviewReject

}

if (UtilValidate.isNotEmpty(statusExpr))
{
   statusCond = EntityCondition.makeCondition(statusExpr, EntityOperator.OR);
   if (prodCond) {
      mainCond = EntityCondition.makeCondition([prodCond, statusCond], EntityOperator.AND);
   }
   else
   {
     mainCond=statusCond;
   }
}

orderBy = ["productReviewId"];

productReviews=FastList.newInstance();
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N") 
 {
	productReviews = delegator.findList("ProductReview",mainCond, null, orderBy, null, true);
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


