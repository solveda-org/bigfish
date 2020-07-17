package common;

import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil;
import com.osafe.services.CatalogUrlServlet;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.party.content.PartyContentWrapper;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;
import com.osafe.util.Util;
import org.ofbiz.entity.condition.EntityCondition;
import org.apache.commons.lang.StringEscapeUtils;

plpItem = request.getAttribute("plpItem");
autoUserLogin = request.getSession().getAttribute("autoUserLogin");
cart = session.getAttribute("shoppingCart");


if(UtilValidate.isNotEmpty(plpItem)) 
{
  productId=plpItem.productId;
  product = delegator.findOne("Product",UtilMisc.toMap("productId",productId), true);
  if (UtilValidate.isNotEmpty(product)) 
  {
	productName = product.productName;
    categoryId = "";
    productInternalName = "";
    productFriendlyUrl = "";
    pdpUrl = "";
    productImageUrl = "";
    productImageAltUrl= "";
    price = "";
    listPrice = "";
    plpLabel = "";
    plpProductFeatureType = "";
    productLongDesc = "";
    
    ProductContentWrapper productContentWrapper = new ProductContentWrapper(product, request);
    productStore = ProductStoreWorker.getProductStore(request);
	productStoreId = productStore.get("productStoreId");

    //GET PRODUCT CONTENT LIST AND SET INTO CONTEXT
    Map productContentIdMap = FastMap.newInstance();
	productContentList = product.getRelatedCache("ProductContent");
	productContentList = EntityUtil.filterByDate(productContentList,true);
	if (UtilValidate.isNotEmpty(productContentList))
	{
        for (GenericValue productContent: productContentList) 
        {
		   productContentTypeId = productContent.productContentTypeId;
		    //Prefix the Content by "PLP" to distinguish content when groovy file is used in PDP
		   context.put("PLP_" + productContent.productContentTypeId,productContent.contentId);
           productContentIdMap.put(productContent.productContentTypeId,productContent.contentId);
        }
	}
    
	
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
    if (UtilValidate.isNotEmpty(plpItem.productImageSmallAlt)) 
    {
        productImageAlt = plpItem.productImageSmallAlt;
    }
    if (UtilValidate.isNotEmpty(plpItem.productImageSmallAltUrl)) 
    {
    	productImageAltUrl = plpItem.productImageSmallAltUrl;
    }
    if (UtilValidate.isNotEmpty(plpItem)) 
    {
      categoryId = parameters.productCategoryId;
	  if (UtilValidate.isEmpty(categoryId))
	  {
		  categoryId = request.getAttribute("productCategoryId");
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
	reviewMethod = Util.getProductStoreParm(request, "REVIEW_METHOD");
	reviewSize=0;
	averageCustomerRating="";
	if(UtilValidate.isNotEmpty(reviewMethod))
	{
	    if(reviewMethod.equalsIgnoreCase("BIGFISH"))
	    {
	        // get the average rating
		    productCalculatedInfo = product.getRelatedOneCache("ProductCalculatedInfo");
		    if (UtilValidate.isNotEmpty(productCalculatedInfo))
		    {
		        averageRating= productCalculatedInfo.getBigDecimal("averageCustomerRating");
		        if (UtilValidate.isNotEmpty(averageRating) && averageRating > 0)
		        {
		 	       averageCustomerRating= averageRating.setScale(1,rounding);
		        }
		    }
	        reviews = product.getRelatedCache("ProductReview");
	        if (UtilValidate.isNotEmpty(reviews))
	        {
	            reviews = EntityUtil.filterByAnd(reviews, UtilMisc.toMap("statusId", "PRR_APPROVED", "productStoreId", productStoreId));
	            reviewSize=reviews.size();
	        }
	        
	    }
	}
    
    plpLabelContent = productContentIdMap.get("PLP_LABEL");
	if (UtilValidate.isNotEmpty(plpLabelContent))
	{
	    	plpLabel = productContentWrapper.get("PLP_LABEL");
	}
   
    //SET PRODUCT LONG DESCRIPTION
    productContentId = productContentIdMap.get("LONG_DESCRIPTION");
    if (UtilValidate.isNotEmpty(productContentId))
    {
    	productLongDesc = productContentWrapper.get("LONG_DESCRIPTION");
    	productLongDesc = StringEscapeUtils.unescapeHtml(productLongDesc.toString());
 	    productLongDesc = productLongDesc;
    }
	
	productFriendlyUrl = CatalogUrlServlet.makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId='+productId+'&productCategoryId='+categoryId);
    pdpUrl = 'eCommerceProductDetail?productId='+productId+'&productCategoryId='+categoryId;
    
    // GET PRODUCT FEATURE AND APPLS: DISTINGUISHING FEATURES
    // Using findByAndCache Call since the ProductService(Service getProductVariantTree call) will make the same findByAndCache Call.
    productDistinguishingFeatures = delegator.findByAndCache("ProductFeatureAndAppl", UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "DISTINGUISHING_FEAT"), UtilMisc.toList("sequenceNum"));
    productDistinguishingFeatures = EntityUtil.filterByDate(productDistinguishingFeatures, true);

    // GET PRODUCT FEATURE AND APPLS : DISTINGUISHING FEATURES BY FEATURE TYPE
    productFeatureTypes = FastList.newInstance();
    productFeaturesByType = new LinkedHashMap();
    facetValueList = context.facetValueList;
    int facetUrlPosition = 0;

    for (GenericValue feature: productDistinguishingFeatures) 
    {
        featureType = feature.getString("productFeatureTypeId");
        if (!productFeatureTypes.contains(featureType)) 
        {
            productFeatureTypes.add(featureType);
        }
        features = productFeaturesByType.get(featureType);
        if (UtilValidate.isEmpty(features)) 
        {
            features = FastList.newInstance();
            productFeaturesByType.put(featureType, features);
        }
        features.add(feature);
    }

    // GET PRODUCT FEATURE TYPE
    Map plpProductFeatureTypesMap = FastMap.newInstance();
    productFeatureTypesList = delegator.findList("ProductFeatureType", null, null, null, null, true);
    if(UtilValidate.isNotEmpty(productFeatureTypesList))
    {
        for (GenericValue productFeatureType : productFeatureTypesList)
        {
        	plpProductFeatureTypesMap.put(productFeatureType.productFeatureTypeId,productFeatureType.description);
        }
        
    }

    List productSelectableFeatureAndAppl = FastList.newInstance();
    List productVariantFeatureList = FastList.newInstance();
    List productAssoc= FastList.newInstance();
    
    Map productVariantContentWrapperMap = FastMap.newInstance();
    Map productVariantProductContentIdMap = FastMap.newInstance();
    Map productFeatureFirstVariantIdMap = FastMap.newInstance();
    Map productFeatureDataResourceMap = FastMap.newInstance();

    featureValueSelected = request.getAttribute("featureValueSelected");
    prevFeatureValueSelected = "";
    productFeatureSelectVariantId="";
    productFeatureSelectVariantProduct = "";
    
    if(UtilValidate.isNotEmpty((product).isVirtual) && ((product).isVirtual).toUpperCase()== "Y")
    {
    	Map productVariantPriceMap = FastMap.newInstance();
    	Map descpFeatureGroupDescMap = FastMap.newInstance();
        variantFirstFeatureIdExist = [];
        facetGroupVariantMatch = request.getAttribute("FACET_GROUP_VARIANT_MATCH");
        plpFacetGroupVariantSticky = request.getAttribute("PLP_FACET_GROUP_VARIANT_STICKY");
        plpFacetGroupVariantSwatch = request.getAttribute("PLP_FACET_GROUP_VARIANT_SWATCH");
    	plpFacetGroupVariantMatch = Util.getProductStoreParm(request,"PLP_FACET_GROUP_VARIANT_MATCH");

    	
    	productAssoc = product.getRelatedCache("MainProductAssoc");
        productAssoc = EntityUtil.filterByDate(productAssoc,true);
        productAssoc = EntityUtil.filterByAnd(productAssoc, UtilMisc.toMap("productAssocTypeId","PRODUCT_VARIANT"));
	    productAssoc = EntityUtil.orderBy(productAssoc,UtilMisc.toList("sequenceNum"));

        if (UtilValidate.isNotEmpty(productAssoc)) 
        {
        	
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

                //BULLD PRODUCT CONTENT WRAPPER FOR EACH VARIANT TO PUT INTO CONTEXT
                varProductContentWrapper = new ProductContentWrapper(assocVariantProduct, request);
                productVariantContentWrapperMap.put(assocVariantProduct.productId, varProductContentWrapper);
                
	            for(GenericValue variantProductPrice : variantProductPrices)
	            {
	              if (variantProductPrice.productPriceTypeId == "DEFAULT_PRICE")
	              {
	                	varBasePrice = variantProductPrice.price;
	                    varPriceMap = productVariantPriceMap.get(variantProductPrice.productId);
	                    if (UtilValidate.isNotEmpty(varPriceMap)) 
	                    {
	                        varPriceMap.put("basePrice", varBasePrice)
	                    } 
	                    else 
	                    {
	                    	productVariantPriceMap.put(variantProductPrice.productId, UtilMisc.toMap("basePrice", varBasePrice))
	                    }
	                    continue;
	              }
	              if (variantProductPrice.productPriceTypeId == "LIST_PRICE")
	              {
		            	varListPrice = variantProductPrice.price;
		                varPriceMap = productVariantPriceMap.get(variantProductPrice.productId);
		                if (UtilValidate.isNotEmpty(varPriceMap)) 
		                {
		                    varPriceMap.put("listPrice", varListPrice)
		                } 
		                else 
		                {
		                	productVariantPriceMap.put(variantProductPrice.productId, UtilMisc.toMap("listPrice", varListPrice))
		                }
		                continue;
	              }
	            }

                for (GenericValue variantProductFeatureAndAppl: variantProductFeatureAndAppls)
                {
		            if (UtilValidate.isNotEmpty(plpFacetGroupVariantMatch)) 
		            {
	                   if (variantProductFeatureAndAppl.productFeatureTypeId == plpFacetGroupVariantMatch && variantProductFeatureAndAppl.productFeatureApplTypeId == "DISTINGUISHING_FEAT")
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
			             
    			        //CREATE A MAP FOR First PRODUCT FEATURE ID AND VARIANT PRODUCT ID
			             //This is only needed if swatches are displayed and is used in plpSwatch.ftl
			            //(featureId, variantIdMap)
			     	    if (UtilValidate.isNotEmpty(plpFacetGroupVariantSwatch))
			     	    {
			        		if(!variantFirstFeatureIdExist.contains(variantProductFeatureAndAppl.productFeatureId))
	                        {
	                    		productVariantContentList = assocVariantProduct.getRelatedCache("ProductContent");
	                    		productVariantContentList = EntityUtil.filterByDate(productVariantContentList,true);
	                    		if (UtilValidate.isNotEmpty(productVariantContentList))
	                    		{
	                                Map variantProductContentMap = FastMap.newInstance();
	                                for (GenericValue productContent: productVariantContentList) 
	                                {
	                        		   productContentTypeId = productContent.productContentTypeId;
	                        		   variantProductContentMap.put(productContent.productContentTypeId,productContent.contentId);
	                                }
	                                productVariantProductContentIdMap.put(assocVariantProduct.productId,variantProductContentMap);
	                                
	                    		}
			        			
	                            productFeatureFirstVariantIdMap.put(variantProductFeatureAndAppl.productFeatureId, productVariantFeatureMap); 
	                            variantFirstFeatureIdExist.add(variantProductFeatureAndAppl.productFeatureId);
	                        }
			     	    }
                    
				             
			             
                    }
                    if(UtilValidate.isNotEmpty(facetValueList) && facetValueList.contains(variantProductFeatureAndAppl.description) && facetUrlPosition <= facetValueList.indexOf(variantProductFeatureAndAppl.description))
                    {
                    	facetUrlPosition = facetValueList.indexOf(variantProductFeatureAndAppl.description);
                    	featureValueSelected = variantProductFeatureAndAppl.description;
                    }
                    
				    if(UtilValidate.isNotEmpty(featureValueSelected) && UtilValidate.isNotEmpty(facetGroupVariantMatch))
				    {
			            if (featureValueSelected != prevFeatureValueSelected)
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
							        prevFeatureValueSelected = featureValueSelected;
				                    break;
				                }
				              }
	                       }
			            }
				    }
	            }
		      }
            }
        }
    
	    if (UtilValidate.isNotEmpty(plpFacetGroupVariantSwatch))
	    {
	      productSelectableFeatureAndAppl = delegator.findByAndCache("ProductFeatureAndAppl", UtilMisc.toMap("productId",productId, "productFeatureTypeId", plpFacetGroupVariantSwatch, "productFeatureApplTypeId", "SELECTABLE_FEATURE"), UtilMisc.toList("sequenceNum"));
	      productSelectableFeatureAndAppl = EntityUtil.filterByDate(productSelectableFeatureAndAppl,true);
	      
          //BUILD CONTEXT MAP FOR PRODUCT_FEATURE_DATA_RESOURCE (productFeatureId, objectInfo)
          productFeatureDataResourceList = delegator.findList("ProductFeatureDataResource", EntityCondition.makeCondition(["featureDataResourceTypeId" : "PLP_SWATCH_IMAGE_URL"]), null, ["productFeatureId"], null, true);
          if (UtilValidate.isNotEmpty(productFeatureDataResourceList))
          {
              for (GenericValue productFeatureDataResource : productFeatureDataResourceList)
              {
              	dataResource = productFeatureDataResource.getRelatedOneCache("DataResource");
                  if(UtilValidate.isNotEmpty(dataResource.objectInfo))
                  {
                  	productFeatureDataResourceMap.put(productFeatureDataResource.productFeatureId,dataResource.objectInfo);
                  }
              }
          	
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
	    }
    
	  if(UtilValidate.isNotEmpty(productFeatureSelectVariantId))
	  {
	        productVariantSelectSmallURL = "";
	        productVariantSelectSmallAltURL = "";
	        productVariantSelectContentWrapper = ProductContentWrapper.makeProductContentWrapper(productFeatureSelectVariantProduct, request);
	        if (UtilValidate.isNotEmpty(productVariantSelectContentWrapper))
	        {
	            productContentList = delegator.findByAndCache("ProductContent", UtilMisc.toMap("productId",productFeatureSelectVariantId, "productContentTypeId", "SMALL_IMAGE_URL"));
	            productContentList = EntityUtil.filterByDate(productContentList,true);
        		if (UtilValidate.isNotEmpty(productContentList))
        		{
        			productContent = EntityUtil.getFirst(productContentList);
    		  	    productContentId = productContent.contentId;
    		        if (UtilValidate.isNotEmpty(productContentId))
    		        {
    			        productVariantSelectSmallURL = productVariantSelectContentWrapper.get("SMALL_IMAGE_URL");

    		            productContentList = delegator.findByAndCache("ProductContent", UtilMisc.toMap("productId",productFeatureSelectVariantId, "productContentTypeId", "SMALL_IMAGE_ALT_URL"));
    		            productContentList = EntityUtil.filterByDate(productContentList,true);
    	        		if (UtilValidate.isNotEmpty(productContentList))
    	        		{
    	        			productContent = EntityUtil.getFirst(productContentList);
            		  	    productContentId = productContent.contentId;
        			        if (UtilValidate.isNotEmpty(productContentId))
        			        {
             			        productVariantSelectSmallAltURL = productVariantSelectContentWrapper.get("SMALL_IMAGE_ALT_URL");
        			        }
    	        			
    	        		}
    		        	
    		        }
        			
        		}
	        	
	        }
	        if(UtilValidate.isNotEmpty(featureValueSelected))
	        {
	   	        if (UtilValidate.isNotEmpty(plpFacetGroupVariantSticky))
	   	        {
	        	    plpProductFeatureType = plpFacetGroupVariantSticky;
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
	                   price = productVariantFeature.basePrice;
	                   listPrice = productVariantFeature.listPrice; 
	               }
	            }
	        }
	  }
    }
    
	//Manufacturer info
	partyManufacturer=product.getRelatedOneCache("ManufacturerParty");
	if (UtilValidate.isNotEmpty(partyManufacturer))
	{
	  context.plpManufacturerPartyId = partyManufacturer.partyId;
	  PartyContentWrapper partyContentWrapper = new PartyContentWrapper(partyManufacturer, request);
	  context.plpManufacturerPartyContentWrapper = partyContentWrapper;
	  context.plpManufacturerDescription = partyContentWrapper.get("DESCRIPTION");
	  context.plpManufacturerProfileName = partyContentWrapper.get("PROFILE_NAME");
	  context.plpManufacturerProfileImageUrl = partyContentWrapper.get("PROFILE_IMAGE_URL");
	}

    context.plpProductName = productName;
    context.plpProductId = productId;
    context.plpCategoryId = categoryId;
    context.plpProductInternalName = productInternalName;
    context.plpProductImageUrl = productImageUrl;
    context.plpPrice=price;
    context.plpListPrice = listPrice;
    context.plpProductContentWrapper = productContentWrapper;
	context.plpLabel = plpLabel;
    context.plpPdpUrl = pdpUrl;
    context.plpProductFriendlyUrl = productFriendlyUrl;
    context.plpProductImageAltUrl = productImageAltUrl;
    context.featureValueSelected = featureValueSelected;
    context.plpLongDescription = productLongDesc;

    //Ratings
    context.decimals = decimals;
    context.rounding = rounding;
    context.plpAverageStarRating = averageCustomerRating;
    context.plpReviewSize = reviewSize;

    context.plpProductFeatureType = plpProductFeatureType;
    context.plpProductFeatureDataResourceMap = productFeatureDataResourceMap;
    context.plpProductSelectableFeatureAndAppl = productSelectableFeatureAndAppl;
    context.plpProductVariantContentWrapperMap = productVariantContentWrapperMap;
    context.plpProductFeatureFirstVariantIdMap = productFeatureFirstVariantIdMap;
    context.plpProductVariantProductContentIdMap = productVariantProductContentIdMap;
    context.plpDisFeatureTypesList = productFeatureTypes;
    context.plpDisFeatureByTypeMap = productFeaturesByType;
    context.plpProductFeatureTypesMap = plpProductFeatureTypesMap;
    
  }    
}