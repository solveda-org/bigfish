<div id ="productCatData" class="commonDivHide" style="display:none">
<table class="osafe">
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.RowNoLabel}</th>
    <th class="idCol firstCol">${uiLabelMap.CategoryIdLabel}</th>
    <th class="nameCol">${uiLabelMap.ParentCategoryIdLabel}</th>
    <th class="descCol">${uiLabelMap.CategoryNameLabel}</th>
    <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
    <th class="nameCol">${uiLabelMap.LongDescLabel}</th>
    <th class="descCol">${uiLabelMap.PLPImageLabel}</th>
    <th class="nameCol">${uiLabelMap.AdditionalPLPTextLabel}</th>
    <th class="nameCol">${uiLabelMap.AdditionalPDPTextLabel}</th>
    <th class="dateCol">${uiLabelMap.FromDateLabel}</th>
    <th class="dateCol">${uiLabelMap.ThruDateLabel}</th>
  </tr>
  <#if productCatDataList?exists && productCatDataList?has_content>
    <#assign rowClass = "1">
    <#assign rowNo = 1>
    <#list productCatDataList as productCategory>
      <tr class="<#if rowClass == "2">even</#if>">
        <td class="idCol firstCol" >${rowNo!""}</td>
        <td class="idCol firstCol">${productCategory.productCategoryId!""}</td>
        <td class="nameCol">${productCategory.parentCategoryId!""}</td>
        <td class="descCol">${productCategory.categoryName!""}</td>
        <td class="descCol">${productCategory.description!""}</td>
        <td class="nameCol">${productCategory.longDescription!""}</td>
        <td class="nameCol">${productCategory.plpImageName!""}</td>
        <td class="nameCol">${productCategory.plpText!""}</td>
        <td class="nameCol">${productCategory.pdpText!""}</td>
        <td class="nameCol">${productCategory.fromDate!""}</td>
        <td class="nameCol">${productCategory.thruDate!""}</td>
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