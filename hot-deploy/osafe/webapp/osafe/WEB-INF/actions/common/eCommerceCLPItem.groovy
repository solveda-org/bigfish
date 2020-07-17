package common;

import org.ofbiz.base.util.UtilValidate;
import com.osafe.control.SeoUrlHelper;
import com.osafe.util.Util;

clpItem = request.getAttribute("clpItem");
if(UtilValidate.isNotEmpty(clpItem)) 
{
    categoryImageUrl = "";
    categoryName =  "";
    categorySupportingText = "";
    productCategoryId = "";
    categoryImageUrl = "";
    productCategoryUrl = "";
    
    if(UtilValidate.isNotEmpty(clpItem.displayImage))
    {
        categoryImageUrl = clpItem.displayImage;
    }
    if(UtilValidate.isNotEmpty(clpItem.displayName))
    {
        categoryName = clpItem.displayName;
    }
    if(UtilValidate.isNotEmpty(clpItem.supportingText))
    {
        categorySupportingText = clpItem.supportingText;
    }
    if(UtilValidate.isNotEmpty(clpItem.name))
    {
        productCategoryId = clpItem.name;
    }
    productCategoryUrl = SeoUrlHelper.makeSeoFriendlyUrl(request,'eCommerceProductList?productCategoryId='+productCategoryId);
    
    context.categoryImageUrl = categoryImageUrl;
    context.categoryName = categoryName;
    context.categorySupportingText = categorySupportingText;
    context.productCategoryId = productCategoryId;
    context.categoryImageUrl = categoryImageUrl;
    context.productCategoryUrl = productCategoryUrl;
}

