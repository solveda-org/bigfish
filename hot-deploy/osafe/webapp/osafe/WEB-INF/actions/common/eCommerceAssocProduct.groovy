package common;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductWorker;
import javolution.util.FastList;

complementProducts = FastList.newInstance();
accessoryProducts = FastList.newInstance();

String productId = parameters.productId;
if (UtilValidate.isNotEmpty(productId))
{
    gvProduct =  delegator.findOne("Product", UtilMisc.toMap("productId",productId), true);

    // first make sure this isn't a variant that has an associated virtual product, if it does show that instead of the variant
    virtualProductId = ProductWorker.getVariantVirtualId(gvProduct);
    if (UtilValidate.isNotEmpty(virtualProductId)) 
    {
        productId = virtualProductId;
        gvProduct =  delegator.findOne("Product", UtilMisc.toMap("productId",productId), true);
    }

    if (UtilValidate.isNotEmpty(gvProduct))
    {
        allAssocProducts = gvProduct.getRelatedCache("MainProductAssoc");
        allAssocProducts = EntityUtil.filterByDate(allAssocProducts,true);
        
        allAssocComplementProducts = EntityUtil.filterByAnd(allAssocProducts, UtilMisc.toMap("productAssocTypeId", "PRODUCT_COMPLEMENT"));
	    allAssocComplementProducts = EntityUtil.orderBy(allAssocComplementProducts,UtilMisc.toList("sequenceNum"));

		for (GenericValue compProduct: allAssocComplementProducts)
		{
		    if (ProductWorker.isSellable(delegator, compProduct.productIdTo))
		    {
			    complementProducts.add(compProduct);
		    }
		}
		
		allAssocAccessoryProducts = EntityUtil.filterByAnd(allAssocProducts, UtilMisc.toMap("productAssocTypeId", "PRODUCT_ACCESSORY"));
	    allAssocAccessoryProducts = EntityUtil.orderBy(allAssocAccessoryProducts,UtilMisc.toList("sequenceNum"));

		for (GenericValue accessProduct: allAssocAccessoryProducts)
		{
		    if (ProductWorker.isSellable(delegator, accessProduct.productIdTo))
		    {
			    accessoryProducts.add(accessProduct);
		    }
		}
    }

    if (UtilValidate.isNotEmpty(complementProducts))
    {
        context.complementProducts = complementProducts;
    }
    
    if (UtilValidate.isNotEmpty(accessoryProducts))
    {
        context.accessoryProducts = accessoryProducts;
    }
}