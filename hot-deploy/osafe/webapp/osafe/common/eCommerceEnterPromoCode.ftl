 <#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists />
<#if (shoppingCart.size() > 0)>
<div class="displayBox promotionDisplayBox">
    <div class="displayBoxHeader promotionDisplayBoxHeader">
        <span class="displayBoxHeaderCaption">${uiLabelMap.PromotionHeading}</span>
    </div>
    <div id="ecommercePromocodeEntry" class="entry">
        <label>${uiLabelMap.EnterPromoCodeLabel}</label>
        <input type="text" id="manualOfferCode" name="manualOfferCode" value="${requestParameters.UofferCode!""}" maxlength="20"/>
        <a class="standardBtn action" href="javascript:addManualPromoCode();">${uiLabelMap.ApplyOfferBtn}</a>
        <@fieldErrors fieldName="productPromoCodeId"/>
    </div>
    ${screens.render("component://osafe/widget/EcommerceScreens.xml#eCommerceEnteredPromoCode")}
</div>
</#if>
