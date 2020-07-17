	<thead>
        <tr class="heading">
            <th class="idCol firstCol">${uiLabelMap.functionLabel}</th>
            <th class="nameCol">${uiLabelMap.DescriptionLabel}</th>
         </tr>
     </thead>
            
<#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1"/>
    <#list resultList as solrConfigTool>
        <#assign hasNext = solrConfigTool_has_next>
        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
            <td class="nameCol <#if !solrConfigTool_has_next?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>${solrConfigTool.toolDetail!""}</@ofbizUrl>">${solrConfigTool.toolType!""}</a></td>
            <td class="descCol <#if !solrConfigTool_has_next?if_exists>lastRow</#if>">${solrConfigTool.toolDesc!""}</td>
        </tr>
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
        </form>
    </#list>
</#if>