package product;

import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.base.util.UtilValidate;
import com.osafe.util.OsafeAdminUtil;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;

if (UtilValidate.isNotEmpty(parameters.productId)) {
    product = delegator.findOne("Product",["productId":parameters.productId], true);
    context.product = product;
    // get the product price
    if("Y".equals(product.isVariant))
     {
        productVariantListPrice =  OsafeAdminUtil.getProductPrice(request, product.productId, "LIST_PRICE");
        productVariantSalePrice = OsafeAdminUtil.getProductPrice(request, product.productId, "DEFAULT_PRICE");
        GenericValue parent = ProductWorker.getParentProduct(product.productId, delegator);
        if (parent != null)
         {
            productListPrice =  OsafeAdminUtil.getProductPrice(request, parent.productId, "LIST_PRICE");
            productDefaultPrice = OsafeAdminUtil.getProductPrice(request, parent.productId, "DEFAULT_PRICE");
			productContentWrapper = new ProductContentWrapper(parent, request);
         }
         if (productVariantListPrice) {
             context.productVariantListPrice = productVariantListPrice;
         }
    
         if (productVariantSalePrice) {
             context.productVariantSalePrice = productVariantSalePrice;
         }
     }
    else
     {
        productListPrice =  OsafeAdminUtil.getProductPrice(request, product.productId, "LIST_PRICE");
        productDefaultPrice = OsafeAdminUtil.getProductPrice(request, product.productId, "DEFAULT_PRICE");
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
    
    if (productListPrice) {
        context.productListPrice = productListPrice;
    }
    
    if (productDefaultPrice) {
        context.productDefaultPrice = productDefaultPrice;
    }
    
 // get QUANTITY price break rules to show
    productPriceCondListAll = delegator.findByAnd("ProductPriceCond", [inputParamEnumId: "PRIP_PRODUCT_ID", condValue: product.productId],["productPriceRuleId ASC"]);
    productPriceCondList = FastList.newInstance();
    int productPriceCondListSize = 0;
    if (UtilValidate.isNotEmpty(productPriceCondListAll))
    {
        for (GenericValue priceCond: productPriceCondListAll) {
            priceRule = priceCond.getRelatedOne("ProductPriceRule");
            prdQtyBreakIdCondList = delegator.findByAnd("ProductPriceCond", [inputParamEnumId: "PRIP_QUANTITY", productPriceRuleId: priceRule.productPriceRuleId],["productPriceRuleId"]);
            if (UtilValidate.isNotEmpty(prdQtyBreakIdCondList)) {
              //Check for Active Price Rule
                List<GenericValue> productPriceRuleList = delegator.findByAnd("ProductPriceRule", UtilMisc.toMap("productPriceRuleId",priceRule.productPriceRuleId));
                productPriceRuleList = EntityUtil.filterByDate(productPriceRuleList);
                if(UtilValidate.isNotEmpty(productPriceRuleList)) {
                    productPriceCondList.add(priceCond);
                    context.prdQtyBreakIdCondList = prdQtyBreakIdCondList;
                    productPriceCondListSize = productPriceCondListSize + 1;
                }
            }
        }
    }
    if(productPriceCondListSize > 0) {
        context.productPriceCondList = productPriceCondList;
        context.productPriceCondListSize = productPriceCondListSize;
    }
}