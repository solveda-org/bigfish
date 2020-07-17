import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.entity.util.EntityUtil;
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
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;

googleTransMap = [:];
transItemList = [];
    if (UtilValidate.isNotEmpty(context.orderHeader)) 
    {
        orderId = orderHeader.orderId;
        googleTransMap.orderId = orderId;
        googleTransMap.storeName = productStore.storeName;
        orderReadHelper = OrderReadHelper.getHelper(orderHeader);
        orderItems = orderReadHelper.getOrderItems();
        orderAdjustments = orderReadHelper.getAdjustments();
        orderSubTotal = orderReadHelper.getOrderItemsSubTotal();
        otherAdjAmount = orderReadHelper.calcOrderAdjustments(orderAdjustments, orderSubTotal, true, false, false);
        shippingAmount = orderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
        googleTransMap.shippingAmount = shippingAmount.add(orderReadHelper.calcOrderAdjustments(orderAdjustments, orderSubTotal, false, false, true));
        googleTransMap.taxAmount = orderReadHelper.getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal;
        googleTransMap.grandTotal = orderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);
        googleTransMap.currencyUom = orderReadHelper.getCurrency();
        shippingLocations = orderReadHelper.getShippingLocations();
        productPromoUse =orderReadHelper.getProductPromoUse();
        if (UtilValidate.isNotEmpty(productPromoUse)) 
        {
            productPromo = EntityUtil.getFirst(productPromoUse);
            googleTransMap.productPromoCode = productPromo.productPromoCodeId;
        }
         
        if (UtilValidate.isNotEmpty(shippingLocations)) 
        {
            shipLocal = EntityUtil.getFirst(shippingLocations);
            googleTransMap.shipCity = shipLocal.city;
            googleTransMap.shipState = shipLocal.stateProvinceGeoId;
            googleTransMap.shipCountry = shipLocal.countryGeoId;
        
        }
        if (orderItems)
        {
            saleCategoryParts=new StringBuffer();
            orderItems.each { orderItem ->
                itemProduct = orderItem.getRelatedOneCache("Product");
                productCategoryId = "";
                categoryName = "";
                transItemMap = [:];
                categoryLookupProduct = itemProduct;
                virtualProductId = ProductWorker.getVariantVirtualId(categoryLookupProduct);
                if (UtilValidate.isNotEmpty(virtualProductId)) 
                {
                    categoryLookupProduct = delegator.findOne("Product", [productId : virtualProductId], true);
                }
                
                if (UtilValidate.isNotEmpty(categoryLookupProduct))
                {
        	        productCategoryMemberList = itemProduct.getRelatedCache("ProductCategoryMember");
                    productCategoryMemberList = EntityUtil.filterByDate(productCategoryMemberList,true);
            	    productCategoryMemberList = EntityUtil.orderBy(productCategoryMemberList,UtilMisc.toList("sequenceNum"));
                    if (UtilValidate.isNotEmpty(productCategoryMemberList))
                    {
        	            productCategoryMember = EntityUtil.getFirst(productCategoryMemberList);
                        productCategory = productCategoryMember.getRelatedOneCache("ProductCategory");
                        categoryName = CategoryContentWrapper.getProductCategoryContentAsText(productCategory, "CATEGORY_NAME", locale, dispatcher);
                        transItemMap.categoryName;
                    }
                }
                transItemMap.productId = categoryLookupProduct.productId;
                transItemMap.skuCode = itemProduct.internalName;
                transItemMap.itemDescription = StringUtil.wrapString(orderItem.itemDescription);
                transItemMap.unitPrice = orderItem.unitPrice;
                transItemMap.quantity = orderItem.quantity;
                transItemMap.orderId = orderItem.orderId;
                if (UtilValidate.isNotEmpty(categoryName))
                {
                    saleCategoryParts.append(categoryName +":" + orderItem.unitPrice + "|");
                }
                     
                transItemList.add(transItemMap);
            }
            if (UtilValidate.isNotEmpty(saleCategoryParts))
            {
                saleCategoryParts.setLength(saleCategoryParts.length() -1);
                googleTransMap.saleCategoryParts = saleCategoryParts.toString();
                
            }
            
        }
    }
context.googleTransMap = googleTransMap;
context.transItemList = transItemList;
