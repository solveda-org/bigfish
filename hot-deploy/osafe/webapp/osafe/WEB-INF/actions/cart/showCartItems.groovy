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

import java.util.Iterator;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import com.osafe.util.Util;

import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo.CartShipItemInfo;
import org.ofbiz.order.shoppingcart.shipping.ShippingEvents;
import org.ofbiz.product.product.ProductWorker;
import com.osafe.control.SeoUrlHelper;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import javolution.util.FastList;

import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.shipping.ShippingEstimateWrapper;
import java.math.BigDecimal;
import org.ofbiz.order.order.*;
import org.ofbiz.base.util.UtilNumber;


// Get the Product Store
productStore = ProductStoreWorker.getProductStore(request);
context.productStore = productStore;
productStoreId=productStore.getString("productStoreId");

//Get logged in User
userLogin = session.getAttribute("userLogin");

shoppingCart = ShoppingCartEvents.getCartObject(request);

//Get currency
CURRENCY_UOM_DEFAULT = Util.getProductStoreParm(request,"CURRENCY_UOM_DEFAULT");
currencyUom = CURRENCY_UOM_DEFAULT;

checkoutGiftMessage = Util.isProductStoreParmTrue(request,"CHECKOUT_GIFT_MESSAGE");
int totalQuantityWithGiftMess = 0;
int totalQuantityAllowGiftMess = 0;
offerPriceVisible = "";

// check if a parameter is passed
product = null;
if (UtilValidate.isNotEmpty(parameters.add_product_id))
{
	add_product_id = parameters.add_product_id;
	product = delegator.findByPrimaryKeyCache("Product", [productId : add_product_id]);
}

if(UtilValidate.isNotEmpty(shoppingCart))
{
	if(UtilValidate.isEmpty(currencyUom))
	{
		currencyUom = shoppingCart.getCurrency();
	}
	shoppingCartSize = shoppingCart.getTotalQuantity();
	shoppingCartItems = shoppingCart.items();
	if(UtilValidate.isNotEmpty(shoppingCartItems))
	{
		for (ShoppingCartItem shoppingCartItem : shoppingCartItems)
		{
			Map cartAttrMap = shoppingCartItem.getOrderItemAttributes();
			quantity = shoppingCartItem.getQuantity();
			cartItemAdjustment = shoppingCartItem.getOtherAdjustments();
			int counter = 1;
			if(cartItemAdjustment < 0)
			{
				offerPriceVisible= "Y";
			}

			giftMessageAllowed = "N";
			pdpGiftMessageAttributeValue = "";
			product = shoppingCartItem.getProduct();
			if(UtilValidate.isNotEmpty(product))
			{
				scItemProductId = shoppingCartItem.getProductId();
				List<GenericValue> productAttributes = FastList.newInstance();
				productAttributes = product.getRelatedCache("ProductAttribute");
				if(UtilValidate.isNotEmpty(productAttributes))
				{
					List<GenericValue> pdpGiftMessageAttributes = EntityUtil.filterByAnd(productAttributes, UtilMisc.toMap("productId",scItemProductId,"attrName","CHECKOUT_GIFT_MESSAGE"));
					pdpGiftMessageAttribute = EntityUtil.getFirst(pdpGiftMessageAttributes);
					if(UtilValidate.isNotEmpty(pdpGiftMessageAttribute))
					{
						pdpGiftMessageAttributeValue = pdpGiftMessageAttribute.attrValue;
					}
				}
			}

			//if sys param is false then do not show gift message link
			if((UtilValidate.isNotEmpty(checkoutGiftMessage) && checkoutGiftMessage == true) && (UtilValidate.isNotEmpty(pdpGiftMessageAttributeValue) && !("FALSE".equals(pdpGiftMessageAttributeValue))))
			{
				totalQuantityAllowGiftMess = totalQuantityAllowGiftMess + quantity;
				giftMessageAllowed = "Y";
			}
			else if((UtilValidate.isNotEmpty(checkoutGiftMessage) && checkoutGiftMessage == true) && UtilValidate.isEmpty(pdpGiftMessageAttributeValue))
			{
				totalQuantityAllowGiftMess = totalQuantityAllowGiftMess + quantity;
				giftMessageAllowed = "Y";
			}
			else if(UtilValidate.isNotEmpty(pdpGiftMessageAttributeValue) && ("TRUE".equals(pdpGiftMessageAttributeValue)))
			{
				totalQuantityAllowGiftMess = totalQuantityAllowGiftMess + quantity;
				giftMessageAllowed = "Y";
			}

			if("Y".equalsIgnoreCase(giftMessageAllowed))
			{
				if(UtilValidate.isNotEmpty(cartAttrMap))
				{

					for (Map.Entry itemAttr : cartAttrMap.entrySet())
					{
						sAttrName = (String)itemAttr.getKey();
						if (sAttrName.startsWith("GIFT_MSG_TEXT_"))
						{
							totalQuantityWithGiftMess = totalQuantityWithGiftMess + 1;
						}

					}

				}
			}
		}
	}
	shippingApplies = shoppingCart.shippingApplies();
	shippingContactMechId = shoppingCart.getShippingContactMechId();
	//if shipping Address not set on cart And User Login Exists than set Logged user First Shipping Address.
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
					try
					{

						//Create New Contact Mech for the cart
						GenericValue contactMech = delegator.makeValue("ContactMech");
						String contactMechId = delegator.getNextSeqId("ContactMech");
						contactMech.set("contactMechId",contactMechId);
						contactMech.set("contactMechTypeId","POSTAL_ADDRESS");
						delegator.create(contactMech);

						//Create New Shipping Postal Address for the cart
						address = partyShippingLocation.getRelatedOneCache("PostalAddress");
						GenericValue postalAddress = delegator.makeValue("PostalAddress");
						postalAddress.set("contactMechId",contactMechId);
						postalAddress.set("toName",address.toName);
						postalAddress.set("attnName",address.attnName);
						postalAddress.set("address1",address.address1);
						postalAddress.set("address2",address.address2);
						postalAddress.set("address3",address.address3);
						postalAddress.set("directions",address.directions);
						postalAddress.set("city",address.city);
						postalAddress.set("postalCode",address.postalCode);
						postalAddress.set("postalCodeExt",address.postalCodeExt);
						postalAddress.set("countryGeoId",address.countryGeoId);
						postalAddress.set("stateProvinceGeoId",address.stateProvinceGeoId);
						postalAddress.set("countyGeoId",address.countyGeoId);
						postalAddress.set("postalCodeGeoId",address.postalCodeGeoId);
						postalAddress.set("geoPointId",address.geoPointId);
						delegator.create(postalAddress);

						//Set the new Contsact Mech to the cart
						shoppingCart.setShippingContactMechId(contactMechId);
					}
					catch(Exception e)
					{
						Debug.logError(e, e.toString(), "showCartItems.groovy");
					}
				}
			}
		}
	}

	//Get Cart Shipment Method: Check for Default Shipping Method in System Parameters
	shippingEstWpr = new ShippingEstimateWrapper(dispatcher, shoppingCart, 0);
	carrierShipmentMethodList = shippingEstWpr.getShippingMethods();

	shipmentMethodTypeId= shoppingCart.getShipmentMethodTypeId();
	carrierPartyId= shoppingCart.getCarrierPartyId();

	shipMethAvailable = "N";
	//try existing
	if(UtilValidate.isNotEmpty(carrierShipmentMethodList) && UtilValidate.isNotEmpty(shipmentMethodTypeId) && UtilValidate.isNotEmpty(carrierPartyId))
	{
		if("NO_SHIPPING".equalsIgnoreCase(shipmentMethodTypeId) && "_NA_".equalsIgnoreCase(carrierPartyId))
		{
			//do nothing because shipmentMethodType and carrier are already set on cart
			shipMethAvailable = "Y";
		}
		else
		{
			//check if the current selected shipping option is available
			for(GenericValue carrierShipmentMethod : carrierShipmentMethodList)
			{
				if(shipmentMethodTypeId.equalsIgnoreCase(carrierShipmentMethod.getString("shipmentMethodTypeId")))
				{
					if(carrierPartyId.equalsIgnoreCase(carrierShipmentMethod.getString("partyId")))
					{
						//do nothing because shipmentMethodType and carrier are already set on cart
						shipMethAvailable = "Y";
						break;
					}
				}
			}
		}
	}
	//try default
	if("N".equals(shipMethAvailable))
	{
		defaultProductStoreShipMethodId = Util.getProductStoreParm(request, "CHECKOUT_CART_DEFAULT_SHIP_METHOD");
		if(UtilValidate.isNotEmpty(defaultProductStoreShipMethodId))
		{
			try
			{
				productStoreShipEstimate = delegator.findByPrimaryKeyCache("ProductStoreShipmentMethView", [productStoreShipMethId : defaultProductStoreShipMethodId]);
				if (UtilValidate.isNotEmpty(productStoreShipEstimate))
				{
					//set default shipping method
					shoppingCart.setCarrierPartyId(0,productStoreShipEstimate.getString("partyId"));
					shoppingCart.setShipmentMethodTypeId(0,productStoreShipEstimate.getString("shipmentMethodTypeId"));
					shipMethAvailable = "Y";
				}
			}
			catch (Exception e)
			{
				Debug.logError(e, e.toString(), "showCartItems.groovy");
			}
		}
	}
	//if we still do not have a shipping method, then try and select the first from the list of available options
	if(UtilValidate.isNotEmpty(carrierShipmentMethodList) && "N".equals(shipMethAvailable))
	{
		firstShippingOption = carrierShipmentMethodList.first();
		shoppingCart.setCarrierPartyId(0,firstShippingOption.getString("partyId"));
		shoppingCart.setShipmentMethodTypeId(0,firstShippingOption.getString("shipmentMethodTypeId"));
	}



	// Selected Shipping Method
	chosenShippingMethod = "";
	chosenShippingMethodDescription = "";
	if (UtilValidate.isNotEmpty(shoppingCart.getShipmentMethodTypeId()) && UtilValidate.isNotEmpty(shoppingCart.getCarrierPartyId()))
	{
		chosenShippingMethod = shoppingCart.getShipmentMethodTypeId() + '@' + shoppingCart.getCarrierPartyId();
		if (chosenShippingMethod.equals("NO_SHIPPING@_NA_"))
		{
			chosenShippingMethodDescription = uiLabelMap.StorePickupLabel;
		}
		else
		{
			carrier =  delegator.findByPrimaryKeyCache("PartyGroup", UtilMisc.toMap("partyId", shoppingCart.getCarrierPartyId()));
			if(UtilValidate.isNotEmpty(carrier))
			{
				if(UtilValidate.isNotEmpty(carrier.groupName))
				{
					chosenShippingMethodDescription = carrier.groupName + " " + shoppingCart.getShipmentMethodType(0).description;
				}
				else
				{
					chosenShippingMethodDescription = shoppingCart.getCarrierPartyId() + " " + shoppingCart.getShipmentMethodType(0).description;
				}
			}
		}
	}
	shippingInstructions = "";
	shippingInstructions = shoppingCart.getShippingInstructions();
	//Set Cart Totals
	//Adjustments are pulled in the FTL
	try
	{
		ShippingEvents.getShipEstimate(request, response);
		shippingAddress = shoppingCart.getShippingAddress();
		if (chosenShippingMethod.equals("NO_SHIPPING@_NA_") || (UtilValidate.isNotEmpty(shippingAddress) && (UtilValidate.isNotEmpty(shippingAddress.get("countryGeoId")) || UtilValidate.isNotEmpty(shippingAddress.get("stateProvinceGeoId")) || UtilValidate.isNotEmpty(shippingAddress.get("postalCodeGeoId")))))
		{
			com.osafe.events.CheckOutEvents.calculateTax(request, response);
		}
	}
	catch(Exception e)
	{
		Debug.logError(e, e.toString(), "showCartItems.groovy");
	}
	orderShippingTotal = shoppingCart.getTotalShipping();
	orderGrandTotal = shoppingCart.getGrandTotal();

	//get Adjustment Info
	appliedPromoList = FastList.newInstance();
	appliedLoyaltyPointsList = FastList.newInstance();
	if((UtilValidate.isNotEmpty(shoppingCart.getAdjustments())) && (shoppingCart.getAdjustments().size() > 0))
	{
		adjustments = shoppingCart.getAdjustments();
		for (GenericValue cartAdjustment : adjustments)
		{
			promoInfo = FastMap.newInstance();
			promoInfo.put("cartAdjustment", cartAdjustment);
			promoCodeText = "";
			adjustmentType = cartAdjustment.getRelatedOneCache("OrderAdjustmentType");
			adjustmentTypeDesc = adjustmentType.get("description",locale);
			//loyalty points
			if(adjustmentType.orderAdjustmentTypeId.equals("LOYALTY_POINTS"))
			{
				loyaltyPointsInfo = FastMap.newInstance();
				loyaltyPointsInfo.put("cartAdjustment", cartAdjustment);
				loyaltyPointsInfo.put("adjustmentTypeDesc", adjustmentTypeDesc);
				appliedLoyaltyPointsList.add(loyaltyPointsInfo);
			}
			//promo
			productPromo = cartAdjustment.getRelatedOneCache("ProductPromo");
			if(UtilValidate.isNotEmpty(productPromo))
			{
				promoInfo.put("adjustmentTypeDesc", adjustmentTypeDesc);
				promoText = productPromo.promoText;
				promoInfo.put("promoText", promoText);
				productPromoCode = productPromo.getRelatedCache("ProductPromoCode");
				if(UtilValidate.isNotEmpty(productPromoCode))
				{
					promoCodesEntered = shoppingCart.getProductPromoCodesEntered();
					if(UtilValidate.isNotEmpty(promoCodesEntered))
					{
						for (String promoCodeEntered : promoCodesEntered)
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
											promoInfo.put("promoCodeText", promoCodeText);
										}
									}
								}
							}
						}

					}
				}
				appliedPromoList.add(promoInfo);
			}
		}
	}

	//Adjustments
	context.appliedPromoList = appliedPromoList;
	context.appliedLoyaltyPointsList = appliedLoyaltyPointsList;
	//Get the Sub Total
	shoppingCartSubTotal = shoppingCart.getSubTotal();
	if(UtilValidate.isNotEmpty(chosenShippingMethod))
	{
		context.chosenShippingMethod = chosenShippingMethod;
	}
	if(UtilValidate.isNotEmpty(chosenShippingMethodDescription))
	{
		context.chosenShippingMethodDescription = chosenShippingMethodDescription;
	}
	if(UtilValidate.isNotEmpty(shippingInstructions))
	{
		context.shippingInstructions = shippingInstructions;
	}
	context.shoppingCart = shoppingCart;
	context.shoppingCartSize = shoppingCartSize;
	context.shoppingCartTotalQuantity = shoppingCartSize;
	context.orderShippingTotal = orderShippingTotal;
	context.orderGrandTotal = orderGrandTotal;

	//Sub Total
	context.cartSubTotal = shoppingCartSubTotal;
	context.shippingApplies = shippingApplies;
	context.offerPriceVisible = offerPriceVisible;
	context.totalQuantityWithGiftMess = totalQuantityWithGiftMess;
	context.totalQuantityAllowGiftMess = totalQuantityAllowGiftMess;
}



// set previos continue button url
prevButtonUrl ="";
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

context.prevButtonUrl = prevButtonUrl;
context.productFeatureTypesMap = productFeatureTypesMap;
if(UtilValidate.isNotEmpty(product))
{
	context.product = product;
}

//Get currency
context.currencyUom = currencyUom;

