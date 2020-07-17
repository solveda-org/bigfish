<div class="displayBox confirmDialog">
     <#if commonConfirmDialogTitle?exists>
         <h3>${commonConfirmDialogTitle!""}</h3>
     </#if>
     <#if commonConfirmDialogText?exists>
         <div class="confirmTxt"><p>${commonConfirmDialogText!""}</p></div>
     </#if>
     <div class="action previousButton">
       <#if commonConfirmDialogNoBtn?exists>
         <input type="button" class="standardBtn negative" name="noBtn" value='${commonConfirmDialogNoBtn!""}'  onClick="javascript:confirmDialogResult('N','${dialogPurpose}');"/>
       </#if>
     </div>
     <div class="action continueButton">
       <#if commonConfirmDialogYesBtn?exists>
         <input type="button" class="standardBtn positive" name="yesBtn" value='${commonConfirmDialogYesBtn!""}' onClick="javascript:confirmDialogResult('Y','${dialogPurpose}');"/>
       </#if>
     </div>
</div>
