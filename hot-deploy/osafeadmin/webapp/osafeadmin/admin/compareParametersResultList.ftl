<!-- start listBox -->
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.ParameterNameLabel}</th>
    <th class="actionColSmall"></th>
    <th class="descCol">${uiLabelMap.CompareParameterResultLabel}</th>
    <th class="actionColSmall"></th>
  </tr>
  <#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1"/>
    <#list resultList as compareParamResult>
      <#assign hasNext = compareParamResult_has_next>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="idCol <#if !hasNext?if_exists>lastRow</#if> firstCol" >${compareParamResult.key?if_exists}</td>
        <td class="actionColSmall <#if !hasNext?if_exists>lastRow</#if>">
          <#assign tooltipData = "<div class=tooltipData>"/>
          <#if compareParamResult.fromMap?has_content>
            <#assign keyYourDatabase = compareParamResult.fromMap.parmKey!"&nbsp;"/>
            <#assign categoryYourDatabase = compareParamResult.fromMap.parmCategory!"&nbsp;"/>
            <#assign valueYourDatabase = compareParamResult.fromMap.parmValue!"&nbsp;"/>
            <#else>
              <#assign keyYourDatabase = uiLabelMap.CompareParamKeysNotPresentInfo/>
              <#assign categoryYourDatabase = "&nbsp;"/>
              <#assign valueYourDatabase = "&nbsp;"/>
          </#if>
          <#assign tooltipData = tooltipData+"<div class=tooltipFirstSubheading>${uiLabelMap.CompareParamYourDatabaseInfo}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.ParameterNameCaption}</label></div><div class=tooltipValue>${keyYourDatabase!}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.CategoryCaption}</label></div><div class=tooltipValue>${categoryYourDatabase!}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.ValueCaption}</label></div><div class=tooltipValue>${valueYourDatabase!}</div>"/>
          <#if compareParamResult.toMap?has_content>
            <#assign keyCompareToParamFile = compareParamResult.toMap.parmKey!"&nbsp;"/>
            <#assign categoryCompareToParamFile = compareParamResult.toMap.parmCategory!"&nbsp;"/>
            <#assign valueCompareToParamFile = compareParamResult.toMap.parmValue!"&nbsp;"/>
            <#else>
              <#assign keyCompareToParamFile = uiLabelMap.CompareParamKeysNotPresentInfo/>
              <#assign categoryCompareToParamFile = "&nbsp;"/>
              <#assign valueCompareToParamFile = "&nbsp;"/>
          </#if>
          <#assign tooltipData = tooltipData+"<div class=tooltipSubheading>${uiLabelMap.CompareParamCompareToFileInfo}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.ParameterNameCaption}</label></div><div class=tooltipValue>${keyCompareToParamFile!}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.CategoryCaption}</label></div><div class=tooltipValue>${categoryCompareToParamFile!}</div>"/>
          <#assign tooltipData = tooltipData+"<div class=tooltipCaption><label>${uiLabelMap.ValueCaption}</label></div><div class=tooltipValue>${valueCompareToParamFile!}</div>"/>
          <#assign tooltipData = tooltipData+"</div>"/>
          <#assign valuesMatch = "true"/>
          <#if compareParamResult.type?has_content && compareParamResult.type == '3'>
              <#if (valueCompareToParamFile?has_content && valueYourDatabase?has_content) && (valueCompareToParamFile != valueYourDatabase)>
                  <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="compareDescIcon"></span></a>
                  <#assign valuesMatch = "false"/>
              </#if>
          </#if>
          <#if valuesMatch?has_content && valuesMatch == "true">
              <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
          </#if>
        </td>
        <td class="descCol <#if !hasNext?if_exists>lastRow</#if>">
        <!-- type  1 searchKeysNotInYourDatabase |  2 searchKeysNotInUploadedFile  |  3  searchKeysInBoth -->
        <#if compareParamResult.type?has_content>
          <#if compareParamResult.type == "1">
            ${uiLabelMap.CompareParamKeysNotInUploadedFileListInfo}
          <#elseif compareParamResult.type == "2">
            ${uiLabelMap.CompareParamKeysNotInYourDatabaseListInfo}
          <#elseif compareParamResult.type == "3">
            ${uiLabelMap.CompareParamKeysInBothListInfo}
          </#if>
        </#if>
        </td>
        <td class="actionColSmall <#if !hasNext?if_exists>lastRow</#if>">
          <#if compareParamResult.type?has_content>
            <#if compareParamResult.type == "2" && compareParamResult.key?has_content>
              <#assign keyMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("key", compareParamResult.key)>
              <#assign addParamKeyTooltip = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","AddParamKeyTooltip",keyMap, locale ) />
              <a href="<@ofbizUrl>addMissingParameter?addKey=${compareParamResult.key?if_exists}</@ofbizUrl>" onMouseover="javascript:showTooltip(event,'${addParamKeyTooltip!""}');" onMouseout="hideTooltip()"><span class="createIcon"></span></a>
            </#if>
            <#if compareParamResult.type == "3" && compareParamResult.key?has_content && valuesMatch == "false">
              <#assign keyMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("key", compareParamResult.key, "value", valueCompareToParamFile)>
              <#assign addParamKeyTooltip = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","SyncKeyAndValueTooltip",keyMap, locale ) />
              <a href="javascript:setNewValue('${compareParamResult.key?if_exists}','${valueCompareToParamFile!}');" onMouseover="javascript:showTooltip(event,'${addParamKeyTooltip!""}');" onMouseout="hideTooltip()"><span class="syncIcon"></span></a>
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
<td colspan="4">
    <form method="post" id="passParameters" name="${detailFormName}" action="<@ofbizUrl>changeParameterValue</@ofbizUrl>">
        <input type="hidden" name="parmKey" id="key" value="" />
        <input type="hidden" name="newValue" id="newValue" value="" />
    </form>
  </td>
</tr>
<!-- end listBox -->