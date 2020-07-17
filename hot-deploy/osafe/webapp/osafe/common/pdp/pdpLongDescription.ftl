<li class="${request.getAttribute("attributeClass")!}">
  <#if LONG_DESCRIPTION?exists && LONG_DESCRIPTION?has_content>
    <div class="pdpLongDescription" id="js_pdpLongDescription">
       <div class="displayBox">
         <h3>${uiLabelMap.PDPLongDescriptionHeading}</h3>
         <p><@renderContentAsText contentId="${LONG_DESCRIPTION}" ignoreTemplate="true"/></p>
       </div>
    </div>
  </#if>
  <#if LONG_DESCRIPTION?exists && LONG_DESCRIPTION?has_content>
    <div class="pdpLongDescription" id="js_pdpLongDescription_Virtual" style="display:none">
      <div class="displayBox">
         <h3>${uiLabelMap.PDPLongDescriptionHeading}</h3>
         <p><@renderContentAsText contentId="${LONG_DESCRIPTION}" ignoreTemplate="true"/></p>
      </div>
    </div>
  </#if>
  <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
    <#list productVariantMapKeys as key>
      <#assign variantContentId = ''/>
      <#assign variantContentMap = productVariantProductContentIdMap.get('${key}')!""/>
      <#if variantContentMap?has_content>
      	<#assign variantContentId = variantContentMap.get("LONG_DESCRIPTION")!""/>
      </#if>
      <#if variantContentId?has_content>
        <div class="pdpLongDescription" id="js_pdpLongDescription_${key}" style="display:none">
          <div class="displayBox">
            <h3>${uiLabelMap.PDPLongDescriptionHeading}</h3>
            <p><@renderContentAsText contentId="${variantContentId}" ignoreTemplate="true"/></p>
          </div>
        </div>
      <#else>
        <div class="pdpLongDescription" id="js_pdpLongDescription_${key}" style="display:none">
         <#if LONG_DESCRIPTION?exists && LONG_DESCRIPTION?has_content>
	          <div class="displayBox">
                <h3>${uiLabelMap.PDPLongDescriptionHeading}</h3>
                <p><@renderContentAsText contentId="${LONG_DESCRIPTION}" ignoreTemplate="true"/></p>
	          </div>
	     </#if>
        </div>
     </#if>
    </#list>
  </#if>
</li>

