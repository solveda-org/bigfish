package common;

import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.*;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

Delegator = request.getAttribute("delegator");

String productStoreId = productStore.productStoreId;
List productStoreCatalogList  = EntityUtil.filterByDate(delegator.findByAnd("ProductStoreCatalog", UtilMisc.toMap("productStoreId",productStoreId)), true);
GenericValue gvProductStoreCatalog = EntityUtil.getFirst(productStoreCatalogList);
String prodCatalogId = gvProductStoreCatalog.prodCatalogId;
String topCategoryId = CatalogWorker.getCatalogTopCategoryId(request, prodCatalogId);

CategoryWorker.getRelatedCategories(request, "topLevelList", topCategoryId, true);

categoryList = request.getAttribute("topLevelList");
if (categoryList) {
    catContentWrappers = FastMap.newInstance();
    CategoryWorker.getCategoryContentWrappers(catContentWrappers, categoryList, request);
    context.catContentWrappers = catContentWrappers;
}
