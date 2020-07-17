package product;

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
import org.ofbiz.base.util.UtilDateTime;
import com.osafe.events.SolrEvents;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.GenericEntityException;


//import org.ofbiz.base.util.Debug;

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
add_product_id = StringUtils.trimToEmpty(parameters.add_product_id);
prod_type = StringUtils.trimToEmpty(parameters.prod_type);

//Success message for Add to Cart
if (UtilValidate.isNotEmpty(add_product_id) && UtilValidate.isNotEmpty(prod_type) && ProductWorker.isSellable(delegator, add_product_id))
{
   messageMap=[:];
   if(prod_type.equals("Variant"))
   {
	   GenericValue add_virtual_product = ProductWorker.getParentProduct(add_product_id, delegator);
	   add_product_name = ProductContentWrapper.getProductContentAsText(add_virtual_product, 'PRODUCT_NAME', request);
   }
   else if(prod_type.equals("FinishedGood"))
   {
	   GenericValue finished_good = delegator.findByPrimaryKey("Product", [productId : add_product_id]);
	   add_product_name = ProductContentWrapper.getProductContentAsText(finished_good, 'PRODUCT_NAME', request);
   }
   messageMap.put("add_product_name", add_product_name);
   context.showSuccessMessage = UtilProperties.getMessage("OSafeAdminUiLabels","CheckoutAddProductSuccess",messageMap, locale )
}

productSearchByCategoryList = FastList.newInstance();
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
} 
else
{
	// Product dynamic view entity
	DynamicViewEntity productDve = new DynamicViewEntity();
	productDve.addMemberEntity("PR", "Product");
	productDve.addAlias("PR", "productId", "productId", null, null, null, null);
	productDve.addAlias("PR", "internalName", "internalName", null, null, null, null);
	productDve.addAlias("PR", "isVirtual", "isVirtual", null, null, null, null);
	productDve.addAlias("PR", "isVariant", "isVariant", null, null, null, null);
	productDve.addAlias("PR", "introductionDate", "introductionDate", null, null, null, null);
	productDve.addAlias("PR", "salesDiscontinuationDate", "salesDiscontinuationDate", null, null, null, null);
	//make relation with ProductCategoryMember
	productDve.addRelation("many", "", "ProductCategoryMember", UtilMisc.toList(new ModelKeyMap("productId", "productId")));
	productDve.addMemberEntity("PCM", "ProductCategoryMember");
	productDve.addAlias("PCM", "productCategoryId", "productCategoryId", null, null, null, null);
	productDve.addViewLink("PR", "PCM", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("productId", "productId")));

	//FieldsToSelect
	List productFields = FastList.newInstance();
	productFields.add("productId");
	productFields.add("internalName");
	productFields.add("isVirtual");
	productFields.add("isVariant");
	productFields.add("introductionDate");
	productFields.add("salesDiscontinuationDate");
	// set distinct
	productFindOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);	
	prodCond = FastList.newInstance();
	paramsExpr = FastList.newInstance();
	prodCtntExprDesc = FastList.newInstance();
	prodCtntExprName = FastList.newInstance();
	prodCtntCondDesc = null;
	
	if (UtilValidate.isNotEmpty(productId))
	{
		paramsExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productId"), EntityOperator.EQUALS, productId.toUpperCase()));
		context.productId=productId;
	}
	if (UtilValidate.isNotEmpty(description))
	{
		prodLongDescList = delegator.findByAnd("ProductContentAndText", [productContentTypeId : "LONG_DESCRIPTION"]);
		productIdListDesc = FastList.newInstance();
		if (UtilValidate.isNotEmpty(prodLongDescList))
		{
			for (GenericValue prodLongDesc : prodLongDescList)
			{
				prodDescTextData = prodLongDesc.textData;
				if (UtilValidate.isNotEmpty(prodDescTextData) && (prodDescTextData.toUpperCase()).contains(description.toUpperCase()))
				{
					productIdListDesc.add(prodLongDesc.productId);
				}
			}
		}
		paramsExpr.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIdListDesc));
		context.description=description;
	}
	if (UtilValidate.isNotEmpty(productName))
	{
		prodNameList = delegator.findByAnd("ProductContentAndText", [productContentTypeId : "PRODUCT_NAME"]);
		productIdListName = FastList.newInstance();
		if (UtilValidate.isNotEmpty(prodNameList))
		{
			for (GenericValue prodName : prodNameList)
			{
				prodNameTextData = prodName.textData;
				if (UtilValidate.isNotEmpty(prodNameTextData) && (prodNameTextData.toUpperCase()).contains(productName.toUpperCase()))
				{
					productIdListName.add(prodName.productId);
				}
			}
		}
		paramsExpr.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIdListName));
		context.productName=productName;
	}
	if (UtilValidate.isNotEmpty(internalName))
	{
		paramsExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("internalName"),
				EntityOperator.LIKE, "%"+internalName.toUpperCase()+"%"));
		context.internalName=internalName;
	}
	if (UtilValidate.isNotEmpty(srchCategoryId) && srchCategoryId != 'all')
	{
		paramsExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productCategoryId"), EntityOperator.EQUALS, srchCategoryId.toUpperCase()));
	}
	else if (UtilValidate.isEmpty(srchCategoryId) || srchCategoryId == 'all')
	{
		if (UtilValidate.isNotEmpty(globalContext.currentCategories))
		{
			currentCategories =globalContext.currentCategories;
			currentCategoryIds = EntityUtil.getFieldListFromEntityList(currentCategories, "productCategoryId", true);
			if (UtilValidate.isNotEmpty(currentCategoryIds))
			{
				paramsExpr.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, currentCategoryIds));
			}
		}
	}
	// Reterive Only Virtual Product with CheckBox implementation
	virtualExpr= FastList.newInstance();
	virtSrchCond = null;
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
		virtSrchCond = EntityCondition.makeCondition(virtualExpr, EntityOperator.AND);
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
	if (UtilValidate.isNotEmpty(virtSrchCond))
	{
		  if (UtilValidate.isNotEmpty(prodCond))
		  {
		   mainCond = EntityCondition.makeCondition([prodCond, virtSrchCond], EntityOperator.AND);
		  }
		  else
		  {
			mainCond=virtSrchCond;
		  }
	}
	if (UtilValidate.isNotEmpty(dateCond))
	{
		mainCond = EntityCondition.makeCondition([mainCond, dateCond], EntityOperator.AND);
	}
	eli = null;
	productSearchList=FastList.newInstance();
	if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N")
	{
		eli = delegator.findListIteratorByCondition(productDve, mainCond, null, productFields, null, productFindOpts);
		productSearchList = eli.getCompleteList();
		productSearchByCategoryList = productSearchList;
	}
	if (UtilValidate.isNotEmpty(eli))
	{
		try
		{
			eli.close();
		}
		catch (GenericEntityException e)
		{}
	}
}

pagingListSize=productSearchByCategoryList.size();
context.pagingListSize=pagingListSize;
context.pagingList = productSearchByCategoryList;
