<thead>
  <tr class="heading">
     <th class="nameCol">${uiLabelMap.ShipmentGatewayConfigIDCaption}</th>
     <th class="desCol">${uiLabelMap.Description}</th>
  </tr>
</thead>
<#if shipmentGatewayConfigList?exists && shipmentGatewayConfigList?has_content>
<#assign rowClass = "1" />
<#list shipmentGatewayConfigList as shipmentGatewayConfig>
  <#assign hasNext = shipmentGatewayConfig_has_next>
  <#assign description = shipmentGatewayConfig.description!"" />
  
  <#assign shipmentGatewayConfigId = shipmentGatewayConfig.shipmentGatewayConfigId!"" />
  <#if shipmentGatewayConfigId?has_content>
     <#assign shipmentGatewayConfigType = delegator.findOne("ShipmentGatewayConfigType", {"shipmentGatewayConfTypeId" : shipmentGatewayConfig.shipmentGatewayConfTypeId}, false) />
  </#if>
  <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">                
    <td class="idCol<#if !hasNext> lastRow</#if> firstCol" ><#if shipmentGatewayConfigId?has_content><a href="<@ofbizUrl>shipmentGatewaysDetails?shipmentGatewayConfigId=${shipmentGatewayConfigId}</@ofbizUrl>">${shipmentGatewayConfigId}</a></#if></td>
    <td class="desCol<#if !hasNext> lastRow</#if> lastCol"><#if shipmentGatewayConfigType?has_content>${shipmentGatewayConfigType.description}</#if></td>
  </tr>
  <#-- toggle the row color -->
  <#if rowClass == "2">
  <#assign rowClass = "1">
  <#else>
    <#assign rowClass = "2">
  </#if>
</#list>
</#if>
      




