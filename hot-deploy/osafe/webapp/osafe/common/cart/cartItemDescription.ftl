<div class="cartItemDescription">
  <div class="labelText">
      <label>${uiLabelMap.CartItemDescriptionCaption}</label>
  </div>
  <div class="labelValue">
    <#if productFeatureAndAppls?has_content>
      <#list productFeatureAndAppls as productFeatureAndAppl>
	    <#assign productFeatureTypeLabel = ""/>
	    <#if productFeatureTypesMap?has_content>
	      <#assign productFeatureTypeLabel = productFeatureTypesMap.get(productFeatureAndAppl.productFeatureTypeId)!"" />
	    </#if>
	    <div class="cartItemProdFeature"><span>${productFeatureTypeLabel!}:${productFeatureAndAppl.description!}</span></div>
      </#list>
    </#if>
  </div>
</div>