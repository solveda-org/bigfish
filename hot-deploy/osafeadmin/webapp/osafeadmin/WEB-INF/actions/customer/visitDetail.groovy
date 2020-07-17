package customer;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.party.contact.ContactMechWorker;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;


userLogin = session.getAttribute("userLogin");
visitId = StringUtils.trimToEmpty(parameters.visitId);
userLoginId = StringUtils.trimToEmpty(parameters.userLoginId);

context.visitId=visitId;
context.userLoginId=userLoginId;
partyId= "";


if (UtilValidate.isNotEmpty(visitId))
{
	visit = delegator.findByPrimaryKey("Visit", [visitId : visitId]);
	if (UtilValidate.isNotEmpty(visit))
	{
		partyId = visit.partyId;
		party = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "CUSTOMER"));
		party = EntityUtil.getFirst(party);
		if (UtilValidate.isNotEmpty(party))
		{
			partyProductStore = party.getRelatedOne("ProductStore");
			if (UtilValidate.isNotEmpty(partyProductStore.storeName))
			{
				productStoreName = partyProductStore.storeName;
			}
			else
			{
				productStoreName = partyProductStore.productStoreId;
			}
			context.productStoreName = productStoreName;
		}
	}
}

context.visit = visit;
context.nowStr = UtilDateTime.nowTimestamp().toString();

if (UtilValidate.isNotEmpty(visit)) 
{
	if(UtilValidate.isNotEmpty(visit.partyId))
	{
		partyId= visit.partyId;
		party = delegator.findByPrimaryKey("Party", [partyId : visit.partyId]);
		context.party = party;
	}
}

if (UtilValidate.isNotEmpty(visit)) 
{
	if(UtilValidate.isNotEmpty(visit.partyId))
	{
		partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, visit.partyId, false);
		if (UtilValidate.isNotEmpty(partyContactMechValueMaps)) 
		{
			partyContactMechValueMaps.each { partyContactMechValueMap ->
				contactMechPurposes = partyContactMechValueMap.partyContactMechPurposes;
				contactMechPurposes.each { contactMechPurpose ->
					if (contactMechPurpose.contactMechPurposeTypeId.equals("GENERAL_LOCATION")) 
					{
						context.partyGeneralContactMechValueMap = partyContactMechValueMap;
					} 
					else if (contactMechPurpose.contactMechPurposeTypeId.equals("SHIPPING_LOCATION")) 
					{
						context.partyShippingContactMechValueMap = partyContactMechValueMap;
					} 
					else if (contactMechPurpose.contactMechPurposeTypeId.equals("BILLING_LOCATION")) 
					{
						context.partyBillingContactMechValueMap = partyContactMechValueMap;
					} 
					else if (contactMechPurpose.contactMechPurposeTypeId.equals("PAYMENT_LOCATION")) 
					{
						context.partyPaymentContactMechValueMap = partyContactMechValueMap;
					} 
					else if (contactMechPurpose.contactMechPurposeTypeId.equals("PHONE_HOME")) 
					{
						context.partyPhoneHomeContactMechValueMap = partyContactMechValueMap;
					} 
					else if (contactMechPurpose.contactMechPurposeTypeId.equals("PRIMARY_PHONE")) 
					{
						context.partyPrimaryPhoneContactMechValueMap = partyContactMechValueMap;
					} 
					else if (contactMechPurpose.contactMechPurposeTypeId.equals("PRIMARY_EMAIL")) 
					{
						context.partyPrimaryEmailContactMechValueMap = partyContactMechValueMap;
					} 
					else if (contactMechPurpose.contactMechPurposeTypeId.equals("PHONE_MOBILE")) 
					{
						context.partyPhoneMobileContactMechValueMap = partyContactMechValueMap;
					}
				}
			}
		}
	}
}

messageMap=[:];
messageMap.put("partyId", partyId);

context.generalInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","CustomerDetailInfoHeading",messageMap, locale )



