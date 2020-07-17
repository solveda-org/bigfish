package common;

import org.ofbiz.base.util.UtilValidate;
import java.text.NumberFormat;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import javolution.util.FastList;
import com.osafe.util.Util;
import com.osafe.services.OsafeManageXml;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilMisc;
import com.osafe.services.InventoryServices;
import org.ofbiz.party.content.PartyContentWrapper;
import com.osafe.services.CatalogUrlServlet;
import org.apache.commons.lang.StringEscapeUtils;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.Debug;
import java.util.LinkedHashMap;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.condition.EntityCondition;


cartLine = request.getAttribute("cartLine");
//Get currency
CURRENCY_UOM_DEFAULT = Util.getProductStoreParm(request,"CURRENCY_UOM_DEFAULT");
currencyUom = CURRENCY_UOM_DEFAULT;
if(UtilValidate.isEmpty(currencyUom))
{
	currencyUom = shoppingCart.getCurrency();
}

cartLineIndex = shoppingCart.getItemIndex(cartLine);
product = cartLine.getProduct();
urlProductId = cartLine.getProductId();
productCategoryId = cartLine.getProductCategoryId()
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
	virtualProduct = ProductWorker.getParentProduct(cartLine.getProductId(), delegator);
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
productImageUrl = ProductContentWrapper.getProductContentAsText(cartLine.getProduct(), "SMALL_IMAGE_URL", locale, dispatcher);
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
productName = ProductContentWrapper.getProductContentAsText(cartLine.getProduct(), "PRODUCT_NAME", locale, dispatcher);
if(UtilValidate.isEmpty(productName) && UtilValidate.isNotEmpty(virtualProduct))
{
	productName = ProductContentWrapper.getProductContentAsText(virtualProduct, "PRODUCT_NAME", locale, dispatcher);
}

price = cartLine.getBasePrice();
displayPrice = cartLine.getDisplayPrice();
offerPrice = "";
cartItemAdjustment = cartLine.getOtherAdjustments();
if (UtilValidate.isNotEmpty(productName) && cartItemAdjustment < 0)
{
	offerPrice = cartLine.getDisplayPrice() + (cartItemAdjustment/cartLine.getQuantity());
}
if (cartLine.getIsPromo() || (shoppingCart.getOrderType() == "SALES_ORDER" && !security.hasEntityPermission("ORDERMGR", "_SALES_PRICEMOD", session)))
{
	price= cartLine.getDisplayPrice();
}
else 
{ 
	if (cartLine.getSelectedAmount() > 0)
	{
		price = cartLine.getBasePrice() / cartLine.getSelectedAmount();
	}
	else
	{
		price = cartLine.getBasePrice();
	}
}

productFriendlyUrl = CatalogUrlServlet.makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${urlProductId}&productCategoryId=${productCategoryId!""}');

IMG_SIZE_CART_H = Util.getProductStoreParm(request,"IMG_SIZE_CART_H");
IMG_SIZE_CART_W = Util.getProductStoreParm(request,"IMG_SIZE_CART_W");


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
context.cartLine = cartLine;
context.cartLineIndex = cartLineIndex;











/*
cartLine = request.getAttribute("cartLine");
if(UtilValidate.isNotEmpty(cartLine))
	{
context.test11 = "AAAAa";
	}
	else
	{
		context.test11 = "BBBBB";
	}

*/





