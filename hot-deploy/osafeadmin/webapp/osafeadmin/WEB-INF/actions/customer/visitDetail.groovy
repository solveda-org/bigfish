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

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import com.ibm.icu.util.Calendar;

import org.ofbiz.base.util.ObjectType;

userLogin = session.getAttribute("userLogin");
visitId = StringUtils.trimToEmpty(parameters.visitId);
userLoginId = StringUtils.trimToEmpty(parameters.userLoginId);

context.visitId=visitId;
context.userLoginId=userLoginId;
partyId= "";



visit = delegator.findByPrimaryKey("Visit", [visitId : visitId]);
context.visit = visit;
context.nowStr = UtilDateTime.nowTimestamp().toString();

if (visit) {
	if(visit.partyId){
		partyId= visit.partyId;
		party = delegator.findByPrimaryKey("Party", [partyId : visit.partyId]);
		context.party = party;
	}
}

if (visit) {
	if(visit.partyId){
		partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, visit.partyId, false);
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
	}
}

messageMap=[:];
messageMap.put("partyId", partyId);

context.generalInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","CustomerDetailInfoHeading",messageMap, locale )



