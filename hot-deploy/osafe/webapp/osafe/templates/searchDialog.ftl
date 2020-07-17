<div class="displayBox confirmDialog">
     <h3>${searchDialogTitle!""}</h3>
     <#assign searchText = Static["com.osafe.util.Util"].getProductStoreParm(request,"SEARCH_NO_ENTRY_ERROR")!""/>
     <div class="confirmTxt"><#if searchText?has_content>${searchText}<#else>${searchDialogText!""}</#if></div>
     <div class="container button">
       <#if searchDialogOkBtn?exists>
         <input type="button" class="standardBtn action" name="noBtn" value='${searchDialogOkBtn!""}'  onClick="javascript:confirmDialogResult('N','${dialogPurpose}');"/>
       </#if>
     </div>
</div>
