<#if PLP_INGREDIENTS?exists && PLP_INGREDIENTS?has_content>
<li class="${request.getAttribute("attributeClass")!}">
  <div>
   <label>${uiLabelMap.PLPIngredientsLabel}</label>
   <span><@renderContentAsText contentId="${PLP_INGREDIENTS}" ignoreTemplate="true"/></span>
  </div>
</li>   
</#if>
