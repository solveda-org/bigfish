<div id ="facetValueData" class="commonDivHide" style="display:none">
<table class="osafe">
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.RowNoLabel}</th>
    <th class="idCol">${uiLabelMap.FacetGroupIdLabel}</th>
    <th class="idCol">${uiLabelMap.FacetValueIdLabel}</th>
    <th class="idCol">${uiLabelMap.DescriptionLabel}</th>
    <th class="idCol">${uiLabelMap.SequenceNumberLabel}</th>
    <th class="nameCol">${uiLabelMap.PlpSwatchUrlLabel}</th>
    <th class="nameCol">${uiLabelMap.PdpSwatchUrlLabel}</th>
    <th class="nameCol">${uiLabelMap.FromDateLabel}</th>
    <th class="nameCol">${uiLabelMap.ThruDateLabel}</th>
  </tr>
  <#if productFacetValueDataList?exists && productFacetValueDataList?has_content>
    <#assign rowClass = "1">
    <#assign rowNo = 1>
    <#list productFacetValueDataList as productFacetValueData>
      <tr class="<#if rowClass == "2">even</#if>">
        <td class="idCol firstCol" >${rowNo!""}</td>
        <td class="idCol">${productFacetValueData.facetGroupId!}</th>
        <td class="idCol">${productFacetValueData.facetValueId!}</th>
        <td class="idCol">${productFacetValueData.description!}</th>
        <td class="idCol">${productFacetValueData.sequenceNum!}</th>
        <td class="nameCol" >${productFacetValueData.plpSwatchUrl!""}</td>
        <td class="nameCol">${productFacetValueData.pdpSwatchUrl!""}</td>
        <td class="idCol">${productFacetValueData.fromDate!}</th>
        <td class="idCol">${productFacetValueData.thruDate!}</th>
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