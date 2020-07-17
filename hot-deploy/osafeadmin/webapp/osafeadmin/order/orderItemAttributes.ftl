<table class="osafe">
  <tr class="heading">
    <th class="nameCol">${uiLabelMap.AttributeNameLabel}</th>
    <th class="nameCol">${uiLabelMap.AttributeValueLabel}</th>
  </tr>
  <#assign resultList = orderItemAttributes?if_exists/>
  <#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1"/>
    <#list resultList as attribute>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="nameCol <#if !attribute_has_next?if_exists>lastRow</#if>">${attribute.attrName!}</td>
        <td class="nameCol <#if !attribute_has_next?if_exists>lastRow</#if>">${attribute.attrValue!}</td>
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