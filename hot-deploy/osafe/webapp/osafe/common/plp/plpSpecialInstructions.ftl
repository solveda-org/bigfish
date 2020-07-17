<#if PLP_SPECIALINSTRUCTIONS?exists && PLP_SPECIALINSTRUCTIONS?has_content>
<li class="${request.getAttribute("attributeClass")!}">
 <div>
     <label>${uiLabelMap.PLPSpecialInstructionsLabel}</label>
     <span><@renderContentAsText contentId="${PLP_SPECIALINSTRUCTIONS}" ignoreTemplate="true"/></span>
 </div>
</li>   
</#if>
