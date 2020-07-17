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


    telecomNumber = EntityUtil.getFirst(EntityUtil.filterByDate(delegator.findByAnd("PartyContactDetailByPurpose",
            [partyId : party.partyId, contactMechPurposeTypeId : "PHONE_HOME"], UtilMisc.toList("-fromDate"))));

   if(telecomNumber){
       context.contactNumberHome= telecomNumber.contactNumber;
       context.areaCodeHome=telecomNumber.areaCode;
       if(telecomNumber.contactNumber.length() == 7){
           context.contactNumber3Home=telecomNumber.contactNumber.substring(0,3);
           context.contactNumber4Home=telecomNumber.contactNumber.substring(3,7);
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