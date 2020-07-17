<#if helperInfoText?exists && helperInfoText?has_content>
 <div class="helperText">
     <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${helperInfoText!}');" onMouseout="hideTooltip()"><span class="helperInfoIcon"></span></a>
 </div>
</#if>
