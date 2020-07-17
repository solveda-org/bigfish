<#if feedConverterList?exists && feedConverterList?has_content>
    <#assign rowClass = "1"/>
    <#list feedConverterList as feedConverter>
        <#assign hasNext = feedConverter_has_next>
        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
            <td class="nameCol <#if !feedConverter_has_next?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>feedConverterDetail?detailScreen=${feedConverter.toolDetail!""}</@ofbizUrl>">${feedConverter.toolType!""}</a></td>
            <td class="descCol <#if !feedConverter_has_next?if_exists>lastRow</#if>">${feedConverter.toolDesc!""}</td>
        </tr>
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
    </#list>
</#if>