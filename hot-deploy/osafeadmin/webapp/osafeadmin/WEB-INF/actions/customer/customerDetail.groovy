package customer;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.party.contact.*;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

userLogin = session.getAttribute("userLogin");
partyId = StringUtils.trimToEmpty(parameters.partyId);
userLoginId = StringUtils.trimToEmpty(parameters.userLoginId);
messageMap=[:];
messageMap.put("partyId", partyId);

context.partyId=partyId;
context.userLoginId=userLoginId;
context.pageTitle = UtilProperties.getMessage("OSafeAdminUiLabels","CustomerManagementDetailTitle",messageMap, locale )
context.generalInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","CustomerDetailInfoHeading",messageMap, locale )
context.customerNoteInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","CustomerNoteHeading",messageMap, locale )

if (!partyId && userLoginId) {
    thisUserLogin = delegator.findByPrimaryKey("UserLogin", [userLoginId : userLoginId]);
    if (thisUserLogin) {
        partyId = thisUserLogin.partyId;
        parameters.partyId = partyId;
    }
}

context.showOld = "true".equals(parameters.SHOW_OLD);
party = delegator.findByPrimaryKey("Party", [partyId : partyId]);
context.party = party;
context.nowStr = UtilDateTime.nowTimestamp().toString();

context.shippingContactMechList = ContactHelper.getContactMech(party, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
context.billingContactMechList = ContactHelper.getContactMech(party, "BILLING_LOCATION", "POSTAL_ADDRESS", false);
if (UtilValidate.isNotEmpty(context.billingContactMechList))
{
    billingContactMech = context.billingContactMechList.get(0);

    // Moving the billing address to the front of the list
    if(UtilValidate.isNotEmpty(context.shippingContactMechList)){
        context.shippingContactMechList.remove(billingContactMech);
        context.shippingContactMechList.add(0,billingContactMech);
        context.billingContactMechId=billingContactMech.contactMechId;
    }
}

shippingContactMechList = context.shippingContactMechList
shippingContactMechPhoneMap = [:];
for (GenericValue contactMech : shippingContactMechList) 
{
    phoneNumberMap = [:];
    if(contactMech)
    {
        contactMechIdFrom = contactMech.contactMechId;
        contactMechLinkList = delegator.findByAnd("ContactMechLink", UtilMisc.toMap("contactMechIdFrom", contactMechIdFrom))

        for (GenericValue link: contactMechLinkList) 
        {
            contactMechIdTo = link.contactMechIdTo
            contactMech = delegator.findByPrimaryKey("ContactMech", [contactMechId : contactMechIdTo]);
            phonePurposeList  = EntityUtil.filterByDate(contactMech.getRelated("PartyContactMechPurpose"), true);
            partyContactMechPurpose = EntityUtil.getFirst(phonePurposeList)

            telecomNumber = null;
            if(partyContactMechPurpose) 
            {
                telecomNumber = partyContactMechPurpose.getRelatedOne("TelecomNumber");
            }

            if(telecomNumber) 
            {
                phoneNumberMap[partyContactMechPurpose.contactMechPurposeTypeId]=telecomNumber;
            }
        }
    }
    shippingContactMechPhoneMap[contactMechIdFrom] = phoneNumberMap;
}
context.shippingContactMechPhoneMap = shippingContactMechPhoneMap;


partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
if (partyContactMechValueMaps) {
    partyContactMechValueMaps.each { partyContactMechValueMap ->
        contactMechPurposes = partyContactMechValueMap.partyContactMechPurposes;
        contactMechPurposes.each { contactMechPurpose ->
            if (contactMechPurpose.contactMechPurposeTypeId.equals("GENERAL_LOCATION")) {
                context.partyGeneralContactMechValueMap = partyContactMechValueMap;
            } else if (contactMechPurpose.contactMechPurposeTypeId.equals("SHIPPING_LOCATION")) {
                context.partyShippingContactMechValueMap = partyContactMechValueMap;
            } else if (contactMechPurpose.contactMechPurposeTypeId.equals("BILLING_LOCATION")) {
                context.partyBillingContactMechValueMap = partyContactMechValueMap;
            } else if (contactMechPurpose.contactMechPurposeTypeId.equals("PAYMENT_LOCATION")) {
                context.partyPaymentContactMechValueMap = partyContactMechValueMap;
            } else if (contactMechPurpose.contactMechPurposeTypeId.equals("PHONE_HOME")) {
                context.partyPhoneHomeContactMechValueMap = partyContactMechValueMap;
            } else if (contactMechPurpose.contactMechPurposeTypeId.equals("PRIMARY_PHONE")) {
                context.partyPrimaryPhoneContactMechValueMap = partyContactMechValueMap;
            } else if (contactMechPurpose.contactMechPurposeTypeId.equals("PRIMARY_EMAIL")) {
                context.partyPrimaryEmailContactMechValueMap = partyContactMechValueMap;
            } else if (contactMechPurpose.contactMechPurposeTypeId.equals("PHONE_MOBILE")) {
                context.partyPhoneMobileContactMechValueMap = partyContactMechValueMap;
            }
        }
    }
}

