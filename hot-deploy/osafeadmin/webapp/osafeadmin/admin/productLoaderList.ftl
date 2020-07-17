<#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1"/>
    <#list resultList as productLoader>
        <#assign hasNext = productLoader_has_next>
        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
            <td class="nameCol <#if !productLoader_has_next?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>productLoaderDetail?detailScreen=${productLoader.toolDetail!""}</@ofbizUrl>">${productLoader.toolType!""}</a></td>
            <td class="descCol <#if !productLoader_has_next?if_exists>lastRow</#if>">${productLoader.toolDesc!""}</td>
        </tr>
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
    </#list>
</#if>