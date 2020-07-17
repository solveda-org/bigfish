<li class="${request.getAttribute("attributeClass")!}">
  <#if pdpManufacturerProfileName?has_content>
    <div>
      <a href="<@ofbizUrl>eCommerceManufacturerDetail?manufacturerPartyId=${manufacturerPartyId}</@ofbizUrl>"><span>${pdpManufacturerProfileName!""}</span></a>
    </div>
  </#if>
</li>
