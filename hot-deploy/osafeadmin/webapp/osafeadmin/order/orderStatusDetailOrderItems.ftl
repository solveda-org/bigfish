<table class="osafe">
    <tr class="heading">
        <th class="actionOrderCol firstCol selectOrderItem">
            <!--<input type="checkbox" class="checkBoxEntry" name="orderItemSeqIdall" id="orderItemSeqIdall" value="Y" onclick="javascript:setCheckboxes('${detailFormName!""}','orderItemSeqId')"  <#if parameters.orderItemSeqIdall?has_content>checked</#if>/>-->
        </th>
        <th class="idCol">${uiLabelMap.ProductNoLabel}</th>
        <th class="itemCol">${uiLabelMap.ItemNoLabel}</th>
        <th class="nameCol">${uiLabelMap.ProductNameLabel}</th>
        <th class="statusCol">${uiLabelMap.ItemStatusLabel}</th>
        <th class="nameCol">${uiLabelMap.CarrierLabel}</th>
        <th class="dateCol">${uiLabelMap.ShipDateLabel}</th>
        <th class="nameCol lastCol">${uiLabelMap.TrackingLabel}</th>
    </tr>
    <#if orderItems?exists && orderItems?has_content>
        <#assign rowClass = "1">
        <#assign rowNo = 0/>
        <#list orderItems as orderItem>
            <#assign productId = orderItem.productId?if_exists>
            <#assign itemProduct = orderItem.getRelatedOne("Product")/>
            <#assign itemStatus = orderItem.getRelatedOne("StatusItem")/>
            <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(itemProduct,request)>
            <#assign productName = productContentWrapper.get("PRODUCT_NAME")!itemProduct.productName!"">
            <#assign shipGroupAssoc = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("OrderItemShipGroupAssoc", {"orderId": orderItem.orderId, "orderItemSeqId": orderItem.orderItemSeqId}))/>
            <#if shipGroupAssoc?has_content>
                <#assign shipGroup = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("OrderItemShipGroup", {"orderId": orderItem.orderId, "shipGroupSeqId": shipGroupAssoc.shipGroupSeqId}))/>
                <#if shipGroup?has_content>
                    <#assign shipDate = ""/>
                    <#assign orderHeader = delegator.findByPrimaryKeyCache("OrderHeader", {"orderId": orderItem.orderId})/>
                    <#if orderHeader?has_content && (orderHeader.statusId == "ORDER_COMPLETED" || orderItem.statusId == "ITEM_COMPLETED") >
                        <#assign shipDate = shipGroup.estimatedShipDate!""/>
                        <#if shipDate?has_content>
                            <#assign shipDate = shipDate?string(preferredDateFormat)!""/>
                        </#if>
                    </#if>
                    <#assign trackingNumber = shipGroup.trackingNumber!""/>
                    <#assign findCarrierShipmentMethodMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentMethodTypeId", shipGroup.shipmentMethodTypeId, "partyId", shipGroup.carrierPartyId,"roleTypeId" ,"CARRIER")>
                    <#assign carrierShipmentMethod = delegator.findByPrimaryKeyCache("CarrierShipmentMethod", findCarrierShipmentMethodMap)>
                    <#assign shipmentMethodType = carrierShipmentMethod.getRelatedOne("ShipmentMethodType")/>
                    <#assign description = shipmentMethodType.description!""/>
                    <#assign carrierPartyGroupName = ""/>
                    <#if shipGroup.carrierPartyId != "_NA_">
                        <#assign carrierParty = carrierShipmentMethod.getRelatedOne("Party")/>
                        <#assign carrierPartyGroup = carrierParty.getRelatedOne("PartyGroup")/>
                        <#assign carrierPartyGroupName = carrierPartyGroup.groupName/>
                    </#if>
                </#if>
            </#if>
            <tr class="dataRow <#if rowClass == "2">even<#else>odd></#if>">
                <td class="actionOrderCol <#if !orderItem_has_next>lastRow</#if> firstCol selectOrderItem">
                    <input type="checkbox" class="checkBoxEntry" name="orderItemSeqId-${orderItem_index}" id="orderItemSeqId-${rowNo}" value="${orderItem.orderItemSeqId!}" <#if parameters.get("orderItemSeqId-${rowNo}")?has_content>checked</#if>/>
                </td>
                <td class="idCol <#if !orderItem_has_next>lastRow</#if>"><a href="<@ofbizUrl>productDetail?productId=${itemProduct.productId?if_exists}</@ofbizUrl>">${itemProduct.productId!"N/A"}</a></td>
                <td class="itemCol <#if !orderItem_has_next>lastRow</#if>">${itemProduct.internalName!itemProduct.productId!""}</td>
                <td class="nameCol <#if !orderItem_has_next>lastRow</#if>"><#if (productName?has_content) && (productName?length gt 0)>${productName!}<#else>${itemProduct.internalName!itemProduct.productId!""}</#if></td>
                <td class="statusCol <#if !orderItem_has_next>lastRow</#if>">${itemStatus.get("description",locale)}</td>
                <td class="nameCol <#if !orderItem_has_next>lastRow</#if>">${carrierPartyGroupName!} ${description!}</td>
                <td class="dateCol <#if !orderItem_has_next>lastRow</#if>">${shipDate!}</td>
                <td class="nameCol <#if !orderItem_has_next>lastRow</#if> lastCol">${trackingNumber!}</td>
            </tr>
            <#-- toggle the row color -->
            <#if rowClass == "2">
                <#assign rowClass = "1">
            <#else>
                <#assign rowClass = "2">
            </#if>
            <#assign rowNo = rowNo+1/>
        </#list>
    </#if>
</table>