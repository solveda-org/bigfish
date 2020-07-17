package common;

import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import com.osafe.util.Util;
import org.ofbiz.base.util.UtilMisc;
import com.osafe.control.SeoUrlHelper;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.Delegator;
import org.ofbiz.order.order.OrderReadHelper;
import com.osafe.services.InventoryServices;
import org.ofbiz.product.catalog.CatalogWorker;


orderItem = request.getAttribute("orderItem");
lineIndex = request.getAttribute("lineIndex");
currentCatalogId = CatalogWorker.getCurrentCatalogId(request);
autoUserLogin = request.getSession().getAttribute("autoUserLogin");
webSiteId = CatalogWorker.getWebSiteId(request);
orderHeader = null;
roleTypeId = "PLACING_CUSTOMER";
OrderReadHelper orderReadHelper = null;
virtualProduct="";
productCategoryId="";
offerPrice = "";
price = "";
displayPrice = "";
recurrencePrice = "";
recurrenceItem = "N";
recurrenceSavePercent = null;
stockInfo = "";

if (UtilValidate.isNotEmpty(orderItem))
{

	orderHeader = delegator.findOne("OrderHeader", [orderId : orderItem.orderId], true);
	orderReadHelper = new OrderReadHelper(orderHeader);

	currencyUom = orderReadHelper.getCurrency();
	if(UtilValidate.isEmpty(currencyUom))
	{
		currencyUom = Util.getProductStoreParm(request,"CURRENCY_UOM_DEFAULT");
	}

	product = orderItem.getRelatedOneCache("Product");
	urlProductId = product.productId;
	productId = product.productId;
	productCategoryId = product.primaryProductCategoryId;
	if(UtilValidate.isEmpty(productCategoryId))
	{
		productCategoryId = orderItem.productCategoryId;
	}
	if(UtilValidate.isEmpty(productCategoryId))
	{
		productCategoryMemberList = product.getRelatedCache("ProductCategoryMember");
		productCategoryMemberList = EntityUtil.filterByDate(productCategoryMemberList,true);
		productCategoryMemberList = EntityUtil.orderBy(productCategoryMemberList, UtilMisc.toList('sequenceNum'));
		if(UtilValidate.isNotEmpty(productCategoryMemberList))
		{
			productCategoryMember = EntityUtil.getFirst(productCategoryMemberList);
			productCategoryId = productCategoryMember.productCategoryId;
		}
	}
	
	if(UtilValidate.isNotEmpty(product.isVariant) && "Y".equals(product.isVariant))
	{
		virtualProduct = ProductWorker.getParentProduct(productId, delegator);
		urlProductId = virtualProduct.productId;
		if(UtilValidate.isEmpty(productCategoryId))
		{
			productCategoryMemberList = virtualProduct.getRelatedCache("ProductCategoryMember");
			productCategoryMemberList = EntityUtil.filterByDate(productCategoryMemberList,true);
			productCategoryMemberList = EntityUtil.orderBy(productCategoryMemberList, UtilMisc.toList('sequenceNum'));
			if(UtilValidate.isNotEmpty(productCategoryMemberList))
			{
				productCategoryMember = EntityUtil.getFirst(productCategoryMemberList);
				productCategoryId = productCategoryMember.productCategoryId;
			}
		}
	}
	
	//Product Image URL
	productImageUrl = ProductContentWrapper.getProductContentAsText(product, "SMALL_IMAGE_URL", locale, dispatcher);
	if(UtilValidate.isEmpty(productImageUrl) && UtilValidate.isNotEmpty(virtualProduct))
	{
		productImageUrl = ProductContentWrapper.getProductContentAsText(virtualProduct, "SMALL_IMAGE_URL", locale, dispatcher);
	}
	//If the string is a literal "null" make it an "" empty string then all normal logic can stay the same
	if(UtilValidate.isNotEmpty(productImageUrl) && "null".equals(productImageUrl))
	{
		productImageUrl = "";
	}
	//Product Alt Image URL
	productImageAltUrl = ProductContentWrapper.getProductContentAsText(product, "SMALL_IMAGE_ALT_URL", locale, dispatcher);
	if(UtilValidate.isEmpty(productImageAltUrl) && UtilValidate.isNotEmpty(virtualProduct))
	{
		productImageAltUrl = ProductContentWrapper.getProductContentAsText(virtualProduct, "SMALL_IMAGE_ALT_URL", locale, dispatcher);
	}
	//If the string is a literal "null" make it an "" empty string then all normal logic can stay the same
	if(UtilValidate.isNotEmpty(productImageAltUrl) && "null".equals(productImageAltUrl))
	{
		productImageAltUrl = "";
	}

	//Product Name
	productName = ProductContentWrapper.getProductContentAsText(product, "PRODUCT_NAME", locale, dispatcher);
	if(UtilValidate.isEmpty(productName) && UtilValidate.isNotEmpty(virtualProduct))
	{
		productName = ProductContentWrapper.getProductContentAsText(virtualProduct, "PRODUCT_NAME", locale, dispatcher);
	}	

	cartItemAdjustment = orderReadHelper.getOrderItemAdjustmentsTotal(orderItem);
	if (UtilValidate.isNotEmpty(cartItemAdjustment) && cartItemAdjustment < 0)
	{
		offerPrice = orderItem.unitPrice + (cartItemAdjustment/orderItem.quantity);
	}

	itemSubTotal = orderReadHelper.getOrderItemSubTotal(orderItem,orderReadHelper.getAdjustments());
	
	displayPrice = orderItem.unitPrice;
	recurrencePrice =  orderItem.unitPrice;
	if (orderItem.selectedAmount > 0)
	{
		price =  orderItem.unitPrice / orderItem.selectedAmount;
	}
	else
	{
		price =  orderItem.unitPrice;
	}

	if (UtilValidate.isNotEmpty(orderItem.shoppingListId))
	{
	        priceContext = [product : product, prodCatalogId : currentCatalogId,
	                    currencyUomId : currencyUom, autoUserLogin : autoUserLogin];
	        priceContext.webSiteId = webSiteId;
	        priceContext.productStoreId = productStoreId;
	        priceContext.checkIncludeVat = "Y";
	        priceContext.productPricePurposeId = "PURCHASE";
	        priceContext.partyId = orderReadHelper.getPlacingParty().partyId;  
	        productPriceMap = dispatcher.runSync("calculateProductPrice", priceContext);
			productPrice = productPriceMap.price;
			recurrenceSavePercent = (productPrice - recurrencePrice) / productPrice;
	    	recurrenceItem = "Y";
		
	}

	//stock
	inStock = true;
	inventoryLevelMap = InventoryServices.getProductInventoryLevel(urlProductId, request);
	inventoryLevel = inventoryLevelMap.get("inventoryLevel");
	inventoryInStockFrom = inventoryLevelMap.get("inventoryLevelInStockFrom");
	inventoryOutOfStockTo = inventoryLevelMap.get("inventoryLevelOutOfStockTo");
	if (inventoryLevel <= inventoryOutOfStockTo)
	{
		stockInfo = uiLabelMap.OutOfStockLabel;
		inStock = false;
	}
	else
	{
		if (inventoryLevel >= inventoryInStockFrom)
		{
			stockInfo = uiLabelMap.InStockLabel;
		}
		else
		{
			stockInfo = uiLabelMap.LowStockLabel;
		}
	}


	//BUILD CONTEXT MAP FOR PRODUCT_FEATURE_TYPE_ID and DESCRIPTION(EITHER FROM PRODUCT_FEATURE_GROUP OR PRODUCT_FEATURE_TYPE)
	Map productFeatureTypesMap = FastMap.newInstance();
	productFeatureTypesList = delegator.findList("ProductFeatureType", null, null, null, null, true);

	//get the whole list of ProductFeatureGroup and ProductFeatureGroupAndAppl
	productFeatureGroupList = delegator.findList("ProductFeatureGroup", null, null, null, null, true);
	productFeatureGroupAndApplList = delegator.findList("ProductFeatureGroupAndAppl", null, null, null, null, true);
	productFeatureGroupAndApplList = EntityUtil.filterByDate(productFeatureGroupAndApplList);

	if(UtilValidate.isNotEmpty(productFeatureTypesList))
	{
		for (GenericValue productFeatureType : productFeatureTypesList)
		{
			//filter the ProductFeatureGroupAndAppl list based on productFeatureTypeId to get the ProductFeatureGroupId
			productFeatureGroupAndAppls = EntityUtil.filterByAnd(productFeatureGroupAndApplList, UtilMisc.toMap("productFeatureTypeId", productFeatureType.productFeatureTypeId));
			description = "";
			if(UtilValidate.isNotEmpty(productFeatureGroupAndAppls))
			{
				productFeatureGroupAndAppl = EntityUtil.getFirst(productFeatureGroupAndAppls);
				productFeatureGroups = EntityUtil.filterByAnd(productFeatureGroupList, UtilMisc.toMap("productFeatureGroupId", productFeatureGroupAndAppl.productFeatureGroupId));
				productFeatureGroup = EntityUtil.getFirst(productFeatureGroups);
				description = productFeatureGroup.description;
			}
			else
			{
				description = productFeatureType.description;
			}
			productFeatureTypesMap.put(productFeatureType.productFeatureTypeId,description);
		}
		
	}
	
	//product features : STANDARD FEATURES 
	//Issue 38934, 38916 - Check for duplicate feature descriptions
	productFeatureAndAppls = FastList.newInstance();
	Map standardFeatureExistsMap = FastMap.newInstance();
	standardFeatures = delegator.findByAndCache("ProductFeatureAndAppl", UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "STANDARD_FEATURE"), UtilMisc.toList("sequenceNum"));
	standardFeatures = EntityUtil.filterByDate(standardFeatures,true);
	standardFeatures = EntityUtil.orderBy(standardFeatures,UtilMisc.toList('sequenceNum'));

	for (GenericValue standardFeature : standardFeatures)
	{
	    String featureDescription = standardFeature.description;
	    if (UtilValidate.isNotEmpty(featureDescription)) 
	    {
	    	featureDescription = featureDescription.toUpperCase();
	        if (!standardFeatureExistsMap.containsKey(featureDescription))
	        {
	        	productFeatureAndAppls.add(standardFeature);
	        	standardFeatureExistsMap.put(featureDescription,featureDescription);
	        }
	    }
	}

	productFriendlyUrl = SeoUrlHelper.makeSeoFriendlyUrl(request,'eCommerceProductDetail?productId='+urlProductId+'&productCategoryId='+productCategoryId+'');
	
	//order item attributes
	orderItemAttributes = orderItem.getRelated("OrderItemAttribute");
	recurrenceFreq = "";
	if(UtilValidate.isNotEmpty(orderItemAttributes))
	{
		recurrenceFreqs = EntityUtil.filterByAnd(orderItemAttributes, UtilMisc.toMap("attrName", "RECURRENCE_FREQ"));
		if(UtilValidate.isNotEmpty(recurrenceFreqs))
		{
			gvRecurrenceFreq = EntityUtil.getFirst(recurrenceFreqs);
			recurrenceFreq = gvRecurrenceFreq.attrValue;
		}
	}

	context.roleTypeId = roleTypeId;
	context.productImageUrl = productImageUrl;
	context.productImageAltUrl = productImageAltUrl;
	context.IMG_SIZE_CART_H = Util.getProductStoreParm(request,"IMG_SIZE_CART_H");
	context.IMG_SIZE_CART_W = Util.getProductStoreParm(request,"IMG_SIZE_CART_W");
	context.productFriendlyUrl = productFriendlyUrl;
	context.urlProductId = urlProductId;
	context.productId = productId;
	context.productName = productName;
	context.wrappedProductName = StringUtil.wrapString(productName);
	context.productFeatureAndAppls = productFeatureAndAppls;
	context.productFeatureTypesMap = productFeatureTypesMap;
	context.displayPrice = displayPrice;
	context.offerPrice = offerPrice;
	context.recurrencePrice = recurrencePrice;
	context.recurrenceItem = recurrenceItem;
	context.recurrenceSavePercent = recurrenceSavePercent;
	context.currencyUom = currencyUom;
	context.quantity = orderItem.quantity;
	context.itemSubTotal = itemSubTotal;
	context.cartItemAdjustment = cartItemAdjustment;
	context.stockInfo = stockInfo;
	context.inStock = inStock;
	context.lineIndex = lineIndex;
	context.recurrenceFreq = recurrenceFreq;
	
	
}

















