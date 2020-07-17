<#if helperInfoText?exists && helperInfoText?has_content>
     <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${helperInfoText!}');" onMouseout="hideTooltip()"><span class="helperInfoIcon"></span></a>
</#if>
