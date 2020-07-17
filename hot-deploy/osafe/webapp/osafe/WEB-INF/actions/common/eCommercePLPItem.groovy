package common;

import javolution.util.FastMap;
import javolution.util.FastList;

import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import com.osafe.services.CatalogUrlServlet;
import com.osafe.util.Util;
import org.ofbiz.product.product.ProductWorker;

plpItem = request.getAttribute("plpItem");
plpItemId = request.getAttribute("plpItemId");
if(UtilValidate.isNotEmpty(plpItem) || UtilValidate.isNotEmpty(plpItemId)) {
    productName = "";
    categoryId = "";
    productInternalName = "";
    productURL = "";
    productImageUrl = "";
    productImageAlt = "";
    productImageAltUrl= "";
    price = "";
    productId="";

    // gets productId
    if(UtilValidate.isNotEmpty(plpItemId)) {
        productId = plpItemId;
    } else if (UtilValidate.isNotEmpty(plpItem)) {
        productId=plpItem.productId;
    } else {
        return;
    }

    product = delegator.findOne("Product",UtilMisc.toMap("productId",productId), true);
    context.priceMap = (dispatcher.runSync("calculateProductPrice", UtilMisc.toMap("product", product, "userLogin", userLogin)));
    
    //retrieves Product related data when Product Id was received.
    if(UtilValidate.isNotEmpty(plpItemId)) {
    	productContentWrapper = ProductContentWrapper.makeProductContentWrapper(product, request)
    	if (UtilValidate.isNotEmpty(productContentWrapper)){
        productName =  productContentWrapper.get("PRODUCT_NAME");  
        productImageUrl = productContentWrapper.get("SMALL_IMAGE_URL");
        productImageAltUrl = productContentWrapper.get("SMALL_IMAGE_ALT_URL");
    	}
        price = (dispatcher.runSync("calculateProductPrice", UtilMisc.toMap("product", product, "userLogin", userLogin))).defaultPrice ;
        productInternalName = product.internalName;
         if (UtilValidate.isNotEmpty(ProductWorker.getCurrentProductCategories(product))) {
             currentProductCategories =ProductWorker.getCurrentProductCategories(product);
             if (UtilValidate.isNotEmpty(currentProductCategories)) {
               categoryId = currentProductCategories[0].productCategoryId;
             }
         }
    }
    
    //retrieves product related Data when Solr document was received .
    else if (UtilValidate.isNotEmpty(plpItem)) {
        if (UtilValidate.isNotEmpty(plpItem.name)) {
            productName = plpItem.name;
        }
        if (UtilValidate.isNotEmpty(plpItem.productImageSmallUrl)) {
            productImageUrl = plpItem.productImageSmallUrl;
         }
        if (UtilValidate.isNotEmpty(parameters.productCategoryId)) {
            categoryId = parameters.productCategoryId;
         }
        if (UtilValidate.isNotEmpty(plpItem.price)) {
            price = plpItem.price;
         }
        if (UtilValidate.isNotEmpty(plpItem.internalName)) {
            productInternalName = plpItem.internalName;
         }    
        productId=plpItem.productId;
        if (UtilValidate.isNotEmpty(plpItem.productImageSmallAlt)) {
            productImageAlt = plpItem.productImageSmallAlt;
         }
         if (UtilValidate.isNotEmpty(plpItem.productImageSmallAltUrl)) {
            productImageAltUrl = plpItem.productImageSmallAltUrl;
         }
    }

    context.productName = productName;
    context.productId = productId;
    context.categoryId = categoryId;
    context.productInternalName = productInternalName;
    context.productImageUrl = productImageUrl;
    context.price=price;
    context.product = product;

    context.productImageAlt = productImageAlt;
    context.productImageAltUrl = productImageAltUrl;
    if (UtilValidate.isNotEmpty(ProductContentWrapper.getProductContentAsText(context.product, 'PLP_LABEL', request))){
        context.plpLabel = ProductContentWrapper.getProductContentAsText(context.product, 'PLP_LABEL', request);
    }
   
    context.productUrl = CatalogUrlServlet.makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId='+productId+'&productCategoryId='+categoryId);
    
    if(UtilValidate.isNotEmpty((context.product).isVirtual) && ((context.product).isVirtual).toUpperCase()== "Y"){
        productAssoc = delegator.findByAnd("ProductAssoc",UtilMisc.toMap("productId" ,context.productId,'productAssocTypeId','PRODUCT_VARIANT'));
        productVariantFeatureList = FastList.newInstance();
        productAssoc.each { pAssoc ->
          productFeatureAndAppl = delegator.findByAnd("ProductFeatureAppl",UtilMisc.toMap("productId" ,pAssoc.productIdTo,'productFeatureApplTypeId','STANDARD_FEATURE')) ;
          productIdTo = pAssoc.productIdTo;
          productTo = pAssoc.getRelatedOne("AssocProduct");
         
          productFeatureAndAppl.each { pFeatureAndAppl ->
             productFeature = pFeatureAndAppl.getRelatedOne("ProductFeature");
             productFeatureDesc = "";
             if(UtilValidate.isNotEmpty(productFeature.description)){
                 productFeatureDesc = productFeature.description;
             }
             productVariantFeatureMap = UtilMisc.toMap("productVariantId", productIdTo,"productVariant", productTo, "productFeatureId", pFeatureAndAppl.productFeatureId,"productFeatureDesc", productFeatureDesc);
             productVariantFeatureList.add(productVariantFeatureMap);
          }
      }
       context.productFeatureAndAppl = delegator.findByAndCache("ProductFeatureAndAppl",UtilMisc.toMap("productId" ,context.productId,'productFeatureTypeId',request.getAttribute("PRODUCT_STORE_PARM_FACET"),'productFeatureApplTypeId','SELECTABLE_FEATURE'));
       context.productVariantFeatureList = productVariantFeatureList;
    }
    featureValueSelected = request.getAttribute("featureValueSelected");
    productFeatureSelectVariantId="";
    productFeatureSelectVariantProduct = "";
    if(UtilValidate.isNotEmpty(featureValueSelected)){
      if(UtilValidate.isNotEmpty(context.productFeatureAndAppl)){
          context.productFeatureAndAppl.each { productFeatureAppls ->
           productFeatureId = productFeatureAppls.productFeatureId;
           productFeatureDescription = "";
           if(UtilValidate.isNotEmpty(productFeatureAppls.description)){
               productFeatureDescription = productFeatureAppls.description;
           }
           context.productFeatureDescription = productFeatureDescription;
           productVariantFeatureList.each { productVariantFeatureInfo ->
             if(productVariantFeatureInfo.productFeatureDesc == featureValueSelected && UtilValidate.isEmpty(productFeatureSelectVariantId)) {
                productFeatureSelectVariantId = productVariantFeatureInfo.productVariantId;
                productFeatureSelectVariantProduct = productVariantFeatureInfo.productVariant;
               }
            }
          }
        }
    }
    context.featureValueSelected = featureValueSelected;
    if(UtilValidate.isNotEmpty(productFeatureSelectVariantId)){
        productVariantSelectContentWrapper = ProductContentWrapper.makeProductContentWrapper(productFeatureSelectVariantProduct, request);
        productVariantSelectSmallURL = productVariantSelectContentWrapper.get("SMALL_IMAGE_URL");
        productVariantSelectSmallAltURL = productVariantSelectContentWrapper.get("SMALL_IMAGE_ALT_URL");
        productImageUrl = "";
        productImageAltUrl = "";
    if(UtilValidate.isNotEmpty(productVariantSelectSmallURL)){
        productImageUrl = productVariantSelectSmallURL;
    }
        context.productImageUrl =productImageUrl;
    if(UtilValidate.isNotEmpty(productVariantSelectSmallAltURL)){
        productImageAltUrl = productVariantSelectSmallAltURL;
     }
        context.productImageAltUrl = productImageAltUrl;
    }

    context.productFeatureSelectVariantProduct = productFeatureSelectVariantProduct;
    context.productFeatureSelectVariantId = productFeatureSelectVariantId;
}