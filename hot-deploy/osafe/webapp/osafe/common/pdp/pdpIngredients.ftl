<li class="${request.getAttribute("attributeClass")!}">
  <#if INGREDIENTS?exists && INGREDIENTS?has_content>
    <div class="pdpIngredients" id="js_pdpIngredients">
       <div class="displayBox">
         <h3>${uiLabelMap.PDPIngredientsHeading}</h3>
         <p><@renderContentAsText contentId="${INGREDIENTS}" ignoreTemplate="true"/></p>
       </div>
    </div>
  </#if>
  <#if INGREDIENTS?exists && INGREDIENTS?has_content>
    <div class="pdpIngredients" id="js_pdpIngredients_Virtual" style="display:none">
      <div class="displayBox">
         <h3>${uiLabelMap.PDPIngredientsHeading}</h3>
         <p><@renderContentAsText contentId="${INGREDIENTS}" ignoreTemplate="true"/></p>
      </div>
    </div>
  </#if>
  <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
    <#list productVariantMapKeys as key>
      <#assign variantContentId = ''/>
      <#assign variantContentMap = productVariantProductContentIdMap.get('${key}')!""/>
      <#if variantContentMap?has_content>
      	<#assign variantContentId = variantContentMap.get("INGREDIENTS")!""/>
      </#if>
      <#if variantContentId?has_content>
        <div class="pdpIngredients" id="js_pdpIngredients_${key}" style="display:none">
          <div class="displayBox">
            <h3>${uiLabelMap.PDPIngredientsHeading}</h3>
            <p><@renderContentAsText contentId="${variantContentId}" ignoreTemplate="true"/></p>
          </div>
        </div>
      <#else>
        <div class="pdpIngredients" id="js_pdpIngredients_${key}" style="display:none">
         <#if INGREDIENTS?exists && INGREDIENTS?has_content>
	          <div class="displayBox">
                <h3>${uiLabelMap.PDPIngredientsHeading}</h3>
                <p><@renderContentAsText contentId="${INGREDIENTS}" ignoreTemplate="true"/></p>
	          </div>
	     </#if>
        </div>
     </#if>
    </#list>
  </#if>
</li>

