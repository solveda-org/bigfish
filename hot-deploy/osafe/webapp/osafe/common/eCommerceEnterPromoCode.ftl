 <#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists />
<#if (shoppingCart.size() > 0)>
<div class="displayBox">
    <h3>${uiLabelMap.PromotionHeading}</h3>
    <div id="ecommercePromocodeEntry" class="entry">
        <label>${uiLabelMap.EnterPromoCodeLabel}</label>
        <input type="text" id="manualOfferCode" name="manualOfferCode" value="${requestParameters.UofferCode!""}" maxlength="20"/>
        <a class="standardBtn action" href="javascript:addManualPromoCode();"><span>${uiLabelMap.ApplyOfferBtn}</span></a>
        <@fieldErrors fieldName="productPromoCodeId"/>
    </div>
    ${screens.render("component://osafe/widget/EcommerceCheckoutScreens.xml#eCommerceEnteredPromoCode")}
</div>
</#if>
