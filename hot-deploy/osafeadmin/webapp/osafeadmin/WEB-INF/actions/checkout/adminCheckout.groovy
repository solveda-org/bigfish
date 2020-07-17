package checkout;

import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.order.shoppingcart.shipping.ShippingEstimateWrapper;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo.CartShipItemInfo;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import javolution.util.FastList;
import com.osafe.util.OsafeAdminUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.math.BigDecimal;
import org.ofbiz.order.shoppingcart.shipping.ShippingEvents;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilNumber;

adminContext = session.getAttribute("ADMIN_CONTEXT");

if (UtilValidate.isNotEmpty(adminContext))
{
	partyId=adminContext.CONTEXT_PARTY_ID;
	if (UtilValidate.isNotEmpty(partyId))
	{
		if (UtilValidate.isNotEmpty(partyId))
		{
			context.customerInformationHeading = UtilProperties.getMessage("OSafeAdminUiLabels","CustomerDetailInfoHeading",["partyId" : partyId], locale )
		}
		party = delegator.findByPrimaryKey("Party", [partyId : partyId]);
		if(UtilValidate.isNotEmpty(party))
		{

			//Get PARTY
			context.party=party;
			context.partyId=partyId;

			person = party.getRelated("Person");
	        context.person=person;
	        if (UtilValidate.isNotEmpty(person))
	        {
	            context.billingFirstName = person.firstName?person.firstName:"";
	            context.billingLastName = person.lastName?person.lastName:"";
	        	
	        }
			
			partyName = PartyHelper.getPartyName(party, true);
            context.partyName=partyName;
			
			partyFirstLastName = StringUtil.split(partyName, ", ");
			if (UtilValidate.isNotEmpty(partyFirstLastName))
			{
				context.partyLastName = partyFirstLastName[0];
				context.partyFirstName = partyFirstLastName[1];
			}

			partyRoles = party.getRelated("PartyRole");
	        context.partyRoles=partyRoles;
	        
	        userLogins = party.getRelated("UserLogin");
	        context.userLogins=userLogins;

			//Get PARTY BILLING,SHIPPING,PRIMARY EMAIL, TELEPHONE LOCATIONS
	        partyContactMechPurpose = party.getRelated("PartyContactMechPurpose");
	        partyContactMechPurpose = EntityUtil.filterByDate(partyContactMechPurpose,true);

	        partyBillingLocation = "";
	        partyBillingLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "BILLING_LOCATION"));
	        partyBillingLocations = EntityUtil.getRelated("PartyContactMech", partyBillingLocations);
	        partyBillingLocations = EntityUtil.filterByDate(partyBillingLocations,true);
	        partyBillingLocations = EntityUtil.orderBy(partyBillingLocations, UtilMisc.toList("fromDate DESC"));
	        if (UtilValidate.isNotEmpty(partyBillingLocations)) 
	        {
	        	partyBillingLocation = EntityUtil.getFirst(partyBillingLocations);
	            context.billingContactMechId = partyBillingLocation.contactMechId;
	            billingContactMechList = EntityUtil.getRelated("ContactMech",partyBillingLocations);
	            context.billingContactMechList = billingContactMechList;
	        }

	        partyShippingLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "SHIPPING_LOCATION"));
	        partyShippingLocations = EntityUtil.getRelated("PartyContactMech", partyShippingLocations);
	        partyShippingLocations = EntityUtil.filterByDate(partyShippingLocations,true);
			if (UtilValidate.isNotEmpty(partyBillingLocation))
			{
				partyShippingLocations.add(partyBillingLocation);
			}
	        partyShippingLocations = EntityUtil.orderBy(partyShippingLocations, UtilMisc.toList("fromDate DESC"));
	        if (UtilValidate.isNotEmpty(partyShippingLocations)) 
	        {
	        	partyShippingLocation = EntityUtil.getFirst(partyShippingLocations);
	            context.shippingContactMechId = partyShippingLocation.contactMechId;
	            shippingContactMechList = EntityUtil.getRelated("ContactMech",partyShippingLocations);
	            context.shippingContactMechList = shippingContactMechList;
	        }
	        
			//Get MOVE BILLING ADDRESS TO THE FRONT OF THE SHIPPING LOCATIONS
			if (UtilValidate.isNotEmpty(context.billingContactMechList))
			{
			    billingContactMech = context.billingContactMechList.get(0);

			    // Moving the billing address to the front of the list
			    if(UtilValidate.isNotEmpty(context.shippingContactMechList))
			    {
			        context.shippingContactMechList.remove(billingContactMech);
			        context.shippingContactMechList.add(0,billingContactMech);
			        context.billingContactMechId=billingContactMech.contactMechId;
			    }
			}

	        partyPurposeEmails = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "PRIMARY_EMAIL"));
	        partyPurposeEmails = EntityUtil.getRelated("PartyContactMech", partyPurposeEmails);
	        partyPurposeEmails = EntityUtil.filterByDate(partyPurposeEmails,true);
	        partyPurposeEmails = EntityUtil.orderBy(partyPurposeEmails, UtilMisc.toList("fromDate DESC"));
	        if (UtilValidate.isNotEmpty(partyPurposeEmails)) 
	        {
	        	partyPurposeEmail = EntityUtil.getFirst(partyPurposeEmails);
	            contactMech = partyPurposeEmail.getRelatedOne("ContactMech");
	            context.userEmailContactMech = contactMech;
	            context.userEmailAddress = contactMech.infoString;
	            context.userEmailAllowSolicitation= partyPurposeEmail.allowSolicitation;
	            userEmailContactMechList= EntityUtil.getRelated("ContactMech",partyPurposeEmails);
	            context.userEmailContactMechList = userEmailContactMechList;
	            
	        }
			
	        partyPurposeHomePhones = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "PHONE_HOME"));
            partyPurposeHomePhones = EntityUtil.getRelated("PartyContactMech", partyPurposeHomePhones);
            partyPurposeHomePhones = EntityUtil.filterByDate(partyPurposeHomePhones,true);
            partyPurposeHomePhones = EntityUtil.orderBy(partyPurposeHomePhones, UtilMisc.toList("fromDate DESC"));
            if (UtilValidate.isNotEmpty(partyPurposeHomePhones)) 
            {
            	partyPurposePhone = EntityUtil.getFirst(partyPurposeHomePhones);
            	telecomNumber = partyPurposePhone.getRelatedOne("TelecomNumber");
                context.phoneHomeTelecomNumber =telecomNumber;
                context.phoneHomeAreaCode =telecomNumber.areaCode;
                context.phoneHomeContactNumber =telecomNumber.contactNumber;
                context.partyPurposeHomePhones =partyPurposeHomePhones;
            }
	        
	        partyPurposeWorkPhones = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "PHONE_WORK"));
            partyPurposeWorkPhones = EntityUtil.getRelated("PartyContactMech", partyPurposeWorkPhones);
            partyPurposeWorkPhones = EntityUtil.filterByDate(partyPurposeWorkPhones,true);
            partyPurposeWorkPhones = EntityUtil.orderBy(partyPurposeWorkPhones, UtilMisc.toList("fromDate DESC"));
            if (UtilValidate.isNotEmpty(partyPurposeWorkPhones)) 
            {
            	partyPurposePhone = EntityUtil.getFirst(partyPurposeWorkPhones);
            	telecomNumber = partyPurposePhone.getRelatedOne("TelecomNumber");
                context.phoneWorkTelecomNumber =telecomNumber;
                context.phoneWorkAreaCode =telecomNumber.areaCode;
                context.phoneWorkContactNumber =telecomNumber.contactNumber;
                context.partyPurposeWorkPhones =partyPurposeWorkPhones;
            }
			
	        partyPurposeMobilePhones = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "PHONE_MOBILE"));
            partyPurposeMobilePhones = EntityUtil.getRelated("PartyContactMech", partyPurposeMobilePhones);
            partyPurposeMobilePhones = EntityUtil.filterByDate(partyPurposeMobilePhones,true);
            partyPurposeMobilePhones = EntityUtil.orderBy(partyPurposeMobilePhones, UtilMisc.toList("fromDate DESC"));
            if (UtilValidate.isNotEmpty(partyPurposeMobilePhones)) 
            {
            	partyPurposePhone = EntityUtil.getFirst(partyPurposeMobilePhones);
            	telecomNumber = partyPurposePhone.getRelatedOne("TelecomNumber");
                context.phoneMobileTelecomNumber =telecomNumber;
                context.phoneMobileAreaCode =telecomNumber.areaCode;
                context.phoneMobileContactNumber =telecomNumber.contactNumber;
                context.partyPurposeMobilePhones =partyPurposeMobilePhones;
            }

            //Get PARTY PAYMENT METHODS
	        paymentMethodValueMaps = PaymentWorker.getPartyPaymentMethodValueMaps(delegator, partyId, false);
	        context.paymentMethodValueMaps = paymentMethodValueMaps;

			//Get PARTY ATTRIBUTES
			partyAttributes = party.getRelated("PartyAttribute");
		    if (UtilValidate.isNotEmpty(partyAttributes))
		    {
				//TITLE
			    partyAttrs = EntityUtil.filterByAnd(partyAttributes, UtilMisc.toMap("attrName", "TITLE"));
			    if (UtilValidate.isNotEmpty(partyAttrs))
			    {
			    	context.title = EntityUtil.getFirst(partyAttrs);
			    }

			    
			    //GENDER
			    partyAttrs = EntityUtil.filterByAnd(partyAttributes, UtilMisc.toMap("attrName", "GENDER"));
			    if (UtilValidate.isNotEmpty(partyAttrs))
			    {
			    	context.gender = EntityUtil.getFirst(partyAttrs);
			    }

			    //DOB_MMDD
			    partyAttrs = EntityUtil.filterByAnd(partyAttributes, UtilMisc.toMap("attrName", "DOB_MMDD"));
			    if (UtilValidate.isNotEmpty(partyAttrs))
			    {
			    	context.dob_MMDD = EntityUtil.getFirst(partyAttrs);
			    }

			    //DOB_MMDDYYYY
			    partyAttrs = EntityUtil.filterByAnd(partyAttributes, UtilMisc.toMap("attrName", "DOB_MMDDYYYY"));
			    if (UtilValidate.isNotEmpty(partyAttrs))
			    {
			    	context.dob_MMDDYYYY = EntityUtil.getFirst(partyAttrs);
			    }

			    //DOB_DDMM
			    partyAttrs = EntityUtil.filterByAnd(partyAttributes, UtilMisc.toMap("attrName", "DOB_DDMM"));
			    if (UtilValidate.isNotEmpty(partyAttrs))
			    {
			    	context.dob_DDMM = EntityUtil.getFirst(partyAttrs);
			    }

			    //DOB_DDMMYYYY
			    partyAttrs = EntityUtil.filterByAnd(partyAttributes, UtilMisc.toMap("attrName", "DOB_DDMMYYYY"));
			    if (UtilValidate.isNotEmpty(partyAttrs))
			    {
			    	context.dob_DDMMYYYY = EntityUtil.getFirst(partyAttrs);
			    }		    	
			    //IS_DOWNLOADED
			    partyAttrs = EntityUtil.filterByAnd(partyAttributes, UtilMisc.toMap("attrName", "IS_DOWNLOADED"));
			    if (UtilValidate.isNotEmpty(partyAttrs))
			    {
			    	context.IS_DOWNLOADED = EntityUtil.getFirst(partyAttrs);
			    }
				//PARTY_EMAIL_PREFERENCE
				partyAttrs = EntityUtil.filterByAnd(partyAttributes, UtilMisc.toMap("attrName", "PARTY_EMAIL_PREFERENCE"));
				if (UtilValidate.isNotEmpty(partyAttrs))
				{
					partyAttr = EntityUtil.getFirst(partyAttrs);
					context.PARTY_EMAIL_PREFERENCE = partyAttr.attrValue;
				}
		    }
		    
			//Get CURRENT SHOPPING CART
		    ShoppingCart shopCart = ShoppingCartEvents.getCartObject(request);
			shippingApplies = shopCart.shippingApplies();
		    context.shoppingCartSize = shopCart.size();
		    context.shoppingCart = shopCart;
			context.shippingApplies = shippingApplies;
			shopCart.setOrderPartyId(partyId);
			//Get CART SHIPPING ESTIMATES
			shippingEstWpr = new ShippingEstimateWrapper(dispatcher, shopCart, 0);
			context.shippingEstWpr = shippingEstWpr;
			carrierShipmentMethodList = shippingEstWpr.getShippingMethods();
			
			//CHECK IF SHIPPING ADDRESS IS A PO BOX
    		gvCartShippingAddress = shopCart.getShippingAddress();
			gvCartTotalWeight = shopCart.getTotalWeight();
			if (UtilValidate.isNotEmpty(gvCartShippingAddress))
	        {
	        	if(UtilValidate.isNotEmpty(carrierShipmentMethodList))
				{
					// clone the list for concurrent modification
	        		returnShippingMethods = UtilMisc.makeListWritable(carrierShipmentMethodList);
	        		for (GenericValue method: carrierShipmentMethodList)
					{
						psShipmentMeth = delegator.findByPrimaryKeyCache("ProductStoreShipmentMeth", [productStoreShipMethId : method.productStoreShipMethId]);
						allowPoBoxAddr = psShipmentMeth.getString("allowPoBoxAddr");
						minWeight = psShipmentMeth.getBigDecimal("minWeight");
				        maxWeight = psShipmentMeth.getBigDecimal("maxWeight");
				        isPoBoxAddr = false;
				        if (!UtilValidate.isNotPoBox(gvCartShippingAddress.get("address1")) || !UtilValidate.isNotPoBox(gvCartShippingAddress.get("address2")) || !UtilValidate.isNotPoBox(gvCartShippingAddress.get("address3")) )
						{
							isPoBoxAddr = true;
						}
				        if ((UtilValidate.isNotEmpty(allowPoBoxAddr) && "N".equals(allowPoBoxAddr) && isPoBoxAddr)||(gvCartTotalWeight != 0 && ((UtilValidate.isNotEmpty(maxWeight)&& maxWeight < gvCartTotalWeight) || (UtilValidate.isNotEmpty(minWeight) && gvCartTotalWeight < minWeight ))))
				        {
	                        returnShippingMethods.remove(method);
	                        continue;
	                    }
	                    //Check shipment CUSTOM METHOD
						if(UtilValidate.isNotEmpty(psShipmentMeth))
						{
							shipmentCustomMethodId = psShipmentMeth.getString("shipmentCustomMethodId");
							if(UtilValidate.isNotEmpty(shipmentCustomMethodId))
							{
								//get the shipment CUSTOM METHOD
								shipmentCustomMeth = delegator.findByPrimaryKeyCache("CustomMethod", [customMethodId : shipmentCustomMethodId]);
								if(UtilValidate.isNotEmpty(shipmentCustomMeth))
								{
									customMethodName = shipmentCustomMeth.customMethodName;
									if(UtilValidate.isNotEmpty(customMethodName))
									{
										//run the custom method
										processorResult = null;
										try 
										{
											processorResult = dispatcher.runSync(customMethodName, customMethodContext);
										} 
										catch (GenericServiceException e)
										{
											//Debug.logError(e, module);
										}
										isAvailable = "N";
										if(UtilValidate.isNotEmpty(processorResult))
										{
											isAvailable = processorResult.get("isAvailable");
										}
								
										//if shipping option is not available for this customer then remove from the displayed list
										if("N".equals(isAvailable))
										{
											returnShippingMethods.remove(method);
										}
									}
								}
							}
						}
	        		}
	        		carrierShipmentMethodList = returnShippingMethods;
	        	}
	        }
			if (UtilValidate.isNotEmpty(carrierShipmentMethodList))
			{
			    context.carrierShipmentMethodList = carrierShipmentMethodList;
			}
			if (UtilValidate.isNotEmpty(shopCart.getShipmentMethodTypeId()) && UtilValidate.isNotEmpty(shopCart.getCarrierPartyId())) 
			{
			    context.chosenShippingMethod = shopCart.getShipmentMethodTypeId() + '@' + shopCart.getCarrierPartyId();
			}
			
            shippingContactMechId = shopCart.getShippingContactMechId();
            if(UtilValidate.isEmpty(shippingContactMechId))
            {
            	shopCart.setShippingContactMechId(context.shippingContactMechId);
            }
			
			context.orderShippingTotal = shopCart.getTotalShipping();
			context.orderTaxTotal = shopCart.getTotalSalesTax();
			context.orderGrandTotal = shopCart.getGrandTotal();
			
			
			currencyRounding=2;
			roundCurrency = globalContext.CURRENCY_UOM_ROUNDING;
			if (UtilValidate.isNotEmpty(roundCurrency) && OsafeAdminUtil.isNumber(roundCurrency))
			{
				currencyRounding = Integer.parseInt(roundCurrency);
			}
			globalContext.currencyRounding =currencyRounding;
			
			orderAdjustmentTypeList = delegator.findList("OrderAdjustmentType", EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN, ["DISCOUNT_ADJUSTMENT", "MISCELLANEOUS_CHARGE","SURCHARGE_ADJUSTMENT"]), null, ["description"], null, false);
			context.orderAdjustmentTypeList = orderAdjustmentTypeList;
			
			
			// Selected Shipping Method
			chosenShippingMethod = "";
			chosenShippingMethodDescription = "";
			if (UtilValidate.isNotEmpty(shopCart.getShipmentMethodTypeId()) && UtilValidate.isNotEmpty(shopCart.getCarrierPartyId()))
			{
				chosenShippingMethod = shopCart.getShipmentMethodTypeId() + '@' + shopCart.getCarrierPartyId();
				if (chosenShippingMethod.equals("NO_SHIPPING@_NA_"))
				{
					chosenShippingMethodDescription = uiLabelMap.StorePickupLabel;
				}
				else
				{
					carrier =  delegator.findByPrimaryKeyCache("PartyGroup", UtilMisc.toMap("partyId", shopCart.getCarrierPartyId()));
					if(UtilValidate.isNotEmpty(carrier))
					{
						if(UtilValidate.isNotEmpty(carrier.groupName))
						{
							chosenShippingMethodDescription = carrier.groupName + " " + shopCart.getShipmentMethodType(0).description;
						}
						else
						{
							chosenShippingMethodDescription = shopCart.getCarrierPartyId() + " " + shopCart.getShipmentMethodType(0).description;
						}
					}
				}
			}
			shippingInstructions = "";
			shippingInstructions = shopCart.getShippingInstructions();
			//Set Cart Totals
			//Adjustments are pulled in the FTL
			try
			{
				shippingAddress = shopCart.getShippingAddress();
				if (chosenShippingMethod.equals("NO_SHIPPING@_NA_") || (UtilValidate.isNotEmpty(shippingAddress) && (UtilValidate.isNotEmpty(shippingAddress.get("countryGeoId")) || UtilValidate.isNotEmpty(shippingAddress.get("stateProvinceGeoId")) || UtilValidate.isNotEmpty(shippingAddress.get("postalCodeGeoId")))))
				{
					com.osafe.events.CheckOutEvents.calculateTax(request, response);
				}
			}
			catch(Exception e)
			{
				Debug.logError(e, e.toString(), "showCartItems.groovy");
			}
			
		}		
	}
}
	
	

