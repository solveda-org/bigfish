<#if WARNINGS?exists &&  WARNINGS?has_content>
 <div class="pdpWarnings">
       <div class="displayBox">
        <h3>${uiLabelMap.PDPWarningsHeading}</h3>
        <p><@renderContentAsText contentId="${WARNINGS}" ignoreTemplate="true"/></p>
       </div>
 </div>
</#if>
