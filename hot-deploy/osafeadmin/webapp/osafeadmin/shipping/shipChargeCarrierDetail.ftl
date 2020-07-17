<#if mode?has_content>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CarrierIdCaption}</label>
      </div>
      <div class="infoValue">
          <input name="partyId" type="text" id="partyId" maxlength="20" value="${parameters.partyId!partyId!""}"/>
      </div>
    </div>
  </div>

<#else>
    ${uiLabelMap.NoDataAvailableInfo}
</#if>
