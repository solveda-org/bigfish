<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div id="eCommerceEnterPromoCode">
    ${uiLabelMap.EnterPromoCodeLabel}
    <input type="text" id="manualOfferCode" name="manualOfferCode" value="${requestParameters.UofferCode!""}" maxlength="20"/>
    <a class="standardBtn action" href="javascript:addManualPromoCode();">${uiLabelMap.ApplyOfferBtn}</a>
    <#if isCheckoutPage?exists && isCheckoutPage! == "true">
        <@fieldErrors fieldName="productPromoCodeId"/>
    </#if>
</div>
