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
import javolution.util.FastList;
import org.apache.commons.lang.StringEscapeUtils;

if (UtilValidate.isNotEmpty(parameters.productId)) 
{
    product = delegator.findOne("Product",["productId":parameters.productId], false);
    virtualProductContentList = FastList.newInstance();
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
     		productContentList = delegator.findByAnd("ProductContent", UtilMisc.toMap("productId" ,parameters.productId));
        	productContentList = EntityUtil.filterByDate(productContentList,true);
     		virtualProductContentList = delegator.findByAnd("ProductContent", UtilMisc.toMap("productId" ,parent.productId));
     		virtualProductContentList = EntityUtil.filterByDate(virtualProductContentList,true);
	     }
	 }
    else
     {
        productListPrice =  OsafeAdminUtil.getProductPrice(request, product.productId, "LIST_PRICE");
        productDefaultPrice = OsafeAdminUtil.getProductPrice(request, product.productId, "DEFAULT_PRICE");
        productContentWrapper = new ProductContentWrapper(product, request);

		productContentList = delegator.findByAnd("ProductContent", UtilMisc.toMap("productId" ,product.productId));
		productContentList = EntityUtil.filterByDate(productContentList,true);
     }
    // render content for varaint group use 0 index variant id 
    if (UtilValidate.isNotEmpty(context.passedVariantProductIds) || UtilValidate.isNotEmpty(parameters.variantProductIds))
    {
        String varaintProductId = "";
        if (UtilValidate.isNotEmpty(parameters.variantProductIds))
        {
            variantProductIdList = StringUtil.split(parameters.variantProductIds, "|");
            if (UtilValidate.isNotEmpty(variantProductIdList))
            {
                varaintProductId = variantProductIdList[0];
            }
        }
        else if(UtilValidate.isNotEmpty(context.passedVariantProductIds))
        {
            varaintProductId = context.passedVariantProductIds.first();
        }
		GenericValue parent = ProductWorker.getParentProduct(varaintProductId, delegator);
        productContentList = delegator.findByAnd("ProductContent", UtilMisc.toMap("productId" ,varaintProductId));
        productContentList = EntityUtil.filterByDate(productContentList,true);
 		virtualProductContentList = delegator.findByAnd("ProductContent", UtilMisc.toMap("productId" ,parent.productId));
 		virtualProductContentList = EntityUtil.filterByDate(virtualProductContentList,true);
    }

	if (UtilValidate.isNotEmpty(productContentList))
	{
            for (GenericValue productContent: productContentList) 
            {
    		   productContentTypeId = productContent.productContentTypeId;
    		   context.put(productContent.productContentTypeId,productContent);
            }
	}
	if (UtilValidate.isNotEmpty(virtualProductContentList))
	{
        for (GenericValue productContent: virtualProductContentList) 
        {
		   productContentTypeId = productContent.productContentTypeId;
		   if (productContentTypeId.equals("PRODUCT_NAME") || productContentTypeId.equals("PLP_LABEL") || productContentTypeId.equals("PDP_LABEL")) 
		   {
		       context.put(productContent.productContentTypeId,productContent);
		   }
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
    if (UtilValidate.isNotEmpty(productContentWrapper))
    {
       context.productContentWrapper = productContentWrapper;
       productDetailHeading = StringEscapeUtils.unescapeHtml(productContentWrapper.get("PRODUCT_NAME").toString());
    }
    if (UtilValidate.isNotEmpty(product))
    {
        if (UtilValidate.isEmpty(productDetailHeading)) 
        {
            productDetailHeading = product.get("productName");
        }
        if (UtilValidate.isEmpty(productDetailHeading)) 
        {
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