package common;

import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import com.osafe.util.Util;
import org.ofbiz.base.util.UtilMisc;
import com.osafe.services.CatalogUrlServlet;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.Delegator;
import org.ofbiz.order.order.OrderReadHelper;
import com.osafe.services.InventoryServices;


orderItem = request.getAttribute("orderItem");
orderHeader = null;
roleTypeId = "PLACING_CUSTOMER";
context.roleTypeId = roleTypeId;
OrderReadHelper orderReadHelper = null;
if (UtilValidate.isNotEmpty(orderItem))
{
	orderHeader = delegator.findOne("OrderHeader", [orderId : orderItem.orderId], false);
}

product = orderItem.getRelatedOneCache("Product");
urlProductId = product.productId;
productId = product.productId;
productCategoryId = product.primaryProductCategoryId;
virtualProduct="";
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

//Product Name
productName = ProductContentWrapper.getProductContentAsText(product, "PRODUCT_NAME", locale, dispatcher);
if(UtilValidate.isEmpty(productName) && UtilValidate.isNotEmpty(virtualProduct))
{
	productName = ProductContentWrapper.getProductContentAsText(virtualProduct, "PRODUCT_NAME", locale, dispatcher);
}

price = orderItem.unitPrice;
displayPrice = orderItem.unitPrice;
offerPrice = "";
if(UtilValidate.isNotEmpty(orderHeader))
{
	orderReadHelper = new OrderReadHelper(orderHeader);
	if(UtilValidate.isNotEmpty(orderReadHelper))
	{
		cartItemAdjustment = orderReadHelper.getOrderItemAdjustmentsTotal(orderItem);
	}
	
	if(UtilValidate.isNotEmpty(orderItem))
	{
		itemSubTotal = orderReadHelper.getOrderItemSubTotal(orderItem,orderReadHelper.getAdjustments());
	}
}


if (UtilValidate.isNotEmpty(cartItemAdjustment) && cartItemAdjustment < 0)
{
	offerPrice = orderItem.unitPrice + (cartItemAdjustment/orderItem.quantity);
}

if ("Y".equals(orderItem.isPromo))
{
	price = orderItem.unitPrice;
}
else 
{ 
	if (orderItem.selectedAmount > 0)
	{
		price =  orderItem.unitPrice / orderItem.selectedAmount;
	}
	else
	{
		price =  orderItem.unitPrice;
	}
}

//Get currency
CURRENCY_UOM_DEFAULT = Util.getProductStoreParm(request,"CURRENCY_UOM_DEFAULT");
if(UtilValidate.isNotEmpty(orderReadHelper))
{
	currencyUom = orderReadHelper.getCurrency();
}
if(UtilValidate.isEmpty(currencyUom))
{
	currencyUom = CURRENCY_UOM_DEFAULT;
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

//product features
productFeatureAndAppls = product.getRelatedCache("ProductFeatureAndAppl");
productFeatureAndAppls = EntityUtil.filterByDate(productFeatureAndAppls,true);
productFeatureAndAppls = EntityUtil.orderBy(productFeatureAndAppls,UtilMisc.toList('sequenceNum'));

productFriendlyUrl = CatalogUrlServlet.makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId='+urlProductId+'&productCategoryId='+productCategoryId+'');

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

//image 
context.productImageUrl = productImageUrl;
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
context.displayPrice = displayPrice;
context.offerPrice = offerPrice;
context.currencyUom = currencyUom;
//quantity
context.quantity = orderItem.quantity;
//item subtotal
context.itemSubTotal = itemSubTotal;

context.cartItemAdjustment = cartItemAdjustment;


//inventory
context.stockInfo = stockInfo;
context.inStock = inStock;


