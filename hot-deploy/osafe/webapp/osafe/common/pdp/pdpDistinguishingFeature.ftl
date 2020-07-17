<#if disFeatureTypesList?has_content>
 <div id="pdpDistinguishingFeature">
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
                                       </ul>
                                       </li>
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
 </div>
</#if>
