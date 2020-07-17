package common;

import org.ofbiz.base.util.*;
import java.lang.*;
import java.text.NumberFormat;
import org.ofbiz.webapp.taglib.*;
import org.ofbiz.webapp.stats.VisitHandler;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.*;
import org.ofbiz.product.store.*;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import javolution.util.FastList;
import com.osafe.util.Util;
import com.osafe.services.OsafeManageXml;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilMisc;
import com.osafe.services.InventoryServices;
import org.ofbiz.party.content.PartyContentWrapper;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import com.osafe.services.CatalogUrlServlet;

productStore = ProductStoreWorker.getProductStore(request);
productStoreId = productStore.get("productStoreId");
String productId = parameters.productId;
String productCategoryId = parameters.productCategoryId;
inlineCounter = request.getAttribute("inlineCounter");
contentPathPrefix = CatalogWorker.getContentPathPrefix(request);
catalogName = CatalogWorker.getCatalogName(request);
currentCatalogId = CatalogWorker.getCurrentCatalogId(request);
cart = ShoppingCartEvents.getCartObject(request);
webSiteId = CatalogWorker.getWebSiteId(request);
autoUserLogin = request.getSession().getAttribute("autoUserLogin");
imagePlaceHolder="/osafe_theme/images/user_content/images/NotFoundImage.jpg";
imageLargePlaceHolder="/osafe_theme/images/user_content/images/NotFoundImagePDPLarge.jpg";

selectOne = UtilProperties.getMessage("OSafeUiLabels", "SelectOneLabel", locale);
jsBufDefault = new StringBuffer();
jsBufDefault.append("<script language=\"JavaScript\" type=\"text/javascript\">");    
String buildNext(Map map, List order, String current, String prefix, Map featureTypes) {
    def ct = 0;
    def featureType = null;
    def featureIndex = 0;
    def buf = new StringBuffer();
    buf.append("function listFT" + current + prefix + "() { ");
    buf.append("document.forms[\"addform\"].elements[\"FT" + current + "\"].options.length = 1;");
    
    buf.append("document.forms[\"addform\"].elements[\"FT" + current + "\"].options[0] = new Option(\"" + selectOne + "\",\"\",true,true);");
    map.each { key, value ->
        def optValue = null;

        if (order.indexOf(current) == (order.size()-1)) {
            optValue = value.iterator().next();
        } else {
            optValue = prefix + "_" + ct;
        }
        String selectedFeature = current+":"+key;
        if(parameters.productFeatureType && parameters.productFeatureType == selectedFeature) {
            featureType = current;
            featureIndex = ct;
        }
        buf.append("document.forms[\"addform\"].elements[\"FT" + current + "\"].options[" + (ct + 1) + "] = new Option(\"" + key + "\",\"" + optValue + "\");");
        ct++;
    }
    buf.append(" }");
    if(map.size()==1)
    {    
        jsBufDefault.append("getList(\"FT" + current + "\", \""+featureIndex+"\", 1);");
    }
        
    if (order.indexOf(current) < (order.size()-1)) {
        ct = 0;
        map.each { key, value ->
            def nextOrder = order.get(order.indexOf(current)+1);
            def newPrefix = prefix + "_" + ct;
            buf.append(buildNext(value, order, nextOrder, newPrefix, featureTypes));
            ct++;
        }
    }
    return buf.toString();
}

String buildNextLi(Map map, List order, String current, String prefix, Map featureTypes) {
    def ct = 0;
    def featureType = null;
    def featureIndex = null;
    def productFeatureId = null;
    def productFeatureTypeId = null;
    def buf = new StringBuffer();
    buf.append("var VARSTOCK = new Object();");
    buf.append("function listLiFT" + current + prefix + "() { ");
    buf.append("document.getElementById(\"LiFT" + current + "\").innerHTML = \"\";");
    map.each { key, value ->
        def optValue = null;
        def stockClass = "";
        if (order.indexOf(current) == (order.size()-1)) {
            optValue = value.iterator().next();
            
            inventoryLevelMap = InventoryServices.getProductInventoryLevel(optValue, request);
            
            inventoryOutOfStockTo = inventoryLevelMap.get("inventoryLevelOutOfStockTo");
            inventoryInStockFrom = inventoryLevelMap.get("inventoryLevelInStockFrom");
            inventoryLevel = inventoryLevelMap.get("inventoryLevel");
            
	        if(inventoryLevel <= inventoryOutOfStockTo)
	        {
	            stockClass = "outOfStock";
	        }
	        else
	        {
	            if(inventoryLevel >= inventoryInStockFrom)
	            {
	                stockClass = "inStock";
	            }
	            else
	            {
	                stockClass = "lowStock";
	            }
	        }
            buf.append("VARSTOCK['" + optValue + "'] = \"" + stockClass + "\";");
        } else {
            optValue = prefix + "_" + ct;
        }
        
        String selectedFeature = current+":"+key;
        def selectedClass = false;
        
        if(parameters.productFeatureType)
        {
            productFeatureTypeIdArr = parameters.productFeatureType.split(":");
            productFeatureTypeId = productFeatureTypeIdArr[0];
            if(parameters.productFeatureType == selectedFeature) {
                featureType = current;
                featureIndex = ct;
                selectedClass = true;
            }
        }
        
        if(!parameters.productFeatureType || (UtilValidate.isNotEmpty(productFeatureTypeId) && current != productFeatureTypeId))
        {
            if(map.size()==1)
            {
                selectedClass = true;
            }
        }
        buf.append("var li = document.createElement('li');");
        if(selectedClass == true)
        {
            buf.append("li.setAttribute(\"class\",\"selected "+stockClass+"\");");
        } else {
            buf.append("li.setAttribute(\"class\",\""+stockClass+"\");");
        }
        
        liText = "<a href=javascript:void(0); onclick=getList('FT" + current + "','" + ct + "',1);>" + key + "</a>";
        buf.append("document.getElementById(\"LiFT" + current + "\").appendChild(li);");
        buf.append("li.innerHTML = \"" + liText + "\";");
        ct++;
    }
    
    buf.append("addFeatureAction(\"LiFT" + current + "\");");
    buf.append(" }");
    if (order.indexOf(current) < (order.size()-1)) {
        ct = 0;
        map.each { key, value ->
            def nextOrder = order.get(order.indexOf(current)+1);
            def newPrefix = prefix + "_" + ct;
            buf.append(buildNextLi(value, order, nextOrder, newPrefix, featureTypes));
            ct++;
        }
    }
    return buf.toString();
}

productCategoryId = "";

if (UtilValidate.isNotEmpty(productId)) 
{
    if(UtilValidate.isEmpty(productCategoryId))
    {
        productCategoryMemberList = delegator.findByAnd("ProductCategoryMember", UtilMisc.toMap("productId", productId));
        if(UtilValidate.isNotEmpty(productCategoryMemberList))
        {
            productCategoryMemberList = EntityUtil.filterByDate(productCategoryMemberList);
            productCategoryMember = EntityUtil.getFirst(productCategoryMemberList);
            productCategoryId = productCategoryMember.productCategoryId; 
        }    
    }
    if (UtilValidate.isNotEmpty(productCategoryId)) 
    {
        productCategoryId = StringUtil.replaceString(productCategoryId,"/","");
        context.productCategoryId = productCategoryId;
        GenericValue gvProductCategory =  delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId",productCategoryId), true);
        if (UtilValidate.isNotEmpty(gvProductCategory))
        {
	        CategoryContentWrapper currentProductCategoryContentWrapper = new CategoryContentWrapper(gvProductCategory, request);
	        context.currentProductCategory = gvProductCategory;
	        context.currentProductCategoryContentWrapper = currentProductCategoryContentWrapper;


            productCategoryContentList = delegator.findByAnd("ProductCategoryContent", UtilMisc.toMap("productCategoryId" , productCategoryId, "prodCatContentTypeId" , "PDP_ADDITIONAL"));
            if (productCategoryContentList) 
            {
              prodCategoryContentList = EntityUtil.filterByDate(productCategoryContentList);
              prodCategoryContent = EntityUtil.getFirst(prodCategoryContentList);
              contentId = prodCategoryContent.contentId;
              if (contentId) 
              {
                 pdpEspotContent = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), true);
                 if (pdpEspotContent) 
                 {
                     context.pdpEspotContent = pdpEspotContent;
                 }
              }
            }
        }
    }
    
    

    GenericValue gvProduct =  delegator.findOne("Product", UtilMisc.toMap("productId",productId), true);
 // Setting variables required in the Manufacturer Info section 
    if (UtilValidate.isNotEmpty(gvProduct)) 
    {
        productId = gvProduct.productId;
        partyManufacturer=gvProduct.getRelatedOne("ManufacturerParty");
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

    // first make sure this isn't a variant that has an associated virtual product, if it does show that instead of the variant
    virtualProductId = ProductWorker.getVariantVirtualId(gvProduct);
    if (virtualProductId) {
        productId = virtualProductId;
        gvProduct = delegator.findByPrimaryKeyCache("Product", [productId : productId]);
    }

    context.title = "";



    if (UtilValidate.isNotEmpty(gvProduct)) 
    {
        context.product = gvProduct;
        context.currentProduct = gvProduct;
        
        context.product_id = gvProduct.productId;
        context.productId = gvProduct.productId;
	    context.internalName = gvProduct.internalName;
	    context.manufacturerPartyId = gvProduct.manufacturerPartyId;
	    context.productName = gvProduct.productName;

        // add one to the product calculated info view to count total times viewed for a product; it is done async
        dispatcher.runAsync("countProductView", [productId : gvProduct.productId, weight : new Long(1)], false);

        // set this as a recently viewed
        pdpRecentViewedaMaxStr = Util.getProductStoreParm(request, "PDP_RECENT_VIEW_MAX");
        try {
            pdpRecentViewedMax = Integer.parseInt(pdpRecentViewedaMaxStr);
        } catch(NumberFormatException) {
            pdpRecentViewedMax = 0;
        }
        if(pdpRecentViewedMax > 0)
        {
            lastViewedProducts = session.getAttribute("lastViewedProducts");
            if (!lastViewedProducts) {
                lastViewedProducts = [];
                session.setAttribute("lastViewedProducts", lastViewedProducts);
            }
            lastViewedProducts.remove(productId);
            lastViewedProducts.add(0, productId);
        } else {
            session.removeAttribute("lastViewedProducts");
        }
        context.pdpRecentViewedMax = pdpRecentViewedMax;
        
        //SET PRODUCT CONTENT IMAGE URLS
        ProductContentWrapper productContentWrapper = new ProductContentWrapper(gvProduct, request);
        context.productContentWrapper = productContentWrapper;
	    productName = productContentWrapper.get("PRODUCT_NAME");
        if (UtilValidate.isNotEmpty(productName))
        {
     	    context.pdpProductName = productName;
	        context.productName = productName;
        }
        
	    imageUrl = productContentWrapper.get("ADDTOCART_IMAGE");
        if (UtilValidate.isNotEmpty(imageUrl) && UtilValidate.isNotEmpty(imageUrl.toString()))
        {
	      context.addToCartImageUrl = contentPathPrefix + imageUrl;
        }
        else
        {
	      context.addToCartImageUrl = imagePlaceHolder;
        }

	    imageUrl = productContentWrapper.get("LARGE_IMAGE_URL");
        if (UtilValidate.isNotEmpty(imageUrl) && UtilValidate.isNotEmpty(imageUrl.toString()))
        {
	      context.productLargeImageUrl = contentPathPrefix + imageUrl;
	      request.setAttribute("largeImageUrl",contentPathPrefix + imageUrl);
        }
        else
        {
	      context.productLargeImageUrl = imageLargePlaceHolder;
        }

	    imageUrl = productContentWrapper.get("DETAIL_IMAGE_URL");
        if (UtilValidate.isNotEmpty(imageUrl) && UtilValidate.isNotEmpty(imageUrl.toString()))
        {
	      context.productDetailImageUrl = contentPathPrefix + imageUrl;
	      request.setAttribute("detailImageUrl",contentPathPrefix + imageUrl);
        }
	
	    imageUrl = productContentWrapper.get("THUMBNAIL_IMAGE_URL");
        if (UtilValidate.isNotEmpty(imageUrl) && UtilValidate.isNotEmpty(imageUrl.toString()))
        {
	      context.productThumbImageUrl = contentPathPrefix + imageUrl;
        }
        else
        {
	      context.productThumbImageUrl = "";
        }
        try {
            maxAltImages = Integer.parseInt(UtilProperties.getPropertyValue("osafe", "pdp-alternate-images"));
        }
        catch(NumberFormatException nfe) {
    	    Debug.logError(nfe, nfe.getMessage(),"");
    	    maxAltImages = 10;
        }
    
        context.maxAltImages = maxAltImages;
        
        for (int i = 1; i <= (maxAltImages +1); i++)
        {
		    imageUrl = productContentWrapper.get("ADDITIONAL_IMAGE_" + i);
            if (UtilValidate.isNotEmpty(imageUrl) && UtilValidate.isNotEmpty(imageUrl.toString()))
	        {
	          
		       context.put("productAddImageUrl" + i,contentPathPrefix + imageUrl);
	        }
            else
	        {
		       context.put("productAddImageUrl" + i,"");
	        }
	        
        }
	
        for (int i = 1; i <= (maxAltImages +1); i++)
        {
		    imageUrl = productContentWrapper.get("XTRA_IMG_" + i + "_LARGE");
            if (UtilValidate.isNotEmpty(imageUrl) && UtilValidate.isNotEmpty(imageUrl.toString()))
	        {
	          
		       context.put("productXtraAddLargeImageUrl" + i,contentPathPrefix + imageUrl);
	        }
            else
	        {
		       context.put("productXtraAddLargeImageUrl" + i,"");
	        }
	        
        }

        for (int i = 1; i <= (maxAltImages +1); i++)
        {
		    imageUrl = productContentWrapper.get("XTRA_IMG_" + i + "_DETAIL");
            if (UtilValidate.isNotEmpty(imageUrl) && UtilValidate.isNotEmpty(imageUrl.toString()))
	        {
	          
		       context.put("productXtraAddImageUrl" + i,contentPathPrefix + imageUrl);
	        }
            else
	        {
		       context.put("productXtraAddImageUrl" + i,"");
	        }
        }

	    imageUrl = productContentWrapper.get("PDP_VIDEO_URL");
        if (UtilValidate.isNotEmpty(imageUrl) && UtilValidate.isNotEmpty(imageUrl.toString()))
        {
	      context.pdpVideoUrl = imageUrl;
        }
        else
        {
	      context.pdpVideoUrl = "";
	    }
        
	    imageUrl = productContentWrapper.get("PDP_VIDEO_360_URL");
        if (UtilValidate.isNotEmpty(imageUrl) && UtilValidate.isNotEmpty(imageUrl.toString()))
        {
	      context.pdpVideo360Url = imageUrl;
        }
        else
        {
	      context.pdpVideo360Url = "";
	    }
        
        String PDPtext = productContentWrapper.get("PDP_LABEL");
        if (UtilValidate.isNotEmpty(PDPtext))
        {
            context.tertiaryInformation =  PDPtext;
        }
        else
        {
            internalNameLabel = UtilProperties.getMessage("OSafeUiLabels", "InternalNameLabel", locale);
            context.tertiaryInformation =  internalNameLabel + " " + gvProduct.internalName ;
        }
        
        pdpFacetGroup = Util.getProductStoreParm(request, "PDP_FACET_GROUP_VARIANT_SWATCH_IMG");
        if (UtilValidate.isNotEmpty(pdpFacetGroup))
        {
     	    pdpFacetGroup = pdpFacetGroup.toUpperCase();
        }
   	    context.PRODUCT_STORE_PARM_FACET = pdpFacetGroup;



		productContentList = delegator.findByAnd("ProductContent", UtilMisc.toMap("productId" ,gvProduct.productId));
		productContentList = EntityUtil.filterByDate(productContentList);
		if (UtilValidate.isNotEmpty(productContentList))
		{
            for (GenericValue productContent: productContentList) 
            {
    		   productContentTypeId = productContent.productContentTypeId;
    		   context.put(productContent.productContentTypeId,productContent.contentId);
            }
		}

        //Check Product Attribute for Meta Data Overrides
        productAttr = delegator.findByAnd("ProductAttribute", UtilMisc.toMap("productId", gvProduct.productId));
        productAttrMap = FastMap.newInstance();
        if (UtilValidate.isNotEmpty(productAttr))
        {
            attrlIter = productAttr.iterator();
            while (attrlIter.hasNext()) {
                attr = (GenericValue) attrlIter.next();
                productAttrMap.put(attr.getString("attrName"),attr.getString("attrValue"));
            }
        }

        //Set Meta title, Description and Keywords
        String productName = productContentWrapper.get("PRODUCT_NAME");
        if (UtilValidate.isEmpty(productName)) {
            productName = gvProduct.productName;
        }
        if(UtilValidate.isNotEmpty(productName)) {
            context.metaTitle = productName;
            context.pageTitle = productName;
        }
        if(UtilValidate.isNotEmpty(productContentWrapper.get("DESCRIPTION"))) {
            context.metaKeywords = productContentWrapper.get("DESCRIPTION");
        }
        if(UtilValidate.isNotEmpty(productContentWrapper.get("LONG_DESCRIPTION"))) {
            context.metaDescription = productContentWrapper.get("LONG_DESCRIPTION");
        }
        //override Meta title, Description and Keywords
        String metaTitle = productContentWrapper.get("HTML_PAGE_TITLE");
        if(UtilValidate.isNotEmpty(metaTitle)) {
            context.metaTitle = metaTitle;
        } else if (UtilValidate.isNotEmpty(productAttrMap.get("SEO_TITLE"))) {
            context.metaTitle = productAttrMap.get("SEO_TITLE");
        }
        String metaKeywords = productContentWrapper.get("HTML_PAGE_META_KEY");
        if(UtilValidate.isNotEmpty(metaKeywords)) {
            context.metaKeywords = metaKeywords;
        } else if (UtilValidate.isNotEmpty(productAttrMap.get("SEO_KEYWORDS"))) {
            context.metaKeywords = productAttrMap.get("SEO_KEYWORDS");
        }
        String metaDescription = productContentWrapper.get("HTML_PAGE_META_DESC");
        if(UtilValidate.isNotEmpty(metaDescription)) {
            context.metaDescription = metaDescription;
        } else if (UtilValidate.isNotEmpty(productAttrMap.get("SEO_DESCRIPTION"))) {
            context.metaKeywords = productAttrMap.get("SEO_DESCRIPTION");
        }
        if (cart.isSalesOrder()) {
            // sales order: run the "calculateProductPrice" service
            priceContext = [product : gvProduct, prodCatalogId : currentCatalogId,
                        currencyUomId : cart.getCurrency(), autoUserLogin : autoUserLogin];
            priceContext.webSiteId = webSiteId;
            priceContext.productStoreId = productStoreId;
            priceContext.checkIncludeVat = "Y";
            priceContext.agreementId = cart.getAgreementId();
            priceContext.partyId = cart.getPartyId();  // IMPORTANT: must put this in, or price will be calculated for the CSR instead of the customer
            pdpPriceMap = dispatcher.runSync("calculateProductPrice", priceContext);
            context.pdpPriceMap = pdpPriceMap;
        } else {
            // purchase order: run the "calculatePurchasePrice" service
            priceContext = [product : gvProduct, currencyUomId : cart.getCurrency(),
                        partyId : cart.getPartyId(), userLogin : userLogin];
            pdpPriceMap = dispatcher.runSync("calculatePurchasePrice", priceContext);
            context.pdpPriceMap = pdpPriceMap;
        }


        // get QUANTITY price break rules to show
        volumePricingRule = [];
        volumePricingRuleMap = FastMap.newInstance();
        productIdConds = delegator.findByAnd("ProductPriceCond", [inputParamEnumId: "PRIP_PRODUCT_ID", condValue: gvProduct.productId],["productPriceRuleId"]);
        if (UtilValidate.isNotEmpty(productIdConds))
        {
            for (GenericValue priceCond: productIdConds) {
                priceRule = priceCond.getRelatedOne("ProductPriceRule");
                qtyBreakIdConds = delegator.findByAnd("ProductPriceCond", [inputParamEnumId: "PRIP_QUANTITY", productPriceRuleId: priceRule.productPriceRuleId],["productPriceRuleId"]);
                if (UtilValidate.isNotEmpty(qtyBreakIdConds)) {
                    //Check for Active Price Rule
                    List<GenericValue> productPriceRuleList = delegator.findByAnd("ProductPriceRule", UtilMisc.toMap("productPriceRuleId",priceRule.productPriceRuleId));
                    productPriceRuleList = EntityUtil.filterByDate(productPriceRuleList);
                    if(UtilValidate.isNotEmpty(productPriceRuleList)) {
                        priceIdActions = delegator.findByAnd("ProductPriceAction", [productPriceActionTypeId: "PRICE_FLAT", productPriceRuleId: priceRule.productPriceRuleId],["productPriceRuleId"]);
                        priceIdAction = EntityUtil.getFirst(priceIdActions);
                        volumePricingRule.add(priceRule);
                        volumePricingRuleMap.put(priceRule.productPriceRuleId,priceIdAction.amount);
                    }
                }
            }
        }
        context.volumePricingRule = volumePricingRule;
        context.volumePricingRuleMap = volumePricingRuleMap;

        // get the product distinguishing features
        disFeatureMap = dispatcher.runSync("getProductFeaturesByType", [productId : productId, productFeatureApplTypeId : "DISTINGUISHING_FEAT"]);
        context.disFeatureTypesList = disFeatureMap.productFeatureTypes;
        context.disFeatureByTypeMap = disFeatureMap.productFeaturesByType;

        //PRODUCT RATINGS
	    decimals=Integer.parseInt("1");
	    rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
	    context.put("decimals",decimals);
	    context.put("rounding",rounding);
	    // get the average rating
	     averageRating= ProductWorker.getAverageProductRating(delegator,gvProduct.productId);
         if (UtilValidate.isNotEmpty(averageRating) && averageRating > 0)
         {
	       averageCustomerRating= averageRating.setScale(1,rounding);
	       context.put("averageStarRating", averageCustomerRating);
         }
	    
       reviewByAnd = UtilMisc.toMap("statusId", "PRR_APPROVED", "productStoreId", productStoreId,"productId", gvProduct.productId);
       reviews = delegator.findByAnd("ProductReview", reviewByAnd);
	   context.put("reviewSize",reviews.size());
        


        context.variantTree = null;
        context.variantTreeSize = null;
        context.variantSample = null;
        context.variantSampleKeys = null;
        context.variantSampleSize = null;
        featureTypes = [:];
        featureOrder = [];
        // Special Variant Code
        if ("Y".equals(gvProduct.isVirtual)) {
            if ("VV_FEATURETREE".equals(gvProduct.getString("virtualVariantMethodEnum"))) {
                context.featureLists = ProductWorker.getSelectableProductFeaturesByTypesAndSeq(gvProduct);
            } else {
                featureMap = dispatcher.runSync("getProductFeatureSet", [productId : productId]);
                featureSet = featureMap.featureSet;
                productFeatureCatGroupApplCond = null;
                orderBy = ["sequenceNum"];
                
                if(UtilValidate.isNotEmpty(productCategoryId))
                {
                    productFeatureCatGroupApplCond = EntityCondition.makeCondition([EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId),
                                               EntityCondition.makeCondition("productFeatureGroupId", EntityOperator.IN, featureSet)], EntityOperator.AND);
                }
                                               
                productFeatureCatGroupAppls = delegator.findList("ProductFeatureCatGrpAppl", productFeatureCatGroupApplCond, null, orderBy, null, false);
                if(UtilValidate.isNotEmpty(productFeatureCatGroupAppls))
                {
                    featureSet = EntityUtil.getFieldListFromEntityList(productFeatureCatGroupAppls, "productFeatureGroupId", true);
                }
                if (featureSet) {
                    //if order is purchase then don't calculate available inventory for product.
                    if (cart.isPurchaseOrder()) {
                        variantTreeMap = dispatcher.runSync("getProductVariantTree", [productId : productId, featureOrder : featureSet, checkInventory: false]);
                    } else {
                        variantTreeMap = dispatcher.runSync("getProductVariantTree", [productId : productId, featureOrder : featureSet, productStoreId : productStoreId]);
                    }
                    variantTree = variantTreeMap.variantTree;
                    imageMap = variantTreeMap.variantSample;
                    virtualVariant = variantTreeMap.virtualVariant;
                    context.virtualVariant = virtualVariant;
                    if (variantTree) {
                        context.variantTree = variantTree;
                        context.variantTreeSize = variantTree.size();
                    }
                    unavailableVariants = variantTreeMap.unavailableVariants;
                    if (unavailableVariants) {
                        context.unavailableVariants = unavailableVariants;
                    }
                    if (imageMap) {
                        context.variantSample = imageMap;
                        context.variantSampleKeys = imageMap.keySet();
                        context.variantSampleSize = imageMap.size();
                    }
                    context.featureSet = featureSet;

                    if (variantTree) {
                        featureOrder = new LinkedList(featureSet);
                        featureOrder.each { featureKey ->
                            featureValue = delegator.findByPrimaryKeyCache("ProductFeatureType", [productFeatureTypeId : featureKey]);
                            fValue = featureValue.get("description") ?: featureValue.productFeatureTypeId;
                            featureTypes[featureKey] = fValue;
                        }
                    }
                    List productFeatureTypeIdList = FastList.newInstance();
                    if (variantTree) {
                        featureOrder = new LinkedList(featureSet);
                        featureOrder.each { featureKey ->
                            productFeatureTypeIdList.add(featureKey);
                        }
                    }
                    productAssoc = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId" ,productId,'productAssocTypeId','PRODUCT_VARIANT'));
                    List varProductIdList = FastList.newInstance();
                    context.featureTypes = featureTypes;
                    context.featureOrder = featureOrder;
                    if (featureOrder) {
                        context.featureOrderFirst = featureOrder[0];
                    }

                    productFeatureAndApplsSelects = delegator.findByAndCache("ProductFeatureAndAppl", UtilMisc.toMap("productId" ,productId,'productFeatureApplTypeId','SELECTABLE_FEATURE'), UtilMisc.toList("sequenceNum"));
                    productFeatureAndApplsSelects = EntityUtil.filterByDate(productFeatureAndApplsSelects, true);
                    String lastProductFeatureTypeId="";
                    List productFeatureAndApplSelectList = FastList.newInstance();
                    Map productFeatureAndApplSelectMap = FastMap.newInstance();
                    for(GenericValue productFeatureAndAppl : productFeatureAndApplsSelects) 
                    {
                       String productFeatureTypeId = productFeatureAndAppl.productFeatureTypeId;
                       if (!productFeatureAndApplSelectMap.containsKey(productFeatureTypeId))
                       {
                           productFeatureAndApplSelectList = FastList.newInstance();
                       }
                       else
                       {
                           productFeatureAndApplSelectList = productFeatureAndApplSelectMap.get(productFeatureTypeId);
                       }
                       productFeatureAndApplSelectList.add(productFeatureAndAppl);
                       productFeatureAndApplSelectMap.put(productFeatureTypeId,productFeatureAndApplSelectList);
                    }
                    context.productFeatureAndApplSelectMap = productFeatureAndApplSelectMap;

                    if (variantTree && imageMap) 
                    {
                        jsBuf = new StringBuffer();
                        jsBuf.append("<script language=\"JavaScript\" type=\"text/javascript\">");
                        jsBuf.append("var OPT = new Array(" + featureOrder.size() + ");");
                        jsBuf.append("var VIR = new Array(" + virtualVariant.size() + ");");
                        jsBuf.append("var VARMAP = new Object();");
                        //jsBuf.append("var detailImageUrl = null;");

                        featureOrder.eachWithIndex { feature, i ->
                            jsBuf.append("OPT[" + i + "] = \"FT" + feature + "\";");
                            
                            featureExist = [];
                            for(GenericValue pAssoc : productAssoc) 
                            {
                                productFeatureApplList = delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap('productFeatureTypeId',feature,'productFeatureApplTypeId','STANDARD_FEATURE','productId',pAssoc.productIdTo));
                                for(GenericValue productFeatureAppl : productFeatureApplList) 
                                {
                                    String mapKey = "FT"+feature+"_"+productFeatureAppl.description;
                                    if(!featureExist.contains(mapKey))
                                    {
                                        jsBuf.append("VARMAP['" + mapKey + "'] = \"" + productFeatureAppl.productId + "\";");
                                        featureExist.add(mapKey);
                                    }
                                }
                            }
                            
                        }
                        
                        
                        virtualVariant.eachWithIndex { variant, i ->
                            jsBuf.append("VIR[" + i + "] = \"" + variant + "\";");
                        }
                        

                        // build the top level
                        featureTyp = null;
                        featureIdx = 0;
                        topLevelName = featureOrder[0];
                        jsBuf.append("var VARSTOCK = new Object();");
                        jsBuf.append("function list" + topLevelName + "() {");
                        jsBuf.append("document.forms[\"addform\"].elements[\"FT" + topLevelName + "\"].options.length = 1;");
                        jsBuf.append("document.forms[\"addform\"].elements[\"FT" + topLevelName + "\"].options[0] = new Option(\"" + selectOne + "\",\"\",true,true);");
                        if (variantTree) {
                            featureOrder.each { featureKey ->
                                jsBuf.append("document.forms[\"addform\"].elements[\"FT" + featureKey + "\"].options.length = 1;");
                            }
                                                        
                            counter = 0;
                            featureCnt = 0;
                            String varProductId = null;
                            variantTree.each { key, value -> 
                                opt = null;
                                if (featureOrder.size() == 1) {
                                    opt = value.iterator().next();
                                    inventoryLevelMap = InventoryServices.getProductInventoryLevel(opt, request);
            
                                    inventoryOutOfStockTo = inventoryLevelMap.get("inventoryLevelOutOfStockTo");
                                    inventoryInStockFrom = inventoryLevelMap.get("inventoryLevelInStockFrom");
                                    inventoryLevel = inventoryLevelMap.get("inventoryLevel");
            
	                                if(inventoryLevel <= inventoryOutOfStockTo)
	                                {
	                                    stockClass = "outOfStock";
	                                }
	                                else
	                                {
	                                    if(inventoryLevel >= inventoryInStockFrom)
	                                    {
	                                        stockClass = "inStock";
	                                    }
	                                    else
	                                    {
	                                        stockClass = "lowStock";
	                                    }
	                                }
                                    jsBuf.append("VARSTOCK['" + opt + "'] = \"" + stockClass + "\";");
                                } else {
                                    opt = counter as String;
                                }
                                // create the variant content wrapper
                                contentWrapper = new ProductContentWrapper(imageMap[key], request);
                                
                                jsBuf.append("document.forms[\"addform\"].elements[\"FT" + topLevelName + "\"].options[" + (counter+1) + "] = new Option(\"" + key + "\",\"" + opt + "\");");
                                String selFeature = topLevelName+"_"+key;
                                if(parameters.value && parameters.value == selFeature) {
                                    featureTyp = topLevelName;
                                    featureIdx = counter;
                                }
                                productFeatureType = topLevelName+":"+key;
                                if(parameters.productFeatureType)
                                {
                                    
                                    if(parameters.productFeatureType == productFeatureType)
                                    {
                                        featureCnt = counter;
                                    }
                                }
                                counter++;
                            }
                        }
                        jsBuf.append("addFeatureAction(\"LiFT" + topLevelName + "\");");
                        jsBuf.append("}");

                        jsBufDefault.append("getList(\"FT" + topLevelName + "\", \""+featureCnt+"\", 1);");

                        // build dynamic lists
                        if (variantTree) {
                            variantTree.values().eachWithIndex { varTree, topLevelKeysCt ->
                                cnt = "" + topLevelKeysCt;
                                if (varTree instanceof Map) {
                                    //varTree = new TreeMap(varTree);
                                    jsBuf.append(buildNext(varTree, featureOrder, featureOrder[1], cnt, featureTypes));
                                    jsBuf.append(buildNextLi(varTree, featureOrder, featureOrder[1], cnt, featureTypes));
                                }
                            }
                        }
                        Map productVariantMap = FastMap.newInstance();
                        // make a list of variant sku with requireAmount
                        variantsRes = dispatcher.runSync("getAssociatedProducts", [productId : productId, type : "PRODUCT_VARIANT", checkViewAllow : true, prodCatalogId : currentCatalogId]);
                        variants = variantsRes.assocProducts;
                        if (variants) {
                            amt = new StringBuffer();
                            amt.append("function checkAmtReq(sku) { ");
                            // Create the javascript to return the price for each variant
                            variantPriceJS = new StringBuffer();
                            variantPriceJS.append("function getVariantPrice(sku) { ");
                            // Format to apply the currency code to the variant price in the javascript
                            if (productStore) {
                                localeString = productStore.defaultLocaleString;
                                if (localeString) {
                                    locale = UtilMisc.parseLocale(localeString);
                                }
                            }
                            numberFormat = NumberFormat.getCurrencyInstance(locale);
                            variants.each { variantAssoc ->
                                variant = variantAssoc.getRelatedOne("AssocProduct");
                                // Get the price for each variant. Reuse the priceContext already setup for virtual product above and replace the product
                                priceContext.product = variant;
                                if (cart.isSalesOrder()) {
                                    // sales order: run the "calculateProductPrice" service
                                    variantPriceMap = dispatcher.runSync("calculateProductPrice", priceContext);
                                } else {
                                    variantPriceMap = dispatcher.runSync("calculatePurchasePrice", priceContext);
                                }
                                amt.append(" if (sku == \"" + variant.productId + "\") return \"" + (variant.requireAmount ?: "N") + "\"; ");
                                if (variantPriceMap && variantPriceMap.basePrice) {
                                    variantPriceJS.append("  if (sku == \"" + variant.productId + "\") return \"" + numberFormat.format(variantPriceMap.basePrice) + "\"; ");
                                }
                                varProductContentWrapper = new ProductContentWrapper(variant, request);
                                productVariantMap.put(variant.productId, varProductContentWrapper);
                            }
                            context.productVariantMap = productVariantMap;
                            context.productVariantMapKeys = productVariantMap.keySet();
                            amt.append(" } ");
                            variantPriceJS.append(" } ");
                        }
                        jsBuf.append(amt.toString());
                        jsBuf.append(variantPriceJS.toString());
                        jsBuf.append("</script>");

                        context.virtualJavaScript = jsBuf;
                    }
                }
            }
        }
    }
    jsBufDefault.append("</script>");
    context.virtualDefaultJavaScript = jsBufDefault;
   //Get Sequence for PDP Div Containers 
   XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafe.properties", "ecommerce-UiSequence-xml-file"), context);
   searchRestrictionMap = FastMap.newInstance();
   searchRestrictionMap.put("screen", "Y");
   uiSequenceSearchList =  OsafeManageXml.getSearchListFromXmlFile(XmlFilePath, searchRestrictionMap, uiSequenceScreen,true, false,true);
   
   for(Map uiSequenceScreenMap : uiSequenceSearchList) {
        if ((uiSequenceScreenMap.value instanceof String) && (UtilValidate.isInteger(uiSequenceScreenMap.value))) {
            if (UtilValidate.isNotEmpty(uiSequenceScreenMap.value)) {
                uiSequenceScreenMap.value = Integer.parseInt(uiSequenceScreenMap.value);
            } else {
                uiSequenceScreenMap.value = 0;
            }
        }
    }
   uiSequenceSearchList = UtilMisc.sortMaps(uiSequenceSearchList, UtilMisc.toList("value"));
   context.uiSequenceSearchList = uiSequenceSearchList;

   uiPdpTabSequenceSearchList =  OsafeManageXml.getSearchListFromXmlFile(XmlFilePath, searchRestrictionMap, "PDPTabs",true, false,true);
   for(Map uiPdpTabSequenceScreenMap : uiPdpTabSequenceSearchList) {
        if ((uiPdpTabSequenceScreenMap.value instanceof String) && (UtilValidate.isInteger(uiPdpTabSequenceScreenMap.value))) {
            if (UtilValidate.isNotEmpty(uiPdpTabSequenceScreenMap.value)) {
                uiPdpTabSequenceScreenMap.value = Integer.parseInt(uiPdpTabSequenceScreenMap.value);
            } else {
                uiPdpTabSequenceScreenMap.value = 0;
            }
        }
    }

    uiPdpTabSequenceGroupMaps = [:] as TreeMap;;
    for(Map uiPdpTabSequenceScreenMap : uiPdpTabSequenceSearchList) {
        if ((UtilValidate.isNotEmpty(uiPdpTabSequenceScreenMap.group)) && (UtilValidate.isInteger(uiPdpTabSequenceScreenMap.group)) && (uiPdpTabSequenceScreenMap.group != "0")) {
            groupNum = Integer.parseInt(uiPdpTabSequenceScreenMap.group)
            if (!uiPdpTabSequenceGroupMaps.containsKey(groupNum)) {
                searchGroupMapList =  OsafeManageXml.getSearchListFromListMaps(uiPdpTabSequenceSearchList, UtilMisc.toMap("group", "Y"), uiPdpTabSequenceScreenMap.group, true, false);
                if (UtilValidate.isNotEmpty(searchGroupMapList)) {
                    uiPdpTabSequenceGroupMaps.put(groupNum, UtilMisc.sortMaps(searchGroupMapList, UtilMisc.toList("value")))
                }
            }
        }
    }
    context.uiPdpTabSequenceGroupMaps = uiPdpTabSequenceGroupMaps;

   //USER PDP VARIABLES
   context.PRODUCT_ID=context.productId;
   context.PRODUCT_NAME=context.productName;
   context.PRODUCT_IMAGE_URL=context.productLargeImageUrl;   
   context.CATEGORY_ID=context.productCategoryId;
   context.REQUEST_URL=CatalogUrlServlet.makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId='+productId+'&productCategoryId='+productCategoryId);
}