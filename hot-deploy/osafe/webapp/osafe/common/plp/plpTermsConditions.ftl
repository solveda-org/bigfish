<#if PLP_TERMS_AND_CONDS?exists &&  PLP_TERMS_AND_CONDS?has_content>
<li class="${request.getAttribute("attributeClass")!}">
 <div>
     <label>${uiLabelMap.PLPTermsConditionsLabel}</label>
     <span><@renderContentAsText contentId="${PLP_TERMS_AND_CONDS}" ignoreTemplate="true"/></span>
 </div>
</li>   
</#if>
