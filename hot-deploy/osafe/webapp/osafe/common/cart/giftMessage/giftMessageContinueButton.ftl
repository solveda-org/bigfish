<#-- Continue button -->
<#assign localNextButtonVisible = nextButtonVisible!"Y">
<#assign localNextButtonUrl = nextButtonUrl!"javascript:submitCheckoutForm(document.${formName!}, 'GM', '');">
<#assign localNextButtonClass = nextButtonClass!"standardBtn positive">
<#assign localNextButtonDescription = nextButtonDescription!uiLabelMap.ContinueBtn>

<#if localNextButtonVisible == "Y">
 <div class="${request.getAttribute("attributeClass")!}">
  <a href="${localNextButtonUrl}" class="${localNextButtonClass}"><span>${localNextButtonDescription}</span></a>
 </div>
</#if>
<#-- End of Continue button -->