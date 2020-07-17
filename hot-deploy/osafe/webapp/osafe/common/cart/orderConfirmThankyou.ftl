<#if orderHeader?has_content>
  <#if showThankYouStatus?exists && showThankYouStatus == "Y">
    <div class="orderConfirmThankyou">
        <p class="instructions">${OrderCompleteInfoMapped!""}</p>
    </div>
  </#if>
</#if>    
