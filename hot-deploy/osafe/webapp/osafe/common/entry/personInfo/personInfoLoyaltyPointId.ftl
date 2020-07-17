<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if userLogin?has_content>
    <#assign partyId = userLogin.partyId!"">
</#if>
<#if partyId?exists && partyId?has_content>
    <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : partyId, "attrName" : "LOYALTY_POINT_ID"}, true)!/>
    <#if partyAttribute?has_content>
      <#assign USER_LOYALTY_POINT_ID = partyAttribute.attrValue!"">
    </#if>
</#if>
<#assign selectedLoyaltyPointId = parameters.get("USER_LOYALTY_POINT_ID")!USER_LOYALTY_POINT_ID!""/>

<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="USER_LOYALTY_POINT_ID"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.LoyaltyPointIdCaption}</label>
      <div class="entryField">
	      <input type="text" id="USER_LOYALTY_POINT_ID" name="USER_LOYALTY_POINT_ID" value="${selectedLoyaltyPointId!""}"/>
	      <input type="hidden" name="USER_LOYALTY_POINT_ID_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="USER_LOYALTY_POINT_ID"/>
      </div>
</div>
