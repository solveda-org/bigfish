<div class="displayBox confirmDialog">
    <div class="displayBoxHeader">
       <span class="displayBoxHeaderCaption">${commonConfirmDialogTitle!""}</span>
     </div>
     <div class="confirmTxt">${commonConfirmDialogText!""}</div>
     <div class="confirmBtn">
       <#if commonConfirmDialogYesBtn?exists>
         <input type="button" class="standardBtn action" name="yesBtn" value='${commonConfirmDialogYesBtn!""}' onClick="javascript:confirmDialogResult('Y','${dialogPurpose}');"/>
       </#if>
       <#if commonConfirmDialogNoBtn?exists>
         <input type="button" class="standardBtn action" name="noBtn" value='${commonConfirmDialogNoBtn!""}'  onClick="javascript:confirmDialogResult('N','${dialogPurpose}');"/>
       </#if>
     </div>
</div>
