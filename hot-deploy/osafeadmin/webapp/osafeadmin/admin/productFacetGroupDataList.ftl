<div id ="facetGroupData" class="commonDivHide" style="display:none">
<table class="osafe">
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.RowNoLabel}</th>
    <th class="idCol">${uiLabelMap.FacetGroupIdLabel}</th>
    <th class="idCol">${uiLabelMap.DescriptionLabel}</th>
    <th class="idCol">${uiLabelMap.ProductCategoryIdLabel}</th>
    <th class="idCol">${uiLabelMap.SequenceNumberLabel}</th>
    <th class="nameCol">${uiLabelMap.TooltipLabel}</th>
    <th class="nameCol">${uiLabelMap.MinDisplayLabel}</th>
    <th class="nameCol">${uiLabelMap.MaxDisplayLabel}</th>
    <th class="nameCol">${uiLabelMap.FromDateLabel}</th>
    <th class="nameCol">${uiLabelMap.ThruDateLabel}</th>
  </tr>
  <#if productFacetGroupDataList?exists && productFacetGroupDataList?has_content>
    <#assign rowClass = "1">
    <#assign rowNo = 1>
    <#list productFacetGroupDataList as productFacetGroupData>
      <tr class="<#if rowClass == "2">even</#if>">
        <td class="idCol firstCol" >${rowNo!""}</td>
        <td class="idCol">${productFacetGroupData.facetGroupId!}</th>
        <td class="idCol">${productFacetGroupData.description!}</th>
        <td class="idCol">${productFacetGroupData.productCategoryId!}</th>
        <td class="idCol">${productFacetGroupData.sequenceNum!}</th>
        <td class="idCol">${productFacetGroupData.tooltip!}</th>
        <td class="idCol">${productFacetGroupData.minDisplay!}</th>
        <td class="nameCol" >${productFacetGroupData.maxDisplay!""}</td>
        <td class="nameCol">${productFacetGroupData.fromDate!""}</td>
        <td class="nameCol">${productFacetGroupData.thruDate!""}</td>
      </tr>
      <#-- toggle the row color -->
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
      <#assign rowNo = rowNo+1/>
    </#list>
  <#else>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
  </#if>
</table>
</div>