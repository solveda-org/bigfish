<#if SHORT_SALES_PITCH?exists &&  SHORT_SALES_PITCH?has_content>
 <div id="pdpSalesPitch">
       <div class="displayBox">
        <h3 class="displayBoxHeaderCaption">${uiLabelMap.PDPSalesPitchHeading}</h3>
        <p><@renderContentAsText contentId="${SHORT_SALES_PITCH}" ignoreTemplate="true"/></p>
       </div>
 </div>
</#if>
