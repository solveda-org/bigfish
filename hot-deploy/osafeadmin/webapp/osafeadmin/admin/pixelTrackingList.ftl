<#-- Note, for now we are always going to say the owner is admin. At some point, we will check for owner Id -->
<!-- start pixelTrackingList.ftl -->
<tr class="heading">
  <th class="idCol firstCol">${uiLabelMap.IdLabel}</th>
  <th class="scopeCol">${uiLabelMap.ScopeLabel}</th>
  <th class="nameCol">${uiLabelMap.PagePositionLabel}</th>
  <th class="seqIdCol">${uiLabelMap.SeqNumberLabel}</th>
  <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
  <th class="statusCol">${uiLabelMap.StatusLabel}</th>
</tr>

<#if resultList?has_content>
  <#assign rowClass = "1">
  <#list resultList as pixelTracking>
    <#assign hasNext = pixelTracking_has_next>
    <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
      <td class="idCol firstCol <#if !hasNext>lastRow</#if>" >
        <a href="<@ofbizUrl>${detailPage!}?pixelId=${pixelTracking.pixelId?if_exists}&amp;productStoreId=${productStoreId!}</@ofbizUrl>">${pixelTracking.pixelId?if_exists}</a>
      </td>
      <td class="scopeCol <#if !hasNext>lastRow</#if>">
        ${pixelTracking.pixelScope!""}
      </td>
      <td class="nameCol <#if !hasNext>lastRow</#if>">
        ${pixelTracking.pixelPagePosition!""}
      </td>
      <td class="seqIdCol <#if !hasNext>lastRow</#if>">
        ${pixelTracking.pixelSequenceNum!""}
      </td>
      <td class="descCol <#if !hasNext>lastRow</#if>">
        ${pixelTracking.description}
      </td>
      <td class="statusCol <#if !hasNext>lastRow</#if>">
        <#if contentMap?has_content && pixelTracking.contentId?has_content>
          <#assign thisContent = contentMap.get(pixelTracking.contentId) />
          <#if thisContent?has_content>
            <#assign statusId = thisContent.statusId!"CTNT_DEACTIVATED" />
            <#if statusId != "CTNT_PUBLISHED">
              <#assign statusId = "CTNT_DEACTIVATED">
            </#if>
            <#assign statusItem = delegator.findOne("StatusItem", {"statusId" : statusId}, false)>
            ${statusItem.description!statusItem.get("description",locale)!statusItem.statusId}
          </#if>
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
   ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
<!-- end pixelTrackingList.ftl -->