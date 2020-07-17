<#if warningText?exists && warningText?has_content>
 <div class="warningText">
  <a href="<#if warningUrl?exists && warningUrl?has_content>${warningUrl!}<#else>javascript:void(0);</#if>" onMouseover="showTooltip(event,'${warningText!}');" onMouseout="hideTooltip()"><span class="warningIcon"></span></a>
 </div>
</#if>
