package product;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.store.*;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilValidate;

if (UtilValidate.isNotEmpty(parameters.productId)) {
    product = delegator.findOne("Product",["productId":parameters.productId], true);
    context.product = product;
    // get the product price and content wrapper
    if("Y".equals(product.getString("isVariant")))
	 {
		GenericValue parent = ProductWorker.getParentProduct(product.productId, delegator);
		if (parent != null)
		 {
			productContentWrapper = new ProductContentWrapper(parent, request);
		 }
	 }
    else
     {
        productContentWrapper = new ProductContentWrapper(product, request);
     }
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
    productAttr = delegator.findByAnd("ProductAttribute", UtilMisc.toMap("productId", parameters.productId));
    productAttrMap = FastMap.newInstance();
    if (UtilValidate.isNotEmpty(productAttr))
    {
        attrlIter = productAttr.iterator();
        while (attrlIter.hasNext()) {
            attr = (GenericValue) attrlIter.next();
            productAttrMap.put(attr.getString("attrName"),attr.getString("attrValue"));
        }
    }

    //Set Meta title, Description and Keywords
    String productName = productContentWrapper.get("PRODUCT_NAME");
    if (UtilValidate.isEmpty(productName)) {
        productName = gvProduct.productName;
    }
    if(UtilValidate.isNotEmpty(productName)) {
        context.defaultTitle = productName;
    }
    if(UtilValidate.isNotEmpty(productContentWrapper.get("DESCRIPTION"))) {
        context.defaultMetaKeywords = productContentWrapper.get("DESCRIPTION");
    }
    if(UtilValidate.isNotEmpty(productContentWrapper.get("LONG_DESCRIPTION"))) {
        context.defaultMetaDescription = productContentWrapper.get("LONG_DESCRIPTION");
    }
}