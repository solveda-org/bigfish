package catalog;

import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.*;
import org.ofbiz.product.store.ProductStoreWorker;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.Timestamp;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.*;

initializedCB = StringUtils.trimToEmpty(parameters.initializedCB);
preRetrieved = StringUtils.trimToEmpty(parameters.preRetrieved);
catalogActiveDate = StringUtils.trimToEmpty(parameters.catalogActiveDate);
showAll = StringUtils.trimToEmpty(parameters.showAll);
String entryDateFormat = preferredDateFormat;
Timestamp catalogActiveDateTs = null;
List errMsgList = FastList.newInstance();

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

productStore = context.productStore;
if (UtilValidate.isNotEmpty(productStore))
{
	String productStoreId = productStore.productStoreId;
	String currentCatalogId = context.currentCatalogId;
	String rootProductCategoryId = context.rootProductCategoryId;
	if (UtilValidate.isNotEmpty(rootProductCategoryId))
	{
		context.catalogTopCategoryId = rootProductCategoryId;
		orderBy = ["sequenceNum"];
		paramsExpr.add(EntityCondition.makeCondition("parentProductCategoryId", EntityOperator.EQUALS, rootProductCategoryId));
		paramCond = null;
        mainCond = null;
        dateCond = null;
        if (UtilValidate.isNotEmpty(paramsExpr)) {
            paramCond=EntityCondition.makeCondition(paramsExpr, EntityOperator.AND);
            mainCond=paramCond;
        }
		dateExpr = FastList.newInstance();
		activeThruDateExpr = FastList.newInstance();
		if(UtilValidate.isEmpty(showAll))
		{
    		if(UtilValidate.isNotEmpty(catalogActiveDate)) {
    		    catalogActiveDateTs =UtilDateTime.nowTimestamp(); 
                try {
    		        catalogActiveDateTs =ObjectType.simpleTypeConvert(catalogActiveDate, "Timestamp", entryDateFormat, locale);
    		    } catch (Exception e) {
    		        catalogActiveDateTs =UtilDateTime.nowTimestamp();
    		        context.catalogActiveDate=UtilDateTime.nowDateString(entryDateFormat);
                    errMsgList.add(UtilProperties.getMessage("OSafeAdminUiLabels","AsOfDateError",locale));
    		    }
    		} else {
    		    catalogActiveDateTs =UtilDateTime.nowTimestamp();
    		    context.catalogActiveDate=UtilDateTime.nowDateString(entryDateFormat);
    		}
    		dateExpr.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, catalogActiveDateTs));
    		activeThruDateExpr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, catalogActiveDateTs));
    		activeThruDateExpr.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
    		dateExpr.add(EntityCondition.makeCondition(activeThruDateExpr, EntityOperator.OR));
    		
    	    if(UtilValidate.isNotEmpty(dateExpr))
    	    {
    	        dateCond = EntityCondition.makeCondition(dateExpr, EntityOperator.AND);
    	    }
    	    
    	    if (dateCond) 
    	    {
    	        mainCond = EntityCondition.makeCondition([paramCond, dateCond], EntityOperator.AND);
    	    }
		}
		else 
		{
		    context.catalogActiveDate="";
		}
		    categoryRollupList = delegator.findList("ProductCategoryRollupAndChild", mainCond, null, orderBy, null, false);
	    
		//CategoryWorker.getRelatedCategories(request, "topLevelActiveList", topCategoryId, true);
		//categoryList = request.getAttribute("topLevelList");
		//categoryList = delegator.findByAnd("ProductCategoryRollupAndChild", UtilMisc.toMap("parentProductCategoryId", topCategoryId),UtilMisc.toList("sequenceNum"));
		context.resultList = categoryRollupList;
		request.setAttribute("topLevelList", categoryRollupList);
		if (categoryRollupList) {
		    catContentWrappers = FastMap.newInstance();
		    CategoryWorker.getCategoryContentWrappers(catContentWrappers, categoryRollupList, request);
		    context.catContentWrappers = catContentWrappers;
		    subCatRollUpMap = FastMap.newInstance();
		    for (GenericValue categoryRollUp : categoryRollupList)
		    {
		        String mapKey = categoryRollUp.productCategoryId;
		        paramsExpr = FastList.newInstance();
		        paramsExpr.add(EntityCondition.makeCondition("parentProductCategoryId", EntityOperator.EQUALS, categoryRollUp.productCategoryId));
		        paramCond = null;
		        mainCond = null;
		        if (UtilValidate.isNotEmpty(paramsExpr)) {
		            paramCond = EntityCondition.makeCondition(paramsExpr, EntityOperator.AND);
		            mainCond = paramCond;
		        }
		        if (dateCond) 
	            {
	                mainCond = EntityCondition.makeCondition([paramCond, dateCond], EntityOperator.AND);
	            }
		        List subCatRollupList = delegator.findList("ProductCategoryRollupAndChild", mainCond, null, orderBy, null, false);
		        if(UtilValidate.isNotEmpty(subCatRollupList)) 
		        {
		            subCatRollUpMap.put(mapKey, subCatRollupList);
		            context.subCatRollUpMap = subCatRollUpMap;
		        }
		    }
		}
	}
	
     if (errMsgList) {
        request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
     }
	
}
