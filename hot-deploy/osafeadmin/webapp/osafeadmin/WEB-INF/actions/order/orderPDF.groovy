package order;

import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.order.order.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.accounting.payment.*;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import com.osafe.util.OsafeAdminUtil;

orderId = parameters.orderId;
userLogin = session.getAttribute("userLogin");

session = context.session;
svcCtx = session.getAttribute("orderPDFMap");
if (UtilValidate.isEmpty(svcCtx)) {
    Map<String, Object> svcCtx = FastMap.newInstance();
}

if (UtilValidate.isNotEmpty(orderId)) {
    svcCtx.put("orderId", orderId);
}

if (UtilValidate.isNotEmpty(svcCtx)) {
    svcCtx.put("viewSize",  Integer.valueOf("1000"));
    svcCtx.put("showAll", "Y");
    Map<String, Object> svcRes = dispatcher.runSync("searchOrders", svcCtx);
    List<GenericValue> orderPDFList = UtilGenerics.checkList(svcRes.get("completeOrderList"), GenericValue.class);
    context.ordersList = orderPDFList;
    if (UtilValidate.isNotEmpty(orderPDFList)) {
        if (UtilValidate.isNotEmpty(orderPDFName)) {
            orderPDFName = orderPDFName+(OsafeAdminUtil.convertDateTimeFormat(UtilDateTime.nowTimestamp(), "yyyy-MM-dd-HHmm"));
            response.setHeader("Content-Disposition","attachment; filename=\"" + UtilValidate.stripWhitespace(orderPDFName) + ".pdf" + "\";");
        }
        Map<String, Object> upOrderHeaderCtx = FastMap.newInstance();
        upOrderHeaderCtx.put("userLogin",userLogin);
        upOrderHeaderCtx.put("isDownloaded","Y");
        upOrderHeaderCtx.put("datetimeDownloaded",UtilDateTime.nowTimestamp());
        


        storePickupMap = [:];
        for(GenericValue orderHeader : orderPDFList) {
        
            orderAttrIsDownloaded = delegator.findOne("OrderAttribute", ["orderId" : orderHeader.orderId, "attrName" : "IS_DOWNLOADED"], true);
        
            Map<String, Object> isDownloadedOrderAttrCtx = FastMap.newInstance();
            isDownloadedOrderAttrCtx.put("orderId", orderHeader.orderId);
            isDownloadedOrderAttrCtx.put("userLogin",userLogin);
            isDownloadedOrderAttrCtx.put("attrName","IS_DOWNLOADED");
            isDownloadedOrderAttrCtx.put("attrValue","Y");
            Map<String, Object> isDownloadOrderAttrMap = null;
            if (UtilValidate.isNotEmpty(orderAttrIsDownloaded)) {
                isDownloadOrderAttrMap = dispatcher.runSync("updateOrderAttribute", isDownloadedOrderAttrCtx);
            } else {
               isDownloadOrderAttrMap = dispatcher.runSync("createOrderAttribute", isDownloadedOrderAttrCtx);
            }
        
            orderAttrDateTimeDownloaded = delegator.findOne("OrderAttribute", ["orderId" : orderHeader.orderId, "attrName" : "DATETIME_DOWNLOADED"], true);
            Map<String, Object> dateTimeDownloadedOrderAttrCtx = FastMap.newInstance();
            dateTimeDownloadedOrderAttrCtx.put("orderId", orderHeader.orderId);
            dateTimeDownloadedOrderAttrCtx.put("userLogin",userLogin);
            dateTimeDownloadedOrderAttrCtx.put("attrName","DATETIME_DOWNLOADED");
            dateTimeDownloadedOrderAttrCtx.put("attrValue",UtilDateTime.nowTimestamp().toString());
            Map<String, Object> dateTimeDownloadedOrderAttrMap = null;
            if (UtilValidate.isNotEmpty(orderAttrDateTimeDownloaded)) {
                dateTimeDownloadedOrderAttrMap = dispatcher.runSync("updateOrderAttribute", dateTimeDownloadedOrderAttrCtx);
            } else {
                dateTimeDownloadedOrderAttrMap = dispatcher.runSync("createOrderAttribute", dateTimeDownloadedOrderAttrCtx);
            }
        
            //upOrderHeaderCtx.put("orderId",orderHeader.orderId);
            //Map<String, Object> resultMap = dispatcher.runSync("updateOrderHeader", upOrderHeaderCtx);

            storeId = "";
            orderPickupDetailMap = [:];
            orderDeliveryOptionAttr = delegator.findOne("OrderAttribute", [orderId : orderHeader.orderId, attrName : "DELIVERY_OPTION"], true);
            if (UtilValidate.isNotEmpty(orderDeliveryOptionAttr) && orderDeliveryOptionAttr.attrValue == "STORE_PICKUP") {
                orderPickupDetailMap.isStorePickup = "Y";
                orderStoreLocationAttr = delegator.findOne("OrderAttribute", [orderId : orderHeader.orderId, attrName : "STORE_LOCATION"], true);
                if (UtilValidate.isNotEmpty(orderStoreLocationAttr)) {
                    storeId = orderStoreLocationAttr.attrValue;
                }
            }

            if (UtilValidate.isNotEmpty(storeId)) {
                orderPickupDetailMap.storeId = storeId;
                store = delegator.findOne("Party", [partyId : storeId], true);
                orderPickupDetailMap.store = store;
                storeInfo = delegator.findOne("PartyGroup", [partyId : storeId], true);
                if (UtilValidate.isNotEmpty(storeInfo)) {
                    orderPickupDetailMap.storeInfo = storeInfo;
                }
                
                partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, storeId, false);
                if (UtilValidate.isNotEmpty(partyContactMechValueMaps)) {
                    partyContactMechValueMaps.each { partyContactMechValueMap ->
                        contactMechPurposes = partyContactMechValueMap.partyContactMechPurposes;
                        contactMechPurposes.each { contactMechPurpose ->
                            if (contactMechPurpose.contactMechPurposeTypeId.equals("GENERAL_LOCATION")) {
                                orderPickupDetailMap.storeContactMechValueMap = partyContactMechValueMap;
                            }
                        }
                    }
                }
            }
            storePickupMap.put(orderHeader.orderId, orderPickupDetailMap);
        }
        context.storePickupMap = storePickupMap;
    }
}

