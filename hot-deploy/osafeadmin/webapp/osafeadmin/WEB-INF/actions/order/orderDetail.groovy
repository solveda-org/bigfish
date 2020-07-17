package order;

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
import org.ofbiz.base.util.UtilValidate;

userLogin = session.getAttribute("userLogin");
orderId = StringUtils.trimToEmpty(parameters.orderId);
if(orderId && security.hasEntityPermission('SPER_ORDER_MGMT', '_VIEW', session)){
    messageMap=[:];
    messageMap.put("orderId", orderId);

    context.orderId=orderId;
    context.pageTitle = UtilProperties.getMessage("OSafeAdminUiLabels","OrderManagementOrderDetailTitle",messageMap, locale )
    context.generalInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","OrderDetailInfoHeading",messageMap, locale )
    context.orderNoteInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","OrderNoteHeading",messageMap, locale )

    pagingListSize=orderItems.size();
    context.pagingListSize=pagingListSize;
    context.pagingList = orderItems;


    storeId = "";
    orderDeliveryOptionAttr = delegator.findOne("OrderAttribute", [orderId : orderHeader.orderId, attrName : "DELIVERY_OPTION"], true);
    if (UtilValidate.isNotEmpty(orderDeliveryOptionAttr) && orderDeliveryOptionAttr.attrValue == "STORE_PICKUP") {
        context.isStorePickup = "Y";
        orderStoreLocationAttr = delegator.findOne("OrderAttribute", [orderId : orderHeader.orderId, attrName : "STORE_LOCATION"], true);
        if (UtilValidate.isNotEmpty(orderStoreLocationAttr)) {
            storeId = orderStoreLocationAttr.attrValue;
        }
    }

    if (UtilValidate.isNotEmpty(storeId)) {
        context.storeId = storeId;
        store = delegator.findOne("Party", [partyId : storeId], true);
        context.store = store;
        storeInfo = delegator.findOne("PartyGroup", [partyId : storeId], true);
        if (UtilValidate.isNotEmpty(storeInfo)) {
            context.storeInfo = storeInfo;
        }
        
        partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, storeId, false);
        if (UtilValidate.isNotEmpty(partyContactMechValueMaps)) {
            partyContactMechValueMaps.each { partyContactMechValueMap ->
                contactMechPurposes = partyContactMechValueMap.partyContactMechPurposes;
                contactMechPurposes.each { contactMechPurpose ->
                    if (contactMechPurpose.contactMechPurposeTypeId.equals("GENERAL_LOCATION")) {
                        context.storeContactMechValueMap = partyContactMechValueMap;
                    }
                }
            }
        }
    }



}