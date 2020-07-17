package product;

import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.base.util.UtilValidate;
import com.osafe.util.OsafeAdminUtil;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import org.apache.commons.lang.StringEscapeUtils;
import org.ofbiz.entity.GenericValue;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilProperties;

if (UtilValidate.isNotEmpty(parameters.productId)) 
{

    product = delegator.findOne("Product",["productId":parameters.productId], false);
    context.product = product;
    
    virtualProductPriceCondList = FastList.newInstance();
    int virtualProductPriceCondListSize = 0;
    
    // get the product price
    if("Y".equals(product.isVariant))
    {
        productVariantListPrice =  OsafeAdminUtil.getProductPrice(request, product.productId, "LIST_PRICE");
        productVariantSalePrice = OsafeAdminUtil.getProductPrice(request, product.productId, "DEFAULT_PRICE");
        productVariantRecurringPrice = OsafeAdminUtil.getProductPrice(request, product.productId, "DEFAULT_PRICE","RECURRING_CHARGE");
        GenericValue parent = ProductWorker.getParentProduct(product.productId, delegator);
        if (UtilValidate.isNotEmpty(parent))
         {
            productListPrice =  OsafeAdminUtil.getProductPrice(request, parent.productId, "LIST_PRICE");
            productDefaultPrice = OsafeAdminUtil.getProductPrice(request, parent.productId, "DEFAULT_PRICE");
            productRecurringPrice = OsafeAdminUtil.getProductPrice(request, parent.productId, "DEFAULT_PRICE","RECURRING_CHARGE");
			productContentWrapper = new ProductContentWrapper(parent, request);
         }
         if (productVariantListPrice) 
         {
             context.productVariantListPrice = productVariantListPrice;
         }
    
         if (productVariantSalePrice) 
         {
             context.productVariantSalePrice = productVariantSalePrice;
         }
         if (productVariantRecurringPrice) 
         {
             context.productVariantRecurringPrice = productVariantRecurringPrice;
         }
         
       // get QUANTITY price break rules for virtual
	    virtualProductPriceCondListAll = delegator.findByAnd("ProductPriceCond", [inputParamEnumId: "PRIP_PRODUCT_ID", condValue: parent.productId],["productPriceRuleId ASC"]);
	    
	    if (UtilValidate.isNotEmpty(virtualProductPriceCondListAll))
	    {
	        for (GenericValue priceCond: virtualProductPriceCondListAll) 
	        {
	            priceRule = priceCond.getRelatedOne("ProductPriceRule");
	            
	            prdQtyBreakIdCondList = priceRule.getRelated("ProductPriceCond");
		        prdQtyBreakIdCondList = EntityUtil.filterByAnd(prdQtyBreakIdCondList,UtilMisc.toMap("inputParamEnumId","PRIP_QUANTITY"));
		        prdQtyBreakIdCondList = EntityUtil.orderBy(prdQtyBreakIdCondList,UtilMisc.toList("productPriceRuleId"));
	            if (UtilValidate.isNotEmpty(prdQtyBreakIdCondList)) 
	            {
	              //Check for Active Price Rule
	                List<GenericValue> productPriceRuleList = delegator.findByAnd("ProductPriceRule", UtilMisc.toMap("productPriceRuleId",priceRule.productPriceRuleId));
	                productPriceRuleList = EntityUtil.filterByDate(productPriceRuleList);
	                if(UtilValidate.isNotEmpty(productPriceRuleList)) 
	                {
	                    virtualProductPriceCondList.add(priceCond);
	                    context.prdQtyBreakIdCondList = prdQtyBreakIdCondList;
	                    virtualProductPriceCondListSize = virtualProductPriceCondListSize + 1;
	                }
	            }
	        }
	    }
    }
    else
    {
        productListPrice =  OsafeAdminUtil.getProductPrice(request, product.productId, "LIST_PRICE");
        productDefaultPrice = OsafeAdminUtil.getProductPrice(request, product.productId, "DEFAULT_PRICE");
        productRecurringPrice = OsafeAdminUtil.getProductPrice(request, product.productId, "DEFAULT_PRICE","RECURRING_CHARGE");
        productContentWrapper = new ProductContentWrapper(product, request);
    }
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
    
    if (UtilValidate.isNotEmpty(productListPrice))
    {
        context.productListPrice = productListPrice;
    }
    
    if (UtilValidate.isNotEmpty(productDefaultPrice)) 
    {
        context.productDefaultPrice = productDefaultPrice;
    }
    
    if (UtilValidate.isNotEmpty(productRecurringPrice)) 
    {
        context.productRecurringPrice = productRecurringPrice;
    }
    
   // get QUANTITY price break rules to show
    productPriceCondListAll = delegator.findByAnd("ProductPriceCond", [inputParamEnumId: "PRIP_PRODUCT_ID", condValue: product.productId],["productPriceRuleId ASC"]);
    productPriceCondList = FastList.newInstance();
    int productPriceCondListSize = 0;
    if (UtilValidate.isNotEmpty(productPriceCondListAll))
    {
        for (GenericValue priceCond: productPriceCondListAll) 
        {
            priceRule = priceCond.getRelatedOne("ProductPriceRule");
            
            prdQtyBreakIdCondList = priceRule.getRelated("ProductPriceCond");
	        prdQtyBreakIdCondList = EntityUtil.filterByAnd(prdQtyBreakIdCondList,UtilMisc.toMap("inputParamEnumId","PRIP_QUANTITY"));
	        prdQtyBreakIdCondList = EntityUtil.orderBy(prdQtyBreakIdCondList,UtilMisc.toList("productPriceRuleId"));
            if (UtilValidate.isNotEmpty(prdQtyBreakIdCondList)) 
            {
              //Check for Active Price Rule
                List<GenericValue> productPriceRuleList = delegator.findByAnd("ProductPriceRule", UtilMisc.toMap("productPriceRuleId",priceRule.productPriceRuleId));
                productPriceRuleList = EntityUtil.filterByDate(productPriceRuleList);
                if(UtilValidate.isNotEmpty(productPriceRuleList)) 
                {
                    productPriceCondList.add(priceCond);
                    context.prdQtyBreakIdCondList = prdQtyBreakIdCondList;
                    productPriceCondListSize = productPriceCondListSize + 1;
                }
            }
        }
    }
    if(productPriceCondListSize > 0) 
    {
        context.productPriceCondList = productPriceCondList;
        context.productPriceCondListSize = productPriceCondListSize;
    }
    if(virtualProductPriceCondListSize > 0) 
    {
        context.virtualProductPriceCondList = virtualProductPriceCondList;
        context.virtualProductPriceCondListSize = virtualProductPriceCondListSize;
    }
}