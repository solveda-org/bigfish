package common;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilMisc;
import javax.servlet.http.HttpServletRequest;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.category.CategoryContentWrapper;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.apache.commons.lang.StringUtils;
import com.osafe.util.Util;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.util.EntityUtil;
import com.osafe.services.OsafeManageXml;
import org.ofbiz.base.util.string.FlexibleStringExpander;


String productCategoryId = parameters.productCategoryId;
GenericValue gvProductCategory =  delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId",productCategoryId), true);

String searchText = com.osafe.util.Util.stripHTML(parameters.searchText);
if (gvProductCategory) 
{
    CategoryContentWrapper currentProductCategoryContentWrapper = new CategoryContentWrapper(gvProductCategory, request);
    context.currentProductCategory = gvProductCategory;
    context.currentProductCategoryContentWrapper = currentProductCategoryContentWrapper;

    //set Meta title, Description and Keywords
    String categoryName = currentProductCategoryContentWrapper.get("CATEGORY_NAME");
    if (UtilValidate.isEmpty(categoryName)) {
        categoryName = gvProductCategory.categoryName;
    }
    if(UtilValidate.isNotEmpty(categoryName)) {
        context.metaTitle = categoryName;
        context.pageTitle = categoryName;
    }
    if(UtilValidate.isNotEmpty(currentProductCategoryContentWrapper.get("DESCRIPTION"))) {
        context.metaKeywords = currentProductCategoryContentWrapper.get("DESCRIPTION");
    }
    if(UtilValidate.isNotEmpty(currentProductCategoryContentWrapper.get("LONG_DESCRIPTION"))) {
        context.metaDescription = currentProductCategoryContentWrapper.get("LONG_DESCRIPTION");
    }
    //override Meta title, Description and Keywords
    String metaTitle = currentProductCategoryContentWrapper.get("HTML_PAGE_TITLE");
    if(UtilValidate.isNotEmpty(metaTitle)) {
        context.metaTitle = metaTitle;
    }
    String metaKeywords = currentProductCategoryContentWrapper.get("HTML_PAGE_META_KEY");
    if(UtilValidate.isNotEmpty(metaKeywords)) {
        context.metaKeywords = metaKeywords;
    }
    String metaDescription = currentProductCategoryContentWrapper.get("HTML_PAGE_META_DESC");
    if(UtilValidate.isNotEmpty(metaDescription)) {
        context.metaDescription = metaDescription;
    }
 
 } else 
 {
    searchResultsTitle = UtilProperties.getMessage("OSafeUiLabels", "SearchResultsTitle", locale);
    if(request.getAttribute("completeDocumentList"))
    {
        searchResultCount = request.getAttribute("completeDocumentList").size();
        String SearchResultsCountsTitle = UtilProperties.getMessage("OSafeUiLabels", "SearchResultsCountsTitle", UtilMisc.toMap("searchText", searchText,"searchResultCount",searchResultCount), locale)
        context.pageTitle = SearchResultsCountsTitle;
    }
    context.title = searchResultsTitle + " - " + searchText;
 }

previousParamsMap = {};
previousParamsList = [];
previousParams = request.getQueryString();
if (previousParams) 
{
    previousParams = UtilHttp.stripNamedParamsFromQueryString(previousParams, ["start", "rows", "sortResults" , "sortBtn"]);
    previousParams = "?" + previousParams;

    previousParamsMap = UtilHttp.getParameterMap(request,UtilMisc.toSet("start", "rows", "sortResults" , "sortBtn"),false);
    previousParamsList = UtilMisc.toList(previousParamsMap.keySet());
} else 
{
    previousParams = "";
}
context.previousParams = previousParams;
context.previousParamsList = previousParamsList;
context.previousParamsMap = previousParamsMap;

filterGroup = parameters.filterGroup ?: "";
if (UtilValidate.isNotEmpty(filterGroup))
{
  facetGroups = FastList.newInstance();
  filterGroupArr = StringUtil.split(filterGroup, "|");
  for (int i = 0; i < filterGroupArr.size(); i++)
  {
        facetAndValue = FastMap.newInstance();
        facetGroup = filterGroupArr[i];
        facetGroup =StringUtils.replace(facetGroup, "_", " ");

        facetGroupSplit =facetGroup.split(":");
	    facet = facetGroupSplit[0];
		facetValue = facetGroupSplit[1];

        facetSplit = facet.split(" ");
	    facetConstant = facetSplit[0];
	    if(facetSplit.size() > 1)
	    {
		    facet = facetSplit[1];
		}

        facetAndValue.put("facet",facet.toUpperCase());
        facetAndValue.put("facetValue",facetValue);
        
        facetGroups.add(facetAndValue);
  }

  context.facetGroups = facetGroups;
}
facetGroupMatch = Util.getProductStoreParm(request,"FACET_GROUP_VARIANT_MATCH");
if (UtilValidate.isNotEmpty(facetGroupMatch))
{
	searchText = parameters.searchText ?: "";
	if (UtilValidate.isNotEmpty(searchText))
	{
      facetGroups = FastList.newInstance();
   	  paramsExpr = FastList.newInstance();
	  exprBldr =  new EntityConditionBuilder();
      List exprListForParameters = [];
      orderBy = ["description"];
   
	  searchTextArr = StringUtil.split(searchText, " ");
      for (List textSearched: searchTextArr) 
       {
          text =textSearched.trim().toUpperCase();
          exprListForParameters.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.LIKE, EntityFunction.UPPER(text + "%")));
       }
      paramCond = EntityCondition.makeCondition(exprListForParameters, EntityOperator.OR); 
      featureTypeCond = EntityCondition.makeCondition("productFeatureTypeId", EntityOperator.EQUALS, facetGroupMatch.toUpperCase());
      paramCond = EntityCondition.makeCondition([paramCond, featureTypeCond], EntityOperator.AND);
      productFeatureList = delegator.findList("ProductFeature",paramCond, null, orderBy, null, true);
      if (UtilValidate.isNotEmpty(productFeatureList))
      {
        for (GenericValue productFeature: productFeatureList)
        {
      
            facetAndValue = FastMap.newInstance();
            facetAndValue.put("facet",productFeature.productFeatureTypeId);
            facetAndValue.put("facetValue",productFeature.description);
            facetGroups.add(facetAndValue);
        }
      }
      context.searchTextGroups = facetGroups;
	}
}
//Get Sequence for PDP Div Containers 
XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafe.properties", "ecommerce-UiSequence-xml-file"), context);
searchRestrictionMap = FastMap.newInstance();
searchRestrictionMap.put("screen", "Y");
uiSequenceSearchList =  OsafeManageXml.getSearchListFromXmlFile(XmlFilePath, searchRestrictionMap, uiSequenceScreen,true, false,true);

for(Map uiSequenceScreenMap : uiSequenceSearchList) 
{
     if ((uiSequenceScreenMap.value instanceof String) && (UtilValidate.isInteger(uiSequenceScreenMap.value))) 
     {
         if (UtilValidate.isNotEmpty(uiSequenceScreenMap.value)) 
         {
             uiSequenceScreenMap.value = Integer.parseInt(uiSequenceScreenMap.value);
         } else {
             uiSequenceScreenMap.value = 0;
         }
     }
}
uiSequenceSearchList = UtilMisc.sortMaps(uiSequenceSearchList, UtilMisc.toList("value"));
context.divSequenceList = uiSequenceSearchList;

