<li class="${request.getAttribute("attributeClass")!}">
  <#if SPECIALINSTRUCTIONS?exists && SPECIALINSTRUCTIONS?has_content>
    <div class="pdpSpecialInstructions" id="js_pdpSpecialInstructions">
       <div class="displayBox">
         <h3>${uiLabelMap.PDPSpecialInstructionsHeading}</h3>
         <p><@renderContentAsText contentId="${SPECIALINSTRUCTIONS}" ignoreTemplate="true"/></p>
       </div>
    </div>
  </#if>
  <#if SPECIALINSTRUCTIONS?exists && SPECIALINSTRUCTIONS?has_content>
    <div class="pdpSpecialInstructions" id="js_pdpSpecialInstructions_Virtual" style="display:none">
      <div class="displayBox">
         <h3>${uiLabelMap.PDPSpecialInstructionsHeading}</h3>
         <p><@renderContentAsText contentId="${SPECIALINSTRUCTIONS}" ignoreTemplate="true"/></p>
      </div>
    </div>
  </#if>
  <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
    <#list productVariantMapKeys as key>
      <#assign variantContentId = ''/>
      <#assign variantContentMap = productVariantProductContentIdMap.get('${key}')!""/>
      <#if variantContentMap?has_content>
      	<#assign variantContentId = variantContentMap.get("SPECIALINSTRUCTIONS")!""/>
      </#if>
      <#if variantContentId?has_content>
        <div class="pdpSpecialInstructions" id="js_pdpSpecialInstructions_${key}" style="display:none">
          <div class="displayBox">
            <h3>${uiLabelMap.PDPSpecialInstructionsHeading}</h3>
            <p><@renderContentAsText contentId="${variantContentId}" ignoreTemplate="true"/></p>
          </div>
        </div>
      <#else>
        <div class="pdpSpecialInstructions" id="js_pdpSpecialInstructions_${key}" style="display:none">
         <#if SPECIALINSTRUCTIONS?exists && SPECIALINSTRUCTIONS?has_content>
	          <div class="displayBox">
                <h3>${uiLabelMap.PDPSpecialInstructionsHeading}</h3>
                <p><@renderContentAsText contentId="${SPECIALINSTRUCTIONS}" ignoreTemplate="true"/></p>
	          </div>
	     </#if>
        </div>
     </#if>
    </#list>
  </#if>
</li>
