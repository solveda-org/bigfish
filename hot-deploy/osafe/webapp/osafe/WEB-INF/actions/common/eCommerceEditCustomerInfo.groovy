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

if (userLogin) {
    context.userLoginId = userLogin.userLoginId;
    party = userLogin.getRelatedOne("Party");


    person = party.getRelatedOne("Person");
    context.person=person;
    partyId=person.partyId
    context.partyId = partyId;
    partyAttribute = delegator.findByAnd("PartyAttribute", UtilMisc.toMap("partyId",partyId,"attrName","PARTY_EMAIL_PREFERENCE"));
    if (UtilValidate.isNotEmpty(partyAttribute))
    { 
      partyAttribute=EntityUtil.getFirst(partyAttribute);
      context.partyEmailPreference=partyAttribute.attrValue;
    }

    // get the Phone Numbers
    context.homePhonePartyContactDetail = EntityUtil.getFirst(EntityUtil.filterByDate(delegator.findByAnd("PartyContactDetailByPurpose",
            [partyId : partyId, contactMechPurposeTypeId : "PHONE_HOME"], UtilMisc.toList("-fromDate"))));
    context.workPhonePartyContactDetail = EntityUtil.getFirst(EntityUtil.filterByDate(delegator.findByAnd("PartyContactDetailByPurpose",
            [partyId : partyId, contactMechPurposeTypeId : "PHONE_WORK"], UtilMisc.toList("-fromDate"))));
    context.mobilePhonePartyContactDetail = EntityUtil.getFirst(EntityUtil.filterByDate(delegator.findByAnd("PartyContactDetailByPurpose",
                    [partyId : partyId, contactMechPurposeTypeId : "PHONE_MOBILE"], UtilMisc.toList("-fromDate"))));

    contactMech = EntityUtil.getFirst(ContactHelper.getContactMech(party, "BILLING_LOCATION", "POSTAL_ADDRESS", false));
    context.contactMech = contactMech;
    postalAddressData = contactMech.getRelatedOne("PostalAddress");
    context.postalAddressData = postalAddressData;
    if (postalAddressData){
        if (postalAddressData.toName != null){
            String toName = postalAddressData.toName;
            toNameParts  = StringUtil.split(toName, " ");
            if (toNameParts){
                if (toNameParts.size() > 0){
                    context.toNameFirst = toNameParts[0];
                    context.toNameLast = StringUtil.join(toNameParts.subList(1,toNameParts.size()), " ");
                }
            }
        }
    }


    if (parameters.CUSTOMER_STATE) {
        geoValue = delegator.findByPrimaryKeyCache("Geo", [geoId : parameters.CUSTOMER_STATE]);
        if (geoValue) {
            context.selectedStateName = geoValue.geoName;
        }
    } else if (postalAddressData?.stateProvinceGeoId) {
        geoValue = delegator.findByPrimaryKeyCache("Geo", [geoId : postalAddressData.stateProvinceGeoId]);
        if (geoValue) {
            context.selectedStateName = geoValue.geoName;
        }
    }

    contactMech = context.contactMech;
    phoneNumberMap = [:];
    if(contactMech){
        contactMechIdFrom = contactMech.contactMechId;
        contactMechLinkList = delegator.findByAnd("ContactMechLink", UtilMisc.toMap("contactMechIdFrom", contactMechIdFrom))

        for (GenericValue link: contactMechLinkList){
            contactMechIdTo = link.contactMechIdTo
            contactMech = delegator.findByPrimaryKey("ContactMech", [contactMechId : contactMechIdTo]);
            phonePurposeList  = EntityUtil.filterByDate(contactMech.getRelated("PartyContactMechPurpose"), true);
            partyContactMechPurpose = EntityUtil.getFirst(phonePurposeList)

            telecomNumber = null;
            if(partyContactMechPurpose) {
                telecomNumber = partyContactMechPurpose.getRelatedOne("TelecomNumber");
            }

            if(telecomNumber) {
                phoneNumberMap[partyContactMechPurpose.contactMechPurposeTypeId]=telecomNumber;
            }
        }
    }
    context.phoneNumberMap = phoneNumberMap;


    userEmailContactMechList = ContactHelper.getContactMech(party, "PRIMARY_EMAIL", "EMAIL_ADDRESS", false)
    userEmailContactMech = EntityUtil.getFirst(userEmailContactMechList);
    if (UtilValidate.isNotEmpty(userEmailContactMech)) {
        context.userEmailContactMech = userEmailContactMech;
        context.userEmailAddress = userEmailContactMech.infoString;
        partyContactMech = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId", userEmailContactMech.contactMechId));
        partyContactMech = EntityUtil.filterByDate(partyContactMech);
        if (UtilValidate.isNotEmpty(partyContactMech))
        {
          partyContactMech = EntityUtil.getFirst(partyContactMech);
          context.userEmailAllowSolicitation= partyContactMech.allowSolicitation;
        }
    }
}