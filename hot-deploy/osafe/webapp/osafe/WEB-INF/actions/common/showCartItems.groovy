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
if (parameters.add_product_id) 
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
   defaultProductStoreShipMethodId = Util.getProductStoreParm(productStoreId, "CHECKOUT_CART_DEFAULT_SHIP_METHOD");
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
                party = delegator.findByPrimaryKey("Party", [partyId : partyId]);    
                shippingContactMechList = ContactHelper.getContactMech(party, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
                shippingAddressContactMech = EntityUtil.getFirst(shippingContactMechList);
                if (UtilValidate.isNotEmpty(shippingAddressContactMech)) 
                {
                  shoppingCart.setShippingContactMechId(shippingAddressContactMech.contactMechId);
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
if (shoppingCart.getShipmentMethodTypeId() && shoppingCart.getCarrierPartyId()) 
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
