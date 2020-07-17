<div class="linkButton">
	<#if metaAction?exists && metaAction?has_content>
      <a href="javascript:submitDetailForm(document.${detailFormName!""}, 'MT');" onMouseover="showTooltip(event,'${uiLabelMap.HtmlMetatagTooltip}');" onMouseout="hideTooltip()"><span class="metatagIcon"></span></a>
    </#if>
    <#if previewAction?exists && previewAction?has_content>
      <a href="javascript:submitDetailForm(document.${detailFormName!""}, 'PC');" onMouseover="showTooltip(event,'${uiLabelMap.PreviewContentTooltip}');" onMouseout="hideTooltip()"><span class="previewIcon"></span></a>
    </#if>
</div>