<#if TERMS_AND_CONDS?exists &&  TERMS_AND_CONDS?has_content>
  <div class="pdpTermsAndConditions">
       <div class="displayBox">
        <h3>${uiLabelMap.PDPTermsConditionsHeading}</h3>
        <p><@renderContentAsText contentId="${TERMS_AND_CONDS}" ignoreTemplate="true"/></p>
       </div>
  </div>
</#if>
