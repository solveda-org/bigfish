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

        ProductContentWrapper productContentWrapper = new ProductContentWrapper(gvProduct, request);
        context.productContentWrapper = productContentWrapper;
        context.currentProduct = gvProduct;



        String productName = productContentWrapper.get("PRODUCT_NAME");
        if (!productName) {
            productName = gvProduct.productName;
        }
       context.productName = productName;

    // get the product review(s)
    reviewByAnd = UtilMisc.toMap("statusId", "PRR_APPROVED", "productStoreId", productStoreId,"productId", productId);
    sortReviewBy = requestParameters.get("sortReviewBy");
    sortReviewCol2="-postedDateTime";
    sortReviewCol1="-productRating";
    if (UtilValidate.isNotEmpty(sortReviewBy))
    {
	    if ("-productRating".equals(sortReviewBy) || "productRating".equals(sortReviewBy))
	    {
           sortReviewCol1=sortReviewBy;
	       sortReviewCol2="-postedDateTime";
	    }
	    else
	    {
	       sortReviewCol1=sortReviewBy;
	       sortReviewCol2="-productRating";
	    }
    }
    else
    {
      sortReviewBy="-productRating";
    }
    context.put("sortReviewBy",sortReviewBy);
    reviews = delegator.findByAnd("ProductReview", reviewByAnd, UtilMisc.toList(sortReviewCol1,sortReviewCol2));
    if (UtilValidate.isNotEmpty(reviews))
    {
  		listSize=reviews.size();
		// set the page parameters
				try {
				    viewIndex = Integer.valueOf((String) request.getParameter("viewIndex")).intValue();
				} catch (Exception e) {
				    viewIndex = 1;
				}
		//
				try {
				    viewSize = Integer.valueOf((String) request.getParameter("viewSize")).intValue();
				} catch (Exception e) {
				    viewSize = 5;
		 	    }
		//
				try {
				     if(viewIndex == 0)
				            viewIndex = viewIndex + 1;
				     lowIndex = (viewIndex -1) * viewSize + 1;
				     if(lowIndex > listSize)
				          lowIndex = listSize;
				            
				} catch (Exception e) {
				    lowIndex = 0;
				}
		//
				try {
				      if(viewIndex == 0)
				         viewIndex = viewIndex + 1;
				        
				       highIndex = viewIndex * viewSize;
				       if (highIndex > listSize)
				            highIndex = listSize;
				        
				} catch (Exception e) {
				    highIndex = 0;
				}
				
		if (listSize > 0)
		  {
			subList = reviews.subList(lowIndex-1, highIndex);            
			context.put("productReviews", subList);
		  }
        viewPages= (listSize / viewSize).intValue();
		if (listSize % viewSize != 0)
		{
		  viewPages = viewPages +1;
		}


		context.put("viewPages", viewPages);
		context.put("listSize", listSize);
		context.put("viewIndex", viewIndex);
		context.put("viewSize", viewSize);
		context.put("lowIndex", lowIndex);
		context.put("highIndex", highIndex);
        
        
    }    
   }
}
