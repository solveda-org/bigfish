<li class="${request.getAttribute("attributeClass")!}">
  <#if TERMS_AND_CONDS?exists && TERMS_AND_CONDS?has_content>
    <div class="pdpTermsConditions" id="js_pdpTermsConditions">
       <div class="displayBox">
         <h3>${uiLabelMap.PDPTermsConditionsHeading}</h3>
         <p><@renderContentAsText contentId="${TERMS_AND_CONDS}" ignoreTemplate="true"/></p>
       </div>
    </div>
  </#if>
  <#if TERMS_AND_CONDS?exists && TERMS_AND_CONDS?has_content>
    <div class="pdpTermsConditions" id="js_pdpTermsConditions_Virtual" style="display:none">
      <div class="displayBox">
         <h3>${uiLabelMap.PDPTermsConditionsHeading}</h3>
         <p><@renderContentAsText contentId="${TERMS_AND_CONDS}" ignoreTemplate="true"/></p>
      </div>
    </div>
  </#if>
  <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
    <#list productVariantMapKeys as key>
      <#assign variantContentId = ''/>
      <#assign variantContentMap = productVariantProductContentIdMap.get('${key}')!""/>
      <#if variantContentMap?has_content>
      	<#assign variantContentId = variantContentMap.get("TERMS_AND_CONDS")!""/>
      </#if>
      <#if variantContentId?has_content>
        <div class="pdpTermsConditions" id="js_pdpTermsConditions_${key}" style="display:none">
          <div class="displayBox">
            <h3>${uiLabelMap.PDPTermsConditionsHeading}</h3>
            <p><@renderContentAsText contentId="${variantContentId}" ignoreTemplate="true"/></p>
          </div>
        </div>
      <#else>
        <div class="pdpTermsConditions" id="js_pdpTermsConditions_${key}" style="display:none">
         <#if TERMS_AND_CONDS?exists && TERMS_AND_CONDS?has_content>
	          <div class="displayBox">
                <h3>${uiLabelMap.PDPTermsConditionsHeading}</h3>
                <p><@renderContentAsText contentId="${TERMS_AND_CONDS}" ignoreTemplate="true"/></p>
	          </div>
	     </#if>
        </div>
     </#if>
    </#list>
  </#if>
</li>


