package common;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo.CartShipItemInfo;
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import javolution.util.FastList;
import org.ofbiz.entity.GenericValue;
import java.math.BigDecimal;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilNumber;

billingPersonFirstName = "";
billingPersonLastName = "";
chosenShippingMethod = "";
shippingInstructions = "";
storeId = "";
offerPriceVisible = "";
deliveryOption = "";
appliedPromoList = FastList.newInstance();
appliedLoyaltyPointsList = FastList.newInstance();

shoppingCart = session.getAttribute("shoppingCart");
deliveryOption="";
party = ""
if (UtilValidate.isNotEmpty(userLogin))
{
	party = userLogin.getRelatedOneCache("Party");
}
	
if (UtilValidate.isNotEmpty(party))
{
	partyId = party.partyId;
	person = party.getRelatedOneCache("Person");
}

if (UtilValidate.isNotEmpty(shoppingCart))
{
	// retrieve the product store id from the cart
	productStoreId = shoppingCart.getProductStoreId();
	
	// Billing
	if(UtilValidate.isNotEmpty(person))
	{
		billingPersonFirstName = person.firstName?person.firstName:"";
		billingPersonLastName = person.lastName?person.lastName:"";
	}
	
	billingAddress = shoppingCart.getBillingAddress();
	billingContactMechId = "";
	if (UtilValidate.isNotEmpty(billingAddress))
	{
		billingAddress = billingAddress;
		billingContactMechId = billingAddress.contactMechId;
	}
	else
	{
		billingAddressContactMechId =shoppingCart.getContactMech("BILLING_LOCATION");
		if (UtilValidate.isNotEmpty(billingAddressContactMechId))
		{
		  billingAddress = delegator.findOne("PostalAddress", [contactMechId :billingAddressContactMechId], true);
		  if (UtilValidate.isNotEmpty(billingAddress))
		  {
			billingAddress = billingAddress;
			billingContactMechId = billingAddress.contactMechId;
		  }
		
		}
		else
		{
		  billingContactMechAddressList = ContactHelper.getContactMech(party, "BILLING_LOCATION", "POSTAL_ADDRESS", false);
		  billingContactMechAddress = EntityUtil.getFirst(billingContactMechAddressList);
		  if(UtilValidate.isNotEmpty(billingContactMechAddress))
		  {
			  billingAddress=billingContactMechAddress.getRelatedOneCache("PostalAddress");
			  billingAddress = billingAddress;
			  billingContactMechId = billingAddress.contactMechId;
		  }
		}
	}
	
	// Shipping
	shippingAddress = shoppingCart.getShippingAddress();
	
	// Selected Shipping Method
	chosenShippingMethodDescription = "";
	if (shoppingCart.getShipmentMethodTypeId() && shoppingCart.getCarrierPartyId())
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
	shippingInstructions = shoppingCart.getShippingInstructions();
	
	//set store pickup to Y if a store location is set
	isStorePickUp = "N";
	if (UtilValidate.isNotEmpty(shoppingCart))
	{
		storeId = shoppingCart.getOrderAttribute("STORE_LOCATION");
		if (UtilValidate.isNotEmpty(storeId))
		{
			isStorePickUp = "Y"
		}
	}
	
	deliveryOption = shoppingCart.getOrderAttribute("DELIVERY_OPTION");
	
	//show offerPrice
	if(UtilValidate.isNotEmpty(shoppingCart))
	{
		shoppingCartItems = shoppingCart.items();
		if(UtilValidate.isNotEmpty(shoppingCartItems))
		{
			for (ShoppingCartItem shoppingCartItem : shoppingCartItems)
			{
				cartItemAdjustment = shoppingCartItem.getOtherAdjustments();
				if(cartItemAdjustment < 0)
				{
					offerPriceVisible= "Y";
					break;
				}
			}
		}
	}
	
	//get Adjustment Info
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
}

// Credit Card Info
creditCardTypes = delegator.findByAndCache("Enumeration", [enumTypeId : "CREDIT_CARD_TYPE"], ["sequenceId"]);
creditCardTypesMap = [:];
for (GenericValue creditCardType :  creditCardTypes)
{
    creditCardTypesMap[creditCardType.enumCode] = creditCardType.description;
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


context.appliedPromoList = appliedPromoList;
context.appliedLoyaltyPointsList = appliedLoyaltyPointsList;

context.offerPriceVisible = offerPriceVisible;

context.productFeatureTypesMap = productFeatureTypesMap;
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
context.shoppingCart  = shoppingCart;
context.productStoreId = productStoreId;

context.billingPersonFirstName = billingPersonFirstName;
context.billingPersonLastName = billingPersonLastName;
context.billingAddress = billingAddress;
context.billingContactMechId = billingContactMechId;

context.shippingAddress = shippingAddress;
context.creditCardTypesMap = creditCardTypesMap;
context.shoppingCartStoreId = storeId;
context.isStorePickUp = isStorePickUp;
context.deliveryOption = deliveryOption;