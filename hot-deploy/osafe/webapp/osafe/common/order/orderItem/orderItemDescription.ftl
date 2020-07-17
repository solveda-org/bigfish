<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
    <div>
      <label>${uiLabelMap.DescriptionCaption}</label>
      <span>
	      <ul class="displayList productFeature">
			<li class="string productFeature">
			 <div>
			  <#if productFeatureAndAppls?has_content>
			      <#list productFeatureAndAppls as productFeatureAndAppl>
				    <#assign productFeatureTypeLabel = ""/>
				    <#if productFeatureTypesMap?has_content>
				      <#assign productFeatureTypeLabel = productFeatureTypesMap.get(productFeatureAndAppl.productFeatureTypeId)!"" />
				    </#if>
				    <span>${productFeatureTypeLabel!}:${productFeatureAndAppl.description!}</span>
			      </#list>
			  </#if>
	         </div>
	       </li>
          </ul>
      </span>
    </div>
</li>