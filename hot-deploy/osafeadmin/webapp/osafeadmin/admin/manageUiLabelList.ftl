<!-- start listBox -->
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.LabelLabel}</th>
    <#if !(showLabelCategory?has_content) || showLabelCategory == "Y">
    	<th class="statusCol">${uiLabelMap.CategoryLabel}</th>
    </#if>
    <#if !(showLabelDescription?has_content) || showLabelDescription == "Y">
    	<th class="descCol">${uiLabelMap.DescriptionLabel}</th>
    </#if>
    <th class="valueCol lastCol">${uiLabelMap.CaptionLabel}</th>
  </tr>
  <#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1"/>
    <#list resultList as manageUiLabels>
      <#assign hasNext = manageUiLabels_has_next>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="idCol <#if !productStoreParams_has_next?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>${detailAction!}?key=${manageUiLabels.key?if_exists}</@ofbizUrl>">${manageUiLabels.key?if_exists}</a></td>
        <td class="statusCol <#if !productStoreParams_has_next?if_exists>lastRow</#if>">${manageUiLabels.category?if_exists}</td>
        <td class="descCol <#if !productStoreParams_has_next?if_exists>lastRow</#if>">${manageUiLabels.description?if_exists}</td>
        <td class="valueCol lastCol">${manageUiLabels.value?if_exists}</td>
      </tr>
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
    </#list>
  </#if>
<!-- end listBox -->