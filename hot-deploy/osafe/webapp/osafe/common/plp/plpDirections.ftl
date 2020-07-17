<#if PLP_DIRECTIONS?exists &&  PLP_DIRECTIONS?has_content>
<li class="${request.getAttribute("attributeClass")!}">
  <div>
   <label>${uiLabelMap.PLPDirectionsLabel}</label>
   <span><@renderContentAsText contentId="${PLP_DIRECTIONS}" ignoreTemplate="true"/></span>
  </div>
</li>   
</#if>
