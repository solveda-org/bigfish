package storelocation;


import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.common.CommonWorkers;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.content.ContentWorker;

userLogin = session.getAttribute("userLogin");
partyId = StringUtils.trimToEmpty(parameters.partyId);
messageMap=[:];
messageMap.put("partyId", partyId);

context.partyId=partyId;

party = delegator.findByPrimaryKey("Party", [partyId : partyId]);
context.party = party;

partyGroup = delegator.findOne("PartyGroup", [partyId : partyId], true);
if (UtilValidate.isNotEmpty(partyGroup)) {
    context.partyGroup = partyGroup;
}

partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
if (partyContactMechValueMaps) {
    partyContactMechValueMaps.each { partyContactMechValueMap ->
        contactMechPurposes = partyContactMechValueMap.partyContactMechPurposes;
        contactMechPurposes.each { contactMechPurpose ->
            if (contactMechPurpose.contactMechPurposeTypeId.equals("GENERAL_LOCATION")) {
                context.partyGeneralContactMechValueMap = partyContactMechValueMap;
            } else if (contactMechPurpose.contactMechPurposeTypeId.equals("PRIMARY_PHONE")) {
                context.partyPrimaryPhoneContactMechValueMap = partyContactMechValueMap;
            } else if (contactMechPurpose.contactMechPurposeTypeId.equals("PRIMARY_EMAIL")) {
                context.partyPrimaryEmailContactMechValueMap = partyContactMechValueMap;
            }
        }
    }
}

partyContent = EntityUtil.getFirst(delegator.findByAnd("PartyContent", [partyId : partyId, 	partyContentTypeId : "STORE_HOURS"]));
if (UtilValidate.isNotEmpty(partyContent)) {
    content = partyContent.getRelatedOne("Content");
    if (UtilValidate.isNotEmpty(content))
    {
       context.storeHoursContentId = content.contentId;
//       storeHoursTextData = ContentWorker.renderContentAsText(dispatcher, delegator, content.contentId, [:], locale, "", true);
//       if (UtilValidate.isNotEmpty(storeHoursTextData) && storeHoursTextData != "null")
//        {
//           context.storeHoursTextData = storeHoursTextData;
//        }
       dataResource = content.getRelatedOne("DataResource");
       if (UtilValidate.isNotEmpty(dataResource))
        {
           context.storeHoursDataResourceId = dataResource.dataResourceId;
        }
    }
}

partyContent = EntityUtil.getFirst(delegator.findByAnd("PartyContent", [partyId : partyId, 	partyContentTypeId : "STORE_NOTICE"]));
if (UtilValidate.isNotEmpty(partyContent)) {
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

partyGeoPoint = EntityUtil.getFirst(delegator.findByAnd("PartyGeoPoint", [partyId : partyId]))
if (UtilValidate.isNotEmpty(partyGeoPoint)) {
    geoPoint = partyGeoPoint.getRelatedOne("GeoPoint");
    context.geoPoint = geoPoint;
}