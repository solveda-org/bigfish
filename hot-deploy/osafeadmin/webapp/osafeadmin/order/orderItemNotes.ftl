<!-- start orderItemNotes.ftl -->

<table class="osafe">
  <#if orderNotes?exists && orderNotes?has_content>
    <#assign rowClass = "1"/>
    <#list orderNotes as note>
      <#assign hasNext = note_has_next>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
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

<!-- end orderItemNotes.ftl -->


