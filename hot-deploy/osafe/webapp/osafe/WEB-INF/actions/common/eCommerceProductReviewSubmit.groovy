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
productAttrMap = FastMap.newInstance();
productId = request.getParameter("productId");
 context.put("title", productId);
   
   
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

        ProductContentWrapper productContentWrapper = new ProductContentWrapper(gvProduct, request);
        context.productContentWrapper = productContentWrapper;
        context.currentProduct = gvProduct;



       //Check Product Attribute for Meta Data Overrides
        productAttr = delegator.findByAndCache("ProductAttribute", UtilMisc.toMap("productId", gvProduct.productId));
        productAttrMap = FastMap.newInstance();

        if (UtilValidate.isNotEmpty(productAttr))
        {
            attrlIter = productAttr.iterator();
            while (attrlIter.hasNext())
              {
                   attr = (GenericValue) attrlIter.next();
                   productAttrMap.put(attr.getString("attrName"),attr.getString("attrValue"));
              }

       }


       //Set Meta title, Description and Keywords

        String productName = productContentWrapper.get("PRODUCT_NAME");
        if (!productName) {
            productName = gvProduct.productName;
        }


        metaTitle=productAttrMap.get("SEO_TITLE");
        if (UtilValidate.isNotEmpty(metaTitle))
        {
            context.title = metaTitle;
        }
        else
        {
            context.title = productName;
        }

        //Set Meta title, Description and Keywords
        metaKeywords=productAttrMap.get("SEO_KEYWORDS");
        if (UtilValidate.isNotEmpty(metaKeywords))
        {
            context.metaKeywords = metaKeywords;
        }
        else
        {
            keywords = [];
            keywords.add(productName);
            members = delegator.findByAndCache("ProductCategoryMember", [productId : gvProduct.productId]);
            members.each { member ->
                category = member.getRelatedOneCache("ProductCategory");
                if (category.longDescription) {
                    keywords.add(category.longDescription);
                }
            }
            context.metaKeywords = StringUtil.join(keywords, ", ");
        }

        metaDescription=productAttrMap.get("SEO_DESCRIPTION");
        if (UtilValidate.isNotEmpty(metaDescription))
        {
            context.metaDescription = metaDescription;
        }
        else
        {
            context.metaDescription = productContentWrapper.get("LONG_DESCRIPTION");
        }
     }
}        
