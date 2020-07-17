package common;

import javolution.util.FastMap;
import javolution.util.FastList;

import org.ofbiz.product.store.*;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import com.osafe.services.CatalogUrlServlet;
import com.osafe.util.Util;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.party.content.PartyContentWrapper;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;

plpItem = request.getAttribute("plpItem");
plpItemId = request.getAttribute("plpItemId");
if(UtilValidate.isNotEmpty(plpItem) || UtilValidate.isNotEmpty(plpItemId)) {
    productName = "";
    categoryId = "";
    productInternalName = "";
    productFriendlyUrl = "";
    pdpUrl = "";
    productImageUrl = "";
    productImageAlt = "";
    productImageAltUrl= "";
    price = "";
    productId="";
    averageCustomerRating = "";

    // gets productId
    if(UtilValidate.isNotEmpty(plpItemId)) {
        productId = plpItemId;
    } else if (UtilValidate.isNotEmpty(plpItem)) {
        productId=plpItem.productId;
    } else {
        return;
    }

    product = delegator.findOne("Product",UtilMisc.toMap("productId",productId), true);
    priceMap = (dispatcher.runSync("calculateProductPrice", UtilMisc.toMap("product", product, "userLogin", userLogin)));
    context.priceMap = priceMap;
    
   // Setting variables required in the Manufacturer Info section 
    if (UtilValidate.isNotEmpty(product)) 
    {
        productId = product.productId;
        partyManufacturer=product.getRelatedOne("ManufacturerParty");
        if (UtilValidate.isNotEmpty(partyManufacturer))
        {
          context.manufacturerPartyId = partyManufacturer.partyId;
          PartyContentWrapper partyContentWrapper = new PartyContentWrapper(partyManufacturer, request);
          context.partyContentWrapper = partyContentWrapper;
          context.manufacturerDescription = partyContentWrapper.get("DESCRIPTION");
          context.manufacturerProfileName = partyContentWrapper.get("PROFILE_NAME");
          context.manufacturerProfileImageUrl = partyContentWrapper.get("PROFILE_IMAGE_URL");
        }
    }
    
    //retrieves Product related data when Product Id was received.
    if(UtilValidate.isNotEmpty(plpItemId)) {
        productContentWrapper = ProductContentWrapper.makeProductContentWrapper(product, request)
        if (UtilValidate.isNotEmpty(productContentWrapper)){
        productName =  productContentWrapper.get("PRODUCT_NAME");  
        productImageUrl = productContentWrapper.get("SMALL_IMAGE_URL");
        productImageAltUrl = productContentWrapper.get("SMALL_IMAGE_ALT_URL");
        }
        price = priceMap.defaultPrice ;
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
    //PRODUCT RATINGS
    decimals=Integer.parseInt("1");
    rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
    context.put("decimals",decimals);
    context.put("rounding",rounding);
    // get the average rating
     averageRating= ProductWorker.getAverageProductRating(delegator,productId);
     if (UtilValidate.isNotEmpty(averageRating) && averageRating > 0)
     {
       averageCustomerRating= averageRating.setScale(1,rounding);
     }
    productStore = ProductStoreWorker.getProductStore(request);
    productStoreId = productStore.get("productStoreId");
    reviewByAnd = UtilMisc.toMap("statusId", "PRR_APPROVED", "productStoreId", productStoreId,"productId", productId);
    reviews = delegator.findByAnd("ProductReview", reviewByAnd);
    context.put("reviewSize",reviews.size());
    
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
   
    productFriendlyUrl = CatalogUrlServlet.makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId='+productId+'&productCategoryId='+categoryId);
    context.pdpUrl = 'eCommerceProductDetail?productId='+productId+'&productCategoryId='+categoryId;
    productSelectableFeatureAndAppl = FastList.newInstance();
    productVariantFeatureList = FastList.newInstance();
    productAssoc= FastList.newInstance();
    
    if(UtilValidate.isNotEmpty((context.product).isVirtual) && ((context.product).isVirtual).toUpperCase()== "Y")
    {
        productAssoc = delegator.findByAnd("ProductAssoc",UtilMisc.toMap("productId" ,context.productId,'productAssocTypeId','PRODUCT_VARIANT'));
        productAssoc.each { pAssoc ->
          productFeatureAndAppl = EntityUtil.filterByDate(delegator.findByAndCache("ProductFeatureAndAppl",UtilMisc.toMap("productId" ,pAssoc.productIdTo,'productFeatureApplTypeId','STANDARD_FEATURE'),UtilMisc.toList("sequenceNum")), true);
          productIdTo = pAssoc.productIdTo;
          productTo = pAssoc.getRelatedOne("AssocProduct");
         
          productFeatureAndAppl.each { pFeatureAndAppl ->
             productFeatureDesc = "";
             if(UtilValidate.isNotEmpty(pFeatureAndAppl.description))
             {
                 productFeatureDesc = pFeatureAndAppl.description;
             }
             productVariantFeatureMap = UtilMisc.toMap("productVariantId", productIdTo,"productVariant", productTo, "productFeatureId", pFeatureAndAppl.productFeatureId,"productFeatureDesc", productFeatureDesc,"productFeatureTypeId", pFeatureAndAppl.productFeatureTypeId);
             productVariantFeatureList.add(productVariantFeatureMap);
          }
        }
       context.productVariantFeatureList = productVariantFeatureList;
       
    }
    
    plpFacetGroupVariantSwatch = request.getAttribute("PLP_FACET_GROUP_VARIANT_SWATCH");
    if (UtilValidate.isNotEmpty(plpFacetGroupVariantSwatch))
    {
           productSelectableFeatureAndAppl = EntityUtil.filterByDate(delegator.findByAndCache("ProductFeatureAndAppl",UtilMisc.toMap("productId" ,context.productId,'productFeatureTypeId',plpFacetGroupVariantSwatch,'productFeatureApplTypeId','SELECTABLE_FEATURE'),UtilMisc.toList("sequenceNum")),true);
           context.productSelectableFeatureAndAppl = productSelectableFeatureAndAppl;
    }
    
    featureValueSelected = request.getAttribute("featureValueSelected");
    productFeatureSelectVariantId="";
    productFeatureSelectVariantProduct = "";
    facetGroupVariantMatch = request.getAttribute("FACET_GROUP_VARIANT_MATCH");
    plpFacetGroupVariantSticky = request.getAttribute("PLP_FACET_GROUP_VARIANT_STICKY");
    
    if(UtilValidate.isNotEmpty(featureValueSelected) && UtilValidate.isNotEmpty(facetGroupVariantMatch))
    {
      if(UtilValidate.isNotEmpty(productAssoc))
      {
        for(GenericValue pAssoc : productAssoc) 
        {
            if (UtilValidate.isNotEmpty(productFeatureSelectVariantId))
            {
               break;
            }
            productFeatureVariantAndAppl = EntityUtil.filterByDate(delegator.findByAndCache("ProductFeatureAndAppl", UtilMisc.toMap('productId',pAssoc.productIdTo,'productFeatureTypeId',facetGroupVariantMatch,'description',featureValueSelected),UtilMisc.toList("sequenceNum")),true);
            productFeatureVariant = EntityUtil.getFirst(productFeatureVariantAndAppl);
            if (UtilValidate.isNotEmpty(productFeatureVariant))
            {
              for(Map productVariantFeatureMap : productVariantFeatureList)
              {
                if (productVariantFeatureMap.productVariantId == pAssoc.productIdTo && productVariantFeatureMap.productFeatureTypeId == plpFacetGroupVariantSticky)
                {
			        productFeatureSelectVariantId = productVariantFeatureMap.productVariantId;
			        productFeatureSelectVariantProduct = productVariantFeatureMap.productVariant;
			        featureValueSelected=productVariantFeatureMap.productFeatureDesc;
                    break;
                }
              }
            }
        }
       }
     }
    else
    {
      //Get First Swatch Selectable Feature and set.
      if(UtilValidate.isEmpty(productFeatureSelectVariantId) && UtilValidate.isNotEmpty(productSelectableFeatureAndAppl))
      {
	      if(UtilValidate.isNotEmpty(productVariantFeatureList) && UtilValidate.isNotEmpty(plpFacetGroupVariantSticky))
	      {
	          firstSelectableFeature = EntityUtil.getFirst(productSelectableFeatureAndAppl);
	          firstSelectableFeatureId = firstSelectableFeature.productFeatureId;
              for(Map productVariantFeatureMap : productVariantFeatureList)
              {
                if (productVariantFeatureMap.productFeatureId == firstSelectableFeatureId && productVariantFeatureMap.productFeatureTypeId == plpFacetGroupVariantSticky)
                {
			        productFeatureSelectVariantId = productVariantFeatureMap.productVariantId;
			        productFeatureSelectVariantProduct = productVariantFeatureMap.productVariant;
			        featureValueSelected=productVariantFeatureMap.productFeatureDesc;
                    break;
                }
              }
	      }
      }
    }
    context.featureValueSelected = featureValueSelected;
    if(UtilValidate.isNotEmpty(productFeatureSelectVariantId))
    {
        productVariantSelectContentWrapper = ProductContentWrapper.makeProductContentWrapper(productFeatureSelectVariantProduct, request);
        productVariantSelectSmallURL = productVariantSelectContentWrapper.get("SMALL_IMAGE_URL");
        productVariantSelectSmallAltURL = productVariantSelectContentWrapper.get("SMALL_IMAGE_ALT_URL");
        productImageUrl = "";
        productImageAltUrl = "";
	        
        if(UtilValidate.isNotEmpty(featureValueSelected))
         {
   	        if (UtilValidate.isNotEmpty(plpFacetGroupVariantSticky))
   	        {
        	    context.productFeatureType = plpFacetGroupVariantSticky;
                productFriendlyUrl = CatalogUrlServlet.makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId='+productId+'&productCategoryId='+categoryId+'&productFeatureType='+plpFacetGroupVariantSticky+':'+featureValueSelected);
   	        }
   	        else
   	        {
                productFriendlyUrl = CatalogUrlServlet.makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId='+productId+'&productCategoryId='+categoryId);
   	        }
        }
        if(UtilValidate.isNotEmpty(productVariantSelectSmallURL))
        {
            productImageUrl = productVariantSelectSmallURL;
        }
        if(UtilValidate.isNotEmpty(productVariantSelectSmallAltURL))
        {
            productImageAltUrl = productVariantSelectSmallAltURL;
        }
        context.productImageAltUrl = productImageAltUrl;
        context.productImageUrl =productImageUrl;
    }

    context.productFeatureSelectVariantProduct = productFeatureSelectVariantProduct;
    context.productFeatureSelectVariantId = productFeatureSelectVariantId;
    context.productFriendlyUrl = productFriendlyUrl;
}