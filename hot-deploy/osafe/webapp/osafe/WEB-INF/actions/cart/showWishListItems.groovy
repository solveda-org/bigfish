
import javolution.util.FastList;
import com.osafe.events.WishListEvents;
import org.ofbiz.base.util.UtilValidate;
import com.osafe.util.Util;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductWorker;
import com.osafe.services.CatalogUrlServlet;
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
    wishListSize = wishList.size();
	for(GenericValue wishListItem : wishList)
	{
		productId = wishListItem.productId;
		product = delegator.findOne("Product", UtilMisc.toMap("productId",productId), true);
		productPrice = dispatcher.runSync("calculateProductPrice", UtilMisc.toMap("product", product, "userLogin", userLogin));
		totalPrice = totalPrice + productPrice.basePrice;
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
nextButtonUrl = "";
continueShoppingLink = Util.getProductStoreParm(request, "CHECKOUT_CONTINUE_SHOPPING_LINK");
if (UtilValidate.isEmpty(continueShoppingLink))
{
	continueShoppingLink = "PLP";
}
if (UtilValidate.isNotEmpty(continueShoppingLink)) 
{
	productId = "";
	productCategoryId = "";
	// check passed parameter first if user comes after add to wish list
	if (UtilValidate.isNotEmpty(parameters.product_id)) 
	{
	     productId = parameters.product_id;
	     parentProduct = delegator.findOne("Product",UtilMisc.toMap("productId",productId), true);
	 	 if (UtilValidate.isNotEmpty(parentProduct))
	 	 {
	     	if (UtilValidate.isNotEmpty(parameters.add_category_id)) 
	     	{
	     		productCategoryId = parameters.add_category_id;
	     	}
	     	else
	     	{
	 	        productCategoryMemberList = parentProduct.getRelatedCache("ProductCategoryMember");
	            productCategoryMemberList = EntityUtil.filterByDate(productCategoryMemberList,true);
	     	    productCategoryMemberList = EntityUtil.orderBy(productCategoryMemberList,UtilMisc.toList("sequenceNum"));
	 	        if(UtilValidate.isNotEmpty(productCategoryMemberList))
	 	        {
	 	            productCategoryMember = EntityUtil.getFirst(productCategoryMemberList);
	 	            productCategoryId = productCategoryMember.productCategoryId; 
	 	        }    
	     	}
	 		
	 	 }
	}
	// take 0 index value from shopping cart
	else if (wishListSize > 0)
	{
	    sci = EntityUtil.getFirst(wishList);
		parentProduct = ProductWorker.getParentProduct(sci.productId, delegator);
		cartItemProduct="";
		if (UtilValidate.isNotEmpty(parentProduct))
		{
	        productId = parentProduct.productId;
	        cartItemProduct=parentProduct;
		}
		else
		{
	        productId = sci.productId;
	        cartItemProduct= delegator.findByPrimaryKeyCache("Product", [productId : sci.productId]);
		}

	    productCategoryMemberList = cartItemProduct.getRelatedCache("ProductCategoryMember");
        productCategoryMemberList = EntityUtil.filterByDate(productCategoryMemberList,true);
 	    productCategoryMemberList = EntityUtil.orderBy(productCategoryMemberList,UtilMisc.toList("sequenceNum"));
        if(UtilValidate.isNotEmpty(productCategoryMemberList))
        {
            productCategoryMember = EntityUtil.getFirst(productCategoryMemberList);
            productCategoryId = productCategoryMember.productCategoryId; 
        }
	 }
		//set url as per productId and product category id
	 if (continueShoppingLink.equalsIgnoreCase("PLP") && UtilValidate.isNotEmpty(productCategoryId))
	 {
		 nextButtonUrl = CatalogUrlServlet.makeCatalogFriendlyUrl(request,"eCommerceProductList?productCategoryId="+productCategoryId);
	 } else if (continueShoppingLink.equalsIgnoreCase("PDP") && UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(productCategoryId)) 
	 {
		 nextButtonUrl = CatalogUrlServlet.makeCatalogFriendlyUrl(request,"eCommerceProductDetail?productId="+productId+"&productCategoryId="+productCategoryId);
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

context.nextButtonUrl = nextButtonUrl;
context.product = product;
context.shoppingCartTotalQuantity = wishListSize;
context.wishListSize = wishListSize;
context.wishList = wishList;
context.cartSubTotal = totalPrice;

