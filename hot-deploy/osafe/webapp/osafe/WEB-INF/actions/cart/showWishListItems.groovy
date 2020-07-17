
import javolution.util.FastList;
import com.osafe.events.WishListEvents;
import org.ofbiz.base.util.UtilValidate;
import com.osafe.util.Util;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductWorker;
import com.osafe.control.SeoUrlHelper;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;

wishList = FastList.newInstance();
wishListSize = 0;
wishListId = WishListEvents.getWishListId(request, false);
totalPrice = 0;
if (UtilValidate.isNotEmpty(wishListId)) 
{ 
    wishList = delegator.findByAndCache("ShoppingListItem", [shoppingListId : wishListId]);
	for(GenericValue wishListItem : wishList)
	{
		//add up total price
		productId = wishListItem.productId;
		product = delegator.findOne("Product", UtilMisc.toMap("productId",productId), true);
		productPrice = dispatcher.runSync("calculateProductPrice", UtilMisc.toMap("product", product, "userLogin", userLogin));
		totalPrice = totalPrice + (productPrice.basePrice * wishListItem.quantity);
		//add up total number of items in cart
		wishListSize = wishListSize + wishListItem.quantity;
	}
}

// check if a parameter is passed
product = null;
if (UtilValidate.isNotEmpty(parameters.add_product_id)) 
{ 
    add_product_id = parameters.add_product_id;
    product = delegator.findByPrimaryKeyCache("Product", [productId : add_product_id]);
}

//set previos continue button url 
prevButtonUrl = "main";
continueShoppingLink = Util.getProductStoreParm(request, "CHECKOUT_CONTINUE_SHOPPING_LINK");
if (UtilValidate.isEmpty(continueShoppingLink))
{
	continueShoppingLink = "PLP";
}
if (UtilValidate.isNotEmpty(continueShoppingLink)) 
{
	productId = "";
	productCategoryId = "";
		//set url as per productId and product category id
	 if (continueShoppingLink.equalsIgnoreCase("PLP"))
	 {
		 //retrieve the productCategoryId from the last visited PLP
		 plpProductCategoryId = session.getAttribute("PLP_PRODUCT_CATEGORY_ID");
		 if(UtilValidate.isNotEmpty(plpProductCategoryId))
		 {
			 productCategoryId = plpProductCategoryId;
		 }
		 if (UtilValidate.isNotEmpty(productCategoryId))
		 {
			 prevButtonUrl = SeoUrlHelper.makeSeoFriendlyUrl(request,"eCommerceProductList?productCategoryId="+productCategoryId);
		 }
	 } 
	 else if (continueShoppingLink.equalsIgnoreCase("PDP")) 
	 {
		 //retrieve the product id and productCategoryId from the last visited PDP
		 pdpProductId = session.getAttribute("PDP_PRODUCT_ID");
		 if(UtilValidate.isNotEmpty(pdpProductId))
		 {
			 productId = pdpProductId;
		 }
		 pdpProductCategoryId = session.getAttribute("PDP_PRODUCT_CATEGORY_ID");
		 if(UtilValidate.isNotEmpty(pdpProductCategoryId))
		 {
			 productCategoryId = pdpProductCategoryId;
		 }
		 if (UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(productCategoryId))
		 {
			 prevButtonUrl = SeoUrlHelper.makeSeoFriendlyUrl(request,"eCommerceProductDetail?productId="+productId+"&productCategoryId="+productCategoryId);
		 }
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

context.productFeatureTypesMap = productFeatureTypesMap;

context.prevButtonUrl = prevButtonUrl;
context.product = product;
context.shoppingCartTotalQuantity = wishListSize;
context.shoppingCartSize = wishListSize;
context.wishListSize = wishListSize;
context.wishList = wishList;
context.cartSubTotal = totalPrice;

