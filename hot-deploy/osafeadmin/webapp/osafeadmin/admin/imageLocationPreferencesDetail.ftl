<#if imageLocationPrefList?has_content>
  <table class="osafe" cellspacing="0">
    <thead>
      <tr class="heading">
        <th class="idCol firstCol">${uiLabelMap.ImageNameLabel}</th>
        <th class="seqCol">${uiLabelMap.DirectoryLocationLabel}</th>
      </tr>
    </thead>
    <tbody>
      <#assign rowClass = "1">
      <#assign rowNo = 1/>   
      <#list imageLocationPrefList  as imageLocationPref>
        <#assign hasNext = imageLocationPref_has_next>
        <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
          <td class="descCol <#if !hasNext>lastRow</#if>" >
            ${imageLocationPref.key!}
            <input type="hidden" name="key_${rowNo}" value="${imageLocationPref.key!}"></input>
          </td>
          <td class="seqCol <#if !hasNext>lastRow</#if>">
            <input type="text" name="imageLocation_${rowNo}" value="${imageLocationPref.value!""}" class="large"></input>
          </td>
        </tr>
        <#assign rowNo = rowNo+1/>
        <#-- toggle the row color -->
        <#if rowClass == "2">
          <#assign rowClass = "1">
        <#else>
          <#assign rowClass = "2">
        </#if>
      </#list>
    </tbody>
  </table>
<#else>
  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
