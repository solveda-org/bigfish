package common;

import java.util.HashMap;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;

contactMechPurposeTypeId =  parameters.contactMechPurposeTypeId;
partyContactMechPurposes = context.partyContactMechPurposes;
if (UtilValidate.isNotEmpty(partyContactMechPurposes))
{
	partyContactMechPurpose = EntityUtil.getFirst(partyContactMechPurposes);
	contactMechPurposeTypeId = partyContactMechPurpose.contactMechPurposeTypeId;
}
context.purposeType = contactMechPurposeTypeId;
