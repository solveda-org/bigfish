<div id ="featureSwatchesData" class="commonDivHide" style="display:none">
<table class="osafe">
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.RowNoLabel}</th>
    <th class="idCol firstCol">${uiLabelMap.FeatureLabel}</th>
    <th class="nameCol">${uiLabelMap.PLPDefaultSwatchLabel}</th>
    <th class="nameCol">${uiLabelMap.PDPDefaultSwatchLabel}</th>
  </tr>
  <#if productFeatureSwatchDataList?exists && productFeatureSwatchDataList?has_content>
    <#assign rowClass = "1">
    <#assign rowNo = 1>
    <#list productFeatureSwatchDataList as productFeatureSwatch>
      <tr class="<#if rowClass == "2">even</#if>">
        <td class="idCol firstCol" >${rowNo!""}</td>
        <td class="idCol firstCol">${productFeatureSwatch.featureId!}</th>
        <td class="nameCol" >${productFeatureSwatch.plpSwatchImage!""}</td>
        <td class="nameCol">${productFeatureSwatch.pdpSwatchImage!""}</td>
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