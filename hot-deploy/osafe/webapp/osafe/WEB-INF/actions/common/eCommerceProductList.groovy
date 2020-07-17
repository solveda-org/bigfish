package common;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.category.CategoryContentWrapper;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.apache.commons.lang.StringUtils;
import com.osafe.util.Util;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.util.EntityUtil;
import com.osafe.services.OsafeManageXml;
import org.ofbiz.base.util.string.FlexibleStringExpander;

import com.osafe.services.SolrIndexDocument;
import com.osafe.services.CatalogUrlServlet;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import java.util.Map;

String productCategoryId = parameters.productCategoryId;
if(UtilValidate.isEmpty(productCategoryId))
{
	productCategoryId =request.getAttribute("productCategoryId");
}
GenericValue gvProductCategory = null;
if(UtilValidate.isNotEmpty(productCategoryId))
{
		gvProductCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId",productCategoryId), true);
		//set last known categoryId from PLP
		session.setAttribute("PLP_PRODUCT_CATEGORY_ID",productCategoryId);
}

String searchText = com.osafe.util.Util.stripHTML(parameters.searchText);
String searchTextSpellCheck = com.osafe.util.Util.stripHTML(parameters.searchTextSpellCheck);
context.pageSubTitle = "";

if (UtilValidate.isNotEmpty(searchText))
{
	context.productListFormSearchText = searchText;
}
if (UtilValidate.isNotEmpty(gvProductCategory)) 
{
    CategoryContentWrapper currentProductCategoryContentWrapper = new CategoryContentWrapper(gvProductCategory, request);
    context.currentProductCategory = gvProductCategory;

    //set Meta title, Description and Keywords
    String categoryName = "";
    categoryName = gvProductCategory.categoryName;

    if(UtilValidate.isNotEmpty(categoryName)) 
    {
        context.metaTitle = categoryName;
        context.pageTitle = categoryName;
    }
    if(UtilValidate.isNotEmpty(gvProductCategory.description)) 
    {
        context.metaKeywords = gvProductCategory.description;
    }
    if(UtilValidate.isNotEmpty(gvProductCategory.longDescription)) 
    {
        context.metaDescription = gvProductCategory.longDescription;
    }
    //override Meta title, Description and Keywords
    String metaTitle = currentProductCategoryContentWrapper.get("HTML_PAGE_TITLE");
    if(UtilValidate.isNotEmpty(metaTitle)) 
    {
        context.metaTitle = metaTitle;
    }
    String metaKeywords = currentProductCategoryContentWrapper.get("HTML_PAGE_META_KEY");
    if(UtilValidate.isNotEmpty(metaKeywords)) 
    {
        context.metaKeywords = metaKeywords;
    }
    String metaDescription = currentProductCategoryContentWrapper.get("HTML_PAGE_META_DESC");
    if(UtilValidate.isNotEmpty(metaDescription)) 
    {
        context.metaDescription = metaDescription;
    }
 
 } 
 else 
 {
    searchResultsTitle = UtilProperties.getMessage("OSafeUiLabels", "SearchResultsTitle", locale);
    if(request.getAttribute("completeDocumentList"))
    {
        String SearchResultsCountsTitle = "";
        String SearchResultsSubPageTitle = "";
        if(UtilValidate.isEmpty(searchTextSpellCheck))
        {
        	SearchResultsPageTitle = UtilProperties.getMessage("OSafeUiLabels", "SearchResultsFoundTitle", UtilMisc.toMap("searchText", searchText), locale)
        }
        else
        {
        	SearchResultsPageTitle = UtilProperties.getMessage("OSafeUiLabels", "SearchResultsNotFoundTitle", UtilMisc.toMap("searchText", searchText), locale)
        	SearchResultsSubPageTitle = UtilProperties.getMessage("OSafeUiLabels", "SearchResultsNotFoundSuggestionTitle", UtilMisc.toMap("searchTextSpellCheck", searchTextSpellCheck), locale)
        }
        if(UtilValidate.isEmpty(SearchResultsSubPageTitle))
        {
        	context.pageSubTitle = SearchResultsSubPageTitle;
        }
        context.pageTitle = SearchResultsPageTitle;
    }
    context.title = searchResultsTitle + " - " + searchText;
 }

previousParamsMap = {};
previousParamsList = [];
previousParams = request.getQueryString();
if (UtilValidate.isNotEmpty(previousParams)) 
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

//GET THE LIST OF FACET VALUES.
facetValueList = FastList.newInstance();
facetGroups = context.facetGroups;
facetGroupMatch = Util.getProductStoreParm(request,"FACET_GROUP_VARIANT_MATCH");

if(UtilValidate.isNotEmpty(facetGroups) && UtilValidate.isNotEmpty(facetGroupMatch))
{ 
	for(Map facet : facetGroups)
	{
		if(facetGroupMatch.toUpperCase() == facet.facet)
		{
			facetValueList.add(facet.facetValue);
		}
	}
}
context.facetValueList = facetValueList;

searchTextGroups = null;
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
	  searchTextGroups = facetGroups;
      context.searchTextGroups = facetGroups;
	}
}


//Refresh the product scroller Url List everytime a new PLP is displayed
productScrollerUrlList = session.getAttribute("productScrollerUrlList");
if(UtilValidate.isNotEmpty(productScrollerUrlList))
{
	session.removeAttribute("productScrollerUrlList");
}
//now build the list and add it to the session
productScrollerUrlList = FastList.newInstance();
//get related system parameters
plpFacetGroupVariantSwatch = Util.getProductStoreParm(request,"PLP_FACET_GROUP_VARIANT_SWATCH_IMG");
plpFacetGroupVariantSticky = Util.getProductStoreParm(request,"PLP_FACET_GROUP_VARIANT_PDP_MATCH");
facetGroupMatch = Util.getProductStoreParm(request,"FACET_GROUP_VARIANT_MATCH");
if (UtilValidate.isNotEmpty(plpFacetGroupVariantSwatch))
{
	plpFacetGroupVariantSwatch=plpFacetGroupVariantSwatch.toUpperCase();
}
if (UtilValidate.isNotEmpty(plpFacetGroupVariantSticky))
{
	plpFacetGroupVariantSticky=plpFacetGroupVariantSticky.toUpperCase();
}
featureValueSelected = "";
if (UtilValidate.isNotEmpty(facetGroupMatch))
{
	facetGroupMatch=facetGroupMatch.toUpperCase();
}
if ((UtilValidate.isNotEmpty(searchTextGroups)) && (UtilValidate.isNotEmpty(facetGroupMatch)))
{
	for (Map facet : searchTextGroups)
	{
		if (facetGroupMatch == facet.facet)
		{
			featureValueSelected=facet.facetValue;
		}
	}
}

//loop through each product and add URL to list
List productDocumentList = session.getAttribute("productDocumentList");
for (SolrIndexDocument productInfo : productDocumentList)
{
	productFriendlyUrl = "";
	productInfoId = productInfo.getProductId();
	if (UtilValidate.isNotEmpty(productInfoId))
	{
		gvproduct = delegator.findOne("Product",UtilMisc.toMap("productId",productInfoId), true);
	}
	//CHECK WE HAVE A DEFAULT PRODUCT CATEGORY THE PRODUCT IS MEMBER OF
	categoryId = "";
	if (UtilValidate.isEmpty(categoryId) && UtilValidate.isNotEmpty(gvproduct))
	{
		productCategoryMemberList = gvproduct.getRelatedCache("ProductCategoryMember");
		productCategoryMemberList = EntityUtil.filterByDate(productCategoryMemberList,true);
		productCategoryMemberList = EntityUtil.orderBy(productCategoryMemberList,UtilMisc.toList("sequenceNum"));
		if(UtilValidate.isNotEmpty(productCategoryMemberList))
		{
			productCategoryMember = EntityUtil.getFirst(productCategoryMemberList);
			categoryId = productCategoryMember.productCategoryId;
		}
	}
	productFeatureTypeExists = true;
	//if plpFacetGroupVariantSticky is defined then check if this product has that feature
	if (UtilValidate.isNotEmpty(plpFacetGroupVariantSticky) && UtilValidate.isNotEmpty(featureValueSelected))
	{
		productSelectableFeatureAndAppl = delegator.findByAndCache("ProductFeatureAndAppl", UtilMisc.toMap("productId",productInfoId, "productFeatureTypeId", plpFacetGroupVariantSticky, "productFeatureApplTypeId", "SELECTABLE_FEATURE", "description", featureValueSelected), UtilMisc.toList("sequenceNum"));
		if (productSelectableFeatureAndAppl.size() < 1)
		{
			productFeatureTypeExists = false;
		}
	}
	//make friendly URL
	if((UtilValidate.isNotEmpty(productInfoId)) && (UtilValidate.isNotEmpty(categoryId)))
	{
		   if (UtilValidate.isNotEmpty(plpFacetGroupVariantSticky)  && UtilValidate.isNotEmpty(featureValueSelected) && productFeatureTypeExists)
		   {
			   productFriendlyUrl = CatalogUrlServlet.makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId='+productInfoId+'&productCategoryId='+categoryId+'&productFeatureType='+plpFacetGroupVariantSticky+':'+featureValueSelected);
		   }
		   //if sticky was not defined or if featureValueSelected is empty
		   if (UtilValidate.isEmpty(productFriendlyUrl))
		   {
			   productFriendlyUrl = CatalogUrlServlet.makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId='+productInfoId+'&productCategoryId='+categoryId);
		   }
	}
	
	//add productFriendlyUrl to the productScrollerList 
	if (UtilValidate.isNotEmpty(productFriendlyUrl))
	{
		productScrollerUrlList.add(productFriendlyUrl);
	}
}

//and add the list to the session
if (UtilValidate.isNotEmpty(productScrollerUrlList))
{
	session.setAttribute("productScrollerUrlList",productScrollerUrlList);
}















