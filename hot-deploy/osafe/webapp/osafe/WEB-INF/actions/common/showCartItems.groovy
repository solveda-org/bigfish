/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import com.osafe.util.Util;
import org.ofbiz.order.shoppingcart.shipping.ShippingEvents;
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import org.ofbiz.product.product.ProductWorker;
import com.osafe.services.CatalogUrlServlet;
import javolution.util.FastMap;

// Get the Product Store
productStore = ProductStoreWorker.getProductStore(request);
context.productStore = productStore;
productStoreId=productStore.getString("productStoreId");

//Get logged in User
userLogin = session.getAttribute("userLogin");

// Get the Cart and Prepare Size
shoppingCart = ShoppingCartEvents.getCartObject(request);
shoppingCartSize = shoppingCart?.size() ?: 0;

// check if a parameter is passed
product = null;
if (UtilValidate.isNotEmpty(parameters.add_product_id)) 
{ 
    add_product_id = parameters.add_product_id;
    product = delegator.findByPrimaryKeyCache("Product", [productId : add_product_id]);
}

//Get Cart Shipment Method: Check for Default Shipping Method in System Parameters
shipmentMethodTypeId= shoppingCart.getShipmentMethodTypeId();
if(UtilValidate.isEmpty(shipmentMethodTypeId))
{
   defaultProductStoreShipMethodId = Util.getProductStoreParm(request, "CHECKOUT_CART_DEFAULT_SHIP_METHOD");
   if(UtilValidate.isNotEmpty(defaultProductStoreShipMethodId))
   {
       try 
       {
           productStoreShipEstimate = delegator.findByPrimaryKeyCache("ProductStoreShipmentMethView", [productStoreShipMethId : defaultProductStoreShipMethodId]);
           if (UtilValidate.isNotEmpty(productStoreShipEstimate)) 
           {
               //Find Shipment Cost Estimate
               shipmentCostEstimates = delegator.findByAndCache("ShipmentCostEstimate", UtilMisc.toMap("productStoreId", productStoreId,"productStoreShipMethId", defaultProductStoreShipMethodId), UtilMisc.toList("shipmentCostEstimateId"));         
               if (UtilValidate.isNotEmpty(shipmentCostEstimates)) 
               {
                   shippingContactMechId = shoppingCart.getShippingContactMechId();
                   //if shipping Address not set on cart And User Login Exists than set Logged user Default Shipping Address.
                   if(UtilValidate.isEmpty(shippingContactMechId) && (UtilValidate.isNotEmpty(userLogin) && userLogin.userLoginId != "anonymous"))
                   {
                       partyId = userLogin.partyId;
                       if (UtilValidate.isNotEmpty(partyId)) 
                       {
                           party = delegator.findByPrimaryKeyCache("Party", [partyId : partyId]);
                           if (UtilValidate.isNotEmpty(party))
                           {
                               partyContactMechPurpose = party.getRelatedCache("PartyContactMechPurpose");
                               partyContactMechPurpose = EntityUtil.filterByDate(partyContactMechPurpose,true);
                               partyContactMechPurpose = EntityUtil.orderBy(partyContactMechPurpose,UtilMisc.toList("-fromDate"));
                    
                               partyShippingLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "SHIPPING_LOCATION"));
                               partyShippingLocations = EntityUtil.getRelatedCache("PartyContactMech", partyShippingLocations);
                               partyShippingLocations = EntityUtil.filterByDate(partyShippingLocations,true);
                               partyShippingLocations = EntityUtil.orderBy(partyShippingLocations, UtilMisc.toList("fromDate DESC"));
                               if (UtilValidate.isNotEmpty(partyShippingLocations)) 
                               {
                                   partyShippingLocation = EntityUtil.getFirst(partyShippingLocations);
                                   shoppingCart.setShippingContactMechId(partyShippingLocation.contactMechId);
                               }
                           }
                       }
                   }
                   shipCostEstimate = EntityUtil.getFirst(shipmentCostEstimates);
                   shoppingCart.setCarrierPartyId(0,shipCostEstimate.getString("carrierPartyId"));
                   shoppingCart.setShipmentMethodTypeId(0,shipCostEstimate.getString("shipmentMethodTypeId"));
               }
        
           }
       } 
       catch (Exception e) 
       {
           Debug.logError(e, e.toString(), "showCartItems.groovy");
       }
   }
}

// Selected Shipping Method
chosenShippingMethod = "";
chosenShippingMethodDescription = "";
if (UtilValidate.isNotEmpty(shoppingCart.getShipmentMethodTypeId()) && UtilValidate.isNotEmpty(shoppingCart.getCarrierPartyId())) 
{
    chosenShippingMethod = shoppingCart.getShipmentMethodTypeId() + '@' + shoppingCart.getCarrierPartyId();
    chosenShippingMethodDescription = shoppingCart.getCarrierPartyId() + " " + shoppingCart.getShipmentMethodType(0).description;
}

//Set Cart Totals
//Adjustments are pulled in the FTL
try
{
    ShippingEvents.getShipEstimate(request, response);
    CheckOutEvents.calcTax(request, response);
}
catch(Exception e)
{
    Debug.logError(e, e.toString(), "showCartItems.groovy");
}
orderShippingTotal = shoppingCart.getTotalShipping();
orderTaxTotal = shoppingCart.getTotalSalesTax();
orderGrandTotal = shoppingCart.getGrandTotal();

// set previos continue button url 
continueShoppingLink = Util.getProductStoreParm(request, "CHECKOUT_CONTINUE_SHOPPING_LINK");
if (UtilValidate.isNotEmpty(continueShoppingLink)) 
{
	productId = "";
	productCategoryId = "";
	// check passed parameter first if user comes after add to cart
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
	else if (shoppingCartSize > 0)
	{
	    sci = shoppingCart.findCartItem(0);
		parentProduct = ProductWorker.getParentProduct(sci.getProductId(), delegator);
		cartItemProduct="";
		if (UtilValidate.isNotEmpty(parentProduct))
		{
	        productId = parentProduct.productId;
	        cartItemProduct=parentProduct;
		}
		else
		{
	        productId = sci.getProductId();
	        cartItemProduct= sci.getProduct();
		}
    	if (UtilValidate.isNotEmpty(sci.getProductCategoryId())) 
    	{
    		productCategoryId = sci.getProductCategoryId();
    	}
    	else
    	{
    		if (UtilValidate.isNotEmpty(cartItemProduct))
    		{
    	        productCategoryMemberList = cartItemProduct.getRelatedCache("ProductCategoryMember");
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
	//set url as per productId and product category id
    if (continueShoppingLink.equalsIgnoreCase("PLP") && UtilValidate.isNotEmpty(productCategoryId))
    {
    	prevButtonUrl = CatalogUrlServlet.makeCatalogFriendlyUrl(request,"eCommerceProductList?productCategoryId="+productCategoryId);
    } else if (continueShoppingLink.equalsIgnoreCase("PDP") && UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(productCategoryId)) 
    {
    	prevButtonUrl = CatalogUrlServlet.makeCatalogFriendlyUrl(request,"eCommerceProductDetail?productId="+productId+"&productCategoryId="+productCategoryId);
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

if(UtilValidate.isNotEmpty(chosenShippingMethod))
{
	context.chosenShippingMethod = chosenShippingMethod;
}
if(UtilValidate.isNotEmpty(chosenShippingMethodDescription))
{
	context.chosenShippingMethodDescription = chosenShippingMethodDescription;
}
if(UtilValidate.isNotEmpty(product))
{
	context.product = product;
}
context.prevButtonUrl = prevButtonUrl;
context.shoppingCart = shoppingCart;
context.shoppingCartSize = shoppingCartSize;

context.orderShippingTotal = orderShippingTotal;
context.orderTaxTotal = orderTaxTotal;
context.orderGrandTotal = orderGrandTotal;