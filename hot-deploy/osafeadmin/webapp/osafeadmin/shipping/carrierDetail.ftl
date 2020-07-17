<#if mode?has_content>
  <#if carrier?has_content>
    <#assign groupName = carrier.groupName! />
    <#assign carrierPartyId = carrier.partyId! />
  </#if>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CarrierIdCaption}</label>
      </div>
      <div class="infoValue">
        <#if mode="add">
          <input name="carrierPartyId" type="text" id="carrierPartyId" maxlength="20" value="${parameters.carrierPartyId!carrierPartyId!""}"/>
        <#else>
          ${parameters.carrierPartyId!carrierPartyId!""}
          <input name="carrierPartyId" type="hidden" id="carrierPartyId" maxlength="20" value="${parameters.carrierPartyId!carrierPartyId!""}"/>
        </#if>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.DescriptionCaption}</label>
      </div>
      <div class="infoValue">
        <input name="groupName" type="text" id="groupName" maxlength="20" value="${groupName!parameters.groupName!""}"/>
      </div>
    </div>
  </div>

<#else>
  ${uiLabelMap.NoDataAvailableInfo}
</#if>
