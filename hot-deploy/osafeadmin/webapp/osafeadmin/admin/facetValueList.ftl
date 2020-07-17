<#if resultList?has_content>
    <thead>
      <tr class="heading">
        <th class="idCol firstCol">${uiLabelMap.FacetGroupIdLabel}</th>
        <th class="descCol">${uiLabelMap.FacetGroupDescLabel}</th>
        <th class="valueCol">${uiLabelMap.FacetValueLabel}</th>
      </tr>
    </thead>
    <tbody>
      <#assign rowClass = "1">
      <#list resultList as category>
        <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
          <td class="idCol firstCol">${category.productFeatureGroupId!}</td>
          <td class="descCol">${category.description!}</td>
          <td class="valueCol">
            <a class="editLink" href="<@ofbizUrl>${facetValueDetail!}?productFeatureGroupId=${category.productFeatureGroupId}</@ofbizUrl>">${uiLabelMap.EditLabel}</a>
          </td>
          <#if rowClass == "2">
            <#assign rowClass = "1">
          <#else>
            <#assign rowClass = "2">
          </#if>
        </tr>
        
      </#list>
    </tbody>
<#else>
  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoResult")}
</#if>