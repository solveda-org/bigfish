<!-- start listBox -->
<thead>
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.ParameterNameLabel}</th>
    <th class="statusCol">${uiLabelMap.CategoryLabel}</th>
    <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
    <th class="valueCol lastCol">${uiLabelMap.CurrentValueLabel}</th>
  </tr>
  </thead>
  <#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1"/>
    <#list resultList as productStoreParams>
      <#assign hasNext = productStoreParams_has_next>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="idCol <#if !productStoreParams_has_next?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>manageParameterDetail?parmKey=${productStoreParams.parmKey?if_exists}</@ofbizUrl>">${productStoreParams.parmKey?if_exists}</a></td>
        <td class="statusCol <#if !productStoreParams_has_next?if_exists>lastRow</#if>">${productStoreParams.parmCategory?if_exists}</td>
        <td class="descCol <#if !productStoreParams_has_next?if_exists>lastRow</#if>">${productStoreParams.description?if_exists}</td>
        <td class="valueCol lastCol">${productStoreParams.parmValue?if_exists}</td>
      </tr>
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
    </#list>
  </#if>
<!-- end listBox -->