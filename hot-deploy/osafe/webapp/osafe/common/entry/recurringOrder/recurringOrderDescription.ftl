<div class="${request.getAttribute("attributeClass")!}">
    <label>${uiLabelMap.CartItemDescriptionCaption}</label>
    <div class="entryField">
      <ul class="productFeature">
		  <#if productFeatureAndAppls?has_content>
		      <#list productFeatureAndAppls as productFeatureAndAppl>
			    <#assign productFeatureTypeLabel = ""/>
			    <#if productFeatureTypesMap?has_content>
			      <#assign productFeatureTypeLabel = productFeatureTypesMap.get(productFeatureAndAppl.productFeatureTypeId)!"" />
			    </#if>
			    <li>${productFeatureTypeLabel!}:${productFeatureAndAppl.description!}</li>
		      </#list>
		  <#else>
		    <li>&nbsp;</li>
		  </#if>
      </ul>
    </div>
</div>
