<div class="displayBox confirmDialog">
    <div class="displayBoxHeader">
       <span class="displayBoxHeaderCaption">${searchDialogTitle!""}</span>
     </div>
     <div class="confirmTxt">${searchDialogText!""}</div>
     <div class="confirmBtn">
       <#if searchDialogOkBtn?exists>
         <input type="button" class="standardBtn action" name="noBtn" value='${searchDialogOkBtn!""}'  onClick="javascript:confirmDialogResult('N','${dialogPurpose}');"/>
       </#if>
     </div>
</div>
