<div id ="productAttributesData" class="commonDivHide" style="display:none">
<table class="osafe">
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.RowNoLabel}</th>
    <th class="idCol">${uiLabelMap.ProductIdLabel}</th>
    <th class="nameCol">${uiLabelMap.AttrNameLabel}</th>
    <th class="descCol">${uiLabelMap.AttrValueLabel}</th>
  </tr>
  <#if productAttributesDataList?exists && productAttributesDataList?has_content>
    <#assign rowClass = "1">
    <#assign rowNo = 1>
    <#list productAttributesDataList as productAttribute>
      <tr class="<#if rowClass == "2">even</#if>">
        <td class="idCol firstCol" >${rowNo!""}</td>
        <td class="idCol">${productAttribute.productId!""}</td>
        <td class="nameCol">${productAttribute.attrName!""}</td>
        <td class="descCol">${productAttribute.attrValue!""}</td>
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