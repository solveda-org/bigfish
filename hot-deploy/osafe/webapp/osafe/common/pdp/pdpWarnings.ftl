<li class="${request.getAttribute("attributeClass")!}">
  <#if WARNINGS?exists && WARNINGS?has_content>
    <div class="pdpWarnings" id="js_pdpWarnings">
       <div class="displayBox">
         <h3>${uiLabelMap.PDPWarningsHeading}</h3>
         <p><@renderContentAsText contentId="${WARNINGS}" ignoreTemplate="true"/></p>
       </div>
    </div>
  </#if>
  <#if WARNINGS?exists && WARNINGS?has_content>
    <div class="pdpWarnings" id="js_pdpWarnings_Virtual" style="display:none">
      <div class="displayBox">
         <h3>${uiLabelMap.PDPWarningsHeading}</h3>
         <p><@renderContentAsText contentId="${WARNINGS}" ignoreTemplate="true"/></p>
      </div>
    </div>
  </#if>
  <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
    <#list productVariantMapKeys as key>
      <#assign variantContentId = ''/>
      <#assign variantContentMap = productVariantProductContentIdMap.get('${key}')!""/>
      <#if variantContentMap?has_content>
      	<#assign variantContentId = variantContentMap.get("WARNINGS")!""/>
      </#if>
      <#if variantContentId?has_content>
        <div class="pdpWarnings" id="js_pdpWarnings_${key}" style="display:none">
          <div class="displayBox">
            <h3>${uiLabelMap.PDPWarningsHeading}</h3>
            <p><@renderContentAsText contentId="${variantContentId}" ignoreTemplate="true"/></p>
          </div>
        </div>
      <#else>
        <div class="pdpWarnings" id="js_pdpWarnings_${key}" style="display:none">
         <#if WARNINGS?exists && WARNINGS?has_content>
	          <div class="displayBox">
                <h3>${uiLabelMap.PDPWarningsHeading}</h3>
                <p><@renderContentAsText contentId="${WARNINGS}" ignoreTemplate="true"/></p>
	          </div>
	     </#if>
        </div>
     </#if>
    </#list>
  </#if>
</li>


