<#if helperText?exists && helperText?has_content>
 <div class="helperText">
     <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${helperIconText!helperText!}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
 </div>
</#if>
