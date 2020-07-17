<#if DIRECTIONS?exists &&  DIRECTIONS?has_content>
 <div id="pdpDirections">
       <div class="displayBox">
        <h3 class="displayBoxHeaderCaption">${uiLabelMap.PDPDirectionsHeading}</h3>
        <p><@renderContentAsText contentId="${DIRECTIONS}" ignoreTemplate="true"/></p>
       </div>
 </div>
</#if>
