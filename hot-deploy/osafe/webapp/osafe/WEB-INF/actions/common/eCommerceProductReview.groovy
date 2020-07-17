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
import com.osafe.util.Util;

reviewMethod = Util.getProductStoreParm(request, "REVIEW_METHOD");
if((UtilValidate.isNotEmpty(reviewMethod)) && (reviewMethod.equalsIgnoreCase("BIGFISH")))
{
    dispatcher = request.getAttribute("dispatcher");
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
      if(UtilValidate.isNotEmpty(context.product))
      {
          gvProduct =  context.product;
      }
      else
      {
          gvProduct =  delegator.findOne("Product", UtilMisc.toMap("productId",productId), true);
      }
      if (UtilValidate.isNotEmpty(gvProduct))
      {
    
        // first make sure this isn't a variant that has an associated virtual product, if it does show that instead of the variant
        virtualProductId = ProductWorker.getVariantVirtualId(gvProduct);
        if (UtilValidate.isNotEmpty(virtualProductId)) 
        {
            productId = virtualProductId;
            gvProduct = delegator.findByPrimaryKeyCache("Product", [productId : productId]);
        }
        
        context.product = gvProduct;
        context.productId = gvProduct.productId;
        decimals=Integer.parseInt("1");
        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
        context.put("decimals",decimals);
        context.put("rounding",rounding);
    
        // get the product review(s)
        sortReviewBy = requestParameters.get("sortReviewBy");
        sortReviewCol2="-postedDateTime";
        sortReviewCol1="-productRating";
        if(UtilValidate.isNotEmpty(context.reviews))
        {
            reviews = context.reviews;
        }
        else
        {
            reviews =  gvProduct.getRelatedCache("ProductReview");
        }
        
        if (UtilValidate.isNotEmpty(reviews))
        {
            reviews = EntityUtil.filterByAnd(reviews, UtilMisc.toMap("statusId", "PRR_APPROVED", "productStoreId", productStoreId));
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
            reviews = EntityUtil.orderBy(reviews,UtilMisc.toList(sortReviewCol1));
            
        }
        
        context.put("sortReviewBy",sortReviewBy);
        
        if (UtilValidate.isNotEmpty(reviews))
        {
              listSize=reviews.size();
              // set the page parameters
                    try 
                    {
                        viewIndex = Integer.valueOf((String) request.getParameter("viewIndex")).intValue();
                    } 
                    catch (Exception e) 
                    {
                        viewIndex = 1;
                    }
            
                    try 
                    {
                        viewSize = Integer.valueOf((String) request.getParameter("viewSize")).intValue();
                    } 
                    catch (Exception e) 
                    {
                        viewSize = 5;
                     }
            
                    try 
                    {
                         if(viewIndex == 0)
                         {
                        	 viewIndex = viewIndex + 1;
                         }
                             
                         lowIndex = (viewIndex -1) * viewSize + 1;
                         if(lowIndex > listSize)
                         {
                        	 lowIndex = listSize;
                         }
                             
                                
                    } 
                    catch (Exception e) 
                    {
                        lowIndex = 0;
                    }
           
                    try 
                    {
                          if(viewIndex == 0)
                          {
                              viewIndex = viewIndex + 1;
                          } 
                          highIndex = viewIndex * viewSize;
                          if (highIndex > listSize)
                          {
                              highIndex = listSize;
                          }
                    } 
                    catch (Exception e) 
                    {
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
}
