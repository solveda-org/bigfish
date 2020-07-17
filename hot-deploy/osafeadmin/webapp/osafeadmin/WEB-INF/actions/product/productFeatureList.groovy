package product;


import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.apache.commons.lang.StringEscapeUtils;

if (UtilValidate.isNotEmpty(parameters.productId)) 
{
    product = delegator.findOne("Product",["productId":parameters.productId], false);
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
        productFeatureAndAppls = delegator.findByAnd("ProductFeatureAndAppl", [productId : parameters.productId]);
        if (UtilValidate.isNotEmpty(productFeatureAndAppls)) 
        {
            productFeatureTypeIds = EntityUtil.getFieldListFromEntityList(productFeatureAndAppls, "productFeatureTypeId", true);
            productFeatureTypes = delegator.findList("ProductFeatureType", EntityCondition.makeCondition("productFeatureTypeId", EntityOperator.IN, productFeatureTypeIds), null, null, null, false);
            context.productFeatureTypes = productFeatureTypes;
            context.resultList = productFeatureTypes;
        }
     }
}