<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists />
<#assign orderAdjustmentAttributeList = sessionAttributes.orderAdjustmentAttributeList!/>
<#list orderAdjustmentAttributeList as orderAdjustmentAttributeInfoMap>
  <#assign loyaltyPointsId = orderAdjustmentAttributeInfoMap.MEMBER_ID!""/>
</#list>

<#if userLogin?has_content>
    <#assign partyId = userLogin.partyId!"">
</#if>
<#if partyId?exists && partyId?has_content>
    <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : partyId, "attrName" : "LOYALTY_POINT_ID"}, true)!/>
    <#if partyAttribute?has_content>
      <#assign loyaltyPointsIdFromDB = partyAttribute.attrValue!"">
    </#if>
</#if>

<#if (shoppingCart.size() > 0)>
 <div class="${request.getAttribute("attributeClass")!}">
  <div class="displayBox">
    <h3>${uiLabelMap.LoyaltyPointsHeading}</h3>
    <ul class="displayActionList ${request.getAttribute("attributeClass")!}">
      <@fieldErrors fieldName="loyaltyPointsId"/>
      <li>
       <div>
        <label>${uiLabelMap.LoyaltyPointsLabel}</label>
        <input type="text" id="js_loyaltyPointsId" name="loyaltyPointsId" value="${loyaltyPointsId!loyaltyPointsIdFromDB!""}" maxlength="20"<#if (loyaltyPointsId?has_content)>readonly</#if> onkeypress="javascript:setCheckoutFormAction(document.${formName!}, 'ALP', '');"/>
        <#if (!orderAdjustmentAttributeList?has_content)>
          <a class="standardBtn action" id="js_applyLoyaltyCard" href="javascript:addLoyaltyPoints();"><span>${uiLabelMap.ApplyLoyaltyCardBtn}</span></a>
        </#if>
       </div>
      </li>
    </ul>
    ${screens.render("component://osafe/widget/EcommerceCheckoutScreens.xml#eCommerceEnteredLoyaltyPoints")}
  </div>
 </div>
</#if>
