<#if DIRECTIONS?exists &&  DIRECTIONS?has_content>
 <div class="pdpDirections">
       <div class="displayBox">
        <h3>${uiLabelMap.PDPDirectionsHeading}</h3>
        <p><@renderContentAsText contentId="${DIRECTIONS}" ignoreTemplate="true"/></p>
       </div>
 </div>
</#if>
