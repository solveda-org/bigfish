<table class="osafe">
    <thead>
      <tr class="heading">
        <th class="idCol firstCol">${uiLabelMap.FacetGroupLabel}</th>
        <th class="descCol">${uiLabelMap.FacetValueLabel}</th>
      </tr>
    </thead>
<#if resultList?has_content>
    <tbody>
      <#assign rowClass = "1">
      <#list resultList as productFeatureType>
        <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
          <td class="idCol firstCol">${productFeatureType.description!}</td>
          <td class="descCol">
            <a class="editLink" href="<@ofbizUrl>${productFeatureDetail!}?productFeatureTypeId=${productFeatureType.productFeatureTypeId}&productId=${parameters.productId!}</@ofbizUrl>">${uiLabelMap.EditLabel}</a>
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

</table>