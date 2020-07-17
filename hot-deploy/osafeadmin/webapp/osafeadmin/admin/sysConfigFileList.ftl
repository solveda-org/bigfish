<#if configFileResultList?exists && configFileResultList?has_content>
  <#assign rowClass = "1"/>
    <#list configFileResultList as sysConfigFile>
        <#assign hasNext = sysConfigFile_has_next>
        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
            <td class="nameCol <#if !sysConfigFile_has_next?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>sysConfigFileDetail?fileName=${sysConfigFile.fileName!""}</@ofbizUrl>">${sysConfigFile.fileName!""}</a></td>
        </tr>
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
        </form>
    </#list>
</#if>