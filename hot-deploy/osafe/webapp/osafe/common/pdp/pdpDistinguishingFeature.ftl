<li class="${request.getAttribute("attributeClass")!}">
 <#if disFeatureTypesList?has_content>
  <div class="pdpDistinguishingFeature" id="js_pdpDistinguishingFeature">
    <div class="displayBox">
      <h3>${uiLabelMap.FeaturesHeading}</h3>
        <ul class="displayList pdpDistinguishingFeatureList">
          <#list disFeatureTypesList as disFeatureType>
            <#if disFeatureByTypeMap?has_content>
              <#assign disFeatureAndApplList = disFeatureByTypeMap[disFeatureType]![]>
              <#assign disFeatureTypeDescription = "">
              <#assign index= 0/>
              <#list disFeatureAndApplList as disFeatureAndAppl>
                <#assign size = disFeatureAndApplList.size()/>
                <#assign disFeatureDescription = disFeatureAndAppl.description!"">
                <#if productFeatureTypesMap?has_content>
                      <#assign productFeatureTypeDescription = productFeatureTypesMap.get(disFeatureAndAppl.productFeatureTypeId)!"" />
                </#if>
                <#if productFeatureTypeDescription?has_content && productFeatureTypeDescription != disFeatureTypeDescription!"">
                  <#assign disFeatureTypeDescription = productFeatureTypeDescription!"">
                </#if>
	            <#if (index == 0)>
                 <li><div><label>${disFeatureTypeDescription!""}:</label>
                 <#if productFacetTooltipMap?exists && productFacetTooltipMap?has_content>
	              <#assign productFacetTooltip = productFacetTooltipMap.get(disFeatureAndAppl.productFeatureTypeId)!""/>
	              <#if productFacetTooltip?has_content>
                    <#assign facetTooltipTxt = Static["com.osafe.util.Util"].formatToolTipText("${productFacetTooltip}", "${productFacetTooltip?length}")/>
                    <#if facetTooltipTxt?has_content >
                      <a href="javascript:void(0);" onMouseover="javascript:showTooltip('${StringUtil.wrapString(facetTooltipTxt)!""}', this, 'icon');" onMouseout="hideTooltip()" class="toolTipLink">
                        <span class="tooltipIcon"></span>
                      </a>
                    </#if>
                  </#if>
                 </#if>  
                 <span>
                 </#if>
                 <#assign index = index + 1/>
                 ${disFeatureDescription!""}<#if (size > 1) && (index != size)>,&nbsp;</#if>
                <#if (index == size)>
                </span></div></li>
	            </#if>
              </#list>
            </#if>
          </#list>
        </ul>
    </div>
  </div>
 </#if>


<#if disFeatureDescription?has_content>
	<div class="pdpDistinguishingFeature" id="js_pdpDistinguishingFeature_Virtual" style="display:none">
	  <#if disFeatureTypesList?has_content>
	    <div class="displayBox">
	      <h3>${uiLabelMap.FeaturesHeading}</h3>
	        <ul class="displayList pdpDistinguishingFeatureList">
	          <#list disFeatureTypesList as disFeatureType>
	            <#if disFeatureByTypeMap?has_content>
	              <#assign disFeatureAndApplList = disFeatureByTypeMap[disFeatureType]![]>
                  <#assign disFeatureTypeDescription = "">
                  <#assign index= 0/>
	              <#list disFeatureAndApplList as disFeatureAndAppl>
	                <#assign size = disFeatureAndApplList.size()/>
	                <#assign disFeatureDescription = disFeatureAndAppl.description!"">
	                <#if productFeatureTypesMap?has_content>
	                      <#assign productFeatureTypeDescription = productFeatureTypesMap.get(disFeatureAndAppl.productFeatureTypeId)!"" />
	                </#if>
	                <#if productFeatureTypeDescription?has_content && productFeatureTypeDescription != disFeatureTypeDescription!"">
	                  <#assign disFeatureTypeDescription = productFeatureTypeDescription!"">
	                </#if>
	            <#if (index == 0)>
                 <li><div><label>${disFeatureTypeDescription!""}:</label>
				 <#if productFacetTooltipMap?exists && productFacetTooltipMap?has_content>
	              <#assign productFacetTooltip = productFacetTooltipMap.get(disFeatureAndAppl.productFeatureTypeId)!""/>
	              <#if productFacetTooltip?has_content>
                    <#assign facetTooltipTxt = Static["com.osafe.util.Util"].formatToolTipText("${productFacetTooltip}", "${productFacetTooltip?length}")/>
                    <#if facetTooltipTxt?has_content >
                      <a href="javascript:void(0);" onMouseover="javascript:showTooltip('${StringUtil.wrapString(facetTooltipTxt)!""}', this, 'icon');" onMouseout="hideTooltip()" class="toolTipLink">
                        <span class="tooltipIcon"></span>
                      </a>
                    </#if>
                  </#if>
                 </#if>
                 <span>
                 </#if>
                 <#assign index = index + 1/>
                 ${disFeatureDescription!""}<#if (size > 1) && (index != size)>,&nbsp;</#if>
                <#if (index == size)>
                </span></div></li>
	            </#if>
	              </#list>
	            </#if>
	          </#list>
	        </ul>
	    </div>
	  </#if>
	</div>
</#if>
<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign disFeatureMap = productVariantDisFeatureTypeMap.get('${key}')!/>
    <#if disFeatureMap?has_content>
      <#assign varDisFeatureTypesList = disFeatureMap.productFeatureTypes!/>
      <#assign varDisFeatureByTypeMap = disFeatureMap.productFeaturesByType!/>
    </#if>
    <#if !varDisFeatureTypesList?has_content>
      <#assign varDisFeatureTypesList = disFeatureTypesList!/>
    </#if>
    <#if !varDisFeatureByTypeMap?has_content>
      <#assign varDisFeatureByTypeMap = disFeatureByTypeMap!/>
    </#if>
    <div class="pdpDistinguishingFeature" id="js_pdpDistinguishingFeature_${key}" style="display:none">
      <#if varDisFeatureTypesList?has_content>
        <div class="displayBox">
        <h3>${uiLabelMap.FeaturesHeading}</h3>
        <ul class="displayList pdpDistinguishingFeatureList">
          <#list varDisFeatureTypesList as disFeatureType>
            <#if varDisFeatureByTypeMap?has_content>
              <#assign disFeatureAndApplList = varDisFeatureByTypeMap[disFeatureType]![]>
              <#assign disFeatureTypeDescription = "">
              <#assign index= 0/>
              <#list disFeatureAndApplList as disFeatureAndAppl>
                <#assign size = disFeatureAndApplList.size()/>
                <#assign disFeatureDescription = disFeatureAndAppl.description!"">
                <#if productFeatureTypesMap?has_content>
                      <#assign productFeatureTypeDescription = productFeatureTypesMap.get(disFeatureAndAppl.productFeatureTypeId)!"" />
                </#if>
                <#if productFeatureTypeDescription?has_content && productFeatureTypeDescription != disFeatureTypeDescription!"">
                  <#assign disFeatureTypeDescription = productFeatureTypeDescription!"">
                </#if>
	            <#if (index == 0)>
                 <li><div><label>${disFeatureTypeDescription!""}:</label>
                 <#if productFacetTooltipMap?exists && productFacetTooltipMap?has_content>
	              <#assign productFacetTooltip = productFacetTooltipMap.get(disFeatureAndAppl.productFeatureTypeId)!""/>
	              <#if productFacetTooltip?has_content>
                    <#assign facetTooltipTxt = Static["com.osafe.util.Util"].formatToolTipText("${productFacetTooltip}", "${productFacetTooltip?length}")/>
                    <#if facetTooltipTxt?has_content >
                      <a href="javascript:void(0);" onMouseover="javascript:showTooltip('${StringUtil.wrapString(facetTooltipTxt)!""}', this, 'icon');" onMouseout="hideTooltip()" class="toolTipLink">
                        <span class="tooltipIcon"></span>
                      </a>
                    </#if>
                  </#if>
                 </#if>
                 <span>
                 </#if>
                 <#assign index = index + 1/>
                 ${disFeatureDescription!""}<#if (size > 1) && (index != size)>,&nbsp;</#if>
                <#if (index == size)>
                </span></div></li>
	            </#if>
              </#list>
            </#if>
          </#list>
        </ul>
      </div>
    </#if>
  </div>  
</#list>
</#if>
</li>