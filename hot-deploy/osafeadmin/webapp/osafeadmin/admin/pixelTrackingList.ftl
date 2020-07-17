<#-- Note, for now we are always going to say the owner is admin. At some point, we will check for owner Id -->
<!-- start pixelTrackingList.ftl -->
<tr class="heading">
  <th class="idCol firstCol">${uiLabelMap.IdLabel}</th>
  <th class="statusCol">${uiLabelMap.StatusLabel}</th>
  <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
  <th class="actionColSmall"></th>
  <th class="dateCol">${uiLabelMap.ActiveDateLabel}</th>
  <th class="dateCol lastCol">${uiLabelMap.CreatedDateLabel}</th>
</tr>

<#if pixelTrackings?has_content>
  <#assign rowClass = "1">
  <#list pixelTrackings as pixelTracking>
    <#assign hasNext = pixelTracking_has_next>
    <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
      <td class="idCol firstCol <#if !hasNext>lastRow</#if>" >
        <a href="<@ofbizUrl>${detailPage!}?pixelId=${pixelTracking.pixelId?if_exists}&amp;productStoreId=${productStoreId!}</@ofbizUrl>">${pixelTracking.pixelId?if_exists}</a>
      </td>
      <td class="statusCol <#if !hasNext>lastRow</#if>">
        <#if contentMap?has_content && pixelTracking.contentId?has_content>
          <#assign thisContent = contentMap.get(pixelTracking.contentId) />
          <#if thisContent?has_content>
            <#assign pixelCode = contentTextMap.get(pixelTracking.contentId!)! />
            <#assign pixelCode = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(pixelCode, ADM_TOOLTIP_MAX_CHAR!)/>
            <#assign statusId = thisContent.statusId!"CTNT_DEACTIVATED" />
            <#if statusId != "CTNT_PUBLISHED">
              <#assign statusId = "CTNT_DEACTIVATED">
            </#if>
            <#assign statusItem = delegator.findOne("StatusItem", {"statusId" : statusId}, false)>
            ${statusItem.description!statusItem.get("description",locale)!statusItem.statusId}
          </#if>
        </#if>
      </td>
      <td class="descCol <#if !hasNext>lastRow</#if>">
        ${pixelTracking.description}
      </td>
      <td class="actionColSmall <#if !hasNext>lastRow</#if>">
        <#assign tooltipData = "<div class=tooltipData>"/>
        <#assign tooltipData = tooltipData+"<div class=tooltipFirstSubheading>${uiLabelMap.PixelScopeCaption}</div><div>${pixelTracking.pixelScope}</div>"/>
        <#assign tooltipData = tooltipData+"<div class=tooltipFirstSubheading>${uiLabelMap.PixelCodeCaption}</div><div>${pixelCode!}</div>"/>
        <#assign tooltipData = tooltipData+"</div>"/>
        <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
      </td>
      <td class="dateCol <#if !hasNext>lastRow</#if>">
        <#if statusId?has_content && statusId == "CTNT_PUBLISHED" >
            <#assign lastModifiedDate = "" />
            <#if thisContent.lastModifiedDate?has_content>
                ${thisContent.lastModifiedDate?string(preferredDateFormat)}
            </#if>
        </#if>
      </td>
      <td class="dateCol <#if !hasNext>lastRow</#if>">
        ${(thisContent.createdDate?string(preferredDateFormat))!""}
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
    <td colspan="6" class="boxNumber">${uiLabelMap.NoDataAvailableInfo}</td>
  </tr>
</#if>
<!-- end pixelTrackingList.ftl -->