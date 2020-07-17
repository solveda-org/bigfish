<#if SPECIALINSTRUCTIONS?exists && SPECIALINSTRUCTIONS?has_content>
  <div class="pdpSpecialInstructions">
       <div class="displayBox">
         <h3>${uiLabelMap.PDPSpecialInstructionsHeading}</h3>
        <p><@renderContentAsText contentId="${SPECIALINSTRUCTIONS}" ignoreTemplate="true"/></p>
       </div>
  </div>
</#if>
