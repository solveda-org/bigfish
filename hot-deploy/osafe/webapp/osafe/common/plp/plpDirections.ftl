<#if PLP_DIRECTIONS?exists &&  PLP_DIRECTIONS?has_content>
 <div class="plpDirections">
   <label>${uiLabelMap.PLPDirectionsLabel}</label>
   <span><@renderContentAsText contentId="${PLP_DIRECTIONS}" ignoreTemplate="true"/></span>
 </div>
</#if>
