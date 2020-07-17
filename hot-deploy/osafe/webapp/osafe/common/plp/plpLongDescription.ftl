<#assign plpLongDescription = StringUtil.wrapString(plpLongDescription!"") />
<#if plpLongDescription?has_content>
  <div class="plpLongDescription" id="plpLongDescription">
      <label>${uiLabelMap.PLPLongDescriptionLabel}</label>
      <span>${plpLongDescription!}</span>
  </div>
</#if>
