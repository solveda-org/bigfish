	<thead>
        <tr class="heading">
            <th class="idCol firstCol">${uiLabelMap.functionLabel}</th>
            <th class="nameCol">${uiLabelMap.DescriptionLabel}</th>
         </tr>
     </thead>
            
<#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1"/>
    <#list resultList as paymentSettingsTool>
        <#assign hasNext = paymentSettingsTool_has_next>
        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
            <td class="nameCol <#if !paymentSettingsTool_has_next?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>${paymentSettingsTool.toolDetail!""}</@ofbizUrl>">${paymentSettingsTool.toolType!""}</a></td>
            <td class="descCol <#if !paymentSettingsTool_has_next?if_exists>lastRow</#if>">${paymentSettingsTool.toolDesc!""}</td>
        </tr>
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
        </form>
    </#list>
</#if>