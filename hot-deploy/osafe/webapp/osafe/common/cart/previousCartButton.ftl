    <#assign localPrevButtonVisible = prevButtonVisible!"Y">
    <#assign localPrevButtonUrl = prevButtonUrl!"javascript:submitCheckoutForm(document.${formName!}, 'BK', '');">
    <#assign localPrevButtonClass = prevButtonClass!"standardBtn negative">
    <#assign localPrevButtonDescription = prevButtonDescription!uiLabelMap.PreviousBtn>
    <#if localPrevButtonVisible == "Y">
     <div class="previousButton">
      <a href="${localPrevButtonUrl}" class="${localPrevButtonClass}"><span>${localPrevButtonDescription}</span></a>
     </div>
    </#if>

