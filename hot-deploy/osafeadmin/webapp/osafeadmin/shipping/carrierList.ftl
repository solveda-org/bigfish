<!-- start carrierList.ftl -->
<thead>
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.CarrierIdLabel}</th>
    <th class="descCol">${uiLabelMap.CarrierDescriptionLabel}</th>
    <th class="urlCol">${uiLabelMap.CarrierTrackingUrlLabel}</th>
  </tr>
</thead>
<#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1">
    <#list resultList as carrier>
      <#if partyGroupPartyIds?has_content && partyGroupPartyIds.contains(carrier.partyId)>
        <#assign hasNext = carrier_has_next>
        <#assign trackingURL = ""/>
        <#assign trackingURLPartyContents = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(delegator.findByAnd("PartyContent", {"partyId": carrier.partyId, "partyContentTypeId": "TRACKING_URL"}))/>
        <#if trackingURLPartyContents?has_content>
            <#assign trackingURLPartyContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(trackingURLPartyContents)/>
            <#if trackingURLPartyContent?has_content>
                <#assign content = trackingURLPartyContent.getRelatedOne("Content")/>
                <#if content?has_content>
                    <#assign dataResource = content.getRelatedOne("DataResource")!""/>
                    <#if dataResource?has_content>
                        <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
                        <#assign trackingURL = electronicText.textData!""/>
                    </#if> 
                </#if>
            </#if>
        </#if>
	    <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">                
	      <td class="idCol <#if !hasNext>lastRow</#if> firstCol" ><a href="<@ofbizUrl>carrierDetail?carrierPartyId=${carrier.partyId}&roleTypeId=${carrier.roleTypeId!}</@ofbizUrl>">${carrier.partyId}</a></td>
	      <td class="descCol <#if !hasNext>lastRow</#if>">${carrier.groupName!""}</td>
	      <td class="urlCol <#if !hasNext>lastRow</#if>">${trackingURL!""}</td>
	    </tr>
	    <#-- toggle the row color -->
	    <#if rowClass == "2">
	      <#assign rowClass = "1">
	    <#else>
	      <#assign rowClass = "2">
	    </#if>
      </#if>
    </#list>
</#if>
<!-- end carrierList.ftl -->