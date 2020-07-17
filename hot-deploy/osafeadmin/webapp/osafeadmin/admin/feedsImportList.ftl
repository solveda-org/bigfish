<#if feedImportList?exists && feedImportList?has_content>
    <#assign rowClass = "1"/>
    <#list feedImportList as feedsImport>
        <#assign hasNext = feedsImport_has_next>
        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
            <td class="nameCol <#if !feedsImport_has_next?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>feedsImportDetail?detailScreen=${feedsImport.toolDetail!""}</@ofbizUrl>">${feedsImport.toolType!""}</a></td>
            <td class="descCol <#if !feedsImport_has_next?if_exists>lastRow</#if>">${feedsImport.toolDesc!""}</td>
        </tr>
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
    </#list>
</#if>