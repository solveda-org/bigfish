package common;

import java.util.HashMap;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;

contactMechPurposeTypeId = ""
contactMechId = parameters.contactMechId;
partyId = parameters.partyId;
if (UtilValidate.isNotEmpty(contactMechId))
{
	party = delegator.findByPrimaryKey("Party", [partyId : partyId]);
    partyContactMechPurposes = party.getRelated("PartyContactMechPurpose");
	if (UtilValidate.isNotEmpty(partyContactMechPurposes))
	{
        partyContactMechPurposes = EntityUtil.filterByDate(partyContactMechPurposes,true);
	    partyAddressPurposes = EntityUtil.filterByAnd(partyContactMechPurposes, UtilMisc.toMap("contactMechId", contactMechId));
		if (UtilValidate.isNotEmpty(partyAddressPurposes))
		{
	        partyAddressPurpose = EntityUtil.getFirst(partyAddressPurposes);
			contactMechPurposeTypeId = partyAddressPurpose.contactMechPurposeTypeId;
		}
	}
}
context.purposeType = contactMechPurposeTypeId;
