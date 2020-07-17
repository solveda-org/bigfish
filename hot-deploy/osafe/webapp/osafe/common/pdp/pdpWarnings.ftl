<#if WARNINGS?exists &&  WARNINGS?has_content>
 <div id="pdpWarnings">
       <div class="displayBox">
        <h3 class="displayBoxHeaderCaption">${uiLabelMap.PDPWarningsHeading}</h3>
        <p><@renderContentAsText contentId="${WARNINGS}" ignoreTemplate="true"/></p>
       </div>
 </div>
</#if>
