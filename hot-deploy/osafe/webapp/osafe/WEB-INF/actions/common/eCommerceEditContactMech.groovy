package common;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

postalAddressData = context.postalAddress;
if (postalAddressData){
    if (postalAddressData.toName != null){
        String toName = postalAddressData.toName;
        toNameParts  = StringUtil.split(toName, " ");

        if (toNameParts.size() > 0){
            context.firstName = toNameParts[0];
            context.lastName = StringUtil.join(toNameParts.subList(1,toNameParts.size()), " ");
        }
    }
}


if (parameters.CUSTOMER_STATE) {
    geoValue = delegator.findByPrimaryKeyCache("Geo", [geoId : parameters.CUSTOMER_STATE]);
    if (geoValue) {
        context.selectedStateName = geoValue.geoName;
    }
}

context.formRequestName = parameters.osafeFormRequestName;

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