<div class="displayBox confirmDialog">
     <h3>${searchDialogTitle!""}</h3>
     <div class="confirmTxt">${searchDialogText!""}</div>
     <div class="confirmBtn">
       <#if searchDialogOkBtn?exists>
         <input type="button" class="standardBtn action" name="noBtn" value='${searchDialogOkBtn!""}'  onClick="javascript:confirmDialogResult('N','${dialogPurpose}');"/>
       </#if>
     </div>
</div>
