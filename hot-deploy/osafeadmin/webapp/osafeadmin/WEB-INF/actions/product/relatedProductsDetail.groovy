package product;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

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
        context.resultList = EntityUtil.filterByDate(delegator.findByAnd("ProductAssoc", [productIdTo : parameters.productId, productAssocTypeId : "PRODUCT_COMPLEMENT"],UtilMisc.toList("sequenceNum ASC")),true);
     }
}