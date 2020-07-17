<!-- start shipmentMethodShipList.ftl -->
          <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.ShipmentMethodLabel}</th>
                <th class="descCol">${uiLabelMap.ShipmentDescriptionLabel}</th>
            </tr>
          </thead>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <#list resultList as shipmentMethod>
              <#assign hasNext = shipmentMethod_has_next>
                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">                
                    <td class="idCol <#if !hasNext>lastRow</#if> firstCol" ><a href="<@ofbizUrl>shipmentMethodDetail?shipmentMethodTypeId=${shipmentMethod.shipmentMethodTypeId!""}</@ofbizUrl>">${shipmentMethod.shipmentMethodTypeId!""}</a></td>
                    <td class="descCol <#if !hasNext>lastRow</#if>">${shipmentMethod.description!""}</td>
                </tr>
                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
        </#if>
<!-- end shipmentMethodShipList.ftl -->