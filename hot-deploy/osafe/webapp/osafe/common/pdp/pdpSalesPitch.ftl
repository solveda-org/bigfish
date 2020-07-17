<li class="${request.getAttribute("attributeClass")!}">
  <#if SHORT_SALES_PITCH?exists && SHORT_SALES_PITCH?has_content>
    <div class="pdpSalesPitch" id="js_pdpSalesPitch">
       <div class="displayBox">
         <h3>${uiLabelMap.PDPSalesPitchHeading}</h3>
         <p><@renderContentAsText contentId="${SHORT_SALES_PITCH}" ignoreTemplate="true"/></p>
       </div>
    </div>
  </#if>
  <#if SHORT_SALES_PITCH?exists && SHORT_SALES_PITCH?has_content>
    <div class="pdpSalesPitch" id="js_pdpSalesPitch_Virtual" style="display:none">
      <div class="displayBox">
         <h3>${uiLabelMap.PDPSalesPitchHeading}</h3>
         <p><@renderContentAsText contentId="${SHORT_SALES_PITCH}" ignoreTemplate="true"/></p>
      </div>
    </div>
  </#if>
  <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
    <#list productVariantMapKeys as key>
      <#assign variantContentId = ''/>
      <#assign variantContentMap = productVariantProductContentIdMap.get('${key}')!""/>
      <#if variantContentMap?has_content>
      	<#assign variantContentId = variantContentMap.get("SHORT_SALES_PITCH")!""/>
      </#if>
      <#if variantContentId?has_content>
        <div class="pdpSalesPitch" id="js_pdpSalesPitch_${key}" style="display:none">
          <div class="displayBox">
            <h3>${uiLabelMap.PDPSalesPitchHeading}</h3>
            <p><@renderContentAsText contentId="${variantContentId}" ignoreTemplate="true"/></p>
          </div>
        </div>
      <#else>
        <div class="pdpSalesPitch" id="js_pdpSalesPitch_${key}" style="display:none">
         <#if SHORT_SALES_PITCH?exists && SHORT_SALES_PITCH?has_content>
	          <div class="displayBox">
                <h3>${uiLabelMap.PDPSalesPitchHeading}</h3>
                <p><@renderContentAsText contentId="${SHORT_SALES_PITCH}" ignoreTemplate="true"/></p>
	          </div>
	     </#if>
        </div>
     </#if>
    </#list>
  </#if>
</li>


