<!-- start carrierShipList.ftl -->
<thead>
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.CarrierIdLabel}</th>
    <th class="descCol">${uiLabelMap.CarrierDescriptionLabel}</th>
  </tr>
</thead>
<#assign roleTypeId = "CARRIER">
<#if resultList?exists && resultList?has_content>
  <#assign rowClass = "1">
  <#list resultList as carrier>
    <#assign hasNext = carrier_has_next>
    <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">                
      <td class="idCol <#if !hasNext>lastRow</#if> firstCol" ><a href="<@ofbizUrl>carrierDetail?carrierPartyId=${carrier.partyId}&roleTypeId=${roleTypeId}</@ofbizUrl>">${carrier.partyId}</a></td>
      <td class="descCol <#if !hasNext>lastRow</#if>">${carrier.groupName!""}</td>
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