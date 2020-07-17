

    <table id="orderSalesHistory" class="standardTable" summary="This table display order sales history.">
      <thead>
        <tr>
          <th class="orderNumber firstCol">${uiLabelMap.OrderOrderNumber}</th>
          <th class="orderDate">${uiLabelMap.OrderDate}</th>
          <th class="orderStatus">${uiLabelMap.CommonStatus}</th>
          <th class="orderTrackingId">${uiLabelMap.TrackingIdLabel}</th>
          <th class="totalPrice lastCol">${uiLabelMap.CommonTotalPrice}</th>
        </tr>
      </thead>
      <tbody>
        <#assign FORMAT_DATE = Static["com.osafe.util.Util"].getProductStoreParm(request,"FORMAT_DATE")!""/>
        <#if orderHeaderList?has_content>
          <#list orderHeaderList as orderHeader>
            <#assign status = orderHeader.getRelatedOneCache("StatusItem") />
            <#assign orderItemShipGroups =  orderHeader.getRelatedCache("OrderItemShipGroup", Static["org.ofbiz.base.util.UtilMisc"].toList("shipGroupSeqId")) />
            <#assign orderItemShipGroupSize = orderItemShipGroups.size()>
            <#assign trackingURL = "">
            <#assign trackingNumber = "">
            <#if orderItemShipGroups?has_content && orderItemShipGroupSize == 1>
                <#list orderItemShipGroups as shipGroup>
                    <#assign trackingNumber = shipGroup.trackingNumber!""/>
                    <#assign findCarrierShipmentMethodMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentMethodTypeId", shipGroup.shipmentMethodTypeId, "partyId", shipGroup.carrierPartyId,"roleTypeId" ,"CARRIER")>
                    <#assign carrierShipmentMethod = delegator.findByPrimaryKeyCache("CarrierShipmentMethod", findCarrierShipmentMethodMap)>
                    <#assign shipmentMethodType = carrierShipmentMethod.getRelatedOneCache("ShipmentMethodType")/>
                    <#assign carrierDescription = shipmentMethodType.description!""/>
                    <#assign carrierPartyGroupName = ""/>
                    <#if shipGroup.carrierPartyId != "_NA_">
                        <#assign carrierParty = carrierShipmentMethod.getRelatedOneCache("Party")/>
                        <#assign carrierPartyGroup = carrierParty.getRelatedOneCache("PartyGroup")/>
                        <#assign carrierPartyGroupName = carrierPartyGroup.groupName/>
                        <#assign trackingURLPartyContents = delegator.findByAndCache("PartyContent", {"partyId": shipGroup.carrierPartyId, "partyContentTypeId": "TRACKING_URL"})/>
                        <#if trackingURLPartyContents?has_content>
                            <#assign trackingURLPartyContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(trackingURLPartyContents)/>
                            <#if trackingURLPartyContent?has_content>
                                <#assign content = trackingURLPartyContent.getRelatedOneCache("Content")/>
                                <#if content?has_content>
                                    <#assign dataResource = content.getRelatedOneCache("DataResource")!""/>
                                    <#if dataResource?has_content>
                                        <#assign electronicText = dataResource.getRelatedOneCache("ElectronicText")!""/>
                                        <#assign trackingURL = electronicText.textData!""/>
                                        <#if trackingURL?has_content>
                                            <#assign trackingURL = Static["org.ofbiz.base.util.string.FlexibleStringExpander"].expandString(trackingURL, {"TRACKING_NUMBER":trackingNumber})/>
                                        </#if>
                                    </#if>
                                </#if>
                            </#if>
                        </#if>
                    </#if>
                </#list><#-- end list of orderItemShipGroups -->
            </#if>
            <tr>
              <td class="orderNumber firstCol">
                <a href="<@ofbizUrl>eCommerceOrderStatus?orderId=${orderHeader.orderId}</@ofbizUrl>">${orderHeader.orderId}</a>
              </td>
              <td class="orderDate">${(Static["com.osafe.util.Util"].convertDateTimeFormat(orderHeader.orderDate, FORMAT_DATE))!"N/A"}</td>
              <td class="orderStatus">${status.get("description",locale)}</td>
              <td class="orderTrackingId">
                <#if orderItemShipGroups?has_content && orderItemShipGroupSize gt 1>
                    <a href="<@ofbizUrl>eCommerceOrderStatus?orderId=${orderHeader.orderId}</@ofbizUrl>">${uiLabelMap.TrackShipmentsLabel}</a>
                <#else>
                    <#if trackingNumber?has_content && orderItemShipGroupSize == 1>
                        <#if trackingURL?has_content><a href="JavaScript:newPopupWindow('${trackingURL!""}');">${trackingNumber!""}</a><#else>${trackingNumber!""}</#if>
                    </#if>
                </#if>
              </td>
              <td class="totalPrice total lastCol"><@ofbizCurrency amount=orderHeader.grandTotal isoCode=orderHeader.currencyUom rounding=globalContext.currencyRounding/></td>
            </tr>
          </#list>
        <#else>
          <tr><td colspan="4">${uiLabelMap.OrderNoOrderFound}</td></tr>
        </#if>
      </tbody>
    </table>
    <div class="backButton">
      <#if backAction?exists && backAction?has_content>
          <a class="standardBtn negative" href="<@ofbizUrl>${backAction!}</@ofbizUrl>">${uiLabelMap.CommonBack}</a>
      </#if>    
    </div>
