<#if feedExportList?exists && feedExportList?has_content>
    <#assign rowClass = "1"/>
    <#list feedExportList as feedsExport>
        <#assign hasNext = feedsExport_has_next>
        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
            <td class="nameCol <#if !feedsExport_has_next?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>feedsExportDetail?detailScreen=${feedsExport.toolDetail!""}</@ofbizUrl>">${feedsExport.toolType!""}</a></td>
            <td class="descCol <#if !feedsExport_has_next?if_exists>lastRow</#if>">${feedsExport.toolDesc!""}</td>
        </tr>
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
    </#list>
</#if>