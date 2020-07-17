<#assign plpLongDescription = StringUtil.wrapString(plpLongDescription!"") />
<#if plpLongDescription?has_content>
<li class="${request.getAttribute("attributeClass")!}">
 <div>
  <label>${uiLabelMap.PLPLongDescriptionLabel}</label>
  <span>${plpLongDescription!}</span>
 </div>
</li>   
</#if>
