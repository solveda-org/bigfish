package common;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.*;

context.allowSolicitation= "";
context.partyEmailPreference="";

if (UtilValidate.isNotEmpty(userLogin)) 
{
    context.userLoginId = userLogin.userLoginId;
    party = userLogin.getRelatedOneCache("Party");
    person = party.getRelatedOneCache("Person");
    context.person=person;
    partyId=person.partyId
    context.partyId = partyId;
    partyAttribute = delegator.findByAndCache("PartyAttribute", UtilMisc.toMap("partyId",partyId,"attrName","PARTY_EMAIL_PREFERENCE"));
    if (UtilValidate.isNotEmpty(partyAttribute))
    { 
      partyAttribute=EntityUtil.getFirst(partyAttribute);
      context.partyEmailPreference=partyAttribute.attrValue;
    }

    // get Party Phone Numbers
    context.homePhonePartyContactDetail = "";
    context.workPhonePartyContactDetail = "";
    context.mobilePhonePartyContactDetail = "";
    partyContactDetailByPurpose = delegator.findByAndCache("PartyContactDetailByPurpose",UtilMisc.toMap("partyId", partyId), UtilMisc.toList("-fromDate"));
    partyContactDetailByPurpose = EntityUtil.filterByDate(partyContactDetailByPurpose,true);
    
    partyHomePhone = EntityUtil.filterByAnd(partyContactDetailByPurpose,UtilMisc.toMap("contactMechPurposeTypeId", "PHONE_HOME"));
    if (UtilValidate.isNotEmpty(partyHomePhone))
    { 
       context.homePhonePartyContactDetail = EntityUtil.getFirst(partyHomePhone);
    }
    partyWorkPhone = EntityUtil.filterByAnd(partyContactDetailByPurpose,UtilMisc.toMap("contactMechPurposeTypeId", "PHONE_WORK"));
    if (UtilValidate.isNotEmpty(partyWorkPhone))
    { 
       context.workPhonePartyContactDetail = EntityUtil.getFirst(partyWorkPhone);
    }
    partyMobilePhone = EntityUtil.filterByAnd(partyContactDetailByPurpose,UtilMisc.toMap("contactMechPurposeTypeId", "PHONE_MOBILE"));
    if (UtilValidate.isNotEmpty(partyMobilePhone))
    { 
       context.mobilePhonePartyContactDetail = EntityUtil.getFirst(partyMobilePhone);
    }

    context.contactMech = "";
    contactMechBilling = "";
    partyContactMechPurpose = party.getRelatedCache("PartyContactMechPurpose");
    partyContactMechPurpose = EntityUtil.filterByDate(partyContactMechPurpose,true);
    
    
    partyPurposeEmails = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "PRIMARY_EMAIL"));
    if (UtilValidate.isNotEmpty(partyPurposeEmails)) 
    {
    	partyPurposeEmail = EntityUtil.getFirst(partyPurposeEmails);
        contactMech = partyPurposeEmail.getRelatedOneCache("ContactMech");
        context.userEmailContactMech = contactMech;
        context.userEmailAddress = contactMech.infoString;
        partyContactMechs = partyPurposeEmail.getRelatedCache("PartyContactMech");
        partyContactMechs = EntityUtil.filterByAnd(partyContactMechs, UtilMisc.toMap("contactMechId", contactMech.contactMechId));
        if (UtilValidate.isNotEmpty(partyContactMechs))
        {
        	partyContactMech = EntityUtil.getFirst(partyContactMechs);
            context.userEmailAllowSolicitation= partyContactMech.allowSolicitation;
        	
        }
        
    }
    
    
    partyBillingLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "BILLING_LOCATION"));
    if (UtilValidate.isNotEmpty(partyBillingLocations))
    {
        contactMechBilling = EntityUtil.getFirst(partyBillingLocations);
    }

    postalAddressData = "";
    phoneNumberMap = [:];
    if (UtilValidate.isNotEmpty(contactMechBilling))
    {
        context.contactMech = contactMechBilling;
	    postalAddressData = contactMechBilling.getRelatedOneCache("PostalAddress");
	    context.postalAddressData = postalAddressData;
	    if (UtilValidate.isNotEmpty(postalAddressData))
	    {
	        if (postalAddressData.toName != null)
	        {
	            String toName = postalAddressData.toName;
	            toNameParts  = StringUtil.split(toName, " ");
	            if (UtilValidate.isNotEmpty(toNameParts))
	            {
	                if (toNameParts.size() > 0)
	                {
	                    context.toNameFirst = toNameParts[0];
	                    context.toNameLast = StringUtil.join(toNameParts.subList(1,toNameParts.size()), " ");
	                }
	            }
	        }
	    }
	    
        contactMechLinkList = delegator.findByAndCache("ContactMechLink", UtilMisc.toMap("contactMechIdFrom", contactMechBilling.contactMechId));
	    if (UtilValidate.isNotEmpty(contactMechLinkList))
	    {
	        for (GenericValue link: contactMechLinkList)
	        {
	            contactMechIdTo = link.contactMechIdTo
	            contactMech = delegator.findByPrimaryKeyCache("ContactMech", [contactMechId : contactMechIdTo]);
	            if(UtilValidate.isNotEmpty(contactMech)) 
	            {
	                phonePurposeList  = contactMech.getRelatedCache("PartyContactMechPurpose");
	                phonePurposeList  = EntityUtil.filterByDate(phonePurposeList, true);
	                if(UtilValidate.isNotEmpty(phonePurposeList)) 
	                {
	                    partyContactMechPurpose = EntityUtil.getFirst(phonePurposeList)
	                    if(UtilValidate.isNotEmpty(partyContactMechPurpose)) 
	                    {
	                        telecomNumber = partyContactMechPurpose.getRelatedOneCache("TelecomNumber");
	                        phoneNumberMap[partyContactMechPurpose.contactMechPurposeTypeId]=telecomNumber;
	                    }
	                }
	            }

	        }
	    }
    }

    context.phoneNumberMap = phoneNumberMap;


    if (UtilValidate.isNotEmpty(parameters.CUSTOMER_STATE)) 
    {
        geoValue = delegator.findByPrimaryKeyCache("Geo", [geoId : parameters.CUSTOMER_STATE]);
        if (UtilValidate.isNotEmpty(geoValue))
        {
            context.selectedStateName = geoValue.geoName;
        }
    } else if (UtilValidate.isNotEmpty(postalAddressData) && UtilValidate.isNotEmpty(postalAddressData.stateProvinceGeoId)) 
    {
        geoValue = delegator.findByPrimaryKeyCache("Geo", [geoId : postalAddressData.stateProvinceGeoId]);
        if (UtilValidate.isNotEmpty(geoValue))
        {
            context.selectedStateName = geoValue.geoName;
        }
    }
}