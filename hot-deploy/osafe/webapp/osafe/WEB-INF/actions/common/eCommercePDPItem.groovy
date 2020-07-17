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

productId = request.getAttribute("plpItemId");
cart = session.getAttribute("shoppingCart");


if(UtilValidate.isNotEmpty(productId)) 
{
  product = delegator.findOne("Product",UtilMisc.toMap("productId",productId), true);
  if (UtilValidate.isNotEmpty(product)) 
  {
    productStore = ProductStoreWorker.getProductStore(request);
	productStoreId = productStore.get("productStoreId");
	productName = product.productName;
    categoryId = "";
    productInternalName = product.internalName;
    productFriendlyUrl = "";
    pdpUrl = "";
    productImageUrl = "";
    productImageAlt = "";
    productImageAltUrl= "";
    price = "";
    listPrice = "";
    plpLabel = "";
    ProductContentWrapper productContentWrapper = new ProductContentWrapper(product, request);

    
    if (UtilValidate.isNotEmpty(productContentWrapper))
    {
        Map productContentIdMap = FastMap.newInstance();

        productContentList = product.getRelatedCache("ProductContent");
		productContentList = EntityUtil.filterByDate(productContentList,true);
		if (UtilValidate.isNotEmpty(productContentList))
		{
            for (GenericValue productContent: productContentList) 
            {
    		   productContentTypeId = productContent.productContentTypeId;
               productContentIdMap.put(productContent.productContentTypeId,productContent.contentId);
            }
		}

		productContentId = productContentIdMap.get("PRODUCT_NAME");
        if (UtilValidate.isNotEmpty(productContentId))
        {
    	    productName = productContentWrapper.get("PRODUCT_NAME");
        }
	    productContentId = productContentIdMap.get("SMALL_IMAGE_URL");
        if (UtilValidate.isNotEmpty(productContentId))
        {
        	productImageUrl = productContentWrapper.get("SMALL_IMAGE_URL");
        }
	    productContentId = productContentIdMap.get("SMALL_IMAGE_ALT_URL");
        if (UtilValidate.isNotEmpty(productContentId))
        {
        	productImageAltUrl = productContentWrapper.get("SMALL_IMAGE_ALT_URL");
        }
        productContentId = productContentIdMap.get("PLP_LABEL");
        if (UtilValidate.isNotEmpty(productContentId))
        {
        	plpLabel = productContentWrapper.get("PLP_LABEL");
        }
        
        
    }
    
    priceContext = [product : product, currencyUomId : cart.getCurrency(),"userLogin": userLogin];
    priceContext.productStoreId = productStoreId;
    priceContext.productStoreGroupId = "_NA_";
    priceMap = dispatcher.runSync("calculateProductPrice",priceContext);
    context.priceMap = priceMap;
    price = priceMap.defaultPrice ;
    listPrice = priceMap.listPrice;

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
	reviewMethod = Util.getProductStoreParm(request, "REVIEW_METHOD");
	reviewSize=0;
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
		 	       context.put("averageStarPLPRating", averageCustomerRating);
		        }
		        else
		        {
		 	       context.put("averageStarPLPRating", "");
		        }
		    }
		    else
		    {
			    context.put("averageStarPLPRating", "");
		    }
	        
	        reviews = product.getRelatedCache("ProductReview");
	        if (UtilValidate.isNotEmpty(reviews))
	        {
	            reviews = EntityUtil.filterByAnd(reviews, UtilMisc.toMap("statusId", "PRR_APPROVED", "productStoreId", productStoreId));
	            reviewSize=reviews.size();
	        }
	        
	    }
	}
    context.put("reviewPLPSize",reviewSize);
    
    // get the no of reviews
    
    context.productName = productName;
    context.productId = productId;
    context.categoryId = categoryId;
    context.productInternalName = productInternalName;
    context.productImageUrl = productImageUrl;
    context.price=price;
    context.listPrice = listPrice;
    context.product = product;
    context.productContentWrapper = productContentWrapper;

    context.productImageAlt = productImageAlt;
    context.productImageAltUrl = productImageAltUrl;
	context.plpLabel = plpLabel;
   
    productFriendlyUrl = CatalogUrlServlet.makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId='+productId+'&productCategoryId='+categoryId);
    context.pdpUrl = 'eCommerceProductDetail?productId='+productId+'&productCategoryId='+categoryId;
    
    List productSelectableFeatureAndAppl = FastList.newInstance();
    List productVariantFeatureList = FastList.newInstance();
    List productAssoc= FastList.newInstance();
    
    Map productVariantContentWrapperMap = FastMap.newInstance();
    Map productVariantProductContentIdMap = FastMap.newInstance();
    Map productFeatureFirstVariantIdMap = FastMap.newInstance();

    featureValueSelected = request.getAttribute("featureValueSelected");
    productFeatureSelectVariantId="";
    productFeatureSelectVariantProduct = "";

    
    if(UtilValidate.isNotEmpty((context.product).isVirtual) && ((context.product).isVirtual).toUpperCase()== "Y")
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
        	PLP_FACET_GROUP_VARIANT_MATCH = Util.getProductStoreParm(request,"PLP_FACET_GROUP_VARIANT_MATCH");
        	
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
        }
    
	    if (UtilValidate.isNotEmpty(plpFacetGroupVariantSwatch))
	    {
	      productSelectableFeatureAndAppl = delegator.findByAndCache("ProductFeatureAndAppl", UtilMisc.toMap("productId",productId, "productFeatureTypeId", plpFacetGroupVariantSwatch, "productFeatureApplTypeId", "SELECTABLE_FEATURE"), UtilMisc.toList("sequenceNum"));
	      productSelectableFeatureAndAppl = EntityUtil.filterByDate(productSelectableFeatureAndAppl,true);
	      
          //BUILD CONTEXT MAP FOR PRODUCT_FEATURE_DATA_RESOURCE (productFeatureId, objectInfo)
          Map productFeatureDataResourceMap = FastMap.newInstance();
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
          context.productFeatureDataResourceMap = productFeatureDataResourceMap;
	      
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
    
	  context.featureValueSelected = featureValueSelected;
	  if(UtilValidate.isNotEmpty(productFeatureSelectVariantId))
	  {
	        productImageUrl = "";
	        productImageAltUrl = "";
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
    

    context.productSelectableFeatureAndAppl = productSelectableFeatureAndAppl;
    context.productFriendlyUrl = productFriendlyUrl;
    context.plpProductVariantContentWrapperMap = productVariantContentWrapperMap;
    context.plpProductFeatureFirstVariantIdMap = productFeatureFirstVariantIdMap;
    context.plpProductVariantProductContentIdMap = productVariantProductContentIdMap;
  }
}