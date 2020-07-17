<!-- start listBox -->
    <table class="osafe">
        <tr class="heading">
            <th class="nameCol firstCol">${uiLabelMap.ShipGroupLabel}</th>
            <th class="dateCol">${uiLabelMap.ShipDateLabel}</th>
            <th class="descCol">${uiLabelMap.ShipMethodLabel}</th>
            <th class="numCol lastCol">${uiLabelMap.ShipTrackingNumberLabel}</th>
        </tr>
        <#assign rowClass = "1"/>
        <#if shipGroups?has_content>
            <tbody>
                <#list shipGroups as shipGroup>
                    <tr class="dataRow <#if rowClass == "2">even<#else>odd></#if>">
                        <#assign shipmentMethodType = shipGroup.getRelatedOne("ShipmentMethodType")?if_exists>
                        <td class="nameCol firstCol">
                            <a href="<@ofbizUrl>orderShippingDetail?orderId=${parameters.orderId}</@ofbizUrl>">${shipGroup.shipGroupSeqId!""}
                        </td>
                        <td class="dateCol firstCol">${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(shipGroup.estimatedShipDate, preferredDateFormat).toLowerCase())!"N/A"}</td>
                        <td class="descCol">
                            <#if shipGroup.carrierPartyId?has_content>
                                <#assign carrier =  delegator.findByPrimaryKey("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", shipGroup.carrierPartyId))?if_exists />
                                <#if carrier?has_content>${carrier.groupName?default(carrier.partyId)!}&nbsp;</#if>
                            </#if>
                            <#if shipmentMethodType?has_content>
                               ${shipmentMethodType.get("description","OSafeAdminUiLabels",locale)?default("")}
                            </#if>
                        </td>
                        <td class="numCol lastCol">
                            ${shipGroup.trackingNumber!}
                            <a href="<@ofbizUrl>orderShippingDetail?orderId=${parameters.orderId}</@ofbizUrl>"><span class="shipmentDetailIcon"></span></a>
                        </td>
                    </tr>

                    <#-- toggle the row color -->
                    <#if rowClass == "2">
                        <#assign rowClass = "1">
                    <#else>
                        <#assign rowClass = "2">
                    </#if>
                </#list>
            </tbody>
        <#else>
            <tbody>
                 ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
            </tbody>
        </#if>
    </table>

<!-- end listBox -->