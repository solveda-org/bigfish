package storelocation;


import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.common.CommonWorkers;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.party.party.PartyHelper;

userLogin = session.getAttribute("userLogin");
partyId = StringUtils.trimToEmpty(parameters.storePartyId);
messageMap=[:];
messageMap.put("partyId", partyId);

context.partyId=partyId;

party = delegator.findByPrimaryKey("Party", [partyId : partyId]);
context.party = party;
storeName = "";
storeName = PartyHelper.getPartyName(party);
partyGroup = delegator.findOne("PartyGroup", [partyId : partyId], false);
if (UtilValidate.isNotEmpty(partyGroup)) 
{
    context.partyGroup = partyGroup;
    storeName = partyGroup.groupName;
}
context.storeName = storeName;
partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
if (UtilValidate.isNotEmpty(partyContactMechValueMaps))
{
    partyContactMechValueMaps.each { partyContactMechValueMap ->
        contactMechPurposes = partyContactMechValueMap.partyContactMechPurposes;
        contactMechPurposes.each { contactMechPurpose ->
            if (contactMechPurpose.contactMechPurposeTypeId.equals("GENERAL_LOCATION")) 
            {
                context.partyGeneralContactMechValueMap = partyContactMechValueMap;
            } else if (contactMechPurpose.contactMechPurposeTypeId.equals("PRIMARY_PHONE"))
            {
                context.partyPrimaryPhoneContactMechValueMap = partyContactMechValueMap;
            } else if (contactMechPurpose.contactMechPurposeTypeId.equals("PRIMARY_EMAIL"))
            {
                context.partyPrimaryEmailContactMechValueMap = partyContactMechValueMap;
            }
        }
    }
}

partyContent = EntityUtil.getFirst(delegator.findByAnd("PartyContent", [partyId : partyId, 	partyContentTypeId : "STORE_HOURS"]));
if (UtilValidate.isNotEmpty(partyContent)) 
{
    content = partyContent.getRelatedOne("Content");
    if (UtilValidate.isNotEmpty(content))
    {
       context.storeHoursContentId = content.contentId;
       dataResource = content.getRelatedOne("DataResource");
       if (UtilValidate.isNotEmpty(dataResource))
        {
           context.storeHoursDataResourceId = dataResource.dataResourceId;
        }
    }
}

partyContent = EntityUtil.getFirst(delegator.findByAnd("PartyContent", [partyId : partyId, 	partyContentTypeId : "STORE_NOTICE"]));
if (UtilValidate.isNotEmpty(partyContent)) 
{
    content = partyContent.getRelatedOne("Content");
    if (UtilValidate.isNotEmpty(content))
    {
       context.storeNoticeContentId = content.contentId;
       dataResource = content.getRelatedOne("DataResource");
       if (UtilValidate.isNotEmpty(dataResource))
       {
          context.storeNoticeDataResourceId = dataResource.dataResourceId;
       }
    }
}

partyContent = EntityUtil.getFirst(delegator.findByAnd("PartyContent", [partyId : partyId, 	partyContentTypeId : "STORE_CONTENT_SPOT"]));
if (UtilValidate.isNotEmpty(partyContent)) 
{
	content = partyContent.getRelatedOne("Content");
	if (UtilValidate.isNotEmpty(content))
	{
	   context.storeContentSpotContentId = content.contentId;
	   dataResource = content.getRelatedOne("DataResource");
	   if (UtilValidate.isNotEmpty(dataResource))
	   {
		  context.storeContentSpotDataResourceId = dataResource.dataResourceId;
	   }
	}
}

partyGeoPoint = EntityUtil.getFirst(delegator.findByAnd("PartyGeoPoint", [partyId : partyId]))
if (UtilValidate.isNotEmpty(partyGeoPoint)) 
{
    geoPoint = partyGeoPoint.getRelatedOne("GeoPoint");
    context.geoPoint = geoPoint;
}