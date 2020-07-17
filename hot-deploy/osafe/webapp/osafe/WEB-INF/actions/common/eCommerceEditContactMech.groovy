package common;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;

postalAddressData = context.postalAddress;
if (UtilValidate.isNotEmpty(postalAddressData))
{
    if (UtilValidate.isNotEmpty(postalAddressData.toName))
    {
        String toName = postalAddressData.toName;
        toNameParts  = StringUtil.split(toName, " ");

        if (UtilValidate.isNotEmpty(toNameParts) && toNameParts.size() > 0)
        {
            context.firstName = toNameParts[0];
            context.lastName = StringUtil.join(toNameParts.subList(1,toNameParts.size()), " ");
        }
    }
}


if (UtilValidate.isNotEmpty(parameters.CUSTOMER_STATE)) 
{
    geoValue = delegator.findByPrimaryKeyCache("Geo", [geoId : parameters.CUSTOMER_STATE]);
    if (UtilValidate.isNotEmpty(geoValue)) 
    {
        context.selectedStateName = geoValue.geoName;
    }
}

context.formRequestName = parameters.osafeFormRequestName;

contactMech = context.contactMech;
phoneNumberMap = [:];
if (UtilValidate.isNotEmpty(contactMech))
{
    contactMechIdFrom = contactMech.contactMechId;
    contactMechLinkList = delegator.findByAndCache("ContactMechLink", UtilMisc.toMap("contactMechIdFrom", contactMechIdFrom))

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
context.phoneNumberMap = phoneNumberMap;