<li class="${request.getAttribute("attributeClass")!}">
  <div class="pdpInternalName" id="js_pdpInternalName">
    <#if pdpInternalName?has_content>
      <label>${uiLabelMap.PDPInternalNameLabel}</label>
      <span>${pdpInternalName!""}</span>
    </#if>
  </div>
  
  <div class="pdpInternalName" id="js_pdpInternalName_Virtual" style="display:none">
    <#if pdpInternalName?has_content>
      <label>${uiLabelMap.PDPInternalNameLabel}</label>
      <span>${pdpInternalName!""}</span>
    </#if>
  </div>
  <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
    <#list productVariantMapKeys as key>
      <#assign product = delegator.findOne("Product", {"productId" : key}, true)!/>  
      <#if product?has_content>
        <div class="pdpInternalName" id="js_pdpInternalName_${key}" style="display:none">
          <label>${uiLabelMap.PDPInternalNameLabel}</label>
          <#assign productInternalName = product.internalName!""/>
          <span>${Static["org.apache.commons.lang.StringEscapeUtils"].unescapeHtml(productInternalName)}</span>
        </div>
      </#if>
    </#list>
  </#if>
</li>
