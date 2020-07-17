<!-- start carrierShipList.ftl -->
          <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.CarrierShipIdLabel}</th>
                <th class="nameCol">${uiLabelMap.ShipMethodTypeLabel}</th>
                <th class="nameCol">${uiLabelMap.CarrierServCodeLabel}</th>
                <th class="descCol">${uiLabelMap.CarrierShipMessageLabel}</th>
                <th class="seqCol">${uiLabelMap.ShipSeqLabel}</th>
            </tr>
          </thead>
          <#assign roleTypeId = "CARRIER">
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <#list resultList as shipCharge>
              <#assign hasNext = shipCharge_has_next>
                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">                
                    <td class="idCol <#if !hasNext>lastRow</#if> firstCol" ><a href="<@ofbizUrl>carrierShipDetail?partyId=${shipCharge.partyId}&shipmentMethodTypeId=${shipCharge.shipmentMethodTypeId}&roleTypeId=${roleTypeId}</@ofbizUrl>">${shipCharge.partyId}</a></td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${shipCharge.shipmentMethodTypeId!""}</td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${shipCharge.carrierServiceCode!""}</td>
                    <td class="descCol <#if !hasNext>lastRow</#if>">${shipCharge.optionalMessage!""}</td>
                    <td class="seqCol <#if !hasNext>lastRow</#if>">${shipCharge.sequenceNumber!""}</td>
                </tr>
                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
        </#if>
<!-- end carrierShipList.ftl -->