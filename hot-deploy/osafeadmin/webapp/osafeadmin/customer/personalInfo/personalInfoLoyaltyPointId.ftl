<#if partyId?exists && partyId?has_content>
    <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : partyId, "attrName" : "LOYALTY_POINT_ID"}, false)!/>
    <#if partyAttribute?has_content>
      <#assign USER_LOYALTY_POINT_ID = partyAttribute.attrValue!"">
    </#if>
</#if>

<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<#assign selectedLoyaltyPointId = parameters.get("userLoyaltyPointId")!USER_LOYALTY_POINT_ID!""/>
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
              <label><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.LoyaltyPointIdCaption}</label>
            </div>
            <div class="infoValue">
              <input type="text" id="userLoyaltyPointId" name="userLoyaltyPointId" value="${selectedLoyaltyPointId!""}"/>
              <input type="hidden" id="userLoyaltyPointId_mandatory" name="userLoyaltyPointId_mandatory" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>
