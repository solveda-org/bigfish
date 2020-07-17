    <#assign localPrevButtonVisible = prevButtonVisible!"Y">
    <#if prevButtonUrl?exists && prevButtonUrl?has_content >
      <#assign localPrevButtonUrl = prevButtonUrl! >
    <#else>
      <#assign localPrevButtonUrl = "javascript:submitCheckoutForm(document.${formName!}, 'BK', '');">
    </#if>
    <#assign localPrevButtonClass = prevButtonClass!"standardBtn negative">
    <#assign localPrevButtonDescription = prevButtonDescription!uiLabelMap.PreviousBtn>
    <#if localPrevButtonVisible == "Y">
     <div class="previousButton">
      <a href="${localPrevButtonUrl}" class="${localPrevButtonClass}"><span>${localPrevButtonDescription}</span></a>
     </div>
    </#if>

