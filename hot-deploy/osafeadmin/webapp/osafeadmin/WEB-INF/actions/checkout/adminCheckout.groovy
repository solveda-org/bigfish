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



//shopCart = ShoppingCartEvents.getCartObject(request);
//context.shoppingCart =shopCart;
adminContext = session.getAttribute("ADMIN_CONTEXT");

if (UtilValidate.isNotEmpty(adminContext))
{
	partyId=adminContext.CONTEXT_PARTY_ID;
	if (UtilValidate.isNotEmpty(partyId))
	{
		if (UtilValidate.isNotEmpty(partyId))
		{
			context.CustomerInformationHeading = UtilProperties.getMessage("OSafeAdminUiLabels","CustomerDetailInfoHeading",["partyId" : partyId], locale )
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
		    shopCart = ShoppingCartEvents.getCartObject(request);
		    context.shoppingCartSize = shopCart.size();
		    context.shoppingCart = shopCart;
			shopCart.setOrderPartyId(partyId);
			//Get CART SHIPPING ESTIMATES
			shippingEstWpr = new ShippingEstimateWrapper(dispatcher, shopCart, 0);
			context.shippingEstWpr = shippingEstWpr;
			carrierShipmentMethodList = shippingEstWpr.getShippingMethods();
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
		    

		

		}		
	}
}
	
	

