<div class="linkButton">
    <#if showOrderDetailLink?has_content && showOrderDetailLink == 'true'>
        <a href="<@ofbizUrl>orderDetail?orderId=${detailId!}</@ofbizUrl>" class="buttontext action" onMouseover="showTooltip(event,'${uiLabelMap.OrderDetailsLabel}');" onMouseout="hideTooltip()"><span class="detailIcon"></span></a>
    </#if>
    <#if showOrderNotesLink?has_content && showOrderNotesLink == 'true'>
        <a href="<@ofbizUrl>orderNotesList?orderId=${detailId!}</@ofbizUrl>" class="buttontext action" onMouseover="showTooltip(event,'${orderNotesTooltipText}');" onMouseout="hideTooltip()"><span class="addNoteIcon"></span></a>
    </#if>
    <#if showStatusUpdateLink?has_content && showStatusUpdateLink == 'true'>
        <a href="<@ofbizUrl>statusUpdateList?orderId=${detailId!}</@ofbizUrl>" class="buttontext action" onMouseover="showTooltip(event,'${uiLabelMap.OrderStatusHistoryLabel}');" onMouseout="hideTooltip()"><span class="statusUpdateIcon"></span></a>
    </#if>
    <#if showPDFLink?has_content && showPDFLink == 'true'>
        <a href="<@ofbizUrl>AdminOrder.pdf?orderId=${detailId!}</@ofbizUrl>" target="Download PDF" class="buttontext action" onMouseover="showTooltip(event,'${uiLabelMap.ExportOrderToPDFLabel}');" onMouseout="hideTooltip()"><span class="exportToPdfIcon"></span></a>
    </#if>
    <#if showXMLLink?has_content && showXMLLink == 'true'>
        <a href="<@ofbizUrl>exportOrderXML?orderId=${detailId!}</@ofbizUrl>" class="buttontext action" onMouseover="showTooltip(event,'${uiLabelMap.ExportOrderToXMLLabel}');" onMouseout="hideTooltip()"><span class="exportToXmlIcon"></span></a>
    </#if>
</div>