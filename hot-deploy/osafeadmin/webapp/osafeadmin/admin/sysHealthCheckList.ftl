<tr class="heading">
    <th class="nameCol">${uiLabelMap.functionLabel}</th>
    <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
</tr>
<#if systemHealthCheckList?exists && systemHealthCheckList?has_content>
    <#assign rowClass = "1"/>
    <#list systemHealthCheckList as sysHealthCheck>
        <#assign hasNext = sysHealthCheck_has_next>
        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
            <td class="nameCol <#if !sysHealthCheck_has_next?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>sysHealthCheckDetail?detailScreen=${sysHealthCheck.sysHealthCheckDetail!""}</@ofbizUrl>">${sysHealthCheck.sysHealthCheckType!""}</a></td>
            <td class="descCol <#if !sysHealthCheck_has_next?if_exists>lastRow</#if> firstCol" >${sysHealthCheck.sysHealthCheckDesc!""}</td>
        </tr>
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
        </form>
    </#list>
</#if>