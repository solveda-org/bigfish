<li class="${request.getAttribute("attributeClass")!}">
  <#if DIRECTIONS?exists && DIRECTIONS?has_content>
    <div class="pdpDirections" id="js_pdpDirections">
       <div class="displayBox">
         <h3>${uiLabelMap.PDPDirectionsHeading}</h3>
         <p><@renderContentAsText contentId="${DIRECTIONS}" ignoreTemplate="true"/></p>
       </div>
    </div>
  </#if>
  <#if DIRECTIONS?exists && DIRECTIONS?has_content>
    <div class="pdpDirections" id="js_pdpDirections_Virtual" style="display:none">
      <div class="displayBox">
         <h3>${uiLabelMap.PDPDirectionsHeading}</h3>
         <p><@renderContentAsText contentId="${DIRECTIONS}" ignoreTemplate="true"/></p>
      </div>
    </div>
  </#if>
  <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
    <#list productVariantMapKeys as key>
      <#assign variantContentId = ''/>
      <#assign variantContentMap = productVariantProductContentIdMap.get('${key}')!""/>
      <#if variantContentMap?has_content>
      	<#assign variantContentId = variantContentMap.get("DIRECTIONS")!""/>
      </#if>
      <#if variantContentId?has_content>
        <div class="pdpDirections" id="js_pdpDirections_${key}" style="display:none">
          <div class="displayBox">
            <h3>${uiLabelMap.PDPDirectionsHeading}</h3>
            <p><@renderContentAsText contentId="${variantContentId}" ignoreTemplate="true"/></p>
          </div>
        </div>
      <#else>
        <div class="pdpDirections" id="js_pdpDirections_${key}" style="display:none">
         <#if DIRECTIONS?exists && DIRECTIONS?has_content>
	          <div class="displayBox">
                <h3>${uiLabelMap.PDPDirectionsHeading}</h3>
                <p><@renderContentAsText contentId="${DIRECTIONS}" ignoreTemplate="true"/></p>
	          </div>
	     </#if>
        </div>
     </#if>
    </#list>
  </#if>
</li>

