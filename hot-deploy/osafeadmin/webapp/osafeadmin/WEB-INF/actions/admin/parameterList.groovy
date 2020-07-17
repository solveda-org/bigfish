package admin;

import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
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
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import com.ibm.icu.util.Calendar;

productStore = globalContext.productStore;
productStoreId=globalContext.productStoreId;

String searchString = StringUtils.trimToEmpty(parameters.searchString);
String searchByName = StringUtils.trimToEmpty(parameters.srchByName);
String searchByDescription = StringUtils.trimToEmpty(parameters.srchByDescription);
String searchByValue=StringUtils.trimToEmpty(parameters.srchByValue);
String searchByCategory=StringUtils.trimToEmpty(parameters.srchByCategory);
String searchByAll=StringUtils.trimToEmpty(parameters.srchAll);
context.srchall=searchByAll;
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

paramsExpr = FastList.newInstance();
exprBldr =  new EntityConditionBuilder();
paramCond=null;
List exprListForParameters = [];

if (parameters.srchall) 
{
    exprListForParameters.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("parmKey"), EntityOperator.LIKE, EntityFunction.UPPER(("%" + searchString) + "%")));
    exprListForParameters.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.LIKE, EntityFunction.UPPER(("%" + searchString) + "%")));
    exprListForParameters.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("parmValue"), EntityOperator.LIKE, EntityFunction.UPPER(("%" + searchString) + "%")));
    exprListForParameters.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("parmCategory"), EntityOperator.LIKE, EntityFunction.UPPER(("%" + searchString) + "%")));
    paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.OR); 
    
} else 
{
    if (UtilValidate.isNotEmpty(searchByName))
    {
        paramsExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("parmKey"),
                EntityOperator.LIKE, EntityFunction.UPPER("%"+searchString+"%")));
        context.searchByName=searchByName;
    }
    if (UtilValidate.isNotEmpty(searchByDescription))
    {
       paramsExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"),
                EntityOperator.LIKE, EntityFunction.UPPER("%"+searchString+"%")));
        context.searchByDescription=searchByDescription;
    }
    if (UtilValidate.isNotEmpty(searchByValue))
    {
       paramsExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("parmValue"),
                EntityOperator.LIKE, EntityFunction.UPPER("%"+searchString+"%")));
        context.searchByValue=searchByValue;
    }
    if (UtilValidate.isNotEmpty(searchByCategory))
    {
       paramsExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("parmCategory"),
                EntityOperator.LIKE, EntityFunction.UPPER("%"+searchString+"%")));
        context.searchByValue=searchByValue;
    }
}
if (UtilValidate.isNotEmpty(paramsExpr)) 
{
    prodCond=EntityCondition.makeCondition(paramsExpr, EntityOperator.OR);
    mainCond=prodCond;
}

if (UtilValidate.isNotEmpty(paramsExpr)) 
{
    statusCond = EntityCondition.makeCondition(paramsExpr, EntityOperator.OR);
    if (UtilValidate.isNotEmpty(prodCond)) 
    {
        paramCond = EntityCondition.makeCondition([prodCond, statusCond], EntityOperator.OR);
    }
    else 
    {
       mainCond=statusCond;
    }
}


storeCond = EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId);
if (UtilValidate.isNotEmpty(paramCond)) 
 {
   paramCond = EntityCondition.makeCondition([paramCond, storeCond], EntityOperator.AND);
 } 
 else 
 {
       paramCond=storeCond;
 }



parameterSearchList = [];
orderBy = ["parmKey"];
productStoreParamsList=FastList.newInstance();

if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N") 
 {
    productStoreParamsList = delegator.findList("XProductStoreParm",paramCond, null, orderBy, null, false);
 }
pagingListSize=productStoreParamsList.size();
context.pagingListSize=pagingListSize;
context.pagingList = productStoreParamsList;
