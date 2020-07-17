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

catalogId = CatalogWorker.getCurrentCatalogId(request);
currentCatalogId = catalogId;

String productId = parameters.productId;
if (UtilValidate.isNotEmpty(productId))
{
    gvProduct =  delegator.findOne("Product", UtilMisc.toMap("productId",productId), true);

    // first make sure this isn't a variant that has an associated virtual product, if it does show that instead of the variant
    virtualProductId = ProductWorker.getVariantVirtualId(gvProduct);
    if (virtualProductId) 
    {
        productId = virtualProductId;
        gvProduct =  delegator.findOne("Product", UtilMisc.toMap("productId",productId), true);
    }

    if (UtilValidate.isNotEmpty(gvProduct))
    {
        recommendProducts = gvProduct.getRelatedCache("AssocProductAssoc");
        recommendProducts = EntityUtil.filterByDate(recommendProducts,true);
        recommendProducts = EntityUtil.filterByAnd(recommendProducts, UtilMisc.toMap("productAssocTypeId","PRODUCT_COMPLEMENT"));
	    recommendProducts = EntityUtil.orderBy(recommendProducts,UtilMisc.toList("sequenceNum"));
    }

    if (UtilValidate.isNotEmpty(recommendProducts))
    {
        context.recommendProducts = recommendProducts;
    }

}

