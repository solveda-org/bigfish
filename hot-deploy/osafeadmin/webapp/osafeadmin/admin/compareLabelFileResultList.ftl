<!-- start listBox -->
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.KeyLabel}</th>
    <th class="actionColSmall"></th>
    <th class="descCol">${uiLabelMap.CompareLabelResultLabel}</th>
    <th class="actionColSmall"></th>
  </tr>
  <#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1"/>
    <#list resultList as compareLabelResult>
      <#assign hasNext = compareLabelResult_has_next>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="idCol <#if !lblCompareResult_has_next?if_exists>lastRow</#if> firstCol" >${compareLabelResult.key?if_exists}</td>
        <td class="actionColSmall <#if !lblCompareResult_has_next?if_exists>lastRow</#if>">
          <#assign tooltipData = "<div class=tooltipData>"/>
          <#if compareLabelResult.fromMap?has_content>
            <#assign keyYourLabelFile = compareLabelResult.fromMap.key!"&nbsp;"/>
            <#assign categoryYourLabelFile = compareLabelResult.fromMap.category!"&nbsp;"/>
            <#assign valueYourLabelFile = compareLabelResult.fromMap.value!"&nbsp;"/>
            <#else>
              <#assign keyYourLabelFile = uiLabelMap.CompareLabelKeysNotPresentInfo/>
              <#assign categoryYourLabelFile = "&nbsp;"/>
              <#assign valueYourLabelFile = "&nbsp;"/>
          </#if>
          <#assign tooltipData = tooltipData+"<div class=tooltipFirstSubheading>${uiLabelMap.CompareLabelYourFileInfo}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.KeyCaption}</label></div><div class=tooltipValue>${keyYourLabelFile}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.CategoryCaption}</label></div><div class=tooltipValue>${categoryYourLabelFile}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.ValueCaption}</label></div><div class=tooltipValue>${valueYourLabelFile}</div>"/>
          <#if compareLabelResult.toMap?has_content>
            <#assign keyCompareToLabelFile = compareLabelResult.toMap.key!"&nbsp;"/>
            <#assign categoryCompareToLabelFile = compareLabelResult.toMap.category!"&nbsp;"/>
            <#assign valueCompareToLabelFile = compareLabelResult.toMap.value!"&nbsp;"/>
            <#else>
              <#assign keyCompareToLabelFile = uiLabelMap.CompareLabelKeysNotPresentInfo/>
              <#assign categoryCompareToLabelFile = "&nbsp;"/>
              <#assign valueCompareToLabelFile = "&nbsp;"/>
          </#if>
          <#assign tooltipData = tooltipData+"<div class=tooltipSubheading>${uiLabelMap.CompareLabelCompareToFileInfo}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.KeyCaption}</label></div><div class=tooltipValue>${keyCompareToLabelFile}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.CategoryCaption}</label></div><div class=tooltipValue>${categoryCompareToLabelFile}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.ValueCaption}</label></div><div class=tooltipValue>${valueCompareToLabelFile}</div>"/>
          <#assign tooltipData = tooltipData+"</div>"/>
          <#assign valuesMatch = "true"/>
          <#if compareLabelResult.type?has_content && compareLabelResult.type == '3'>
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
        <#if compareLabelResult.type?has_content>
          <#if compareLabelResult.type == "1">
            ${uiLabelMap.CompareLabelKeysNotInYourLabelFileListInfo}
          <#elseif compareLabelResult.type == "2">
            ${uiLabelMap.CompareLabelKeysNotInUploadedFileListInfo}
          <#elseif compareLabelResult.type == "3">
            ${uiLabelMap.CompareLabelKeysInBothFileListInfo}
          </#if>
        </#if>
        </td>
        <td class="actionColSmall">
          <#if compareLabelResult.type?has_content>
            <#if compareLabelResult.type == "2" && compareLabelResult.key?has_content>
              <#assign keyMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("key", compareLabelResult.key)>
              <#assign addKeyTooltipText = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","AddKeyTooltip",keyMap, locale ) />
              <a href="<@ofbizUrl>addMissingUiLabel?addKey=${compareLabelResult.key?if_exists}</@ofbizUrl>" onMouseover="javascript:showTooltip(event,'${addKeyTooltipText!""}');" onMouseout="hideTooltip()"><span class="createIcon"></span></a>
            </#if>
            <#if compareLabelResult.type == "3" && compareLabelResult.key?has_content && valuesMatch == "false">
              <#assign keyMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("key", compareLabelResult.key, "value", valueCompareToLabelFile)>
              <#assign addKeyTooltipText = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","SyncKeyAndValueTooltip",keyMap, locale ) />
              <a href="javascript:setNewValue('${compareLabelResult.key?if_exists}','${valueCompareToLabelFile!}');" onMouseover="javascript:showTooltip(event,'${addKeyTooltipText!""}');" onMouseout="hideTooltip()"><span class="syncIcon"></span></a>
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
<tr>
  <td colspan="4">
    <form method="post" id="passParameters" name="${detailFormName}" action="<@ofbizUrl>changeUiLabelValue</@ofbizUrl>">
        <input type="hidden" name="key" id="key" value="" />
        <input type="hidden" name="newValue" id="newValue" value="" />
    </form>
  </td>
</tr>
<!-- end listBox -->