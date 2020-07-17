package common;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.store.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastList;

//String showThankYouStatus = parameters.showThankYouStatus;
String showThankYouStatus = context.showThankYouStatus;
if (UtilValidate.isEmpty(showThankYouStatus)){
    context.showThankYouStatus ="N"
}

if("Y".equals (showThankYouStatus)) {

    ResourceBundle orderNumberCaptionBundle = UtilProperties.getResourceBundle ("OSafeUiLabels", locale);
    String orderNumberCaption = orderNumberCaptionBundle.getString("OrderNumberCaption");
    messageMap=[:];
    messageMap.put("orderNumberCaption", orderNumberCaption);
    messageMap.put("orderId", context.orderHeader["orderId"]);

    context.OrderCompleteInfoMapped = UtilProperties.getMessage("OSafeUiLabels","OrderCompleteInfo",messageMap, locale )
}

party = context.party;
partyId = context.partyId;
if (UtilValidate.isEmpty(partyId)) {
    if (UtilValidate.isNotEmpty(userLogin)) {
        party = userLogin.getRelatedOne("Party");
        partyId = party.partyId;
    }
} else {
    party = delegator.findOne("Party", [partyId : partyId], true);
}

shippingContactMechList = ContactHelper.getContactMech(party, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
shippingContactMechPhoneMap = [:];
for (GenericValue contactMech : shippingContactMechList){
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
    shippingContactMechPhoneMap[contactMechIdFrom] = phoneNumberMap;
}
context.shippingContactMechPhoneMap = shippingContactMechPhoneMap;

creditCardTypes = delegator.findByAnd("Enumeration", [enumTypeId : "CREDIT_CARD_TYPE"], ["sequenceId"]);
creditCardTypesMap = [:];
for (GenericValue creditCardType :  creditCardTypes) {
    creditCardTypesMap[creditCardType.enumCode] = creditCardType.description;
}

context.creditCardTypesMap = creditCardTypesMap;

