<table class="osafe">
  <thead>
    <tr class="heading">
      <th class="idCol firstCol">${uiLabelMap.functionLabel}</th>
      <th class="nameCol">${uiLabelMap.DescriptionLabel}</th>
    </tr>
  </thead>
 
  <#if adminToolsList?exists && adminToolsList?has_content>
    <#assign rowClass = "1"/>
    <#list adminToolsList as adminTool>
      <#assign hasNext = adminTool_has_next>
        <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
          <td class="nameCol <#if !hasNext?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>${detailAction!}?detailScreen=${adminTool.toolDetail!""}</@ofbizUrl>">${adminTool.toolType!""}</a></td>
          <td class="descCol <#if !hasNext?if_exists>lastRow</#if>">${adminTool.toolDesc!""}</td>
        </tr>
        <#if rowClass == "2">
          <#assign rowClass = "1">
        <#else>
          <#assign rowClass = "2">
        </#if>
    </#list>
  </#if>
</table>







