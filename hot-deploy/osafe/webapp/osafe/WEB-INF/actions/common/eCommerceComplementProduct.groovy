package common;

import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.*;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import com.osafe.services.OsafeManageXml;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import javolution.util.FastList;

catalogId = CatalogWorker.getCurrentCatalogId(request);
currentCatalogId = catalogId;

recommendProducts = FastList.newInstance();

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
        allRecommendProducts = gvProduct.getRelatedCache("AssocProductAssoc");
        allRecommendProducts = EntityUtil.filterByDate(allRecommendProducts,true);
        allRecommendProducts = EntityUtil.filterByAnd(allRecommendProducts, UtilMisc.toMap("productAssocTypeId","PRODUCT_COMPLEMENT"));
	    allRecommendProducts = EntityUtil.orderBy(allRecommendProducts,UtilMisc.toList("sequenceNum"));

		for (GenericValue recProduct: allRecommendProducts)
		{
		   if (ProductWorker.isSellable(delegator, recProduct.productId))
		   {
			  recommendProducts.add(recProduct);
		   }
		}
		
		
    }

    if (UtilValidate.isNotEmpty(recommendProducts))
    {
        context.recommendProducts = recommendProducts;
    }

}

