<#-- Previous button -->
<#assign localPrevButtonVisible = prevButtonVisible!"Y">
<#if prevButtonUrl?exists && prevButtonUrl?has_content >
  <#assign localPrevButtonUrl = prevButtonUrl! >
<#else>
  <#assign localPrevButtonUrl = "javascript:history.go(-1)">
</#if>
<#assign localPrevButtonClass = prevButtonClass!"standardBtn negative">
<#assign localPrevButtonDescription = prevButtonDescription!uiLabelMap.PreviousBtn>
<div class="${request.getAttribute("attributeClass")!}">
    <a href="${localPrevButtonUrl}" class="${localPrevButtonClass}"><span>${localPrevButtonDescription}</span></a>
</div>
<#-- End of Previous button -->