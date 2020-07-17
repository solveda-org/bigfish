<!-- start customerDetailAttributeInfo.ftl -->
<table class="osafe">
  <tr class="heading">
    <th class="nameCol">${uiLabelMap.AttributeNameLabel}</th>
    <th class="descCol">${uiLabelMap.ValueLabel}</th>
  </tr>
  <#if customerAttributes?exists && customerAttributes?has_content>
    <#assign rowClass = "1"/>
    <#list customerAttributes as customerAttribute>
      <#assign hasNext = customerAttribute_has_next>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="nameCol <#if !hasNext?if_exists>lastRow</#if>">
          ${customerAttribute.attrName!}
        </td>
        <td class="descCol <#if !hasNext?if_exists>lastRow</#if>">
          ${customerAttribute.attrValue!}
        </td>
      </tr>
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
    </#list>
  <#else>
        ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
  </#if>
</table>
<!-- end customerDetailAttributeInfo.ftl -->