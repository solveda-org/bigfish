<div class="linkButton">
    <#if ExportToFileAction?exists && ExportToFileAction?has_content>
      <a href="<@ofbizUrl>${ExportToFileAction}?${detailParamKey!}=${detailId!}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ExportToCSVTooltipText}');" onMouseout="hideTooltip()"><span class="exportToCsvIcon"></span></a>
    </#if>
    <#if ExportToXMLAction?exists && ExportToXMLAction?has_content>
       <a href="<@ofbizUrl>${ExportToXMLAction}?${detailParamKey}=${detailId}</@ofbizUrl>" target="Download XML" onMouseover="showTooltip(event,'${uiLabelMap.ExportToXMLTooltipText}');" onMouseout="hideTooltip()"><span class="exportToXmlIcon"></span></a>
    </#if>
</div>