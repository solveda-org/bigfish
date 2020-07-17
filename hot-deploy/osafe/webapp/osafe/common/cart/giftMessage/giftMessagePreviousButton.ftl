<#-- Previous button -->
<#assign localPrevButtonVisible = prevButtonVisible!"Y">
<#assign localPrevButtonUrl = prevButtonUrl!"javascript:history.go(-1)" >
<#assign localPrevButtonClass = prevButtonClass!"standardBtn negative">
<#assign localPrevButtonDescription = prevButtonDescription!uiLabelMap.PreviousBtn>
<#if localPrevButtonVisible == "Y">
 <div class="${request.getAttribute("attributeClass")!}">
  <a href="${localPrevButtonUrl}" class="${localPrevButtonClass}"><span>${localPrevButtonDescription}</span></a>
 </div>
</#if>
<#-- End of Previous button -->