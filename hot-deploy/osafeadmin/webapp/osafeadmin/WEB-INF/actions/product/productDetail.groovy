package product;
import java.text.NumberFormat;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;
import org.ofbiz.webapp.taglib.*;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductSearch;
import org.ofbiz.product.product.ProductSearchSession;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.store.*;
import org.ofbiz.webapp.stats.VisitHandler;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import com.osafe.util.OsafeAdminUtil;

if (UtilValidate.isNotEmpty(parameters.productId)) {
    product = delegator.findOne("Product",["productId":parameters.productId], true);
    context.product = product;
    // get the product price and content wrapper
    if("Y".equals(product.getString("isVariant")))
	 {
		GenericValue parent = ProductWorker.getParentProduct(product.productId, delegator);
		if (parent != null)
		 {
		    productListPrice =  OsafeAdminUtil.getProductPrice(request, parent.productId, "LIST_PRICE");
		    productDefaultPrice = OsafeAdminUtil.getProductPrice(request, parent.productId, "DEFAULT_PRICE");
			productContentWrapper = new ProductContentWrapper(parent, request);
     		productContentList = delegator.findByAnd("ProductContent", UtilMisc.toMap("productId" ,parent.productId));
        	productContentList = EntityUtil.filterByDate(productContentList);
	     }
	 }
    else
     {
        productListPrice =  OsafeAdminUtil.getProductPrice(request, product.productId, "LIST_PRICE");
        productDefaultPrice = OsafeAdminUtil.getProductPrice(request, product.productId, "DEFAULT_PRICE");
        productContentWrapper = new ProductContentWrapper(product, request);

		productContentList = delegator.findByAnd("ProductContent", UtilMisc.toMap("productId" ,product.productId));
		productContentList = EntityUtil.filterByDate(productContentList);
     }
    

	if (UtilValidate.isNotEmpty(productContentList))
	{
            for (GenericValue productContent: productContentList) 
            {
    		   productContentTypeId = productContent.productContentTypeId;
    		   context.put(productContent.productContentTypeId,productContent);
            }
	}

    if (productListPrice) 
    {
        context.productListPrice = productListPrice;
    }
    
    if (productDefaultPrice) 
    {
        context.productDefaultPrice = productDefaultPrice;
    }
    String productDetailHeading = "";
    if(productContentWrapper)
    {
       context.productContentWrapper = productContentWrapper;
       productDetailHeading = productContentWrapper.get("PRODUCT_NAME");
    }
    if(product)
    {
        if (UtilValidate.isEmpty(productDetailHeading)) {
            productDetailHeading = product.get("productName");
        }
        if (UtilValidate.isEmpty(productDetailHeading)) {
            productDetailHeading = product.get("internalName");
        }
    	ecl = EntityCondition.makeCondition([
    	  EntityCondition.makeCondition("productId", EntityOperator.EQUALS, product.productId),
    	  EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.NOT_EQUAL, null),
         ],
    	EntityOperator.AND);

    	prodCatList = delegator.findList("ProductCategoryAndMember", ecl, null, null, null, false);
    	context.productCategory = EntityUtil.getFirst(prodCatList);
    }
    context.productDetailHeading = productDetailHeading;
}