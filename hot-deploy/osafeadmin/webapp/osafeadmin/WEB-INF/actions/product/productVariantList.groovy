package product;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductContentWrapper;
import org.apache.commons.lang.StringEscapeUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.entity.GenericValue;

if (UtilValidate.isNotEmpty(parameters.productId)) 
{
    product = delegator.findOne("Product",["productId":parameters.productId], false);
    if("Y".equals(product.getString("isVariant")))
	{
	    GenericValue parent = ProductWorker.getParentProduct(product.productId, delegator);
	    if(UtilValidate.isNotEmpty(parent))
	    {
	        product = parent;
	    }
	}
    context.product = product;
    if (UtilValidate.isNotEmpty(product)) 
    {
        productContentWrapper = new ProductContentWrapper(product, request);
        String productDetailHeading = "";
        if (UtilValidate.isNotEmpty(productContentWrapper))
        {
            productDetailHeading = StringEscapeUtils.unescapeHtml(productContentWrapper.get("PRODUCT_NAME").toString());
            if (UtilValidate.isEmpty(productDetailHeading)) 
            {
                productDetailHeading = product.get("productName");
            }
            if (UtilValidate.isEmpty(productDetailHeading)) 
            {
                productDetailHeading = product.get("internalName");
            }
            context.productDetailHeading = productDetailHeading;
            context.productContentWrapper = productContentWrapper;
        }
        productAssocs = product.getRelated("MainProductAssoc");
        context.resultList = EntityUtil.filterByAnd(productAssocs, UtilMisc.toMap("productAssocTypeId", "PRODUCT_VARIANT")); 
    }
}