package customer;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.party.party.PartyHelper;

//Get the Cart and Prepare Size
shoppingCart = ShoppingCartEvents.getCartObject(request);
context.shoppingCart = shoppingCart;

partyId = StringUtils.trimToEmpty(parameters.partyId);
context.partyId=partyId;
if (UtilValidate.isNotEmpty(partyId))
{
	context.generalInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","CustomerDetailInfoHeading",["partyId" : partyId], locale )
}
if (UtilValidate.isNotEmpty(partyId))
{
	party = delegator.findByPrimaryKey("Party", [partyId : partyId]);
	if (UtilValidate.isNotEmpty(party))
	{
        context.party=party;

        person = party.getRelatedOne("Person");
        context.person=person;

		partyRoles = party.getRelated("PartyRole");
        context.partyRoles=partyRoles;
        
        userLogins = party.getRelated("UserLogin");
        context.userLogins=userLogins;
		
		
		//Get PARTY BILLING,SHIPPING,PRIMARY EMAIL, TELEPHONE LOCATIONS
        partyContactMechPurpose = party.getRelated("PartyContactMechPurpose");
    	if (UtilValidate.isNotEmpty(partyContactMechPurpose))
    	{
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
	
	        if (UtilValidate.isNotEmpty(context.billingContactMechList))
			{
			    billingContactMech = context.billingContactMechList.get(0);
			    // Moving the billing address to the front of the list
			    if(UtilValidate.isNotEmpty(context.shippingContactMechList))
			    {
			        context.shippingContactMechList.remove(billingContactMech);
			        context.shippingContactMechList.add(0,billingContactMech);
			        context.billingContactMechId=billingContactMech.contactMechId;
			    } else {
		            context.shippingContactMechList = [billingContactMech];
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
	            context.partyPurposeWorkPhone =partyPurposePhone;
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
    	}
		//Get PARTY ATTRIBUTES
		partyAttributes = party.getRelated("PartyAttribute");

	    //IS_DOWNLOADED
	    partyAttrs = EntityUtil.filterByAnd(partyAttributes, UtilMisc.toMap("attrName", "IS_DOWNLOADED"));
	    if (UtilValidate.isNotEmpty(partyAttrs))
	    {
	    	partyAttr = EntityUtil.getFirst(partyAttrs);
	    	context.IS_DOWNLOADED = partyAttr.attrValue;
	    }
	    //PARTY_EMAIL_PREFERENCE
	    partyAttrs = EntityUtil.filterByAnd(partyAttributes, UtilMisc.toMap("attrName", "PARTY_EMAIL_PREFERENCE"));
	    if (UtilValidate.isNotEmpty(partyAttrs))
	    {
	    	partyAttr = EntityUtil.getFirst(partyAttrs);
	    	context.PARTY_EMAIL_PREFERENCE = partyAttr.attrValue;
	    }			    	
		
		
	}
	
}

