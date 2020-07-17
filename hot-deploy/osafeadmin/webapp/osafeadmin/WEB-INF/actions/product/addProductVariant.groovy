package product;


import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

if (UtilValidate.isNotEmpty(parameters.productId)) 
{
    product = delegator.findOne("Product",["productId":parameters.productId], false);
    context.product = product;
    if (UtilValidate.isNotEmpty(product)) 
     {
        productFeatureGroupAndAppls = delegator.findByAnd("ProdFeaGrpAppAndProdFeaApp", [productId : parameters.productId, productFeatureApplTypeId : "SELECTABLE_FEATURE"], ['sequenceNum']);
        if (UtilValidate.isNotEmpty(productFeatureGroupAndAppls)) 
        {
            productFeatureGroupIds = EntityUtil.getFieldListFromEntityList(productFeatureGroupAndAppls, "productFeatureGroupId", true);
            productSelectableFeatureTypes = delegator.findList("ProductFeatureGroup", EntityCondition.makeCondition("productFeatureGroupId", EntityOperator.IN, productFeatureGroupIds), null, null, null, false);
            context.productSelectableFeatureTypes = productSelectableFeatureTypes;
        }
        productFeatureGroupAndAppls = delegator.findByAnd("ProdFeaGrpAppAndProdFeaApp", [productId : parameters.productId, productFeatureApplTypeId : "DISTINGUISHING_FEAT"], ['sequenceNum']);
        if (UtilValidate.isNotEmpty(productFeatureGroupAndAppls)) 
        {
            productFeatureGroupIds = EntityUtil.getFieldListFromEntityList(productFeatureGroupAndAppls, "productFeatureGroupId", true);
            productDistinguishingFeatureTypes = delegator.findList("ProductFeatureGroup", EntityCondition.makeCondition("productFeatureGroupId", EntityOperator.IN, productFeatureGroupIds), null, null, null, false);
            context.productDistinguishingFeatureTypes = productDistinguishingFeatureTypes;
        }
     }
}