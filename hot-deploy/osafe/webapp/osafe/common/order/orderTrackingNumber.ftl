<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <label>${uiLabelMap.TrackingNumberCaption}</label>
    <#if orderItemShipGroups?has_content && orderItemShipGroupSize gt 1>
            <a href="<@ofbizUrl>eCommerceOrderDetail?orderId=${orderHeader.orderId}</@ofbizUrl>"><span>${uiLabelMap.TrackShipmentsLabel}</span></a>
    <#else>
      <#if trackingNumber?has_content && orderItemShipGroupSize == 1>
         <#if trackingURL?has_content>
           <a href="JavaScript:newPopupWindow('${trackingURL!""}');"><span>${trackingNumber!""}</span></a>
         <#else>
           <span>${trackingNumber!""}</span>
         </#if>
      <#else>
           <span>${uiLabelMap.NotApplicableLabel}</span>
      </#if>
    </#if>
  </div>
</li>