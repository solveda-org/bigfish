<#-- Note, for now we are always going to say the owner is admin. At some point, we will check for owner Id -->
<!-- start pdpVariantCaptionList.ftl -->
<tr class="heading">
  <th class="idCol firstCol">${uiLabelMap.CaptionIDLabel}</th>
  <th class="nameCol">${uiLabelMap.CaptionLabel}</th>
  <th class="dateCol">${uiLabelMap.CreatedDateLabel}</th>
  <th class="dateCol">${uiLabelMap.UpdatedDateLabel}</th>
</tr>

<#if pdpVariantCaptionList?has_content>
  <#assign rowClass = "1">
  <#list pdpVariantCaptionList as pdpVariantCaption>
    <#assign hasNext = pdpVariantCaption_has_next>
    <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
      <td class="idCol firstCol" >
        <a href="<@ofbizUrl>${detailPage}?productFeatureTypeId=${pdpVariantCaption.productFeatureTypeId}</@ofbizUrl>">${pdpVariantCaption.productFeatureTypeId!"N/A"}</a>
      </td>
      <td class="nameCol <#if !hasNext>lastRow</#if>" >
        ${pdpVariantCaption.description!"N/A"}
      </td>
      <td class="dateCol <#if !hasNext>lastRow</#if>">
        <#if pdpVariantCaption.createdStamp?has_content>
          ${pdpVariantCaption.createdStamp?string(preferredDateFormat)}
        </#if>
      </td>
      <td class="dateCol <#if !hasNext>lastRow</#if>">
        <#if pdpVariantCaption.lastUpdatedStamp?has_content>
          ${pdpVariantCaption.lastUpdatedStamp?string(preferredDateFormat)}
        </#if>
      </td>
    </tr>
    <#-- toggle the row color -->
    <#if rowClass == "2">
      <#assign rowClass = "1">
    <#else>
      <#assign rowClass = "2">
    </#if>
  </#list>
<#else>
  <tr>
    <td colspan="4" class="boxNumber">${uiLabelMap.NoDataAvailableInfo}</td>
  </tr>
</#if>
<!-- end pdpVariantCaptionList.ftl -->