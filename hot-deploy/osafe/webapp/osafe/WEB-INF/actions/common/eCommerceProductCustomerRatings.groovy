package common;

import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.store.*;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import javolution.util.FastList;
import javolution.util.FastSet;
import javolution.util.FastMap;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.webapp.stats.VisitHandler;

dispatcher = request.getAttribute("dispatcher");
webSiteId = CatalogWorker.getWebSiteId(request);
catalogName = CatalogWorker.getCatalogName(request);
prodCatalogId = CatalogWorker.getCurrentCatalogId(request);
productStoreId = ProductStoreWorker.getProductStoreId(request);
userLogin = session.getAttribute("userLogin");
productId = parameters.productId;
listSize=0;
viewIndex = 1;
viewSize =5;
lowIndex = 0;
highIndex = 0;


if (UtilValidate.isNotEmpty(productId))
{
    GenericValue gvProduct =  delegator.findOne("Product", UtilMisc.toMap("productId",productId), true);

    // first make sure this isn't a variant that has an associated virtual product, if it does show that instead of the variant
    virtualProductId = ProductWorker.getVariantVirtualId(gvProduct);
    if (virtualProductId) {
        productId = virtualProductId;
        gvProduct = delegator.findByPrimaryKeyCache("Product", [productId : productId]);
    }
    
    if (UtilValidate.isNotEmpty(gvProduct))
    {
        context.product = gvProduct;
        context.productId = gvProduct.productId;
	    decimals=Integer.parseInt("1");
	    rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
	    context.put("decimals",decimals);
	    context.put("rounding",rounding);
	    // get the average rating
	    productCalculatedInfo = delegator.findByPrimaryKey("ProductCalculatedInfo", UtilMisc.toMap("productId", productId));
	    context.put("customerRatingDir","5_0");
	    if (UtilValidate.isNotEmpty(productCalculatedInfo))
	    {
	     averageRating= productCalculatedInfo.getBigDecimal("averageCustomerRating");
         if (UtilValidate.isNotEmpty(averageRating))
         {
	       averageCustomerRating= averageRating.setScale(1,rounding);
	       context.put("customerRating", averageCustomerRating);
	       context.put("customerRatingDir",UtilFormatOut.replaceString(""+averageCustomerRating,".","_"));
         }
	    }
	    
       reviewByAnd = UtilMisc.toMap("statusId", "PRR_APPROVED", "productStoreId", productStoreId,"productId", productId);
       reviews = delegator.findByAnd("ProductReview", reviewByAnd);
	   context.put("reviewSize",reviews.size());
	    
    }    
   
}
