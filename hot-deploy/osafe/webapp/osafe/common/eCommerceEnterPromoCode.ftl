 <#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists />
<#if (shoppingCart.size() > 0)>
<div class="${request.getAttribute("attributeClass")!}">
 <div class="displayBox">
    <h3>${uiLabelMap.PromotionHeading}</h3>
    <ul class="displayActionList ${request.getAttribute("attributeClass")!}">
      <@fieldErrors fieldName="productPromoCodeId"/>
      <li>
       <div>
        <label>${uiLabelMap.EnterPromoCodeLabel}</label>
        <input type="text" id="js_manualOfferCode" name="manualOfferCode" value="${requestParameters.manualOfferCode!""}" maxlength="20" onkeypress="javascript:setCheckoutFormAction(document.${formName!}, 'APC', '');"/>
        <a class="standardBtn action" href="javascript:addManualPromoCode();"><span>${uiLabelMap.ApplyOfferBtn}</span></a>
       </div>
      </li>
    </ul>
    ${screens.render("component://osafe/widget/EcommerceCheckoutScreens.xml#eCommerceEnteredPromoCode")}    
 </div>
</div>
</#if>
