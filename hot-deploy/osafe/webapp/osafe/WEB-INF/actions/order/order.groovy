package common;

import java.math.BigDecimal;
import java.util.List;

import org.ofbiz.base.util.UtilValidate;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import com.osafe.util.Util;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.Delegator;
import org.ofbiz.base.util.string.FlexibleStringExpander;

orderHeader = request.getAttribute("orderHeader");
status = orderHeader.getRelatedOneCache("StatusItem");
orderItemShipGroups =  orderHeader.getRelatedCache("OrderItemShipGroup", UtilMisc.toList("shipGroupSeqId"));
orderItemShipGroupSize = orderItemShipGroups.size();
rowClass = request.getAttribute("rowClass");
lineIndex = request.getAttribute("lineIndex");
trackingURL = "";
trackingNumber = "";
if (UtilValidate.isNotEmpty(orderItemShipGroups) && orderItemShipGroupSize == 1)
 {
    for (GenericValue shipGroup : orderItemShipGroups)
    {
        trackingNumber = shipGroup.trackingNumber;
        findCarrierShipmentMethodMap = UtilMisc.toMap("shipmentMethodTypeId", shipGroup.shipmentMethodTypeId, "partyId", shipGroup.carrierPartyId,"roleTypeId" ,"CARRIER");
        carrierShipmentMethod = delegator.findByPrimaryKeyCache("CarrierShipmentMethod", findCarrierShipmentMethodMap);
        carrierDescription = "";
        if (UtilValidate.isNotEmpty(carrierShipmentMethod))
        {
            shipmentMethodType = carrierShipmentMethod.getRelatedOneCache("ShipmentMethodType");
            if (UtilValidate.isNotEmpty(shipmentMethodType))
            {
              carrierDescription = shipmentMethodType.description;
            }
        	
        }
        carrierPartyGroupName = "";
        if (UtilValidate.isNotEmpty(shipGroup.carrierPartyId) && shipGroup.carrierPartyId != "_NA_")
        {
            carrierParty = carrierShipmentMethod.getRelatedOneCache("Party");
            carrierPartyGroup = carrierParty.getRelatedOneCache("PartyGroup");
            carrierPartyGroupName = carrierPartyGroup.groupName;
            trackingURLPartyContents = delegator.findByAndCache("PartyContent",UtilMisc.toMap("partyId",shipGroup.carrierPartyId,"partyContentTypeId","TRACKING_URL"));
            if (UtilValidate.isNotEmpty(trackingURLPartyContents))
            {
                trackingURLPartyContent = EntityUtil.getFirst(trackingURLPartyContents);
                if (UtilValidate.isNotEmpty(trackingURLPartyContent))
                {
                    content = trackingURLPartyContent.getRelatedOneCache("Content");
                    if (UtilValidate.isNotEmpty(content))
                    {
                        dataResource = content.getRelatedOneCache("DataResource");
                        if (UtilValidate.isNotEmpty(dataResource))
                        {
                            electronicText = dataResource.getRelatedOneCache("ElectronicText");
                            trackingURL = electronicText.textData;
                            if (UtilValidate.isNotEmpty(trackingURL))
                            {
                                trackingURL = FlexibleStringExpander.expandString(trackingURL, UtilMisc.toMap("TRACKING_NUMBER",trackingNumber));
                            }
                        }
                    }
                }
            }
        }
    }
 }




context.orderHeader = orderHeader;
context.status = status;
context.orderItemShipGroups = orderItemShipGroups;
context.orderItemShipGroupSize = orderItemShipGroupSize;
context.trackingURL = trackingURL;
context.trackingNumber = trackingNumber;
context.lineIndex = lineIndex;
context.rowClass = rowClass;



