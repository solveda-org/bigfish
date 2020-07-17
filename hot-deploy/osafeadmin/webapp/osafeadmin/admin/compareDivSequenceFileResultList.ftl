<!-- start listBox -->
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.ScreenLabel}</th>
    <th class="idCol">${uiLabelMap.KeyLabel}</th>
    <th class="actionColSmall"></th>
    <th class="descCol">${uiLabelMap.CompareLabelResultLabel}</th>
    <th class="actionColSmall"></th>
  </tr>
  <#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1"/>
    <#list resultList as compareDivResult>
      <#assign hasNext = compareDivResult_has_next>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="idCol <#if !lblCompareResult_has_next?if_exists>lastRow</#if> firstCol" >${compareDivResult.screen?if_exists}</td>
        <td class="idCol <#if !lblCompareResult_has_next?if_exists>lastRow</#if>" >${compareDivResult.key?if_exists}</td>
        <td class="actionColSmall <#if !lblCompareResult_has_next?if_exists>lastRow</#if>">
          <#assign tooltipData = "<div class=tooltipData>"/>
          <#if compareDivResult.fromMap?has_content>
            <#assign keyYourLabelFile = compareDivResult.fromMap.key!"&nbsp;"/>
            <#assign screenYourLabelFile = compareDivResult.fromMap.screen!"&nbsp;"/>
            <#assign valueYourLabelFile = compareDivResult.fromMap.value!"&nbsp;"/>
            <#else>
              <#assign keyYourLabelFile = uiLabelMap.CompareLabelKeysNotPresentInfo/>
              <#assign screenYourLabelFile = "&nbsp;"/>
              <#assign valueYourLabelFile = "&nbsp;"/>
          </#if>
          <#assign tooltipData = tooltipData+"<div class=tooltipFirstSubheading>${uiLabelMap.CompareLabelYourFileInfo}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.ScreenCaption}</label></div><div class=tooltipValue>${screenYourLabelFile}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.KeyCaption}</label></div><div class=tooltipValue>${keyYourLabelFile}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.ValueCaption}</label></div><div class=tooltipValue>${valueYourLabelFile}</div>"/>
          <#if compareDivResult.toMap?has_content>
            <#assign keyCompareToLabelFile = compareDivResult.toMap.key!"&nbsp;"/>
            <#assign screenCompareToLabelFile = compareDivResult.toMap.screen!"&nbsp;"/>
            <#assign valueCompareToLabelFile = compareDivResult.toMap.value!"&nbsp;"/>
            <#else>
              <#assign keyCompareToLabelFile = uiLabelMap.CompareLabelKeysNotPresentInfo/>
              <#assign screenCompareToLabelFile = "&nbsp;"/>
              <#assign valueCompareToLabelFile = "&nbsp;"/>
          </#if>
          <#assign tooltipData = tooltipData+"<div class=tooltipSubheading>${uiLabelMap.CompareLabelCompareToFileInfo}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.ScreenCaption}</label></div><div class=tooltipValue>${screenCompareToLabelFile}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.KeyCaption}</label></div><div class=tooltipValue>${keyCompareToLabelFile}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.ValueCaption}</label></div><div class=tooltipValue>${valueCompareToLabelFile}</div>"/>
          <#assign tooltipData = tooltipData+"</div>"/>
          <#assign valuesMatch = "true"/>
          <#if compareDivResult.type?has_content && compareDivResult.type == '3'>
              <#if (valueCompareToLabelFile?has_content && valueYourLabelFile?has_content) && (valueCompareToLabelFile != valueYourLabelFile)>
                  <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="compareDescIcon"></span></a>
                  <#assign valuesMatch = "false"/>
              </#if>
          </#if>
          <#if valuesMatch?has_content && valuesMatch == "true">
          <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
          </#if>
        </td>
        <td class="descCol <#if !lblCompareResult_has_next?if_exists>lastRow</#if>">
        <!-- type  1 searchKeysNotInYourLabelFile |  2 searchKeysNotInUploadedFile  |  3  searchKeysInBothFile -->
        <#if compareDivResult.type?has_content>
          <#if compareDivResult.type == "1">
            ${uiLabelMap.CompareLabelKeysNotInYourLabelFileListInfo}
          <#elseif compareDivResult.type == "2">
            ${uiLabelMap.CompareLabelKeysNotInUploadedFileListInfo}
          <#elseif compareDivResult.type == "3">
            ${uiLabelMap.CompareLabelKeysInBothFileListInfo}
          </#if>
        </#if>
        </td>
        <td class="actionColSmall">
          <#if compareDivResult.type?has_content>
            <#if compareDivResult.type == "2" && compareDivResult.key?has_content>
              <#assign keyMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("key", compareDivResult.key)>
              <#assign addKeyTooltipText = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","AddKeyTooltip",keyMap, locale ) />
              <a href="<@ofbizUrl>addMissingDivSeqItem?addKey=${compareDivResult.key?if_exists}&amp;screenType=${compareDivResult.screen?if_exists}</@ofbizUrl>" onMouseover="javascript:showTooltip(event,'${addKeyTooltipText!""}');" onMouseout="hideTooltip()"><span class="createIcon"></span></a>
            </#if>
            <#if compareDivResult.type == "3" && compareDivResult.key?has_content && valuesMatch == "false">
              <#assign keyMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("key", compareDivResult.key, "value", valueCompareToLabelFile)>
              <#assign addKeyTooltipText = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","SyncKeyAndValueTooltip",keyMap, locale ) />
              <a href="<@ofbizUrl>changeDivSeqItemValue?key=${compareDivResult.key?if_exists}&amp;screenType=${compareDivResult.screen?if_exists}&amp;newValue=${valueCompareToLabelFile}</@ofbizUrl>" onMouseover="javascript:showTooltip(event,'${addKeyTooltipText!""}');" onMouseout="hideTooltip()"><span class="syncIcon"></span></a>
            </#if>
          </#if>
        </td>
      </tr>
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
    </#list>
  </#if>
<!-- end listBox -->