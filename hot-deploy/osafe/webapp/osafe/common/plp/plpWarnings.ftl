<#if PLP_WARNINGS?exists &&  PLP_WARNINGS?has_content>
 <div class="plpWarnings">
     <label>${uiLabelMap.PLPWarningsLabel}</label>
     <span><@renderContentAsText contentId="${PLP_WARNINGS}" ignoreTemplate="true"/></span>
 </div>
</#if>
