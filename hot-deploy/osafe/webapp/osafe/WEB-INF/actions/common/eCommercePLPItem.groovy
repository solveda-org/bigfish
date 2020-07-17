package common;

import javolution.util.FastMap;
import javolution.util.FastList;

import org.ofbiz.product.store.*;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil;
import com.osafe.services.CatalogUrlServlet;
import com.osafe.util.Util;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.party.content.PartyContentWrapper;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

plpItem = request.getAttribute("plpItem");
plpItemId = request.getAttribute("plpItemId");
autoUserLogin = request.getSession().getAttribute("autoUserLogin");
cart = session.getAttribute("shoppingCart");

if(UtilValidate.isNotEmpty(plpItem) || UtilValidate.isNotEmpty(plpItemId)) 
{
    productName = "";
    if (UtilValidate.isNotEmpty(parameters.productCategoryId)) 
    {
      categoryId = parameters.productCategoryId;
    }
    else
    {
      categoryId = "";
    }
    productInternalName = "";
    productFriendlyUrl = "";
    pdpUrl = "";
    productImageUrl = "";
    productImageAlt = "";
    productImageAltUrl= "";
    price = "";
    listPrice = "";
    productId="";

    // gets productId
    if(UtilValidate.isNotEmpty(plpItemId)) 
    {
        productId = plpItemId;
    } else if (UtilValidate.isNotEmpty(plpItem)) 
    {
        productId=plpItem.productId;
    } else 
    {
        return;
    }

    productStore = ProductStoreWorker.getProductStore(request);
	productStoreId = productStore.get("productStoreId");

    product = delegator.findOne("Product",UtilMisc.toMap("productId",productId), true);
    
    
   // Setting variables required in the Manufacturer Info section 
    if (UtilValidate.isNotEmpty(product)) 
    {
	    priceContext = [product : product, currencyUomId : cart.getCurrency(),"userLogin": userLogin];
	    priceContext.productStoreId = productStoreId;
	    priceContext.productStoreGroupId = "_NA_";
	    
        productId = product.productId;
        partyManufacturer=product.getRelatedOneCache("ManufacturerParty");
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
    if(UtilValidate.isNotEmpty(plpItemId)) 
    {
        productContentWrapper = ProductContentWrapper.makeProductContentWrapper(product, request)
        if (UtilValidate.isNotEmpty(productContentWrapper))
        {
	        productName =  productContentWrapper.get("PRODUCT_NAME");  
	        productImageUrl = productContentWrapper.get("SMALL_IMAGE_URL");
	        productImageAltUrl = productContentWrapper.get("SMALL_IMAGE_ALT_URL");
        }
        if (UtilValidate.isNotEmpty(priceContext))
        {
            priceMap = dispatcher.runSync("calculateProductPrice",priceContext);
	        context.priceMap = priceMap;
            price = priceMap.defaultPrice ;
            listPrice = priceMap.listPrice;
        }
        
        productInternalName = product.internalName;
    }
    
    //retrieves product related Data when Solr document was received .
    else if (UtilValidate.isNotEmpty(plpItem)) 
    {
        if (UtilValidate.isNotEmpty(plpItem.name)) 
        {
            productName = StringUtil.wrapString(plpItem.name);
        }
        if (UtilValidate.isNotEmpty(plpItem.productImageSmallUrl)) 
        {
            productImageUrl = plpItem.productImageSmallUrl;
        }
        if (UtilValidate.isNotEmpty(plpItem.price)) 
        {
            price = plpItem.price;
        }
        if (UtilValidate.isNotEmpty(plpItem.listPrice)) 
        {
            listPrice = plpItem.listPrice;
        }
        if (UtilValidate.isNotEmpty(plpItem.internalName)) 
        {
            productInternalName = plpItem.internalName;
        }    
        productId=plpItem.productId;
        if (UtilValidate.isNotEmpty(plpItem.productImageSmallAlt)) 
        {
            productImageAlt = plpItem.productImageSmallAlt;
        }
        if (UtilValidate.isNotEmpty(plpItem.productImageSmallAltUrl)) 
        {
            productImageAltUrl = plpItem.productImageSmallAltUrl;
        }
    }
    
    //CHECK WE HAVE A DEFAULT PRODUCT CATEGORY THE PRODUCT IS MEMBER OF
    if (UtilValidate.isEmpty(categoryId))
    {
        productCategoryMemberList = product.getRelatedCache("ProductCategoryMember");
        productCategoryMemberList = EntityUtil.filterByDate(productCategoryMemberList,true);
	    productCategoryMemberList = EntityUtil.orderBy(productCategoryMemberList,UtilMisc.toList("sequenceNum"));
        if(UtilValidate.isNotEmpty(productCategoryMemberList))
        {
            productCategoryMember = EntityUtil.getFirst(productCategoryMemberList);
            categoryId = productCategoryMember.productCategoryId; 
        }    
    }
    
    //GET PRODUCT RATINGS AND REVIEWS
    decimals=Integer.parseInt("1");
    rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
    context.put("decimals",decimals);
    context.put("rounding",rounding);
    // get the average rating
    productCalculatedInfo = product.getRelatedOneCache("ProductCalculatedInfo");
    if (UtilValidate.isNotEmpty(productCalculatedInfo))
    {
        averageRating= productCalculatedInfo.getBigDecimal("averageCustomerRating");
        if (UtilValidate.isNotEmpty(averageRating) && averageRating > 0)
        {
 	       averageCustomerRating= averageRating.setScale(1,rounding);
 	       context.put("averageStarPLPRating", averageCustomerRating);
        }
    }
	reviewMethod = Util.getProductStoreParm(request, "REVIEW_METHOD");
	if(UtilValidate.isNotEmpty(reviewMethod))
	{
	    if(reviewMethod.equals("BIGFISH"))
	    {
	        reviews = product.getRelatedCache("ProductReview");
	        if (UtilValidate.isNotEmpty(reviews))
	        {
	            reviews = EntityUtil.filterByAnd(reviews, UtilMisc.toMap("statusId", "PRR_APPROVED", "productStoreId", productStoreId));
	     	    context.put("reviewPLPSize",reviews.size());
	        }
	    }
	}
    
    // get the no of reviews
    
    context.productName = productName;
    context.productId = productId;
    context.categoryId = categoryId;
    context.productInternalName = productInternalName;
    context.productImageUrl = productImageUrl;
    context.price=price;
    context.listPrice = listPrice;
    context.product = product;

    context.productImageAlt = productImageAlt;
    context.productImageAltUrl = productImageAltUrl;
	context.plpLabel = "";
    if (UtilValidate.isNotEmpty(ProductContentWrapper.getProductContentAsText(context.product, 'PLP_LABEL', request)))
    {
        context.plpLabel = ProductContentWrapper.getProductContentAsText(context.product, 'PLP_LABEL', request);
    }
   
    productFriendlyUrl = CatalogUrlServlet.makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId='+productId+'&productCategoryId='+categoryId);
    context.pdpUrl = 'eCommerceProductDetail?productId='+productId+'&productCategoryId='+categoryId;
    productSelectableFeatureAndAppl = FastList.newInstance();
    productVariantFeatureList = FastList.newInstance();
    productAssoc= FastList.newInstance();
    featureValueSelected = request.getAttribute("featureValueSelected");
    productFeatureSelectVariantId="";
    productFeatureSelectVariantProduct = "";
    facetGroupVariantMatch = request.getAttribute("FACET_GROUP_VARIANT_MATCH");
    plpFacetGroupVariantSticky = request.getAttribute("PLP_FACET_GROUP_VARIANT_STICKY");
    
    if(UtilValidate.isNotEmpty((context.product).isVirtual) && ((context.product).isVirtual).toUpperCase()== "Y")
    {
    	productVariantPriceMap = FastMap.newInstance();
    	descpFeatureGroupDescMap = FastMap.newInstance();
        productAssoc = product.getRelatedCache("MainProductAssoc");
        productAssoc = EntityUtil.filterByDate(productAssoc,true);
        productAssoc = EntityUtil.filterByAnd(productAssoc, UtilMisc.toMap("productAssocTypeId","PRODUCT_VARIANT"));
	    productAssoc = EntityUtil.orderBy(productAssoc,UtilMisc.toList("sequenceNum"));

        if (UtilValidate.isNotEmpty(productAssoc)) 
        {
        	PLP_FACET_GROUP_VARIANT_MATCH = Util.getProductStoreParm(request,"PLP_FACET_GROUP_VARIANT_MATCH");
		    plpFacetGroupVariantSwatch = request.getAttribute("PLP_FACET_GROUP_VARIANT_SWATCH");
		    if (UtilValidate.isNotEmpty(plpFacetGroupVariantSwatch))
		    {
		           productSelectableFeatureAndAppl = product.getRelatedCache("ProductFeatureAndAppl");
		           productSelectableFeatureAndAppl = EntityUtil.filterByDate(productSelectableFeatureAndAppl,true);
                   productSelectableFeatureAndAppl = EntityUtil.filterByAnd(productSelectableFeatureAndAppl, UtilMisc.toMap("productFeatureTypeId",plpFacetGroupVariantSwatch,"productFeatureApplTypeId","SELECTABLE_FEATURE"));
		           productSelectableFeatureAndAppl = EntityUtil.orderBy(productSelectableFeatureAndAppl,UtilMisc.toList("sequenceNum"));
		           context.productSelectableFeatureAndAppl = productSelectableFeatureAndAppl;
		    }
        	
            for(GenericValue pAssoc : productAssoc)
            {
		      productIdTo = pAssoc.productIdTo;
              isSellableVariant = ProductWorker.isSellable(delegator, productIdTo);
	          if (isSellableVariant)
		      {
                assocVariantProduct = pAssoc.getRelatedOneCache("AssocProduct");
                variantProductFeatureAndAppls = assocVariantProduct.getRelatedCache("ProductFeatureAndAppl");
                variantProductFeatureAndAppls = EntityUtil.filterByDate(variantProductFeatureAndAppls,true);
  	            variantProductFeatureAndAppls = EntityUtil.orderBy(variantProductFeatureAndAppls,UtilMisc.toList("sequenceNum"));
  	            
                variantProductPrices = assocVariantProduct.getRelatedCache("ProductPrice");
                variantProductPrices = EntityUtil.filterByDate(variantProductPrices,true);

	            for(GenericValue variantProductPrice : variantProductPrices)
	            {
	              if (variantProductPrice.productPriceTypeId == "DEFAULT_PRICE")
	              {
	                	basePrice = variantProductPrice.price;
	                    varPriceMap = productVariantPriceMap.get(variantProductPrice.productId);
	                    if (UtilValidate.isNotEmpty(varPriceMap)) 
	                    {
	                        varPriceMap.put("basePrice", basePrice)
	                    } 
	                    else 
	                    {
	                    	productVariantPriceMap.put(variantProductPrice.productId, UtilMisc.toMap("basePrice", basePrice))
	                    }
	                    continue;
	              }
	              if (variantProductPrice.productPriceTypeId == "LIST_PRICE")
	              {
		            	listPrice = variantProductPrice.price;
		                varPriceMap = productVariantPriceMap.get(variantProductPrice.productId);
		                if (UtilValidate.isNotEmpty(varPriceMap)) 
		                {
		                    varPriceMap.put("listPrice", listPrice)
		                } 
		                else 
		                {
		                	productVariantPriceMap.put(variantProductPrice.productId, UtilMisc.toMap("listPrice", listPrice))
		                }
		                continue;
	              }
	            }

                for (GenericValue variantProductFeatureAndAppl: variantProductFeatureAndAppls)
                {
		            if (UtilValidate.isNotEmpty(PLP_FACET_GROUP_VARIANT_MATCH)) 
		            {
	                   if (variantProductFeatureAndAppl.productFeatureTypeId == PLP_FACET_GROUP_VARIANT_MATCH && variantProductFeatureAndAppl.productFeatureApplTypeId == "DISTINGUISHING_FEAT")
	                    {
	            		          descpFeatureGroupDescMap.put(variantProductFeatureAndAppl.productId, variantProductFeatureAndAppl.description);
	            		          continue;
	                    }
	                }
	                
                    if (variantProductFeatureAndAppl.productFeatureApplTypeId == "STANDARD_FEATURE")
                    {
			             productFeatureDesc = "";
			             if(UtilValidate.isNotEmpty(variantProductFeatureAndAppl.description))
			             {
			                 productFeatureDesc = variantProductFeatureAndAppl.description;
			             }
			             productVariantFeatureMap = UtilMisc.toMap("productVariantId", productIdTo,"productVariant", assocVariantProduct, "productFeatureId", variantProductFeatureAndAppl.productFeatureId,"productFeatureDesc", productFeatureDesc,"productFeatureTypeId", variantProductFeatureAndAppl.productFeatureTypeId);
			
			             variantPriceMap = productVariantPriceMap.get(productIdTo);
			             
			             if (UtilValidate.isNotEmpty(variantPriceMap))
			             {
							if (UtilValidate.isNotEmpty(variantPriceMap.get("basePrice")))
							{
								productVariantFeatureMap.put("basePrice", variantPriceMap.get("basePrice"));
							}
			             }
			             else 
			             {
			                 productVariantFeatureMap.put("basePrice", price);
			             }
			             
			             if (UtilValidate.isNotEmpty(variantPriceMap))
			             {
							if (UtilValidate.isNotEmpty(variantPriceMap.get("listPrice")))
							{
			                    productVariantFeatureMap.put("listPrice", variantPriceMap.get("listPrice"));
							}
			             }
			             else 
			             {
			                 productVariantFeatureMap.put("listPrice", listPrice);
			             }
			
			             descpFeatureGroupDesc = descpFeatureGroupDescMap.get(productIdTo);
			             if (UtilValidate.isNotEmpty(descpFeatureGroupDesc))
			             {
			                 productVariantFeatureMap.put("descriptiveFeatureGroupDesc", descpFeatureGroupDesc);
			             }
			             else 
			             {
			                 productVariantFeatureMap.put("descriptiveFeatureGroupDesc", "");
			             }
			
			             productVariantFeatureList.add(productVariantFeatureMap);
                    }
                    
				    if(UtilValidate.isNotEmpty(featureValueSelected) && UtilValidate.isNotEmpty(facetGroupVariantMatch))
				    {
			            if (UtilValidate.isEmpty(productFeatureSelectVariantId))
			            {
	                       if (variantProductFeatureAndAppl.productFeatureTypeId == facetGroupVariantMatch && variantProductFeatureAndAppl.description == featureValueSelected)
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
		      }
            }
            context.productVariantFeatureList  = productVariantFeatureList;            
        }
    
      if(UtilValidate.isEmpty(productFeatureSelectVariantId) && UtilValidate.isNotEmpty(productSelectableFeatureAndAppl))
      {
	        if(UtilValidate.isNotEmpty(productVariantFeatureList) && UtilValidate.isNotEmpty(plpFacetGroupVariantSticky))
	        {
	          for(GenericValue productFeatureAppls : productSelectableFeatureAndAppl)
	          {
	              firstSelectableFeatureId = productFeatureAppls.productFeatureId;
	          
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
	              if(UtilValidate.isNotEmpty(productFeatureSelectVariantId))
	              {
	                  break;
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
	        
	        if(UtilValidate.isNotEmpty(productVariantFeatureList))
	        {
	            for(Map productVariantFeature : productVariantFeatureList)
	            {
	               if(productVariantFeature.productVariantId == productFeatureSelectVariantId)
	               {
	                   context.price = productVariantFeature.basePrice;
	                   context.listPrice = productVariantFeature.listPrice; 
	               }
	            }
	        }
	        context.productImageAltUrl = productImageAltUrl;
	        context.productImageUrl = productImageUrl;
	  }
    }
    

    context.productFeatureSelectVariantProduct = productFeatureSelectVariantProduct;
    context.productFeatureSelectVariantId = productFeatureSelectVariantId;
    context.productFriendlyUrl = productFriendlyUrl;
}