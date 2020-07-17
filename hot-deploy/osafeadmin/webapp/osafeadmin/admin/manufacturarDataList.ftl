<div id ="manufacturerData" class="commonDivHide" style="display:none">
<table class="osafe">
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.RowNoLabel}</th>
    <th class="idCol firstCol">${uiLabelMap.ManuIdLabel}</th>
    <th class="nameCol">${uiLabelMap.ManuNameLabel}</th>
    <th class="nameCol">${uiLabelMap.AddressLabel}</th>
    <th class="nameCol">${uiLabelMap.CityLabel}</th>
    <th class="nameCol">${uiLabelMap.StateLabel}</th>
    <th class="nameCol">${uiLabelMap.ZipLabel}</th>
    <th class="nameCol">${uiLabelMap.CountryLabel}</th>
    <th class="decsCol">${uiLabelMap.ShortDescLabel}</th>
    <th class="decsCol">${uiLabelMap.LongDescLabel}</th>
    <th class="nameCol">${uiLabelMap.ManuImageLabel}</th>
  </tr>
  <#if manufacturerDataList?exists && manufacturerDataList?has_content>
    <#assign rowClass = "1">
    <#assign rowNo = 1>
    <#list manufacturerDataList as manufacturer>
      <tr class="<#if rowClass == "2">even</#if>">
        <td class="idCol firstCol" >${rowNo!""}</td>
        <td class="idCol firstCol" >${manufacturer.partyId!""}</td>
        <td class="nameCol">${manufacturer.manufacturerName!""}</td>
        <td class="nameCol">${manufacturer.address1!""}</td>
        <td class="nameCol">${manufacturer.city!""}</td>
        <td class="nameCol">${manufacturer.state!""}</td>
        <td class="nameCol">${manufacturer.zip!""}</td>
        <td class="nameCol">${manufacturer.country!""}</td>
        <td class="decsCol">
          <#assign shortDescription = manufacturer.shortDescription!""/>
          <#if shortDescription?has_content && shortDescription != "">
            <#assign shortDescription = shortDescription?replace("\'", "\\'") />
            <#assign shortDescription = shortDescription?replace("\r\n", "\\n") />
            <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${shortDescription?html}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
          </#if>
        </td>
        <td class="decsCol">
          <#assign longDescription = manufacturer.longDescription!""/>
          <#if longDescription?has_content && longDescription != "">
            <#assign longDescription = longDescription?replace("\'", "\\'") />
            <#assign longDescription = longDescription?replace("\r\n", "\\n") />
            <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${longDescription?html}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
          </#if>
        </td>
        <td class="nameCol">${manufacturer.manufacturerImage!""}</td>
      </tr>
      <#-- toggle the row color -->
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
      <#assign rowNo = rowNo+1/>
    </#list>
  <#else>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
  </#if>
</table>
</div>