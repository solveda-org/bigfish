<div class="pdpDistinguishingFeature" id="pdpDistinguishingFeature">
  <#if disFeatureTypesList?has_content>
    <div class="displayBox">
      <h3>${uiLabelMap.FeaturesHeading}</h3>
        <ul>
          <#list disFeatureTypesList as disFeatureType>
            <#assign index= 0/>
            <#if disFeatureByTypeMap?has_content>
              <#assign disFeatureAndApplList = disFeatureByTypeMap[disFeatureType]![]>
              <#list disFeatureAndApplList as disFeatureAndAppl>
                <#assign index = index + 1/>
                <#assign size = disFeatureAndApplList.size()/>
                <#assign disFeatureDescription = disFeatureAndAppl.description!"">
                <#assign productFeatureType = disFeatureAndAppl.getRelatedOne("ProductFeatureType")!"" />
                <#if productFeatureType?has_content && productFeatureType.description != disFeatureTypeDescription!"">
                  <#assign disFeatureTypeDescription = productFeatureType.description!"">
                   <#if (index > 1)>
                     </ul></li>
                   </#if>
	               <li>
	                 <label>${disFeatureTypeDescription!""}:</label>
	                   <ul>
                </#if>
	            <li>
	              <span>${disFeatureDescription!""}</span>
	            </li>
                <#if (index == size)>
                  </ul>
                  </li>
                </#if>
              </#list>
            </#if>
          </#list>
        </ul>
    </div>
  </#if>
</div>
<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign disFeatureMap = dispatcher.runSync("getProductFeaturesByType", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", key,"productFeatureApplTypeId", "DISTINGUISHING_FEAT", "userLogin", userLogin))?if_exists/>

    <#assign varDisFeatureTypesList = disFeatureMap.productFeatureTypes!/>
    <#assign varDisFeatureByTypeMap = disFeatureMap.productFeaturesByType!/>
    <#if !varDisFeatureTypesList?has_content>
      <#assign varDisFeatureTypesList = disFeatureTypesList!/>
    </#if>
    <#if !varDisFeatureByTypeMap?has_content>
      <#assign varDisFeatureByTypeMap = disFeatureByTypeMap!/>
    </#if>
    <div class="pdpDistinguishingFeature" id="pdpDistinguishingFeature_${key}" style="display:none">
      <#if varDisFeatureTypesList?has_content>
        <div class="displayBox">
        <h3>${uiLabelMap.FeaturesHeading}</h3>
        <ul>
          <#list varDisFeatureTypesList as disFeatureType>
            <#assign index= 0/>
            <#if varDisFeatureByTypeMap?has_content>
              <#assign disFeatureAndApplList = varDisFeatureByTypeMap[disFeatureType]![]>
              <#list disFeatureAndApplList as disFeatureAndAppl>
                <#assign index = index + 1/>
                <#assign size = disFeatureAndApplList.size()/>
                <#assign disFeatureDescription = disFeatureAndAppl.description!"">
                <#assign productFeatureType = disFeatureAndAppl.getRelatedOne("ProductFeatureType")!"" />
                <#if productFeatureType?has_content && productFeatureType.description != disFeatureTypeDescription!"">
                  <#assign disFeatureTypeDescription = productFeatureType.description!"">
                   <#if (index > 1)>
                     </ul></li>
                   </#if>
	               <li>
	                 <label>${disFeatureTypeDescription!""}:</label>
	                   <ul>
                </#if>
	            <li>
	              <span>${disFeatureDescription!""}</span>
	            </li>
                <#if (index == size)>
                  </ul>
                  </li>
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
