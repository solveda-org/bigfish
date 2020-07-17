<#if mode?has_content>
	<#if carrier?has_content>
    <#assign groupName = carrier.groupName!"" />
  	</#if>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CarrierIdCaption}</label>
      </div>
      <div class="infoValue">
      <#if mode="add">
          <input name="partyId" type="text" id="partyId" maxlength="20" value="${parameters.partyId!partyId!""}"/>
      <#else>
      		${parameters.partyId!partyId!""}
      		<input name="partyId" type="hidden" id="partyId" maxlength="20" value="${parameters.partyId!partyId!""}"/>
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
