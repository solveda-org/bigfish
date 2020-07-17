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
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import com.osafe.util.Util;
import org.ofbiz.order.shoppingcart.shipping.ShippingEvents;
import org.ofbiz.order.shoppingcart.CheckOutEvents;

// Get the Product Store
productStore = ProductStoreWorker.getProductStore(request);
context.productStore = productStore;
productStoreId=productStore.getString("productStoreId");

//Get logged in User
userLogin = session.getAttribute("userLogin");

// Get the Cart
shoppingCart = ShoppingCartEvents.getCartObject(request);

//Get currency
CURRENCY_UOM_DEFAULT = Util.getProductStoreParm(request,"CURRENCY_UOM_DEFAULT");
currencyUom = CURRENCY_UOM_DEFAULT;
if(UtilValidate.isEmpty(currencyUom)){
	currencyUom = shoppingCart.getCurrency();
}

//Get size (Number of unique products)
shoppingCartSize = shoppingCart?.size() ?: 0;

//Get the Total Number of Items in the Cart
shoppingCartTotalQuantity = shoppingCart.getTotalQuantity();

//Get the Sub Total
shoppingCartSubTotal = shoppingCart.getSubTotal();

//Get Cart Shipment Method (If empty then take the default in System Parameters)
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
		   Debug.logError(e, e.toString(), "lightCart.groovy");
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
	Debug.logError(e, e.toString(), "lightCart.groovy");
}

//Get total Shipping Amount
orderShippingTotal = shoppingCart.getTotalShipping();

//Get the Tax Total
orderTaxTotal = shoppingCart.getTotalSalesTax();

//Get the order Total
orderGrandTotal = shoppingCart.getGrandTotal();

//get Promo Text
promoText = "";
promoCodeText = "";
adjustmentTypeDesc = "";
cartAdjustment = "";
if((UtilValidate.isNotEmpty(shoppingCart.getAdjustments())) && (shoppingCart.getAdjustments().size() > 0))
{
	adjustments = shoppingCart.getAdjustments();
	for (GenericValue cartAdjustment : adjustments)
	{
		promoCodeText = "";
		adjustmentType = cartAdjustment.getRelatedOneCache("OrderAdjustmentType");
		adjustmentTypeDesc = adjustmentType.get("description",locale);
		productPromo = cartAdjustment.getRelatedOneCache("ProductPromo");
		if(UtilValidate.isNotEmpty(productPromo))
		{
			promoText = productPromo.promoText;
			productPromoCode = productPromo.getRelatedCache("ProductPromoCode");
			if(UtilValidate.isNotEmpty(productPromoCode))
			{
				promoCodesEntered = shoppingCart.getProductPromoCodesEntered();
				if(UtilValidate.isNotEmpty(promoCodesEntered))
				{
					for (GenericValue promoCodeEntered : promoCodesEntered)
					{
						if(UtilValidate.isNotEmpty(promoCodeEntered))
						{
							for (GenericValue promoCode : productPromoCode)
							{
								promoCodeEnteredId = promoCodeEntered;
								promoCodeId = promoCode.productPromoCodeId;
								if(UtilValidate.isNotEmpty(promoCodeEnteredId))
								{
									if(promoCodeId == promoCodeEnteredId)
									{
										promoCodeText = promoCode.productPromoCodeId;
									}
								}
							}
						}
					}
					
				}
			}
		}
	}
}

context.shoppingCart = shoppingCart;
//Get currency
context.currencyUom = currencyUom;
//Number of Unique products
context.shoppingCartSize = shoppingCartSize;
//Cart Total Quantity
context.shoppingCartTotalQuantity = shoppingCartTotalQuantity;
//Sub Total
context.shoppingCartSubTotal = shoppingCartSubTotal;
//Shipping Method
if(UtilValidate.isNotEmpty(chosenShippingMethod))
{
	context.chosenShippingMethod = chosenShippingMethod;
}
if(UtilValidate.isNotEmpty(chosenShippingMethodDescription))
{
	context.chosenShippingMethodDescription = chosenShippingMethodDescription;
}
//Shipping Total
context.orderShippingTotal = orderShippingTotal;
//Tax total
context.orderTaxTotal = orderTaxTotal;
//Total
context.orderGrandTotal = orderGrandTotal;
//Promo
context.promoText = promoText;
context.promoCodeText = promoCodeText;
context.adjustmentTypeDesc = adjustmentTypeDesc;
context.cartAdjustment = cartAdjustment;

