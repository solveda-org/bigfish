package common;

import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.*;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

catalogName = CatalogWorker.getCatalogName(request);
Delegator = request.getAttribute("delegator");

String productCategoryId = parameters.productCategoryId;

GenericValue gvProductCategory =  delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId",productCategoryId), true);
if (gvProductCategory) {
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
}