<table class="osafe">
<tr class="heading">
    <th class="nameCol">${uiLabelMap.cacheTypeLabel}</th>
    <th class="descCol">${uiLabelMap.CacheStoreLabel}</th>
    <th class="actionColSmall"></th>
</tr>
<input type="hidden" name="detailScreen" id="detailScreen" value="${parameters.detailScreen?default(detailScreenName!"")}" />
<input type="hidden" name="cacheType" id="cacheType" value="" />
<input type="hidden" name="cacheName" id="cacheName" value="" />
<#if cacheList?exists && cacheList?has_content>
  <#assign rowClass = "1"/>
    <#list cacheList as clearCache>
        <#assign hasNext = clearCache_has_next>
        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
            <td class="nameCol <#if !hasNext?if_exists>lastRow</#if>">${clearCache.cacheType!""}</td>
            <td class="descCol <#if !hasNext?if_exists>lastRow</#if>">${Static["com.osafe.util.Util"].getFormattedText(clearCache.cacheStore!"")}</td>
            <td class="actionColSmall">
                <a href="javascript:clearCache('${clearCache.cacheToClear!}', '${clearCache.cacheType!}');">
                    <span class="clearCacheIcon"></span>
                </a>
            </td>
        </tr>
        
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
    </#list>
</#if>
</table>