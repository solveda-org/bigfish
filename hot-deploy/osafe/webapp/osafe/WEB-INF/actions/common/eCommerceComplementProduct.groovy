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

Delegator = request.getAttribute("delegator");
catalogId = CatalogWorker.getCurrentCatalogId(request);
currentCatalogId = catalogId;

String productId = parameters.productId;
if (UtilValidate.isNotEmpty(productId))
{
    GenericValue gvProduct =  delegator.findOne("Product", UtilMisc.toMap("productId",productId), true);

    // first make sure this isn't a variant that has an associated virtual product, if it does show that instead of the variant
    virtualProductId = ProductWorker.getVariantVirtualId(gvProduct);
    if (virtualProductId) {
        productId = virtualProductId;
    }

    crossSellProducts = dispatcher.runSync("getAssociatedProducts", UtilMisc.toMap("productIdTo", productId, "type", "PRODUCT_COMPLEMENT", "checkViewAllow", Boolean.TRUE, "prodCatalogId", currentCatalogId));
    List recommendProducts = crossSellProducts.get("assocProducts");

    Map recommendedContentWrappers = null;
    if (recommendProducts)
    {
        recommendedContentWrappers = FastMap.newInstance();
        for (GenericValue assocProduct: recommendProducts)
        {
            GenericValue product = assocProduct.getRelatedOne("MainProduct");
            ProductContentWrapper productContentWrapper = new ProductContentWrapper(product, request);
            recommendedContentWrappers.put(product.get("productId"), productContentWrapper);

        }

        context.recommendedContentWrappers = recommendedContentWrappers;
        context.recommendProducts = recommendProducts;

     }

}

