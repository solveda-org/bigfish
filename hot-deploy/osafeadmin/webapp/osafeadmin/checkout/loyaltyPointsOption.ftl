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



<div class="infoRow">
    <div class="infoEntry">
        <div class="infoCaption"><label>${uiLabelMap.LoyaltyPointIdCaption}</label></div>
        <div class="infoValue">
	       <input type="text" id="loyaltyPointsId" name="loyaltyPointsId" value="${loyaltyPointsId!loyaltyPointsIdFromDB!""}" maxlength="20"<#if (loyaltyPointsId?has_content)>readonly</#if>/>
	       <#if (!orderAdjustmentAttributeList?has_content)>
	          <a href="javascript:addLoyaltyPoints();"><span class="refreshIcon"></span></a>
	       </#if>
        </div>
    </div>
</div>
<div class="infoRow">
${screens.render("component://osafeadmin/widget/AdminCheckoutScreens.xml#enteredLoyaltyPoints")}
</div>