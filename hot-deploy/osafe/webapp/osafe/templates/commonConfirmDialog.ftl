<div class="displayBox confirmDialog">
     <h3>${commonConfirmDialogTitle!""}</h3>
     <div class="confirmTxt"><p>${commonConfirmDialogText!""}</p></div>
     <div class="container button">
       <#if commonConfirmDialogNoBtn?exists>
         <input type="button" class="standardBtn negative" name="noBtn" value='${commonConfirmDialogNoBtn!""}'  onClick="javascript:confirmDialogResult('N','${dialogPurpose}');"/>
       </#if>
       <#if commonConfirmDialogYesBtn?exists>
         <input type="button" class="standardBtn positive" name="yesBtn" value='${commonConfirmDialogYesBtn!""}' onClick="javascript:confirmDialogResult('Y','${dialogPurpose}');"/>
       </#if>
     </div>
</div>
