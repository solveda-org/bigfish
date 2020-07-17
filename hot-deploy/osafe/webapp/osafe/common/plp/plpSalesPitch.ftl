<#if PLP_SHORT_SALES_PITCH?exists &&  PLP_SHORT_SALES_PITCH?has_content>
<li class="${request.getAttribute("attributeClass")!}">
 <div>
   <label>${uiLabelMap.PLPSalesPitchLabel}</label>
   <span><@renderContentAsText contentId="${PLP_SHORT_SALES_PITCH}" ignoreTemplate="true"/></span>
 </div>
</li>   
</#if>
