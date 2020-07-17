<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div class="${request.getAttribute("attributeClass")!}">
    ${uiLabelMap.EnterPromoCodeLabel}
    <input type="text" id="js_manualOfferCode" name="manualOfferCode" value="${requestParameters.UofferCode!""}" maxlength="20"/>
    <a class="standardBtn action" href="javascript:addManualPromoCode();">${uiLabelMap.ApplyOfferBtn}</a>
    <#if isCheckoutPage?exists && isCheckoutPage! == "true">
        <@fieldErrors fieldName="productPromoCodeId"/>
    </#if>
</div>
