<#if parameters.noteId?exists && parameters.noteId?has_content>
  <#assign note = delegator.findByPrimaryKey("OrderHeaderNoteView", {"orderId" : orderHeader.orderId!, "noteId", parameters.noteId!})/>
  <#assign noteDateTime = (Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(note.noteDateTime!, preferredDateTimeFormat).toLowerCase())!"N/A"/>
  <#assign noteDateTime = noteDateTime?split(" ")/>
  <#assign noteDate=noteDateTime[0] />
  <#assign noteTime=noteDateTime[1] />
  <#assign noteInfo = note.noteInfo!/>
  <input type="hidden" name="noteId" value="${parameters.noteId!}"/>
</#if>
<input type="hidden" name="orderId" value="${orderHeader.orderId!}"/>
<input type="hidden" name="internalNote" value="Y"/>
<#if mode == 'edit'>
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.NoteNoCaption}</label>
      </div>
      <div class="infoValue">
        ${note.noteId!""}
      </div>
    </div>
  </div>
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.AddedByCaption}</label>
      </div>
      <div class="infoValue">
        ${note.noteParty!""}
      </div>
    </div>
  </div>
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.DateCaption}</label>
      </div>
      <div class="infoValue">
        ${noteDate!""}
      </div>
    </div>
  </div>
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.TimeCaption}</label>
      </div>
      <div class="infoValue">
        ${noteTime!""}
      </div>
    </div>
  </div>
</#if>
<div class="infoRow row">
  <div class="infoEntry long">
    <div class="infoCaption">
      <label>${uiLabelMap.NoteCaption}</label>
    </div>
    <div class="infoValue">
      <textarea name="noteInfo" cols="50" rows="5">${parameters.noteInfo!noteInfo!""}</textarea>
    </div>
  </div>
</div>

