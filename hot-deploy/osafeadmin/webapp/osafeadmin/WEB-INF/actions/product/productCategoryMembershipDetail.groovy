package product;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.apache.commons.lang.StringEscapeUtils;
import org.ofbiz.base.util.UtilMisc;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilProperties;

if (UtilValidate.isNotEmpty(parameters.productId)) 
{
	
    product = delegator.findOne("Product",["productId":parameters.productId], false);
    context.product = product;
    if (UtilValidate.isNotEmpty(product)) 
     {
        productContentWrapper = new ProductContentWrapper(product, request);
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
        ecl = EntityCondition.makeCondition([
          EntityCondition.makeCondition("productId", EntityOperator.EQUALS, product.productId),
          EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.NOT_EQUAL, null),
         ],
        EntityOperator.AND);

        resultList = delegator.findList("ProductCategoryAndMember", ecl, null, null, null, false);
        context.resultList = EntityUtil.filterByDate(resultList,true);
     }
}