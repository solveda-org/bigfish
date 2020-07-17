package product;

import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.*;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

Delegator = request.getAttribute("delegator");

String productCategoryId = parameters.productCategoryId;

GenericValue gvProductCategory =  delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId",productCategoryId), false);
if (gvProductCategory) 
{
    CategoryContentWrapper currentProductCategoryContentWrapper = new CategoryContentWrapper(gvProductCategory, request);
    context.productCategory = gvProductCategory;
    context.currentProductCategoryContentWrapper = currentProductCategoryContentWrapper;
    context.productCategoryName = gvProductCategory.categoryName?gvProductCategory.categoryName:parameters.productCategoryId;
    context.title = "";
    String categoryName = currentProductCategoryContentWrapper.get("CATEGORY_NAME");
    if (UtilValidate.isEmpty(categoryName)) 
    {
        categoryName = gvProductCategory.categoryName;
    }
    if(UtilValidate.isNotEmpty(categoryName)) 
    {
        context.defaultTitle = categoryName;
    }
    if(UtilValidate.isNotEmpty(currentProductCategoryContentWrapper.get("DESCRIPTION"))) 
    {
        context.defaultMetaKeywords = currentProductCategoryContentWrapper.get("DESCRIPTION");
    }
    if(UtilValidate.isNotEmpty(currentProductCategoryContentWrapper.get("LONG_DESCRIPTION"))) 
    {
        context.defaultMetaDescription = currentProductCategoryContentWrapper.get("LONG_DESCRIPTION");
    }
}