<div class="pdpLongDescription" id="pdpLongDescription">
  <#if (LONG_DESCRIPTION?exists && LONG_DESCRIPTION?has_content)>
    <div class="displayBox">
      <h3>${uiLabelMap.PDPLongDescriptionHeading}</h3>
      <p><@renderContentAsText contentId="${LONG_DESCRIPTION}" ignoreTemplate="true"/></p>
    </div>
  </#if>
</div>

<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign contentId = ''/>
    <#assign variantProduct = delegator.findOne("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",key), true)/>
    <#assign productVariantContent = variantProduct.getRelatedCache("ProductContent") />
    <#assign productVariantContent = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productVariantContent,true)/>
    <#assign productVariantLongDesc = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(productVariantContent,Static["org.ofbiz.base.util.UtilMisc"].toMap('productContentTypeId','LONG_DESCRIPTION'))/>
    <#if productVariantLongDesc?has_content>
      <#assign productContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productVariantLongDesc) />
      <#assign contentId = productContent.contentId?if_exists />
    </#if>
    <#if contentId?has_content>
      <div class="pdpLongDescription" id="pdpLongDescription_${key}" style="display:none">
        <div class="displayBox">
          <h3>${uiLabelMap.PDPLongDescriptionHeading}</h3>
          <p><@renderContentAsText contentId="${contentId}" ignoreTemplate="true"/></p>
        </div>
      </div>
    <#else>
      <div class="pdpLongDescription" id="pdpLongDescription_${key}" style="display:none">
        <div class="displayBox">
          <h3>${uiLabelMap.PDPLongDescriptionHeading}</h3>
          <p>${key} <@renderContentAsText contentId="${LONG_DESCRIPTION}" ignoreTemplate="true"/></p>
        </div>
      </div>
    </#if>
  </#list>
</#if>