<div class="linkButton">
    <#if previewAction?exists && previewAction?has_content>
      <a href="javascript:submitDetailForm(document.${detailFormName!""}, 'PC');" onMouseover="showTooltip(event,'${uiLabelMap.PreviewContentTooltip}');" onMouseout="hideTooltip()" class="previewIcon"></a>
    </#if>
</div>