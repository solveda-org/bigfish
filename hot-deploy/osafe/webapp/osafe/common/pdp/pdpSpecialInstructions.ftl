<#if SPECIALINSTRUCTIONS?exists && SPECIALINSTRUCTIONS?has_content>
  <div id="pdpSpecialInstructions">
       <div class="displayBox">
         <h3 class="displayBoxHeaderCaption">${uiLabelMap.PDPSpecialInstructionsHeading}</h3>
        <p><@renderContentAsText contentId="${SPECIALINSTRUCTIONS}" ignoreTemplate="true"/></p>
       </div>
  </div>
</#if>
