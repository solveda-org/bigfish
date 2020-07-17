<div id ="productAssocData" class="commonDivHide" style="display:none">
<table class="osafe">
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.RowNoLabel}</th>
    <th class="idCol">${uiLabelMap.FromMASTERProductIdLabel}</th>
    <th class="idCol">${uiLabelMap.ToMASTERProductIdLabel}</th>
    <th class="typeCol">${uiLabelMap.ProductAssocTypeLabel}</th>
    <th class="dateCol">${uiLabelMap.FromDateLabel}</th>
    <th class="dateCol">${uiLabelMap.ThruDateLabel}</th>
  </tr>
  <#if productAssocDataList?exists && productAssocDataList?has_content>
    <#assign rowClass = "1">
    <#assign rowNo = 1>
    <#list productAssocDataList as productAssoc>
      <tr class="<#if rowClass == "2">even</#if>">
        <td class="idCol firstCol" >${rowNo!""}</td>
        <td class="idCol" >${productAssoc.productId!""}</td>
        <td class="idCol">${productAssoc.productIdTo!""}</td>
        <td class="typeCol">${productAssoc.productAssocType!""}</td>
        <td class="dateCol">${productAssoc.fromDate!""}</td>
        <td class="dateCol">${productAssoc.thruDate!""}</td>
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