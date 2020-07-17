package common;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.category.CategoryWorker;
import javolution.util.FastMap;
import javolution.util.FastList;
import com.osafe.solr.SolrConstants;
import java.net.URLDecoder;

// Get the Cart and Prepare Size
shoppingCart = ShoppingCartEvents.getCartObject(request);
context.shoppingCart = shoppingCart;

CategoryWorker.getRelatedCategories(request, "topLevelList", CatalogWorker.getCatalogTopCategoryId(request, CatalogWorker.getCurrentCatalogId(request)), true);
curCategoryId = parameters.productCategoryId ?: parameters.CATEGORY_ID ?: context.productCategoryId ?: "";
curTopMostCategoryId = parameters.topMostProductCategoryId ?: parameters.TOP_MOST_CATEGORY_ID ?: "";

request.setAttribute("curCategoryId", curCategoryId);
request.setAttribute("curTopMostCategoryId", curTopMostCategoryId);

CategoryWorker.setTrail(request, curCategoryId);
topCategoryList = request.getAttribute("topLevelList");
if (UtilValidate.isNotEmpty(topCategoryList))
{
    catContentWrappers = FastMap.newInstance();
    CategoryWorker.getCategoryContentWrappers(catContentWrappers, topCategoryList, request);
    context.catContentWrappers = catContentWrappers;
}

searchText = com.osafe.util.Util.stripHTML(parameters.searchText) ?: "";
context.searchText = searchText;

context.topMostProductCategoryIdFacet = "N";
context.productCategoryIdFacet = "N";
filterGroup = parameters.filterGroup ?: "";
if (UtilValidate.isNotEmpty(filterGroup))
{
  facetGroups = FastList.newInstance();
  filterGroupValues = FastList.newInstance();
  removeFilterGroupValues = FastList.newInstance();
  filterGroupArr = StringUtil.split(filterGroup, "|");
  
  for (int i = 0; i < filterGroupArr.size(); i++)
  {
		  
        facetGroupName = filterGroupArr[i];
        if(UtilValidate.isNotEmpty(facetGroupName))
        {
        	String[] facetGroupNameArr = StringUtils.split(facetGroupName, ":");
    	    if(facetGroupNameArr.length > 1)
    	    {
    	        try
    	        {
    	      	    facetGroupName = facetGroupNameArr[0]+":"+URLDecoder.decode(facetGroupNameArr[1], SolrConstants.DEFAULT_ENCODING);
    	        }
    	        catch (UnsupportedEncodingException e)
    	        {
    	            facetGroupName = facetGroupNameArr[0]+":"+facetGroupNameArr[1];
    	        }
    	    }
        }
	    
        if(facetGroupName.indexOf("productCategoryId") > -1)
        {
            context.productCategoryIdFacet = "Y";
        } 
        else if (facetGroupName.indexOf("topMostProductCategoryId") > -1 )
        {
            context.topMostProductCategoryIdFacet = "Y";
        }
        else
        {
            // Only replace "_" underscores for facetGroups that are not productCategoryId
            // This is because the productCategoryId facetGroup value needs to be looked up
            // the actual "Product Category" information and we do not want to change the productCategoryId value.
            // ex. productCategoryId:SOME_PROD_CAT_ID needs to stay that value. In all other cases the
            // underscores can be replaced to get the description used for the breadcrumb
            facetGroupName =StringUtils.replace(facetGroupName, "_", " ");
        }
        removeValueList = FastList.newInstance();
        for(int j=0; j<filterGroupArr.size(); j++)
        {
            if(i != j)
            {
                removeValueList.add(filterGroupArr[j])
            }
        }
        facetGroups.add(facetGroupName);
        filterGroupValue = StringUtil.join(filterGroupArr.subList(0,i), "|");
        filterGroupValues.add(filterGroupValue);
        removeFilterGroupValue = StringUtil.join(removeValueList.subList(0,removeValueList.size()), "|");
        removeFilterGroupValues.add(removeFilterGroupValue);
  }
  context.removeFilterGroupValues = removeFilterGroupValues;
  context.facetGroups = facetGroups;
  context.filterGroupValues = filterGroupValues;
}

