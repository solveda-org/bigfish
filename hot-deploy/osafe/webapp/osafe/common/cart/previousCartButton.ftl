<#assign localPrevButtonVisible = prevButtonVisible!"Y">
<#assign useBrowserBackAction = useBrowserBackAction!"N">
<#if useBrowserBackAction=="Y">
  <#assign localPrevButtonUrl = "javascript:submitCheckoutForm(document.${formName!}, 'BBK', '');">
<#else>
  <#if prevButtonUrl?exists && prevButtonUrl?has_content >
    <#assign localPrevButtonUrl = prevButtonUrl! >
  <#else>
    <#assign localPrevButtonUrl = "javascript:submitCheckoutForm(document.${formName!}, 'BK', '');">
  </#if>
</#if>
<#assign localPrevButtonClass = prevButtonClass!"standardBtn negative">
<#assign localPrevButtonDescription = prevButtonDescription!uiLabelMap.PreviousBtn>
<#if localPrevButtonVisible == "Y">
  <div class="${request.getAttribute("attributeClass")!}">
    <a href="${localPrevButtonUrl}" class="${localPrevButtonClass}"><span>${localPrevButtonDescription}</span></a>
  </div>
</#if>
