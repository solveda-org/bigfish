<#if PLP_SPECIALINSTRUCTIONS?exists && PLP_SPECIALINSTRUCTIONS?has_content>
  <div class="plpSpecialInstructions">
     <label>${uiLabelMap.PLPSpecialInstructionsLabel}</label>
     <span><@renderContentAsText contentId="${PLP_SPECIALINSTRUCTIONS}" ignoreTemplate="true"/></span>
  </div>
</#if>
