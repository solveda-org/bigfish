<tr class="heading">
  <th class="nameCol firstCol">${uiLabelMap.FileNameLabel}</th>
  <th class="actionCol"></th>
  <th class="actionCol"></th>
  <th class="typeCol">${uiLabelMap.MediaFolderLabel}</th>
  <th class="sizeCol">${uiLabelMap.SizeLabel}</th>
  <th class="sizeCol">${uiLabelMap.HeightLabel}</th>
  <th class="sizeCol">${uiLabelMap.WidthLabel}</th>
  <th class="actionColSmall"></th>
</tr>
<#if resultList?exists && resultList?has_content>
  <#assign rowClass = "1"/>
  <#list resultList as fileName>
    <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
      <#if fileListMap?exists && fileListMap?has_content>
        <#assign fileAttrMap = fileListMap.get("${StringUtil.wrapString(fileName)}")!""/>
      </#if>
      <td class="descCol firstCol" ><a href="<@ofbizUrl>mediaContentDetail?mediaName=${fileName?if_exists}&mediaType=${fileAttrMap.parentDirName!}</@ofbizUrl>">${fileName}</a></td>
      <td class="actionCol">
          <div class="infoIcon">
            <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.ContentPathHelperInfo} <@ofbizContentUrl>${fileAttrMap.imagePath}</@ofbizContentUrl>');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
          </div>
      </td>
      <td class="actionCol">
        <#if (fileAttrMap.height > 0 && fileAttrMap.width > 0)>
          <a href="javascript:void(0);" onMouseover="<#if fileAttrMap.imagePath?exists>showTooltipImage(event,'','${fileAttrMap.imagePath}?${nowTimestamp!}');<#else>showTooltip(event,'${uiLabelMap.ProductImagesTooltip}');</#if>" onMouseout="hideTooltip()"><span class="imageIcon"></span></a>
        </#if>
      </td>
      <#if fileAttrMap.parentDirName == 'images'>
        <#assign type="Image"/>
      <#elseif fileAttrMap.parentDirName == 'flash'>
        <#assign type="Flash"/>
      <#elseif fileAttrMap.parentDirName == 'document'>
        <#assign type="Document"/>
      <#else>
        <#assign type=fileAttrMap.parentDirName!/>
      </#if>
      <td class="typeCol">${type!}</td>
      <td class="sizeCol" >${fileAttrMap.fileSize?string}kb</td>
      <td class="sizeCol" >${fileAttrMap.height}px</td>
      <td class="sizeCol" >${fileAttrMap.width}px</td>
      <td class="actionCol">
        <#assign deleteMediaAssetConfirmText = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "DeleteMediaAssetConfirmListText", Static["org.ofbiz.base.util.UtilMisc"].toMap("fileName", "${fileName?html}"), locale)/>
        <a href="javascript:setConfirmDialogContent('${fileName?html}:${fileAttrMap.parentDirName!}','${deleteMediaAssetConfirmText}','deleteMediaContentFromList');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
      </td>      
    </tr>
    <#if rowClass == "2">
      <#assign rowClass = "1">
    <#else>
      <#assign rowClass = "2">
    </#if>
  </#list>
</#if>