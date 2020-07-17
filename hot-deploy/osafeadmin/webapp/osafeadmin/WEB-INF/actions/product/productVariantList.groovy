package product;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductContentWrapper;
import org.apache.commons.lang.StringEscapeUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastMap;

if (UtilValidate.isNotEmpty(parameters.productId)) 
{
    product = delegator.findOne("Product",["productId":parameters.productId], false);
    if("Y".equals(product.getString("isVariant")))
	{
	    GenericValue parent = ProductWorker.getParentProduct(product.productId, delegator);
	    if(UtilValidate.isNotEmpty(parent))
	    {
	        product = parent;
	    }
	}
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
        productAssocs = product.getRelated("MainProductAssoc");
        context.resultList = EntityUtil.filterByAnd(productAssocs, UtilMisc.toMap("productAssocTypeId", "PRODUCT_VARIANT")); 
    }
    
    //BUILD CONTEXT MAP FOR PRODUCT_FEATURE_TYPE_ID and DESCRIPTION(EITHER FROM PRODUCT_FEATURE_GROUP OR PRODUCT_FEATURE_TYPE)
    Map productFeatureTypesMap = FastMap.newInstance();
    productFeatureTypesList = delegator.findList("ProductFeatureType", null, null, null, null, false);

    //get the whole list of ProductFeatureGroup and ProductFeatureGroupAndAppl
    productFeatureGroupList = delegator.findList("ProductFeatureGroup", null, null, null, null, false);
    productFeatureGroupAndApplList = delegator.findList("ProductFeatureGroupAndAppl", null, null, null, null, false);
    productFeatureGroupAndApplList = EntityUtil.filterByDate(productFeatureGroupAndApplList);

    if(UtilValidate.isNotEmpty(productFeatureTypesList))
    {
        for (GenericValue productFeatureType : productFeatureTypesList)
        {
        	//filter the ProductFeatureGroupAndAppl list based on productFeatureTypeId to get the ProductFeatureGroupId
        	productFeatureGroupAndAppls = EntityUtil.filterByAnd(productFeatureGroupAndApplList, UtilMisc.toMap("productFeatureTypeId", productFeatureType.productFeatureTypeId));
        	description = "";
        	if(UtilValidate.isNotEmpty(productFeatureGroupAndAppls))
        	{
        		productFeatureGroupAndAppl = EntityUtil.getFirst(productFeatureGroupAndAppls);
            	productFeatureGroups = EntityUtil.filterByAnd(productFeatureGroupList, UtilMisc.toMap("productFeatureGroupId", productFeatureGroupAndAppl.productFeatureGroupId));
            	productFeatureGroup = EntityUtil.getFirst(productFeatureGroups);
            	description = productFeatureGroup.description;
        	}
        	else
        	{
        		description = productFeatureType.description;
        	}
        	productFeatureTypesMap.put(productFeatureType.productFeatureTypeId,description);
        }
    	
    }
    context.productFeatureTypesMap = productFeatureTypesMap;
}