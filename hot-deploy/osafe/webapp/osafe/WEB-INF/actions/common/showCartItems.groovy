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
import org.ofbiz.order.shoppingcart.product.ProductDisplayWorker;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import com.osafe.util.Util;
import org.ofbiz.order.shoppingcart.shipping.ShippingEvents;
import org.ofbiz.order.shoppingcart.shipping.ShippingEstimateWrapper;
import org.ofbiz.party.contact.*;
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import org.ofbiz.product.product.ProductWorker;
import com.osafe.services.CatalogUrlServlet;

// Get the Product Store
productStore = ProductStoreWorker.getProductStore(request);
context.productStore = productStore;
productStoreId=productStore.getString("productStoreId");

//Get logged in User
userLogin = session.getAttribute("userLogin");

// Get the Cart and Prepare Size
shoppingCart = ShoppingCartEvents.getCartObject(request);
context.shoppingCartSize = shoppingCart?.size() ?: 0;
context.shoppingCart = shoppingCart;

// check if a parameter is passed
if (UtilValidate.isNotEmpty(parameters.add_product_id)) 
{ 
    add_product_id = parameters.add_product_id;
    product = delegator.findByPrimaryKeyCache("Product", [productId : add_product_id]);
    context.product = product;
}

// Get Catalog path
context.contentPathPrefix = CatalogWorker.getContentPathPrefix(request);


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
     } catch (Exception e) 
       {
         Debug.logError(e, e.toString(), "showCartItems.groovy");
       
       }
   
   }
}

// Selected Shipping Method
if (UtilValidate.isNotEmpty(shoppingCart.getShipmentMethodTypeId()) && UtilValidate.isNotEmpty(shoppingCart.getCarrierPartyId())) 
{
    context.chosenShippingMethod = shoppingCart.getShipmentMethodTypeId() + '@' + shoppingCart.getCarrierPartyId();
    context.chosenShippingMethodDescription = shoppingCart.getCarrierPartyId() + " " + shoppingCart.getShipmentMethodType(0).description;
}

//Set Cart Totals
//Adjustments are pulled in the FTL
try{
    ShippingEvents.getShipEstimate(request, response);
    CheckOutEvents.calcTax(request, response);
}
catch(Exception e){
    Debug.logError(e, e.toString(), "showCartItems.groovy");
}
context.orderShippingTotal = shoppingCart.getTotalShipping();
context.orderTaxTotal = shoppingCart.getTotalSalesTax();
context.orderGrandTotal = shoppingCart.getGrandTotal();

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
        product = delegator.findOne("Product",UtilMisc.toMap("productId",productId), true);
    	if (UtilValidate.isNotEmpty(product))
    	{
        	if (UtilValidate.isNotEmpty(parameters.add_category_id)) 
        	{
        		productCategoryId = parameters.add_category_id;
        	}
        	else
        	{
    	        productCategoryMemberList = product.getRelatedCache("ProductCategoryMember");
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
	else if (context.shoppingCartSize > 0)
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
    	context.prevButtonUrl = CatalogUrlServlet.makeCatalogFriendlyUrl(request,"eCommerceProductList?productCategoryId="+productCategoryId);
    } else if (continueShoppingLink.equalsIgnoreCase("PDP") && UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(productCategoryId)) 
    {
    	context.prevButtonUrl = CatalogUrlServlet.makeCatalogFriendlyUrl(request,"eCommerceProductDetail?productId="+productId+"&productCategoryId="+productCategoryId);
    }
}