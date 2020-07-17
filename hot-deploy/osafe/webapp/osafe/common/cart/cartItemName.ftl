<div class="cartItemName">
TEST CART NAME
<#--
  <#assign productName = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.getProduct(), "PRODUCT_NAME", locale, dispatcher)?if_exists>
  <#if !productName?has_content && virtualProduct?has_content>
    <#assign productName = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(virtualProduct, "PRODUCT_NAME", locale, dispatcher)?if_exists>
  </#if>
  <td class="description <#if !cartLine_has_next>lastRow</#if>">
    <dl>
      <dt>${uiLabelMap.ProductDescriptionAttributesInfo}</dt>
      <dd class="description">
        <a href="${productFriendlyUrl}">${StringUtil.wrapString(productName!)}</a>
      </dd>
      <#assign productFeatureAndAppls = product.getRelatedCache("ProductFeatureAndAppl") />
      <#assign productFeatureAndAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureAndAppls,true)/>
      <#assign productFeatureAndAppls = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(productFeatureAndAppls,Static["org.ofbiz.base.util.UtilMisc"].toList('sequenceNum'))/>
      <#if productFeatureAndAppls?has_content>
        <#list productFeatureAndAppls as productFeatureAndAppl>
          <#assign productFeatureTypeLabel = ""/>
          <#if productFeatureTypesMap?has_content>
            <#assign productFeatureTypeLabel = productFeatureTypesMap.get(productFeatureAndAppl.productFeatureTypeId)!"" />
          </#if>
          <dd>${productFeatureTypeLabel!}:${productFeatureAndAppl.description!}</dd>
      </#list>
      </#if>
    </dl>
  </td>
-->
</div>
