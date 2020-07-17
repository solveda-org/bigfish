<#if PLP_WARNINGS?exists &&  PLP_WARNINGS?has_content>
<li class="${request.getAttribute("attributeClass")!}">
 <div>
     <label>${uiLabelMap.PLPWarningsLabel}</label>
     <span><@renderContentAsText contentId="${PLP_WARNINGS}" ignoreTemplate="true"/></span>
 </div>
</li>   
</#if>
