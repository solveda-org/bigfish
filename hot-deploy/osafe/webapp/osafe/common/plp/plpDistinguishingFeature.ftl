<#if plpDisFeatureTypesList?has_content>
 <li class="${request.getAttribute("attributeClass")!}">
  <ul class="plpDistinguishingFeatureList">
          <#list plpDisFeatureTypesList as disFeatureType>
            <#assign index= 0/>
            <#if plpDisFeatureByTypeMap?has_content>
              <#assign disFeatureAndApplList = plpDisFeatureByTypeMap[disFeatureType]![]>
              <#list disFeatureAndApplList as disFeatureAndAppl>
                <#assign index = index + 1/>
                <#assign size = disFeatureAndApplList.size()/>
                <#assign disFeatureDescription = disFeatureAndAppl.description!"">
                <#if plpProductFeatureTypesMap?has_content>
                      <#assign productFeatureTypeDescription = plpProductFeatureTypesMap.get(disFeatureAndAppl.productFeatureTypeId)!"" />
                </#if>
                <#if plpProductFeatureTypesMap?has_content && productFeatureTypeDescription != disFeatureTypeDescription!"">
                  <#assign disFeatureTypeDescription = productFeatureTypeDescription!"">
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
  </li>
</#if>


