<tr class="heading">
    <th class="descCol firstCol">${uiLabelMap.TypeLabel!""}</th>
    <th class="idCol">${uiLabelMap.IdLabel}</th>
    <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
    <th class="descCol">${uiLabelMap.AssetTypeLabel}</th>
    <th class="descCol">${uiLabelMap.AssetURLLabel}</th>
</tr>
<#assign resultList = request.getAttribute("resultList")!/>
<#if resultList?has_content>
    <#assign rowClass = "1">
    <#assign alreadyShownList = Static["javolution.util.FastList"].newInstance()/>
    <#list resultList as assetInfo>
        <#assign hasNext = assetInfo_has_next>
        <#if !alreadyShownList.contains(assetInfo.assetID!"")>
        <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
            <td class="descCol <#if !hasNext>lastRow</#if>">${assetInfo.type?if_exists}</td>
            <td class="idCol <#if !hasNext>lastRow</#if>">${assetInfo.ID?if_exists}</td>
            <td class="descCol <#if !hasNext>lastRow</#if>">${assetInfo.description?if_exists}</td>
            <td class="descCol <#if !hasNext>lastRow</#if>">${assetInfo.assetType?if_exists}</td>
            <td class="descCol <#if !hasNext>lastRow</#if> lastCol">${assetInfo.imageURL?if_exists}</td>
        </tr>
        </#if>
        <#assign changed = alreadyShownList.add(assetInfo.assetID!"")/>
        <#-- toggle the row color -->
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
    </#list>
<#else>
   <tr>
       <td colspan="4" class="boxNumber">${uiLabelMap.AssetCheckSuccessInfo}</td>
    </tr>
</#if>
