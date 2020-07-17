<#if bluedartFeedsImportList?exists && bluedartFeedsImportList?has_content>
    <#assign rowClass = "1"/>
    <#list bluedartFeedsImportList as bluedartFeedsImport>
        <#assign hasNext = bluedartFeedsImport_has_next>
        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
            <td class="nameCol <#if !bluedartFeedsImport_has_next?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>bluedartFeedsImportDetail?detailScreen=${bluedartFeedsImport.toolDetail!""}</@ofbizUrl>">${bluedartFeedsImport.toolType!""}</a></td>
            <td class="descCol <#if !bluedartFeedsImport_has_next?if_exists>lastRow</#if>">${bluedartFeedsImport.toolDesc!""}</td>
        </tr>
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
    </#list>
</#if>