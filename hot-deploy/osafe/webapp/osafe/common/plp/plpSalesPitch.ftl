<#if PLP_SHORT_SALES_PITCH?exists &&  PLP_SHORT_SALES_PITCH?has_content>
 <div class="plpSalesPitch">
     <label>${uiLabelMap.PLPSalesPitchLabel}</label>
    <span><@renderContentAsText contentId="${PLP_SHORT_SALES_PITCH}" ignoreTemplate="true"/></span>
 </div>
</#if>
