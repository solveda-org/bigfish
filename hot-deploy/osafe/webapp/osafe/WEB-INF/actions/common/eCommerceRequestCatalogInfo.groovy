package common;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.entity.condition.EntityCondition;

if (userLogin) {
context.emailLogin=userLogin.userLoginId;
person = userLogin.getRelatedOne("Person");
context.firstName=person.firstName;
context.lastName=person.lastName;
party = userLogin.getRelatedOne("Party");
contactMech = EntityUtil.getFirst(ContactHelper.getContactMech(party, "BILLING_LOCATION", "POSTAL_ADDRESS", false));
context.contactMech = contactMech;
postalAddressData = contactMech.getRelatedOne("PostalAddress");
context.address1 = postalAddressData.address1;
context.address2 = postalAddressData.address2;
context.city=postalAddressData.city;
context.postalCode=postalAddressData.postalCode;
context.postalAddressData=postalAddressData;

 if (parameters.stateCode) {
        geoValue = delegator.findByPrimaryKeyCache("Geo", [geoId : parameters.stateCode]);
        if (geoValue) {
            context.selectedStateName = geoValue.geoName;
            context.stateProvinceGeoId = geoValue.geoId;
        }
    } else if (postalAddressData?.stateProvinceGeoId) {
        geoValue = delegator.findByPrimaryKeyCache("Geo", [geoId : postalAddressData.stateProvinceGeoId]);
        if (geoValue) {
            context.selectedStateName = geoValue.geoName;
            context.stateProvinceGeoId = geoValue.geoId;
        }
    }

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
    telecomNumber = phoneNumberMap["PHONE_HOME"];

   if(telecomNumber){
       context.contactPhoneContact= telecomNumber.contactNumber;
       context.contactPhoneArea=telecomNumber.areaCode;
       if(telecomNumber.contactNumber.length() == 7){
           context.contactPhoneContact3=telecomNumber.contactNumber.substring(0,3);
           context.contactPhoneContact4=telecomNumber.contactNumber.substring(3,7);
       }
   }
}
else{
if (parameters.stateCode) {
    geoValue = delegator.findByPrimaryKeyCache("Geo", [geoId : parameters.stateCode]);
    if (geoValue) {
        context.selectedStateName = geoValue.geoName;
        context.stateProvinceGeoId = geoValue.geoId;
    }
}

}