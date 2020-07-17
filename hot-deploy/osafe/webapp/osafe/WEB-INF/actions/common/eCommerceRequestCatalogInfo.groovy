package common;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.Debug;

if (UtilValidate.isNotEmpty(userLogin)) 
{
	context.emailLogin=userLogin.userLoginId;
	person = userLogin.getRelatedOneCache("Person");
	context.firstName=person.firstName;
	context.lastName=person.lastName;
	party = userLogin.getRelatedOneCache("Party");
	contactMech = EntityUtil.getFirst(ContactHelper.getContactMech(party, "BILLING_LOCATION", "POSTAL_ADDRESS", false));
	context.contactMech = contactMech;
	postalAddressData = contactMech.getRelatedOneCache("PostalAddress");
	context.address1 = postalAddressData.address1;
	context.address2 = postalAddressData.address2;
	context.city=postalAddressData.city;
	context.postalCode=postalAddressData.postalCode;
	context.postalAddressData=postalAddressData;

	 if (UtilValidate.isNotEmpty(parameters.stateCode)) 
	 {
	        geoValue = delegator.findByPrimaryKeyCache("Geo", [geoId : parameters.stateCode]);
	        if (UtilValidate.isNotEmpty(geoValue)) 
	        {
	            context.selectedStateName = geoValue.geoName;
	            context.stateProvinceGeoId = geoValue.geoId;
	        }
	 } 
	 else if (UtilValidate.isNotEmpty(postalAddressData) && UtilValidate.isNotEmpty(postalAddressData.stateProvinceGeoId)) 
	 {
	        geoValue = delegator.findByPrimaryKeyCache("Geo", [geoId : postalAddressData.stateProvinceGeoId]);
	        if (UtilValidate.isNotEmpty(geoValue)) 
	        {
	            context.selectedStateName = geoValue.geoName;
	            context.stateProvinceGeoId = geoValue.geoId;
	        }
	 }
	
    telecomNumber = delegator.findByAndCache("PartyContactDetailByPurpose",[partyId : party.partyId, contactMechPurposeTypeId : "PHONE_HOME"], UtilMisc.toList("fromDate"));
    telecomNumber = EntityUtil.filterByDate(telecomNumber,true);

    if(UtilValidate.isNotEmpty(telecomNumber))
	{
	       telecomNumber = EntityUtil.getFirst(telecomNumber);
	       context.contactNumberHome= telecomNumber.contactNumber;
	       context.areaCodeHome=telecomNumber.areaCode;
	       if(UtilValidate.isNotEmpty(telecomNumber.contactNumber) && (telecomNumber.contactNumber.length() > 6))
	       {
	           context.contactNumber3Home=telecomNumber.contactNumber.substring(0,3);
	           context.contactNumber4Home=telecomNumber.contactNumber.substring(3,7);
	       }
	 }
} 
else
{
    if (UtilValidate.isNotEmpty(parameters.stateCode)) 
    {
	    geoValue = delegator.findByPrimaryKeyCache("Geo", [geoId : parameters.stateCode]);
	    if (UtilValidate.isNotEmpty(geoValue)) 
	    {
	        context.selectedStateName = geoValue.geoName;
	        context.stateProvinceGeoId = geoValue.geoId;
	    }
    }
}