<#if warningText?exists && warningText?has_content>
 <div class="warningText">
  <a href="javascript:void(0);" onMouseover="showTooltip(event,'${warningText!}');" onMouseout="hideTooltip()"><span class="warningIcon"></span></a>
 </div>
</#if>
