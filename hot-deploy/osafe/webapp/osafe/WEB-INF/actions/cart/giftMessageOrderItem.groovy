package common;

import java.math.BigDecimal;
import java.util.List;

import org.ofbiz.base.util.UtilValidate;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import com.osafe.util.Util;
import org.ofbiz.base.util.UtilMisc;
import com.osafe.control.SeoUrlHelper;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.Delegator;
import com.osafe.services.InventoryServices;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.order.order.*;

// Get the Product Store
productStore = ProductStoreWorker.getProductStore(request);
productStoreId=productStore.getString("productStoreId");
currentCatalogId = CatalogWorker.getCurrentCatalogId(request);

//Get currency
CURRENCY_UOM_DEFAULT = Util.getProductStoreParm(request,"CURRENCY_UOM_DEFAULT");
currencyUom = CURRENCY_UOM_DEFAULT;

product = null;
cartLine = null;
urlProductId = "";
productId = "";
productCategoryId = "";
quantity = 0;
price = "";
displayPrice = "";
offerPrice = "";
cartItemAdjustment = null;
Map cartAttrMap = FastMap.newInstance();
recurrencePrice = "";

recurrencePrice = "";
recurrenceItem = "N";
recurrenceSavePercent = null;

int cartLineIndex = 0;
cartLineIndexStr = StringUtils.trimToEmpty(parameters.cartLineIndex);
ShoppingCart shoppingCart = session.getAttribute("shoppingCart");
if(UtilValidate.isNotEmpty(cartLineIndexStr) && UtilValidate.isNotEmpty(shoppingCart))
{
	try
	{
		cartLineIndex = cartLineIndexStr.toInteger();
	}
	catch (NumberFormatException nfe)
	{
		cartLineIndex = 0;
	}
	cartLine = shoppingCart.findCartItem(cartLineIndex);
}
cartLineIndex = shoppingCart.getItemIndex(cartLine);

//Get Order Information
orderId = parameters.orderId;
orderItemSeqId = parameters.orderItemSeqId;
shipGroupSeqId = parameters.shipGroupSeqId;
orderHeader = null;
orderReadHelper = null;
orderItem = null;
orderItemShipGroupAssoc = null;

if(UtilValidate.isNotEmpty(orderId))
{
	orderHeader = delegator.findOne("OrderHeader", [orderId : orderId], true);
	orderReadHelper = new OrderReadHelper(orderHeader);
	orderItems = orderReadHelper.getOrderItems();
	if(UtilValidate.isNotEmpty(orderItemSeqId))
	{
		orderItem = delegator.findOne("OrderItem", [orderId : orderId, orderItemSeqId : orderItemSeqId], true);
		if(UtilValidate.isNotEmpty(shipGroupSeqId))
		{
			orderItemShipGroupAssoc = delegator.findOne("OrderItemShipGroupAssoc", [orderId : orderId, orderItemSeqId : orderItemSeqId, shipGroupSeqId : shipGroupSeqId], true);
		}
	}
	
}

if(UtilValidate.isEmpty(currencyUom))
{
	if(UtilValidate.isNotEmpty(shoppingCart))
	{
		currencyUom = shoppingCart.getCurrency();
	}
	if(UtilValidate.isNotEmpty(orderReadHelper))
	{
		currencyUom = orderReadHelper.getCurrency();
	}
}

if(UtilValidate.isNotEmpty(orderItem))
{
	product = orderItem.getRelatedOneCache("Product");
	urlProductId = product.getString("productId");
	productId = product.getString("productId");
	productCategoryId = orderItem.getString("productCategoryId");
	if(UtilValidate.isNotEmpty(orderItemShipGroupAssoc))
	{
		quantity = orderItemShipGroupAssoc.getBigDecimal("quantity");
	}
	price = orderItem.getBigDecimal("unitPrice");
	displayPrice = orderItem.getBigDecimal("unitListPrice");
	recurrencePrice =  orderItem.unitPrice;
	
	cartItemAdjustment = (orderReadHelper.getOrderItemAdjustmentsTotal(orderItem, true, false, false)).divide(quantity);
	offerPrice = (orderItem.unitPrice).add(cartItemAdjustment);
	
	itemSubTotal = offerPrice.multiply(quantity);
	//orderReadHelper.getOrderItemSubTotal(orderItem);
	
	orderItemAttributes = orderItem.getRelatedCache("OrderItemAttribute");
	if(UtilValidate.isNotEmpty(orderItemAttributes))
    {
        for(GenericValue orderItemAttribute : orderItemAttributes)
    	{
        	cartAttrMap.put(orderItemAttribute.getString("attrName"), orderItemAttribute.getString("attrValue"))
    	}
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
	
}
if(UtilValidate.isNotEmpty(cartLine))
{
	product = cartLine.getProduct();
	urlProductId = cartLine.getProductId();
	productId = cartLine.getProductId();
	productCategoryId = cartLine.getProductCategoryId();
	quantity = cartLine.getQuantity();
	price = cartLine.getBasePrice();
	displayPrice = cartLine.getDisplayPrice();
	recurrencePrice = cartLine.getRecurringDisplayPrice();
	itemSubTotal = cartLine.getDisplayItemSubTotal();
	
	cartItemAdjustment = cartLine.getOtherAdjustments();
	if (UtilValidate.isNotEmpty(cartItemAdjustment) && cartItemAdjustment < 0)
	{
		offerPrice = cartLine.getDisplayPrice() + (cartItemAdjustment/cartLine.getQuantity());
	}
	cartAttrMap = cartLine.getOrderItemAttributes();
	
	//If the item was added to the Shopping Cart as a Recurrence Item
	//The Base and Display Price in the cart is changed to match the Recurrence Price.
	//To display the Recurrence Savings the system is calling calculate product price here to get back the original Default price.
	if (UtilValidate.isNotEmpty(cartLine.getShoppingListId()) && "SLT_AUTO_REODR".equals(cartLine.getShoppingListId()))
	{
		priceContext = [product : product, prodCatalogId : currentCatalogId,
					currencyUomId : currencyUom, autoUserLogin : autoUserLogin];
		priceContext.webSiteId = webSiteId;
		priceContext.productStoreId = productStoreId;
		priceContext.checkIncludeVat = "Y";
		priceContext.agreementId = shoppingCart.getAgreementId();
		priceContext.productPricePurposeId = "PURCHASE";
		priceContext.partyId = shoppingCart.getPartyId();
		productPriceMap = dispatcher.runSync("calculateProductPrice", priceContext);
		productPrice = productPriceMap.price;
		recurrenceSavePercent = (productPrice - recurrencePrice) / productPrice;
		recurrenceItem = "Y";
		
	}
}

virtualProduct="";
if(UtilValidate.isEmpty(productCategoryId))
{
	productCategoryId = product.primaryProductCategoryId;
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

IMG_SIZE_CART_H = Util.getProductStoreParm(request,"IMG_SIZE_CART_H");
IMG_SIZE_CART_W = Util.getProductStoreParm(request,"IMG_SIZE_CART_W");

//stock
stockInfo = "";
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

context.cartAttrMap = cartAttrMap;

context.recurrencePrice = recurrencePrice;
context.recurrenceItem = recurrenceItem;
context.recurrenceSavePercent = recurrenceSavePercent;

//image 
context.productImageUrl = productImageUrl;
context.productImageAltUrl = productImageAltUrl;
context.IMG_SIZE_CART_H = IMG_SIZE_CART_H;
context.IMG_SIZE_CART_W = IMG_SIZE_CART_W;
//friendlyURL
context.productFriendlyUrl = productFriendlyUrl;
context.urlProductId = urlProductId;
//product Name
context.productName = productName;
if(UtilValidate.isNotEmpty(productName))
{
	context.wrappedProductName = StringUtil.wrapString(productName);
}
//product features 
context.productFeatureAndAppls = productFeatureAndAppls;
context.productFeatureTypesMap = productFeatureTypesMap;
context.cartLine = cartLine;
context.cartLineIndex = cartLineIndex;
context.lineIndex = cartLineIndex;
context.displayPrice = displayPrice;
context.offerPrice = offerPrice;
context.currencyUom = currencyUom;
//quantity
context.quantity = quantity;
//item subtotal
context.itemSubTotal = itemSubTotal;
context.cartItemAdjustment = cartItemAdjustment;
//inventory
context.stockInfo = stockInfo;
context.inStock = inStock;
context.productStore = productStore;
context.shoppingCart=shoppingCart;
context.shipGroupSeqId = shipGroupSeqId;


