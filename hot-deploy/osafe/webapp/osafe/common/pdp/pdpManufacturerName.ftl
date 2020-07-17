<li class="${request.getAttribute("attributeClass")!}">
  <#if pdpManufacturerProfileName?has_content>
    <div>
      <label>${uiLabelMap.PDPManufacturerNameLabel}</label>
      <a href="<@ofbizUrl>eCommerceManufacturerDetail?manufacturerPartyId=${manufacturerPartyId}</@ofbizUrl>"><span>${pdpManufacturerProfileName!""}</span></a>
    </div>
  </#if>
</li>
