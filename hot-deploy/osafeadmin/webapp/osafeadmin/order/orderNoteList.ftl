<table class="osafe">
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.NoteNoLabel}</th>
    <th class="nameCol">${uiLabelMap.ByLabel}</th>
    <th class="dateCol">${uiLabelMap.DateLabel}</th>
    <th class="dateCol">${uiLabelMap.TimeLabel}</th>
    <th class="noteCol">${uiLabelMap.NoteLabel}</th>
  </tr>
  
  <#if resultList?exists && resultList?has_content>
  <#assign orderNotes = resultList/>
    <#assign rowClass = "1"/>
    <#list orderNotes as note>
      <#assign hasNext = note_has_next>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="idCol <#if !hasNext?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>orderNoteDetail?noteId=${note.noteId?if_exists}&orderId=${orderHeader.orderId!}</@ofbizUrl>">${note.noteId?if_exists}</a></td>
        <td class="nameCol <#if !hasNext?if_exists>lastRow</#if>">${note.noteParty?if_exists}</td>
        <#assign noteDateTime = (Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(note.noteDateTime!, preferredDateTimeFormat).toLowerCase())!"N/A"/>
        <#assign noteDateTime = noteDateTime?split(" ")/>
        <#assign noteDate=noteDateTime[0] />
        <#assign noteTime=noteDateTime[1] />
        <td class="dateCol <#if !hasNext?if_exists>lastRow</#if>">${noteDate!}</td>
        <td class="dateCol <#if !hasNext?if_exists>lastRow</#if>">${noteTime!}</td>
        <td class="noteCol <#if !hasNext?if_exists>lastRow</#if>">${Static["com.osafe.util.Util"].getFormattedText(note.noteInfo!"")}</td>
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