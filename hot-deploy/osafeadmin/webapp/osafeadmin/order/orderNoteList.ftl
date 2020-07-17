<table class="osafe">
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.NoteNoLabel}</th>
    <th class="nameCol">${uiLabelMap.ByLabel}</th>
    <th class="dateCol">${uiLabelMap.DateLabel}</th>
    <th class="dateCol">${uiLabelMap.TimeLabel}</th>
    <th class="noteCol">${uiLabelMap.NoteLabel}</th>
  </tr>
  <#assign noteList = delegator.findByAnd("OrderHeaderNoteView", {"orderId" : orderHeader.orderId!})/>
  <#if noteList?exists && noteList?has_content>
    <#assign rowClass = "1"/>
    <#list noteList as note>
      <#assign hasNext = note_has_next>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="idCol <#if !note_has_next?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>orderNoteDetail?noteId=${note.noteId?if_exists}&orderId=${orderHeader.orderId!}</@ofbizUrl>">${note.noteId?if_exists}</a></td>
        <td class="nameCol <#if !note_has_next?if_exists>lastRow</#if>">${note.noteParty?if_exists}</td>
        <#assign noteDateTime = (Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(note.noteDateTime!, preferredDateTimeFormat).toLowerCase())!"N/A"/>
        <#assign noteDateTime = noteDateTime?split(" ")/>
        <#assign noteDate=noteDateTime[0] />
        <#assign noteTime=noteDateTime[1] />
        <td class="dateCol <#if !note_has_next?if_exists>lastRow</#if>">${noteDate!}</td>
        <td class="dateCol <#if !note_has_next?if_exists>lastRow</#if>">${noteTime!}</td>
        <td class="noteCol <#if !note_has_next?if_exists>lastRow</#if>">${Static["com.osafe.util.Util"].getFormattedText(note.noteInfo!"")}</td>
      </tr>
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
    </#list>
  <#else>
    <tr><td class="boxNumber" colspan="5">${uiLabelMap.NoDataAvailableInfo}</td></tr>
  </#if>
</table>