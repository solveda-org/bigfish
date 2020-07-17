package common;

import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.*;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

productStoreCatalogList  = CatalogWorker.getStoreCatalogs(request);
if (UtilValidate.isNotEmpty(productStoreCatalogList))
{
	productStoreCatalogList  = EntityUtil.filterByDate(productStoreCatalogList, true);
	gvProductStoreCatalog = EntityUtil.getFirst(productStoreCatalogList);
	String prodCatalogId = gvProductStoreCatalog.prodCatalogId;
	String topCategoryId = CatalogWorker.getCatalogTopCategoryId(request, prodCatalogId);
	if(UtilValidate.isNotEmpty(topCategoryId))
	{
	    context.topCategoryId = topCategoryId;
	}
	CategoryWorker.getRelatedCategories(request, "topLevelList", topCategoryId, true);
	
	categoryList = request.getAttribute("topLevelList");
	if (UtilValidate.isNotEmpty(categoryList)) 
	{
	    catContentWrappers = FastMap.newInstance();
	    CategoryWorker.getCategoryContentWrappers(catContentWrappers, categoryList, request);
	    context.catContentWrappers = catContentWrappers;
	}
}
