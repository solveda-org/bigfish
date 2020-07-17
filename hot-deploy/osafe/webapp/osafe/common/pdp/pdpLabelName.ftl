<li class="${request.getAttribute("attributeClass")!}">
  <#if pdpLabel?has_content>
    <div>
      <label>${uiLabelMap.PDPLabelNameLabel}</label>
      <span>${pdpLabel!""}</span>
    </div>
  </#if>
</li>
