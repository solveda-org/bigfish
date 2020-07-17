import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import com.osafe.util.OsafeAdminUtil;
import org.ofbiz.base.util.UtilDateTime;
import com.osafe.events.SolrEvents;

String productId = StringUtils.trimToEmpty(parameters.productId);
String productName = StringUtils.trimToEmpty(parameters.productName);
String description = StringUtils.trimToEmpty(parameters.description);
String internalName = StringUtils.trimToEmpty(parameters.internalName);
initializedCB = StringUtils.trimToEmpty(parameters.initializedCB);
preRetrieved = StringUtils.trimToEmpty(parameters.preRetrieved);
srchVirtualOnly=StringUtils.trimToEmpty(parameters.srchVirtualOnly);
srchAll=StringUtils.trimToEmpty(parameters.srchall);
srchFinishedGoodOnly=StringUtils.trimToEmpty(parameters.srchFinishedGoodOnly);
srchCategoryId=StringUtils.trimToEmpty(parameters.srchCategoryId);
categoryId = StringUtils.trimToEmpty(parameters.categoryId);
notYetIntroduced = StringUtils.trimToEmpty(parameters.notYetIntroduced);
discontinued = StringUtils.trimToEmpty(parameters.discontinued);
searchText = StringUtils.trimToEmpty(parameters.searchText);

atTime = UtilDateTime.nowTimestamp();

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
if(categoryId)
{
	srchCategoryId = categoryId;
	parameters.srchCategoryId = categoryId;
}
paramsExpr = FastList.newInstance();
prodCtntExprDesc = FastList.newInstance();
prodCtntExprName = FastList.newInstance();
exprBldr =  new EntityConditionBuilder();

if(UtilValidate.isNotEmpty(searchText)) 
{
    if(UtilValidate.isEmpty(request.getParameter("searchText"))) 
    {
        request.setAttribute("searchText", searchText);
    }
    webSearchResult = SolrEvents.solrSearch(request,response);
    completeDocumentList = FastList.newInstance();
    if(webSearchResult == 'success') 
    {
        completeDocumentList = request.getAttribute("completeDocumentList");
    }
    productSearchByCategoryList = completeDocumentList;
    context.searchText = searchText;
} else 
{
    if (UtilValidate.isNotEmpty(productId))
    {
        paramsExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productId"), EntityOperator.EQUALS, productId.toUpperCase()));
        context.productId=productId;
    }
    //Start Fetch the Record from Product Content.
    if (UtilValidate.isNotEmpty(description))
    {
        prodCtntExprDesc.add(EntityCondition.makeCondition(
        		EntityFunction.UPPER_FIELD("textData"),
                EntityOperator.LIKE, "%"+description.toUpperCase() + "%"));
        prodCtntExprDesc.add(EntityCondition.makeCondition("productContentTypeId", EntityOperator.EQUALS, "LONG_DESCRIPTION"));
        prodCtntCondDesc = EntityCondition.makeCondition(prodCtntExprDesc, EntityOperator.AND);
        prodCtntListDesc = delegator.findList("ProductContentAndText",prodCtntCondDesc, null, null, null, false);
        productIdListDesc = EntityUtil.getFieldListFromEntityList(prodCtntListDesc, "productId", true);
        paramsExpr.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIdListDesc));
        context.description=description;
    }
    
    if (UtilValidate.isNotEmpty(productName))
    {
        prodCtntExprName.add(EntityCondition.makeCondition(
                EntityFunction.UPPER_FIELD("textData"),
                EntityOperator.LIKE, "%"+productName.toUpperCase() + "%"));
        prodCtntExprName.add(EntityCondition.makeCondition("productContentTypeId", EntityOperator.EQUALS, "PRODUCT_NAME"));
        prodCtntCondName = EntityCondition.makeCondition(prodCtntExprName, EntityOperator.AND);
        prodCtntListName = delegator.findList("ProductContentAndText",prodCtntCondName, null, null, null, false);
        productIdListName = EntityUtil.getFieldListFromEntityList(prodCtntListName, "productId", true);
        paramsExpr.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIdListName));
        context.productName=productName;
    }
    //End Fetch the Record from Product Content.
    if (UtilValidate.isNotEmpty(internalName))
    {
        paramsExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("internalName"),
                EntityOperator.LIKE, "%"+internalName.toUpperCase()+"%"));
        context.internalName=internalName;
    }
    
    if (UtilValidate.isNotEmpty(srchCategoryId) && srchCategoryId != 'all') 
    {
    	productCategoryMembers = delegator.findByAnd("ProductCategoryMember", UtilMisc.toMap("productCategoryId", srchCategoryId));
    	productIds = EntityUtil.getFieldListFromEntityList(productCategoryMembers, "productId", true);
        if (UtilValidate.isNotEmpty(productIds))
        {
        	paramsExpr.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
        	
        }
    }
 // Reterive Only Virtual Product with CheckBox implementation
    virtualExpr= FastList.newInstance();
    finishedGoodExpr = FastList.newInstance();
    srchCond = null;
    mainCond = null;
    //When Virtual is checked.
    if (UtilValidate.isNotEmpty(srchVirtualOnly) && UtilValidate.isEmpty(srchFinishedGoodOnly) && UtilValidate.isEmpty(srchAll))
    {
        virtualExpr.add(EntityCondition.makeCondition("isVirtual", EntityOperator.EQUALS, "Y"));
        context.srchVirtualOnly=srchVirtualOnly
    }
    //When Finished Good is checked.
    else if(UtilValidate.isNotEmpty(srchFinishedGoodOnly) && UtilValidate.isEmpty(srchVirtualOnly) && UtilValidate.isEmpty(srchAll))
    {
    	virtualExpr.add(EntityCondition.makeCondition("isVirtual", EntityOperator.EQUALS, "N"));
    	virtualExpr.add(EntityCondition.makeCondition("isVariant", EntityOperator.EQUALS, "N"));
    }
    //When ALL is checked.
    else if((UtilValidate.isNotEmpty(srchFinishedGoodOnly) && UtilValidate.isNotEmpty(srchVirtualOnly))||(UtilValidate.isNotEmpty(srchAll)))
    {
        virtualExpr.add(EntityCondition.makeCondition("isVariant", EntityOperator.NOT_EQUAL, "Y"));
    }
    //When None is checked.
    else if(UtilValidate.isEmpty(srchFinishedGoodOnly) && UtilValidate.isEmpty(srchVirtualOnly) && UtilValidate.isEmpty(srchAll))
    {
        virtualExpr.add(EntityCondition.makeCondition("isVariant", EntityOperator.NOT_EQUAL, "Y"));
    }
    
    if (UtilValidate.isNotEmpty(virtualExpr))
    {
    	srchCond = EntityCondition.makeCondition(virtualExpr, EntityOperator.AND);
    }
    dateExpr= FastList.newInstance();
    introDateExpr= FastList.newInstance();
    if(!notYetIntroduced)
    {
        introDateExpr.add(EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, atTime));
        introDateExpr.add(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null));
        dateExpr.add(EntityCondition.makeCondition(introDateExpr, EntityOperator.OR));
    }
    discoDateExpr= FastList.newInstance();
    if(!discontinued)
    {
        discoDateExpr.add(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, atTime));
        discoDateExpr.add(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null));
        dateExpr.add(EntityCondition.makeCondition(discoDateExpr, EntityOperator.OR));
    }
    dateCond = null;
    if(UtilValidate.isNotEmpty(dateExpr))
    {
        dateCond = EntityCondition.makeCondition(dateExpr, EntityOperator.AND);
    }
    prodCond=null;
    if (UtilValidate.isNotEmpty(paramsExpr)) 
    {
        prodCond=EntityCondition.makeCondition(paramsExpr, EntityOperator.AND);
        mainCond=prodCond;
    }
    
    paramCond=null;
    if (UtilValidate.isNotEmpty(srchCond)) 
    {
   	   if (UtilValidate.isNotEmpty(prodCond))
   	   {
 	      mainCond = EntityCondition.makeCondition([prodCond, srchCond], EntityOperator.AND);
   	   }
   	   else
   	   {
   	     mainCond=srchCond;
   	   }
    }
    if (UtilValidate.isNotEmpty(dateCond)) 
    {
        mainCond = EntityCondition.makeCondition([mainCond, dateCond], EntityOperator.AND);
    }
    orderBy = ["productId"];
    productSearchList=FastList.newInstance();
    if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N") 
    {
        productSearchList = delegator.findList("Product",mainCond, null, orderBy, null, false);
    }
    productSearchByCategoryList=FastList.newInstance();
    if (UtilValidate.isNotEmpty(productSearchList))
    {
      if (UtilValidate.isNotEmpty(globalContext.currentCategories))
      {
        currentCategories =globalContext.currentCategories; 
        productExists=FastMap.newInstance();
        for (GenericValue currentCategory  : currentCategories)
        {
          productCategoryList = CategoryWorker.filterProductsInCategory(delegator,productSearchList,currentCategory.productCategoryId);
          if (UtilValidate.isNotEmpty(productCategoryList))
          {
	        for (GenericValue curValue: productCategoryList) 
	        {
	          if (!productExists.containsValue(curValue))
	          {
		          productExists.put(curValue,curValue);
	              productSearchByCategoryList.add(curValue);
	          }
            }
          }
        }
      }
      
    }
}    
    pagingListSize=productSearchByCategoryList.size();
    context.pagingListSize=pagingListSize;
    context.pagingList = productSearchByCategoryList;
