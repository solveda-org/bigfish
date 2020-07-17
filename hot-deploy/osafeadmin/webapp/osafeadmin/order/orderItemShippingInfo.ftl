<!-- start orderItemShippingInfo.ftl -->
<div class="infoRow firstRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.ShipDateCaption}</label>
     </div>
     <#if orderItemShipDate?has_content>
          <#assign orderItemShipDate = orderItemShipDate?string(preferredDateFormat)!""/>
      </#if>
     <div class="infoValue medium">
       <#if orderItemShipDate?has_content>${orderItemShipDate!""}</#if>
     </div>
     <div class="infoCaption">
      <label>${uiLabelMap.CarrierCaption}</label>
     </div>
     <div class="infoValue">
     	<span><#if orderItemCarrier?has_content>${orderItemCarrier!}</#if></span>
     </div>
   </div>
</div>
<#-- Fetching the carrier tracking URL -->
<#assign trackingURLPartyContents = delegator.findByAnd("PartyContent", {"partyId": carrierPartyId, "partyContentTypeId": "TRACKING_URL"})/>
<#if trackingURLPartyContents?has_content>
    <#assign trackingURLPartyContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(trackingURLPartyContents)/>
    <#if trackingURLPartyContent?has_content>
        <#assign content = trackingURLPartyContent.getRelatedOne("Content")/>
        <#if content?has_content>
            <#assign dataResource = content.getRelatedOne("DataResource")!""/>
            <#if dataResource?has_content>
                <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
                <#assign trackingURL = electronicText.textData!""/>
                <#if trackingURL?has_content>
                    <#assign trackingURL = Static["org.ofbiz.base.util.string.FlexibleStringExpander"].expandString(trackingURL, {"TRACKING_NUMBER":orderItemTrackingNo})/>
                    <#assign orderShipmentIconVisible= "true"/>
                </#if>
            </#if>
        </#if>
    </#if>
</#if>
<div class="infoRow firstRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.TrackingNoCaption}</label>
     </div>
     <div class="infoValue medium">
       <#if orderItemTrackingNo?has_content><p>${orderItemTrackingNo!""}</p></#if>
       <#--
       <a href="<@ofbizUrl>orderShipmentDetail?orderId=${parameters.orderId}</@ofbizUrl>"><span class="shipmentDetailIcon"></span></a>
       -->
       <#if trackingURL?has_content><a href="JavaScript:newPopupWindow('${trackingURL!""}');" ><span class="shipmentDetailIcon"></span></a></#if>
     </div>
   </div>
</div>

<!-- end orderItemShippingInfo.ftl -->


