<!-- start listBox -->
      <tr class="heading">
        <th class="idCol firstCol">${uiLabelMap.ProductFeatureLabel}</th>
        <th class="descCol">${uiLabelMap.ProductFeatureTypeLabel}</th>
        <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
      </tr>
      <#if resultList?exists && resultList?has_content>
        <#assign rowClass = "1"/>
        <#list resultList as productFeature>
          <#assign hasNext = productFeature_has_next>
          <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
            <td class="idCol <#if !productFeature_has_next?if_exists>lastRow</#if> firstCol" ><a href="javascript:set_values('${productFeature.productFeatureId?if_exists}','${productFeature.description?if_exists}')">${productFeature.productFeatureId?if_exists}</a></td>
            <td class="descCol <#if !productFeature_has_next?if_exists>lastRow</#if>">${productFeature.productFeatureTypeDescription!productFeature.productFeatureTypeId!""}</td>
            <td class="descCol">${productFeature.description?if_exists}</td>
          </tr>
          <#if rowClass == "2">
            <#assign rowClass = "1">
          <#else>
            <#assign rowClass = "2">
          </#if>
        </#list>
      </#if>
<!-- end listBox -->