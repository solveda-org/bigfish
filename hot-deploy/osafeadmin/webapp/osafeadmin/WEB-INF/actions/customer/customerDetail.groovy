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
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.GenericValue;

//Get the Cart and Prepare Size
shoppingCart = ShoppingCartEvents.getCartObject(request);
context.shoppingCart = shoppingCart;

partyId = StringUtils.trimToEmpty(parameters.partyId);
context.partyId=partyId;
if (UtilValidate.isNotEmpty(partyId))
{
	context.generalInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","CustomerDetailInfoHeading",["partyId" : partyId], locale )
	context.customerAttributesInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","CustomerAttributesInfoHeading",["partyId" : partyId], locale )
}
if (UtilValidate.isNotEmpty(partyId))
{
	party = delegator.findByPrimaryKey("Party", [partyId : partyId]);
	if (UtilValidate.isNotEmpty(party))
	{
        context.party=party;

        person = party.getRelatedOne("Person");
        context.person=person;

		partyRoles = party.getRelated("ProductStoreRole");
        context.partyRoles=partyRoles;
        // set party product store name in context
        if (UtilValidate.isNotEmpty(partyRoles))
    	{
        	partyRoles = EntityUtil.filterByDate(partyRoles,true);
        	partyRole = EntityUtil.getFirst(partyRoles);
        	partyProductStore = partyRole.getRelatedOne("ProductStore");
            if (UtilValidate.isNotEmpty(partyProductStore))
        	{
                if (UtilValidate.isNotEmpty(partyProductStore.storeName))
            	{
                    productStoreName = partyProductStore.storeName;
            	}
                else
                {
                	productStoreName = partyProductStore.productStoreId;
                }
            	context.productStoreName = productStoreName;
        	}
    	}
        
        userLogins = party.getRelated("UserLogin");
        context.userLogins=userLogins;
		
		
		//Get PARTY BILLING,SHIPPING,PRIMARY EMAIL, TELEPHONE LOCATIONS
        partyContactMechPurpose = party.getRelated("PartyContactMechPurpose");
    	if (UtilValidate.isNotEmpty(partyContactMechPurpose))
    	{
	        partyContactMechPurpose = EntityUtil.filterByDate(partyContactMechPurpose,true);
	
	        shippingContactMechList = FastList.newInstance();
	        addrExpr= FastList.newInstance();
	        addrExpr.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "SHIPPING_LOCATION"));
	        addrExpr.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "GENERAL_LOCATION"));
	        addrExpr.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
	        partyShippingLocations = EntityUtil.filterByOr(partyContactMechPurpose,addrExpr);
	        partyShippingLocations = EntityUtil.getRelated("PartyContactMech", partyShippingLocations);
	        partyShippingLocations = EntityUtil.filterByDate(partyShippingLocations,true);
	        partyShippingLocations = EntityUtil.orderBy(partyShippingLocations, UtilMisc.toList("fromDate DESC"));
	        if (UtilValidate.isNotEmpty(partyShippingLocations)) 
	        {
	        	partyShippingLocation = EntityUtil.getFirst(partyShippingLocations);
	            context.shippingContactMechId = partyShippingLocation.contactMechId;
	            contactMechList = EntityUtil.getRelated("ContactMech",partyShippingLocations);
	            for (GenericValue contactMech: contactMechList) 
	            {
	                if (!shippingContactMechList.contains(contactMech))
	                {
	                	shippingContactMechList.add(contactMech)
	                }

	            }

	        }
	
			context.resultList= shippingContactMechList;
			
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
		context.customerAttributes = partyAttributes;

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
	    //PARTY_EMAIL_PREFERENCE
	    partyAttrs = EntityUtil.filterByAnd(partyAttributes, UtilMisc.toMap("attrName", "PARTY_TEXT_PREFERENCE"));
	    if (UtilValidate.isNotEmpty(partyAttrs))
	    {
	    	partyAttr = EntityUtil.getFirst(partyAttrs);
	    	context.userTextPreference = partyAttr.attrValue;
	    }
		
		
	}
	
}

