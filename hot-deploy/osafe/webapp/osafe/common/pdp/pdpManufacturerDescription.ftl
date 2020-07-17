<li class="${request.getAttribute("attributeClass")!}">
  <#if pdpManufacturerDescription?has_content>
    <div>
      <p>${pdpManufacturerDescription!""}</p>
    </div>
  </#if>
</li>
