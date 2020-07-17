package product;

import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;

if (UtilValidate.isNotEmpty(parameters.productId)) 
{
	//checkout message
	add_product_id = StringUtils.trimToEmpty(parameters.add_product_id);
	prod_type = StringUtils.trimToEmpty(parameters.prod_type);
	
	if (UtilValidate.isNotEmpty(add_product_id) && UtilValidate.isNotEmpty(prod_type) && ProductWorker.isSellable(delegator, add_product_id))
	{
	   messageMap=[:];
	   if(prod_type.equals("Variant"))
	   {
		   GenericValue add_virtual_product = ProductWorker.getParentProduct(add_product_id, delegator);
		   add_product_name = ProductContentWrapper.getProductContentAsText(add_virtual_product, 'PRODUCT_NAME', request);
	   }
	   else if(prod_type.equals("FinishedGood"))
	   {
		   GenericValue finished_good = delegator.findByPrimaryKey("Product", [productId : add_product_id]);
		   add_product_name = ProductContentWrapper.getProductContentAsText(finished_good, 'PRODUCT_NAME', request);
	   }
	   messageMap.put("add_product_name", add_product_name);
	   context.showSuccessMessage = UtilProperties.getMessage("OSafeAdminUiLabels","CheckoutAddProductSuccess",messageMap, locale )
	}
	//end checkout message
	
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
        productAssoc = product.getRelated("MainProductAssoc");
        productAssoc = EntityUtil.filterByDate(productAssoc,true);
        compProductAssoc = EntityUtil.filterByAnd(productAssoc, UtilMisc.toMap("productAssocTypeId","PRODUCT_COMPLEMENT"));
	    compProductAssoc = EntityUtil.orderBy(compProductAssoc,UtilMisc.toList("sequenceNum"));
        context.compProductAssoc = compProductAssoc;
        
        accessProductAssoc = EntityUtil.filterByAnd(productAssoc, UtilMisc.toMap("productAssocTypeId","PRODUCT_ACCESSORY"));
	    accessProductAssoc = EntityUtil.orderBy(accessProductAssoc,UtilMisc.toList("sequenceNum"));
	    context.accessProductAssoc = accessProductAssoc;
     }
}