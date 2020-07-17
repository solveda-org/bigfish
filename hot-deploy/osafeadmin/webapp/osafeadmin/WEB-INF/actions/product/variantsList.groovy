package product;


import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.product.ProductContentWrapper;

if (UtilValidate.isNotEmpty(parameters.productId)) {
    product = delegator.findOne("Product",["productId":parameters.productId], true);
    context.product = product;
    if (product) 
     {
        productContentWrapper = new ProductContentWrapper(product, request);
        String productDetailHeading = "";
        if(productContentWrapper)
        {
            productDetailHeading = productContentWrapper.get("PRODUCT_NAME");
            if (UtilValidate.isEmpty(productDetailHeading)) {
                productDetailHeading = product.get("productName");
            }
            if (UtilValidate.isEmpty(productDetailHeading)) {
                productDetailHeading = product.get("internalName");
            }
            context.productDetailHeading = productDetailHeading;
            context.productContentWrapper = productContentWrapper;
        }
        context.resultList = delegator.findByAnd("ProductAssoc", [productId : parameters.productId, productAssocTypeId : "PRODUCT_VARIANT"]);
     }
}